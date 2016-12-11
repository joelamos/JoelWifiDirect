package edu.emu.joelwifidirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HostActivity extends Activity {

    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private List<WifiP2pDevice> peers = new ArrayList();
    private WifiP2pManager.PeerListListener peerListListener;
    private TextView groupSizeText;
    protected ListView peerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Button findPeersButton = (Button) findViewById(R.id.findPeersHostButton);
        findPeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });
        groupSizeText = (TextView) findViewById(R.id.groupSizeText);
        peerListView = (ListView) findViewById(R.id.hostPeerList);
        final PeerAdapter peerAdapter = new PeerAdapter(this, android.R.layout.simple_list_item_1, peers);
        peerListView.setAdapter(peerAdapter);
        registerListViewClickListener(peerListView);
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                List<WifiP2pDevice> connectedPeers = peerAdapter.getConnectedPeers();
                Iterator<WifiP2pDevice> connectedIterator = connectedPeers.iterator();
                while (connectedIterator.hasNext()) {
                    WifiP2pDevice connectedPeer = connectedIterator.next();
                    boolean stillAPeer = false;
                    for (WifiP2pDevice peer : peers) {
                        if (connectedPeer.equals(peer)) {
                            stillAPeer = true;
                            break;
                        }
                    }
                    if (!stillAPeer) {
                        connectedIterator.remove();
                    }
                }
                peerAdapter.setConnectedPeers(connectedPeers);
                peerAdapter.notifyDataSetChanged();
                //Toast.makeText(getThis(), "Found " + peers.size() + " peer" + (peers.size() == 1 ? "" : "s"), Toast.LENGTH_LONG).show();
            }
        };

        broadcastReceiver = new WiFiDirectBroadcastReceiver(Shared.wifiManager, Shared.channel, this, peerListListener);
        registerReceiver(broadcastReceiver, Shared.intentFilter);
        discoverPeers();
    }

    private void discoverPeers() {
        Toast.makeText(this, "Searching for peers...", Toast.LENGTH_LONG).show();
        Shared.wifiManager.discoverPeers(Shared.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getThis(), "Found peers", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(getThis(), "Failed to find peers", Toast.LENGTH_LONG).show();
                peers.clear();
                ((ArrayAdapter) peerListView.getAdapter()).notifyDataSetChanged();
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

    private void registerListViewClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                final TextView textView = (TextView) viewClicked;

                WifiP2pConfig config = new WifiP2pConfig();
                config.groupOwnerIntent = 15;
                config.deviceAddress = ((WifiP2pDevice) peers.get(position)).deviceAddress;
                Shared.wifiManager.connect(Shared.channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(getThis(), "Connected to " + textView.getText(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getThis(), "Failed to connect", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    protected void setGroupSize(int size) {
        groupSizeText.setText("Group size: " + size);
    }

    private Activity getThis() {
       return this;
    }
}