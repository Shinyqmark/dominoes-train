package dominoes.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
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
	int xDraw=0;
	int yDraw=0;
	int totalPlayers=0;
	ZRectangle [][] zrectArray = new ZRectangle[9][10];
	Dominoe [] playerChips = new Dominoe[100];
	Hashtable <Integer, Integer> positions= new Hashtable <Integer, Integer> () ;
	boolean [] positionFree = new boolean [24];
	
	ZRectangle zrect;
	String i="0";
	String j="0";
	private String imgFileName = "images/"+ i + "_" + j + ".png";//fichaDomino_2.png";
	private static boolean moveDone=false;

	public DrawingArea (int _totalPlayers){
		MovingAdapter ma = new MovingAdapter();
		this.totalPlayers=_totalPlayers;
		addMouseMotionListener(ma);
		addMouseListener(ma);

		int newy=y;
		int newx=x;
		for (int i=0; i<totalPlayers+1; i++){
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
					playerChips[cont]= new Dominoe (0,0,imgFileName,this,0);
				}else{
					playerChips[cont]= new Dominoe (0,45,imgFileName,this,0);
				}
				cont++;
			}
		}
		
	
		initPositions();
		Vector <Integer> playChips= ClientInterface.player.getMyChips();
		for(int i=0; i<playChips.size(); i++)
		{
			playerChips[playChips.get(i)].setPositionNumber(i);
		//	playerChips[playChips.get(i)].setX(10+(i*80)); positions
			playerChips[playChips.get(i)].setX(positions.get(i));
			if(i%2==0)
			{
				playerChips[playChips.get(i)].setY(0);
			}else
			{
				playerChips[playChips.get(i)].setY(50);
			}
			positionFree[i]=false;
		}
		
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);    // paints background
		g.setColor(Color.white);
		g.clearRect(x-10, y, weith, height);
		g.fillRect(x-10,y,weith,height);
		g.fillRect(x-10,0,weith,100);
		
		int newy=y;
		int newx=x;
		Graphics2D g2d = (Graphics2D) g;



		// draw gameboard
		g2d.setColor(Color.gray);
		for (int i=0; i<totalPlayers+1; i++){
			newx=x;
			if(ClientInterface.currentTurtn ==i)
				g2d.setColor(Color.green);
			else 
				g2d.setColor(Color.gray);
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
			if(playChips.get(i)!=ClientInterface.player.getFisrtChip())
			{
				transform = new AffineTransform();
				transform.translate(playerChips[playChips.get(i)].x, playerChips[playChips.get(i)].y);
				
				g2d.drawImage(playerChips[playChips.get(i)].getImg(), transform, this);	
			}
	
		}

		
		Stack<Integer>[] stackTemp=ClientInterface.player.getGameBoard();
		for(int i=0; i<stackTemp.length; i++ )
		{
			for (int j=0; j<stackTemp[i].size(); j++)
			{
				transform = new AffineTransform();
	
				transform.translate(playerChips[stackTemp[i].get(j)].x, playerChips[stackTemp[i].get(j)].y);
	
				if(playerChips[stackTemp[i].get(j)].isShift())
				{
		//			System.out.println("Imagen con shift");
					transform.rotate(Math.toRadians(180), playerChips[stackTemp[i].get(j)].width/2, playerChips[stackTemp[i].get(j)].height/2 );
				}
			//	System.out.println("Coloreando ficha : "+ i + "_" + j);
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

	public void updateBoard (int track, int chip, int position, int isShift){
		
		playerChips[chip].setX(zrectArray[track][position-2].x);
		playerChips[chip].setY(zrectArray[track][position-2].y);
		if(isShift==1)
			playerChips[chip].setIsShift(true);
		repaint();
	}
	
	public void updateSelfChip(int chip){
		
		int freePos=getFreePos();
		if(freePos >-1){
			playerChips[chip].setPositionNumber(freePos);
			playerChips[chip].setX(positions.get(freePos));
			if(freePos%2==0)
			{
				playerChips[chip].setY(0);
			}else
			{
				playerChips[chip].setY(50);
			}
			positionFree[freePos]=false;	
		}
		
		repaint();
	}
	
	
 	public boolean isMoveDone() {
		return moveDone;
	}

	public static void setMoveDone(boolean _moveDone) {
		moveDone = _moveDone;
	}


	public int getFreePos(){
		for (int i=0; i< positionFree.length; i++)
		{
			if(positionFree[i]==true)
				return i;
			
		}
		return -1;
	}
	public void initPositions(){
		

		positions.put(0, 0);
		positions.put(2, 90);
		positions.put(4, 180);
		positions.put(6, 270);
		positions.put(8, 360);
		positions.put(10, 450);
		positions.put(12, 540);
		positions.put(14, 630);
		positions.put(16, 720);
		positions.put(18, 810);
		positions.put(20, 900);
		positions.put(22, 990);
		positions.put(24, 1080);
		
		positions.put(1, 0);
		positions.put(3, 90);
		positions.put(5, 180);
		positions.put(7, 270);
		positions.put(9, 360);
		positions.put(11, 450);
		positions.put(13, 540);
		positions.put(15, 630);
		positions.put(17, 720);
		positions.put(19, 810);
		positions.put(21, 900);
		positions.put(23, 990);


		
		for (int i=0; i< positionFree.length; i++)
		{
			positionFree[i]=true;
		}
	}

	public void paintTurn(){
		repaint();
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
				boolean chipHit=false;
				for(int i=0; i<zrectArray.length; i++ )
					for(int j=0; j<10; j++)
					{
						if (zrectArray[i][j].isHit(x, y)) {
								System.out.println("Zrect number " + i + "," + j + " is Hit. In " + x +"," + y + "Dominochip x/y" + zrectArray[i][j].x + "/" + zrectArray[i][j].y);
								
								
								if(ChipSelected>=0 && ClientInterface.player.validateTrack(i))
								{
									int valid=ClientInterface.player.validateChip(i, ChipSelected);
									if(valid !=1 )
									{
										playerChips[ChipSelected].setX(zrectArray[i][j].x);
										playerChips[ChipSelected].setY(zrectArray[i][j].y);
										int chipPos=playerChips[ChipSelected].getPositionNumber();
										playerChips[ChipSelected].setPositionNumber(-1);
										positionFree[chipPos]=true;
										
										if(valid==2)
										{
											playerChips[ChipSelected].setIsShift(true);
											ClientInterface.player.setMsjTosend(i, ChipSelected, 1);
										}else
										{
											playerChips[ChipSelected].setIsShift(false);
											ClientInterface.player.setMsjTosend(i, ChipSelected, 0);
										}
										ClientInterface.player.cleadGameBoard();
										repaint();
										System.out.println("Mymove: " + ClientInterface.player.printChip(ChipSelected) + " TRack" + i);
										//ClientInterface.player.updateGameBoard(i,ChipSelected);
										ChipSelected=-1;
										chipHit=true;
									
										ClientInterface.moveDone=true;
									}
									
								}
								break;
							}
						if(chipHit)break;
					}
				ChipSelected=-1;
			} 
		}
	}
}
