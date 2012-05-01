package dominoes.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import dominoes.server.DominoeChip;

public class PlayerUtils {

	
	public ArrayList <DominoeChip> Dominoes ;
	private DataInputStream in;
	private DataOutputStream out;
	private static String playerName="Arianne";
	private  String startPlaymsj="startplay_"+ playerName;
	private int playTurn;
	private int fisrtChip;
	private int totalPlayers;
	private String [] chips;
	public static String ErrorInvalidChip="ERROR_InvalidChip";
	public static String ErrorSkipTurn="OK_Skip";
	public static String ErrorEmptyChip="ERROR_EmptyChip";
	public static String OKchip="OK_Skip";
	public Stack<Integer> [] gameBoard;
	public Vector <Integer> myChips = new Vector (); 
	int isValid=0;
	static int noValid=1;
	int validAndShifed=2;
	
	public PlayerUtils (){
		
	}
	
	public void initGame(String data){

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
	
	public void updateGameBoard(int player, int chip){
		gameBoard[player].push(chip);
		// repaint
	}
	
	public void initGameBoard(int player, int chip){
		for(int i=0; i<totalPlayers; i++ ){
			gameBoard[i].push(chip);
		}
		System.out.println("Ficha Inicial--> " + printChip(chip));
		printGameBoard();
		// repaint
	}

	public void updateSelfChips(int chip){
		myChips.add(chip);
		// repaint
	}

	public void backTracking(String dataReply ){


		int myChip=Integer.parseInt(dataReply.split("_")[2]);
		int trail=Integer.parseInt(dataReply.split("_")[3]);
		//myChips.add(gameBoard[trail].pop());
		gameBoard[trail].pop();
		myChips.add(myChip);
		//repaint
	}
	
	public String playRound() {
		// player#_chip_trail_chipsTail
		int selectedChip;
		String selectedTrail;
		String PlayMsj="";;

		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);

		System.out.println("Select the trail to play");
		try {
		selectedTrail=in.readLine();
		printChips(gameBoard[Integer.parseInt(selectedTrail)]);
		do{
		System.out.println("Select your chip to play");
		//for(int i=0; i<myChips.size(); i++ )System.out.print(i +":"+ myChips.get(i) + ",");
		printChips(myChips);
	
			selectedChip=Integer.parseInt(in.readLine());
		
		}while(validateChip(Integer.parseInt(selectedTrail),myChips.get(selectedChip))==noValid);
		if(selectedChip>=myChips.size())
		{
			PlayMsj="player"+ playTurn+ "_"+""+"_"+selectedTrail+"_"+myChips.size();
		}else{
			selectedChip=myChips.remove(selectedChip);
			PlayMsj="player"+ playTurn+ "_"+selectedChip+"_"+selectedTrail+"_"+myChips.size();

		}


		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return PlayMsj;
	}
	
	public int validateChip(int player, int chip){
		int isValid=0;
		int noValid=1;
		int validAndShifed=2;
		int previousChip=gameBoard[player].pop();
		DominoeChip prevChip=Dominoes.get(previousChip);
		DominoeChip newChip=Dominoes.get(chip);
		
		if(prevChip.getShifted()==1){
			if(prevChip.getChip0()== newChip.getChip0())
			{
				gameBoard[player].push(previousChip);
				gameBoard[player].push(chip);
				return isValid;
			}else if(prevChip.getChip0()== newChip.getChip1())
			{
				Dominoes.remove(chip);
				newChip.setShifted(1);
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
				//Dominoes.remove(chip);
				newChip.setShifted(1);
				Dominoes.set(chip, newChip);
				return validAndShifed;
			}
		}
		
		gameBoard[player].push(previousChip);
		return noValid;
	}

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


	public void printChips(Vector <Integer> myChips){
		String aux="";
		
		for(int i=0; i<myChips.size(); i++){
			//aux.concat(str)
			System.out.print(i +":" +printChip(myChips.get(i)) + " , ");
			
		}
	}
	

	public String getDominoesChips(Vector <Integer> myChips){
		String aux="";
		
		for(int i=0; i<myChips.size(); i++){
			aux.concat(myChips.get(i) + " , ");
	
		}
		return aux;
	
	}
	
	public void printChips(Stack <Integer> myChips){
		
		for(int i=0; i<myChips.size(); i++){
			System.out.print(i +":" +printChip(myChips.get(i)) + " , ");
		}
	}
	
	public String printChip(int chip){
		return  Dominoes.get(chip).getChip0() + "|" +  Dominoes.get(chip).getChip1();
	}

	public void printGameBoard(){
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
