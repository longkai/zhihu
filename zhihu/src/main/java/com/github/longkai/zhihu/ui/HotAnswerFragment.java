/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.longkai.zhihu.util.Constants;

/**
 * 热门问答。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class HotAnswerFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private CursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new AnswersAdaper(getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Constants.parseUri(Constants.QUESTIONS);
		return new CursorLoader(getActivity(), uri, new String[]{BaseColumns._ID, "title"}, null, null, null);
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
					.inflate(android.R.layout.simple_list_item_1, null);

			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(android.R.id.text1);
			holder.titleIndex = cursor.getColumnIndex("title");
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.title.setText(cursor.getString(holder.titleIndex));
		}

		private static class ViewHolder {
			TextView title;
			int titleIndex;
		}
	}

}
