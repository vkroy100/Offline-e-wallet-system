package com.example.oepay;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    NearBySend activity;
    public WifiBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                System.out.println("Wifi on");
            } else {
                System.out.println("Wifi off");
            }
        } else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            System.out.println("We have to request peers now!");
            activity.wifiP2pManager.requestPeers(activity.channel, activity.peerListListener);
        } else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            System.out.println("Connection changed do something!");
            if(activity.wifiP2pManager==null){
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                activity.wifiP2pManager.requestConnectionInfo(activity.channel, activity.connectionInfoListener);
            } else {
                System.out.println("Device disconnected...");
            }
        } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            System.out.println("This device's configuration changed!");
        }
    }


    void setNearByDevicesHandler(NearBySend nearByDevices){
        this.activity = nearByDevices;
    }
}


