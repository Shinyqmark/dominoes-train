package dominoes.gui.moving;

import java.awt.Image;

import javax.swing.ImageIcon;


public class Dominoe  {

	int x;
	int y;
	int width;
	int height;
	
    public Dominoe(int _x, int _y, int _width, int _height) {
   //     setRect(x, y, width, height);
    	this.x=_x;
    	this.y=_y;
    	this.width=_width;
    	this.height=_height;
    	Image ficha= new ImageIcon("C:\\eclipse\\fichaDomino_2.png").getImage();
    //	drawImage(ficha, 0, 0, this);
    }

    
    
    public boolean isHit(float x, float y) {
        if (x <= (this.x+this.width/2) &&  x >= (this.x-this.width/2) && y <= (this.y + this.height/2) && y >= (this.y - this.height/2)) {
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
