package com.search.wiki.network;

import com.search.wiki.model.FeedResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

//ALL API calls endpoints
public interface NetworkService {
    @GET("w/api.php")
    Observable<FeedResponse> getSearchFeed(@Query("format") String format, @Query("action") String action, @Query("prop") String prop, @Query("generator") String generator,
       @Query("piprop") String piprop, @Query("pithumbsize") int thumbSize, @Query("gpssearch") String query, @Query("gpslimit") int pageSize, @Query("gpsoffset") int page);
}

