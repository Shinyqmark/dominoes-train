package dominoes.server;

public class DominoeChip {
	private int chip[];
	private int id;

	public DominoeChip(int x, int y, int Id)
	{
		chip = new int[2];
		chip[0] = x;
		chip[1] = y;
		id = Id;
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
	
}
