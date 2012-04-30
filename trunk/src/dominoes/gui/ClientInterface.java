package dominoes.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

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

import dominoes.gui.moving.Dominoe;

public class ClientInterface extends JFrame implements ActionListener {

	/**
	 * @param args
	 */
	Container content;
	int gamePlayers=8;
	int boardSpaces=12;
	private static final long serialVersionUID = 1L;
	private Dominoe fichaD;

    // Image
    private String imgFileName = "images/fichaDomino_2.png"; // relative to project root or bin

    JButton drawButton= new JButton("Draw");
 	JButton passButton= new JButton("Pass");
    
	public static void main(String[] args) {

		new ClientInterface ();
	}
	
	public ClientInterface (){
		super("Dominoes");
		setVisible(true);
		setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-50,(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-50);

	    fichaD= new Dominoe (100,100,imgFileName,this);

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
		}
		if(	e.getSource().equals(passButton))
		{
			//
			System.out.println("ClickOnpassButtonButton");
		}
	}

}
