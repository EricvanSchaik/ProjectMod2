package Server;

import Game.*;
import java.util.*;

public interface Player {
	
	public String getName();
	
	public void addSteen(Steen steen);
	
	public void write(String message);
	
	public String stenenToString();
	
	public List<Steen> getStenen();
	
	public void makeMove();
	
	public void reset();
	
}
