package com.pr.sepp.common.notify;

import com.pr.sepp.notify.fetch.FetchClient;
import com.pr.sepp.common.websocket.push.MessageServer;
import com.pr.sepp.notify.model.resp.GlobalDataResp;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.common.websocket.model.MessageType.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.Silent.class)
public class MessageServerTest {

    @InjectMocks
    private MessageServer messageServer;
    @Mock
    private FetchClient fetchClient;
    @Mock
    private WebSocketSession session;
    private Map<String,Object> map;

    @Before
    public void setUp() throws Exception {
        map = Maps.newHashMap();
        map.put(MESSAGE_PAGE_NUM, 1);
        map.put(MESSAGE_PAGE_SIZE, 10);
        map.put(PRODUCT_ID, "9");
        when(fetchClient.notifyMessage(session, ALARM)).thenReturn(new PageInfo());
        when(fetchClient.notifyMessage(session, NOTICE)).thenReturn(new PageInfo());
    }

    @Test
    public void responseBuilderAlarmAndNoticeTest() {
        map.put(MESSAGE_TYPE, Lists.newArrayList(ALARM, NOTICE));
        when(session.getAttributes()).thenReturn(map);
        GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
        verify(session, times(2)).getAttributes();
        assertNotNull(globalDataResp);
    }

    @Test
    public void responseBuilderAlarm() {
        map.put(MESSAGE_TYPE, Lists.newArrayList(ALARM));

        when(session.getAttributes()).thenReturn(map);
        GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
        verify(session, times(2)).getAttributes();
        assertNotNull(globalDataResp);
    }

    @Test
    public void responseBuilderNotice() {
        map.put(MESSAGE_TYPE, Lists.newArrayList(NOTICE));

        when(session.getAttributes()).thenReturn(map);
        GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
        verify(session, times(2)).getAttributes();
        assertNotNull(globalDataResp);
    }

}
