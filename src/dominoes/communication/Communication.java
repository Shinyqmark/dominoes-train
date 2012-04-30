package dominoes.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public  class Communication {
	private  ArrayList<Socket> connections = null;
	
	
	public Communication ()
	{
		connections = new ArrayList <Socket >();
	}
	public void addConnection(Socket socket)
	{
		connections.add(socket);
	}
	public void sendMessage (String msg , int player) 
	{
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " sendMessage : message to be sent " +msg + " to player " + player );

		try {
			
		DataOutputStream out  = new DataOutputStream( connections.get(player).getOutputStream());
		
	
			out.writeUTF(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String receiveMessage ( int player)
	{

		DataInputStream in;
		String msg = "";
		try {
			in = new DataInputStream( connections.get(player).getInputStream());
			 msg = in.readUTF();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " receiveMessage : message received " +msg + " to player " + player );
		
		return msg;
		
	}
	public void broadCast (String msg) throws IOException
	{
		System.out.println ("ThreadID " + Thread.currentThread().getId() + " broadCast : message to be sent " +msg  );
		for (int x = 0 ; x< connections.size() ; x ++)
		{
			sendMessage (msg,x);
		}

		
	}

}
