package dominoes.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import dominoes.gui.moving.Dominoe;

public class ClientInterface extends JFrame {

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

    private int imgWidth, imgHeight;  
    
	public static void main(String[] args) {

		new ClientInterface ();
	}
	
	public ClientInterface (){
		super("Dominoes");
		setVisible(true);
		setSize(200,200);
	    MovingAdapter ma = new MovingAdapter();

	    addMouseMotionListener(ma);
	    addMouseListener(ma);

	    fichaD= new Dominoe (100,100,imgFileName,this);

		getContentPane().setLayout(new BorderLayout());
		((JComponent) getContentPane()).setDoubleBuffered(true);
		buildInterface (getContentPane());
		//add(buildInterface());
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
		board.setLayout(new GridLayout(gamePlayers, boardSpaces));
		int totalSpace=gamePlayers*boardSpaces;
		
		for(int i=0; i<totalSpace; i++){
			board.add(new JButton(""+i));
		}
		
		board.setPreferredSize(new Dimension(400, 0));
		board.setBackground(Color.white);
		return board;
	}
	
	public JPanel gameMenu(){
		JLabel tableroTitle= new JLabel("Juego de Domino");
		JButton drawButton= new JButton();
//		button.setIcon(new ImageIcon("C:\\eclipse\\fichaDomino_1.png"));
	 	JButton passButton= new JButton();
//		button2.setIcon(new ImageIcon("C:\\eclipse\\fichaDomino_1.png"));

		tableroTitle.setBounds(10, 10, 50, 300);
		passButton.setBounds(500, 10, 40, 20);
		drawButton.setBounds(500, 35, 40, 20);
		
		JPanel playerBoard = new JPanel( );
		playerBoard.setLayout(null);
		playerBoard.setPreferredSize(new Dimension(400, 80));
		playerBoard.add(tableroTitle);
		playerBoard.add(drawButton);
		playerBoard.add(passButton);
	

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
	//	playersBar.setLayout(new BorderLayout());
		//playersBar.setPreferredSize(new Dimension(400, 80));
		playersBar.setLayout(new GridLayout (gamePlayers,1));
		for(int i=0; i<gamePlayers; i++){
			playersBar.add(new JLabel("Player " + i + "F" + 4 ));
		}
		
		return playersBar;
	}

	  public void paint(Graphics g) {
	        super.paint(g);
	        
	        Graphics2D g2d = (Graphics2D) g;
	   
	        Font font = new Font("Serif", Font.BOLD, 40);
	        g2d.setFont(font);
	        
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                        RenderingHints.VALUE_ANTIALIAS_ON);
	        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


	        AffineTransform transform = new AffineTransform();  // identity transform

	       // transform.translate(fichaD.x - imgWidth/2, fichaD.y - imgHeight/2);
	        //g2d.drawImage(fichaD.getImg(), transform, this);
	        
	     
	        
	    }
	  
	  class MovingAdapter extends MouseAdapter {

	        private int x;
	        private int y;
	        private boolean mymove=false;
	        public void mousePressed(MouseEvent e) {
	            x = e.getX();
	            y = e.getY();
	            System.out.println("Mouse presed" + x + "," + y );
	            
	            if (fichaD.isHit(x, y)) {
	            	 System.out.println("Ficha is hit");
	            	mymove=true;
	            }else mymove=false;
	        }

	        public void mouseReleased(MouseEvent e) {
	        
	        	if(mymove){
	        		 System.out.println("Ficha is released");
	        		x = e.getX();
	        		y = e.getY();
	        		fichaD.setX(x);
	        		fichaD.setY(y);
	        		System.out.println("Mouse released" + x + "," + y );
	        		repaint();
	        		mymove=false;
	        	}
	        }
	    }

}
