package dominoes.gui.moving;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;


public class Dominoe  {

	public int x;
	public int y;
	public int width;
	public int height;
	Image img;

	private String imgFileName; //= "images/fichaDomino_1.png"; 

	public Dominoe(int _x, int _y, String _imgFile, ImageObserver imgob) {

		this.x=_x;
		this.y=_y;
		this.imgFileName=_imgFile;

		URL url = getClass().getClassLoader().getResource(imgFileName);
		if (url == null) {
			System.err.println("Couldn't find file: " + imgFileName);
		} else {
			try {
				img = ImageIO.read(url);
				this.width = img.getWidth(imgob);
				this.height = img.getHeight(imgob);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}



	//    public boolean isHit(float x, float y) {
	//        if (x <= (this.x+this.width/2) &&  x >= (this.x-this.width/2) && y <= (this.y + this.height/2) && y >= (this.y - this.height/2)) {
	//            return true;
	//        } else {
	//            return false;
	//        }
	//    }

	public boolean isHit(float x, float y) {
		if (x <= (this.x+this.width) && (x > this.x) &&  y <= (this.y + this.height) && y> this.y) {
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
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}


	public void addWidth(float w) {
		this.width += w;
	}

	public void addHeight(float h) {
		this.height += h;
	}

	public Image getImg() {
		return img;
	}



	public void setImg(Image img) {
		this.img = img;
	}

}
