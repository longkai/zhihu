/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.Toast;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.bean.Answer;
import com.github.longkai.zhihu.bean.Question;
import com.github.longkai.zhihu.bean.User;
import com.github.longkai.zhihu.provider.ZhihuProvider;
import com.github.longkai.zhihu.ui.AnswerActivity;

import static com.github.longkai.zhihu.util.Constants.*;
import static com.github.longkai.zhihu.util.Constants.TOPICS;
import static com.github.longkai.zhihu.util.Constants.VOTERS;

/**
 * 一些工具类
 *
 * @User longkai
 * @Date 13-11-11
 * @Mail im.longkai@gmail.com
 */
public class Utils {

	/**
	 * view user' s infomation on the web.
	 *
	 * @param context
	 * @param id
	 */
	public static void viewUserInfo(Context context, String id) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.zhihu.com/people/" + id));
		context.startActivity(intent);
	}

    /**
     * 分享某个阅读条目
     * @param context
     * @param subject 主题
     * @param content 内容
     * @return intent
     */
	public static Intent share(Context context, String subject, String content) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, subject);
		share.putExtra(Intent.EXTRA_TEXT, content + context.getString(R.string.share_from));
		return share;
	}

	public static void viewOnWeb(Context context, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		context.startActivity(intent);
	}

	public static void viewAnswer(Context context, Cursor cursor) {
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

			Intent intent = new Intent(context, AnswerActivity.class);
			intent.putExtra("answer", answer);
			context.startActivity(intent);
		} else {
			Toast.makeText(context, context.getString(R.string.not_found), Toast.LENGTH_LONG).show();
		}
	}

    /**
     * 传递查询路径，解析其URI
     * @param path
     * @return URI
     */
    public static Uri parseUri(String path) {
        return Uri.parse(ZhihuProvider.BASE_URI + path);
    }

    /**
     * page == 1 表示第一页，最新的那页
     *
     * @param page
     * @return
     */
    public static String url(int page) {
        return "http://www.zhihu.com/reader/json/"
                + page + "?r=" + System.currentTimeMillis();
    }

	public static void createTables(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        // 阅读项目数据表
        sql.append("CREATE TABLE ").append(ITEMS).append("(")
                .append(BaseColumns._ID).append(" int PRIMARY KEY,")
                .append(QUESTION_ID).append(" int NOT NULL,")
                .append(TITLE).append(" text NOT NULL")
                .append(DESCRIPTION).append(" text,")
                .append(STARRED).append(" int,")
                .append(ANSWERED).append(" int,")
                .append(VIEWED).append(" int,")
                .append(TOPICS).append(" text,")

                .append(ANSWER_ID).append(" int,")
                .append(ANSWER).append(" text,")
                .append(VOTE).append(" int,")
                .append(LAST_ALTER_DATE).append(" int,")
                .append(VOTERS).append(" text,")

                .append(UID).append(" text,")
                .append(NICK).append(" text,")
                .append(STATUS).append(" text,")
                .append(AVATAR).append(" text")
            .append(")");

        db.execSQL(sql.toString());

        // 重置string builder
        sql.setLength(0);

        // 话题数据表
        sql.append("CREATE TABLE ").append(TOPICS).append("(")
                .append(TOPIC_ID).append(" int PRIMARY KEY,")
                .append(TOPIC_NAME).append(" text,")
                .append(TOPIC_DESCRIPTION).append(" text,")
                .append(TOPIC_AVATAR).append(" text")
            .append(")");

        db.execSQL(sql.toString());
	}

	public static void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TOPICS);
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS);
    }

    /**
     * 通过Android默认的主键`_id`来查询数据
     * @param id
     * @return _id=xx
     */
    public static final String queryById(String id) {
        return BaseColumns._ID + "=" + id;
    }

}
