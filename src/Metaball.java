import java.awt.*;

public class Metaball {
    double x;
    double y;
    double r;
    double falseR;

    int strength;
    Color color;
    double C;
    double M;
    double Y;
    String name;

    double vVel;
    double hVel;

    boolean isNegative;

    Metaball(double x, double y, double r, int strength, Color color, String name, boolean isNegative) {


        this.x = x;
        this.y = y;
        this.r = r;
        this.strength = strength;
        this.falseR = r * (r / 10);
//        this.falseR = r * (r / 5);
        this.color = color;
        this.name = name;
        this.isNegative = isNegative;

        vVel = 0;
        hVel = 0;

        C = 1 - (color.getRed() / 255);
        M = 1 - (color.getGreen() / 255);
        Y = 1 - (color.getBlue() / 255);
    }

    public boolean boundCheck(int x, int y) {
        return x > this.x - this.r &&
                x < this.x + this.r &&
                y > this.y - this.r &&
                y < this.y + this.r;
    }
}
