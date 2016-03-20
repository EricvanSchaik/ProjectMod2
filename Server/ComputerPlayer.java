package Server;

import java.util.ArrayList;
import java.util.List;

import Game.Game;
import Game.Steen;

public class ComputerPlayer implements Player {
	
	protected String name;
	private Server server;
	private boolean connected = false;
	public Game game;
	private boolean joined;
	private List<Steen> stenen;
	private String[] move;
	private boolean movesucceed = false;
	
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
	
	public String createMove() throws InvalidArgumentException{
		int maxx = Board.getMaxX();
		int minx = Board.getMinX();
		int maxy = Board.getMaxY();
		int miny = Board.getMinY();
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
						if (Board.isValidMove(test)==true){
							break;
				}
				if (Board.isValidMove(test)==true){
					break;
				}
			}
				if (Board.isValidMove(test)==true){
					break;
				}
			}
		}
		if (Board.isValidMove(test)== true){
			String steenstring = steen.getType()[0] + "," + steen.getType()[1];
			String vakstring = vakje[0] + "," + vakje[1];
			return("place" + steenstring + " " + vakstring);
		}
		else {
			return("trade" + this.stenenToString());
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

}
