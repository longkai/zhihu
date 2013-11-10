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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.github.longkai.zhihu.util.BitmapLruCache;

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

	private static RequestQueue sQueue;
	private static ImageLoader sLoader;

	private SharedPreferences mPreferences;

	@Override
	public void onCreate() {
		super.onCreate();
		sApp = this;
		sQueue = Volley.newRequestQueue(this);
		// todo make the cache' s size available
		sLoader = new ImageLoader(sQueue, new BitmapLruCache(100)); // 100 cache entries
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public static ZhihuApp getApp() {
		return sApp;
	}

	public static RequestQueue getRequestQueue() {
		return sQueue;
	}

	public static ImageLoader getImageLoader() {
		return sLoader;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "key: " + key + " changed!");
	}

	public SharedPreferences getPreferences() {
		return mPreferences;
	}
}
