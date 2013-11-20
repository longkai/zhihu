/*
 * The MIT License (MIT)
 * Copyright (c) 2013 longkai(龙凯)
 * The software shall be used for good, not evil.
 */
package com.github.longkai.zhihu.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户（非常少的资料= =）
 *
 * @User longkai
 * @Date 13-11-10
 * @Mail im.longkai@gmail.com
 */
public class User implements Parcelable {

	/** 用户昵称 */
	public String nick;

	/** 状态 */
	public String status;

	/** 用户id */
	public String id;

	/** 用户头像 */
	public String avatar;

	/** todo 感觉应该是用户hash的用户id */
	public String hash;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(nick);
		dest.writeString(status);
		dest.writeString(id);
		dest.writeString(avatar);
		dest.writeString(hash);
	}

	public static final Creator<User> CREATOR=new Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			User user = new User();
			user.id = source.readString();
			user.status = source.readString();
			user.avatar = source.readString();
			user.hash = source.readString();
			return user;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;

		User user = (User) o;

		if (id != null ? !id.equals(user.id) : user.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
