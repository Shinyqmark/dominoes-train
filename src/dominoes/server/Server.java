package dominoes.server;
import java.util.ArrayList;


public class Server {

	ArrayList <DominoeChip> Dominoes ;
	
	public Server()
	{
		Dominoes = null;
	}
	
	//
	// This function will generate a RANDOM set of chips 
	//
	public void  generateDominoes ()
	{
		
	}
	//
	// This function creates the initial set of the dominoes
	// Initially the dominoes is SORTED.. 
	// Size is the max size of the dominoes\
	//
	public void generateInitalSetDominoes (int size)
	{
		System.out.println ("generateInitalSetDominoes : size max of " + size);
		int numChip = 1 ;
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Server server = new Server();
		server.generateInitalSetDominoes(12);
		server.printDominoeList();
		
		
	}

}
