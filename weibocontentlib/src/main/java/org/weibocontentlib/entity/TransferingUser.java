package org.weibocontentlib.entity;

public class TransferingUser {

	private int id;
	private byte[] cookies;
	private int transferingIndex;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getCookies() {
		return cookies;
	}

	public void setCookies(byte[] cookies) {
		this.cookies = cookies;
	}

	public int getTransferingIndex() {
		return transferingIndex;
	}

	public void setTransferingIndex(int transferingIndex) {
		this.transferingIndex = transferingIndex;
	}

}
