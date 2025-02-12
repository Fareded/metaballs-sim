import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class MyPanel extends JPanel implements MouseListener, MouseMotionListener {
    Graphics2D g2D;
    ArrayList<Metaball> mballs = new ArrayList<Metaball>();
    Metaball heldBall = null;

    MyPanel() {
        this.setPreferredSize(new Dimension(1000, 1000));
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        Metaball mball = new Metaball(100, 100, 100, Color.BLUE, "BLUE");
        mballs.add(mball);
        Metaball mball2 = new Metaball(500, 100, 100, Color.RED, "RED");
        mballs.add(mball2);
        Metaball mball3 = new Metaball(100, 500, 100, Color.GREEN, "GREEN");
        mballs.add(mball3);
        Metaball mball4 = new Metaball(500, 500, 100, Color.YELLOW, "YELLOW");
        mballs.add(mball4);
    }

    public void paint(Graphics g) {
        g2D = (Graphics2D) g;
        renderALl();
    }

    public void renderALl() {
        super.paint(g2D); //clear screen before each redraw
        for (Metaball mball : mballs) {
            displayMetaball(mball);
        }
        drawBallDistance();
    }

    public void displayMetaball(Metaball m) {
        g2D.setColor(m.color);

        double distort = metaDistortion(m, m.x, m.y);
        for (double x = m.x - m.trueR; x <= m.x + m.trueR + distort; x++) {
            for (double y = m.y - m.trueR; y <= m.y + m.trueR + distort; y++) {
                distort = metaDistortion(m, x, y);
                double radius = m.r + distort;
                double r_sqrd = Math.pow(radius, 2);
                double d_sqrd = Math.pow(x - m.x, 2) + Math.pow(y - m.y, 2);
                if (d_sqrd < r_sqrd) {
                    g2D.fillRect((int) x, (int) y, 1, 1);
                }
            }
        }
        System.out.println("ball drawn");
    }

    public void updateBallPos(int x, int y) {
        heldBall.x = x;
        heldBall.y = y;
    }

    public void drawBallDistance() {
        g2D.setColor(Color.BLACK);
        System.out.println("<----------DISTANCE---------->");
        int exclusion = 0;
        for (int i = 0; i < mballs.size(); i++) {
            for (int j = exclusion; j < mballs.size(); j++) {
                Metaball mball = mballs.get(i);
                Metaball altball = mballs.get(j);
                if (mball != altball) {
                    g2D.drawLine((int) mball.x, (int) mball.y, (int) altball.x, (int) altball.y);
                    double dist = Math.sqrt(Math.pow(mball.x - altball.x, 2) + Math.pow(mball.y - altball.y, 2));
                    System.out.println(mball.name + "->" + altball.name);
                    System.out.println("distance: " + dist);
                    System.out.println("falloff: " + (100 / dist));
                }
            }
            exclusion++;
        }
    }

    public double metaDistortion(Metaball m, double x, double y) {
        double distortion = 0;

        for (Metaball mball : mballs) {
            if (mball != m) {
                double dist = Math.sqrt(Math.pow(mball.x - x, 2) + Math.pow(mball.y - y, 2));
                distortion = distortion + (1000 * (1 / dist));
            }
        }

        return distortion;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (heldBall != null) {
            updateBallPos(e.getX(), e.getY());
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("Moved");
//        System.out.println("X:" + e.getX() + " Y:" + e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean isHeld;

        for (int i = 0; i < mballs.size(); i++) {
            isHeld = mballs.get(i).boundCheck(e.getX(), e.getY());
//            System.out.println("Pressed - X:" + e.getX() + " Y:" + e.getY());
//            System.out.println(isHeld);
            if (isHeld) {
                heldBall = mballs.get(i);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        heldBall = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
