package org.apache.catalina.coyote;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.endpoint.AbstractEndpoint;
import org.apache.catalina.connector.endpoint.nio.NioChannel;
import org.apache.catalina.connector.endpoint.nio.SocketState;
import org.apache.catalina.util.ErrorHelper;

import javax.servlet.http.Cookie;
import java.io.*;
import java.util.Map;

/**
 * @author: Mr.Yu
 * @create: 2022-04-01 21:01
 **/
@Slf4j
public class HttpNioProcessor extends AbstractProcessor {

    private boolean keepAlive;

    public HttpNioProcessor(AbstractEndpoint endpoint) {

        this.endpoint = endpoint;
        request = new Request();
        inputBuffer = new InternalInputNioBuffer(request);
        request.setInputBuffer(inputBuffer);
        response = new Response();
        outputBuffer = new InternalOutputNioBuffer(response);
        response.setOutputBuffer(outputBuffer);
        response.setRequest(request);
        request.setResponse(response);
    }


    /**
     * 解析请求行，请求体，将请求传递给容器
     * @param channel
     * @return boolean
     */
    @Override
    public SocketState process(NioChannel channel) {

        inputBuffer.init(channel);
        outputBuffer.init(channel);
        keepAlive = true;
        SocketState state = null;
        boolean ok = true;

        while (keepAlive) {

            try {
                // 解析请求行，返回false，证明当前无数据可读，退出循环，重新注册到poller中，等待下一次可读事件
                if (!inputBuffer.parseRequestLine()) {
                    state = SocketState.LONG;
                    break;
                }
                // 出现异常代表socket无数据可读，关闭socket
            } catch (IOException e) {
                e.printStackTrace();
                ok = false;
                response.setError(true);
            } catch (Exception e) {
                ErrorHelper.logErrorToSocket(e,response);
                // 未知异常，关闭连接
                state = SocketState.CLOSE;
                break;
            }


            try {
                if (ok && !parseRequestHeaders()) {
                    state = SocketState.LONG;
                    break;
                }
            } catch (IOException e) {
                // 无数据可读，关闭连接
                e.printStackTrace();
                ok = false;
                response.setError(true);
            } catch (Exception e) {
                ErrorHelper.logErrorToSocket(e,response);
                // 未知异常，关闭连接
                state = SocketState.CLOSE;
                break;
            }

            try {
                if (ok) {
                    // 准备请求行
                    prepareRequestLine();
                    // 简陋处理请求头
                    state = checkHeaders();
                }
            } catch (Exception e) {
                ErrorHelper.logErrorToSocket(e,response);
                // 未知异常，关闭连接
                state = SocketState.CLOSE;
                break;
            }

            // 将请求交给容器处理
            adapter.service(request, response);

            // 在整个请求中如果出现异常，关闭连接
            if (response.getError()) {
                state = SocketState.CLOSE;
                break;
            }
            // 接收下一次请求
            inputBuffer.recycle();
            outputBuffer.recycle();
        }
        return state;
    }


    public boolean parseRequestHeaders() throws IOException {
        if (!inputBuffer.getParseRequestHeader()) {
            return false;
        }
        while (true) {
            HttpHeader header = new HttpHeader();
            if (!inputBuffer.parseHeader(header)) {
                return false;
            }
            if (header.valueEnd == 0 && header.nameEnd == 0) {
                return true;
            }
            addHeader(header);
        }
    }

    public void addHeader(HttpHeader header) {
        if (header == null) {
            return;
        }
        byte[] name = header.name;
        byte[] value = header.value;

//        log.info("header: {}: {}",name,value);

        String realName = new String(name, 0, header.nameEnd);
        String realValue = new String(value, 0, header.valueEnd);
//        log.info("header: {name: " + realName + ", value: " + realValue + "}");
        request.addHeaders(realName, realValue);
        if (realName.equalsIgnoreCase("cookie")) {
            Cookie[] cookies = generateCookies(realValue);
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals("jsessionid")) {
                    request.setRequestedSessionId(cookie.getValue());
                    request.setRequestedSessionCookie(true);
                    request.setRequestedSessionURL(false);
                }
                request.addCookies(cookies[i]);
            }
        } else if (realName.equals("content-length")) {
            int n = -1;
            try {
                n = Integer.parseInt(realValue);
            } catch (Exception e) {
                log.error("content-length is not valid");
            }
            request.setContentLength(n);
        } else if (realName.equals("content-type")) {
            request.setContentType(realValue);
        }
    }

    private Cookie[] generateCookies(String str) {
        String[] value = str.split(";");
        int len = value.length;
        Cookie[] cookies = new Cookie[len];
        for (int i = 0; i < len; i++) {
            String s = value[i];
            String[] split = s.split("=");
            Cookie cookie = new Cookie(split[0], split.length > 1 ? split[1] : null);
            cookies[i] = cookie;
        }
        return cookies;
    }

    private void prepareRequestLine() {
        HttpRequestLine requestLine = inputBuffer.getRequestLine();
        if (requestLine.methodEnd == 0) {
            throw new RuntimeException("miss method!");
        }
        request.setMethod(new String(requestLine.method, 0, requestLine.methodEnd));
        if (requestLine.protocolEnd == 0) {
            throw new RuntimeException("miss protocol!");
        }
        request.setScheme(new String(requestLine.protocol, 0, requestLine.protocolEnd));
        if (requestLine.uriEnd == 0) {
            throw new RuntimeException("miss uri!");
        }
        int question = requestLine.indexOf((byte) '?');
        String uri = null;
        if (question > -1) {
            request.setQueryString(new String(requestLine.uri, question + 1, requestLine.uriEnd-question-1));
            uri = new String(requestLine.uri, 0, question);
        } else {
            request.setQueryString(null);
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
        }
        if (!uri.startsWith("/")) {
            int pos = uri.indexOf("://");
            if (pos == -1) {
                pos = uri.indexOf('/', pos + 3);
                if (pos == -1) {
                    uri = "";
                } else {
                    uri = uri.substring(pos);
                }
            }
        }
        // 如果浏览器禁用cookie，会在uri上携带Jessionid
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                request.setRequestedSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
        } else {
            request.setRequestedSessionURL(false);
        }
        request.setUri(uri);
//        log.info(request.getMethod() + " " + request.getUri() + " " + request.getScheme());
    }

    private SocketState checkHeaders() {
        Map<String, String> headers = request.getHeaders();
        String connection = headers.get("Connection");
        SocketState state = SocketState.LONG;
        if ("close".equals(connection)) {
            keepAlive = false;
            log.info("headers connection is close, so keepalive is false");
            state = SocketState.CLOSE;
        }
        return state;
    }


}
