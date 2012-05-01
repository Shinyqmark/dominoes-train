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
		int serverPort = 9997;
		String data;
		String playMsj;
		generateInitalSetDominoes(6);
		
		try {
			s = new Socket("localhost", serverPort);
			in = new DataInputStream(s.getInputStream());
			
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Socket and dataInput started ");

			

			// subscribe to play
			//out.writeUTF(startPlaymsj);
			data = in.readUTF();
			
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " message received :  " + data);

			
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
				
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " player message received :  playerNum: " + playerNum + " playerChip : " +playerChip );

				
				initGameBoard(playerNum,playerChip);
				System.out.println("init gameboard");
				
				//
				// IF this player has the initial chip.. then he needs to remove it from his list...
				// 
				if (playerNum ==playTurn)
				{
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " I'm the one with the initial chip... delete it from my bucket" );

					int z = getDominoesPlayerPosition (myChips,playerChip);
					myChips.remove(z);

				}
			}
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
			
			
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " now wait for next message ");

			

			while (!playMsj.contains("GAMEOVER"))
			{
				
				playMsj=in.readUTF();

				System.out.println("Msg received from Server : " + playMsj);


				if(playMsj.contains("ping" + playTurn ))
				{
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "we just got a ping message... let's start working ! ");

					// send msj
					String replyMsj;
					String chipToplay=playRound();
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "Chip to play : " +chipToplay);

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
					
					//
					// remove chip from our list
					//
				}
				else if(playMsj.contains("player" + playTurn ))
				{
					

					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					// play game & No need to update gameboard
					System.out.println(" Received broadCast : playerId : "+playerNum + " player Chip: "+playerChip);
					//
					// no need to uptade the gameboard if the broadcast is from myself
					// 
					if (playerNum != playTurn)
					{
						
						updateGameBoard(playerNum,playerChip);
						System.out.println("update gameboard");
					}
					else
					{
						System.out.println("No need to update the gameboard since the broadcast comes from myself ");

					}

					
				}
				else
				{
					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					
					System.out.println(" Message recevied : playerId : "+playerNum + " player Chip: "+playerChip);

					updateGameBoard(playerNum,playerChip);
					
				}
				//playMsj=in.readUTF();
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

		System.out.println("Player " + playTurn + " Total Players: " + totalPlayers );
		
		//
		// game board is players + 1 because it includes the Global Track
		//
		gameBoard=new Stack [totalPlayers+1];
		for(int i=0; i<totalPlayers+1; i++)
		{
			
			gameBoard[i]= new Stack <Integer>();
		}
		
		System.out.print("initGame : Chips " );

		for(int j=0; j<chips.length; j++)
		{
			System.out.print(" : "+ chips[j]);

			myChips.add(Integer.parseInt(chips[j]));
		}
		System.out.println(" ");

	}

	public static void updateGameBoard(int player, int chip){
		gameBoard[player].push(chip);
		// repaint
	}
	
	public static void initGameBoard(int player, int chip){
		//RJUA
		// totalPlayer +1 ... this is because the Global track..
		//
		for(int i=0; i<totalPlayers+1; i++ ){
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
		gameBoard[trail].pop();
		myChips.add(myChip);
		//repaint
	}
	public static String playRound() throws IOException{
		// player#_chip_trail_chipsTail
		int selectedChip;
		String selectedTrail;
		String PlayMsj;
		int returnValid =-1;

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		
		
		//printChips (myChips);
		printGameBoard();

		System.out.println("Select the trail to play");
		
		selectedTrail=in.readLine();
		
		printChips(gameBoard[Integer.parseInt(selectedTrail)]);
		
		do{
			
			System.out.println("Select your chip to play");
			//for(int i=0; i<myChips.size(); i++ )System.out.print(i +":"+ myChips.get(i) + ",");
			printChips(myChips);
			selectedChip=Integer.parseInt(in.readLine());
			if(selectedChip ==99)
			{
				System.out.println(" User does not have a valid chip... let's request one.. ");
				//
				// isValid = 0
				//
				returnValid = 0;

			}
			else
			{
				returnValid = validateChip(Integer.parseInt(selectedTrail),myChips.get(selectedChip));
			}
			
			System.out.println(" The validateChip function returned : "+ returnValid);

		}while(returnValid==noValid);
	
		if(selectedChip>=myChips.size())
		{
			PlayMsj="player"+ playTurn+ "_"+"-1"+"_"+selectedTrail+"_"+myChips.size();
		}
		else
		{
			selectedChip=myChips.remove(selectedChip);
			PlayMsj="player"+ playTurn+ "_"+selectedChip+"_"+selectedTrail+"_"+myChips.size();

		}
		
		//
		// 	validateChip is going to return  2 if the chip is shifted...
		//  add the proper flag
		if (returnValid == 2)
		{
			PlayMsj+="_1";
		}
		else
		{
			PlayMsj+="_0";
		}


		System.out.println(" playRound : message to return  "+ PlayMsj);



		return PlayMsj;
	}
	
	public static int validateChip(int player, int chip){
		int isValid=0;
		int noValid=1;
		int validAndShifed=2;
		int previousChip=gameBoard[player].pop();
		DominoeChip prevChip=Dominoes.get(previousChip);
		DominoeChip newChip=Dominoes.get(chip);
		
		System.out.println("validateChip: player: "+player + " chip: " + chip + " value : "+newChip.getChip0()+"|"+newChip.getChip1());

		System.out.println("validateChip: prevChip "+ +prevChip.getChip0()+"|"+prevChip.getChip1() +" getShifted : " + prevChip.getShifted()  );

		if(prevChip.getShifted()==1)
		{
			if(prevChip.getChip0()== newChip.getChip0())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
				
			}
			else if(prevChip.getChip0()== newChip.getChip1())
			{
				Dominoes.remove(chip);
				newChip.setShifted(1);
				Dominoes.set(chip, newChip);
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return validAndShifed;
			}
		}
		else
		{
			if(prevChip.getChip1()== newChip.getChip0())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
			}
			else if(prevChip.getChip1()== newChip.getChip1())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				//Dominoes.remove(chip);
				newChip.setShifted(1);
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
		String aux="";
		
		for(int i=0; i<myChips.size(); i++){
			//aux.concat(str)
			System.out.print(i +":" +printChip(myChips.get(i)) + " , ");
			
		}
	}
	

	public static String getDominoesChips(Vector <Integer> myChips){
		String aux="";
		
		for(int i=0; i<myChips.size(); i++){
			aux.concat(myChips.get(i) + " , ");
	
		}
		return aux;
	
	}
	
	public static void printChips(Stack <Integer> myChips){
		
		for(int i=0; i<myChips.size(); i++){
			System.out.print(i +":" +printChip(myChips.get(i)) + " , ");
		}
	}
	
	public static String printChip(int chip){
		return  Dominoes.get(chip).getChip0() + "|" +  Dominoes.get(chip).getChip1();
	}

	public static void printGameBoard(){
		System.out.println (" >> printGameBoard () :");

		//
		// again .. totalPlayer +1 because it includes the global track available for all the players
		// 
		for(int i=0; i<totalPlayers+1; i++)
		{
			System.out.print(" player : " + i + " Chip :");

			for(int j=0; j< gameBoard[i].size(); j++)
			{
				System.out.print(printChip(gameBoard[i].get(j)) + " , ");
			}
			System.out.println();
		}
	}
	
	public static int getDominoesPlayerPosition (Vector<Integer> DominoesPlayer, int chipId)
	{
		int position =-1;
		
		for (int x =0; x< DominoesPlayer.size(); x++)
		{
			int temp = DominoesPlayer.get(x);
			if (temp==chipId)
			{
				position = x;
				break;
			}
		}
		return position;
	}


}