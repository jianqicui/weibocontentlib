package org.weibocontentlib.entity;

public class Status {

	private int id;
	private String statusText;
	private String statusPictureFile;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getStatusPictureFile() {
		return statusPictureFile;
	}

	public void setStatusPictureFile(String statusPictureFile) {
		this.statusPictureFile = statusPictureFile;
	}

}
