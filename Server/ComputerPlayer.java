package Server;

import java.util.*;

import Game.*;

public class ComputerPlayer implements Player, Runnable {
	
	protected String name;
	public Game game;
	private List<Steen> stenen;
	
	public ComputerPlayer(Game game, String name) {
		this.game = game;
		this.stenen = new ArrayList<Steen>();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addSteen(Steen steen) {
		stenen.add(steen);
	}
	
	public void write(String message) {
		
	}
	
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

	private Board getBoardgame(){
		return game.getBoard();
	}
	
	public void makeMove() {
		Board board = getBoardgame();
		String[] move = new String[2];
		int maxx = board.getMaxX();
		int minx = board.getMinX();
		int maxy = board.getMaxY();
		int miny = board.getMinY();
		Map<Steen, int[]> test = new HashMap<Steen, int[]>();
		Steen steen = null;
		int[] vakje = new int[2];
		for (int i = minx; i<maxx+1; i++){
			for (int j = miny; j<maxy+1; j++){
				for (int k = 0; k<stenen.size(); k++){
					test = new HashMap<Steen, int[]>();
						steen = stenen.get(k);
						vakje = new int[2];
						vakje[0] = i;
						vakje[1] = j;
						test.put(steen, vakje);
						if (board.isValidMove(test)==true){
							break;
				}
				if (board.isValidMove(test)==true){
					break;
				}
			}
				if (board.isValidMove(test)==true){
					break;
				}
			}
		}
		if (board.isValidMove(test)== true){
			String steenstring = steen.getType()[0] + "," + steen.getType()[1];
			String vakstring = vakje[0] + "," + vakje[1];
			move[0] = "place";
			move[1] = steenstring + " " + vakstring;
		}
		else {
			move[0] = "trade";
			move[1] = this.stenenToString();
		}
		if (game.getCurrentPlayer().equals(this)) {
			boolean movesucceed;
			if (move[0].equals("place")) {
				Map<Steen, int[]> placingmap = new HashMap<Steen, int[]>();
				String[] steenenplaatsen = move[1].split(" ");
				if ((steenenplaatsen.length % 2) != 0) {
					write("error 0, invalid move");
				} else {
					for (int i = 0; i < steenenplaatsen.length; i = i + 2) {
						String[] steen1 = steenenplaatsen[i].split(",");
						if (!(steen1.length == 2)) {
							write("error 0, invalid stone");
						} else {
							System.out.println("Trying to place the tiles...");
							String[] plaatss = steenenplaatsen[i + 1].split(",");
							int[] plaatsi = new int[2];
							plaatsi[0] = Integer.parseInt(plaatss[0]);
							plaatsi[1] = Integer.parseInt(plaatss[1]);
							try {
								placingmap.put(getSteen(Integer.parseInt(steen1[0]), Integer.parseInt(steen1[1])),
										plaatsi);
								stenen.remove(getSteen(Integer.parseInt(steen1[0]), Integer.parseInt(steen1[1])));
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
	
	public String stenenToString() {
		String stenenToString = "";
		for (Steen s: stenen) {
			stenenToString = s.toString() + ", " + stenenToString;
		}
		return stenenToString;
	}

	
	public void reset() {
		stenen = new ArrayList<Steen>();
	}
	
	public List<Steen> getStenen() {
		return stenen;
	}
	
	public void run() {
		while (game.isRunning()) {
			if (this.equals(game.getCurrentPlayer())) {
				makeMove();
				try{
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				notify();
			}
		}
	}
}