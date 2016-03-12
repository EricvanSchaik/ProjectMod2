package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client extends Observable implements Runnable {

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private View view;
	private static final String USAGE = "usage: java week7.cmdline.Client <name> <address> <port>";
	private String input;
	public boolean isItsTurn = false;
	public boolean isInGame = false;
	public boolean hasLoggedIn = false;
	public String name;

	public Client(Socket socket) throws IOException {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.view = new TUIView(this);
	}

	/** Starts a Client application. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			Socket sock = new Socket(addr, port);
			Client client = new Client(sock);
			client.run();
			client.handleTerminalInput();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			while ((input = in.readLine()) != null) {
				notifyObservers(input);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void write(String text) {
		try {
			out.write(text);
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleTerminalInput() throws IOException {
		view.handleTerminalInput();
	}

	public Socket getSocket() {
		return socket;
	}

}
