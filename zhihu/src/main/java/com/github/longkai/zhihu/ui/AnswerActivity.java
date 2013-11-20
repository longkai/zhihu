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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.ZhihuApp;
import com.github.longkai.zhihu.util.Utils;

import static com.github.longkai.zhihu.util.Constants.ANSWER;
import static com.github.longkai.zhihu.util.Constants.ANSWER_ID;
import static com.github.longkai.zhihu.util.Constants.AVATAR;
import static com.github.longkai.zhihu.util.Constants.DESCRIPTION;
import static com.github.longkai.zhihu.util.Constants.ITEMS;
import static com.github.longkai.zhihu.util.Constants.LAST_ALTER_DATE;
import static com.github.longkai.zhihu.util.Constants.NICK;
import static com.github.longkai.zhihu.util.Constants.QUESTION_ID;
import static com.github.longkai.zhihu.util.Constants.STATUS;
import static com.github.longkai.zhihu.util.Constants.TITLE;
import static com.github.longkai.zhihu.util.Constants.UID;

/**
 * 显示内容界面。
 *
 * @User longkai
 * @Date 13-11-11
 * @Mail im.longkai@gmail.com
 */
public class AnswerActivity extends ActionBarActivity {

    /** 问题id */
    private long qid;
    /** 本答案id */
    private long id;
    /** 问题的标题，用于分享 */
    private String questionTitle;
    /** 答案摘要 */
    private String answerDigest;
    /** 答主id */
    private String uid;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer);

        final TextView title = (TextView) findViewById(android.R.id.title);
        final WebView desc = (WebView) findViewById(R.id.description);
        final TextView nick = (TextView) findViewById(R.id.nick);
        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
        final TextView status = (TextView) findViewById(R.id.status);
        final WebView answer = (WebView) findViewById(android.R.id.content);
        final TextView last_alter_date = (TextView) findViewById(R.id.last_alter_date);

        // 由于bean间的关系被弄复杂了，以至于现在还要再去抓一次 `问题`
        id = getIntent().getLongExtra(ANSWER_ID, 0);
        new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor.moveToNext()) {
                    // 问题相关
                    qid = cursor.getLong(cursor.getColumnIndex(QUESTION_ID));
                    questionTitle = cursor.getString(cursor.getColumnIndex(TITLE));
                    title.setText(questionTitle);
                    String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                    if (TextUtils.isEmpty(description)) {
                        desc.setVisibility(View.GONE);
                    } else {
                        desc.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
                        desc.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                        desc.setBackgroundColor(getResources().getColor(R.color.bgcolor));
                    }

                    // 答主相关
                    uid = cursor.getString(cursor.getColumnIndex(UID));
                    nick.setText(cursor.getString(cursor.getColumnIndex(NICK)));
                    String src = cursor.getString(cursor.getColumnIndex(AVATAR));
                    ZhihuApp.getImageLoader().get(src, ImageLoader.getImageListener(avatar,
                            R.drawable.ic_launcher, R.drawable.ic_launcher));
                    status.setText(cursor.getString(cursor.getColumnIndex(STATUS)));

                    // 答案相关
                    String content = cursor.getString(cursor.getColumnIndex(ANSWER));
                    answerDigest = content.length() > 50 ? content.substring(0, 50) : content;
                    answer.loadDataWithBaseURL(null, content,
                            "text/html", "utf-8", null);
                    answer.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                    answer.setBackgroundColor(getResources().getColor(R.color.bgcolor));

                    last_alter_date.setText(DateUtils.getRelativeTimeSpanString(
                            cursor.getLong(cursor.getColumnIndex(LAST_ALTER_DATE))));

                    cursor.close();
                }
            }
        }.startQuery(0, null, Utils.parseUri(ITEMS), null,
                Utils.queryByKey(ANSWER_ID, id), null, null);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
		provider.setShareIntent(Utils.share(this, questionTitle, answerDigest));
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.author: // 去web上查看用户信息
				Utils.viewUserInfo(this, uid);
				break;
			case R.id.view_at_web: // 去web上查看该答案
                Utils.viewOnWeb(this, Uri.parse("http://www.zhihu.com/question/" + qid + "/answer/" + id));
                break;
			case R.id.view_all:    // 在web上查看所有的答案
                Utils.viewOnWeb(this, Uri.parse("http://www.zhihu.com/question/" + id));
                break;
			default:
				throw new RuntimeException("no this option!");
		}
		return super.onOptionsItemSelected(item);
	}

}