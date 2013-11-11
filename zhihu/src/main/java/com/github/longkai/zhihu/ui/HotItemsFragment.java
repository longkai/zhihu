/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.ui;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
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
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.bean.Answer;
import com.github.longkai.zhihu.bean.Question;
import com.github.longkai.zhihu.bean.User;
import com.github.longkai.zhihu.util.Constants;

/**
 * 热门问答。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class HotItemsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private CursorAdapter mAdapter;

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
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, final long id) {
		// todo 这里有一个bug，假如有多个答案对应着同一个question id就跪了，看来按照java bean的关系做不太合适啊
		new AsyncQueryHandler(getActivity().getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//				mAdapter.swapCursor(cursor);
				if (cursor.moveToNext()) {
					Answer answer = new Answer();
					answer.last_alter_date = cursor.getLong(cursor.getColumnIndex("last_alter_date"));
					answer.vote = cursor.getInt(cursor.getColumnIndex("vote"));
					answer.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
					answer.status = cursor.getString(cursor.getColumnIndex("status"));
					answer.answer = cursor.getString(cursor.getColumnIndex("answer"));

					answer.question = new Question();
					answer.question.id = cursor.getLong(cursor.getColumnIndex("qid"));

					answer.user = new User();
					answer.user.id = cursor.getString(cursor.getColumnIndex("uid"));

					Intent intent = new Intent(getActivity(), AnswerActivity.class);
					intent.putExtra("answer", answer);
					getActivity().startActivity(intent);
				} else {
					Toast.makeText(getActivity(), getString(R.string.not_found), Toast.LENGTH_LONG).show();
				}
			}
		}.startQuery(0, null, Constants.parseUri(Constants.ANSWERS), null, "qid=" + id, null, null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Constants.parseUri(Constants.QUESTIONS);
		return new CursorLoader(getActivity(), uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	private static class QueryHandler extends AsyncQueryHandler {

		public QueryHandler(ContentResolver cr) {
			super(cr);
		}
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
			holder.topics = (TextView) view.findViewById(R.id.topics);
			holder.topicsIndex = cursor.getColumnIndex("topics");
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
			holder.topics.setText(cursor.getString(holder.topicsIndex));
			holder.nice.setImageResource(R.drawable.rating_not_important_light);
		}

		private static class ViewHolder {
			TextView title;
			int titleIndex;

			ImageView nice;

			TextView topics;
			int topicsIndex;

//			TextView desc;
//			int descIndex;
		}
	}

}
