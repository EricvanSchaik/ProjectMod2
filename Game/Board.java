package Game;

import java.util.*;

/**
 * Class to represent the Board in the Qwirkle game.
 * @author Eric van Schaik and Birte Brunt
 *
 */
public class Board {
	
	boolean valid;
	boolean kleur;
	boolean vorm;
	boolean hor;
	boolean ver;
	boolean steen1fout;
	Steen steeneen = null;
	int[] vakeen = null;
	int[] vak = null;
	Map<Steen, int[]> nieuwcopy = new HashMap<Steen, int[]>();
	List<Integer> rij = new ArrayList<Integer>();
	int[] kolom = null;
	boolean nietNeergelegd;
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
	 * This method places a list of tiles with given coordinates and puts them on the board.
	 * @param steentjes the list of tiles with given coordinates.
	 * @return true if all the tiles are successfully placed, false if not.
	 */
	
	public boolean place(Map<Steen, int[]> steentjes) {
		if (isValidMove(steentjes)) {
			for (Map.Entry<Steen, int[]> s: steentjes.entrySet()) {
				vakjes.put(s.getKey(), s.getValue());
			}
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
	
	private boolean eersteSteen (Steen steen, int[] vakje){
		steen1fout = false;
		int x = vakje[0];
		int y = vakje[1];
		if (vakjes.containsValue(getVakje(x, y))){
			for (Map.Entry<Steen, int[]> f: vakjes.entrySet()){
				if (f.getValue()==(getVakje(x, y))){
					if (f.getKey().getType()[0] == steen.getType()[0] && f.getKey().getType()[1] != steen.getType()[1]){
						valid = true;
						vorm = true;
						return true;
					}
					else if (f.getKey().getType()[1] == steen.getType()[1] && f.getKey().getType()[0] != steen.getType()[0]){
						valid = true;
						kleur = true;
						return true;
					}
					else {
						steen1fout = true;
						return false;
					}
				}
			}
		}
		return false;
	}
	
	private boolean herhaling(int i, boolean hor, Steen steen){
		if (hor == true){
			vak[0] = vakeen[0]-i;
			vak[1] = vakeen[1];
		}
		else {
			vak[0] = vakeen[0];
			vak[1] = vakeen[1]-i;
		}
			if (vakjes.containsValue(vak)){
				for (Map.Entry<Steen, int[]> e: vakjes.entrySet()){
					if (e.getValue()==vak){
						if (vorm == true){
							if (e.getKey().getType()[1] == steen.getType()[1]){return false;}
						}
						else if (kleur == true){
							if (e.getKey().getType()[0] == steen.getType()[0]){return false;}
						}
					}
				}
			}
			return true;
	}
	
	private boolean nogNietAanwezig(int j, boolean vorm){
			nietNeergelegd = false;
			vak[0] = vakeen[0];
			vak[1] = vakeen[1]-j;
			int m;
			if (vorm){m = 1;}
			else {m = 0;}
			if (nieuwcopy.containsValue(vak)){
				for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
					if (g.getValue() == vak){
						if (rij.contains(g.getKey().getType()[m])) {
							return false;
						}
						rij.add(new Integer(g.getKey().getType()[m]));
						nieuwcopy.remove(g.getKey());
					}
				}
			}
			else {nietNeergelegd = true;}
			return true;
	}
	
	/**
	 * Checks whether a list of tiles with given coordinates can be placed on the board.
	 * @param steentjes the list of tiles to be placed if the method returns true.
	 * @return true if all the tiles can be placed on the given coordinates, false if not.
	 */
	public boolean isValidMove(Map<Steen, int[]> nieuw) {
		// variabelen
				valid = false;
				kleur = false;
				vorm = false;
				boolean hor = false;
				boolean ver = false;
				boolean hor2 = false;
				boolean ver2 = false;
				boolean links = false;
				boolean onder = false;
				Steen steeneen = null;
				int[] vakeen = new int[2];
				int[] testvak = new int[2];
				int[] vak2 = new int[2];
				int aanwezig = -60;
				// hier wordt een copy gemaakt van de binnengekregen map
				nieuwcopy.putAll(nieuw);
				
				if (vakjes.isEmpty()){
					testvak[0] = 0;
					testvak[1] = 0;
					if (nieuw.containsValue(testvak)){
						for (Map.Entry<Steen, int[]> e: nieuw.entrySet()){
							if (e.getValue() == testvak){
								steeneen = e.getKey();
								vakeen = e.getValue();
								nieuwcopy.remove(e);
							}
						}
						if (!nieuwcopy.isEmpty()){
							vak[0]=0;
							vak[1]=1;
							vak2[0]=0;
							vak2[1]=-1;
							if (nieuwcopy.containsValue(vak)||nieuwcopy.containsValue(vak2)){
								ver=true;
								if (nieuwcopy.containsValue(vak)){
									for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
										if (e.getValue()==vak){
											if (steeneen.getType()[0]==e.getKey().getType()[0]){
												vorm=true;
												rij.add(steeneen.getType()[1]);
												rij.add(e.getKey().getType()[1]);
											}
											if (steeneen.getType()[1]==e.getKey().getType()[1]){
												kleur=true;
												rij.add(steeneen.getType()[0]);
												rij.add(e.getKey().getType()[0]);
											}
											if ((vorm&&kleur)||(!vorm && !kleur)){return false;}
										}
									}
								}
							}
							vak[0]=1;
							vak[1]=0;
							vak2[0]=-1;
							vak2[1]=0;
							if (nieuwcopy.containsValue(vak)||nieuwcopy.containsValue(vak2)){
								hor = true;
								if (nieuwcopy.containsValue(vak)){
									for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
										if (e.getValue()==vak){
											if (steeneen.getType()[0]==e.getKey().getType()[0]){
												vorm=true;
												rij.add(steeneen.getType()[1]);
												rij.add(e.getKey().getType()[1]);
											}
											if (steeneen.getType()[1]==e.getKey().getType()[1]){
												kleur=true;
												rij.add(steeneen.getType()[0]);
												rij.add(e.getKey().getType()[0]);
											}
											if ((vorm&&kleur)||(!vorm && !kleur)){return false;}
										}
									}
								}
							}
							if(ver&&hor){return false;}
							
							if (ver){
								for (int i = -6; i<6; i++){
									vak[0] = vakeen[0];
									vak[1] = vakeen[1]+i;
									if (nieuwcopy.containsValue(vak)){
										aanwezig = i;
										break;
									}
								}
							}
							if(hor){
								for (int i= -6; i<6; i++){
									vak[0] = vakeen[0]+i;
									vak[1] = vakeen[1];
									if (nieuwcopy.containsValue(vak)){
										aanwezig = i;
										break;
									}
								}
							}
							
							for (int j = aanwezig; j<6; j++){
								if (kleur = true){
									if (!nogNietAanwezig(j, !kleur)){return false;}
								}
								if (vorm = true){
									if (!nogNietAanwezig(j, vorm)){return false;}
								}
								if (nietNeergelegd == true){break;}
							}
							if (!nieuwcopy.isEmpty()) {return false;}
						}
					}
					else {return true;}
				}
				else {
					return false;
				}
				
				// hier wordt naar de eerste steen gezocht
				for (Map.Entry<Steen, int[]> e: nieuw.entrySet()){
					int x = e.getValue()[0];
					int y = e.getValue()[1];
					
					// ligt de steen links van een al aanwezige steen
					testvak[0]=x-1;
					testvak[1]=y;
					if (eersteSteen(e.getKey(), testvak)==true){hor = true; links = true;}
					
					// ligt de steen rechts van een al aanwezige steen
					testvak[0]=x+1;
					testvak[1]=y;
					if (eersteSteen(e.getKey(), testvak)==true){hor = true;}
					
					// ligt de steen onder een al aanwezige steen
					testvak[0]=x;
					testvak[1]=y-1;
					if (eersteSteen(e.getKey(), testvak)==true){ver = true; onder = true;}
					
					// ligt de steen boven een al aanwezige steen
					testvak[0]=x;
					testvak[1]=y+1;
					if (eersteSteen(e.getKey(), testvak)==true){ver = true;}
					
					// als de steen niet dezelfde vorm of kleur heeft als de steen die ernaast ligt dan return false
					if (steen1fout = true) {return false;}
					
					// als er een steen gevonden is die aansluit op een andere steen, stop dan met zoeken naar een andere steen
					if (valid == true){
						steeneen = e.getKey();
						vakeen = e.getValue();
						nieuwcopy.remove(e);
						break;
					}
				}
				
				// als er geen enkele steen aansluit dan return false
				if (!valid) {
					return false;
				}
				
				// ga anders naar de rest van de stenen kijken
				else {
					// is de reeks stenen een horizontale of verticale rij
					vak[0] = vakeen[0];
					vak[1] = vakeen[1]-1;
					vak2[0] = vakeen[0];
					vak2[1] = vakeen[1] +1;
					if (nieuwcopy.containsValue(vak)||nieuwcopy.containsValue(vak2)){ver2=true;}
					vak[0] = vakeen[0]-1;
					vak[1] = vakeen[1];
					vak2[0] = vakeen[0]+1;
					vak2[1] = vakeen[1];
					if (nieuwcopy.containsValue(vak)||nieuwcopy.containsValue(vak2)){hor2=true;}
					if (ver2 && hor2) {return false;}
					
					// stel de reeks is horizontaal en de eerste steen lag al horizontaal aan een andere steen
					if (hor2){
						if (vorm){
							// de int[] rij staan alle kleuren in die al in de rij aanwezig zijn en dus niet nog eens mogen worden gebruikt
							rij.add(steeneen.getType()[1]);
							
							// als er al een steen links van de originele steen aanwezig is
							if (hor){
								if (links == true){
									// check of er in de al aanwezige stenen de kleur voorkomt van de nieuwe steen
									for (int i = 0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
											for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
												if (herhaling(i, hor, g.getKey()) == false){return false;}
											}
										 }
									}
								else {
									for (int i = 0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
											for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
												if (herhaling(i, hor, g.getKey()) == false){return false;}
											}
										 }
									}
								}
							else if (ver){
								if (onder){
									for (int i =0; i>-6; i--){
										if (herhaling(i, !ver, steeneen) == false){return false;}
									}
								}
								else {
									for (int i =0; i<6; i++){
										if (herhaling(i, !ver, steeneen) == false){return false;}
									}	
								}
							}
							// check of er in de nieuwe rij dubbele gegevens voorkomen
							for (int i = -6; i<6; i++){
								vak[0] = vakeen[0];
								vak[1] = vakeen[1]+i;
								if (nieuwcopy.containsValue(vak)){
									aanwezig = i;
									break;
								}
							}
							for (int j = aanwezig; j<6; j++){
								if (!nogNietAanwezig(j, vorm)){return false;}
								if (nietNeergelegd == true){break;}
							}
							rij = null;
						}
						if (kleur){
							rij.add(steeneen.getType()[0]);
							if (hor){
								if (links == true){
									for (int i = 0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, hor, g.getKey()) == false){return false;}
										}
									}
								}
								else {
									for (int i = 0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, hor, g.getKey()) == false){return false;}
										}
									}
								}
							}
							else if (ver) {
								if (onder){
									for (int i =0; i>-6; i--){
										if (herhaling(i, !ver, steeneen) == false){return false;}
									}
								}
								else {
									for (int i =0; i<6; i++){
										if (herhaling(i, !ver, steeneen) == false){return false;}
									}
								}
							}
							for (int i = -6; i<6; i++){
								vak[0] = vakeen[0];
								vak[1] = vakeen[1]+i;
								if (nieuwcopy.containsValue(vak)){
									aanwezig = i;
									break;
								}
							}
							for (int j = aanwezig; j<6; j++){
								if (!nogNietAanwezig(j, !kleur)){return false;}
								if (nietNeergelegd == true){break;}
							}
							rij = null;
						}
					}
					
					if (ver2){
						if (vorm){
							// de int[] rij staan alle kleuren in die al in de rij aanwezig zijn en dus niet nog eens mogen worden gebruikt
							rij.add(steeneen.getType()[1]);
							
							// als er al een steen links van de originele steen aanwezig is
							if (ver){
								if (onder == true){
									// check of er in de al aanwezige stenen de kleur voorkomt van de nieuwe steen
									for (int i = 0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, !ver, g.getKey()) == false){return false;}
										}
									}
								}
								else {
									for (int i = 0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, !ver, g.getKey()) == false){return false;}
										}
									}
								}
							}
							else if (hor){
								if (links){
									for (int i =0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
									}
								}
								else{
									for (int i =0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
									}
								}
							}	
							// check of er in de nieuwe rij dubbele gegevens voorkomen
							for (int i = -6; i<6; i++){
								vak[0] = vakeen[0];
								vak[1] = vakeen[1]+i;
								if (nieuwcopy.containsValue(vak)){
									aanwezig = i;
									break;
								}
							}
							for (int j = aanwezig; j<6; j++){
								if (!nogNietAanwezig(j, vorm)){return false;}
								if (nietNeergelegd == true){break;}
							}
							rij = null;
						}
						if (kleur){
							rij.add(steeneen.getType()[0]);
							if (ver){
								if (onder == true){
									for (int i = 0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, !ver, g.getKey()) == false){return false;}
										}
									}
								}
								else {
									for (int i = 0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
										for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
											if (herhaling(i, !ver, g.getKey()) == false){return false;}
										}
									}
								}
							}
							else if (hor){
								if (links){
									for (int i =0; i>-6; i--){
										if (herhaling(i, hor, steeneen) == false){return false;}
									}
								}
								else {
									for (int i =0; i<6; i++){
										if (herhaling(i, hor, steeneen) == false){return false;}
									}
								}
							}					
							for (int i = -6; i<6; i++){
								vak[0] = vakeen[0];
								vak[1] = vakeen[1]+i;
								if (nieuwcopy.containsValue(vak)){
									aanwezig = i;
									break;
								}
							}
							for (int j = aanwezig; j<6; j++){
								if (!nogNietAanwezig(j, !kleur)){return false;}
								if (nietNeergelegd == true){break;}
							}
							rij = null;
						}
					}
					if (!nieuwcopy.isEmpty()) {return false;}
				}
				return true;
	}
	
	/**
	 * Returns the x-coordinate of the tile with the highest x-coordinate.
	 * @return the x-coordinate of the tile with the highest x-coordinate.
	 */
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
	
	/**
	 * Returns the x-coordinate of the tile with the lowest x-coordinate.
	 * @return the x-coordinate of the tile with the lowest x-coordinate.
	 */
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
	
	/**
	 * Returns the x-coordinate of the tile with the lowest x-coordinate.
	 * @return the x-coordinate of the tile with the lowest x-coordinate.
	 */
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
	
	/**
	 * Returns the y-coordinate of the tile with the highest y-coordinate.
	 * @return the y-coordinate of the tile with the highest y-coordinate.
	 */
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
	
	/**
	 * Checks whether a given field on the board is empty.
	 * @param x, the x-coordinate of the field.
	 * @param y, the y-coordinate of the field.
	 * @return true if the field is empty, false if there already is a tile on it.
	 */
	public boolean isEmpty(int x, int y){
		boolean empty = true;
		for (Map.Entry<Steen, int[]> e: vakjes.entrySet()){
			int mapx = e.getValue()[0];
			int mapy = e.getValue()[1];
			if (mapx==x && mapy==y){
				empty = false;
			}
		}
		return empty;
	}
	
	/**
	 * This method is called every round which gives a String representation of the board, to be sent to all the clients, so that they know what the board looks like.
	 * @return a String representation of the board.
	 */
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
