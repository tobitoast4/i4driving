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
        String resourceDescriptor = handshake.getResourceDescriptor();
        CategoryLogger.always().info("Client connected: " + resourceDescriptor);

        if (resourceDescriptor.contains("?")) {
            String query = resourceDescriptor.substring(resourceDescriptor.indexOf('?') + 1);
            String[] params = query.split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                if (kv.length == 2 && kv[0].equals("avId")) {
                    String avId = kv[1];
                    conn.setAttachment("AV" + avId); // ID an Verbindung hängen
                }
            }
        }
//        JSONObject msg = new JSONObject();
//        msg.put("type", "STATUS");
//        msg.put("data", "SILAB acknowledged connection");
//        conn.send(msg.toString());
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
//            CategoryLogger.always().error(e);
            e.printStackTrace();
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

    public boolean sendToClient(String clientId, String message) {
        for (WebSocket conn : this.getConnections()) {
            Object attachment = conn.getAttachment();
            if (attachment != null && attachment.equals(clientId)) {
                conn.send(message);
                return true; // erfolgreich gesendet
            }
        }
        return false; // kein Client mit der ID gefunden
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

