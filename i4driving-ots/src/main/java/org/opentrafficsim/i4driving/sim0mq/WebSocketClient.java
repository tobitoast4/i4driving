package org.opentrafficsim.i4driving.sim0mq;

import org.djutils.logger.CategoryLogger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private Session userSession;
    private WebSocketClientListener listener;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            CategoryLogger.always().error("Could not connect to WebSocket server: " + endpointURI +
                    " as of " + e + "\nMake sure the server is running");
        }
    }

    public void setListener(WebSocketClientListener listener) {
        this.listener = listener;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.userSession = session;
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (listener != null) {
                listener.onEvent(jsonObject);
            }
        } catch (JSONException e) {
            // message is not a JSON
            CategoryLogger.always().error(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.userSession = null;
    }

    public void sendMessage(String message) {
        if (userSession != null && userSession.isOpen()) {
            userSession.getAsyncRemote().sendText(message);
        } else {
            CategoryLogger.always().error("Session is not open.");
        }
    }
}

interface WebSocketClientListener {
    void onEvent(JSONObject object);
}
