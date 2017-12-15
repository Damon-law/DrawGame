package com.example.DrawGame;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiP2p extends Activity implements DeviceActionListener{
    public static final int PIC_PORT = 8888;
    public static final int NAME_POCT = 8898;
    protected static final String TAG = "HOME";
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel channel;
    private MyWifiP2pBroadcastReceiver receiver;
    private MessageBrocastReceiver MsgReceiver;
    private WifiP2pInfo info;
    private WifiP2pManager.PeerListListener peerListListener;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private StartListener startListener;
    final HashMap<String, String> buddies = new HashMap<String, String>();
    private final IntentFilter intentFilter = new IntentFilter();
    private IntentFilter Msgintentfilter = new IntentFilter();
    private List peers = new ArrayList();

    //    private ChatConnection mConnection;
    private DeviceListFragment listFragment;
    private DeviceDetailFragment detailFragment;
    private TextView user_name;
    private ImageView img;

    //user info
    private HashMap<String,String>hostToName;
    private HashMap<String,String>hostToPic;
    private List<String>host;
    private InfoServer server;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p);
        listFragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
        detailFragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        final Intent intent = getIntent();
        user_name = findViewById(R.id.user_name);
        img = findViewById(R.id.head);
        user_name.setText(intent.getExtras().getString(MainActivity.USER_NAME));
        img.setImageBitmap(BitmapFactory.decodeFile(intent.getExtras().getString(MainActivity.PHOTO_PATH)));
        InitFilter();
        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {

            @Override
            public void onChannelDisconnected() {
                // TODO Auto-generated method stub

            }
        });
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());
                listFragment.onPeersAvailable(peers);
            }
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                info = wifiP2pInfo;
                if(info.isGroupOwner && info.groupFormed){
                        server = new InfoServer(PIC_PORT, NAME_POCT,hostToName,hostToPic,host);
                        server.run();
                        hostToName.put(info.groupOwnerAddress.getHostAddress(),intent.getExtras().getString(MainActivity.USER_NAME));
//                    InitializeTask server = new InitializeTask(WifiP2p.this,hostToName);
//                    server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    findViewById(R.id.start).setVisibility(View.VISIBLE);
                }else if(info.groupFormed){
                    findViewById(R.id.prepare).setVisibility(View.VISIBLE);
                }
            };
        };

        startListener = new StartListener() {
            @Override
            public void ListenningChange() {
             UserInfoSerializable info = new UserInfoSerializable(hostToName,hostToPic,host);
             Intent intent = new Intent(WifiP2p.this,DrawActivity.class);
             intent.putExtra("INFO",info);
//             startActivity(intent);
            }
        };

        MsgReceiver = new MessageBrocastReceiver(startListener);
        receiver = new MyWifiP2pBroadcastReceiver(mManager,channel,peerListListener,connectionInfoListener,listFragment,WifiP2p.this,info);
        registerReceiver(receiver,intentFilter);
        registerReceiver(MsgReceiver,Msgintentfilter);
        Button prepare = findViewById(R.id.prepare);
        prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Button start  = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   // using app icon for navigation up or home:
                Log.d(TAG, "home clicked.");
                // startActivity(new Intent(home.class, Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            case R.id.atn_direct_enable:
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                return true;
            case R.id.atn_direct_discover:

                listFragment.onInitiateDiscovery();
                Log.d(TAG, "onOptionsItemSelected : start discoverying ");
                discoverPeers();

                return true;

            case R.id.disconnect:
                Log.d(TAG, "onOptionsItemSelected : disconnect all connections and stop server ");
//                if (mConnection!=null)
//                    mConnection.tearDown();
//                mConnection=null;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        detailFragment.showDetails(device);
    }

    @Override
    public void cancelDisconnect() {

    }

    @Override
    public void connect(WifiP2pConfig config) {
        mManager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiP2p.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void disconnect() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                detailFragment.resetViews();
            }
        });

        mManager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int paramInt) {
                // TODO Auto-generated method stub
                detailFragment.getView().setVisibility(View.GONE);
            }
        });

    }

    public void InitFilter(){

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }
    public void InitMsgFilter(){
        Msgintentfilter.addAction(MessageBrocastReceiver.START_GAME);
        Msgintentfilter.addAction(MessageBrocastReceiver.END_GET_PICTURE);
        Msgintentfilter.addAction(MessageBrocastReceiver.START_GET_PICTURE);
        Msgintentfilter.addAction(MessageBrocastReceiver.START_GET_USER_NAME);
        Msgintentfilter.addAction(MessageBrocastReceiver.END_GET_USER_NAME);
    }

    private void discoverPeers() {
        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                Toast.makeText(WifiP2p.this, "Searching devices", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOptionsItemSelected : discovery succeed");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.\
                listFragment.clearPeers();
                Toast.makeText(WifiP2p.this, "Discovery Failed, try again... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(MsgReceiver);
    }

    public void startActivityCurrently(){

    }
}
