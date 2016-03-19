package Testing;
import Game.*;
import Server.*;
import java.io.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

import org.junit.*;

public class GameTest {
	
	private Game game;
	private Player p1;
	private Player p2;
	private Player p3;
	private Player p4;
	private List<Player> spelerslijst = new ArrayList<Player>();
	
	@Before
	public void setUp() {
		p1 = new ComputerPlayer(game, "player1");
		p2 = new ComputerPlayer(game, "player2");
		p3 = new ComputerPlayer(game, "player3");
		p4 = new ComputerPlayer(game, "player4");
		spelerslijst.add(p1);
		spelerslijst.add(p2);
		game = new Game(spelerslijst, 4);
	}
	
	@Test
	public void test1() {
		assertEquals(game.gameSize(), 4);
		assertEquals(game.getSpelers(), spelerslijst);
		assertEquals(game.getCurrentPlayer(), null);
		assertFalse(game.legeZak());
		assertFalse(game.isRunning());
	}
	
	@Test
	public void test2() {
		game.addSpeler(p3);
		assertFalse(game.isRunning());
		assertEquals(spelerslijst, game.getSpelers());
		game.addSpeler(p4);
		assertTrue(game.isRunning());
		assertTrue(spelerslijst.contains(game.getCurrentPlayer()));
		
	}
	
}
