import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MyPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    Graphics2D g2D;
    ArrayList<Metaball> mballs = new ArrayList<>();
    Metaball heldBall = null;
    SpinnerModel radiusValue =
            new SpinnerNumberModel(50, //initial value
                    20, //minimum value
                    100, //maximum value
                    5); //step
    Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.PINK};

    MyPanel() {
        this.setPreferredSize(new Dimension(500, 500));
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        JButton addBall = new JButton("add Metaball");
        JButton addNegBall = new JButton("add Negative Metaball");
        addBall.addActionListener(this);
        this.add(addBall);
        addNegBall.addActionListener(this);
        this.add(addNegBall);

        JSpinner radiusSpinner = new JSpinner(radiusValue);
        radiusSpinner.setMinimumSize(new Dimension(50,50));
        JLabel radiusLabel = new JLabel("Size");

        this.add(radiusSpinner);
        this.add(radiusLabel);

        Metaball mball = new Metaball(100, 100, 25, Color.BLUE, "BLUE", false);
        mballs.add(mball);
        Metaball mball2 = new Metaball(500, 100, 25, Color.RED, "RED", false);
        mballs.add(mball2);
        Metaball mball3 = new Metaball(100, 500, 25, Color.GREEN, "GREEN", false);
        mballs.add(mball3);
        Metaball mball4 = new Metaball(500, 500, 100, Color.YELLOW, "YELLOW", false);
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
        for (double x = m.x - m.falseR; x <= m.x + m.falseR + Math.abs(distort); x++) {
            for (double y = m.y - m.falseR; y <= m.y + m.falseR + Math.abs(distort); y++) {
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
                    System.out.println(mball.name + "<->" + altball.name);
                    System.out.println("distance: " + dist);
                    System.out.println("falloff: " + (100 / dist));
                }
            }
            exclusion++;
        }
    }

    public double metaDistortion(Metaball m, double x, double y) {
        double distortion = 0;
        if (m.isNegative) {
            return 0;
        }
        for (Metaball mball : mballs) {
            if (mball != m) {

                if (mball.isNegative) {
                    double dist = Math.sqrt(Math.pow((mball.x - x), 2) + Math.pow((mball.y - y), 2));
                    distortion = distortion + (2000 * (-1 / dist));
                } else {
                    double dist = Math.sqrt(Math.pow(mball.x - x, 2) + Math.pow(mball.y - y, 2));
                    distortion = distortion + (1000 * (1 / dist));
                }
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
        System.out.println("Click: " + e.getButton());
        if (e.getButton() == 3) {
            boolean isDeleted;
            for (int i = 0; i < mballs.size(); i++) {
                isDeleted = mballs.get(i).boundCheck(e.getX(), e.getY());
//
                if (isDeleted) {
                    mballs.remove(i);
                    this.repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean isHeld;

        for (Metaball mball : mballs) {
            isHeld = mball.boundCheck(e.getX(), e.getY());
//            System.out.println("Pressed - X:" + e.getX() + " Y:" + e.getY());
//            System.out.println(isHeld);
            if (isHeld) {
                heldBall = mball;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        String command = e.getActionCommand();

        if (command.equals("add Metaball")) {
            Color color = colors[0];
            for (int i = 0; i <= mballs.size(); i++) {
                boolean present = false;
                for (Metaball m : mballs) {
                    if (m.color == colors[i]) {
                        present = true;
                    }
                }
                if (!present) {
                    color = colors[i];
                    break;
                }

            }
            int r = (int) radiusValue.getValue();
            Metaball m = new Metaball(100, 100, r, color, "GREEN", false);
            mballs.add(m);
            this.repaint();
        } else if (command.equals("add Negative Metaball")) {
            int r = (int) radiusValue.getValue();
            Metaball m = new Metaball(150, 150, r, Color.BLACK, "BLACK(-)", true);
            mballs.add(m);
            this.repaint();
        }
    }
}

