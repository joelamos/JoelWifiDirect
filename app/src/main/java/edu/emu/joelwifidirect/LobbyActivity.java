package edu.emu.joelwifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LobbyActivity extends Activity {

    private BroadcastReceiver broadcastReceiver;
    private List<WifiP2pDevice> peers = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        ListView peerListView = (ListView) findViewById(R.id.lobbyPeerList);
        final PeerAdapter peerAdapter = new PeerAdapter(this, android.R.layout.simple_list_item_1, peers);
        peerListView.setAdapter(peerAdapter);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                        Toast.makeText(getThis(), "Please turn on WiFi", Toast.LENGTH_LONG).show();
                    }
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.isConnected()) {
                        Shared.wifiManager.requestGroupInfo(Shared.channel, new WifiP2pManager.GroupInfoListener() {
                            @Override
                            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                                peers.clear();
                                peers.add(wifiP2pGroup.getOwner());
                                peers.addAll(wifiP2pGroup.getClientList());
                                try {
                                    Toast.makeText(getThis(), "Hello I'm here", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {

                                }
                                peerAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, Shared.intentFilter);

        Shared.wifiManager.requestGroupInfo(Shared.channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                peers.clear();
                peers.add(wifiP2pGroup.getOwner());
                peers.addAll(wifiP2pGroup.getClientList());
                peerAdapter.notifyDataSetChanged();
                try {
                    Toast.makeText(getThis(), "Hello I'm here2", Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Shared.intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private Activity getThis() {
        return this;
    }
}
