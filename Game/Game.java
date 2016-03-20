package Game;

import java.util.*;

import Server.*;

/**
 * Overall model class to represent the Qwirkle game, consisting of players, a bag of cubes, a board and a scoreboard.
 * @author Eric van Schaik and Birte Brunt.
 *
 */
public class Game extends Thread {
	
	private List<Steen> zak = null;
	private List<Player> spelers;
	private Player currentPlayer = null;
	private Board board = null;
	private Map<Player, Integer> scoreboard = null;
	private int gamesize;
	private boolean isRunning = false;
	private boolean eindeSpel = false;
	
	
	//--------------------Constructor--------------------
	
	
	//@ requires spelers.size() >= 1 && spelers.size() <= 4;
	/**
	 * Constructs a new game with existing players, but makes a new board, a new bag with cubes and a new scoreboard (by calling reset()).
	 * @param spelers: the players participating in the game.
	 */
	public Game(List<Player> spelers, int gamesize) {
		this.gamesize = gamesize;
		this.spelers = spelers;
		currentPlayer = spelers.get(0);
		this.board = new Board();
		this.zak = new ArrayList<Steen>(108);
		for (int i=0; i<3; i++) {
			for (int i2=0; i2<6; i2++) {
				for (int i3=0; i3<6; i3++) {
					Steen steen = null;
					try {
						steen = new Steen(i2,i3);
					}
					catch (InvalidArgumentException e) {
						
					}
					zak.add(steen);
				}
			}
		}
		reset();
	}
	
	//--------------------Queries--------------------
	
	/**
	 * Gives the amount of players needed to start the game.
	 * @return amount of players to participate.
	 */
	public int gameSize() {
		return gamesize;
	}
	
	/**
	 * Returns a list of players participating.
	 * @return the list of players participating.
	 */
	public List<Player> getSpelers() {
		return spelers;
	}
	
	/**
	 * Returns the list of players participating in String-form.
	 * @return a list of players participating in String-form.
	 */
	public String spelersToString() {
		String spelersToString = " ";
		for (Player s: spelers) {
			spelersToString = s.getName() + ", " + spelersToString;
		}
		
		return spelersToString;
	}

	/**
	 * Returns the player whose turn it is.
	 * @return the player who can make a move.
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Gives whether the bag of tiles is empty.
	 * @return true if there are no tiles left in the bag, false if there are.
	 */
	public boolean legeZak() {
		return zak.isEmpty();
	}
	
	/**
	 * Gives whether this game is running.
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Returns a String-representation of the scoreboard.
	 * @return the scoreboard in String-form.
	 */
	public String scoreboardToString() {
		String scoreboardToString = "scoreboard: ";
		for (Map.Entry<Player, Integer> e: scoreboard.entrySet()) {
			scoreboardToString = scoreboardToString + e.getKey().getName() + "->" + e.getValue().intValue() + ", ";
		}
		return scoreboardToString;
	}
	
	/**
	 * Determines the points to be added to the current player when the method place is being called upon.
	 * @param nieuwestenen: the map of cubes and points where they need to be placed.
	 * @return the points to be added to the current player.
	 */
	private int calculatePoints(Map<Steen, int[]> nieuwestenen) {
		int score = 0;
		boolean stop = false;
		List<Integer> ybonus = new ArrayList<Integer>();
		List<Integer> xbonus = new ArrayList<Integer>();
		List<Integer> y = new ArrayList<Integer>();
		List<Integer> x = new ArrayList<Integer>();
		Set<Map.Entry<Steen, int[]>> entryset = nieuwestenen.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset){
			int[] vakje = e.getValue();
			if (!board.isEmpty(vakje[0], (vakje[1]+1)) || !board.isEmpty(vakje[0], (vakje[1]-1)) || !board.isEmpty((vakje[0]+1), vakje[1]) || !board.isEmpty((vakje[0]-1), vakje[1])){
				score = score +2;
			}
			else {score = score +1;}
			if (!x.contains(vakje[0])){
			while (!stop){
				for (int i=1; i<7; i++){
					if (!board.isEmpty(vakje[0], vakje[1]+i)&&!nieuwestenen.containsKey(vakje[1]+i)){
						score = score +1;
					}
					if (board.isEmpty(vakje[0], vakje[1]+i)){
						stop = true;
					}
				}
			}
			stop = false;
			while (!stop){
				for (int i=1; i<7; i++){
					if (!board.isEmpty(vakje[0], vakje[1]-i)&&!nieuwestenen.containsKey(vakje[1]-i)){
						score = score +1;
					}
					if (board.isEmpty(vakje[0], vakje[1]-i)){
						stop = true;
					}
				}
			}
			x.add(vakje[0]);
			}
			stop = false;
			if (!y.contains(vakje[1])){
			while (!stop){
				for (int i=1; i<7; i++){
					if (!board.isEmpty(vakje[0]+i, vakje[1])&&!nieuwestenen.containsKey(vakje[0]+i)){
						score = score +1;
					}
					if (board.isEmpty(vakje[0]+i, vakje[1])){
						stop = true;
					}
				}
			}
			stop = false;
			while (!stop){
				for (int i=1; i<7; i++){
					if (!board.isEmpty(vakje[0]-i, vakje[1])&&!nieuwestenen.containsKey(vakje[0]-i)){
						score = score +1;
					}
					if (board.isEmpty(vakje[0]-i, vakje[1])){
						stop = true;
					}
				}
			}
			y.add(vakje[1]);
			}
			if (!ybonus.contains(vakje[1])){
				for (int i = -5; i<1 ; i++){
					if (!board.isEmpty(vakje[0]+i, vakje[1]) && !board.isEmpty(vakje[0]+i+1, vakje[1]) && !board.isEmpty(vakje[0]+i+2, vakje[1]) && !board.isEmpty(vakje[0]+i+3, vakje[1]) && !board.isEmpty(vakje[0]+i+4, vakje[1])&&!board.isEmpty(vakje[0]+i+5, vakje[1])){
						score = score + 6;
						ybonus.add(vakje[1]);
					}
				}
			}
			if (!xbonus.contains(vakje[0])){
				for (int i = -5; i<1; i++){
					if (!board.isEmpty(vakje[0], vakje[1]+i) && !board.isEmpty(vakje[0], vakje[1]+i+1) && !board.isEmpty(vakje[0], vakje[1]+i+2) && !board.isEmpty(vakje[0], vakje[1]+i+3) && !board.isEmpty(vakje[0], vakje[1]+i+4) && !board.isEmpty(vakje[0], vakje[1]+i+5)){
						score = score + 6;
						xbonus.add(vakje[0]);
					}
				}	
			}
		}
		return score;
	}
	
	//--------------------Commands--------------------
	
	/**
	 * Returns a Steen from the bag of cubes, and removes it from the bag.
	 * @return a Steen from the bag of cubes.
	 */
	public Steen takeSteen() {
		Steen steen = null;
		if (!zak.isEmpty()){
			steen = zak.get((int)(Math.random()*zak.size()));
			zak.remove(steen);
		}
		return steen;
	}

	/**
	 * Starts the game if being called. Gets called by the last player to join. First gives the players random cubes, then keeps giving turns, until one of the players is out of cubes.
	 */
	public synchronized void run() {
		reset();
		for (Player p: spelers) {
			for (int i = 0; i < 6; i++) {
				Steen s = takeSteen();
				p.addSteen(s);
			}
		}
		currentPlayer = spelers.get(0);
		while (!eindeSpel) {
			sendAllPlayers("turn " + currentPlayer.getName());
			sendAllPlayers(board.toString());
			sendAllPlayers(scoreboardToString());
			sendStenenToAll();
			synchronized(currentPlayer) {
				try {
					currentPlayer.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			currentPlayer = spelers.get((spelers.indexOf(currentPlayer) + 1)%spelers.size());
		}
		endGameMessage();
		try {
			join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends a message to all players participating in this game.
	 * @param message: the message to be send.
	 */
	public void sendAllPlayers(String message) {
		for (Player s: spelers) {
			s.write(message);
		}
	}

	/**
	 * Adds a player to the list of players of this game. Gets called if a ServerPeer calls join.
	 * @param speler: the player who wants to join.
	 */
	public void addSpeler(Player speler) {
		spelers.add(speler);
		if (spelers.size() == gamesize) {
			isRunning = true;
			start();
		}
	}
	
	/**
	 * Places a list of tiles with given coordinates on the board.
	 * @param steentjes, the tiles to be placed (with coordinates).
	 * @return true if all the tiles are successfully placed, false if not.
	 */
	public boolean place(Map<Steen, int[]> steentjes) {
		boolean placed = board.place(steentjes);
		if (!placed) {
			currentPlayer.write("error 0, couldn't place the tiles");
		}
		else {
			Integer oldScore = scoreboard.get(currentPlayer);
			Integer newScore = new Integer(oldScore.intValue()+calculatePoints(steentjes));
			scoreboard.put(currentPlayer, newScore);
		}
		return placed;
	}

	/**
	 * The method that is to be called by a player when he has no stones left.
	 * @param speler: the player that calls the method.
	 */
	public void noStonesLeft(ServerPeer speler) {
		Integer oldScore = scoreboard.get(speler);
		Integer newScore = new Integer(oldScore.intValue()+6);
		scoreboard.put(currentPlayer, newScore);
		eindeSpel = true;
	}
	

	/**
	 * The trade method as alternative for the players to the place method. 
	 * @param stenen: List of values of type Steen, to be placed in the bag.
	 * @return List of values of type Steen, to be given back to the player.
	 */
	public List<Steen> tradeStenen(List<Steen> stenen) {
		zak.addAll(stenen);
		int amount = stenen.size();
		List<Steen> teruggave = new ArrayList<Steen>();
		Random random = new Random();
		for (int i = 0; i < amount; i++) {
			Steen newsteen = zak.get(random.nextInt(amount));
			teruggave.add(newsteen);
			zak.remove(newsteen);
		}
		
		
		return teruggave;
	}
	
	/**
	 * When the game ends, this method is being called to inform the players.
	 */
	private void endGameMessage() {
		Set<Map.Entry<Player, Integer>> entryset = scoreboard.entrySet();
		Map.Entry<Player, Integer> highest = null;
		for (Map.Entry<Player, Integer> score: entryset) {
			if (highest.getValue() < score.getValue()) {
				highest = score;
			}
		}
		Player winner = highest.getKey();
		if (winner instanceof ServerPeer) {
			((ServerPeer) winner).write("Congratulations! You've won!");	
		}
		for (Player s: spelers) {
			if (s instanceof ServerPeer) {
				((ServerPeer) s).write("You've lost :( The winner is" + winner.getName());
				((ServerPeer) s).joined = false;
			}
		}
	}
	
	/**
	 * Method to inform all the players every round which stones they have in possession.
	 */
	private void sendStenenToAll() {
		for (Player s: spelers) {
			s.write("Your stones are " + s.stenenToString());
		}
	}
	
	/**
	 * Resets the game by resetting the scoreboard (given every Player 0 points) and by removing the Stenen owned by the players.
	 */
	private void reset() {
		for (Player s: spelers) {
			s.reset();	
		}
		scoreboard = new HashMap<Player, Integer>();
		for (Player s: spelers) {
			scoreboard.put(s, new Integer(0));
		}
		eindeSpel = false;
	}

}
