package dominoes.server;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;


public class Server {

	public static ArrayList <DominoeChip> Dominoes ;
	public static Stack<DominoeChip> Track[];
	public static Object lock = new Object();
	public static Object lockTrack = new Object();


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
		
	
		public Server_player (int id)
		{
			idPlayer = id;
			isReadyToStart = 0;
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
				
				System.out.println ("ThreadID " + Thread.currentThread().getId() + "   Now is my turn... lets communicate with my player ");
				
				//
				// 1 . Communicate player saying .. "it's your turn"
				//
				// 2. Wait for their chip...or empty
				//
				// 3. Validate chip... needs to follow the rules
				//
				DominoeChip playerChip = null;
				int  trackValue  = Server.getValueInTrack(idPlayer);
				
				
				
				// 4.  Three options
				///    - the chip is "good" .. now just wait
				////   - is not good, request a new one
				//     - is empty... then send a new chip 
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
		
		server.setChipInAllTracks(initChip);
		//int playerMove = -1;
		
		server.printAllTracks();
		boolean isPlaying = true;
		
		while (isPlaying)
		{
			synchronized (players[playerMove])
			{
				players[playerMove].notify();
				
				try {
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " main thread will wait ");

					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				playerMove = server.getNextPlayer(playerMove);
			}
			
		}
		
		//
		// At this point we have the players, initial chip and Who has that chip..
		// good enough to start with the game...
		//

		
	}

}
