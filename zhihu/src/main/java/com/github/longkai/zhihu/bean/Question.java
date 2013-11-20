/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 问题。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Question implements Parcelable {

	/** todo 暂不知道是为何= = */
	public long long1;

	/** 标题 */
	public String title;

	/** 描述 */
	public String description;

	/** id */
	public long id;

	/** 关注次数 */
	public int starred;

	/** 回答次数 */
	public int answered;

	/** 浏览次数 */
	public int viewed;

	/** 所属话题 */
	public Topic[] topics;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(long1);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeLong(id);
		dest.writeInt(starred);
		dest.writeInt(answered);
		dest.writeInt(viewed);

		// write array' s length first
		dest.writeInt(topics.length);
		dest.writeTypedArray(topics, flags);
	}

	public static final Creator<Question> CREATOR = new Creator<Question>() {
		@Override
		public Question createFromParcel(Parcel source) {
			Question question = new Question();
			question.long1 = source.readLong();
			question.title = source.readString();
			question.description = source.readString();
			question.id = source.readLong();
			question.starred = source.readInt();
			question.answered = source.readInt();
			question.viewed = source.readInt();

			// read the array' s length first
			int length = source.readInt();
			question.topics = Topic.CREATOR.newArray(length);
			source.readTypedArray(question.topics, Topic.CREATOR);
			return question;
		}

		@Override
		public Question[] newArray(int size) {
			return new Question[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Question)) return false;

		Question question = (Question) o;

		if (id != question.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
