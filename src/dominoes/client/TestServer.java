package dominoes.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public static int totalPlayers=3;
	
		public static void main (String args[]) {
			try{
				int serverPort = 7896; // the server port
			//	ConnectionsHandle connectionHAndle= new ConnectionsHandle();
				ServerSocket listenSocket = new ServerSocket(serverPort);
				
				while(true) {
					Socket clientSocket = listenSocket.accept();
				//	requestQueue.add(clientSocket);
					Connection c = new Connection(clientSocket);
				}
			} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
		}
		
		
	
}

class Connection extends Thread {
	
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public  int totalPlayers=3;
	public  int turnPlayer=3;
	public  int turn=3;
	public  int chip=6;
	public  String chipsPlayer="1,2,3,4,5,6";
	
	public Connection (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	public void run(){
		try {			                 // an echo server
			String data="";
			while (true){
				data = in.readUTF();	 // read a line of data from the stream
				System.out.println("Recibe mensaje");
				String reply="initGame_"+ turnPlayer+"_"+ totalPlayers+"_"+ chipsPlayer+"_"+"4";
				out.writeUTF(reply);
				for(int pTurn=0; pTurn<6; pTurn++ ){
					String playMsj="player"+pTurn+"_"+ chip;
					out.writeUTF(playMsj);
				}
				out.writeUTF("GAMEOVER");
				out.flush();
			}

		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
		

	}
}