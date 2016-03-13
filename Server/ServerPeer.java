package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;
import Game.*;

public class ServerPeer extends Observable implements Runnable {
    
	protected String name;
    protected Socket sock;
    protected BufferedReader in;
    protected BufferedWriter out;
    private Server server;
    private boolean connected = false;
    public Game game;
    private String[] commands = {"join", "hello", "place", "trade"};
    private List<String> commandslist = Arrays.asList(commands);
    private boolean joined;
    private List<Steen> stenen;
    private String[] move;
    
    public ServerPeer(Socket sockArg, Server server) throws IOException {
    	this.sock = sockArg;
    	in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    	out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
    	this.server = server;
    	this.stenen = new ArrayList<Steen>();
    }
    
    public void run() {
    	String input = null;
    	while (server.isRunning) {
    		try {
    			input = in.readLine();
    			System.out.println("Client sent " + input);
    			String[] command = input.split(" ",2);
    			if (commandslist.contains(command[0])) {
    				System.out.println("Executing command " + command[0]);
    				executeCommand(command[0],command[1]);
    			}
    			else {
    				write("error 0");
    			}
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	try {
    		sock.close();
    		connected = false;
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void executeCommand(String command, String specs) {
    	if (command.equals("hello")) {
			if (connected) {
				write("error 0");
			}
			else if (!isValidName(specs)) {
				write("error 2");
			}
			else {
				write("hello from the other side");
				write("joinlobby " + game.spelersToString());
				game.sendAllPlayers("joinlobby " + specs);
				setName(specs);
				connected = true;
			}
		}
    	else if (command.equals("join")) {
    		if (joined || !(Integer.parseInt(specs) > 1 && Integer.parseInt(specs) < 5)) {
    			write("error 0");
    		}
    		else {
    			join(Integer.parseInt(specs));
    		}
    	}
    	else if (command.equals("place") || command.equals("trade")) {
    		if (!game.isRunning || !this.equals(game.getCurrentPlayer())) {
    			write("error 0");
    		}
    		else {
    			determineMove(command, specs);
    		}
    	}
    	
    }
    
    public void join(int gamesize) {
    	boolean exists = false;
    	for (Map.Entry<Game, List<ServerPeer>> e: server.waiting.entrySet()) {
    		if (e.getKey().gameSize() == gamesize) {
    			exists = true;
    			Game game = e.getKey();
    		}
    	}
    	if (exists) {
    		game.addSpeler(this);
    		setGame(game);
    		joined = true;
    		if (game.isRunning) {
    			server.waiting.remove(game);
    			server.running.put(game, game.getSpelers());
    		}
    	}
    	else {
    		List<ServerPeer> newlist = new ArrayList<ServerPeer>();
    		newlist.add(this);
    		Game newGame = new Game(newlist, gamesize, this.server);
    		server.waiting.put(newGame, newlist);
    		joined = true;
    	}
    }
    
    public void shutDown() {
    	try {
    		sock.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void write(String message) {
    	try {
    		System.out.println("Sending " + message);
    		out.write(message + "\n");
    		out.flush();
    	}
    	catch (IOException e) {
    		System.out.println("error 3");
    		server.sendAllClients(getName() + "disconnected");
    		connected = false;
    	}
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    
    public boolean isValidName(String name) {
    	if (commandslist.contains(name) || name.contains(" ")) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
	public void addSteen(Steen steen) {
		stenen.add(steen);
	}
	
	public void removeSteen(Steen steen) {
		stenen.remove(steen);
	}
	
	public void determineMove(String command, String specs) {
		move = new String[2];
		move[0] = command;
		move[1] = specs;
		notify();
	}
	
	public void setGame(Game game) {
		this.game = game;
		
	}
	
	public void reset() {
		stenen = new ArrayList<Steen>();
	}
	
	public Steen getSteen(int vorm, int kleur) throws InvalidArgumentException {
		Steen compare = null;
		Steen result = null;
		boolean exist = false;
		compare = new Steen(vorm, kleur);
		for (Steen s: stenen) {
			if (s.equals(compare)) {
				result = s;
				exist = true;
			}
		}
		if (!exist) {
			throw new InvalidArgumentException();
		}
		return result;
	}
	
	public boolean makeMove() {
		boolean movemade = false;
		if (game.getCurrentPlayer().equals(this)) {
			if (move[0].equals("place")) {
				Map<Steen, int[]> placingmap = new HashMap<Steen, int[]>();
				String[] steenenplaats =  move[1].split(" ");
				if ((steenenplaats.length % 2) == 0) {
					write("error 0");
				}
				else {
					for (int i = 0; i < steenenplaats.length; i = i + 2) {
						String[] steen = steenenplaats[i].split(",");
						if (!(steen.length == 2)) {
							write("error 0");
						}
						else {
							String[] plaatss = steenenplaats[i+1].split(",");
							int[] plaatsi = new int[2];
							plaatsi[0] = Integer.parseInt(plaatss[0]);
							plaatsi[1] = Integer.parseInt(plaatss[1]);
							try {
								placingmap.put(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])), plaatsi);
								stenen.remove(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])));
								stenen.add(game.getSteen());
							}
							catch (InvalidArgumentException e) {
								write("error 0");
							}
						}
					}
					movemade = game.place(placingmap);
					
				}
			}
			else if (move[0].equals("trade")) {
				List<Steen> tstenen = new ArrayList<Steen>();
				String[] sstenen = move[1].split(" ");
				for (int i = 0; i < sstenen.length; i++) {
					String[] ssteen = sstenen[i].split(",");
					try {
						tstenen.add(getSteen(Integer.parseInt(ssteen[0]), Integer.parseInt(ssteen[1])));
						stenen.remove(getSteen(Integer.parseInt(ssteen[0]), Integer.parseInt(ssteen[1])));
					}
					catch (InvalidArgumentException e) {
						write("error 0");
					}
					
				}
				stenen.addAll(game.tradeStenen(tstenen));
				movemade = true;
			}
			if (stenen.isEmpty()) {
				game.noStonesLeft(this);
			}
		}
		else {
			write("error 0");
		}
		return movemade;
	}
}	
	