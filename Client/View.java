package Client;

import java.util.*;
import java.io.*;

public abstract class View implements Observer {
	
	public abstract void handleTerminalInput() throws IOException ;
	
	public abstract void update(Observable o, Object arg);
	
}
