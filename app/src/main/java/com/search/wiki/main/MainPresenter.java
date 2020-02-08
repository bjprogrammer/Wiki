package com.search.wiki.main;

import android.content.Context;

import com.search.wiki.model.FeedResponse;
import com.search.wiki.network.NetworkError;
import com.search.wiki.network.Service;
import com.search.wiki.utils.Constants;
import com.search.wiki.utils.MyPrefs;

import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.MainPresenter{

    private MainContract.MainView view;
    private Disposable disposable;

    private Context context;

    public MainPresenter(Context context,MainContract.MainView view) {
        this.view=view;
        this.context=context;
    }

    public void getFeedList(String search, int imageSize, int page) {
        if(page == 0) {
            view.showWait();
        }

        new Service().getFeedsList(new Service.FeedsCallback() {
            @Override
            public void onSuccess(FeedResponse response)  {

                if(page == 0) {
                    view.removeWait();
                }

                if(response != null) {
                    view.onSuccess(response);
                }
            }

            @Override
            public void onError(final NetworkError networkError) {
                if(page == 0) {
                    view.removeWait();
                }
                view.onFailure(networkError.getAppErrorMessage());
            }

            @Override
            public void getDisposable(Disposable d) {
                disposable = d;
            }
        },search, imageSize, page);
    }

    public void unSubscribe(){
        if(disposable != null){
            disposable.dispose();
        }
    }

    public String getLastSearch(){
        return MyPrefs.getString(context, Constants.SEARCH, Constants.DEFAULT_SEARCH);
    }

    public void setLastSearch(String search){
        MyPrefs.putString(context, Constants.SEARCH , search);
    }
}
