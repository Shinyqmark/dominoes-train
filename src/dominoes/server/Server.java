package dominoes.server;
import java.util.ArrayList;
import java.util.Stack;


public class Server {

	public static ArrayList <DominoeChip> Dominoes ;
	public static int DominoesAssigned = 0;
	public static Stack<DominoeChip> Track[];
	public static Object lock = new Object();
	public static Object lockTrack = new Object();
	public static boolean TrainPerTrack [] = null; 
	public static int initialChip = -1;


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
	public void printTrack(int track)
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
	
	
	public static class Server_player implements Runnable {

		private int idPlayer;
		private String player;
		private int isReadyToStart ;
		private boolean moveDone;
		
	
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
		@Override
		public void run() {

			System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player name : " + idPlayer + " waiting for the game to start ");
			
			//
			// first things first.. lets wait till we have all the players ready... 
			//
			synchronized (this)
			{
				try {
	
					wait();	
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
			//Server.printDominoePerPlayerList(DominoesPlayer);
			//
			// TODO : send chips to the player
			// ie. communicate.sendMessage();
			//
			//
			// TODO : After the communication was successful set isreadyTostart to 1 
			//
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
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
				}
				else 
				{	
					int z = 0;
					//
					// 1 . Communicate player saying .. "it's your turn"
					//
					// 2. Wait for their chip...or empty
					//
					// 3. Validate chip... needs to follow the rules
					//
					DominoeChip playerChip = null;
					//int  trackValue  = Server.getValueInTrack(idPlayer);
					ArrayList <TrackingValue> trackValues = Server.getValuesAvailable(idPlayer);
					
					Server.printTrackValues(trackValues);
					
					boolean loopTracking = true;
					int getValue = 0;
					boolean foundChip = false;
					TrackingValue trackValue = null;

					
					while (loopTracking)
					{
						 trackValue = trackValues.get(getValue);
						
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  value to find " + trackValue.getValue());
		
						//RJUA
						// this is just for testing..  delete it on the final version
						// ****************************************************
						for ( z = 0 ; z < DominoesPlayer.size(); z++)
						{
							int domPlayer = DominoesPlayer.get(z);
							
							synchronized (Server.lock)
							{
								playerChip = Dominoes.get(domPlayer);
								synchronized (Server.lockTrack)
								{
									if (playerChip.getChip0() == trackValue.getValue())
									{
										
											System.out.println ("ThreadID " + Thread.currentThread().getId() + "  found the value in chip[0] .. idChip" + playerChip.getId() + " value "+playerChip.getChip0() +"_"+playerChip.getChip1());
											foundChip = true;
											break;
											
									}// if
									else if (playerChip.getChip1() == trackValue.getValue())
									{
										
										System.out.println ("ThreadID " + Thread.currentThread().getId() + "  found the value in chip[1] .. idChip" + playerChip.getId() + " value "+playerChip.getChip0() +"_"+playerChip.getChip1());
										playerChip.setShifted(1);
										foundChip = true;
										break;
									}
									else
									{
										System.out.println ("ThreadID " + Thread.currentThread().getId() + "  This chip does not help us at all.. get the next one !!");
		
									}
										
								}// synchronized server.LockTrack
								
							}// synchronized server.lock
						}// for per user
						
						if (foundChip)
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " Player has a chip for this move... continue ");
							loopTracking =false;
						}
						else if (!foundChip && (getValue + 1 >= trackValues.size()))
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " This means that the player does not have any chip for this.. skip the loop and ask for a free chip ");
							loopTracking = false;
						}
							
						getValue ++;
					} //while loopTracking
					
					if (foundChip)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  remove chip from player's list.. but add it to the track one ");

						Server.Track[trackValue.getTrack()].push(playerChip);
						
						// RJUA check this.. it might be wrong..
						// z specially
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
							}
						}
						else
						{
							System.out.println ("ThreadID " + Thread.currentThread().getId() + " since this player has already asked for a chip.. no need to get a new one");

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
					}
					
					/////***************************************
					
					// 4.  Three options
					///    - the chip is "good" .. now just wait
					////   - is not good, request a new one
					//     - is empty... then send a new chip 
					
				}
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

		int maxPlayers = 2;
		int x = 0;
		Server server = new Server();
		server.generateInitalSetDominoes(6);
		server.setMaxPlayers(maxPlayers);
		server.TrainPerTrack = new boolean [maxPlayers];
		
		
		//Server_player player [] = null;
		Server_player players [] = new Server_player[maxPlayers];
		server.Track = new Stack[maxPlayers+1];

		
		for (x = 0 ; x < maxPlayers; x++ )
		{
			 players[x] = new Server_player(x);
			 server.Track[x] = new Stack<DominoeChip>();
			 server.TrainPerTrack[x] = false;
			 
			 new Thread(players[x]).start();
		}
		//
		// create a new trck used for all players "global"
		//
		 server.Track[x] = new Stack<DominoeChip>();

		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " before sleep 1000 ");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " after sleep 1000 ");

		
		for (int w = 0 ; w < maxPlayers; w++ )
		{
			synchronized (players[w])
			{
				players[w].notify();
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
				synchronized (players[xy])
				{
					//
					// if any of the players is not ready... we need to wait 
					//
					if (players[xy].getIsReadyToStart()==0)
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
		int playerMove = server.getPlayerWithChip(initialChip);
		DominoeChip initChip = server.getInitialChip(initialChip);
		
		server.initialChip = initialChip;
		server.setChipInAllTracks(initChip);
		//int playerMove = -1;
		
		//server.printAllTracks();
		boolean isPlaying = true;
		boolean flag = false;
		
		while (isPlaying)
		{
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " It's time for player " +playerMove + " to play " );

			synchronized (players[playerMove])
			{
				flag = false;
				players[playerMove].notify();
			}
				while (!flag)
				{
					
					
					try {
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " main thread will wait ");
	
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					synchronized (players[playerMove])
					{
						flag = players[playerMove].getMoveDone();
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
