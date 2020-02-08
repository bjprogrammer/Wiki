package com.search.wiki.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.search.wiki.R;
import com.search.wiki.model.FeedResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FeedResponse.Query.Search> data;
    private onPressListener listener;

    // flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public interface  onPressListener{
        void onClick(FeedResponse.Query.Search search);
    }

    public MainAdapter( onPressListener listener) {
        data = new ArrayList<>();
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (i) {
            case ITEM:
                viewHolder = new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_feed, viewGroup, false));
                break;
            case LOADING:
                viewHolder = new Loading(DataBindingUtil.inflate(inflater, R.layout.item_progress, viewGroup, false).getRoot());
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {

        switch (getItemViewType(i)) {
            case ITEM:
                MainAdapter.ViewHolder viewHolder = (MainAdapter.ViewHolder) holder;
                viewHolder.bind(data.get(i),listener);
                break;

            case LOADING:
//                Do nothing
                break;
        }
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == data.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }


        public void bind(Object obj,onPressListener listener) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onClick(((FeedResponse.Query.Search)obj));
                }
            });

            binding.setVariable(BR.search, obj);
            binding.executePendingBindings();
        }
    }

    protected class Loading extends RecyclerView.ViewHolder {
        public Loading(View itemView) {
            super(itemView);
        }
    }

    public void add(FeedResponse.Query.Search response) {
        data.add(response);
        notifyItemInserted(data.size() - 1);
    }


    public void addAll(Map<String, FeedResponse.Query.Search> mcList) {
        for (Map.Entry<String, FeedResponse.Query.Search> entry : mcList.entrySet()) {
            add(entry.getValue());
        }
    }

    public void remove(FeedResponse.Query.Search tweet) {
    int position = data.indexOf(tweet);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new FeedResponse.Query.Search());
    }

    public void removeLoadingFooter() {
        if(!data.isEmpty()) {
            isLoadingAdded = false;

            int position = data.size() - 1;
            FeedResponse.Query.Search item = getItem(position);
            if (item != null) {
                data.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public FeedResponse.Query.Search getItem(int position) {
        return data.get(position);
    }
}
