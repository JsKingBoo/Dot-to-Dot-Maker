package dottodotmaker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author JsKingBoo
 */
public class DotToDotMaker {

    private BufferedImage overlay;
    private BufferedImage shownImage;
    
    //Every subarray is a new "strand"
    private ArrayList<ArrayList<Dot>> dotsArray = new ArrayList<>();
    
    private JFrame frame;
    private JLabel theEverything;
    
    private File source;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DotToDotMaker dots = new DotToDotMaker();
    }

    public DotToDotMaker() {
        
        //Import the overlay
        source = new File("C:\\temp\\image.png");
        try {
            BufferedImage sourceImage = ImageIO.read(source);
            overlay = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = overlay.createGraphics();
            g.drawImage(sourceImage, null, 0, 0);
            g.dispose();
            //now clone it to shownImage
            shownImage = new BufferedImage(overlay.getWidth(), overlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = shownImage.createGraphics();
            g2.drawImage(overlay, null, 0, 0);
            g2.dispose();
        }
        catch (IOException e) {
            System.out.println("Could not import overlay");
            System.out.println(e);
        }
        
        //Add the starter strand to the dot array
        dotsArray.add(new ArrayList<>());
        
        //Initialize the frame
        frame = new JFrame("Dot to Dot Maker");
        
        //Adds the background to the frame
        shownImage = setImageAlpha(shownImage, (byte) 128);
        theEverything = new JLabel(new ImageIcon(shownImage));
        //frame.add(theEverything);
        
        //Sets the frame to be visible
        frame.setSize(overlay.getWidth(), overlay.getHeight());
        frame.setVisible(true);
        
        /********************************************************************
         *                          Keybindings ;D
         ********************************************************************/
        Action breakLine = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("breakLine");
                dotsArray.add(new ArrayList<>());
            }
        };  
        Action clearDots = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("clearDots");
                dotsArray.clear();
                dotsArray.add(new ArrayList<>());
            }
        };        
        
        theEverything.getInputMap().put(KeyStroke.getKeyStroke("F"), "breakLine");
        theEverything.getActionMap().put("breakLine", breakLine);
        
        theEverything.getInputMap().put(KeyStroke.getKeyStroke("R"), "clearDots");
        theEverything.getActionMap().put("clearDots", clearDots);
       
        MouseListener l = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Dot newDot = new Dot(x, y); 
                System.out.println(x + " " + y);
                //add to the end of the latest dot array
                dotsArray.get(dotsArray.size() - 1).add(newDot);
                paintDots();
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        };
        
        theEverything.addMouseListener(l);
        
    }
    
    private BufferedImage setImageAlpha(BufferedImage image, byte alpha){
        //Thank you Felix!
        for (int cx = 0; cx < image.getWidth(); cx++) {          
            for (int cy = 0; cy < image.getHeight(); cy++) {
                int color = image.getRGB(cx, cy);
                int mc = (alpha << 24) | 0x00FFFFFF;
                int newcolor = color & mc;
                image.setRGB(cx, cy, newcolor);            
            }
        }       
        return image;
    }
    
    private void paintDots(){
        final int radius = 3;
        //hell i broke it :(
        Graphics g = shownImage.getGraphics();  
        
        //for every strand
        for (int i = 0; i < dotsArray.size(); i++){
            //for every dot in said strand
            for (int j = 0; j < dotsArray.get(i).size(); j++){
                int x = dotsArray.get(i).get(j).getX();
                int y = dotsArray.get(i).get(j).getY();

                //now for the actual dot
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                //draw line
                if (j != dotsArray.get(i).size() - 1) {
                    g.drawLine(x, y, dotsArray.get(i).get(j + 1).getX(), dotsArray.get(i).get(j + 1).getY());
                }
            }
        }
        
        g.dispose();
    }
    
}
