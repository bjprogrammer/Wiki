package com.search.wiki.network;

import com.search.wiki.model.FeedResponse;
import com.search.wiki.utils.Constants;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


//Networking using RxJava
public class Service {
    public Service(){ }

    public void getFeedsList(final FeedsCallback callback,String search, int imageSize, int page){
        NetworkAPI.getClient().create(NetworkService.class).getSearchFeed(Constants.FORMAT, Constants.ACTION,
                Constants.PROP, Constants.GENERATOR, Constants.PIPROP,imageSize,search, Constants.PAGE_SIZE, (page* Constants.PAGE_SIZE) +1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<FeedResponse>() {
                @Override
                public void onSubscribe(Disposable d) {
                    callback.getDisposable(d);
                }

                @Override
                public void onNext(FeedResponse response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(new NetworkError(e));
                }

                @Override
                public void onComplete() { }
            });
    }

    public interface FeedsCallback{
        void onSuccess(FeedResponse response);
        void onError(NetworkError networkError);
        void getDisposable(Disposable disposable);
    }
}

