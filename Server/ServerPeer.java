package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;
import Game.*;
	
public class ServerPeer implements Runnable, Player {
	
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	private Server server;
	private boolean connected = false;
	public Game game;
	private String[] commands = { "join", "hello", "place", "trade" };
	private List<String> commandslist = Arrays.asList(commands);
	public boolean joined;
	private List<Steen> stenen;
	private String[] move;
	private boolean movesucceed = false;
	
	//--------------------Constructor--------------------
	
	/**
	 * Constructs a new ServerPeer, bound to a socket 
	 * @param sockArg
	 * @param server
	 */
	public ServerPeer(Socket sockArg, Server server) {
		this.sock = sockArg;
		this.server = server;
		this.stenen = new ArrayList<Steen>();
	}
	
	//--------------------Queries--------------------
	
	/**
	 * This will return the name of the player/client this serverpeer is attached to.
	 * @return the name of the player as a String.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * A method to determine if a given String can be used as name for the player.
	 * @param name, the String the client proposes as name.
	 * @return true if the String is a valid name, false if not.
	 */
	public boolean isValidName(String name) {
		if (commandslist.contains(name) || name.contains(" ") || server.getServerPeers().contains(name)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This is a complementary class to make it possible within this class to retrieve a tile given a color and shape.
	 * @param vorm, the shape of the tile.
	 * @param kleur, the color of the tile.
	 * @return a reference to the tile of type Steen of this player, with the given color and shape.
	 * @throws InvalidArgumentException, to be thrown if such a tile is not in the possession of the player.
	 */
	private Steen getSteen(int vorm, int kleur) throws InvalidArgumentException {
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
	
	/**
	 * Gives a String representation of the tiles in possession of the player.
	 * @return a String describing all the tiles, separated by commas.
	 */
	public String stenenToString() {
		String stenenToString = "";
		for (Steen s: stenen) {
			stenenToString = s.toString() + ", " + stenenToString;
		}
		return stenenToString;
	}
	
	/**
	 * Returns the list of tiles this player has.
	 * @return the list of tiles this player has.
	 */
	public List<Steen> getStenen() {
		return stenen;
	}
	//--------------------Commands--------------------
	
	/**
	 * The method to be called when this object is created, so that all the input the serverpeer gets from the client can be processed in a concurrent thread.
	 */
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * This method is to be called when a client writes a valid command, and needs to be processed.
	 * @param command, the command that needs to be processed.
	 * @param specs, the specifications belonging to the command, specifying the tiles if the command is trading for instance.
	 */
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
			if (joined || !(Integer.parseInt(specs) > 1 && Integer.parseInt(specs) < 5) || (!connected)) {
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
	
	/**
	 * This method is to be called when a player wants to join a game.
	 * @param gamesize, the amount of players needed to play the game. Needs to be between 2 and 4.
	 */
	public void join(int gamesize) {
		boolean exists = false;
		Game posgame = null;
		if (server.waitingGames() != null) {
			for (Map.Entry<Game, List<Player>> e : server.waitingGames().entrySet()) {
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
			List<Player> newlist = new ArrayList<Player>();
			newlist.add(this);
			Game newGame = new Game(newlist, gamesize);
			setGame(newGame);
			server.waitingGames().put(newGame, newlist);
			joined = true;
		}
	}

	/**
	 * To be called when the client is disconnected.
	 */
	public void shutDown() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will make sure all the messages that need to go to the client are being sent.
	 */
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
	
	/**
	 * This method will determine the name of the player when it first connects with the server.
	 * @param name, the new name of this player.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method adds a tile to the collection of tiles of this player.
	 */
	public void addSteen(Steen steen) {
		stenen.add(steen);
	}
	
	/**
	 * This method will remove a tile from the collection of tiles of the player, for instance if he decides to trade the tile or places it on the board.
	 * @param steen, the tile to be removed.
	 */
	public void removeSteen(Steen steen) {
		stenen.remove(steen);
	}
	
	/**
	 * This method is to be called when a player is in the game, it is its turn and it has written its move to the serverpeer.
	 * @param command, either place or trade.
	 * @param specs, specifying the tiles and possibly the coordinates.
	 */
	public synchronized void determineMove(String command, String specs) {
		move = new String[2];
		move[0] = command;
		move[1] = specs;
		makeMove();
		if (movesucceed) {
			System.out.println("Current player has made its move");
			notify();
		}
		movesucceed = false;
	}
	
	/**
	 * Bounds this player to a game.
	 * @param game, the game this player needs to be bound to.
	 */
	public void setGame(Game game) {
		this.game = game;

	}
	
	/**
	 * This will reset the collection of tiles of the player.
	 */
	public void reset() {
		stenen = new ArrayList<Steen>();
	}

	/**
	 * This method tries to carry out the move the client wants to make.
	 */
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
							System.out.println("Trying to place the tiles...");
							String[] plaatss = steenenplaatsen[i + 1].split(",");
							int[] plaatsi = new int[2];
							plaatsi[0] = Integer.parseInt(plaatss[0]);
							plaatsi[1] = Integer.parseInt(plaatss[1]);
							try {
								placingmap.put(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])),
										plaatsi);
								stenen.remove(getSteen(Integer.parseInt(steen[0]), Integer.parseInt(steen[1])));
								stenen.add(game.takeSteen());
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
						movesucceed = true;
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
	
}
