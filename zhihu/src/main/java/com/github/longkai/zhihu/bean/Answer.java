/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 回答。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Answer implements Parcelable {

	/** todo 暂不明白是干啥的 */
	public long long1;

	/** 回答者的状态签名 */
	public String status;

	/** 回答内容 */
	public String answer;

	/** 答案获得投票数 */
	public int vote;

	/** 答案最后更新时间戳(需要*1000) */
	public long last_alter_date;

	/** answer id */
	public long id;

	/** 对应的提问 */
	public Question question;

	/** 回答者 */
	public User user;

//	/** 投票者 */
//	public Voter[] voters;


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(long1);
		dest.writeString(status);
		dest.writeString(answer);
		dest.writeInt(vote);
		dest.writeLong(last_alter_date);
		dest.writeLong(id);

//		dest.writeParcelable(question, flags);
//		dest.writeParcelable(user, flags);
		dest.writeLong(question.id);
		dest.writeString(user.id);
		// write array' s length first
//		dest.writeInt(voters.length);
//		dest.writeTypedArray(voters, flags);
	}

	public static final Creator<Answer> CREATOR = new Creator<Answer>() {
		@Override
		public Answer createFromParcel(Parcel source) {
			Answer answer = new Answer();
			answer.long1 = source.readLong();
			answer.status = source.readString();
			answer.answer = source.readString();
			answer.vote = source.readInt();
			answer.last_alter_date = source.readLong();
			answer.id = source.readLong();

//			answer.question = source.readParcelable(null);
//			answer.user = source.readParcelable(null);
			answer.question = new Question();
			answer.question.id = source.readLong();

			answer.user = new User();
			answer.user.id = source.readString();
			// read the array' s length first
//			int length = source.readInt();
//			answer.voters = Voter.CREATOR.newArray(length);
//			source.readTypedArray(answer.voters, Voter.CREATOR);
			return answer;
		}

		@Override
		public Answer[] newArray(int size) {
			return new Answer[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Answer)) return false;

		Answer answer = (Answer) o;

		if (id != answer.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
