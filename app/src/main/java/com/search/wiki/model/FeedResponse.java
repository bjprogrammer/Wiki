package com.search.wiki.model;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.library.baseAdapters.BR;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class FeedResponse extends BaseObservable {

	private Query query;

	public void setQuery(Query query) {
		this.query = query;
		notifyPropertyChanged(BR.query);
	}

	@Bindable
	public Query getQuery() {
		return query;
	}

	public static class Query extends BaseObservable {
		@Bindable
		public Map<String, Search> getSearch() {
			return search;
		}

		public void setSearch(Map<String, Search> search) {
			this.search = search;
			notifyPropertyChanged(BR.search);
		}

		@SerializedName("pages")
		private Map<String, Search> search;

		public static class Search extends BaseObservable {
			@Bindable
			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
				notifyPropertyChanged(BR.id);
			}

			@Bindable
			public int getIndex() {
				return index;
			}

			public void setIndex(int index) {
				this.index = index;
				notifyPropertyChanged(BR.index);
			}

			@Bindable
			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
				notifyPropertyChanged(BR.title);
			}

			@Bindable
			public Image getImage() {
				return image;
			}

			public void setImage(Image image) {
				this.image = image;
				notifyPropertyChanged(BR.image);
			}

			@Bindable
			public Terms getTerms() {
				return terms;
			}

			public void setTerms(Terms terms) {
				this.terms = terms;
				notifyPropertyChanged(BR.terms);
			}

			@SerializedName("pageid")
			private int id;

			private int index;
			private String title;

			@SerializedName("thumbnail")
			private Image image;

			private Terms terms;

			public class Image extends BaseObservable{
				@Bindable
				public String getSource() {
					return source;
				}

				public void setSource(String source) {
					this.source = source;
					notifyPropertyChanged(BR.source);
				}

				private String source;
			}

			public class Terms extends  BaseObservable{
				@Bindable
				public List<String> getDescription() {
					return description;
				}

				public void setDescription(List<String> description) {
					this.description = description;
					notifyPropertyChanged(BR.description);
				}

				private List<String> description;
			}
		}
	}

	@BindingAdapter({"bind:titleText"})
	public static void setTitle(TextView view, String title) {
		view.setText(title);
	}

	@BindingAdapter({"bind:description"})
	public static void setDescription(TextView view, List<String> description) {
		if(description!=null) {
			view.setText(description.get(0));
		}else {
			view.setText("");

		}
	}

	@BindingAdapter({"bind:url"})
	public static void loadImage(ImageView view, Query.Search search) {
		if (search.getImage() != null) {
			String url = search.getImage().getSource();
			view.setVisibility(View.INVISIBLE);

			if (url != null) {
				Glide.with(view.getContext())
						. load(url)
						.thumbnail(0.1f)
						.listener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								return false;
							}

							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								view.setVisibility(View.VISIBLE);
								return false;
							}
						})
						. into(view);
			} else {
				view.setVisibility(View.INVISIBLE);
			}
		}else {
			view.setVisibility(View.INVISIBLE);
		}
	}
}