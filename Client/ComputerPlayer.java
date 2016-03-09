package Client;

import java.util.List;

import Game.Game;
import Game.Steen;

public class ComputerPlayer implements Player {
	
	private String name;
	private Game game;
	private Strategy strategy;
	private int punten;
	private List<Steen> stenen;
	
	public ComputerPlayer(String name, Game game, Strategy strategy) {
		this.name = name;
		this.game = game;
		this.strategy = strategy;
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
