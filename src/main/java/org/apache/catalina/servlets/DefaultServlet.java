package org.apache.catalina.servlets;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Globals;
import org.apache.catalina.WelComeResource;
import org.apache.catalina.coyote.HttpStatus;
import org.apache.catalina.pageResovler.PageResolver;
import org.apache.catalina.util.MimeUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Mr.Yu
 * @create: 2022-04-17 13:15
 **/
@Slf4j
public class DefaultServlet extends HttpServlet implements WelComeResource {

    private Map<String, ByteBuffer> tempResource = new HashMap<>();

    private String prefix;

    private String basePath;

    private final List<String> welcomeFilenames = new ArrayList<>();

    private final Map<String, byte[]> allResources = new HashMap<>();
    // 回收
    private int doGarage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String home = System.getProperty(Globals.CATALINA_HOME);
        String contextPath = config.getServletContext().getContextPath();
        prefix = contextPath;
        String appBase = Globals.DEPLOY_BASE;
        basePath = home + appBase + contextPath;
        Map<String, Path> paths = initResource(basePath);
        getStreamFormPath(paths);
        doGarage = tempResource.size();
    }

    private void getStreamFormPath(Map<String, Path> paths) {
        for (Map.Entry<String, Path> entry : paths.entrySet()) {
            Path path = entry.getValue();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 2);
            FileChannel channel = null;
            try {
                channel = FileChannel.open(path, StandardOpenOption.READ);
                buffer.clear();
                while (channel.read(buffer) != -1) {
                    if (buffer.position() == buffer.capacity() ||
                            buffer.remaining() == 0) {
                        ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                        buffer.flip();
                        newBuffer.put(buffer);
                        buffer = newBuffer;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (channel != null)
                        channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tempResource.put(entry.getKey(), buffer);
        }
    }

    public Map<String, Path> initResource(String path) {
        Map<String, Path> paths = new HashMap<>();
        StringBuilder builder = new StringBuilder("/");
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    builder.append(dir.getFileName().toString());
                    builder.append("/");
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (builder.toString().startsWith(prefix + "/WEB-INF/classes/") ||
                            builder.toString().startsWith(prefix + "/META-INF/"))
                        return super.visitFile(file, attrs);
                    String filename = file.getFileName().toString();
                    builder.append(filename);
//                    log.info("文件名："+ builder.toString());
                    paths.put(builder.toString(), file);
                    builder.delete(builder.length() - filename.length(), builder.length());
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    int fileNameLength = dir.getFileName().toString().length();
                    int builderLength = builder.length();
                    int start = builderLength - fileNameLength;
                    builder.delete(start - 1, builderLength);
                    return super.postVisitDirectory(dir, exc);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendStaticResource(req, resp);
    }

    public void sendStaticResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String uri = req.getRequestURI();
        // 如果uri没有contextPath，加上它，这种情况的uri是重定向的uri
        if (!uri.startsWith(prefix)) {
            uri = prefix + uri;
        }
        boolean notFound = false;
        if (prefix.equals(uri) || (prefix+"/").equals(uri)) {
            // 欢迎页面
            byte[] welcomeResource = findWelcomeResource();
            if (welcomeResource == null) {
                notFound = true;
            } else {
                writeBytesToResponse(null, welcomeResource, req, resp, false);
            }
        }

        if (notFound || ( !allResources.containsKey(uri) &&
                (tempResource == null || !tempResource.containsKey(uri) ) )) {
            // 404
            try {
                int status = HttpStatus.NOT_FOUND.getCode();
                String error = HttpStatus.NOT_FOUND.toString() + " " + uri;
                resp.sendError(status, error);
            } catch (IOException e) {
                // not to do
            }
        } else {
            byte[] bytes;
            if (allResources.containsKey(uri)) {
                bytes = allResources.get(uri);
            } else {
                ByteBuffer byteBuffer = tempResource.get(uri);
                byteBuffer.flip();
                int size = byteBuffer.limit();
                bytes = new byte[size];
                byteBuffer.get(bytes, 0, size);
                allResources.put(uri, bytes);
                tempResource.remove(uri);
                if (tempResource.size() == 0) {
                    // 回收资源
                    tempResource = null;
                }
            }
            writeBytesToResponse(uri, bytes, req, resp, true);
        }

    }

    private void writeBytesToResponse(String uri,
                                      byte[] bytes,
                                      HttpServletRequest req,
                                      HttpServletResponse resp,
                                      boolean parse) throws IOException {
        ServletOutputStream outputStream = null;
        try {
            outputStream = resp.getOutputStream();
            String mimeType = MimeUtils.getMimeType(uri);
            System.out.println(mimeType);
            if (mimeType != null)
                resp.setContentType(mimeType);

            // 简单解析资源
            if (parse && "text/html".equalsIgnoreCase(resp.getContentType()))
                bytes = PageResolver.resolve(bytes, req);

            outputStream.write(bytes);
            outputStream.flush();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void addWelComeFile(String filename) {
        welcomeFilenames.add(filename);
    }

    @Override
    public byte[] findWelcomeResource() {
        for (String filename : welcomeFilenames) {
            if (allResources.containsKey(filename)) {
                return allResources.get(filename);
            }
            File file = new File(basePath, filename);
            if (file.exists()) {
                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                     ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    byte[] bytes = baos.toByteArray();
                    allResources.put(filename, bytes);
                    return bytes;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
