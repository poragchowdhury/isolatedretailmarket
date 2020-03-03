package BranchWork;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Drawing {
    private static JFrame window;
    private static JLabel imageFrame;

    private static void initWindow(BufferedImage im) {
        if (window != null)
            return;

        // Initialize window and image frame
        window = new JFrame();
        window.setSize(im.getWidth() + 150, im.getHeight() + 150);
        window.setLayout(new FlowLayout());
        window.setVisible(true);

        imageFrame = new JLabel();
        window.add(imageFrame);

    }

    public static void displayImage(String filename) {
        BufferedImage im = null;
        try {
            im = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initWindow(im);

        ImageIcon icon = new ImageIcon(im);
        imageFrame.setIcon(icon);
    }
}
