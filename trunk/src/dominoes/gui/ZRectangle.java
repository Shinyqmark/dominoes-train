package dominoes.gui;


import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


class ZRectangle extends Rectangle2D.Float {

	public int x;
	public int y;

    public ZRectangle(int x, int y, float width, float height) {
        this.x=x;
        this.y=y;
    	setRect(x, y, width, height);
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