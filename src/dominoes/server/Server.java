package dominoes.server;
import java.util.ArrayList;


public class Server {

	ArrayList <DominoeChip> Dominoes ;
	int MaxChipsPerPlayer;
	
	public Server()
	{
		Dominoes = null;
		MaxChipsPerPlayer=7;
	}
	
	//
	// This function will generate a RANDOM set of chips per player
	//
	public ArrayList <Integer>  generateDominoes ()
	{
		ArrayList <Integer> DominoesPlayer = new ArrayList<Integer> ();
		int randomNumber = 0;
		
		for (int i = 0 ; i < MaxChipsPerPlayer ; i++)
		{
			randomNumber = (int) (Math.random() * Dominoes.size());
			System.out.println ("generateDominoes : random number " + randomNumber);

			//
			// first check if the chip has not been assigned to another player
			if (Dominoes.get(randomNumber).getAssigned()==0)
			{
				Dominoes.get(randomNumber).setAssigned();
				DominoesPlayer.add(randomNumber);
				
			}
			else
			{
				System.out.println ("generateDominoes : entry  " + randomNumber + " already chosen ... next!");
				i--;

			}
			
		}
		
		printDominoePerPlayerList(DominoesPlayer);
		
		
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
		Dominoes  = new ArrayList<DominoeChip> ();

		
		for (x = 0; x <= size ; x ++)
		{
			for (; y <= size ; y ++)
			{
				DominoeChip chip = new DominoeChip (x,y, numChip);
				numChip++;
				Dominoes.add(chip);
			}
			y=x+1;
		}
		
	}
	//
	// This function is itself explanatory
	public void printDominoeList ()
	{
		for (int x = 0 ; x < Dominoes.size(); x++)
		{
			DominoeChip  temp = Dominoes.get(x);
			System.out.println ("Chip Id: " +temp.getId()+ " Num_0 : "+temp.getChip0()+ " Num_1: " + temp.getChip1());
		}
	}
	
	public void printDominoePerPlayerList ( ArrayList <Integer> DominoesPlayer)
	{
		for (int x = 0 ; x < DominoesPlayer.size(); x++)
		{
			int  temp = DominoesPlayer.get(x);
			System.out.print ("printDominoePerPlayerList : Chip Id: " +temp + " ");
			Dominoes.get(temp).pintChip();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Server server = new Server();
		server.generateInitalSetDominoes(6);
		//server.printDominoeList();
		server.generateDominoes();
		server.generateDominoes();
		server.generateDominoes();
		server.generateDominoes();


		
		
	}

}
