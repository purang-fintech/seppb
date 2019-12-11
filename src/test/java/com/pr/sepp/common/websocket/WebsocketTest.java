package com.pr.sepp.common.websocket;

import com.pr.sepp.common.BaseIntegrationTest;
import com.pr.sepp.common.websocket.handler.MessageWebSocketHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebsocketTest extends BaseIntegrationTest {
    final CountDownLatch latch = new CountDownLatch(1);
    @LocalServerPort
    private int port;
    private SockJsClient sockJsClient;

    @Autowired
    private MessageWebSocketHandler messageWebSocketHandler;


    @Before
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        this.sockJsClient = new SockJsClient(transports);
    }


    @Test
    @Ignore
    public void getGreeting() throws Exception {
        this.sockJsClient.doHandshake(messageWebSocketHandler,
                "ws://localhost:" + port + "/sepp/myHandler");
        if (latch.await(60, TimeUnit.SECONDS)) {
        } else {
        }

    }

}