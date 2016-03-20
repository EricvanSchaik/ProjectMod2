package Client;

import java.util.*;
import java.io.*;

public abstract class View implements Observer {
	
	public abstract void handleTerminalInput() throws IOException ;
	
	public abstract void update(Observable o, Object arg);
	
	public void writeToView(String message) {
		System.out.println(message);
	}
}
