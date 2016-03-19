package Server;

import Game.*;

public interface Player {
	
	public String getName();
	
	public void addSteen(Steen steen);
	
	public void write(String message);
	
	public String stenenToString();
	
	public void reset();
	
}
