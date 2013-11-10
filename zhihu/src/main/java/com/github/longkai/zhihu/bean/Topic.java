/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 话题。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Topic implements Parcelable {

	/** todo 咱不知为何= = */
	public long long1;

	/** 名字 */
	public String name;

	/** 介绍 */
	public String description;

	/** 封面 */
	public String avatar;

	/** id */
	public long id;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(long1);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(avatar);
		dest.writeLong(id);
	}

	public static final Creator<Topic> CREATOR = new Creator<Topic>() {
		@Override
		public Topic createFromParcel(Parcel source) {
			Topic topic = new Topic();
			topic.long1 = source.readLong();
			topic.name = source.readString();
			topic.description = source.readString();
			topic.avatar = source.readString();
			topic.id = source.readLong();
			return topic;
		}

		@Override
		public Topic[] newArray(int size) {
			return new Topic[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Topic)) return false;

		Topic topic = (Topic) o;

		if (id != topic.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
