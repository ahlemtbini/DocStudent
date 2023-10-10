package com.technifysoft.docstudent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;





public class network extends Fragment {
    TextView internet;
    ImageView wifi;
    ImageView nowifi;
    Context context;

    //private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        internet = view.findViewById(R.id.internet);
        wifi = view.findViewById(R.id.imageView);
        nowifi = view.findViewById(R.id.imageView1);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context = getContext();
        context.registerReceiver(Connectivity, filter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(Connectivity);
    }

    public boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private BroadcastReceiver Connectivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                internet.setText("Your device is currently connected");
                wifi.setVisibility(View.VISIBLE);
                nowifi.setVisibility(View.INVISIBLE);
            } else {
                internet.setText("Your device is not connected!! ");
                nowifi.setVisibility(View.VISIBLE);
                wifi.setVisibility(View.INVISIBLE);
            }

            return;
        }
    };
}