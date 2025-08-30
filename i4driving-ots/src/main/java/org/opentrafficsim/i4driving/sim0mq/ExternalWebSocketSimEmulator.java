package org.opentrafficsim.i4driving.sim0mq;

import org.djutils.logger.CategoryLogger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.pmw.tinylog.Level;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.Instant;

public class ExternalWebSocketSimEmulator {

    private ExternalWebSocketServer server;
    private String fileName = "101.log";

    private ExternalWebSocketSimEmulator() {
        server = new ExternalWebSocketServer(new InetSocketAddress(8099));
        server.start();

        CategoryLogger.setAllLogLevel(Level.DEBUG);
        CategoryLogger.setAllLogMessageFormat("[{date: YYYY-MM-dd HH:mm:ss.SSS}] {level}: {message}");
        CategoryLogger.always().debug("ExternalWebSocketServer is running");

        try {
            sendFileContents();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method.
     * @param args command line arguments.
     */
    public static void main(final String... args)
    {
        ExternalWebSocketSimEmulator sim = new ExternalWebSocketSimEmulator();
    }

    private void sendFileContents() throws FileNotFoundException {
        long lastMsgUnixMillis = -1;

        InputStream inputStream = getClass().getResourceAsStream("/websocketlogs/" + fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: /websocketlogs/" + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (server.getConnections().isEmpty()) {
                // Lets wait until a client connects to the server, before sending data
            }
            CategoryLogger.always().debug("Client connected");
            Thread.sleep(3000);  // Wait some time until client is loaded
            CategoryLogger.always().debug("Start sending messages");

            String line;
            while ((line = reader.readLine()) != null) {
                int position = line.indexOf(":");  // gets the first occurance of a ':'
                String timeStr = line.substring(0, position);
                long time = (long) Double.parseDouble(timeStr);
                String message = line.substring(position+1);  // +1 as there is a space after ':'
                long timeToSleep;
                if (lastMsgUnixMillis <= 0) {
                    timeToSleep = 0;
                } else {
                    timeToSleep = time - lastMsgUnixMillis;
                }
                if (timeToSleep > 2000) {
                    timeToSleep = 2000;  // sometimes there is too much waiting time in the records
                }
                preciseSleep(timeToSleep);
                server.broadcast(message);
                CategoryLogger.always().debug("SENT: " + message);
                lastMsgUnixMillis = time;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Thread.sleep() is not precise, this function is more accurate
    public void preciseSleep(long millis) {
        long currentMillisTime = System.currentTimeMillis();
        long end = currentMillisTime + millis;

        while (currentMillisTime < end) {
            currentMillisTime = System.currentTimeMillis();
        }
    }

    private class ExternalWebSocketServer extends WebSocketServer {

        public ExternalWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        public void onOpen(WebSocket conn, ClientHandshake handshake) {  // New client connected
            JSONObject msg = new JSONObject();
            msg.put("type", "STATUS");
            msg.put("data", "SILAB acknowledged connection");
            conn.send(msg.toString());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

        @Override
        public void onMessage(WebSocket conn, String message) {
            CategoryLogger.always().debug("RECEIVED: " + message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {

        }

        @Override
        public void onStart() {
            System.out.println("WebSocket server started on port: " + getPort());
        }

        public void broadcast(String message) {
            for (WebSocket conn : this.getConnections()) {
                conn.send(message);
            }
        }
    }
}
