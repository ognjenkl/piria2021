package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comment implements Serializable{

	private static final long serialVersionUID = 7781376783475892576L;

	int usersId;
	int booksId;
	String comment;
	Timestamp commentDate;
	
	public int getUsersId() {
		return usersId;
	}
	public void setUsersId(int usersId) {
		this.usersId = usersId;
	}
	public int getBooksId() {
		return booksId;
	}
	public void setBooksId(int booksId) {
		this.booksId = booksId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Timestamp getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Timestamp commentDate) {
		this.commentDate = commentDate;
	}

	



}
