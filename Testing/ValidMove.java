package Testing;

import Game.*;
import java.util.*;

public class ValidMove {

static boolean result;
	
	public static void main(String[] args) throws InvalidArgumentException{
		Board board = new Board();
		Map<Steen, int[]> nieuw = new HashMap<Steen, int[]>();
		Steen steen = new Steen(0, 4);
		Steen steen2 = new Steen(1,4);
		Steen steen3 = new Steen(2,4);
		nieuw.put(steen,  Board.getVakje(0,0));
		nieuw.put(steen2, Board.getVakje(0,1));
		nieuw.put(steen3, Board.getVakje(0,2));
		System.out.println("map to be placed: " + nieuw);
		result = Board.isValidMove(nieuw);
		System.out.println("result: " + result);
		System.out.println("result: " + Board.isValidMove(nieuw));
		System.out.println("Succeeding at placement: " + board.place(nieuw));
		System.out.println("vakjes is nu" + board.getVakjes().entrySet().toArray());
//		Steen steen4 = new Steen(1,0);
//		Map<Steen, int[]> twee = new HashMap<Steen, int[]>();
//		twee.put(steen4, Board.getVakje(1, 1));
//		result = Board.isValidMove(twee);
//		System.out.println(result);
	}
	
}
