import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MyPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    Graphics2D g2D;
    ArrayList<Metaball> mballs = new ArrayList<>();
    Metaball heldBall = null;

    boolean distLines = false;
    SpinnerModel radiusValue =
            new SpinnerNumberModel(25, //initial value
                    20, //minimum value
                    100, //maximum value
                    5); //step

    SpinnerModel strengthValue =
            new SpinnerNumberModel(1000, //initial value
                    0, //minimum value
                    3000, //maximum value
                    100); //step
    Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.PINK};

    MyPanel() {
        this.setPreferredSize(new Dimension(700, 700));
        this.setBackground(Color.WHITE);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        JButton addBall = new JButton("+ Metaball");
        JButton addNegBall = new JButton("+ Negative Metaball");
        addBall.addActionListener(this);
        this.add(addBall);
        addNegBall.addActionListener(this);
        this.add(addNegBall);

        JButton clearScreen = new JButton("Clear Screen");
        clearScreen.addActionListener(this);
        this.add(clearScreen);

        JSpinner radiusSpinner = new JSpinner(radiusValue);
        radiusSpinner.setMinimumSize(new Dimension(50, 50));
        JLabel radiusLabel = new JLabel("Radius");

        this.add(radiusSpinner);
        this.add(radiusLabel);

        JSpinner strengthSpinner = new JSpinner(strengthValue);
        strengthSpinner.setMinimumSize(new Dimension(50, 50));
        JLabel strengthLabel = new JLabel("Strength");

        this.add(strengthSpinner);
        this.add(strengthLabel);

        JCheckBox lineCheck = new JCheckBox("Draw Distance Lines");
        lineCheck.addActionListener(this);
        this.add(lineCheck);

        JButton sameColor = new JButton("Same Color");
        sameColor.addActionListener(this);
        this.add(sameColor);

        Metaball mball = new Metaball(100, 100, 25, 1000, Color.BLUE, "BLUE", false);
        mballs.add(mball);
        Metaball mball2 = new Metaball(200, 100, 25, 1000, Color.RED, "RED", false);
        mballs.add(mball2);
        Metaball mball3 = new Metaball(100, 200, 25, 1000, Color.GREEN, "GREEN", false);
        mballs.add(mball3);
        Metaball mball4 = new Metaball(200, 200, 25, 1000, Color.YELLOW, "YELLOW", false);
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
//        System.out.println(distLines);
        if (distLines) {
            drawBallDistance();
        }
    }

    public void displayMetaball(Metaball m) {
        g2D.setColor(m.color);
        double distort = (double) metaDistortion(m, m.x, m.y).get(0);
        for (double x = m.x - m.falseR; x <= m.x + m.falseR + Math.abs(distort); x++) {
            for (double y = m.y - m.falseR; y <= m.y + m.falseR + Math.abs(distort); y++) {
                ArrayList distortionValues = metaDistortion(m, x, y);
                distort = (double) distortionValues.get(0);
                Color color = (Color) distortionValues.get(1);
                g2D.setColor(color);
                double radius = m.r + distort;
                double r_sqrd = Math.pow(radius, 2);
                double d_sqrd = Math.pow(x - m.x, 2) + Math.pow(y - m.y, 2);
                if (d_sqrd < r_sqrd) {
                    g2D.fillRect((int) x, (int) y, 1, 1);
                }
            }
        }
//        System.out.println("ball drawn");
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

    public ArrayList metaDistortion(Metaball m, double x, double y) {
        double distortion = 0;
        ArrayList values = new ArrayList<>();
        Color gradient = m.color;

        if (!m.isNegative) {
            for (Metaball mball : mballs) {
                if (mball != m) {

                    double dist = Math.sqrt(Math.pow((mball.x - x), 2) + Math.pow((mball.y - y), 2));
                    if (mball.isNegative) {
                        distortion = distortion + (mball.r * 50 * (-1 / dist));
                    } else {
                        distortion = distortion + (mball.r * 50 * (1 / dist));

                        double p = (dist / 100);
                        if (p <= 0.5) {
                            p = 0.5;
                        }
                        if (p < 1) {
                            double R = m.color.getRed() * p + mball.color.getRed() * (1 - p);
                            double G = m.color.getGreen() * p + mball.color.getGreen() * (1 - p);
                            double B = m.color.getBlue() * p + mball.color.getBlue() * (1 - p);

                            gradient = new Color((int) R, (int) G, (int) B);
                        }
                    }
                }
            }
        }

        values.add(distortion);
        values.add(gradient);
        return values;
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

        switch (command) {
            case "+ Metaball" -> {
                Color color = colors[0];
                for (int i = 0; i <= mballs.size(); i++) {
                    boolean present = false;
                    for (Metaball m : mballs) {
                        if (m.color == colors[i]) {
                            present = true;
                            break;
                        }
                    }
                    if (!present) {
                        color = colors[i];
                        break;
                    }

                }
                int r = (int) radiusValue.getValue();
                int s = (int) strengthValue.getValue();
                Metaball m = new Metaball(100, 100, r, s, color, "GREEN", false);
                mballs.add(m);
            }
            case "+ Negative Metaball" -> {
                int r = (int) radiusValue.getValue();
                int s = (int) strengthValue.getValue();
                Metaball m = new Metaball(150, 150, r, s, Color.BLACK, "BLACK(-)", true);
                mballs.add(m);
            }
            case "Clear Screen" -> {
                while (mballs.size() != 0) {
                    mballs.remove(mballs.get(0));
                }
            }
            case "Draw Distance Lines" -> {
                JCheckBox checkbox = (JCheckBox) e.getSource();
                distLines = checkbox.isSelected();
                System.out.println("drawLines?:" + distLines);
            }
            case "Same Color" -> {
                for (Metaball m:mballs) {
                    m.color = Color.RED;
                }
            }
        }
        this.repaint();
    }
}

