package com.search.wiki.main;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.search.wiki.R;
import com.search.wiki.utils.ConnectivityReceiver;
import org.greenrobot.eventbus.EventBus;
import es.dmoral.toasty.Toasty;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.activity_main);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        addFragment();
    }


    private void addFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentByTag("list");

        if(mainFragment == null){
            mainFragment = new MainFragment();
        }

        if(!mainFragment.isAdded()) {
            ft.add(R.id.frameLayout, mainFragment, "list");
            ft.commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    //Checking internet isConnected using broadcast receiver
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(this.isConnected !=isConnected)
        {
            if(isConnected){
                Toasty.success(this, "Connected to internet", Toast.LENGTH_SHORT, true).show();
            }
            else
            {
                Toasty.error(getApplicationContext(), "Not connected to internet", Toast.LENGTH_LONG, true).show();
            }
        }
        this.isConnected = (isConnected);

        EventBus.getDefault().post(this.isConnected);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) { }
        }
    }
}
