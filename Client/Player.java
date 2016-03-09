package Client;

import java.util.*;

import Game.Game;
import Game.Steen;

public interface Player {
	
	public void addSteen(Steen steen);
	
	public void makeMove();
	
	public void setGame(Game game);
	
	public void reset();
	
	public String getName();
	
}
