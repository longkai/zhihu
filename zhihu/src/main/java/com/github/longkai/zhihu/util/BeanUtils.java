/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.util;

import android.content.Context;
import android.widget.Toast;
import com.github.longkai.zhihu.bean.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
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
			throw new RuntimeException("error when deserializing voter!", e);
		}

		return voter;
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
			throw new RuntimeException("error when deserializing topic!", e);
		}
		return topic;
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
			throw new RuntimeException("error when deserializing question!", e);
		}
		return question;
	}

	public static User toUser(JSONArray jsonArray) {
		User user = new User();
		try {
			user.nick = jsonArray.getString(0);
			user.id = jsonArray.getString(1);
			user.avatar = jsonArray.getString(2);
			user.hash = jsonArray.getString(3);
		} catch (JSONException e) {
			throw new RuntimeException("error when deserializing user!", e);
		}
		return user;
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
			throw new RuntimeException("error when deserializing answer!", e);
		}
		return answer;
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
				questions[i] = toQuestion(entry.getJSONArray(7));

				JSONArray topicArray = entry.getJSONArray(7).getJSONArray(7);
				Topic[] _topics = new Topic[topicArray.length()];
				for (int j = 0; j < topicArray.length(); j++) {
					_topics[j] = toTopic(topicArray.getJSONArray(j));
					topics.add(_topics[j]);
				}

				JSONArray voterArray = entry.getJSONArray(8);
				for (int j = 0; j < voterArray.length(); j++) {
					voters.add(toVoter(voterArray.getJSONArray(j)));
				}

				// deal with relationships
				answers[i].user = users[i];
				answers[i].question = questions[i];

				questions[i].topics = _topics;
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < questions.length; i++) {
			sb.append(i + 1).append(": ").append(questions[i].title).append("\n");
		}
		Toast.makeText(context, sb, Toast.LENGTH_LONG).show();
	}

}
