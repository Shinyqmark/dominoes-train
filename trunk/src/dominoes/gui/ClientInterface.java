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
import javax.swing.JPanel;
import javax.swing.JTextField;

import dominoes.client.Player;
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
	private static String playerName="Arianne";
	private static String startPlaymsj="startplay_"+ playerName;
	public static String ErrorInvalidChip="ERROR_InvalidChip";
	public static String ErrorSkipTurn="OK_Skip";
	public static String ErrorEmptyChip="ERROR_EmptyChip";
	public static String OKchip="OK_Skip";
	public static Stack<Integer> [] gameBoard;
	int isValid=0;
	static int noValid=1;
	int validAndShifed=2;
	public static PlayerUtils player;


	public static void main(String[] args) {

		new ClientInterface ();
	}

	public ClientInterface (){
		super("Dominoes");
		Socket s = null;
		int serverPort = 7896;
		String data;
		//		String playMsj;
		player=new PlayerUtils();
		player.generateInitalSetDominoes(6);

		try {
			s = new Socket("localhost", serverPort);
			in = new DataInputStream(s.getInputStream());
			out =new DataOutputStream( s.getOutputStream());

			// subscribe to play
			out.writeUTF(startPlaymsj);
			data = in.readUTF();
			System.out.println(data);
			player.initGame(data);

			// wait the game starts 1st chip
			data=in.readUTF();

			if(data.contains("player"))
			{
				String [] broadCastmsj=data.split("_");
				int playerNum=Integer.parseInt(broadCastmsj[0].substring(6, 7));
				int playerChip=Integer.parseInt(broadCastmsj[1]);
				player.initGameBoard(playerNum,playerChip);
				System.out.println("init gameboard");
			}

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




	}

	public void buildInterface(Container pane){

		// pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		JMenu menu = new JMenu("Archivo");
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

		DrawingArea gameboarddraw= new DrawingArea ();
		board.setPreferredSize(new Dimension(400, 100));

		board.add(gameboarddraw);
		return gameboarddraw;
	}

	public JPanel gameMenu(){
		//	JButton drawButton= new JButton("Draw");
		//	JButton passButton= new JButton("Pass");
		drawButton.addActionListener(this);
		passButton.addActionListener(this);
		JLabel remainingChipsL =new JLabel("Remain Chips " );
		JTextField remainingChipsT = new JTextField(5);

		JPanel playerBoard = new JPanel( );
		JPanel chipsBoard = new JPanel( );
		chipsBoard.setPreferredSize(new Dimension(750, 40));

		playerBoard.setPreferredSize(new Dimension(1100, 40));
		playerBoard.add(chipsBoard);

		playerBoard.add(drawButton);
		playerBoard.add(passButton);
		playerBoard.add(remainingChipsL);
		playerBoard.add(remainingChipsT);


		return playerBoard;
	}

	public JPanel statusBar(){
		JPanel statusBAr = new JPanel( );
		JLabel moves =new JLabel("Moves " );
		JTextField cajatexto = new JTextField(5);
		JLabel timer =new JLabel("Timer " );
		JTextField timerText = new JTextField(5);
		statusBAr.add(moves);
		statusBAr.add(cajatexto);
		statusBAr.add(timer);
		statusBAr.add(timerText);
		return statusBAr;
	}

	public JPanel playersBar(){

		JPanel playersBar = new JPanel( );

		playersBar.setLayout(new BoxLayout(playersBar, BoxLayout.Y_AXIS));
		playersBar.setPreferredSize(new Dimension(80, 450));

		JLabel Title=new JLabel("Jugadores");
		Title.setPreferredSize(new Dimension(80, 90));
		playersBar.add(Title);
		for(int i=0; i<=gamePlayers; i++){
			JLabel aux=new JLabel("Player" + i);
			JTextField ChipRemain=new JTextField("5");
			aux.setPreferredSize(new Dimension(80, 10));
			ChipRemain.setSize(new Dimension(80, 15));


			playersBar.add(aux);
			playersBar.add(ChipRemain);
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
		
			System.out.println("ClickOnDrawButton");
			player.playRound();
		}
		if(	e.getSource().equals(passButton))
		{
			//
			System.out.println("ClickOnpassButtonButton");
		}
	}





}
