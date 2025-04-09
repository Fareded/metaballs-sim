import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    Graphics2D g2D;
    ArrayList<Metaball> mballs = new ArrayList<>();
    Metaball heldBall = null;

    boolean distLines = false;
    boolean mergeBalls = false;
    SpinnerModel radiusValue =
            new SpinnerNumberModel(25, //initial value
                    5, //minimum value
                    50, //maximum value
                    5); //step

    SpinnerModel strengthValue =
            new SpinnerNumberModel(50, //initial value
                    0, //minimum value
                    50, //maximum value
                    5); //step

    SpinnerModel tempValue =
            new SpinnerNumberModel(1, //initial value
                    0, //minimum value
                    50, //maximum value
                    1); //step
    Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.PINK};
    Map<String, Color> colorBoxValues = new HashMap<>();


    JComboBox colorBox;
    JComboBox mixingType;

    MyPanel() {
        this.setPreferredSize(new Dimension(700, 700));
        this.setBackground(Color.WHITE);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

        colorBoxValues.put("Auto", Color.WHITE);
        colorBoxValues.put("Blue", Color.BLUE);
        colorBoxValues.put("Red", Color.RED);
        colorBoxValues.put("Green", Color.GREEN);
        colorBoxValues.put("Yellow", Color.YELLOW);
        colorBoxValues.put("Magenta", Color.MAGENTA);
        colorBoxValues.put("Orange", Color.ORANGE);
        colorBoxValues.put("Pink", Color.PINK);
        colorBoxValues.put("Cyan", Color.CYAN);
        colorBox = new JComboBox(colorBoxValues.keySet().toArray(new String[0]));

        this.add(colorBox);

//        JSpinner tempSpinner = new JSpinner(tempValue);
//        tempSpinner.setMinimumSize(new Dimension(50, 50));
//        JLabel tempLabel = new JLabel("Temp");
//        this.add(tempSpinner);
//        this.add(tempLabel);

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

        JCheckBox mergeCheck = new JCheckBox("Merge Balls");
        mergeCheck.addActionListener(this);
        this.add(mergeCheck);

        JButton sameColor = new JButton("Same Color");
        sameColor.addActionListener(this);
        this.add(sameColor);

        String[] mtypes = {"ADD", "SUB", "AVG"};
        mixingType = new JComboBox(mtypes);

        this.add(mixingType);

        Metaball mball = new Metaball(100, 100, 25, 50, Color.BLUE, "BLUE", false);
        mballs.add(mball);
        Metaball mball2 = new Metaball(300, 100, 25, 50, Color.RED, "RED", false);
        mballs.add(mball2);
        Metaball mball3 = new Metaball(100, 300, 25, 50, Color.GREEN, "GREEN", false);
        mballs.add(mball3);
        Metaball mball4 = new Metaball(300, 300, 25, 50, Color.YELLOW, "YELLOW", false);
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
        double distort = 0;


        for (double x = m.x - m.falseR; x <= m.x + m.falseR; x++) {
            for (double y = m.y - m.falseR; y <= m.y + m.falseR; y++) {
                double distance = Math.sqrt(Math.pow(x - m.x, 2) + Math.pow(y - m.y, 2));
                ArrayList distortionValues = metaDistortion(m, x, y);
                distort = (double) distortionValues.get(0);
                Color color = (Color) distortionValues.get(1);
                g2D.setColor(color);
                if (distance < m.r + distort && distance < m.falseR) {
                    g2D.fillRect((int) x, (int) y, 1, 1);
                }
            }
        }
    }


    public ArrayList metaDistortion(Metaball m, double x, double y) {
        double distortion = 0;
        ArrayList values = new ArrayList<>();
        Color gradient = m.color;
        String mixType = (String) mixingType.getSelectedItem();
        double R = 0;
        double G = 0;
        double B = 0;
        int c = mballs.size();

        switch (mixType) {
            case "ADD":
                R = 127.5 ;
                G = 127.5 ;
                B = 127.5 ;
                break;
            case "SUB":
                R = 127.5 ;
                G = 127.5 ;
                B = 127.5 ;
                break;
            case "AVG":
                R = 255 * 0.5;
                G = 255 * 0.5;
                B = 255 * 0.5;
                break;
        }

        if (!m.isNegative) {
            for (Metaball mball : mballs) {
                double dist = Math.sqrt(Math.pow((mball.x - x), 2) + Math.pow((mball.y - y), 2));

                if (mball != m) {
                    if (mball.isNegative) {
                        distortion = distortion + (mball.r * 50 * (-1 / dist));
                    } else {

                        distortion = distortion + (mball.r * mball.strength * (1 / dist));
                    }
                }
                if (!mball.isNegative) {
                    double p = (dist / 150);

                    p = Math.max(p, 0.75);
                    p = Math.min(p, 1);
                    switch (mixType) {
                        case "ADD":
                            if (p < 1) {
                                R = (R * p) + mball.color.getRed() * (1 - p);
                                G = (G * p) + mball.color.getGreen() * (1 - p);
                                B = (B * p) + mball.color.getBlue() * (1 - p);
                            }
                            break;
                        case "SUB":
                            if (p < 1) {
//
                                    R = (R * p) - mball.color.getRed() * (1 - p);
                                    G = (G * p) - mball.color.getGreen() * (1 - p);
                                    B = (B * p) - mball.color.getBlue() * (1 - p);
//
                            }
                            break;
                        case "AVG":
                            p = (dist / 150);
                            if (p <= 0.5) {
                                p = 0.5;
                            }
                            if (p < 1) {
                                R = (R * p + mball.color.getRed() * (1 - p)) / 2;
                                G = (G * p + mball.color.getGreen() * (1 - p)) / 2;
                                B = (B * p + mball.color.getBlue() * (1 - p)) / 2;
                            }
                            break;
                    }
                }

            }
//            System.out.println(R + " " + G + " " + B);

            R = R < 0 ? 0 : R;
            B = B < 0 ? 0 : B;
            G = G < 0 ? 0 : G;
            gradient = new Color((int) R, (int) G, (int) B);
        }
        values.add(distortion);
        values.add(gradient);
        return values;
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

    public Metaball checkAllBounds(int x, int y) {
        for (Metaball m : mballs) {
            if (m.boundCheck(x, y)) {
                return m;
            }
        }
        return null;
    }

    public void mergeBalls() {
        for (Metaball m : mballs) {
            if (m != heldBall && !m.isNegative) {
                double d = Math.sqrt(Math.pow(m.x - heldBall.x, 2) + Math.pow(m.y - heldBall.y, 2));
                if (d < 10) {
                    Color c = mergeColor(heldBall.color, m.color, 0.5);
                    double size = (heldBall.r + m.r) * 0.75;
                    if (size > 100) {
                        size = 100;
                    }
                    Metaball merged = new Metaball(heldBall.x, heldBall.y, size, heldBall.strength, c, "Merged", false);
                    mballs.remove(m);
                    mballs.remove(heldBall);
                    mballs.add(merged);
                    heldBall = merged;
                    this.repaint();
                    break;
                }
            }
        }
    }

    public Color mergeColor(Color x, Color y, double p) {
        double R = x.getRed() * p + y.getRed() * (1 - p);
        double G = x.getGreen() * p + y.getGreen() * (1 - p);
        double B = x.getBlue() * p + y.getBlue() * (1 - p);

        return new Color((int) R, (int) G, (int) B);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (heldBall != null) {
            updateBallPos(e.getX(), e.getY());
            if (mergeBalls && !heldBall.isNegative) {
                mergeBalls();
            }
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Metaball m = checkAllBounds(e.getX(), e.getY());

        if (m != null) {
            this.setToolTipText("<html>" + m.name + "<br> " +
                    "Position: " + (int) m.x + "," + (int) m.y + "<br>" +
                    " Base Radius: " + m.r + "<br> " +
                    "Strength: " + m.strength + " </html>");
        } else {
            this.setToolTipText(null);
        }
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
                int count = mballs.size();
                for (Metaball m : mballs) {
                    if (m.isNegative){
                        count--;
                    }
                }
                if (count >= colorBoxValues.size() - 1) {
                    System.out.println("Maximum balls on screen");
                    break;
                }
                String colorName = (String) colorBox.getSelectedItem();
                Color color = colors[0];
                if (Objects.equals(colorName, "Auto")) {
                    for (Map.Entry<String, Color> c : colorBoxValues.entrySet()) {
                        if (c.getValue() != Color.WHITE) {
                            boolean present = false;
                            for (Metaball m : mballs) {
                                if (m.color == c.getValue()) {
                                    present = true;
                                    break;
                                }
                            }
                            if (!present) {
                                color = c.getValue();
                                colorName = c.getKey();
                                break;
                            }
                        }
                    }
                } else {
                    color = colorBoxValues.get(colorName);
                }
                int r = (int) radiusValue.getValue();
                int s = (int) strengthValue.getValue();

                System.out.println(colorName);
                Metaball m = new Metaball(100, 100, r, s, color, colorName, false);
                mballs.add(m);
            }
            case "+ Negative Metaball" -> {
                int r = (int) radiusValue.getValue();
                int s = (int) strengthValue.getValue();
                Metaball m = new Metaball(150, 150, r, s, Color.BLACK, "BLACK(-)", true);
                mballs.add(m);
            }
            case "Clear Screen" -> mballs.clear();
            case "Draw Distance Lines" -> {
                JCheckBox checkbox = (JCheckBox) e.getSource();
                distLines = checkbox.isSelected();
                System.out.println("drawLines?:" + distLines);
            }
            case "Merge Balls" -> {
                JCheckBox checkbox = (JCheckBox) e.getSource();
                mergeBalls = checkbox.isSelected();
            }
            case "Same Color" -> {
                for (Metaball m : mballs) {
                    if (!m.isNegative) {
                        m.color = Color.RED;
                    }
                }
            }
        }
        this.repaint();
    }
}

