import javax.swing.*;
import java.awt.event.*;

public class MyFrame extends JFrame {
    MyPanel panel;

    MyFrame() {
        panel = new MyPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

//        panel.addMouseMotionListener(this);
    }


//    @Override
//    public void mouseDragged(MouseEvent e) {
//        System.out.println("Dragged");
//        System.out.println("X:" + e.getX() + " Y:" + e.getY());
//        System.out.println(e);
//    }
//
//    @Override
//    public void mouseMoved(MouseEvent e) {
//        System.out.println("moved");
//    }
}
