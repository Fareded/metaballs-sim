import java.awt.*;

public class Metaball {
    double x;
    double y;
    double r;
    double trueR;
    Color color;

    String name;

    Metaball(double x, double y, double r, Color color, String name) {


        this.x = x;
        this.y = y;
        this.trueR = r;
        this.r = trueR/4;
        this.color = color;
        this.name = name;
    }

    public boolean boundCheck(int x, int y) {
        return x > this.x - this.r &&
                x < this.x + this.r &&
                y > this.y - this.r &&
                y < this.y + this.r;
    }
}
