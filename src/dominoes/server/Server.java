package dominoes.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

import dominoes.communication.Communication;


public class Server {

	public static ArrayList <DominoeChip> Dominoes ;
	public static int DominoesAssigned = 0;
	public static Stack<DominoeChip> Track[];
	public static Object lock = new Object();
	public static Object lockTrack = new Object();
	public static boolean TrainPerTrack [] = null; 
	public static int initialChip = -1;
	
	public static Communication communication = new Communication ();


	public static int maxPlayers;
	static int MaxChipsPerPlayer;
	
	public Server()
	{
		Dominoes = null;
		MaxChipsPerPlayer=7;
		maxPlayers = -1;
	}
	public void setMaxPlayers(int players)
	{
		maxPlayers = players;
	}
	
	public static int getFreeChip (int playerId)
	{
		//int chip = -1;
		int randomNumber = -1;
		
		synchronized (Server.lock)
		{
			if (DominoesAssigned >= Dominoes.size())
			{
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " chips are fully assigned.. nothing to do ");
			}
			else
			{
				while (true)
				{
					randomNumber = (int) (Math.random() * Dominoes.size());
					//System.out.println ("ThreadID " + Thread.currentThread().getId() + "generateDominoes : random number " + randomNumber);
		
					//
					// first check if the chip has not been assigned to another player
					if (Dominoes.get(randomNumber).getAssigned()==0)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "generateDominoes :new chip assgnied " + randomNumber);

						Dominoes.get(randomNumber).setAssigned();
						DominoesAssigned++;
						Dominoes.get(randomNumber).setPlayer(playerId);
						break;
						
					}
					else
					{
						//System.out.println ("ThreadID " + Thread.currentThread().getId() + " chip already assigned .. get next one");
		
					}
				}
			}
		}
		return randomNumber;
	}
	//
	// This function will generate a RANDOM set of chips per player
	//
	public static ArrayList <Integer>  generateDominoes (int playerId)
	{
		ArrayList <Integer> DominoesPlayer = new ArrayList<Integer> ();
		int randomNumber = 0;
		synchronized (Server.lock)
		{
			for (int i = 0 ; i < MaxChipsPerPlayer ; i++)
			{
				randomNumber = (int) (Math.random() * Dominoes.size());
				//System.out.println ("ThreadID " + Thread.currentThread().getId() + "generateDominoes : random number " + randomNumber);
	
				//
				// first check if the chip has not been assigned to another player
				if (Dominoes.get(randomNumber).getAssigned()==0)
				{
					Dominoes.get(randomNumber).setAssigned();
					DominoesAssigned++;
					Dominoes.get(randomNumber).setPlayer(playerId);
					DominoesPlayer.add(randomNumber);
					
				}
				else
				{
					//System.out.println ("generateDominoes : entry  " + randomNumber + " already chosen ... next!");
					i--;
	
				}
				
			}
		}
		
	//	printDominoePerPlayerList(DominoesPlayer);
		
		return DominoesPlayer;
		
	}
	
	
	//
	// This function creates the initial set of the dominoes
	// Initially the dominoes is SORTED.. 
	// Size is the max size of the dominoes\
	//
	public void generateInitalSetDominoes (int size)
	{
		System.out.println ("generateInitalSetDominoes : size max of " + size);
		int numChip = 0 ;
		int x  =0 ;
		int  y=0;
		synchronized (Server.lock)
		{
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
		
	}
	public void printFreeChips()
	{
		int totalFree = 0;
		synchronized (Server.lock)
		{
			for (int x = 0 ; x < Dominoes.size(); x++)
			{
				DominoeChip  temp = Dominoes.get(x);
				if (temp.getAssigned()==0)
				{
					System.out.println ("Free Chip : Chip Id: " +temp.getId()+ " Num_0 : "+temp.getChip0()+ " Num_1: " + temp.getChip1());
					totalFree ++;
				}
			}
			System.out.println ("Total Free " + totalFree);

		}
	}
	//
	// This function is itself explanatory
	public void printDominoeList ()
	{
		synchronized (Server.lock)
		{
			for (int x = 0 ; x < Dominoes.size(); x++)
			{
				DominoeChip  temp = Dominoes.get(x);
				System.out.println ("Chip Id: " +temp.getId()+ " Num_0 : "+temp.getChip0()+ " Num_1: " + temp.getChip1() + " player Id :"+ temp.getPlayerId());
				if (temp.getMula()==1)
				{
					System.out.println ("Ficha es mula ");

				}
			}
		}
	}
	
	public static void printDominoePerPlayerList ( ArrayList <Integer> DominoesPlayer)
	{
		synchronized (Server.lock)
		{
			for (int x = 0 ; x < DominoesPlayer.size(); x++)
			{
				int  temp = DominoesPlayer.get(x);
				System.out.print ("ThreadID  : "+Thread.currentThread().getId()+"printDominoePerPlayerList : Chip Id: " +temp + " ");
				Dominoes.get(temp).pintChip();
			}
		}
	}
	
	public int getInitialChip()
	{
		int chip= -1;
		DominoeChip  temp = null;
		synchronized (Server.lock)
		{
		
			for (int x = Dominoes.size()-1 ; x >= 0 ; x--)
			{
				  temp = Dominoes.get(x);
				if (temp.getMula()==1)
				{
					if (temp.getPlayerId()>= 0 && temp.getAssigned() ==1)
					{
						System.out.println ("ThreadID  : "+Thread.currentThread().getId()+" La ficha " + temp.getId() + " mula de  " + temp.getChip0()+ "_"+temp.getChip1() + " return value " +x );

						//
						// This means that a player has this chip assigned..
						// Lets start with him
						chip = x;
						break;
					}
				}
	
			}
		}
		return chip;
		
	}
	
	public void setChipInAllTracks (DominoeChip chip)
	{
		//
		// since we have a global track for a/l the players... 
		// the latest one will be the one
		//
		for (int x = 0; x < maxPlayers+1 ; x ++)
		{
			setChipInTrack (chip, x);
		}
	}
	public void setChipInTrack (DominoeChip chip, int track)
	{
		synchronized (lockTrack)
		{
			Track[track].push(chip);
		}
	}
	
	
	public static ArrayList <TrackingValue> getValuesAvailable(int track)
	{
		ArrayList <TrackingValue>  returnValue= new ArrayList <TrackingValue> ();
		
		TrackingValue track_value = null;
		int value=  -1;
		
		//
		//  first lets check the players track...
		//
		value = getValueInTrack (track);
		track_value = new TrackingValue (track,value);
		System.out.println ("ThreadID  : "+Thread.currentThread().getId()+" the track " + track + " has the value " + value + " add it to the list");
		returnValue.add(track_value);
		
		value = getValueInTrack (maxPlayers);
		track_value = new TrackingValue (maxPlayers,value);
		System.out.println ("ThreadID  : "+Thread.currentThread().getId()+" the track (global) " + maxPlayers + " has the value " + value + " add it to the list");
		returnValue.add(track_value);
		
		for (int t = 0 ; t < Server.TrainPerTrack.length ; t++)
		{
			if (Server.TrainPerTrack[t] == true && (t != track))
			{
				value = getValueInTrack (t);
				track_value = new TrackingValue (t,value);
				System.out.println ("ThreadID  : "+Thread.currentThread().getId()+" the track (open track) " + t + " has the value " + value + " add it to the list");
				returnValue.add(track_value);
			}
		}

		//
		// here get the list of all the possible tracks....
		
		return returnValue;
	}
	
	public static int getValueInTrack (int track)
	{
		int value = -1;
		DominoeChip chipTrack = getChipInTrack (track);
		if (chipTrack.getShifted() == 0 )
		{
			//
			// This is how it should be
			value = chipTrack.getChip1();
		}
		else
		{
			value = chipTrack.getChip0();
		}
		return value;
	}
	public static DominoeChip getChipInTrack(int track)
	{
		DominoeChip chip = null;
		synchronized (lockTrack)
		{
			chip = Track[track].peek();
		}
		
		return chip;
	}
	public void printAllTracks()
	{
		for (int x = 0; x < maxPlayers ; x ++)
		{
			printTrack (x);
		}
	}
	public static void printTrack(int track)
	{
		for (int x = 0 ; x< Track[track].size(); x ++)
		{
			DominoeChip chip = Track[track].get(x);
			System.out.println ("ThreadID  : "+Thread.currentThread().getId()+"  printTrack : Chip Id: " + chip.getId() + " value: " +chip.getChip0()+"_"+chip.getChip1()+ " Track " + track);

		}
	}
	
	public DominoeChip getInitialChip (int chip)
	{
		return Dominoes.get(chip);
	}
	public int getPlayerWithChip (int chip)
	{
		int player = 0;
		
		synchronized(Server.lock)
		{
			DominoeChip  temp = Dominoes.get(chip);
			player = temp.getPlayerId();
			
			System.out.println ("ThreadID  : "+Thread.currentThread().getId()+" chipID  " + temp.getId() + "  playerID   " + temp.getPlayerId()  );


		}
		
		
		return player;
	}
	public int getNextPlayer (int currentPlayer)
	{
		int nextPlayer = -1;
		if ((currentPlayer + 1) >= maxPlayers )
		{
			nextPlayer = 0;
			
		}
		else
		{
			nextPlayer = currentPlayer;
			nextPlayer++;
		}
		return nextPlayer;
	}
	
	public  static void printTrackValues (ArrayList <TrackingValue> list)
	{
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " Print Track list  .. size of " + list.size());

		for (int x = 0 ; x < list.size(); x ++)
		{
			TrackingValue temp = list.get(x);
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Track: " + temp.getTrack() + " value : " + temp.getValue() );

		}
	}
	
	
	public static String getDominoesChips(ArrayList <Integer> myChips){
		String aux="";
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " getDominoesChips: size of the list " + myChips.size() );

		for(int i=0; i<myChips.size(); i++)
		{
			int temp = myChips.get(i);
			aux+=Integer.toString (temp) +",";
		//	aux.concat(myChips.get(i).toString() + ",");
	
		}
		return aux;
	
	}
	
	public static class Server_player implements Runnable {

		private int idPlayer;

		private int isReadyToStart ;
		private boolean moveDone;
		public static String ErrorInvalidChip="ERROR_InvalidChip";
		public static String ErrorSkipTurn="OK_Skip";
		public static String ErrorEmptyChip="ERROR_EmptyChip";
		public static String OKchip="OK_Skip";
		
	
		public Server_player (int id)
		{
			idPlayer = id;
			isReadyToStart = 0;
			moveDone = false;
			
			
			
		}
		public void setMoveDone (boolean move)
		{
			moveDone = move;
		}
		public boolean getMoveDone()
		{
			return moveDone;
		}
		
		public int getIsReadyToStart()
		{
			return isReadyToStart;
		}
		public int parseChipResponse (String msg, int value)
		{
			int result=-1;
			
			String temp [];
			temp = msg.split("_");
			
			result = Integer.parseInt(temp[value]);
			return result;
		}
		public int getDominoesPlayerPosition (ArrayList<Integer> DominoesPlayer, int chipId)
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
		
		public String validateTrackValues (ArrayList <TrackingValue> trackValues , DominoeChip playerChip, int trackFromPlayer, int isShifted)
		{
			String msg = "";
			
			
			for (int op = 0 ; op < trackValues.size(); op++)
			{
				TrackingValue temp = trackValues.get(op);
				if (temp.getTrack() == trackFromPlayer)
				{
					if ( isShifted ==1)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " Shifted = 1 :  Chip  idChip [1]" + playerChip.getId() + " value "+playerChip.getChip0()+"_"+ playerChip.getChip1());

						if (temp.getValue() == playerChip.getChip1())
						{
							msg = OKchip;
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " Good chip  idChip [1]" + playerChip.getId() + " value "+playerChip.getChip0()+"_"+ playerChip.getChip1());

						}
						else
						{
							msg = ErrorInvalidChip +"_"+playerChip.getId()+"_"+trackFromPlayer;
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " wrong chip : "+ playerChip.getId()+"return error ");

						}
						
					}
					else 
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " Shifted = 0 :  Chip  idChip [1]" + playerChip.getId() + " value "+playerChip.getChip0()+"_"+ playerChip.getChip1());

						if (temp.getValue() == playerChip.getChip0())
						{
							msg = OKchip;

							System.out.println ("ThreadID " + Thread.currentThread().getId() + " Good chip  idChip [0] " + playerChip.getId() + " value "+playerChip.getChip0()+"_"+ playerChip.getChip1());

						}
						else
						{
							msg = ErrorInvalidChip +"_"+playerChip.getId()+"_"+trackFromPlayer;

							System.out.println ("ThreadID " + Thread.currentThread().getId() + " wrong chip : "+ playerChip.getId()+"return error ");

						}
					}
				}// if trackPlayer == trackValues
			}// for trackingValues
			
			return msg;
		}
		@Override
		public void run() {

			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player name : " + idPlayer + " waiting for the game to start ");
			
			//
			// first things first.. lets wait till we have all the players ready... 
			//
			synchronized (this)
			{
				try {
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player name : " + idPlayer + " lets wait ");

					wait();	
				} catch (InterruptedException e1) {e1.printStackTrace();}
			}
			
			//
			// TODO : se necesita saber cuantos jugadores hay ..
			// 
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player name : " + idPlayer + " After wait ");

			
			//
			// Now we know we are ready to start playing
			// Lets get and send the chips to each player.
			//
			ArrayList <Integer> DominoesPlayer = Server.generateDominoes( idPlayer);
			
			
			Server.printDominoePerPlayerList(DominoesPlayer);
			//
			String listDominoes = Server.getDominoesChips(DominoesPlayer);
			String reply="initGame_"+ idPlayer+"_"+ Server.maxPlayers+"_"+ listDominoes;
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player name : " + idPlayer + " msg to be sent  " + reply);
			
			Server.communication.sendMessage(reply, idPlayer);

	/*		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {e.printStackTrace();} 
			*/
			
			System.out.println ("ThreadID " + Thread.currentThread().getId() + "  Set to isReady now ");

			isReadyToStart =1;
		
			//
			// Now each player should wait for their turn
			// server will call them all
			///
			boolean needsToWait = true;
			
			while (true)
			{
				if (needsToWait)

				{
					synchronized (this)
					{
						try {
							System.out.println ("ThreadID " + Thread.currentThread().getId() + "   Let's wait my turn ");
							wait();	
							moveDone = false;
						} catch (InterruptedException e1) {e1.printStackTrace();}
					}
				}
				System.out.println ("ThreadID " + Thread.currentThread().getId() + "   Now is my turn... Size of my chips " +DominoesPlayer.size() +" lets communicate with my player ");
				
				//
				// 0.5 .. if the player has the initial chip.. he does not have any option at all
				//
				if ( DominoesPlayer.contains(Server.initialChip) )
				{
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "  This is the first move with the initial chip.. ");
					int deleteChip = DominoesPlayer.indexOf(initialChip);
					DominoesPlayer.remove(deleteChip);
					//
					// send broadcast to all the players..
					//
					String playMsj="player"+idPlayer+"_"+ Server.initialChip + "_" + DominoesPlayer.size() + "_" + (Dominoes.size()-Server.DominoesAssigned +"_0"); // broadcast
			
					try {
						Server.communication.broadCast(playMsj);
					} catch (IOException e) {e.printStackTrace();}

				}
				else 
				{	
					int z = 0;
					boolean foundChip = false;

					DominoeChip playerChip = null;
					//
					// 1 . Communicate player saying .. "it's your turn"
					//
					String msg="ping"+idPlayer;
					String msgFromPlayer="";
					
					if (needsToWait== true)
					{
						Server.communication.sendMessage(msg, idPlayer);
					}
					msgFromPlayer = Server.communication.receiveMessage(idPlayer);

					
					int chipFromPlayer = parseChipResponse (msgFromPlayer,1) ;
					int trackFromPlayer = parseChipResponse (msgFromPlayer,2);
					int remainingChips = parseChipResponse (msgFromPlayer,3);
					int isShifted = parseChipResponse (msgFromPlayer,4);

					System.out.println ("ThreadID " + Thread.currentThread().getId() + "  message  parsed chipFromPlayer: "+chipFromPlayer + " trackFromPlayer: " +trackFromPlayer+" remaining: "+remainingChips +" isShifted: "+isShifted );

					// 2. Wait for their chip...or empty
					//
					
					if (chipFromPlayer == 999 )
					{
						foundChip =false;
					}
					else
					{
						playerChip = Dominoes.get(chipFromPlayer);
						//int  trackValue  = Server.getValueInTrack(idPlayer);
						ArrayList <TrackingValue> trackValues = Server.getValuesAvailable(idPlayer);
						
						Server.printTrackValues(trackValues);
						String msgOutput = validateTrackValues (trackValues, playerChip,trackFromPlayer,isShifted);
						if (msgOutput.contains(OKchip))
						{
							foundChip =true;
							
							if (isShifted == 1)
							{
								playerChip.setShifted(isShifted);
								System.out.println ("ThreadID " + Thread.currentThread().getId() + "  shifted the chip ");

							}
						}
					}
					// 3. Validate chip... needs to follow the rules
					//
								
					if (foundChip)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  remove chip "+playerChip.getId()+" from player's list.. but add it to the track one ");

						// original
						//Server.Track[trackValue.getTrack()].push(playerChip);
						
						printDominoePerPlayerList (DominoesPlayer);
						
						Server.Track[trackFromPlayer].push(playerChip);
						
						printTrack (trackFromPlayer);
						
				
						z = getDominoesPlayerPosition (DominoesPlayer, playerChip.getId());
						
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  find chip in position "+z);

						DominoesPlayer.remove(z);
						
						if (DominoesPlayer.size() == 0 )
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " The player ID " + idPlayer + "has WON !!!!! " );
							//
							// send the proper messages
							// 
						}
						
						// if player has already set "train" in their track... now he can remove it
						//
						if ( Server.TrainPerTrack[idPlayer] == true)
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " The player had set their track.. set it back to false ");

							Server.TrainPerTrack[idPlayer] = false;
						}

						needsToWait = true;
						
						Server.communication.sendMessage(OKchip,  idPlayer);
						
						String playMsj="player"+idPlayer+"_"+playerChip.getId() + "_" + DominoesPlayer.size() + "_" + (Dominoes.size()-Server.DominoesAssigned) +"_0"; 
						
						try {
							Server.communication.broadCast(playMsj);
						} catch (IOException e) {e.printStackTrace();}


					}
					else
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  It does not have any chips.. request one more ");

						if (needsToWait == true)
						{	
							
							int returnFreeChip = getFreeChip (idPlayer); 
							if (returnFreeChip >= 0)
							{
								DominoesPlayer.add(returnFreeChip);
								Server.communication.sendMessage(ErrorEmptyChip+"_"+returnFreeChip, idPlayer);
								//
								// TODO : new broadcast msg
								//		Server.communication.sendMessage(ErrorEmptyChip+"_"+returnFreeChip, idPlayer);

								
							}
						}
						else
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " since this player has already asked for a chip.. no need to get a new one");
							Server.communication.sendMessage(OKchip, idPlayer);
							String playMsj="player"+idPlayer+"_999_" + DominoesPlayer.size() + "_" + (Dominoes.size()-Server.DominoesAssigned) +"_1"; 
							
							try {
								Server.communication.broadCast(playMsj);
							} catch (IOException e) {e.printStackTrace();}

						}
						
						//
						// this means is the first time that has not waited... let's give just one chance
						//
						if ( needsToWait )
						{
							needsToWait = false;
						}
						else
						{
							//
							// set "train" as an open track for all the players...
							//
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " playerID : " + idPlayer + " set their train track flag ");

							Server.TrainPerTrack[idPlayer] = true;
							
							needsToWait = true;

						}
					}// else
					
					/////***************************************
	
				}// else initial chip
				if (needsToWait == true)
				{
					moveDone = true;
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "   move done  .. ");

				}
				else
				{
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "  this player is going to repeat the move ");

				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
			}// while true loop
		}// function run 
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int maxPlayers = 0;
		int x = 0;
		Server server = new Server();
		server.generateInitalSetDominoes(6);
		ArrayList <Server_player> players  = new ArrayList<Server_player> ();
		
		

		try{
			int serverPort = 9997; // the server port
			ServerSocket listenSocket = new ServerSocket(serverPort);
			
			while(x <2) 
			{
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " Waiting for a conection ");

				Socket clientSocket = listenSocket.accept();
				
				Server.communication.addConnection(clientSocket);
				Server_player temp = new Server_player(x);
				players.add(temp);
				new Thread (temp).start();
				//players.add(new Server_player(x));
				//new Thread (players.get(x)).start();
				
				x++;
			}
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
		
		maxPlayers = x;
		
		server.TrainPerTrack = new boolean [maxPlayers];
		server.Track = new Stack[maxPlayers+1];
		server.setMaxPlayers(maxPlayers);
		
		for (int wx = 0 ; wx < maxPlayers ; wx ++)
		{
			server.Track[wx] = new Stack<DominoeChip>();
			server.TrainPerTrack[wx] = false;
			
		}
		
		server.Track[maxPlayers] = new Stack<DominoeChip>();

		
		//
		// create a new trck used for all players "global"
		//
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int w = 0 ; w < x; w++ )
		{
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " before synchronized " + w);

			synchronized (players.get(w))
			{
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " notify player " + w);

				players.get(w).notify();
			}
		}
		
		//
		// At this point... the players should have already communicated to each other..
		// Let's check if they are ready to rock and roll
		//
		boolean wait = true ;
		boolean ready = false;
		while (wait)
		{
			for (int xy = 0 ; xy < maxPlayers; xy++ )
			{
				synchronized (players.get(xy))
				{
					//
					// if any of the players is not ready... we need to wait 
					//
					if (players.get(xy).getIsReadyToStart()==0)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " player  " + xy + " not ready yet");
						ready = false;
						break;

					}// if getIsReadyToStart
					else
					{
						ready=true;
					}
				} // synchronized
			}// for loop
			if (ready == false)
			{
			
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " Sleep ");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println ("ThreadID " + Thread.currentThread().getId() + " Ready to go ");
				wait = false;

			}
		} // while loop
		
		//
		// now we know all the players are ready..
		// Start checking who and which chip will be the first one..
		//
		
	
		//
		//server.printFreeChips();
	//	server.printDominoeList();
		int initialChip = server.getInitialChip();
		if (initialChip==-1)
		{
			initialChip =0;
		}
		int playerMove = server.getPlayerWithChip(initialChip);
		DominoeChip initChip = server.getInitialChip(initialChip);
		
		server.initialChip = initialChip;
		server.setChipInAllTracks(initChip);

		//server.printAllTracks();
		boolean isPlaying = true;
		boolean flag = false;
		
		while (isPlaying)
		{
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " It's time for player " +playerMove + " to play " );

			synchronized (players.get(playerMove))
			{
				flag = false;
				players.get(playerMove).notify();
			}
				while (!flag)
				{
					
					
					try {
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " main thread will wait ");
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();}
					
					synchronized (players.get(playerMove))
					{
						flag = players.get(playerMove).getMoveDone();
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " Flag value " + flag);
					}
				}
				playerMove = server.getNextPlayer(playerMove);

			//}

		}
		
		//
		// At this point we have the players, initial chip and Who has that chip..
		// good enough to start with the game...
		//

		
	}

}
