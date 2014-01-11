package it.unibo.cs.swarch.sercamobile;

public class Table {
	
	private String username;
	private int playersallowed;
	private int watchers;
	
	
	
	public Table (String nome, int usercontable) {
		super();
		this.username = nome;
		this.playersallowed = usercontable;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getPlayersAllowed() {
		return playersallowed;
	}
	
	public void setPlayersAllowed(int playersallowed) {
		this.playersallowed = playersallowed;
	}
	
	public int getWatchers() {
		return watchers;
	}
	
	public void setWatchers(int watchers) {
		this.watchers = watchers;
	}
}
