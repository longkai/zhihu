/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.github.longkai.zhihu.R;
import com.github.longkai.zhihu.provider.ZhihuProvider;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.longkai.zhihu.util.Constants.ANSWER;
import static com.github.longkai.zhihu.util.Constants.ANSWERED;
import static com.github.longkai.zhihu.util.Constants.ANSWER_ID;
import static com.github.longkai.zhihu.util.Constants.AVATAR;
import static com.github.longkai.zhihu.util.Constants.DESCRIPTION;
import static com.github.longkai.zhihu.util.Constants.ITEMS;
import static com.github.longkai.zhihu.util.Constants.LAST_ALTER_DATE;
import static com.github.longkai.zhihu.util.Constants.NICK;
import static com.github.longkai.zhihu.util.Constants.QUESTION_ID;
import static com.github.longkai.zhihu.util.Constants.STARRED;
import static com.github.longkai.zhihu.util.Constants.STATUS;
import static com.github.longkai.zhihu.util.Constants.TITLE;
import static com.github.longkai.zhihu.util.Constants.TOPICS;
import static com.github.longkai.zhihu.util.Constants.TOPIC_AVATAR;
import static com.github.longkai.zhihu.util.Constants.TOPIC_DESCRIPTION;
import static com.github.longkai.zhihu.util.Constants.TOPIC_ID;
import static com.github.longkai.zhihu.util.Constants.TOPIC_NAME;
import static com.github.longkai.zhihu.util.Constants.UID;
import static com.github.longkai.zhihu.util.Constants.VIEWED;
import static com.github.longkai.zhihu.util.Constants.VOTE;
import static com.github.longkai.zhihu.util.Constants.VOTERS;

/**
 * 一些工具类
 *
 * @User longkai
 * @Date 13-11-11
 * @Mail im.longkai@gmail.com
 */
public class Utils {

    public static final String TAG = "Utils";

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

    /**
     * 刷新的web数据接口
     */
    public static String refreshUrl() {
        return url(1);
    }

    /**
     * sql模糊查询
     * @param str
     * @return '%str%'
     */
    public static String like(String str) {
        return "'%" + str + "'%";
    }

	public static void createTables(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        // 阅读项目数据表
        // _id 仅做为本地的一个标识与列表顺序比较的时间戳，实际的主键还是答案的id
        sql.append("CREATE TABLE ").append(ITEMS).append("(")
                .append(BaseColumns._ID).append(" int,")
                .append(QUESTION_ID).append(" int NOT NULL,")
                .append(TITLE).append(" text NOT NULL,")
                .append(DESCRIPTION).append(" text,")
                .append(STARRED).append(" int,")
                .append(ANSWERED).append(" int,")
                .append(VIEWED).append(" int,")
                .append(TOPICS).append(" text,")

                .append(ANSWER_ID).append(" int NOT NULL,")
                .append(ANSWER).append(" text,")
                .append(VOTE).append(" int PRIMARY KEY,")
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
    public static String queryById(String id) {
        return queryByKey(BaseColumns._ID, id);
    }

    /**
     * 通过key=value来查询，字符串会加上单引号
     * @param key
     * @param value
     * @return key=value
     */
    public static String queryByKey(String key, Object value) {
        if (value instanceof String) {
            return key + "='" + value + "'";
        }
        return key + "=" + value;
    }

    /**
     * 处理从网络得到的数据并将其转换为ContentValues数据
     * @param jsonArray data
     * @return items和topics的content values map
     */
    public static Map<String, ContentValues[]> process(JSONArray jsonArray) {
        ContentValues[] items = new ContentValues[jsonArray.length()];
        List<ContentValues> topics = new ArrayList<ContentValues>();
        ContentValues item;
        ContentValues topic;
        // 临时变量
        JSONArray array;
        JSONArray user;
        JSONArray question;
        JSONArray _topics;
        JSONArray _topic;
        // 存储某个项目所属于的话题的id值，依照这样的格式(末尾有个多余的逗号，
        // 不去也恰好方便查询用= =)：123,321,456,
        StringBuilder topicIds = new StringBuilder();
        // 用当前时间戳来作为item在本地的标识与排序，降序，时间戳越大代表越新
        long millis = System.currentTimeMillis();

        for (int i = 0; i < items.length; i++) {
            array = jsonArray.optJSONArray(i);
            item = new ContentValues();

            item.put(BaseColumns._ID, millis--);

            // 答案相关
            item.put(STATUS, array.optString(1));
            item.put(ANSWER, array.optString(2));
            item.put(VOTE, array.optInt(3));
            item.put(LAST_ALTER_DATE, array.optLong(4) * 1000);
            item.put(ANSWER_ID, array.optLong(5));

            // 答主相关
            user = array.optJSONArray(6);
            if (user != null) {
                item.put(NICK, user.optString(0));
                item.put(UID, user.optString(1));
                item.put(AVATAR, user.optString(2));
            }

            // 问题相关
            question = array.optJSONArray(7);
            if (question != null) {
                item.put(TITLE, question.optString(1, null));
                item.put(DESCRIPTION, question.optString(2));
                item.put(QUESTION_ID, question.optLong(3));
                item.put(STARRED , question.optLong(5));
                item.put(VIEWED, question.optLong(6));
            }

            // 话题相关
            topicIds.setLength(0);
            if (!question.isNull(7)) {
                _topics = question.optJSONArray(7);
                for (int j = 0; j < _topics.length(); j++) {
                    _topic = _topics.optJSONArray(j);
                    topic = new ContentValues(4);
                    topic.put(TOPIC_NAME, _topic.optString(1, null));
                    topic.put(TOPIC_DESCRIPTION, _topic.optString(2));
                    topic.put(TOPIC_AVATAR, _topic.optString(3));
                    topic.put(TOPIC_ID, _topic.optLong(4));
                    // todo 可能会有重复，但是存进数据库的话会replace主键相同的值，
                    // 考虑要不要在这里去掉重复的话题再插入数据库
                    topics.add(topic);

                    // 处理本item所属的话题
                    topicIds.append(_topic.optLong(4)).append(",");
                }
            }
            item.put(TOPICS, topicIds.toString());

            // 赞同用户们，直接存储json字符串
            item.put(VOTERS, array.isNull(8)
                    ? null : array.optJSONArray(8).toString());

            items[i] = item;
        }

        Map<String, ContentValues[]> map = new HashMap<String, ContentValues[]>(2);
        map.put(ITEMS, items);
        map.put(TOPICS, topics.toArray(new ContentValues[0]));
        return map;
    }

}
