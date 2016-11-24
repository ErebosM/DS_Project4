package ch.ethz.inf.vs.a4.wdmf_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener{//}, MessageTarget {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private ArrayList<WifiP2pDevice> peers_list;

    static final int SERVER_PORT = 4545;
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        peers_list = new ArrayList<WifiP2pDevice>();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Button btn_search = (Button) findViewById(R.id.search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverPeers();
            }
        });
        Button btn_connect = (Button) findViewById(R.id.connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPeers();
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        if (mManager != null && mChannel != null) {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d("wifidirect:  ", "Disconnect failed. Reason :" + reasonCode);
                }
                @Override
                public void onSuccess() {
                }
            });
        }
        super.onStop();
    }

    void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //when successful
                //TODO
            }

            @Override
            public void onFailure(int reasonCode) {
                //when there was a problem
                //TODO
            }
        });
    }

    void connectPeers() {
        //connects to every peers around
        if (this.peers_list.size() == 0) {
            TextView research = (TextView) findViewById(R.id.research);
            research.setText("Found 0 device :'(");
            return;
        }
        for (WifiP2pDevice device : this.peers_list) {
            connect_device(device);
        }
    }

    void connect_device(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        final String s = config.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //if it connected, we display the info of the device on the screen
                TextView text = (TextView) findViewById(R.id.device);
                String devices_connected = text.getText().toString() + "\n" + s;
                text.setText(devices_connected);
            }
            @Override
            public void onFailure(int reason) {
                //TODO: Retry after timeout?
                Toast.makeText(MainActivity.this, "Connection failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         *

        Thread handler = null;

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
            Log.d("wifidirect:  ", "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(((MessageTarget) this).getHandler());
                handler.start();
            } catch (IOException e) {
                Log.d("wifidirect:  ", "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case,
            // you'll want to create a client thread that connects to the group
            // owner.
            Log.d("wifidirect:  ", "Connected as peer");
            handler = new ClientSocketHandler(((MessageTarget) this).getHandler(), p2pInfo.groupOwnerAddress);
            handler.start();
        }
        chatFragment = new WiFiChatFragment();
        getFragmentManager().beginTransaction().replace(R.id.container_root, chatFragment).commit();*/
    }

    ArrayList<WifiP2pDevice> getPeersList() {
        return this.peers_list;
    }

    void setPeersList(ArrayList<WifiP2pDevice> a) {
        this.peers_list = a;
    }
}
