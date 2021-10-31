import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class JuliaSet extends JPanel {
    private static final int MAX_ITERATIONS = 60;
    private static final double RADIUS = 0.7885;

    private int angle = 0; // 0..2 * Math.PI

    public JuliaSet() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.white);
    }

    void drawJuliaSet(Graphics2D graph) {
        final int w = getWidth();
        final int h = getHeight();

        final double midX = w / 2.0;
        final double midY = h / 2.0;

        final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        double a = Math.toRadians(angle);
        final double CX = RADIUS * Math.cos(a);
        final double CY = RADIUS * Math.sin(a);

//        final int total = w * h;
//        for (int idx = 0; idx < total; idx++) {
//            final int x = idx / h;
//            final int y = idx % h;
//            final int r = (int) (x * 255F / w);
//            final int b = (int) (y * 255F / h);
//
//            double zx = (2.0 * x - w) / midX; // (2 * x - w) / (w / 2)
//            double zy = (2.0 * y - h) / midY;
//            int i = 0;
//            while (i < MAX_ITERATIONS && (zx * zx + zy * zy) <= 4.0) {
//                double tmpZx = zx * zx - zy * zy + CX;
//                zy = 2 * zx * zy + CY;
//                zx = tmpZx;
//                i++;
//            }
//            final int g = 255 * i / MAX_ITERATIONS;
//            image.setRGB(x, y, new Color(r, g, b).getRGB());
//        }

        IntStream.range(0, w).parallel().forEach(x ->
                IntStream.range(0, h).parallel().forEach(y -> {
                    final int r = (int) (x * 255F / w);
                    final int b = (int) (y * 255F / h);

                    double zx = (2.0 * x - w) / midX; // (2 * x - w) / (w / 2)
                    double zy = (2.0 * y - h) / midY;
                    int i = 0;
                    while (i < MAX_ITERATIONS && (zx * zx + zy * zy) <= 4.0) {
                        double tmpZx = zx * zx - zy * zy + CX;
                        zy = 2 * zx * zy + CY;
                        zx = tmpZx;
                        i++;
                    }
                    final int g = 255 * i / MAX_ITERATIONS;
                    image.setRGB(x, y, new Color(r, g, b).getRGB());
                }));
        graph.drawImage(image, 0, 0, null);
    }

    private void onTick() {
        angle++;
        angle %= 360;
        repaint();
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        final Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawJuliaSet(g);
    }

    public static void main(String[] args) {
//        int width = 800;
//        int height = 600;
//        int total = width * height;
//
//        int maxX = 0, maxY = 0;
//        for (int i = 0; i < total; i++) {
//            int x = i % width;
//            int y = i / width;
//            if (x > maxX) {
//                maxX = x;
//            }
//            if(y > maxY) {
//                maxY = y;
//            }
//            System.out.println("Idx " + i + ", x " + x + ", y " + y);
//        }


        SwingUtilities.invokeLater(() -> {
            final JuliaSet set = new JuliaSet();
            final Timer timer = new Timer(75, ae -> {
                set.onTick();
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.setInitialDelay(0);
            final JFrame f = new JFrame();
            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    timer.stop();
                }

                @Override
                public void windowOpened(WindowEvent e) {
                    timer.start();
                }
            });
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Julia Set");
            f.setResizable(false);
            f.add(set, BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
