package Game;

import java.util.*;

/**
 * Class to represent the Board in the Qwirkle game.
 * @author Eric van Schaik and Birte Brunt
 *
 */
public class Board {
	
	//@ invariant toString() != null;

		private boolean valid;
		private boolean kleur;
		private boolean vorm;
		private boolean hor;
		private boolean ver;
		private boolean hor2;
		private boolean ver2;
		private boolean steen1fout;
		private Steen steeneen = null;
		private int[] vakeen = new int[2];
		private int[] vak = new int[2];
		private Map<Steen, int[]> nieuwcopy = new HashMap<Steen, int[]>();
		private List<Integer> rij = new ArrayList<Integer>();
		boolean nietNeergelegd;
		private Map<Steen, int[]> vakjes;
		private boolean vakbestaat = false;

		
		/**
		 * A new Board gets a new empty map, with room for entries with values
		 * of the type Steen and of the type int[].
		 */
		/*@ pure */ public Board() {
			vakjes = new HashMap<Steen, int[]>();
		}
		
		/**
		 * Another version of the normal Board constructor, where you give a non-empty map
		 * of entries with values of type Steen and type int[], which then make up the new Board.
		 * Only used for copying the board.
		 * @param vakjes, gives the fields which make up the new Board
		 */
		

		/*@ pure */ private Board(Map<Steen, int[]> vakjes) {
			this.vakjes = vakjes;
		}
		
		/**
		 * Deletes all fields of the Board, the board now only has an empty map, like in the standard constructor.
		 */
		/*@ ensures vakjes.isEmpty() = True;
		 */
		public void reset() {
			vakjes = new HashMap<Steen, int[]>();
		}
		
		public Map<Steen, int[]> getVakjes(){
			return vakjes;
		}
		
		/**
		 * This method places a list of tiles with given coordinates and puts them on the board.
		 * @param steentjes the list of tiles with given coordinates.
		 * @return true if all the tiles are successfully placed, false if not.
		 */
		/*@ requires steentjes.isEmpty() != True;
			ensures result == True || result == False;
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
		/*@ requires x != null;
		 *  requires y != null;
		 */
		/*@ pure */ public static int[] getVakje(int x, int y) {
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
		/*@ requires vakje.length != 0;
			ensures steen != null;
		 */
		/*@ pure */ public Steen getSteen(int[] vakje) {
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
		/*@ pure */ public Board deepCopy() {
			return new Board(vakjes);
		}
		
		/*@ requires steen != null;
		 *  requires vakje.length != 0;
			ensures result == True || result == False;
		 */
		/*@ pure */ private boolean eersteSteen (Steen steen, int[] vakje){
			steen1fout = false;
			int x = vakje[0];
			int y = vakje[1];
			boolean vakjebestaat = false;
				for (Map.Entry<Steen, int[]> f: vakjes.entrySet()){
					if (f.getValue()[0]==x && f.getValue()[1]==y){
						vakjebestaat = true;
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
				if (vakjebestaat == false){return false;}
				else {return true;}
		}
		
		/*@ requires moves.isEmpty() != True;
			ensures result == True || result == False;
		 */
		/*@ pure */ private boolean herhaling(int i, boolean hor, Steen steen){
			if (hor){
				vak[0] = vakeen[0]-i;
				vak[1] = vakeen[1];
			}
			if (ver) {
				vak[0] = vakeen[0];
				vak[1] = vakeen[1]-i;
			}
			for (Map.Entry<Steen, int[]> e: vakjes.entrySet()){
				if (e.getValue()[0]==vak[0] && e.getValue()[1]==vak[1]){
					if (vorm == true){
						if (e.getKey().getType()[1] == steen.getType()[1]){return false;}
					}
					else if (kleur == true){
						if (e.getKey().getType()[0] == steen.getType()[0]){return false;}
					}
				}
			}
			return true;
		}
		
		/*@ requires j != 0;
			ensures result == True || result == False;
		 */
		/*@ pure */ private boolean nogNietAanwezig(int j, boolean vorm){
				nietNeergelegd = true;
				Map<Steen, int[]> toremove = new HashMap<Steen, int[]>();
				if(ver2){
					vak[0] = vakeen[0];
					vak[1] = vakeen[1]+j;
				}
				if(hor2){
					vak[0] = vakeen[0]+j;
					vak[1] = vakeen[1];
				}
				int m;
				if (vorm){m = 1;}
				else {m = 0;}
					for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
						if (g.getValue()[0] == vak[0] && g.getValue()[1] == vak[1]){
							nietNeergelegd = false;
							if (rij.contains(g.getKey().getType()[m])||g.getKey().getType()[(m+1)%2]!=steeneen.getType()[(m+1)%2]) {
								return false;
							}
							rij.add(new Integer(g.getKey().getType()[m]));
							toremove.put(g.getKey(), g.getValue());
						}
					}
					for (Map.Entry<Steen, int[]> k: toremove.entrySet()){
						nieuwcopy.remove(k.getKey(), k.getValue());
					}
				return true;
		}
		
		/*@ requires vak.length != 0;
			ensures result == True || result == False;
		 */
		private boolean kleurofvorm(int[] vak){
			vakbestaat = false;
				for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
					if (e.getValue()[0]==vak[0] && e.getValue()[1]==vak[1]){
						vakbestaat = true;
						if (steeneen.getType()[0]==e.getKey().getType()[0]){
							vorm=true;
							rij.add(steeneen.getType()[1]);
						}
						if (steeneen.getType()[1]==e.getKey().getType()[1]){
							kleur=true;
							rij.add(steeneen.getType()[0]);
						}
						if ((vorm && kleur)||!vorm && !kleur) {return false;}
					}
				}
			
			return true;
		}
		
		/**
		 * Checks whether a list of tiles with given coordinates can be placed on the board.
		 * @param steentjes the list of tiles to be placed if the method returns true.
		 * @return true if all the tiles can be placed on the given coordinates, false if not.
		 */
		/*@ requires nieuw.isEmpty() != True;
			ensures result == True || result == False;
		 */
		/*@ pure */ public boolean isValidMove(Map<Steen, int[]> nieuw) {
			// variabelen
					valid = false;
					kleur = false;
					vorm = false;
					hor = false;
					ver = false;
					hor2 = false;
					ver2 = false;
					boolean links = false;
					boolean onder = false;
					steeneen = null;
					vakeen = new int[2];
					int[] testvak = new int[2];
					int[] vak2 = new int[2];
					int aanwezig = -60;
					// hier wordt een copy gemaakt van de binnengekregen map
					nieuwcopy.putAll(nieuw);
					for (Map.Entry<Steen, int[]> e: nieuw.entrySet()){
						for (Map.Entry<Steen, int[]> f: vakjes.entrySet()){
							if (e.getValue()[0] == f.getValue()[0] && e.getValue()[1] == f.getValue()[1]){
								return false;
							}
						}
						nieuwcopy.remove(e.getKey(), e.getValue());
						for (Map.Entry<Steen, int[]> f: nieuwcopy.entrySet()){
							if (e.getValue()[0] == f.getValue()[0] && e.getValue()[1]==f.getValue()[1]){
								return false;
							}
						}
						nieuwcopy.put(e.getKey(), e.getValue());
					}
					
					//stel het is de eerste zet
					if (vakjes.isEmpty()){
							for (Map.Entry<Steen, int[]> e: nieuw.entrySet()){
								if ((e.getValue()[0]==0)&&e.getValue()[1]==0){
									steeneen = e.getKey();
									vakeen = e.getValue();
									nieuwcopy.remove(steeneen);
								}
							}
							if (steeneen == null){return false;}
							if (!nieuwcopy.isEmpty()){
								testvak[0]=0;
								testvak[1]=1;
								vak2[0]=0;
								vak2[1]=-1;
								for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
									if (((e.getValue()[0]==0)&&e.getValue()[1]==1)||((e.getValue()[0]==0)&&e.getValue()[1]==-1)){
										ver2=true;
										kleurofvorm(testvak);
										if (vakbestaat==false){
											kleurofvorm(vak2);
										}
										if (kleurofvorm(testvak)==false || kleurofvorm(vak2)==false){
											return false;
										}
									}
								}
								testvak[0]=1;
								testvak[1]=0;
								vak2[0]=-1;
								vak2[1]=0;
								for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
									if ((e.getValue()[0]==1&&e.getValue()[1]==0)||(e.getValue()[0]==-1&&e.getValue()[1]==0)){
										hor2 = true;
										kleurofvorm(testvak);
										if (vakbestaat==false){
											kleurofvorm(vak2);
										}
										if (kleurofvorm(testvak)==false || kleurofvorm(vak2)==false){
											return false;
										}
									}
								}
								if(ver2&&hor2){return false;}
								if (ver2){
									for (int i = -6; i<0; i++){
										testvak[0] = vakeen[0];
										testvak[1] = vakeen[1]+i;
										for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){										
											if (e.getValue()[0]==testvak[0]&&e.getValue()[1]==testvak[1]){
												aanwezig = i;
												break;
											}
										}
										if (aanwezig !=-60){break;}
									}
								}
								if(hor2){
									for (int i= -6; i<0; i++){
										testvak[0] = vakeen[0]+i;
										testvak[1] = vakeen[1];
										for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
											if (e.getValue()[0]==testvak[0]&&e.getValue()[1]==testvak[1]){
												aanwezig = i;
												break;
											}
										}
										if (aanwezig !=-60){break;}
									}
								}

								if (aanwezig == -60){
									aanwezig = 0;
									nietNeergelegd = true;
								}
								
								for (int j = aanwezig; j<0; j++){
									if (kleur == true){
										if (!nogNietAanwezig(j, !kleur)){return false;}
									}
									if (vorm == true){
										if (!nogNietAanwezig(j, vorm)){return false;}
									}
									if (nietNeergelegd == true){break;}
								}
								if (nietNeergelegd){
									for (int j = 1; j<6; j++){
										if (kleur == true){
											if (!nogNietAanwezig(j, !kleur)){return false;}
										}
										if (vorm == true){
											if (!nogNietAanwezig(j, vorm)){return false;}
										}
										if (nietNeergelegd == true){break;}
									}
								}
								if (!nieuwcopy.isEmpty()) {return false;}
								else{
									rij = new ArrayList<Integer>();
									return true;}
							}
					}
					
					else{
						// hier wordt naar de eerste steen gezocht
						for (Map.Entry<Steen, int[]> e: nieuw.entrySet()){
							int x = e.getValue()[0];
							int y = e.getValue()[1];
							
							// ligt de steen links van een al aanwezige steen
							if (eersteSteen(e.getKey(), getVakje(x-1,y))==true){hor = true; links = true;}
							
							// ligt de steen rechts van een al aanwezige steen
							if (eersteSteen(e.getKey(), getVakje(x+1,y))==true){hor = true;}
							
							// ligt de steen onder een al aanwezige steen
							if (eersteSteen(e.getKey(), getVakje(x,y-1))==true){ver = true; onder = true;}
							
							// ligt de steen boven een al aanwezige steen
							if (eersteSteen(e.getKey(), getVakje(x,y+1))==true){ver = true;}
							
							// als de steen niet dezelfde vorm of kleur heeft als de steen die ernaast ligt dan return false
							if (steen1fout) {return false;}
							// als er een steen gevonden is die aansluit op een andere steen, stop dan met zoeken naar een andere steen
							if (valid == true){
								steeneen = e.getKey();
								vakeen = e.getValue();
								nieuwcopy.remove(e.getKey(),e.getValue());
								break;
							}
						}
						
						// als er geen enkele steen aansluit dan return false
						if (!valid) {
							return false;
						}
						
						
						if (nieuwcopy.isEmpty()){
							return true;
						}
						
						// ga anders naar de rest van de stenen kijken
						else {
							// is de reeks stenen een horizontale of verticale rij
							for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
								if ((e.getValue()[0]==vakeen[0] && e.getValue()[1] == vakeen[1]-1) || (e.getValue()[0]==vakeen[0] && e.getValue()[1] == vakeen[1]+1)){
									ver2 = true;
								}
								if ((e.getValue()[0]==vakeen[0]-1 && e.getValue()[1] == vakeen[1]) || (e.getValue()[0]==vakeen[0]+1 && e.getValue()[1] == vakeen[1])){
									hor2 = true;
								}
							}
							if (ver2 && hor2) {return false;}						
							// stel de reeks is horizontaal en de eerste steen lag al horizontaal aan een andere steen
							if (hor2){
								// als er al een steen links van de originele steen aanwezig is
								if (hor){
									if (vorm){
										// de int[] rij staan alle kleuren in die al in de rij aanwezig zijn en dus niet nog eens mogen worden gebruikt
										rij.add(steeneen.getType()[1]);
									}
									if (kleur){
										rij.add(steeneen.getType()[0]);
									}
									if (links){
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de nieuwe steen
										for (int i = 0; i>-6; i--){
											if (herhaling(i, hor, steeneen) == false){return false;}
												for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
													if (herhaling(i, hor, g.getKey()) == false){return false;}
												}
											 }
										}
									}
									else {
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de nieuwe steen
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
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de eerste steen
										for (int i =0; i>-6; i--){
											if (herhaling(i, !ver, steeneen) == false){return false;}
										}
									}
									else {
										//check of er in de al aanwezige stenen de kleur/vorm voorkomt van de eerste steen
										for (int i =0; i<6; i++){
											if (herhaling(i, !ver, steeneen) == false){return false;}
										}	
									}
								}
								// check of er in de nieuwe rij dubbele gegevens voorkomen
								for (int i = -6; i<0; i++){
									for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
										if (e.getValue()[0] == vakeen[0] && e.getValue()[1] == vakeen[1]+i){
											aanwezig = i;
											break;
										}
									}
									if (aanwezig != -60) {break;}
								}
								if (aanwezig == -60){
									aanwezig =0;
									nietNeergelegd = false;
								}
							if (ver2){	
								// als er al een steen links van de originele steen aanwezig is
								if (ver){
									if (onder == true){
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de nieuwe steen
										for (int i = 0; i>-6; i--){
											if (herhaling(i, hor, steeneen) == false){return false;}
											for (Map.Entry<Steen, int[]> g: nieuwcopy.entrySet()){
												if (herhaling(i, !ver, g.getKey()) == false){return false;}
											}
										}
									}
									else {
										//check of er in de al aanwezige stenen de kleur/vorm voorkomt van de nieuwe steen
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
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de eerste steen
										for (int i =0; i>-6; i--){
											if (herhaling(i, hor, steeneen) == false){return false;}
										}
									}
									else{
										// check of er in de al aanwezige stenen de kleur/vorm voorkomt van de nieuwe steen
										for (int i =0; i<6; i++){
											if (herhaling(i, hor, steeneen) == false){return false;}
										}
									}
								}	
								// check wat de eerste steen in de rij is
								for (int i = -6; i<0; i++){
									for (Map.Entry<Steen, int[]> e: nieuwcopy.entrySet()){
										if (e.getValue()[0] == vakeen[0] && e.getValue()[1] == vakeen[1]+i){
											aanwezig = i;
											break;
										}
									}
									if (aanwezig != -60) {break;}
								}
								if (aanwezig == -60){
									aanwezig = 0;
									nietNeergelegd = false;
								}
						}
						for (int j = aanwezig; j<0; j++){
							if (!nogNietAanwezig(j, vorm)){return false;}
							if (nietNeergelegd == true){break;}
						}
						if(!nietNeergelegd){
							for (int j = 1; j<6; j++){
								if (!nogNietAanwezig(j, vorm)){return false;}
								if (nietNeergelegd == true){break;}
							}
						}
						}
					}
					rij = new ArrayList<Integer>();
					if (!nieuwcopy.isEmpty()) {return false;}
					else {return true;}
		}

		/**
		 * Returns the x-coordinate of the tile with the highest x-coordinate.
		 * @return the x-coordinate of the tile with the highest x-coordinate.
		 */
		/*@ ensures result >= 0;
		 */
		/*@ pure */ public int getMaxX() {
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
		/*@ ensures result >= 0;
		 */
		/*@ pure */ public int getMinY() {
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
		/*@ ensures result >= 0;
		 */
		/*@ pure */ public int getMinX() {
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
		/*@ ensures result >= 0;
		 */
		/*@ pure */ public int getMaxY() {
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
		/*@ ensures result == True || result == False;
		 */
		/*@ pure */ public boolean isEmpty(int x, int y){
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
		
		/*@
		 * ensures result != "";
		 */
		/*@ pure */ public String toString() {
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
