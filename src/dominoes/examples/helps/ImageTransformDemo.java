package dominoes.examples.helps;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.*;
import javax.swing.*;
import java.io.*;
 
/** Test applying affine transform on images */
@SuppressWarnings("serial")
public class ImageTransformDemo extends JPanel {
   // Named-constants for dimensions
   public static final int CANVAS_WIDTH = 640;
   public static final int CANVAS_HEIGHT = 480;
   public static final String TITLE = "Image Transform Demo";
 
   // Image
   private String imgFileName = "images/fichaDomino_1.png"; // relative to project root or bin
   private Image img;
   private int imgWidth, imgHeight;    // width and height of the image
   private double x = 100.0, y = 80.0; // center (x, y), with initial value
 
   /** Constructor to set up the GUI components */
   public ImageTransformDemo() {
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
 
      this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
   }
 
   /** Custom painting codes on this JPanel */
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);    // paint background
      setBackground(Color.WHITE);
 
      Graphics2D g2d = (Graphics2D) g;
      g2d.drawImage(img, 0, 0, this);  // Display with top-left corner at (0, 0)
 
      // drawImage() does not use the current transform of the Graphics2D context
      // Need to create a AffineTransform and pass into drawImage()
      AffineTransform transform = new AffineTransform();  // identity transform
      // Display the image with its center at the initial (x, y)
      transform.translate(x - imgWidth/2, y - imgHeight/2);
      g2d.drawImage(img, transform, this);
      // Try applying more transform to this image
      for (int i = 0; i < 5; i++) {
         transform.translate(70.0, 5.0);
         transform.rotate(Math.toRadians(15), imgWidth/2, imgHeight/2); // about its center
         transform.scale(0.9, 0.9);
         g2d.drawImage(img, transform, this);
      }
   }
 
   /** The Entry main method */
   public static void main(String[] args) {
      // Run the GUI codes on the Event-Dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new ImageTransformDemo());
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // center the application window
            frame.setVisible(true);
         }
      });
   }
}