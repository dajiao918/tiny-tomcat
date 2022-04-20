import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Socket socket = new Socket("localhost",8080);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        StringBuilder builder = new StringBuilder();
        builder.append("GET /testTomcat/hello HTTP/1.1\r\n");
        builder.append("Host: localhost\r\n");
        builder.append("Connection: keep-alive\r\n");
        builder.append("Cache-Control: max-age=0\r\n");
        builder.append("sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"\r\n");
        builder.append("sec-ch-ua-mobile: ?0\r\n");
        builder.append("sec-ch-ua-platform: \"Windows\"\r\n");
        builder.append("Upgrade-Insecure-Requests: 1\r\n");
        builder.append("Sec-Fetch-Site: none\r\n");
        builder.append("Sec-Fetch-Mode: navigate\r\n");
        builder.append("Sec-Fetch-User: ?1\r\n");
        builder.append("Sec-Fetch-Dest: document\r\n");
        builder.append("Accept-Encoding: gzip, deflate, br\r\n");
        builder.append("Accept-Language: zh-CN,zh;q=0.9\r\n");
        builder.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n");
        builder.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36\r\n");
        builder.append("\r\n");
        builder.append("abc");
        writer.write(builder.toString());
        writer.flush();
        System.in.read();
    }

}
