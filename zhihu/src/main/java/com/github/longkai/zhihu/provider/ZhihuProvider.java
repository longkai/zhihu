/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.util.Constants;

/**
 * 知乎阅读数据源。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class ZhihuProvider extends ContentProvider {

	public static final String TAG = "ZhihuProvider";

	public static final String AUTHORITY = "com.github.longkai.zhihu.provider";
	public static final String BASE_URI = "content://" + AUTHORITY + "/";
	public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + ".";
	public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + ".";

	private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

	public static final int USERS = 0;
	public static final int USER = 1;
	public static final int ANSWERS = 2;
	public static final int ANSWER = 3;
	public static final int QUESTIONS = 4;
	public static final int QUESTION = 5;
	public static final int TOPICS = 6;
	public static final int TOPIC = 7;
	public static final int VOTERS = 8; // array, no single

	static {
		matcher.addURI(AUTHORITY, Constants.USERS, USERS);
		matcher.addURI(AUTHORITY, Constants.USERS + "/#", USER);

		matcher.addURI(AUTHORITY, Constants.ANSWERS, ANSWERS);
		matcher.addURI(AUTHORITY, Constants.ANSWERS + "/#", ANSWER);

		matcher.addURI(AUTHORITY, Constants.QUESTIONS, QUESTIONS);
		matcher.addURI(AUTHORITY, Constants.QUESTIONS + "/#", QUESTION);

		matcher.addURI(AUTHORITY, Constants.TOPICS, TOPICS);
		matcher.addURI(AUTHORITY, Constants.TOPICS + "/#", TOPIC);

		matcher.addURI(AUTHORITY, Constants.VOTERS, VOTERS);
	}

	private ZhihuData mData;

	@Override
	public boolean onCreate() {
		mData = new ZhihuData(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mData.getReadableDatabase();
		Cursor cursor;
		switch (matcher.match(uri)) {
			case USERS:
				cursor = db.query(Constants.USERS, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case USER:
				selection = BaseColumns._ID + "='" + uri.getLastPathSegment() + "'";
				cursor = db.query(Constants.USERS, projection, selection, null, null, null, null);
				break;
			case ANSWERS:
				cursor = db.query(Constants.ANSWERS, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case ANSWER:
				selection = BaseColumns._ID + "=" + uri.getLastPathSegment();
				cursor = db.query(Constants.ANSWERS, projection, selection, null, null, null, null);
				break;
			case QUESTIONS:
				cursor = db.query(Constants.QUESTIONS, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case QUESTION:
				selection = BaseColumns._ID + "=" + uri.getLastPathSegment();
				cursor = db.query(Constants.QUESTIONS, projection, selection, null, null, null, null);
				break;
			case TOPICS:
				cursor = db.query(Constants.TOPICS, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			case TOPIC:
				selection = BaseColumns._ID + "=" + uri.getLastPathSegment();
				cursor = db.query(Constants.TOPICS, projection, selection, null, null, null, null);
				break;
			case VOTERS:
				cursor = db.query(Constants.VOTERS, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			default:
				throw new RuntimeException("not found for the uri: " + uri);
		}

		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		String type;
		switch (matcher.match(uri)) {
			case USER:
				type = SINGLE_RECORD_MIME_TYPE + Constants.USERS;
				break;
			case USERS:
				type = MULTIPLE_RECORDS_MIME_TYPE + Constants.USERS;
				break;
			case ANSWER:
				type = SINGLE_RECORD_MIME_TYPE + Constants.ANSWERS;
				break;
			case ANSWERS:
				type = MULTIPLE_RECORDS_MIME_TYPE + Constants.ANSWERS;
				break;
			case QUESTION:
				type = SINGLE_RECORD_MIME_TYPE + Constants.QUESTIONS;
				break;
			case QUESTIONS:
				type = MULTIPLE_RECORDS_MIME_TYPE + Constants.QUESTIONS;
				break;
			case TOPIC:
				type = SINGLE_RECORD_MIME_TYPE + Constants.TOPICS;
				break;
			case TOPICS:
				type = MULTIPLE_RECORDS_MIME_TYPE + Constants.TOPICS;
				break;
			case VOTERS:
				type = MULTIPLE_RECORDS_MIME_TYPE + Constants.VOTERS;
				break;
			default:
				throw new RuntimeException("not found for the uri: " + uri);
		}
		return type;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		String table;
		switch (matcher.match(uri)) {
			case USERS:
				table = Constants.USERS;
				break;
			case ANSWERS:
				table = Constants.ANSWERS;
				break;
			case QUESTIONS:
				table = Constants.QUESTIONS;
				break;
			case TOPICS:
				table = Constants.TOPICS;
				break;
			case VOTERS:
				table = Constants.VOTERS;
				break;
			default:
				throw new RuntimeException("not found for the uri: " + uri);
		}
		SQLiteDatabase db = mData.getWritableDatabase();
		db.beginTransaction();

		for (int i = 0; i < values.length; i++) {
			db.insertWithOnConflict(table, null, values[i], SQLiteDatabase.CONFLICT_REPLACE); // replace the old one...
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		getContext().getContentResolver().notifyChange(uri, null, false); // no sync adapter here = =
		return values.length;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("UnsupportedOperationException");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("UnsupportedOperationException");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("UnsupportedOperationException");
	}

	private class ZhihuData extends SQLiteOpenHelper {

		public ZhihuData(Context context) {
			super(context, context.getString(R.string.db_name),
					null, context.getResources().getInteger(R.integer.db_version));
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "begin creating tables...");
			db.execSQL(getContext().getString(R.string.table_users));
			db.execSQL(getContext().getString(R.string.table_topics));
			db.execSQL(getContext().getString(R.string.table_voters));
			db.execSQL(getContext().getString(R.string.table_questions));
			db.execSQL(getContext().getString(R.string.table_answers));
			Log.d(TAG, "end creating tables...");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "drop all tables...");
			db.execSQL(getContext().getString(R.string.drop_all_table));

			Log.d(TAG, "recreate tables...");
			onCreate(db);
		}
	}
}
