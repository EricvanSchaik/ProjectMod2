package Client;

import java.util.List;

import Game.Game;
import Game.Steen;

public class ComputerPlayer  {
	
	private String name;
	private Game game;
	private int punten;
	private List<Steen> stenen;
	
	public ComputerPlayer(String name, Game game) {
		this.name = name;
		this.game = game;
	}
	
	public void makeMove() {
		
		
	}

	
	public void addSteen(Steen steen) {
		stenen.add(steen);
	}
	
	public void setGame(Game game) {
		
		
	}
	
	public void reset() {
		
		
	}
	
	public String getName() {
		
		return null;
	}

	
	
}
