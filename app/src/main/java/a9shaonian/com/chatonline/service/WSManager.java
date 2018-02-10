package a9shaonian.com.chatonline.service;


import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WSManager {
    private static WSManager wsManager;

    private StatusChangeListener statusChangeListener;

    public void addMsgListener(StatusChangeListener statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
    }


    private WSManager() {
    }

    public static synchronized WSManager getInstance() {
        synchronized (WSManager.class) {
            if (wsManager == null) {
                wsManager = new WSManager();
            }
            return wsManager;
        }
    }

    public void init(String address) throws IOException {
        new WebSocketFactory().createSocket(address).addListener(new WSListener()).connectAsynchronously();
    }


    class WSListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            statusChangeListener.message(text);
        }


        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
            statusChangeListener.connect();
        }


        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
            statusChangeListener.error();
            exception.printStackTrace();
        }


        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            statusChangeListener.disconnect();
        }


    }


}

