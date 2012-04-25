package dominoes.gui.moving;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MovingScaling extends JPanel {

    private ZRectangle zrect;
    private ZEllipse zell;
    private Image ficha;
    private Dominoe fichaD;
    int globalX=0; int globalY =0;

    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    
    // Image
    private String imgFileName = "images/fichaDomino_1.png"; // relative to project root or bin
    private Image img;
    private int imgWidth, imgHeight;  

    public MovingScaling() {

        MovingAdapter ma = new MovingAdapter();

        addMouseMotionListener(ma);
        addMouseListener(ma);
        addMouseWheelListener(new ScaleHandler());

 
        zrect = new ZRectangle(50, 50, 50, 50);
      //  dominotp = new TexturePaint(dominoImage, zrect);
        
   
        zell = new ZEllipse(150, 70, 80, 80);
        ficha= new ImageIcon("C:\\eclipse\\fichaDomino_2.png").getImage();

        setDoubleBuffered(true);
        
        // URL can read from disk file and JAR file
        URL url = getClass().getClassLoader().getResource(imgFileName);
        if (url == null) {
           System.err.println("Couldn't find file: " + imgFileName);
        } else {
           try {
              img = ImageIO.read(url);
              imgWidth = img.getWidth(this);
              imgHeight = img.getHeight(this);
           } catch (IOException ex) {
              ex.printStackTrace();
           }
        }
   
        
        fichaD= new Dominoe (0,0,imgWidth,imgHeight);
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

      //  g2d.setColor(new Color(0, 0, 200));
      //  g2d.setPaint(dominotp);
        g2d.fill(zrect);
        g2d.setColor(new Color(0, 200, 0));
        g2d.fill(zell);
      //  g2d.drawImage(ficha, fichaD.x, fichaD.y, null);
        AffineTransform transform = new AffineTransform();  // identity transform
        // Display the image with its center at the initial (x, y)
        transform.translate(globalX - imgWidth/2, globalY - imgHeight/2);
        g2d.drawImage(img, transform, this);
        
        
      //Crear la imagen estática
       // ficha= new ImageIcon("C:\\eclipse\\fichaDomino_2.png").getImage();
       // Graphics gbuffer = ficha.getGraphics();
      //  g2d.setClip(0, 0, 50, 50);
     //   g2d.drawImage(ficha, 0, 0, this);
       
        
    }



    class MovingAdapter extends MouseAdapter {

        private int x;
        private int y;

        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();

        }

        public void mouseDragged(MouseEvent e) {

            int dx = e.getX() - x;
            int dy = e.getY() - y;

//            if (zrect.isHit(x, y)) {
//                zrect.addX(dx);
//                zrect.addY(dy);
//                repaint();
//            }
//
//            if (zell.isHit(x, y)) {
//                zell.addX(dx);
//                zell.addY(dy);
//                repaint();
//            }

            if (fichaD.isHit(x, y)) {
            	fichaD.addX(dx);
            	fichaD.addY(dy);
                globalX=x;
                globalY=y;
                repaint();
            }
            
            x += dx;
            y += dy;
        }   
    }


    class ScaleHandler implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {

            int x = e.getX();
            int y = e.getY();

            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

                if (zrect.isHit(x, y)) {
                    float amount =  e.getWheelRotation() * 5f;
                    zrect.addWidth(amount);
                    zrect.addHeight(amount);
                    repaint();
                }

                if (zell.isHit(x, y)) {
                    float amount =  e.getWheelRotation() * 5f;
                    zell.addWidth(amount);
                    zell.addHeight(amount);
                    repaint();
                }
                
                if (fichaD.isHit(x, y)) {
                    float amount =  e.getWheelRotation() * 5f;
                    fichaD.addWidth(amount);
                    fichaD.addHeight(amount);
                    repaint();
                }
            }
        }
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
