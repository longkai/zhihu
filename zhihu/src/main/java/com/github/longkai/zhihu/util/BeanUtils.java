/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;
import com.github.longkai.zhihu.bean.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 把json数据序列化为bean以及将bean转换为content values相关方法
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class BeanUtils {

	public static final String TAG = "BeanUtils";

	public static Voter toVoter(JSONArray jsonArray) {
		Voter voter = new Voter();
		try {
			voter.nick = jsonArray.getString(0);
			voter.id = jsonArray.getString(1);
		} catch (JSONException e) {
			Log.wtf(TAG, "error when deserializing voter!", e);
//			throw new RuntimeException("error when deserializing voter!", e);
		}

		return voter;
	}

	public static ContentValues toContentValues(Voter voter) {
		ContentValues values = new ContentValues(3);
		values.put(BaseColumns._ID, voter.id);
		values.put("nick", voter.nick);
		values.put("answer_id", voter.answer_id);
		return values;
	}

	public static Topic toTopic(JSONArray jsonArray) {
		Topic topic = new Topic();
		try {
			topic.long1 = jsonArray.getLong(0);
			topic.name = jsonArray.getString(1);
			topic.description = jsonArray.getString(2);
			topic.avatar = jsonArray.getString(3);
			topic.id = jsonArray.getLong(4);
		} catch (JSONException e) {
			Log.wtf(TAG, "error when deserializing topic!", e);
//			throw new RuntimeException("error when deserializing topic!", e);
		}
		return topic;
	}

	public static ContentValues toContentValues(Topic topic) {
		ContentValues values = new ContentValues(5);
		values.put(BaseColumns._ID, topic.id);
		values.put("long1", topic.long1);
		values.put("name", topic.name);
		values.put("description", topic.description);
		values.put("avatar", topic.avatar);
		return values;
	}

	public static Question toQuestion(JSONArray jsonArray) {
		Question question = new Question();
		try {
			question.long1 = jsonArray.getLong(0);
			question.title = jsonArray.getString(1);
			question.description = jsonArray.getString(2);
			question.id = jsonArray.getLong(3);
			question.starred = jsonArray.getInt(4);
			question.answered = jsonArray.getInt(5);
			question.viewed = jsonArray.getInt(6);

			// leave the topics filed alone
		} catch (JSONException e) {
			Log.wtf(TAG, "error when deserializing question!", e);
//			throw new RuntimeException("error when deserializing question!", e);
		}
		return question;
	}

	public static ContentValues toContentValues(Question question) {
		ContentValues values = new ContentValues(7);
		values.put(BaseColumns._ID, question.id);
		values.put("title", question.title);
		values.put("description", question.description);
		values.put("starred", question.starred);
		values.put("answered", question.answered);
		values.put("viewed", question.viewed);

		// use jsonarray string to store the topics belong to
		String topics = "[";
		for (int i = 0; i < question.topics.length; i++) {
			topics += question.topics[i].id;
			if (i != question.topics.length - 1) {
				topics += ",";
			}
		}
//		String topics = "";
//		for (int i = 0; i < question.topics.length; i++) {
//			topics += question.topics[i].name;
//			if (i != question.topics.length - 1) {
//				topics += "|";
//			}
//		}
		values.put("topics", topics + "]");
		values.put("topics", topics);

		return values;
	}

	public static User toUser(JSONArray jsonArray) {
		User user = new User();
		try {
			user.nick = jsonArray.getString(0);
			user.id = jsonArray.getString(1);
			user.avatar = jsonArray.getString(2);
			user.hash = jsonArray.getString(3);
		} catch (JSONException e) {
			Log.wtf(TAG, "error when deserializing user!", e);
//			throw new RuntimeException("error when deserializing user!", e);
		}
		return user;
	}

	public static ContentValues toContentValues(User user) {
		ContentValues values = new ContentValues(4);
		values.put(BaseColumns._ID, user.id);
		values.put("nick", user.nick);
		values.put("avatar", user.avatar);
		values.put("hash", user.hash);
		values.put("status", user.status);
		return values;
	}

	public static Answer toAnswer(JSONArray jsonArray) {
		Answer answer = new Answer();
		try {
			answer.long1 = jsonArray.getLong(0);
			answer.status = jsonArray.getString(1);
			answer.answer = jsonArray.getString(2);
			answer.vote = jsonArray.getInt(3);
			answer.last_alter_date = jsonArray.getLong(4) * 1000; // x1000
			answer.id = jsonArray.getLong(5);

			// leave question, user, voters[] alone!
		} catch (JSONException e) {
			Log.wtf(TAG, "error when deserializing answer!", e);
//			throw new RuntimeException("error when deserializing answer!", e);
		}
		return answer;
	}

	public static ContentValues toContentValues(Answer answer) {
		ContentValues values = new ContentValues(8);
		values.put(BaseColumns._ID, answer.id);
		values.put("long1", answer.long1);
		values.put("status", answer.status);
		values.put("answer", answer.answer);
		values.put("vote", answer.vote);
		values.put("last_alter_date", answer.last_alter_date);
		values.put("qid", answer.question.id);
		values.put("uid", answer.user.id);
		return values;
	}

	public static void persist(Context context, JSONArray data) {
		int length = data.length();
		Answer[] answers = new Answer[length];
		Question[] questions = new Question[length];

		// set, no duplication
		Set<Topic> topics = new HashSet<Topic>();

		User[] users = new User[length];

		Set<Voter> voters = new HashSet<Voter>();
		try {
			for (int i = 0; i < length; i++) {
				JSONArray entry = data.getJSONArray(i);

				answers[i] = toAnswer(entry);
				users[i] = toUser(entry.getJSONArray(6));
				users[i].status = answers[i].status;
				questions[i] = toQuestion(entry.getJSONArray(7));

				JSONArray topicArray = entry.getJSONArray(7).getJSONArray(7);
				Topic[] _topics = new Topic[topicArray.length()];
				for (int j = 0; j < topicArray.length(); j++) {
					_topics[j] = toTopic(topicArray.getJSONArray(j));
					topics.add(_topics[j]);
				}

				JSONArray voterArray = entry.getJSONArray(8);
				for (int j = 0; j < voterArray.length(); j++) {
					Voter voter = toVoter(voterArray.getJSONArray(j));
					voter.answer_id = answers[i].id;
					voters.add(voter);
				}

				// deal with relationships
				answers[i].user = users[i];
				answers[i].question = questions[i];

				questions[i].topics = _topics;
			}
		} catch (JSONException e) {
			Log.wtf(TAG, "json deserializing error!", e);
			throw new RuntimeException(e);
		}

		// now persist in to local databases = =
		ContentValues[] valueses = new ContentValues[answers.length];
		for (int i = 0; i < answers.length; i++) {
			valueses[i] = toContentValues(answers[i]);
		}
		context.getContentResolver().bulkInsert(Constants.parseUri(Constants.ANSWERS), valueses);

		for (int i = 0; i < questions.length; i++) {
			// duplicate will be replace
			valueses[i].clear();
			valueses[i] = toContentValues(questions[i]);
		}
		context.getContentResolver().bulkInsert(Constants.parseUri(Constants.QUESTIONS), valueses);

		for (int i = 0; i < users.length; i++) {
			valueses[i].clear();
			valueses[i] = toContentValues(users[i]);
		}
		context.getContentResolver().bulkInsert(Constants.parseUri(Constants.USERS), valueses);

		valueses = new ContentValues[topics.size()];
		int i = 0;
		for (Topic t : topics) {
			valueses[i] = toContentValues(t);
			i++;
		}
		context.getContentResolver().bulkInsert(Constants.parseUri(Constants.TOPICS), valueses);

		valueses = new ContentValues[voters.size()];
		Iterator<Voter> it = voters.iterator();
		i = 0;
		while (it.hasNext()) {
			Voter voter = it.next();
			valueses[i] = toContentValues(voter);
			i++;
		}
		context.getContentResolver().bulkInsert(Constants.parseUri(Constants.VOTERS), valueses);
	}

}
