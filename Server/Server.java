package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Game.*;

import java.util.*;

public class Server extends Thread {
	
    private static final String USAGE
        = "usage: " + Server.class.getName() + "<port>";
    protected ServerSocket servsock;
    public boolean isRunning;
    private List<Player> serverpeers;
    private Map<Game, List<Player>> waiting = new HashMap<Game, List<Player>>();
    private Map<Game, List<Player>> running = new HashMap<Game, List<Player>>();
    
    public static void main(String[] args) {
    	if (args.length != 1) {
    		System.out.println(USAGE);
    		System.exit(0);
    	}
    	ServerSocket servsock = null;
    	try {
    		servsock = new ServerSocket(Integer.parseInt(args[0]));
    		Server server = new Server(servsock);
    		server.run();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public Server (ServerSocket servsock) {
    	this.servsock = servsock;
    	serverpeers = new ArrayList<Player>();
    	isRunning = true;
    }
    
    public void run() {
    	while (isRunning) {
    		try {
    			Socket peersock = servsock.accept();
    			System.out.println("Client connected");
    			ServerPeer serverpeer = new ServerPeer(peersock,this);
    			serverpeers.add(serverpeer);
    			Thread serverpeerthread = new Thread(serverpeer);
    			serverpeerthread.start();
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		if (!isRunning) {
    			shutDown();
    		}
    	}
    }
    
    public void sendAllClients(String message) {
    	for (Player sp: serverpeers) {
    		sp.write(message);
    	}
    }
    
    public Map<Game, List<Player>> waitingGames() {
    	return waiting;
    }
    
    public Map<Game, List<Player>> runningGames() {
    	return running;
    }
    
    public void shutDown() {
    	try {
    		servsock.close();
    		isRunning = false;
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public List<Player> getServerPeers() {
    	return serverpeers;
    }
    
    
}
