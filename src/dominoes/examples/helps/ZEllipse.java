package dominoes.examples.helps;

import java.awt.geom.Ellipse2D;

class ZEllipse extends Ellipse2D.Float {
    public ZEllipse(float x, float y, float width, float height) {
        setFrame(x, y, width, height);
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