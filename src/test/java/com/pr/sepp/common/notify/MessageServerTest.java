package com.pr.sepp.common.notify;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pr.sepp.common.websocket.push.MessageServer;
import com.pr.sepp.notify.fetch.FetchClient;
import com.pr.sepp.notify.model.GlobalDataResp;
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
	private Map<String, Object> map;

	@Before
	public void setUp() throws Exception {
		map = Maps.newHashMap();
		map.put(MESSAGE_PAGE_NUM, 1);
		map.put(MESSAGE_PAGE_SIZE, 10);
		map.put(PRODUCT_ID, "9");
		when(fetchClient.notifyMessage(session, WARNING)).thenReturn(new PageInfo());
		when(fetchClient.notifyMessage(session, MESSAGE)).thenReturn(new PageInfo());
	}

	@Test
	public void responseBuilderWARNINGAndMESSAGETest() {
		map.put(MESSAGE_TYPE, Lists.newArrayList(WARNING, MESSAGE));
		when(session.getAttributes()).thenReturn(map);
		GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
		verify(session, times(2)).getAttributes();
		assertNotNull(globalDataResp);
	}

	@Test
	public void responseBuilderWARNING() {
		map.put(MESSAGE_TYPE, Lists.newArrayList(WARNING));

		when(session.getAttributes()).thenReturn(map);
		GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
		verify(session, times(2)).getAttributes();
		assertNotNull(globalDataResp);
	}

	@Test
	public void responseBuilderMESSAGE() {
		map.put(MESSAGE_TYPE, Lists.newArrayList(MESSAGE));

		when(session.getAttributes()).thenReturn(map);
		GlobalDataResp globalDataResp = messageServer.responseBuilder(session);
		verify(session, times(2)).getAttributes();
		assertNotNull(globalDataResp);
	}

}
