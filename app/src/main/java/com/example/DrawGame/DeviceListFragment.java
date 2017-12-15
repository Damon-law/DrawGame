package com.example.DrawGame;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminlyb on 2017/12/14.
 */

public class DeviceListFragment extends ListFragment {
    private static final String TAG = "PTP_ListFrag";

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(),R.layout.row_devices,peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list,null);
        return mContentView;
    }

    public WifiP2pDevice getDevice(){
        return device;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        ((DeviceActionListener)getActivity()).showDetails(device);
    }

    public void updateThisDevice(WifiP2pDevice device) { // callback of this device details changed bcast event.
        TextView nameview = (TextView) mContentView.findViewById(R.id.my_name);
        TextView statusview = (TextView) mContentView.findViewById(R.id.my_status);

        if ( device != null) {
            Log.d(TAG, "updateThisDevice: " + device.deviceName + " = " + Util.getDeviceStatus(device.status));
            this.device = device;
            nameview.setText(device.deviceName);
            statusview.setText(Util.getDeviceStatus(device.status));
        } else if (this.device != null ){
            nameview.setText(this.device.deviceName);
            statusview.setText("WiFi Direct Disabled, please re-enable.");
        }
    }

    public void onPeersAvailable(List<WifiP2pDevice> peerList) {   // the callback to collect peer list after discover.
        if (progressDialog != null && progressDialog.isShowing()) {  // dismiss progressbar first.
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList);
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(TAG, "onPeersAvailable : No devices found");
            return;
        }
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Searching...", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
    }
    public void clearPeers() {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                peers.clear();
                ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
                Toast.makeText(getActivity(), "p2p connection lost.please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }
    class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice>items;

        public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice>objects){
            super(context,textViewResourceId,objects);
            items = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(Util.getDeviceStatus(device.status));
                }
                Log.d(TAG, "WiFiPeerListAdapter : getView : " + device.deviceName);
            }
            return v;
        }
    }
}
