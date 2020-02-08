package com.search.wiki.main;

import com.search.wiki.model.FeedResponse;

public class MainContract{
    interface MainView {
        void showWait();
        void removeWait();
        void onFailure(String appErrorMessage);
        void onSuccess(FeedResponse response);

    }

    interface MainPresenter{
         void  setLastSearch(String search);
         String getLastSearch();
         void getFeedList(String search, int imageSize, int page);
         void unSubscribe();
    }
}
