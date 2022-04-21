package org.apache.catalina.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.ProtocolHandler;
import org.apache.catalina.Service;
import org.apache.catalina.core.LifecycleBase;
import org.apache.catalina.coyote.Adapter;
import org.apache.catalina.coyote.CoyoteAdapter;
import org.apache.catalina.util.Mapper;

import java.io.IOException;

/**
 * 连接器，负责接收客户端的请求，解析http数据，调用adapter封装request，response，传输给容器处理
 * @author: Mr.Yu
 * @create: 2022-03-30 15:11
 **/
@Slf4j
public class Connector extends LifecycleBase {

    private final String info = "org.apache.catalina.connector.Connector/1.0";

    // 协议
    private String scheme = "http";

    private ProtocolHandler protocolHandler;

    private String protocolClassName;

    private Service service;

    private Adapter adapter;

    private Mapper mapper = new Mapper();

    private MapperListener mapperListener = new MapperListener(mapper, this);

    public Connector() {
        super();
        setProtocol();
        try {
            protocolHandler = (ProtocolHandler) Class.forName(protocolClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public MapperListener getMapperListener() {
        return mapperListener;
    }

    public void setProtocol() {
        if (scheme.equals("http")) {
            protocolClassName = "org.apache.catalina.coyote.HttpNioProtocol";
        }
    }

    public void initInternal() {

        adapter = new CoyoteAdapter(this);
        protocolHandler.setAdapter(adapter);
        try {
            protocolHandler.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapperListener.init();
    }

    public void startInternal() {
        try {
            protocolHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapperListener.start();
    }

    public void pause() {
        try {
            protocolHandler.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopInternal() {
        try {
            protocolHandler.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapperListener.stop();
    }

    @Override
    public void destroyInternal() {
        try {
            protocolHandler.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getService().removeConnector(this);

    }

    @Override
    public String getInfo() {
        return info;
    }

    public String getScheme() {
        return scheme;
    }
}
