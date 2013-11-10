/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.net.Uri;
import com.github.longkai.zhihu.provider.ZhihuProvider;

/**
 * Created with IntelliJ IDEA.
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Constants {

	public static final String USERS = "users";
	public static final String ANSWERS = "answers";
	public static final String QUESTIONS = "questions";
	public static final String TOPICS = "topics";
	public static final String VOTERS = "voters";
	public static final String QUESTION_TOPICS = "question_topics";

	public static Uri parseUri(String path) {
		return Uri.parse(ZhihuProvider.BASE_URI + path);
	}

	public static String url(int page) {
		return "http://www.zhihu.com/reader/json/" + page + "?r=" + System.currentTimeMillis();
	}

}
