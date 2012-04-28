package dominoes.server;

public class DominoeChip {
	private int chip[];
	private int id;
	private int isAssigned;
	private int playerId;
	private int isMula;
	private boolean isShifted;

	public DominoeChip(int x, int y, int Id)
	{
		chip = new int[2];
		chip[0] = x;
		chip[1] = y;
		id = Id;
		isAssigned = 0;
		playerId = -1;
		isMula = 0;
		isShifted = false;
	}
	public void setShifted(boolean i)
	{
		isShifted =i;
	}
	public boolean getShifted()
	{
		return isShifted;
	}
	public void setMula()
	{
		isMula =1;
	}
	public int getMula()
	{
		return isMula;
	}
	public void setPlayer (int player)
	{
		playerId = player;
	}
	public int getPlayerId()
	{
		return playerId;
	}
	
	public int getId ()
	{
		return id;
	}
	public void  setChip(int x, int y, int Id)
	{
		chip[0] = x;
		chip[1] = y;
		id = Id;
	}
	public int[] getChip()
	{
		return chip;
	}
	public int getChip0()
	{
		return chip[0];
	}
	public int getChip1()
	{
		return chip[1];
	}
	public void setAssigned()
	{
		isAssigned=1;
	}
	public int getAssigned()
	{
		return isAssigned;
	}
	public void pintChip ()
	{
		System.out.println("Num_0 : "+ chip[0] + " Num_1 : " + chip[1] );
	}
	
}
