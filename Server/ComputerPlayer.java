package Server;

import java.util.ArrayList;
import java.util.List;

import Game.Game;
import Game.Steen;

public class ComputerPlayer implements Player {
	
	protected String name;
	private Server server;
	private boolean connected = false;
	public Game game;
	private boolean joined;
	private List<Steen> stenen;
	private String[] move;
	private boolean movesucceed = false;
	
	public ComputerPlayer(Game game, String name) {
		this.game = game;
		this.stenen = new ArrayList<Steen>();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	
	public void addSteen(Steen steen) {
		stenen.add(steen);
	}
	
	public void write(String message) {
		
	}

	
	public String stenenToString() {
		String stenenToString = "";
		for (Steen s: stenen) {
			stenenToString = s.toString() + ", " + stenenToString;
		}
		return stenenToString;
	}

	
	public void reset() {
		stenen = new ArrayList<Steen>();
	}

}
