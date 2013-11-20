/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.ui;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SearchViewCompat;
import android.text.TextUtils;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.ZhihuApp;
import com.github.longkai.zhihu.util.Constants;
import com.github.longkai.zhihu.util.Utils;

import static com.github.longkai.zhihu.util.Constants.*;

/**
 * 热门问答。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class HotItemsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

	private CursorAdapter mAdapter;

	private Button loadMore;

	private String keywords;

	private int page = 1;

	private boolean loading;

	private static final int COUNT = 5; // 每次加载的步长为5个

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new AnswersAdaper(getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 列表为空的试图和加载更多的按钮
		setEmptyText(getString(R.string.empty_list));
		loadMore = (Button) getActivity().getLayoutInflater().inflate(R.layout.load_more, null);
		loadMore.setText(getString(R.string.load_more));
		loadMore.setOnClickListener(this);
		getListView().addFooterView(loadMore);

		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(true); // for search
	}

	// 关键字查询标题和内容是否有相符合的
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem search = menu.add(android.R.string.search_go);
		search.setIcon(R.drawable.action_search_light);
		// earn some room in action bar
		MenuItemCompat.setShowAsAction(search, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		final View searchView = SearchViewCompat.newSearchView(getActivity());
		if (searchView != null) {
			SearchViewCompat.setOnQueryTextListener(searchView,
					new SearchViewCompat.OnQueryTextListenerCompat() {
						@Override
						public boolean onQueryTextChange(String newText) {
							String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
							if (keywords == null && newFilter == null) {
								return true;
							}
							if (keywords != null && keywords.equals(newFilter)) {
								return true;
							}
							keywords = newFilter;
							getLoaderManager().restartLoader(0, null, HotItemsFragment.this);
							return true;
						}
					});
			SearchViewCompat.setOnCloseListener(searchView,
					new SearchViewCompat.OnCloseListenerCompat() {
						@Override
						public boolean onClose() {
							if (!TextUtils.isEmpty(SearchViewCompat.getQuery(searchView))) {
								SearchViewCompat.setQuery(searchView, null, true);
								return true;
							}
							return false;
						}

					});
			MenuItemCompat.setActionView(search, searchView);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, final long id) {
        // 阅读
        Intent intent = new Intent(getActivity(), AnswerActivity.class);
        intent.putExtra(ANSWER_ID, id);
        startActivity(intent);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Utils.parseUri(ITEMS);
        String selection = null;
        // 判断一下是否是用户来查询
        if (!TextUtils.isEmpty(keywords)) {
            String like = Utils.like(keywords);
            selection = "title like " + like
                    + " or description like " + like;
        }
        // roll back the load more button
        loadMore.setClickable(true);
        loadMore.setText(R.string.load_more);
        return new CursorLoader(getActivity(), uri, ITEMS_PROJECTION, selection, null,
                "_id desc limit " + (page * COUNT));
    }

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onClick(View v) {
		loading = true;
		page++;
		// 异步加载更多数据
		new AsyncQueryHandler(getActivity().getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				mAdapter.changeCursor(cursor);
				loading = false;
				if (cursor.moveToLast()) {
                    // todo 这个按钮有bug
					// 如果到了最后一个，那么这个button就不能按了，
					// 这里，简单的已id值为最小的作为最后一个，实际上顺序不是这样的= =
					long l1 = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
					long l2 = ZhihuApp.getApp().getPreferences().getLong(BaseColumns._ID, 0);
					if (l1 == l2) {
						loadMore.setText(getString(R.string.no_more));
						loadMore.setClickable(false);
					}
				}
			}
		}.startQuery(0, null, Utils.parseUri(ITEMS), ITEMS_PROJECTION,
                null, null, "_id desc limit " + (page * COUNT));

	}


	/**
	 * 热门内容列表适配器。
	 */
	private static class AnswersAdaper extends CursorAdapter {

		public AnswersAdaper(Context context) {
			super(context, null, 0);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = LayoutInflater.from(context)
					.inflate(R.layout.question_row, null);

			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(android.R.id.title);
			holder.titleIndex = cursor.getColumnIndex(TITLE);
			holder.viewed = (TextView) view.findViewById(R.id.viewed);
			holder.viewedIndex = cursor.getColumnIndex(VIEWED);

			holder.nice = (ImageView) view.findViewById(R.id.nice);

			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.title.setText(cursor.getString(holder.titleIndex));
			holder.viewed.setText(cursor.getString(holder.viewedIndex));
		}

		private static class ViewHolder {
			TextView title;
			int titleIndex;

			ImageView nice;

			TextView viewed;
			int viewedIndex;
		}
	}

}
