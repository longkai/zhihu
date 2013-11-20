/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.provider.BaseColumns;

/**
 * 常量值
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Constants {

    public static final String DATA = "data";

    /** 清除缓存 */
    public static final String CLEAR_CACHE = "clear_cache";

    /** 阅读条目们 */
    public static final String ITEMS = "items";

    /** 一个条目 */
    public static final String ITEM = "item";

    // 问题相关
    /** id */
    public static final String QUESTION_ID = "question_id";
    /** 标题 */
    public static final String TITLE = "title";
    /** 描述 */
    public static final String DESCRIPTION = "description";
    /** 关注次数 */
    public static final String STARRED = "starred";
    /** 回答次数 */
    public static final String ANSWERED = "answered";
    /** 浏览次数 */
    public static final String VIEWED = "viewed";
    /** 所属话题 */
    public static final String TOPICS = "topics";

    // 回答相关
    /** id */
    public static final String ANSWER_ID = "answer_id";
    /** 回答内容 */
    public static final String ANSWER = "answer";
    /** 答案获得投票数 */
    public static final String VOTE = "vote";
    /** 答案最后更新时间戳(需要*1000) */
    public static final String LAST_ALTER_DATE = "last_alter_date";
    /** 赞同此回答的用户列表 */
    public static final String VOTERS = "voters";

    // 回答者相关
    /** id */
    public static final String UID = "uid";
    /** 昵称 */
    public static final String NICK = "nick";
    /** 个性描述 */
    public static final String STATUS = "status";
    /** 头像 */
    public static final String AVATAR = "avatar";

    // 话题相关，这里需要用另一个表
    /** id */
    public static final String TOPIC_ID = BaseColumns._ID;
    /** 名字 */
    public static final String TOPIC_NAME = "name";
    /** 介绍 */
    public static final String TOPIC_DESCRIPTION = "description";
    /** 封面 */
    public static final String TOPIC_AVATAR = "avatar";

    /** 使用android默认的_id标识降序 */
    public static final String DESC_ORDER = BaseColumns._ID + " DESC";

    /** 回答者的查询列 */
    public static final String[] USER_PROJECTION = {BaseColumns._ID, UID, NICK, STATUS, AVATAR};

    /** 问题的查询列 */
    public static final String[] ITEMS_PROJECTION = {BaseColumns._ID, TITLE, ANSWER_ID, VIEWED};

}
