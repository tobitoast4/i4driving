package org.opentrafficsim.i4driving.sim0mq;

import org.djutils.logger.CategoryLogger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;


public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private WebSocketServerListener listener;

    public WebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public void setListener(WebSocketServerListener listener) {
        this.listener = listener;
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {  // New client connected
//        JSONObject msg = new JSONObject();
//        msg.put("type", "STATUS");
//        msg.put("data", "SILAB acknowledged connection");
//        conn.send(msg.toString());
        CategoryLogger.always().info("Client connected");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

    @Override
    public void onMessage(WebSocket conn, String message) {
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

    @Override
    public void onError(WebSocket conn, Exception ex) {
        CategoryLogger.always().error("Error: " + ex);
    }

    @Override
    public void onStart() {
        CategoryLogger.always().info("WebSocket server started on port: " + getPort());
    }

    public void broadcast(String message) {
        for (WebSocket conn : this.getConnections()) {
            conn.send(message);
        }
    }
}

interface WebSocketServerListener {
    void onEvent(JSONObject object);
}

