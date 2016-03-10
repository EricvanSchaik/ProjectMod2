package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private View view;
	private static final String USAGE
        = "usage: java week7.cmdline.Client <name> <address> <port>";
    
    public Client (Socket socket) throws IOException {
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
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			Socket sock = new Socket(addr, port);
			Client client = new Client(sock);
			client.run();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
    public void run() {
    	
    }
    
    
    /*public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(0);
        }
        
        String name = args[0];
        InetAddress addr = null;
        int port = 0;
        Socket sock = null;
        
        // check args[1] - the IP-adress
        try {
            addr = InetAddress.getByName(args[1]);
        } catch (UnknownHostException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: host " + args[1] + " unknown");
            System.exit(0);
        }
        
        // parse args[2] - the port
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: port " + args[2]
            		           + " is not an integer");
            System.exit(0);
        }
        
        // try to open a Socket to the server
        try {
            sock = new Socket(addr, port);
        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + addr
                    + " and port " + port);
        }
        
        // create Peer object and start the two-way communication
        try {
            ClientPeer client = new ClientPeer(sock);
            Thread streamInputHandler = new Thread(client);
            streamInputHandler.start();
            client.handleTerminalInput();
            client.shutDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    
} // end of class Client
