package dominoes.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dominoes.client.PlayerUtils;
import dominoes.server.DominoeChip;


public class ClientInterface extends JFrame implements ActionListener {

	/**
	 * @param args
	 */
	Container content;
	int gamePlayers=8;
	int boardSpaces=12;
	private static final long serialVersionUID = 1L;

	JButton drawButton= new JButton("Draw");
	JButton passButton= new JButton("Pass");



	public static ArrayList <DominoeChip> Dominoes ;
	private DataInputStream in;
	private DataOutputStream out;
	public static String ErrorInvalidChip="ERROR_InvalidChip";
	public static String ErrorSkipTurn="OK_Skip";
	public static String ErrorEmptyChip="ERROR_EmptyChip";
	public static String OKchip="OK_Skip";
	public static Stack<Integer> [] gameBoard;
	int isValid=0;
	static int noValid=1;
	int validAndShifed=2;
	public static PlayerUtils player;
	DrawingArea gameboarddraw;
	public static boolean moveDone=false;
	int DrawCont=0;
	JTextField [] playersChipsCound;
	JTextField RemainingChips;
	public static int currentTurtn;
	public static boolean TrainPerTrack [] = null; 
	
	
	public static void main(String[] args) {

		new ClientInterface ();
	}

	public ClientInterface (){
		super("Dominoes");
		Socket s = null;
		int serverPort = 9996;
		String data;
		String playMsj="";
		player=new PlayerUtils();
		player.generateInitalSetDominoes(12);

		try {
			s = new Socket("localhost", serverPort);
			in = new DataInputStream(s.getInputStream());
			out =new DataOutputStream( s.getOutputStream());

			// subscribe to play
			//	out.writeUTF(startPlaymsj);
			data = in.readUTF();
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " message received :  " + data);


			if(data.contains("initGame"))
			{
				player.initGame(data);
			}

			// wait the game starts 1st chip
			playMsj=in.readUTF();




			if(playMsj.contains("player"))
			{
				String [] broadCastmsj=playMsj.split("_");
				int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
				int playerChip=Integer.parseInt(broadCastmsj[1]);

				System.out.println ("ThreadID " + Thread.currentThread().getId() + " player message received :  playerNum: " + playerNum + " playerChip : " +playerChip );


				player.initGameBoard(playerNum,playerChip);
				System.out.println("init gameboard");

				//
				// IF this player has the initial chip.. then he needs to remove it from his list...
				// 
				if (playerNum ==player.getPlayTurn())
				{
					System.out.println ("ThreadID " + Thread.currentThread().getId() + " I'm the one with the initial chip... delete it from my bucket" );

					player.removeDominoesPlayerPosition (playerChip);
				
				}
			}
	
			System.out.println ("ThreadID " + Thread.currentThread().getId() + " now wait for next message ");
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		setVisible(true);
		setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-50,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-50);
		getContentPane().setLayout(new BorderLayout());
		((JComponent) getContentPane()).setDoubleBuffered(true);

		buildInterface (getContentPane());


		
		try {
			

			while (!playMsj.contains("GAMEOVER"))
			{
				playMsj = in.readUTF();
				DrawCont=0;
				drawButton.setEnabled(true);
			//	playMsj=in.readUTF();
				
				System.out.println("Msg received from Server : " + playMsj);

				if (playMsj.contains ("GAMEOVER"))
				{
					System.out.println(" Game Over  ---  Exit now  " );

				}
				
				else if(playMsj.contains("ping"))
				{
					if(playMsj.contains("ping"+ player.getPlayTurn() ))
					{
					//	myTurn=true;
						gameboarddraw.setMyTurn(true);
						gameboarddraw.paintTurn();
						currentTurtn=player.getPlayTurn();
						
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "we just got a ping message... let's start working ! ");

						player.printGameBoard();
						while(!moveDone){
							Thread.sleep(100);
						}
						moveDone=false;
						// send msj
						String replyMsj;

						String chipToplay=player.getMsjTosend();
						System.out.println ("ThreadID " + Thread.currentThread().getId() + "Chip to play : " +chipToplay);

						out.writeUTF(chipToplay);
						replyMsj=in.readUTF();
						do{
							if(replyMsj.contains("ERROR"))
							{

								if(replyMsj.contains(ErrorInvalidChip))
								{
									System.out.println("ErrorInvalidChip > "+replyMsj);
									player.backTracking(replyMsj);
									//chipToplay=player.playRound();
									player.printGameBoard();
									while(!moveDone){
										Thread.sleep(100);
									}
									moveDone=false;
									
									out.writeUTF(player.getMsjTosend());
									replyMsj=in.readUTF();

								}else if(replyMsj.contains(ErrorEmptyChip))
								{
									System.out.println("ErrorEmptyChip > " + replyMsj);
									// Read the chip sent, update player chips & play again
									int newChip=Integer.parseInt(replyMsj.split("_")[2]);
									player.updateSelfChips(newChip);
									gameboarddraw.updateSelfChip(newChip);
									
									player.printGameBoard();
									while(!moveDone){
										Thread.sleep(100);
									}
									moveDone=false;
									System.out.println("Move detected");
									chipToplay=player.getMsjTosend();
									System.out.println("Msj to send: " + chipToplay);
									out.writeUTF(chipToplay);
									replyMsj=in.readUTF();
								}

							}

						}while(!replyMsj.contains(OKchip) );
						gameboarddraw.setMyTurn(false);
						gameboarddraw.paintTurn();
					}
						
					
					
					currentTurtn=-1;
				}
				else if(playMsj.contains("player" + player.getPlayTurn() ))
				{
					

					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					int remainingChips =Integer.parseInt(broadCastmsj[2]);
					int globalRemainingChips =Integer.parseInt(broadCastmsj[3]);
					int trackAvailable = Integer.parseInt(broadCastmsj[4]);
					int trackFromPlayer = Integer.parseInt(broadCastmsj[5]);
					int isShifted = Integer.parseInt(broadCastmsj[6]);

					playersChipsCound[playerNum].setText(remainingChips+"");
					RemainingChips.setText(globalRemainingChips+"");
					
					
					if (remainingChips ==0 )
					{
						System.out.println("  playerId : "+playerNum + " WON !!! ");
						JOptionPane.showMessageDialog(this, "  playerId : "+playerNum + " WON !!! ");
					}
					
					// play game & No need to update gameboard
					System.out.println(" Received broadCast : playerId : "+playerNum + " player Chip: "+playerChip + " trackAvailable : " + trackAvailable + " trackFromPlayer : " +trackFromPlayer +" isShifted: "+isShifted);
					//
					// no need to uptade the gameboard if the broadcast is from myself
					// 
					if (playerNum !=  player.getPlayTurn())
					{

						player.updateTrackAvailable (playerNum, trackAvailable);
						if (playerChip != 999)
						{
							if (trackFromPlayer != playerNum)
							{
								System.out.println(" Player played in a different track.. update the proper variable ");
								playerNum = trackFromPlayer;
								

								
							}
							player.updateShifted (playerNum, playerChip, isShifted);
							player.updateGameBoard(playerNum,playerChip);
							System.out.println("update gameboard");
						}
						else
						{
							System.out.println(" no update due to chip 999 ");

						}
					}
					else
					{
						player.updateTrackAvailable (playerNum, trackAvailable);
						
						System.out.println("No need to update the gameboard since the broadcast comes from myself ");

					}

					
				}else
				{
					String [] broadCastmsj=playMsj.split("_");
					int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
					int playerChip=Integer.parseInt(broadCastmsj[1]);
					int remainingChips =Integer.parseInt(broadCastmsj[2]);
					int globalRemainingChips =Integer.parseInt(broadCastmsj[3]);
					int trackAvailable = Integer.parseInt(broadCastmsj[4]);
					int trackFromPlayer = Integer.parseInt(broadCastmsj[5]);
					int isShifted = Integer.parseInt(broadCastmsj[6]);

					playersChipsCound[playerNum].setText(remainingChips+"");
					RemainingChips.setText(globalRemainingChips+"");
					
					if (remainingChips ==0 )
					{
						System.out.println("  playerId : "+playerNum + " WON !!! ");
						JOptionPane.showMessageDialog(this, "  playerId : "+playerNum + " WON !!! ");
					}
					
					System.out.println(" Message recevied : playerId : "+playerNum + " player Chip: "+playerChip + " trackAvailable : " + trackAvailable +" trackFromPlayer : " +trackFromPlayer +" isShifted: "+isShifted);
					
					player.updateTrackAvailable (playerNum, trackAvailable);
					
					if (playerChip != 999)
					{
						if (trackFromPlayer != playerNum)
						{
							System.out.println(" Player played in a different track.. update the proper variable and shifted ");
							playerNum = trackFromPlayer;
							
							
							
						}
						
						player.updateShifted (playerNum, playerChip, isShifted);
						
						int stackSize=player.updateGameBoard(playerNum,playerChip);
						gameboarddraw.updateBoard(playerNum,playerChip,stackSize, isShifted);
					}
					else
					{
						System.out.println(" no update due to chip 999 ");

					}
					
				}
			//	playMsj=in.readUTF();
				Thread.sleep(900);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Game Oveer");



	}

	public void buildInterface(Container pane){

		// pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		int playerID=player.getPlayTurn();
		JMenu menu = new JMenu("Jugador " + playerID );
		menu.add(new JMenuItem("Salir", 'S'));
		JMenuBar barramenu = new JMenuBar();
		barramenu.add(menu);
		setJMenuBar(barramenu); // Asignar al JFrame
		pane.add(gameMenu(), BorderLayout.NORTH);
		pane.add(gameBoard(), BorderLayout.CENTER);
		pane.add(statusBar(), BorderLayout.SOUTH); 
		pane.add(playersBar(), BorderLayout.WEST);

	}

	public JPanel gameBoard(){

		JPanel board = new JPanel();

		gameboarddraw= new DrawingArea (player.getTotalPlayers(), player.getPlayTurn());
		board.setPreferredSize(new Dimension(400, 100));

		board.add(gameboarddraw);
		return gameboarddraw;
	}

	public JPanel gameMenu(){
		drawButton.addActionListener(this);
		passButton.addActionListener(this);
		JLabel remainingChipsL =new JLabel("Remain Chips " );
		RemainingChips = new JTextField(5);

		JPanel playerBoard = new JPanel( );
		JPanel chipsBoard = new JPanel( );
		chipsBoard.setPreferredSize(new Dimension(750, 40));

		playerBoard.setPreferredSize(new Dimension(1100, 40));
		playerBoard.add(chipsBoard);

		playerBoard.add(drawButton);
		playerBoard.add(passButton);
		playerBoard.add(remainingChipsL);
		playerBoard.add(RemainingChips);


		return playerBoard;
	}

	public JPanel statusBar(){
		JPanel statusBAr = new JPanel( );
		JLabel moves =new JLabel("Moves " );
		JTextField movesText = new JTextField(5);
		JLabel timer =new JLabel("Timer " );
		JTextField timerText = new JTextField(5);
		statusBAr.add(moves);
		statusBAr.add(movesText);
		statusBAr.add(timer);
		statusBAr.add(timerText);
		return statusBAr;
	}

	public JPanel playersBar(){

		playersChipsCound= new JTextField [gamePlayers+1];
		JPanel playersBar = new JPanel( );

		playersBar.setLayout(new BoxLayout(playersBar, BoxLayout.Y_AXIS));
		playersBar.setPreferredSize(new Dimension(80, 450));

		JLabel Title=new JLabel("Jugadores");
		Title.setPreferredSize(new Dimension(80, 90));
		playersBar.add(Title);
		for(int i=0; i<=gamePlayers; i++){
			JLabel aux=new JLabel("Player" + i);
			if(i==player.getTotalPlayers()){
				aux.setText("Mex_Train");
			}
			playersChipsCound[i]=new JTextField("");
			aux.setPreferredSize(new Dimension(80, 10));
			playersChipsCound[i].setSize(new Dimension(80, 15));

			if(i>player.getTotalPlayers()){
				aux.setVisible(false);
				playersChipsCound[i].setVisible(false);
			}
			
			playersBar.add(aux);
			playersBar.add(playersChipsCound[i]);
		}

		return playersBar;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("ClickOnButton");
		if(	e.getSource().equals(drawButton))
		{
			// SEND MSJ WITH EMPTY CHIP
			if(DrawCont==0){
				moveDone=true;
				player.setEmptyMSj();
				System.out.println("ClickOnDrawButton");
				DrawCont++;
				drawButton.setEnabled(false);
			}
			

		}
		if(	e.getSource().equals(passButton))
		{
			if(DrawCont==1){
				drawButton.setEnabled(true);
				moveDone=true;
				player.setEmptyMSj();
				DrawCont=0;
				System.out.println("ClickOnpassButtonButton");
			}
			
			
		}
	}





}
