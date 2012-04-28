package dominoes.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Player {

	private static DataInputStream in;
	private static String playerName="Arianne";
	private static String startPlaymsj="startplay_"+ playerName;
	private static int playTurn;
	private static int fisrtChip;
	private static int totalPlayers;
	private static String [] chips;

	public static void main (String args[]) {
		Socket s = null;
		int serverPort = 7896;
		String data;
		String playMsj;

		try {
			s = new Socket("localhost", serverPort);
			in = new DataInputStream(s.getInputStream());
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
			
			// subscribe to play
			out.writeUTF(startPlaymsj);
			data = in.readUTF();
			if(data.contains("initGame")){
				initGame(data);
			}
			// wait the game starts
			playMsj=in.readUTF();
			while (!playMsj.contains("GAMEOVER")){
				playMsj=in.readUTF();
				if(playMsj.contains("player" + playTurn )){
					// play game & update gameboard
					System.out.println("play game & update gameboard");
				}else{
					System.out.println("just update gameboard");
				}
			}
			
			System.out.println("Game Oveer");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{ try {s.close();}catch (IOException e){/*close failed*/}}


	}



	public static void initGame(String data){

		String [] initMsj=data.split("_");
		playTurn=Integer.parseInt(initMsj[1]);
		totalPlayers=Integer.parseInt(initMsj[2]);
		chips=initMsj[3].split(",");
		fisrtChip=Integer.parseInt(initMsj[4]);
		System.out.println("Player " + playTurn + "Total Players: " + totalPlayers +" chips: " + chips.toString());





	}
}