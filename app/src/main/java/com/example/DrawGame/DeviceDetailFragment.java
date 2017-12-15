package com.example.DrawGame;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by adminlyb on 2017/12/14.
 */

public class DeviceDetailFragment extends Fragment {
    private static final String TAG = "PTP_Detail";
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_details, null);

        // connect button, per
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = 0;  // least inclination to be group owner.
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Establishing Connection",
                        "Connecting to :" + device.deviceAddress, true, true,  // cancellable
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                ((DeviceActionListener) getActivity()).cancelDisconnect();
                            }
                        });
                // perform p2p connect upon user click the connect button, connect available handle when connection done.
                ((DeviceActionListener) getActivity()).connect(config);
                progressDialog.dismiss();
            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        // p2p connected, manager request connection info done, group owner elected.
//        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "start_client button clicked, start chat activity !");
//                        //TODO
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        getActivity().startActivity(intent);
//                    }
//                });

        mContentView.findViewById(R.id.prepare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        ChatApplication app = (ChatApplication) getActivity().getApplication();
//        if(app.connection != null && app.connection.isReady()) {
//            // hide the connect button and enable start chat button
//            mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
//            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
//            Log.d(TAG, "onConnectionInfoAvailable: socket connection established, show start chat button ! ");
//
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//        }
    }

    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);

    }

    public void resetViews() {
        Log.d(TAG, "resetViews: detail frag dismiss progress dialog and clear views");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
