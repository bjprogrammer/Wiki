package com.search.wiki.detail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.search.wiki.MyApplication;
import com.search.wiki.R;
import com.search.wiki.databinding.FragmentDetailBinding;
import com.search.wiki.utils.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import es.dmoral.toasty.Toasty;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private Activity activity;

    private String url, urlType;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof Activity){
            activity = (Activity)context;
        }
        else {
            activity = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        View view = binding.getRoot();

        String title = activity.getIntent().getStringExtra(Constants.Title);
        binding.setUrl(Constants.WIKI_BASE_URL+title);
        activity.setTitle(title);
        return view;
    }

    @BindingAdapter({"bind:url","bind:progressBar"})
    public static void loadMedia(WebView view, String url, ProgressBar progressBar) {
        File httpCacheDirectory = new File(MyApplication.getContext().getCacheDir(), "offlineCache");

        view.loadUrl(url);
        WebSettings webSettings = view.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCachePath(httpCacheDirectory.getPath());
        webSettings.setAppCacheEnabled(true);

        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }


            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toasty.error( view.getContext(), "Could not load page", Toast.LENGTH_LONG).show();
            }

            public void onLoadResource(WebView view, String url) { }

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            }
        });
    }
}
