/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.ui;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.ImageLoader;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.ZhihuApp;
import com.github.longkai.zhihu.bean.Answer;
import com.github.longkai.zhihu.util.Utils;

import static com.github.longkai.zhihu.util.Constants.*;

/**
 * 显示内容界面。
 *
 * @User longkai
 * @Date 13-11-11
 * @Mail im.longkai@gmail.com
 */
public class AnswerActivity extends ActionBarActivity {

	private Answer answer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.github.longkai.android.R.layout.fragment_container);

		AnswerFragment fragment = new AnswerFragment();
		answer = getIntent().getExtras().getParcelable("answer");
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
				.add(com.github.longkai.android.R.id.fragment_container, fragment)
				.commit();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		completeData();
	}

	private void completeData() {
		new AsyncQueryHandler(getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				if (cursor.moveToNext()) {
					answer.question.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
					answer.question.title = cursor.getString(cursor.getColumnIndex("title"));
					answer.question.description = cursor.getString(cursor.getColumnIndex("description"));
				}
			}
		}.startQuery(0, null, parseUri(QUESTIONS), null,
				"_id=" + answer.question.id, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.answer, menu);
		MenuItem share = menu.findItem(R.id.share);
		ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
		provider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
			@Override
			public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
				startActivity(intent);
				return true;
			}
		});
		provider.setShareIntent(Utils.share(this, answer.question.title,
				answer.answer.length() > 50 ? answer.answer.substring(0, 50) : answer.answer));
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.author:
				new AsyncQueryHandler(getContentResolver()) {
					@Override
					protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
						if (cursor.moveToNext()) {
							Utils.viewUserInfo(AnswerActivity.this, cursor.getString(0));
						} else {
							Toast.makeText(AnswerActivity.this, R.string.not_found, Toast.LENGTH_SHORT).show();
						}
					}
				}.startQuery(0, null, parseUri(USERS), new String[]{BaseColumns._ID},
						"_id='" + answer.user.id + "'", null, null);
				break;
			case R.id.view_at_web:
				Utils.viewOnWeb(this, Uri.parse("http://www.zhihu.com/question/"
						+ answer.question.id + "/answer/" + answer.id));
				break;
			case R.id.view_all:
				Utils.viewOnWeb(this, Uri.parse("http://www.zhihu.com/question/"
						+ answer.question.id));
				break;
			default:
				throw new RuntimeException("no this option!");
		}
		return super.onOptionsItemSelected(item);
	}

	public static class AnswerFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.answer, container, false);
			final Answer answer = getArguments().getParcelable("answer");

			final TextView title = (TextView) view.findViewById(android.R.id.title);
			final WebView desc = (WebView) view.findViewById(R.id.description);
			new AsyncQueryHandler(getActivity().getContentResolver()) {
				@Override
				protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
					if (cursor.moveToNext()) {
						title.setText(cursor.getString(cursor.getColumnIndex("title")));
						desc.loadDataWithBaseURL(null, cursor.getString(cursor.getColumnIndex("description")),
								"text/html", "utf-8", null);
						desc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
						desc.setBackgroundColor(getResources().getColor(R.color.bgcolor));
					} else {
						desc.setVisibility(View.GONE);
					}
				}
			}.startQuery(0, null, parseUri(QUESTIONS), null,
					"_id=" + answer.question.id, null, null);

			final TextView nick = (TextView) view.findViewById(R.id.nick);
			final ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
			final TextView status = (TextView) view.findViewById(R.id.status);
			new AsyncQueryHandler(getActivity().getContentResolver()) {
				@Override
				protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
					if (cursor.moveToNext()) {
//						nick.setText(cursor.getString(cursor.getColumnIndex("nick")) + "\t"
//								+ cursor.getString(cursor.getColumnIndex("status")));
						nick.setText(cursor.getString(cursor.getColumnIndex("nick")));
						String src = cursor.getString(cursor.getColumnIndex("avatar"));
						ZhihuApp.getImageLoader().get(src, ImageLoader.getImageListener(avatar,
								R.drawable.ic_launcher, R.drawable.ic_launcher));
						status.setText(cursor.getString(cursor.getColumnIndex("status")));
					}
				}
			}.startQuery(0, null, parseUri(USERS),
					null, "_id='" + answer.user.id + "'", null, null);

			WebView content = (WebView) view.findViewById(android.R.id.content);
			content.loadDataWithBaseURL(null, answer.answer, "text/html", "utf-8", null);
			content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
			content.setBackgroundColor(getResources().getColor(R.color.bgcolor));

			TextView last_alter_date = (TextView) view.findViewById(R.id.last_alter_date);
			last_alter_date.setText(DateUtils.getRelativeTimeSpanString(answer.last_alter_date));

			return view;
		}
	}

}