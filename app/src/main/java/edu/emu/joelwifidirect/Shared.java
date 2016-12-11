package edu.emu.joelwifidirect;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;

public class Shared  {
    protected static WifiP2pManager wifiManager;
    protected static WifiP2pManager.Channel channel;
    protected static IntentFilter intentFilter;

    public static void set(WifiP2pManager wifiManager, WifiP2pManager.Channel channel, IntentFilter intentFilter) {
        Shared.wifiManager = wifiManager;
        Shared.channel = channel;
        Shared.intentFilter = intentFilter;
    }
}