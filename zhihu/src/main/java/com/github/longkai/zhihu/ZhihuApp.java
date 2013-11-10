/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class ZhihuApp extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String TAG = "ZhihuApp";

	private static ZhihuApp sApp;

	private SharedPreferences mPreferences;

	@Override
	public void onCreate() {
		super.onCreate();
		sApp = this;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public static ZhihuApp getApp() {
		return sApp;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "key: " + key + " changed!");
	}

	public SharedPreferences getPreferences() {
		return mPreferences;
	}
}
