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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(nick);
		dest.writeString(id);
	}

	public static final Creator<Voter> CREATOR = new Creator<Voter>() {
		@Override
		public Voter createFromParcel(Parcel source) {
			Voter voter = new Voter();
			voter.nick = source.readString();
			voter.id = source.readString();
			return voter;
		}

		@Override
		public Voter[] newArray(int size) {
			return new Voter[size];
		}
	};
}
