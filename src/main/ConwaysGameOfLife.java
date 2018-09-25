package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConwaysGameOfLife {

    private static final Random RNG = new Random();

    private int[][] pixels;
    private ConwaysGameOfLifeDisplay display;
    private int width;
    private int height;
    private long timeDelay;
    private Random rng;
    private ScheduledExecutorService executor;

    private ConwaysGameOfLife(int width, int height, long timeDelay) {
        this.width = width;
        this.height = height;
        pixels = new int[width][height];
        display = new ConwaysGameOfLifeDisplay();
        display.addMouseListener(new MouseListener());
        display.addKeyListener(new KeyListener());
        rng = new Random();
        executor = Executors.newScheduledThreadPool(10);
        this.timeDelay = timeDelay;
    }

    private void run(){
        System.out.println("Use 'c' to clear, 'e' to exit, 's', 'w', and 'r' to draw shapes on current mouse location");
        executor.schedule(() -> update(), timeDelay, TimeUnit.MILLISECONDS);
    }

    private void update(){
        int [][] updatedPixels = new int[width][height];
         for ( int  i = 0; i < width; i++ ){
            for ( int j = 0; j < height; j++ ){
                updatePixel(i, j, updatedPixels);
            }
        }
        pixels = updatedPixels;
        display.repaint();
        executor.schedule(() -> update(), timeDelay, TimeUnit.MILLISECONDS );
    }

    private void updatePixel(int i, int j, int[][] updatedPixels) {
        int liveNeighbours = countLiveNeighbours(i, j);

        if ( pixels[i][j] == 0 ){
            updatedPixels[i][j] = liveNeighbours == 3 ? 1 : 0;
            return;
        }

        if (liveNeighbours < 2 || liveNeighbours > 3){
            updatedPixels[i][j] = 0;
            return;
        }

        updatedPixels[i][j] = 1;

    }

    private int countLiveNeighbours(int i, int j) {

        int count = 0;
        for ( int ii = -1; ii < 2; ii++ ){
            for ( int jj = -1; jj < 2; jj++ ){
                if (i + ii >= 0 && i + ii < width &&
                        j + jj >= 0 && j + jj < height ){
                    count += pixels[i+ii][j+jj];
                }
            }
        }
        return count - pixels[i][j];
    }


    public static void main(String[] args) {
        Scanner sysIn = new Scanner(System.in);
        System.out.println("Input window width ( suggested width: 400 )...");
        int width = sysIn.nextInt();
        System.out.println("Input window height ( suggested height: 200 )...");
        int height = sysIn.nextInt();
        System.out.println("Input update delay ( suggested delay: 100 - 500 )...");
        int updateDelay = sysIn.nextInt();
        ConwaysGameOfLife cgol = new ConwaysGameOfLife(width, height, updateDelay);
        cgol.run();
    }

    private class ConwaysGameOfLifeDisplay extends JFrame {

        private ConwaysGameOfLifeDisplay(){
            this.setPreferredSize(new Dimension(width, height));
            this.pack();
            this.setVisible(true);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        }

        @Override
        public void paint(Graphics g){
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int colour = -1;
            //int colour = rng.nextInt(Integer.MAX_VALUE);
            for ( int  i = 0; i < width; i++ ) {
                for (int j = 0; j < height; j++) {
                    if (pixels[i][j] != 0) {
                        image.setRGB(i, j, colour);
                    }
                }
            }
            image.flush();
            g.drawImage(image, 0, 0, null);

        }
    }

    private class KeyListener implements java.awt.event.KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if ( e.getKeyChar() == 'c'){
                clear();
                return;
            }

            if ( e.getKeyChar() == 'e'){
                System.exit(0);
            }

            Point point = display.getMousePosition();

            int x = point.x;
            int y = point.y;
            if ( e.getKeyChar() == 'r'){
                drawRandom(x, y, RNG.nextInt(50 ) +25);
                return;
            }
            if ( e.getKeyChar() == 's'){
                drawSquare(x, y, RNG.nextInt(50 ) +25);
                return;
            }
            if ( e.getKeyChar() == 'w'){
                drawWeird(x, y, RNG.nextInt(25 ) +25);
                return;
            }
        }

        private void clear() {
            for ( int i = 0; i < width; i++ ){
                for ( int j = 0; j < height; j++ ){
                    pixels[i][j] = 0;
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    private class MouseListener implements java.awt.event.MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            switch ( RNG.nextInt(3)){
                case 0:
                    System.out.println("Square");
                    drawSquare(x, y, RNG.nextInt(25 ) +25);
                    break;
                case 1:
                    System.out.println("Weird");
                    drawWeird(x, y, RNG.nextInt(25 ) +25);
                    break;
                case 2:
                    System.out.println("Random");
                    drawRandom(x, y, RNG.nextInt(25 ) +25);
                    break;
            }
            System.out.println(x+ ", " +y);
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    private void drawSquare(int x, int y, int sideLen){
        for (int i = 0; i < sideLen / 2; i++ ){
            for (int j = 0; j < sideLen / 2; j++ ){
                pixels[x + i][y + j] = 1;
                pixels[x - i][y + j] = 1;
                pixels[x + i][y - j] = 1;
                pixels[x - i][y - j] = 1;
            }
        }
    }

    private void drawRandom(int x, int y, int sideLen){

        for (int i = 0; i <= sideLen/2; i++ ){
            for ( int j = 0; j <= sideLen/2; j++ ){
                int val = ( sideLen + 3*i + 7*j )% 2;
                pixels[x + i][y + j] = val;
                pixels[x - i][y + j] = val;
                pixels[x + i][y - j] = val;
                pixels[x - i][y - j] = val;
            }
        }
    }

    private void drawWeird(int x, int y, int radius){
        for (int i = 0; i <= radius; i++ ){
            for ( int j = 0; j < i; j++ ){
                pixels[x + i][y + j] = 1;
                pixels[x - i][y + j] = 1;
                pixels[x + i][y - j] = 1;
                pixels[x - i][y - j] = 1;
            }
        }
    }
}
