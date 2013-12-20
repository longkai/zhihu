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
import android.util.Log;

import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.util.Constants;
import com.github.longkai.zhihu.util.Utils;

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

    /** 阅读条目列表 */
    private static final int ITEMS = 0;
    /** 单个阅读条目 */
    private static final int ITEM = 1;
    /** 话题列表 */
    private static final int TOPICS = 2;

    /** 清除缓存 */
    private static final int CLEAR = 10;

	static {
		matcher.addURI(AUTHORITY, Constants.ITEMS, ITEMS);
		matcher.addURI(AUTHORITY, Constants.ITEMS + "/#", ITEM);

		matcher.addURI(AUTHORITY, Constants.TOPICS, TOPICS);

        matcher.addURI(AUTHORITY, Constants.CLEAR_CACHE, CLEAR);
    }

	private ZhihuData mData;

	@Override
	public boolean onCreate() {
		mData = new ZhihuData(getContext());
		return true;
	}

    @Override
    public String getType(Uri uri) {
        String type;
        switch (matcher.match(uri)) {
            case ITEMS:
                type = MULTIPLE_RECORDS_MIME_TYPE + Constants.ITEMS;
                break;
            case ITEM:
                type = SINGLE_RECORD_MIME_TYPE + Constants.ITEM;
                break;
            case TOPICS:
                type = MULTIPLE_RECORDS_MIME_TYPE + Constants.TOPICS;
                break;
            case CLEAR:
                type = null;
                break;
            default:
                type = null;
        }
        return type;
    }

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mData.getReadableDatabase();
		Cursor cursor;
		switch (matcher.match(uri)) {
            case ITEMS:
                cursor = db.query(Constants.ITEMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM:
                selection = Utils.queryById(uri.getLastPathSegment());
                cursor = db.query(Constants.ITEMS, projection, selection, selectionArgs, null, null, null);
                break;
            case TOPICS:
                cursor = db.query(Constants.TOPICS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
				throw new RuntimeException("not found for the uri: " + uri);
		}

		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		String table;
		switch (matcher.match(uri)) {
            case ITEMS:
                table = Constants.ITEMS;
                break;
            case TOPICS:
                table = Constants.TOPICS;
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
		SQLiteDatabase db = mData.getWritableDatabase();
		switch (matcher.match(uri)) {
			case CLEAR:
				Utils.dropTables(db);
				Utils.createTables(db);
				break;
			default:
				throw new UnsupportedOperationException("UnsupportedOperationException");
		}
		getContext().getContentResolver().notifyChange(uri, null, false);
		return 0;
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
			Utils.createTables(db);
			Log.d(TAG, "end creating tables...");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "drop all tables...");
			Utils.dropTables(db);

			Log.d(TAG, "recreate tables...");
			onCreate(db);
		}
	}
}
