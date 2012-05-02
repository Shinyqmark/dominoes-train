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
	
	public static boolean TrainPerTrack [] = null; 


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
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " we just got a ping message... let's start working ! ");

					// send msj
					String replyMsj;
					String chipToplay=playRound();
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " message  to play : " +chipToplay);

					out.writeUTF(chipToplay);
					replyMsj=in.readUTF();
					
					do{
						if(replyMsj.contains("ERROR"))
						{

							if(replyMsj.contains(ErrorInvalidChip))
							{
								System.out.println("ErrorInvalidChip > "+replyMsj);
								backTracking(replyMsj);
								chipToplay=playRound();
								
								System.out.println ("ThreadID " + Thread.currentThread().getId() + " message  to play in errorInvalid: " +chipToplay);
								out.writeUTF(chipToplay);
								replyMsj=in.readUTF();

							}else if(replyMsj.contains(ErrorEmptyChip))
							{
								System.out.println("ErrorEmptyChip > " + replyMsj);
								// Read the chip sent, update player chips & play again
								int newChip=Integer.parseInt(replyMsj.split("_")[2]);
								updateSelfChips(newChip);
								chipToplay=playRound();
								
								System.out.println ("ThreadID " + Thread.currentThread().getId() + " message  to play in ErrorEmpty: " +chipToplay);

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
					int trackAvailable = Integer.parseInt(broadCastmsj[4]);
					int trackFromPlayer = Integer.parseInt(broadCastmsj[5]);
					int isShifted = Integer.parseInt(broadCastmsj[6]);


					// play game & No need to update gameboard
					System.out.println(" Received broadCast : playerId : "+playerNum + " player Chip: "+playerChip + " trackAvailable : " + trackAvailable + " trackFromPlayer : " +trackFromPlayer +" isShifted: "+isShifted);
					//
					// no need to uptade the gameboard if the broadcast is from myself
					// 
					if (playerNum != playTurn)
					{

						if (playerChip != 999)
						{
							if (trackFromPlayer != playerNum)
							{
								System.out.println(" Player played in a different track.. update the proper variable ");
								playerNum = trackFromPlayer;
								updateShifted (playerNum, playerChip, isShifted);

								
							}
							updateGameBoard(playerNum,playerChip);
							System.out.println("update gameboard");
						}
						else
						{
							System.out.println(" no update due to chip 999 ");

						}
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
					int trackAvailable = Integer.parseInt(broadCastmsj[4]);
					int trackFromPlayer = Integer.parseInt(broadCastmsj[5]);
					int isShifted = Integer.parseInt(broadCastmsj[6]);

					
					System.out.println(" Message recevied : playerId : "+playerNum + " player Chip: "+playerChip + " trackAvailable : " + trackAvailable +" trackFromPlayer : " +trackFromPlayer +" isShifted: "+isShifted);
					
					if (playerChip != 999)
					{
						if (trackFromPlayer != playerNum)
						{
							System.out.println(" Player played in a different track.. update the proper variable and shifted ");
							playerNum = trackFromPlayer;
							
							updateShifted (playerNum, playerChip, isShifted);
							
						}
						updateGameBoard(playerNum,playerChip);
					}
					else
					{
						System.out.println(" no update due to chip 999 ");

					}
					
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
		System.out.println("updateGameBoard: player:  " + player +" chip: "+chip );

		gameBoard[player].push(chip);
		
		printGameBoard();
		
		//
		
		// repaint
	}
	
	public static void initGameBoard(int player, int chip){
		//
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
		System.out.println("updateSelfChips :  chip: "+chip );

		myChips.add(chip);
		
		//TODO borrar esta linea
		printChips(myChips);
		// repaint
	}

	public static void backTracking(String dataReply ){


		System.out.println("backTracking : dataReply : "+dataReply );

		int myChip=Integer.parseInt(dataReply.split("_")[2]);
		int trail=Integer.parseInt(dataReply.split("_")[3]);
		//myChips.add(gameBoard[trail].pop());
		gameBoard[trail].pop();
		myChips.add(myChip);
		
		//TODO borrar esta linea
		printChips(myChips);

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
		
		System.out.print("Size of your chips: "+myChips.size()+ " These are your chips : ");

		printChips (myChips);
		System.out.println(" ");

		printGameBoard();

		System.out.println("Select the trail to play");
		
		selectedTrail=in.readLine();
		
		printChips(gameBoard[Integer.parseInt(selectedTrail)]);
		
		do{
			
			System.out.println("Select your chip to play");
			//for(int i=0; i<myChips.size(); i++ )System.out.print(i +":"+ myChips.get(i) + ",");
			printChips(myChips);
			selectedChip=Integer.parseInt(in.readLine());
			if(selectedChip ==999)
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
			PlayMsj="player"+ playTurn+ "_"+"999"+"_"+selectedTrail+"_"+myChips.size();
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
	public static void updateShifted(int player, int chip, int shifted)
	{
		
		System.out.println("updateShifted: player: "+player + " chip: " + chip + " shifted : " + shifted );

		//int lastChip=gameBoard[player].pop();
		
		//getDominoesPlayerPosition(Dominoes, chip);
		// getDominoesPlayerPositionFromStack(gameBoard[player], chip); 
		//DominoeChip lastChip=Dominoes.get(chip);
		DominoeChip lastChip = getDominoeFromId(chip);
		if (lastChip == null)
		{
			System.out.println (" ************** > updateShifted () : completely unexpected!!!! Chip : "+chip+"  ****************");
		}
		
		System.out.println("updateShifted: foundChip: "+lastChip.getId() + " chip: " +lastChip.getChip0()+"|"+lastChip.getChip1() + " isShifted : "+ lastChip.getShifted());

		if (shifted != lastChip.getShifted())
		{
			lastChip.setShifted(shifted);
			System.out.println("updateShifted: After setting shifted: "+lastChip.getId() + " chip: " +lastChip.getChip0()+"|"+lastChip.getChip1() + " isShifted : "+ lastChip.getShifted());
		}
		else
		{
			System.out.println("updateShifted: no need to update shifted " );

		}
		
		


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
				System.out.println("validateChip: isValid - shifted = 1"  );

				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
				
			}
			else if(prevChip.getChip0()== newChip.getChip1())
			{
				System.out.println("validateChip: validAndShifed - shifted = 1"  );

				//Dominoes.remove(chip);
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
				System.out.println("validateChip: isValid - shifted = 0"  );

				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
			}
			else if(prevChip.getChip1()== newChip.getChip1())
			{
				System.out.println("validateChip: validAndShifed - shifted = 0"  );

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


	public static void printChips(Vector <Integer> myChips)
	{
		System.out.print("printChips Vector : ");

		for(int i=0; i<myChips.size(); i++)
		{
			int  temp = myChips.get(i);
			System.out.print(i +":");
			String rs = printChip(temp);
			System.out.print( rs + " , ");
			
		}
	}
	

	public static String getDominoesChips(Vector <Integer> myChips){
		String aux="";
		
		for(int i=0; i<myChips.size(); i++)
		{
			int  temp = myChips.get(i);
			aux.concat(temp + " , ");
	
		}
		return aux;
	
	}
	
	public static void printChips(Stack <Integer> myChips)
	{
		System.out.print("printChips Stack : ");
		
		for(int i=0; i<myChips.size(); i++)
		{
			int  temp = myChips.get(i);
			System.out.print(i +":");
			String rs = printChip(temp);
			System.out.print(rs + " , ");
		}
	}
	
	public static String printChip(int chip)
	{
		String result= "";
		
		DominoeChip temp = getDominoeFromId (chip);
		
		if (temp ==null)
		{
			System.out.println (" ************** > printChip () : completely unexpected!!!! Chip : "+ chip +"  **************** ");

		}
		
		result = temp.getChip0() + "|" +  temp.getChip1();
		
		return  result;
	}

	public static void printGameBoard(){
		System.out.println (" >> printGameBoard () :");

		//
		// again .. totalPlayer +1 because it includes the global track available for all the players
		// 
		for(int i=0; i<totalPlayers+1; i++)
		{
			System.out.print(" player : " + i + " SizeOfGameBoard : " + gameBoard[i].size() +" Chip : ");

			for(int j=0; j< gameBoard[i].size(); j++)
			{
				int temp = gameBoard[i].get(j);
				
				System.out.print("j:"+j +" ");
				String rs = printChip(temp);
				System.out.print(rs + " , ");
			}
			System.out.println();
		}
	}
	
	public static DominoeChip getDominoeFromId( int chipId )
	{
		DominoeChip returnChip =null;
		
		for (int x =0; x< Dominoes.size(); x++)
		{
			DominoeChip temp = Dominoes.get(x);
			if (temp.getId()==chipId)
			{
				returnChip = temp;
				break;
			}
		}
		if (returnChip == null)
		{
			System.out.print(" DominoeChip : This is pretty bad... what was the chipID " + chipId);

		}
		return returnChip;
		
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