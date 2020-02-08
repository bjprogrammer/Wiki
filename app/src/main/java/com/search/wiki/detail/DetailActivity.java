package com.search.wiki.detail;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;


import com.search.wiki.R;
import com.search.wiki.utils.ConnectivityReceiver;

import es.dmoral.toasty.Toasty;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class DetailActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addFragment();
    }


    private void addFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag("media");

        if(detailFragment == null){
            detailFragment = new DetailFragment();
        }

        if(!detailFragment.isAdded()) {
            ft.add(R.id.frameLayout, detailFragment, "media")
                    .commit();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
