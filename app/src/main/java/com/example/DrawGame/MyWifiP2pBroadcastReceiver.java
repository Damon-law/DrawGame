package com.example.DrawGame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by adminlyb on 2017/12/14.
 */

public class MyWifiP2pBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private DeviceListFragment listFragment;
    private Activity activity;
    private WifiP2pInfo info;
    public int deviceCount;
    public MyWifiP2pBroadcastReceiver(){
        mManager = null;
        channel = null;
        peerListListener = null;
        connectionInfoListener = null;
        listFragment = null;
    }
    public MyWifiP2pBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel channel,
                                      WifiP2pManager.PeerListListener peerListListener, WifiP2pManager.ConnectionInfoListener connectionInfoListener,
                                      DeviceListFragment listFragment, Activity activity, WifiP2pInfo info) {
        this.mManager = mManager;
        this.channel = channel;
        this.peerListListener = peerListListener;
        this.connectionInfoListener = connectionInfoListener;
        this.listFragment = listFragment;
        this.activity = activity;
        this.info = info;
        deviceCount = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//	                activity.setIsWifiP2pEnabled(true);
//	            } else {
//	                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(channel, peerListListener);
            }
            Log.d(TAG, "P2P peers changed");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(channel, connectionInfoListener);
                Toast.makeText(activity,"已连接",Toast.LENGTH_SHORT);
                deviceCount += 1;
//                if(info.isGroupOwner && info.groupFormed){
//                    Toast.makeText(activity,"YOU ARE THE OWNER",Toast.LENGTH_LONG);
//                }else if(info.groupFormed){
//                    activity.findViewById(R.id.prepare).setVisibility(View.VISIBLE);
//                }
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            listFragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}
