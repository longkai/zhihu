/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 来自xx的赞同。
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class Voter implements Parcelable {

	public String nick;

	public String id;

	public long answer_id;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(nick);
		dest.writeString(id);
		dest.writeLong(answer_id);
	}

	public static final Creator<Voter> CREATOR = new Creator<Voter>() {
		@Override
		public Voter createFromParcel(Parcel source) {
			Voter voter = new Voter();
			voter.nick = source.readString();
			voter.id = source.readString();
			voter.answer_id = source.readLong();
			return voter;
		}

		@Override
		public Voter[] newArray(int size) {
			return new Voter[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Voter)) return false;

		Voter voter = (Voter) o;

		if (id != null ? !id.equals(voter.id) : voter.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
