package com.search.wiki.main;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.search.wiki.MyApplication;
import com.search.wiki.R;
import com.search.wiki.databinding.FragmentMainBinding;
import com.search.wiki.detail.DetailActivity;
import com.search.wiki.model.FeedResponse;
import com.search.wiki.utils.Constants;
import com.search.wiki.utils.PaginationScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainFragment extends Fragment implements  MainContract.MainView{

    private FragmentMainBinding binding;
    private MainPresenter presenter;

    private MainAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private boolean isLoading = false, isSearching = false;
    private boolean isSavedLastPage = false, isCurrentLastPage = false;
    private int savedPage = 0, currentPage = 0;

    private String savedSearch, currentSearch;
    private Activity activity;
    private SearchView search;
    private Boolean isConnected = true;
    private int imageSize;
    private PublishSubject<String> subject;
    private Disposable disposable;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        presenter = new MainPresenter(getContext(),this);

        recyclerView = binding.recyclerView;
        progressBar = binding.progressBar;

        savedSearch = presenter.getLastSearch();
        imageSize = (int) (getResources().getDimension(R.dimen.thumbnail_size)/ getResources().getDisplayMetrics().density);


        setupRecyclerView();

        presenter.getFeedList(savedSearch, imageSize, savedPage);
        return binding.getRoot();
    }


    private void setupRecyclerView(){
        adapter = new MainAdapter(new MainAdapter.onPressListener() {
            @Override
            public void onClick(FeedResponse.Query.Search search) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(Constants.Title,search.getTitle());
                startActivity(intent);
                activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
            }
        });

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration mDivider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        mDivider.setDrawable(getResources().getDrawable(R.drawable.list_divider));
        recyclerView.addItemDecoration(mDivider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;

                if(isSearching){
                    presenter.getFeedList(currentSearch, imageSize, currentPage +1);

                }else
                {
                    presenter.getFeedList(savedSearch, imageSize, savedPage +1);
                }
            }

            @Override
            public boolean isLastPage() {
                if(isSearching){
                    return isCurrentLastPage;

                }else
                {
                    return isSavedLastPage;
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void onDetach() {
        presenter.unSubscribe();
        presenter = null;
        super.onDetach();
    }

    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        if(isConnected) {
            adapter.removeLoadingFooter();
            isLoading = false;
            Toasty.error(MyApplication.getContext(), appErrorMessage, Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    public void onSuccess(FeedResponse response) {
        adapter.removeLoadingFooter();
        isLoading = false;

        if(isSearching){
            currentPage +=1 ;
        }else {
            savedPage += 1;
        }

        if (response != null) {
            if (response.getQuery() != null) {
                adapter.addAll(response.getQuery().getSearch());
                if(response.getQuery().getSearch().size()<Constants.PAGE_SIZE){
                   setLastPage();
                }else {
                    adapter.addLoadingFooter();
                }
            } else {
                setLastPage();
            }
        } else {
            setLastPage();
        }
    }


    private void setLastPage(){
        if(isSearching){
            isCurrentLastPage = true;

        }else {
            isSavedLastPage = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuitem=  menu.findItem(R.id.action_search);
        search = (SearchView)menuitem.getActionView();
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        search.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));


        if(search != null)
        {
            subject = PublishSubject.create();
            searchFilter();

            search.setOnQueryTextListener (new SearchView. OnQueryTextListener () {

                // Inserting user query in Shared Preference and searching it using server
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search.clearFocus();

                    savedSearch = query;
                    presenter.setLastSearch(savedSearch);

                    isSearching = false;
                    savedPage = 0;
                    isSavedLastPage = false;

                    adapter.clear();
                    presenter.unSubscribe();
                    presenter.getFeedList(savedSearch,imageSize, savedPage);

                    if (!search.isIconified()) {
                        menuitem.collapseActionView();
                        search.setIconified(true);
                    }
                    return true;
                }


                // Giving suggestions to user while he/she is typing
                @Override
                public boolean onQueryTextChange(String query)
                {
                    if(!TextUtils.isEmpty(query)) {
                        if(disposable.isDisposed()){
                             searchFilter();
                        }
                        subject.onNext(query);
                    }else {

                        if(!disposable.isDisposed()) {
                            disposable.dispose();
                        }

                        isSearching = false;
                        savedPage = 0;
                        isSavedLastPage = false;

                        adapter.clear();
                        presenter.unSubscribe();
                        presenter.getFeedList(savedSearch,imageSize,savedPage);
                    }
                    return true ;
                }
            });

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchFilter(){
        disposable = subject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        currentSearch = s;

                        isSearching = true;
                        currentPage = 0;
                        isCurrentLastPage = false;

                        adapter.clear();
                        presenter.unSubscribe();
                        presenter.getFeedList(currentSearch, imageSize, currentPage);
                        return Observable.empty();
                    }})
                .subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultReceived(Boolean result) {
        this.isConnected = result;

        if(isLoading && isConnected){
            if(isSearching){
                presenter.getFeedList(currentSearch, imageSize,currentPage);

            }else {
                presenter.getFeedList(savedSearch, imageSize, savedPage);
            }
        }
    }

    @Override public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
