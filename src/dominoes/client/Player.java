package dominoes.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import dominoes.server.DominoeChip;

public class Player {

	public static ArrayList <DominoeChip> Dominoes ;
	private static DataInputStream in;
	private static String playerName="Arianne";
	private static String startPlaymsj="startplay_"+ playerName;
	private static int playTurn;
	private static int fisrtChip;
	private static int totalPlayers;
	private static String [] chips;
	public static String ErrorInvalidChip="ERROR_InvalidChip";
	public static String ErrorSkipTurn="OK_Skip";
	public static String ErrorEmptyChip="ERROR_EmptyChip";
	public static String OKchip="OK_Skip";
	public static Stack<Integer> [] gameBoard;
	public static Vector <Integer> myChips = new Vector (); 
	int isValid=0;
	static int noValid=1;
	int validAndShifed=2;

	public static void main (String args[]) {
		Socket s = null;
		int serverPort = 7896;
		String data;
		String playMsj;
		generateInitalSetDominoes(6);
		
		try {
			s = new Socket("localhost", serverPort);
			in = new DataInputStream(s.getInputStream());
			DataOutputStream out =new DataOutputStream( s.getOutputStream());

			// subscribe to play
			out.writeUTF(startPlaymsj);
			data = in.readUTF();
			if(data.contains("initGame"))
			{
				initGame(data);
			}
			// wait the game starts 1st chip
			playMsj=in.readUTF();
			
			if(playMsj.contains("player"))
			{
				String [] broadCastmsj=playMsj.split("_");
				int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
				int playerChip=Integer.parseInt(broadCastmsj[1]);
				initGameBoard(playerNum,playerChip);
				System.out.println("init gameboard");
			}
			
			
			
			
			playMsj=in.readUTF();
			while (!playMsj.contains("GAMEOVER"))
			{

				if(playMsj.contains("ping" + playTurn ))
				{
					// send msj
					String replyMsj;
					String chipToplay=playRound();
					out.writeUTF(chipToplay);
					replyMsj=in.readUTF();
					do{
						if(replyMsj.contains("ERROR"))
						{

							if(replyMsj.contains(ErrorInvalidChip))
							{
								System.out.println(replyMsj);
								backTracking(replyMsj);
								chipToplay=playRound();
								out.writeUTF(chipToplay);
								replyMsj=in.readUTF();

							}else if(replyMsj.contains(ErrorEmptyChip))
							{
								System.out.println(replyMsj);
								// Read the chip sent, update player chips & play again
								int newChip=Integer.parseInt(replyMsj.split("_")[2]);
								updateSelfChips(newChip);
								chipToplay=playRound();
								out.writeUTF(chipToplay);
								replyMsj=in.readUTF();
							}

						}

					}while(!replyMsj.contains(OKchip) );
				}else if(playMsj.contains("player" + playTurn ))
				{
					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					updateGameBoard(playerNum,playerChip);
					System.out.println("update gameboard");

					// play game & No need to update gameboard
					System.out.println("play game & update gameboard");
				}else{
					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					updateGameBoard(playerNum,playerChip);
					System.out.println("update gameboard");
				}
				playMsj=in.readUTF();
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
		//fisrtChip=Integer.parseInt(initMsj[4]);
		System.out.println("Player " + playTurn + "Total Players: " + totalPlayers +" chips: " + chips.toString());
		gameBoard=new Stack [totalPlayers];
		for(int i=0; i<totalPlayers; i++)
		{
			gameBoard[i]= new Stack <Integer>();
		}
		for(int j=0; j<chips.length; j++)
		{
			myChips.add(Integer.parseInt(chips[j]));
		}
	}

	public static void updateGameBoard(int player, int chip){
		gameBoard[player].push(chip);
		// repaint
	}
	
	public static void initGameBoard(int player, int chip){
		for(int i=0; i<totalPlayers; i++ ){
			gameBoard[i].push(chip);
		}
		System.out.println("Ficha Inicial--> " + printChip(chip));
		printGameBoard();
		// repaint
	}

	public static void updateSelfChips(int chip){
		myChips.add(chip);
		// repaint
	}

	public static void backTracking(String dataReply ){


		int myChip=Integer.parseInt(dataReply.split("_")[2]);
		int trail=Integer.parseInt(dataReply.split("_")[3]);
		//myChips.add(gameBoard[trail].pop());
		myChips.add(myChip);
		//repaint
	}
	public static String playRound() throws IOException{
		// player#_chip_trail_chipsTail
		int selectedChip;
		String selectedTrail;
		String PlayMsj;

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		System.out.println("Select the trail to play");
		selectedTrail=in.readLine();
		do{
		System.out.println("Select your chip to play");
		//for(int i=0; i<myChips.size(); i++ )System.out.print(i +":"+ myChips.get(i) + ",");
		printChips(myChips);
		selectedChip=Integer.parseInt(in.readLine());
		}while(validateChip(Integer.parseInt(selectedTrail),selectedChip)==noValid);
		if(selectedChip>=myChips.size())
		{
			PlayMsj="player"+ playTurn+ "_"+""+"_"+selectedTrail+"_"+myChips.size();
		}else{
			selectedChip=myChips.remove(selectedChip);
			PlayMsj="player"+ playTurn+ "_"+selectedChip+"_"+selectedTrail+"_"+myChips.size();

		}





		return PlayMsj;
	}
	
	public static int validateChip(int player, int chip){
		int isValid=0;
		int noValid=1;
		int validAndShifed=2;
		int previousChip=gameBoard[player].pop();
		DominoeChip prevChip=Dominoes.get(previousChip);
		DominoeChip newChip=Dominoes.get(chip);
		
		if(prevChip.getShifted()){
			if(prevChip.getChip0()== newChip.getChip0())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
			}else if(prevChip.getChip0()== newChip.getChip1())
			{
				Dominoes.remove(chip);
				newChip.setShifted(true);
				Dominoes.set(chip, newChip);
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return validAndShifed;
			}
		}else{
			if(prevChip.getChip1()== newChip.getChip0())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
			}else if(prevChip.getChip1()== newChip.getChip1())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				Dominoes.remove(chip);
				newChip.setShifted(true);
				Dominoes.set(chip, newChip);
				return validAndShifed;
			}
		}
		
		gameBoard[player].push(previousChip);
		return noValid;
	}

	public static void generateInitalSetDominoes (int size)
	{
		System.out.println ("generateInitalSetDominoes : size max of " + size);
		int numChip = 0 ;
		int x  =0 ;
		int  y=0;
		Dominoes  = new ArrayList<DominoeChip> ();

		for (x = 0; x <= size ; x ++)
		{
			for (; y <= size ; y ++)
			{
				DominoeChip chip = new DominoeChip (x,y, numChip);
				if (x == y )
				{
					chip.setMula();
				}
				numChip++;
				Dominoes.add(chip);
			}
			y=x+1;
		}
	}


	public static void printChips(Vector <Integer> myChips){
		
		for(int i=0; i<myChips.size(); i++){
			System.out.print(i +":" +printChip(myChips.get(i)) + " , ");
		}
	}
	
	public static String printChip(int chip){
		return  Dominoes.get(chip).getChip0() + "|" +  Dominoes.get(chip).getChip1();
	}

	public static void printGameBoard(){
		for(int i=0; i<totalPlayers; i++)
		{
			for(int j=0; j<gameBoard[i].size(); j++)
			{
				System.out.print(printChip(gameBoard[i].get(j)) + " , ");
			}
			System.out.println();
		}
	}


}