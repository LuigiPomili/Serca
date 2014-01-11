package it.unibo.cs.swarch.sercamobile;

public class User {
	
	private String username;
	private int score;
	private String status;
	
	public User (String username, int score, String status) {
		super();
		this.username = username;
		this.score = score;
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
