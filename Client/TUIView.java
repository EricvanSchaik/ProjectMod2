package Client;

import java.util.*;
import java.io.*;
import java.net.*;

public class TUIView extends View implements Observer {
	
	private Client client;
	private Socket socket;
	
	public TUIView(Client client) {
		this.client = client;
		this.socket = client.getSocket();
	}
	
	public void handleTerminalInput() throws IOException {
		Scanner scanin = new Scanner(System.in);
    	String input;
    	while (!socket.isClosed()) {
			if (scanin.hasNextLine()) {
				input = scanin.nextLine();
				String[] strings = input.split(" ");
				if (strings[0].equals("hello")) {
					client.name = strings[1];
				}
				if (strings[0].equals("exit")) {
					socket.close();
				}
				if ((strings[0].equals("join") && !client.isInGame && (Integer.parseInt(strings[1]) < 5 && Integer.parseInt(strings[1]) > 1 )) || (strings[0].equals("hello") && !client.hasLoggedIn) || (strings[0].equals("place")) || (strings[0].equals("trade"))) {
					client.write(input);
				}
			}
		}
    	scanin.close();
    }

	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			System.out.println(arg);
			String[] strings = ((String) arg).split(" ");
			if (arg.equals("hello from the other side")) {
				client.hasLoggedIn = true;
			}
			
		}
	}
	
	
	
}
