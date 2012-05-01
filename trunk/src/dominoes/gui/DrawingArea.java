package dominoes.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import dominoes.gui.moving.Dominoe;

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
		//String imgNAme= imgFileName + 
//		for(int i=0; i<10; i++)
//		{
//			if(i%2==0){
//				playerChips[i]= new Dominoe (90+(i*80),0,imgFileName,this);
//			}else{
//				playerChips[i]= new Dominoe (90+(i*80),45,imgFileName,this);
//			}
//			
//		}
		
		int cont=0;
		for(int i=0; i<=12; i++)
		{
			for(int j=i; j<=12; j++)
			{
				imgFileName = "images/"+ i + "_" + j + ".png";
				playerChips[cont]= new Dominoe (90+(i*80),0,imgFileName,this);
				cont++;
			}
		}
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);    // paints background
		g.setColor(Color.white);
		g.fillRect(x-10,y,weith,height);
		g.fillRect(x-10,0,weith,100);
		int newy=y;
		int newx=x;
		Graphics2D g2d = (Graphics2D) g;
		
		 
		
		
		g2d.setColor(Color.blue);
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


        AffineTransform transform = new AffineTransform();  // identity transform

        for(int i=0; i<10; i++)
        {
        	// transform.translate(playerChips[i].x - playerChips[i].width/2, playerChips[i].y - playerChips[i].height/2);
        	transform = new AffineTransform();
        	transform.translate(playerChips[i].x, playerChips[i].y);
             g2d.drawImage(playerChips[i].getImg(), transform, this);	
        }
       


	}


	class MovingAdapter extends MouseAdapter {

		private int x;
		private int y;
		private int ChipSelected=0;

		public void mousePressed(MouseEvent e) {
			x = e.getX();
			y = e.getY();

		}

		public void mouseReleased(MouseEvent e) {

//			x = e.getX();
//			y = e.getY();
//
//			for(int i=0; i<zrectArray.length; i++ )
//			{
//				if (zrectArray[i].isHit(x, y)) {
//					System.out.println("Zrect number " + i + " is Hit");
//					// repaint();
//				}
//			}
		}
		
		public void mouseClicked(MouseEvent e){
		
			x = e.getX();
			y = e.getY();
			
			if(e.getClickCount() ==1){
			
				for( int i=0; i< 10; i++)
				{
					if (playerChips[i].isHit(x, y)) {
						System.out.println("Dominoe number " + i + " is Hit");
						ChipSelected=i;
						// repaint();
					}
				}
			}else if (e.getClickCount() ==2 ){
				x = e.getX();
				y = e.getY();

				for(int i=0; i<zrectArray.length; i++ )
					for(int j=0; j<10; j++)
					{
						
			
				{
					if (zrectArray[i][j].isHit(x, y)) {
						System.out.println("Zrect number " + i + "," + j + " is Hit");
						if(ChipSelected>=0)
						{
							playerChips[ChipSelected].setX(zrectArray[i][j].x);
							playerChips[ChipSelected].setY(zrectArray[i][j].y);
							repaint();
							ChipSelected=-1;
						}
					 break;
					}
				}		}
				ChipSelected=-1;
			} 
		}
	}
}
