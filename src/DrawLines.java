import javax.swing.*;
import java.awt.*;

public class DrawLines extends JFrame {
    private double[] x;
    private double[] y;

    public DrawLines(double[] x, double[] y) {
        this.x = x;
        this.y = y;
        initUI();
    }

    private void initUI() {
        setTitle("Draw Lines");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < x.length - 1; i++) {
            int x1 = (int) x[i];
            int y1 = (int) y[i];
            int x2 = (int) x[i + 1];
            int y2 = (int) y[i + 1];
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            double[] xCoordinates = {10.0, 100.0, 150.0, 200.0};
            double[] yCoordinates = {50.0, 100.0, 50.0, 100.0};
            DrawLines example = new DrawLines(xCoordinates, yCoordinates);
            example.setVisible(true);
        });
    }
}
