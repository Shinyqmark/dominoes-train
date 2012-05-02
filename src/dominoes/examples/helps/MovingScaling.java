package dominoes.examples.helps;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dominoes.gui.Dominoe;


public class MovingScaling extends JPanel {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Dominoe fichaD;
    int globalX=0; int globalY =0;

    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    
    // Image
    private String imgFileName = "images/fichaDomino_1.png"; // relative to project root or bin

    private int imgWidth, imgHeight;  

    public MovingScaling() {

        MovingAdapter ma = new MovingAdapter();

        addMouseMotionListener(ma);
        addMouseListener(ma);

        setDoubleBuffered(true);
        
        fichaD= new Dominoe (0,0,imgFileName,this,0);
        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
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

        transform.translate(fichaD.x - imgWidth/2, fichaD.y - imgHeight/2);
        g2d.drawImage(fichaD.getImg(), transform, this);
        
     
        
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
        	System.out.println("Mouse released" + x + "," + y );
        	if(mymove){
        		 System.out.println("Ficha is released");
        		x = e.getX();
        		y = e.getY();
        		fichaD.setX(x);
        		fichaD.setY(y);
        		globalX=x;
        		globalY=y;
        		repaint();
        		mymove=false;
        	}
        }
//        public void mouseDragged(MouseEvent e) {
//
//            int dx = e.getX() - x;
//            int dy = e.getY() - y;
//
////            if (zrect.isHit(x, y)) {
////                zrect.addX(dx);
////                zrect.addY(dy);
////                repaint();
////            }
////
////            if (zell.isHit(x, y)) {
////                zell.addX(dx);
////                zell.addY(dy);
////                repaint();
////            }
//
//            if (fichaD.isHit(x, y)) {
//            	fichaD.addX(dx);
//            	fichaD.addY(dy);
//                globalX=x;
//                globalY=y;
//                repaint();
//            }
//            
//            x += dx;
//            y += dy;
//        }   
//    
//    
    }


     public static void main(String[] args) {

        JFrame frame = new JFrame("Moving and Scaling");
        frame.add(new MovingScaling());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
