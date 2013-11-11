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
import com.github.longkai.zhihu.ui.AnswerActivity;

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

	public static void createTables(Context context, SQLiteDatabase db) {
		db.execSQL(context.getString(R.string.table_users));
		db.execSQL(context.getString(R.string.table_topics));
		db.execSQL(context.getString(R.string.table_voters));
		db.execSQL(context.getString(R.string.table_questions));
		db.execSQL(context.getString(R.string.table_answers));
	}

	public static void dropTables(Context context, SQLiteDatabase db) {
		db.execSQL(context.getString(R.string.drop_all_table));
	}

}
