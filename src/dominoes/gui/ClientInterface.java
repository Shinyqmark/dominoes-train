package dominoes.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.*;

public class ClientInterface extends JFrame {

	/**
	 * @param args
	 */
	Container content;
	int gamePlayers=8;
	int boardSpaces=12;
	public static void main(String[] args) {

		new ClientInterface ();
	}
	
	public ClientInterface (){
		super("Dominoes");
		setVisible(true);
		setSize(200,200);
		getContentPane().setLayout(new BorderLayout());
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
		
//		JButton button= new JButton("1");
//		JButton button2= new JButton("2");
//		JButton button3= new JButton("3");
		board.setPreferredSize(new Dimension(400, 0));
		board.setBackground(Color.white);
//		board.add(button);
//		board.add(button2);
//		board.add(button3);
		return board;
	}
	
	public JPanel gameMenu(){
		JLabel tableroTitle= new JLabel("Juego de Domino");
	 	JButton button= new JButton();
		button.setIcon(new ImageIcon("C:\\eclipse\\fichaDomino_1.png"));
		button.setPreferredSize(new Dimension(95, 54));
		JButton button2= new JButton();
		button2.setIcon(new ImageIcon("C:\\eclipse\\fichaDomino_1.png"));
		button2.setPreferredSize(new Dimension(95, 54));
		JPanel menu = new JPanel( );
		menu.setPreferredSize(new Dimension(400, 80));
		menu.setLayout(new GridLayout (0,6));
		menu.setSize(100, 50);
		menu.add(tableroTitle);
		menu.add(button, 1);
		menu.add(button2,2);
	

		return menu;
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
		//playersBar.setPreferredSize(new Dimension(400, 80));
		playersBar.setLayout(new GridLayout (gamePlayers,1));
		for(int i=0; i<gamePlayers; i++){
			playersBar.add(new JLabel("Player " + i));
		}
		
		return playersBar;
	}


}
