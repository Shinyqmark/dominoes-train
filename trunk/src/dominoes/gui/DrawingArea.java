package dominoes.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JPanel;


public class DrawingArea extends JPanel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int weith=1100;
	int height=500;
	int x=10;
	int y=100;
	int xMax=x+weith;
	int yMax=y+height;
	ZRectangle [][] zrectArray = new ZRectangle[9][10];
	Dominoe [] playerChips = new Dominoe[100];
	ZRectangle zrect;
	String i="0";
	String j="0";
	private String imgFileName = "images/"+ i + "_" + j + ".png";//fichaDomino_2.png";
	private static boolean moveDone=false;

	public DrawingArea (){
		MovingAdapter ma = new MovingAdapter();

		addMouseMotionListener(ma);
		addMouseListener(ma);

		int newy=y;
		int newx=x;
		for (int i=0; i<9; i++){
			newx=x;
			for (int j=0; j< 10; j++)
			{
				zrectArray[i][j]=new ZRectangle(newx, newy, 85, 45);
				//	 g.fillRect(newx,newy,90,45);
				newx=newx+110;
			}
			newy=newy+55;
		}

		int cont=0;
		for(int i=0; i<=12; i++)
		{
			for(int j=i; j<=12; j++)
			{
				imgFileName = "images/"+ i + "_" + j + ".png";
				if(cont%2==0){
					playerChips[cont]= new Dominoe (0,0,imgFileName,this);
				}else{
					playerChips[cont]= new Dominoe (0,45,imgFileName,this);
				}
				cont++;
			}
		}
		
	
		
		Vector <Integer> playChips= ClientInterface.player.getMyChips();
		for(int i=0; i<playChips.size(); i++)
			playerChips[playChips.get(i)].setX(10+(i*80));
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);    // paints background
		g.setColor(Color.white);
		g.fillRect(x-10,y,weith,height);
		g.fillRect(x-10,0,weith,100);
		int newy=y;
		int newx=x;
		Graphics2D g2d = (Graphics2D) g;



		// draw gameboard
		g2d.setColor(Color.gray);
		for (int i=0; i<9; i++){
			newx=x;
			for (int j=0; j< 10; j++)
			{
				//	 zrectArray[i*j]=new ZRectangle(newx, newy, 90, 45);;
				g2d.fill( zrectArray[i][j]);
				//	 g.fillRect(newx,newy,90,45);
				newx=newx+110;
			}
			newy=newy+55;
		}


		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


		// draw chip images
		AffineTransform transform = new AffineTransform();  // identity transform
		Vector <Integer> playChips= ClientInterface.player.getMyChips();
		for(int i=0; i<playChips.size(); i++)
		{
			// transform.translate(playerChips[i].x - playerChips[i].width/2, playerChips[i].y - playerChips[i].height/2);
			
			transform = new AffineTransform();
			transform.translate(playerChips[playChips.get(i)].x, playerChips[playChips.get(i)].y);
			g2d.drawImage(playerChips[playChips.get(i)].getImg(), transform, this);	
		}

		
		Stack<Integer>[] stackTemp=ClientInterface.player.getGameBoard();
		for(int i=0; i<stackTemp.length; i++ )
		{
			for (int j=0; j<stackTemp[i].size(); j++)
			{
				transform = new AffineTransform();
				transform.translate(playerChips[stackTemp[i].get(j)].x, playerChips[stackTemp[i].get(j)].y);
				g2d.drawImage(playerChips[stackTemp[i].get(j)].getImg(), transform, this);	
			}
		}
		
		
		//draw Initial chip
		int fisrtChip=ClientInterface.player.getFisrtChip();
		playerChips[fisrtChip].setX(1000);
		playerChips[fisrtChip].setY(45);
		transform = new AffineTransform();
		transform.translate(playerChips[fisrtChip].x, playerChips[fisrtChip].y);
		g2d.drawImage(playerChips[fisrtChip].getImg(), transform, this);	

	}

	public void updateBoard (int track, int chip, int position){
		
		playerChips[chip].setX(zrectArray[track][position-2].x);
		playerChips[chip].setY(zrectArray[track][position-2].y);
		repaint();
	}
	
	
	
 	public boolean isMoveDone() {
		return moveDone;
	}

	public static void setMoveDone(boolean _moveDone) {
		moveDone = _moveDone;
	}



	class MovingAdapter extends MouseAdapter {

		private int x;
		private int y;
		private int ChipSelected=0;

		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();

		}

		public void mouseClicked(MouseEvent e){

			x = e.getX();
			y = e.getY();

			if(e.getClickCount() ==1){
				Vector <Integer> playChips= ClientInterface.player.getMyChips();
				for( int i=0; i< playChips.size() ; i++)
				{
					if (playerChips[playChips.get(i)].isHit(x, y)) {
						System.out.println("Dominoe number " + i + " is Hit");
						ChipSelected=playChips.get(i);
						// repaint();
					}
				}
			}else if (e.getClickCount() ==2 ){
				x = e.getX();
				y = e.getY();

				for(int i=0; i<zrectArray.length; i++ )
					for(int j=0; j<10; j++)
					{
						if (zrectArray[i][j].isHit(x, y)) {
								System.out.println("Zrect number " + i + "," + j + " is Hit. In " + x +"," + y + "Dominochip x/y" + zrectArray[i][j].x + "/" + zrectArray[i][j].y);
								if(ChipSelected>=0 && ClientInterface.player.validateTrack(i) && ClientInterface.player.validateChip(i, ChipSelected) !=1 )
								{
									playerChips[ChipSelected].setX(zrectArray[i][j].x);
									playerChips[ChipSelected].setY(zrectArray[i][j].y);
									repaint();
									ChipSelected=-1;
									ClientInterface.moveDone=true;
								}
								break;
							}
						
					}
				ChipSelected=-1;
			} 
		}
	}
}
