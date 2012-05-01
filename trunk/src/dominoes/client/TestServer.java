package dominoes.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public static int totalPlayers=3;
	public static String ErrorInvalidChip="ERROR_InvalidChip";
	public static String ErrorSkipTurn="OK_Skip";
	public static String ErrorEmptyChip="ERROR_EmptyChip";
	public static String OKchip="OK_Skip";
	
		public static void main (String args[]) {
			try{
				int serverPort = 7896; // the server port
				ServerSocket listenSocket = new ServerSocket(serverPort);
				
				while(true) {
					Socket clientSocket = listenSocket.accept();
					Connection c = new Connection(clientSocket);
				}
			} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
		}
		
		
	
}

class Connection extends Thread {
	
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public  int totalPlayers=7;
	public  int turnPlayer=3;
	public  int turn=3;
	public  int chip=6;
	public  String chipsPlayer="1,4,23,34,25,6";
	
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
				System.out.println("Subscription received");
				String reply="initGame_"+ turnPlayer+"_"+ totalPlayers+"_"+ chipsPlayer;
				out.writeUTF(reply);
				Thread.sleep(100);
				
				
				// send first chip
				//String playMsj="player"+0+"_"+ CHIP + "_" + "restoFichas";
				String playMsj="player"+0+"_"+ 13 + "_" + "restoFichas";
				out.writeUTF(playMsj);
				
				
				for(int pTurn=0; pTurn<turnPlayer; pTurn++ )
				{
						playMsj="player"+pTurn+"_"+ (14+pTurn);
						out.writeUTF(playMsj);
						//	playMsj="player"+0+"_"+ 14;
				}
				
				
				reply="ping"+turnPlayer;
				out.writeUTF(reply);
				data = in.readUTF();
				String [] dataParse=data.split("_");
				// validate ficha 	PlayMsj="player"+ playTurn+ "_"+selectedChip+"_"+selectedTrail+"_"+myChips.size();
				if(dataParse[1].length()<1){
					out.writeUTF(TestServer.ErrorEmptyChip+"_"+8); //TestServer.ErrorEmptyChip+"_"+NewChip)
				}else{
					out.writeUTF(TestServer.ErrorInvalidChip+"_"+dataParse[1]+"_"+dataParse[2]); //TestServer.ErrorInvalidChip+"_"+InvalidChip+"_"+Trail
				}
			
				data = in.readUTF();
				out.writeUTF(TestServer.OKchip);
				playMsj="player"+turnPlayer+"_"+ data.split("_")[1];
				out.writeUTF(playMsj);
				out.writeUTF("GAMEOVER");
				out.flush();
			}

		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
		

	}
}