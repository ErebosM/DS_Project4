package com.example.manuel.wifimesh;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

    static final public String DSS_CLIENT_VALUES = "test.microsoft.com.mywifimesh.DSS_CLIENT_VALUES";
    static final public String DSS_CLIENT_MESSAGE = "test.microsoft.com.mywifimesh.DSS_CLIENT_MESSAGE";


    LocalBroadcastManager broadcaster;
    private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private ChatManager chat;
    private String mAddress;
    private int mPort;
    private Socket socket;
    private boolean socket_connected;

    public ClientSocketHandler(Handler handler, String groupOwnerAddress, int port,Context context) {
        this.broadcaster = LocalBroadcastManager.getInstance(context);
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.mPort = port;
    }

    @Override
    public void run() {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress,mPort));
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, handler, "Client");
            new Thread(chat).start();
            socket_connected = true;
        } catch (Exception e) {
            if(broadcaster != null) {
                Intent intent = new Intent(DSS_CLIENT_VALUES);
                intent.putExtra(DSS_CLIENT_MESSAGE, e.toString());
                broadcaster.sendBroadcast(intent);
            }
            try {
                socket.close();
            } catch (Exception e1) {
                if(broadcaster != null) {
                    Intent intent = new Intent(DSS_CLIENT_VALUES);
                    intent.putExtra(DSS_CLIENT_MESSAGE, e.toString());
                    broadcaster.sendBroadcast(intent);
                }
            }
            return;
        }
    }

    public void close_socket() throws IOException {
        if(socket != null && socket_connected) {
            socket.close();
            socket_connected = false;
        }
    }

    public ChatManager getChat() {
        return chat;
    }

    public Socket getSocket() {return socket;}

}
