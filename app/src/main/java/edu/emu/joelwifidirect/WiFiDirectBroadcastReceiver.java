package edu.emu.joelwifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private HostActivity hostActivity;
    private WifiP2pManager.PeerListListener peerListListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager wifiManager, WifiP2pManager.Channel channel, HostActivity hostActivity, WifiP2pManager.PeerListListener peerListListener) {
        super();
        this.wifiManager = wifiManager;
        this.channel = channel;
        this.hostActivity = hostActivity;
        this.peerListListener = peerListListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                Toast.makeText(hostActivity, "Please turn on WiFi", Toast.LENGTH_LONG).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wifiManager != null) {
                wifiManager.requestPeers(channel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                wifiManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                        ((PeerAdapter) hostActivity.peerListView.getAdapter()).setConnectedPeers(new ArrayList(wifiP2pGroup.getClientList()));
                    }
                });
                wifiManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        String ownerAddress = info.groupOwnerAddress.getHostAddress();
                        //Toast.makeText(hostActivity, "Server IP Address " + ownerAddress, Toast.LENGTH_SHORT).show();
                        //connectedDevices.add(s);
                            /*boolean owner = info.isGroupOwner;
                            if(owner) {
                                Toast.makeText(hostActivity, "Group Owner", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(hostActivity, "Peer" + ownerAddress, Toast.LENGTH_SHORT).show();
                                //hostActivity.start_waiting(s);
                            }*/
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}