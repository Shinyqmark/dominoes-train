package dominoes.gui.moving;


import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


class ZRectangle extends Rectangle2D.Float {

    BufferedImage dominoImage;
    TexturePaint dominotp;

	
    public ZRectangle(float x, float y, float width, float height) {
        setRect(x, y, width, height);

        try {
        	dominoImage = ImageIO.read(new File ("C:\\eclipse\\fichaDomino_2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        dominotp = new TexturePaint(dominoImage, this);
    }

  

 
    
    public boolean isHit(float x, float y) {
        if (getBounds2D().contains(x, y)) {
            return true;
        } else {
            return false;
        }
    }

    public void addX(float x) {
        this.x += x;
    }

    public void addY(float y) {
        this.y += y;
    }

    public void addWidth(float w) {
        this.width += w;
    }

    public void addHeight(float h) {
        this.height += h;
    }
}