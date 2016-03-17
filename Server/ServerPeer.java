package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;
import Game.*;

public class ServerPeer implements Runnable {

	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	private Server server;
	private boolean connected = false;
	public Game game;
	private String[] commands = { "join", "hello", "place", "trade" };
	private List<String> commandslist = Arrays.asList(commands);
	private boolean joined;
	private List<Steen> stenen;
	private String[] move;
	private boolean movesucceed = false;

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
				String[] command = input.split(" ", 2);
				if (commandslist.contains(command[0])) {
					System.out.println("Executing command " + command[0]);
					executeCommand(command[0], command[1]);
				} else {
					write("error 0, invalid command");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			sock.close();
			connected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void executeCommand(String command, String specs) {
		if (command.equals("hello")) {
			if (connected) {
				write("error 0");
			} else if (!isValidName(specs)) {
				write("error 2");
			} else {
				write("hello from the other side");
				setName(specs);
				connected = true;
			}
		} else if (command.equals("join")) {
			if (joined || !(Integer.parseInt(specs) > 1 && Integer.parseInt(specs) < 5)) {
				write("error 0");
			} else {
				join(Integer.parseInt(specs));
			}
		} else if (command.equals("place") || command.equals("trade")) {
			if (!game.isRunning() || !this.equals(game.getCurrentPlayer())) {
				write("error 0, not your turn");
			} else {
				System.out.println("Current player determining its move");
				determineMove(command, specs);
			}
		}

	}

	public void join(int gamesize) {
		boolean exists = false;
		Game posgame = null;
		if (server.waitingGames() != null) {
			for (Map.Entry<Game, List<ServerPeer>> e : server.waitingGames().entrySet()) {
				if (e.getKey().gameSize() == gamesize) {
					exists = true;
					posgame = e.getKey();
				}
			}
		}
		if (exists) {
			write("Joining existing lobby");
			posgame.addSpeler(this);
			setGame(posgame);
			write("joinlobby: " + posgame.spelersToString());
			posgame.sendAllPlayers("joinlobby " + name);
			joined = true;
			if (game.isRunning()) {
				server.waitingGames().remove(game);
				server.runningGames().put(game, game.getSpelers());
			}
		} if (!exists) {
			write("Making new lobby, waiting for players...");
			List<ServerPeer> newlist = new ArrayList<ServerPeer>();
			newlist.add(this);
			Game newGame = new Game(newlist, gamesize, this.server);
			setGame(newGame);
			server.waitingGames().put(newGame, newlist);
			joined = true;
		}
	}

	public void shutDown() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String message) {
		try {
			System.out.println("Sending " + message);
			out.write(message + "\n");
			out.flush();
		} catch (IOException e) {
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
		if (commandslist.contains(name) || name.contains(" ") || server.getServerPeers().contains(name)) {
			return false;
		} else {
			return true;
		}
	}

	public void addSteen(Steen steen) {
		stenen.add(steen);
	}

	public void removeSteen(Steen steen) {
		stenen.remove(steen);
	}

	public synchronized void determineMove(String command, String specs) {
		move = new String[2];
		move[0] = command;
		move[1] = specs;
		System.out.println("Making move...");
		makeMove();
		if (movesucceed) {
			System.out.println("Current player has made its move");
			notify();
		}
		movesucceed = false;
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
		for (Steen s : stenen) {
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

	public void makeMove() {
		if (game.getCurrentPlayer().equals(this)) {
			if (move[0].equals("place")) {
				Map<Steen, int[]> placingmap = new HashMap<Steen, int[]>();
				String[] steenenplaatsen = move[1].split(" ");
				if ((steenenplaatsen.length % 2) != 0) {
					write("error 0, invalid move");
				} else {
					for (int i = 0; i < steenenplaatsen.length; i = i + 2) {
						String[] steen = steenenplaatsen[i].split(",");
						if (!(steen.length == 2)) {
							write("error 0, invalid stone");
						} else {
							String[] plaatss = steenenplaatsen[i + 1].split(",");
							int[] plaatsi = new int[2];
							plaatsi[0] = Integer.parseInt(plaatss[0]);
							plaatsi[1] = Integer.parseInt(plaatss[1]);
							try {
								placingmap.put(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])),
										plaatsi);
								stenen.remove(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])));
								stenen.add(game.getSteen());
							} catch (InvalidArgumentException e) {
								write("error 0, stone not in your possession");
							}
						}
					}
					boolean isplaced = game.place(placingmap);
					if (isplaced) {
						movesucceed = true;
					}
				}
			} else if (move[0].equals("trade")) {
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
			}
			if (stenen.isEmpty()) {
				game.noStonesLeft(this);
			}
		} else {
			write("error 0, checking if it goes wrong here");
		}
	}
	
	public String stenenToString() {
		String stenenToString = "";
		for (Steen s: stenen) {
			stenenToString = s.toString() + ", " + stenenToString;
		}
		return stenenToString;
	}
	
	
}
