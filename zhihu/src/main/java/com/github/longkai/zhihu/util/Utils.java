/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.github.longkai.zhihu.R;

/**
 * Created with IntelliJ IDEA.
 *
 * @User longkai
 * @Date 13-11-11
 * @Mail im.longkai@gmail.com
 */
public class Utils {

	/**
	 * view user' s infomation on the web.
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

}
