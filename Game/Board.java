package Game;

import java.util.*;

/**
 * Class to represent the Board in the Qwirkle game.
 * @author Eric van Schaik and Birte Brunt
 *
 */
public class Board {
	
	private Map<Steen, int[]> vakjes;
	
	/**
	 * A new Board gets a new empty map, with room for entries with values
	 * of the type Steen and of the type int[].
	 */
	public Board() {
		vakjes = new HashMap<Steen, int[]>();
	}
	
	/**
	 * Another version of the normal Board constructor, where you give a non-empty map
	 * of entries with values of type Steen and type int[], which then make up the new Board.
	 * Only used for copying the board.
	 * @param vakjes, gives the fields which make up the new Board
	 */
	
	private Board(Map<Steen, int[]> vakjes) {
		this.vakjes = vakjes;
	}
	
	/**
	 * Deletes all fields of the Board, the board now only has an empty map, like in the standard constructor.
	 */
	public void reset() {
		vakjes = new HashMap<Steen, int[]>();
	}
	
	/**
	 * Places a Steen on the board on a given field. Before then, the field didn't exist.
	 * @param steen: The Steen to be placed.
	 * @param vakje: the field on which the Steen needs to be placed.
	 * @return true if the Steen can be placed on the field, and therefore is placed there,
	 * false if the Steen cannot be placed there.
	 */
	
	public boolean place(Steen steen, int[] vakje) {
		if (isValidMove(steen, vakje)) {
			vakjes.put(steen, vakje);
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns an int-array representation of a field, given a x and y coordinate.
	 * @param x: the x-coordinate of the field.
	 * @param y: the y-coordinate of the field.
	 * @return an int-array representation of a field
	 */
	
	public int[] getVakje(int x, int y) {
		int[] vakje = new int[2];
		vakje[0] = x;
		vakje[1] = y;
		return vakje;
	}
	
	/**
	 * Returns the Steen which is on the given field.
	 * @param vakje: the int-array representation of a field.
	 * @return the Steen which is on the given field.
	 */
	
	public Steen getSteen(int[] vakje) {
		Steen steen = null;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset) {
			if (Arrays.equals(vakje, e.getValue())) {
				steen = e.getKey();
			}
		}
		return steen;
	}
	
	/**
	 * Gives a copy of this Board.
	 * @return a copy of the board.
	 */
	
	public Board deepCopy() {
		return new Board(vakjes);
	}
	
	/**
	 * Checks if the given Steen can be placed on the given field
	 * @param steen: the Steen which can or cannot be placed.
	 * @param vakje: the field on which the Steen can or cannot be placed.
	 * @return true if the given Steen can be placed on the given field, false if not.
	 */
	
	public boolean isValidMove(Steen steen, int[] vakje) {
		int x = vakje[0];
		int y = vakje[1];
		if (vakjes.containsValue(vakje)) {
			return false;
		}
		else if (vakjes.containsValue(getVakje((x-1), y)) || vakjes.containsValue(getVakje((x+1), y)) || vakjes.containsValue(getVakje(x, (y-1))) || vakjes.containsValue(getVakje(x, (y-1)))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private int getMaxX() {
		int maxx = 0;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset) {
			int value = e.getValue()[0];
			if (value > maxx){
				maxx = value;
			}
		}
		return maxx;
	}
	
	private int getMinY() {
		int maxy = 0;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset) {
			int value = e.getValue()[1];
			if (value > maxy){
				maxy = value;
			}
		}
		return maxy;
	}
	
	private int getMinX() {
		int minx = 0;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset) {
			int value = e.getValue()[0];
			if (value < minx){
				minx = value;
			}
		}
		return minx;
	}
	
	private int getMaxY() {
		int miny = 0;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset) {
			int value = e.getValue()[1];
			if (value < miny){
				miny = value;
			}
		}
		return miny;
	}
	
	public boolean isEmpty(int x, int y){
		boolean empty = true;
		Set<Map.Entry<Steen, int[]>> entryset = vakjes.entrySet();
		for (Map.Entry<Steen, int[]> e: entryset){
			int mapx = e.getValue()[0];
			int mapy = e.getValue()[1];
			if (mapx==x && mapy==y){
				empty = false;
			}
		}
		return empty;
	}
	
	public String toString() {
		if (isEmpty(0,0) == true){
			return "";
		}
		else {
			int minx = getMinX();
			int maxx = getMaxX();
			int miny = getMinY();
			int maxy = getMaxY();
			String streep = "";
			for (int k = minx; k<(maxx); k++){streep = streep + "-----+";}
			streep = streep + "-----";
			String complete = "";
			for (int i = maxy; i < (miny+1); i++){
				String row = "";
				for (int j = minx; j < (maxx+1); j++){
					if (isEmpty(j,i) == false){
						int[] field = {j,i};
						Steen steen = getSteen(field);
						int[] typesteen = steen.getType();
						if (j==minx){row = " " + typesteen[0] + "," + typesteen[1] + " | ";}
						else {
							if (j==maxx){row = row + typesteen[0] + "," + typesteen[1];}
							else{row = row + typesteen[0] + "," + typesteen[1] + " | "; }
						}
					}
					else {
						if (j==minx){row = "     | ";}
						else{
							if (j==maxx){}
							else {row = row + "    | ";}
						}
					}
				}	
				if (i!=miny){complete = complete + row + "\n" + streep + "\n";}
				else {complete = complete + row;}
			}
			return complete;
		}
	}
}
