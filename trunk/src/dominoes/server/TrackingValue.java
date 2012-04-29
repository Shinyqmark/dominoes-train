package dominoes.server;

public class TrackingValue {
	private int Track;
	private int Value;
	
	public TrackingValue(int track, int value)
	{
		Track = track;
		Value = value;
	}
	public  int getTrack ()
	{
		return Track;
	}
	public int getValue()
	{
		return Value;
	}

}
