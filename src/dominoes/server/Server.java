package dominoes.server;
import java.util.ArrayList;
import java.util.Stack;


public class Server {

	public static ArrayList <DominoeChip> Dominoes ;
	public static int DominoesAssigned = 0;
	public static Stack<DominoeChip> Track[];
	public static Object lock = new Object();
	public static Object lockTrack = new Object();
	public static int initialChip = -1;


	public int maxPlayers;
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
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "generateDominoes : random number " + randomNumber);
		
					//
					// first check if the chip has not been assigned to another player
					if (Dominoes.get(randomNumber).getAssigned()==0)
					{
						Dominoes.get(randomNumber).setAssigned();
						DominoesAssigned++;
						Dominoes.get(randomNumber).setPlayer(playerId);
						break;
						
					}
					else
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " chip already assigned .. get next one");
		
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
		for (int x = 0; x < maxPlayers ; x ++)
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
			while (true)
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
					int  trackValue  = Server.getValueInTrack(idPlayer);
					
					System.out.println ("ThreadID " + Thread.currentThread().getId() + "  value to find " + trackValue);
	
					//RJUA
					// this is just for testing..  delete it on the final version
					// ****************************************************
					boolean foundChip = false;
					for ( z = 0 ; z < DominoesPlayer.size(); z++)
					{
						int domPlayer = DominoesPlayer.get(z);
						
						synchronized (Server.lock)
						{
							playerChip = Dominoes.get(domPlayer);
							synchronized (Server.lockTrack)
							{
								if (playerChip.getChip0() == trackValue)
								{
									
										System.out.println ("ThreadID " + Thread.currentThread().getId() + "  found the value in chip[0] .. idChip" + playerChip.getId() + " value "+playerChip.getChip0() +"_"+playerChip.getChip1());
										foundChip = true;
										break;
										
								}// if
								else if (playerChip.getChip1() == trackValue)
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
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  remove chip from player's list.. but add it to the track one ");

						Server.Track[idPlayer].push(playerChip);
						DominoesPlayer.remove(z);

	
					}
					else
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "  It does not have any chips.. request one more ");
						int returnFreeChip = getFreeChip (idPlayer); 
						if (returnFreeChip >= 0)
						{
							DominoesPlayer.add(returnFreeChip);
						}
					}
					
					/////***************************************
					
					// 4.  Three options
					///    - the chip is "good" .. now just wait
					////   - is not good, request a new one
					//     - is empty... then send a new chip 
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println ("ThreadID " + Thread.currentThread().getId() + "   move done  .. ");
				moveDone = true;
	
			}// while true loop
		}// function run 
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int maxPlayers = 3;
		Server server = new Server();
		server.generateInitalSetDominoes(6);
		server.setMaxPlayers(maxPlayers);
		
		
		//Server_player player [] = null;
		Server_player players [] = new Server_player[maxPlayers];
		server.Track = new Stack[maxPlayers];

		
		for (int x = 0 ; x < maxPlayers; x++ )
		{
			 players[x] = new Server_player(x);
			 server.Track[x] = new Stack<DominoeChip>();
			 new Thread(players[x]).start();
		}
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " before sleep 1000 ");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " after sleep 1000 ");

		
		for (int x = 0 ; x < maxPlayers; x++ )
		{
			synchronized (players[x])
			{
				players[x].notify();
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
			for (int x = 0 ; x < maxPlayers; x++ )
			{
				synchronized (players[x])
				{
					//
					// if any of the players is not ready... we need to wait 
					//
					if (players[x].getIsReadyToStart()==0)
					{
						System.out.println ("ThreadID " + Thread.currentThread().getId() + " player  " + x + " not ready yet");
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
