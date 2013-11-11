/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.ui;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SearchViewCompat;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.util.Constants;
import com.github.longkai.zhihu.util.Utils;

/**
 * 热门问答。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class HotItemsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private CursorAdapter mAdapter;

	private String keywords;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new AnswersAdaper(getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText(getString(R.string.empty_list));
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		setHasOptionsMenu(true); // search
	}

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
		// todo 这里有一个bug，假如有多个答案对应着同一个question id就跪了，看来按照java bean的关系做不太合适啊
		new AsyncQueryHandler(getActivity().getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//				mAdapter.swapCursor(cursor);
				Utils.viewAnswer(getActivity(), cursor);
			}
		}.startQuery(0, null, Constants.parseUri(Constants.ANSWERS), null, "qid=" + id, null, null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Constants.parseUri(Constants.QUESTIONS);
		String seletion = null;
		if (!TextUtils.isEmpty(keywords)) {
			String like = "'%" + keywords + "%'";
			seletion = "title like " + like
					+ " or description like " + like;
		}
		return new CursorLoader(getActivity(), uri, null, seletion, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
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
			holder.titleIndex = cursor.getColumnIndex("title");
			holder.viewed = (TextView) view.findViewById(R.id.viewed);
			holder.viewedIndex = cursor.getColumnIndex("viewed");
//			holder.desc = (TextView) view.findViewById(R.id.desc);
//			holder.descIndex = cursor.getColumnIndex("description");

			holder.nice = (ImageView) view.findViewById(R.id.nice);

			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.title.setText(cursor.getString(holder.titleIndex));
//			String desc = cursor.getString(holder.descIndex);
//			holder.desc.setText(desc.length() > 30 ? desc.substring(0, 30) : desc);
			holder.viewed.setText(cursor.getString(holder.viewedIndex));
//			holder.nice.setImageResource(R.drawable.rating_not_important_light);
		}

		private static class ViewHolder {
			TextView title;
			int titleIndex;

			ImageView nice;

			TextView viewed;
			int viewedIndex;

//			TextView desc;
//			int descIndex;
		}
	}

}
