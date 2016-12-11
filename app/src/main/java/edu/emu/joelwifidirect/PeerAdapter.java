package edu.emu.joelwifidirect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PeerAdapter extends ArrayAdapter {

    private List<WifiP2pDevice> peers;
    private List<WifiP2pDevice> connectedPeers = new ArrayList<WifiP2pDevice>();
    private Activity activity;
    private boolean isOwnerList;

    public PeerAdapter(Activity activity, int resource, List<WifiP2pDevice> peers) {
        super((Context) activity, resource, peers);
        isOwnerList = activity instanceof HostActivity;
        this.activity = activity;
        this.peers = peers;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        WifiP2pDevice peer = peers.get(position);
        textView.setText(peer.deviceName + (isOwnerList ? "\n" + peer.deviceAddress : ""));
        textView.setTextColor(Color.BLACK);
        for (WifiP2pDevice connectedPeer : connectedPeers) {
            if (connectedPeer.equals(peer)) {
                textView.setTextColor(0xFF428927);
                break;
            }
        }
        return textView;
    }

    public void setConnectedPeers(List<WifiP2pDevice> connectedPeers) {
        if (isOwnerList) {
            this.connectedPeers = new ArrayList<WifiP2pDevice>(connectedPeers);
            notifyDataSetChanged();
            ((HostActivity) activity).setGroupSize(connectedPeers.size() + 1);
        }
    }

    public List<WifiP2pDevice> getConnectedPeers() {
        return new ArrayList(connectedPeers);
    }
}
