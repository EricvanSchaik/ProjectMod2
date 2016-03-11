package Client;

import java.util.*;
import java.io.*;

public class TUIView extends View implements Observer {
	
	private Client client;
	
	public TUIView(Client client) {
		this.client = client;
	}

	public void handleTerminalInput() throws IOException {
		Scanner scanin = new Scanner(System.in);
    	String input;
    	while (!client.getSocket().isClosed()) {
			if (scanin.hasNextLine()) {
				if ((input = scanin.nextLine()).equals("exit")) {
					client.getSocket().close();
				}
				else {
					 
				}
			}
		}
    	scanin.close();
    }

	public void update(Observable o, Object arg) {
		
		
	}
	
}
