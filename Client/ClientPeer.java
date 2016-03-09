package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import Server.*;
import Game.*;

public class ClientPeer extends Thread {
	
    protected String name;
    protected Socket sock;
    protected BufferedReader in;
    protected BufferedWriter out;
    private ServerPeer serverpeer;
    
    public ClientPeer(Socket sockArg, ServerPeer serverpeer) throws IOException {
    	this.sock = sockArg;
    	in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
    	this.serverpeer = serverpeer;
    }
    
    public void run() {
    	
    }
    
    
    /*public void run() {
    	String input = null;
    	while (serverpeer.isRunning) {
    		try {
    			input = in.readLine();
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	try {
    		sock.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }*/
    
    
    /*public void handleTerminalInput() {
    	Scanner scanin = new Scanner(System.in);
    	try {
    		while (!sock.isClosed()) {
    			if (scanin.hasNextLine()) {
    				if ((input = scanin.nextLine()).equals("EXIT")) {
    					shutDown();
    				}
    				else {
    					out.write(input + "\n");
    					out.flush();
    				}
    			}
    		}
    	}
    	catch (IOException e) {
    		if (e instanceof SocketException) {
    			System.out.println("Peer has been closed, closing socket now...");
    			shutDown();
    			System.out.println("Socket has been closed");
    		}
    		else {
    			e.printStackTrace();
    		}
    	}
    	finally {
			scanin.close();
		}
    }*/
        
    static public String readString(String tekst) {
        System.out.print(tekst);
        String antw = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            antw = in.readLine();
        } catch (IOException e) {
        }
        return (antw == null) ? "" : antw;
    }
}	
	