package dottodotmaker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
            g.drawImage(sourceImage, 0, 0, null);
            g.dispose();
            //now clone it to shownImage
            shownImage = new BufferedImage(overlay.getWidth(), overlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = shownImage.createGraphics();
            g2.drawImage(overlay, 0, 0, null);
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
        frame.add(theEverything);
        
        //Sets the frame to be visible
        frame.setSize(overlay.getWidth(), overlay.getHeight());
        frame.setVisible(true);
        
        paintDots();
        
        /********************************************************************
         *                          Keybindings ;D
         ********************************************************************/
        Action breakLine = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("breakLine");
                dotsArray.add(new ArrayList<>());
                clearDots();
                paintDots();
            }
        };  
        Action clearDots = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("clearDots");
                if (dotsArray.size() > 1){
                    dotsArray.remove(dotsArray.size() - 1);
                } else {
                    dotsArray.clear();
                    dotsArray.add(new ArrayList<>());
                }
                System.out.println(dotsArray);
                clearDots();
                paintDots();
            }
        };
        Action qualityPrint = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("qualityPrint");

                Graphics g = theEverything.getGraphics();
                Graphics2D g2 = (Graphics2D) theEverything.getGraphics();
                FontMetrics fm = g2.getFontMetrics();
                //System.out.println(g2.getFont().toString());
                g2.setFont(new Font("Dialog", Font.PLAIN, 12));
                
                //completely clear the screen
                g.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
                g.setColor(Color.white);
                g.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
                g.setColor(Color.black);
                //different radius from "paintDots" due to different exporting options
                //or something
                final int radius = 3;
                int numberCounter = 1;
                //custom dot drawing
                //for every strand...
                for (int i = 0; i < dotsArray.size(); i++){
                    //for every dot in said strand
                    for (int j = 0; j < dotsArray.get(i).size(); j++){
                        int x = dotsArray.get(i).get(j).getX();
                        int y = dotsArray.get(i).get(j).getY();
                        boolean star = false;
                        //just an ordinary dot
                        if (j != dotsArray.get(i).size() - 1){
                            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                        } 
                        //the "ending strand" dot: a star
                        else {
                            g2.setFont(new Font("Dialog", Font.PLAIN, 10)); 
                            g2.drawString("★", x - (fm.stringWidth("★") / 2), y);
                            g2.setFont(new Font("Dialog", Font.PLAIN, 12));
                            star = true;
                            //g.fillRect(x - (radius / 2), y - (radius / 2), radius, radius);
                        }
                        
                        //now draw the number
                        String dotNumber = numberCounter + "";
                        if (!star){
                            g2.drawString(dotNumber, x - (fm.stringWidth(dotNumber) / 2), y - 5);
                        } else {
                            g2.drawString(dotNumber, x - (fm.stringWidth(dotNumber) / 2), y - 7); 
                        }
                        numberCounter++;
                    }
                }                
                
                g.dispose();
                g2.dispose();
            }
        };   
        
        theEverything.getInputMap().put(KeyStroke.getKeyStroke("F"), "breakLine");
        theEverything.getActionMap().put("breakLine", breakLine);
        
        theEverything.getInputMap().put(KeyStroke.getKeyStroke("R"), "clearDots");
        theEverything.getActionMap().put("clearDots", clearDots);
       
        theEverything.getInputMap().put(KeyStroke.getKeyStroke("D"), "qualityPrint");
        theEverything.getActionMap().put("qualityPrint", qualityPrint);        
        
        MouseListener l = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Dot newDot = new Dot(x, y); 
                //System.out.println(x + " " + y);
                //add to the end of the latest dot array
                dotsArray.get(dotsArray.size() - 1).add(newDot);
                clearDots();
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
        
        //now lets draw some dots
        Graphics g = theEverything.getGraphics();
        g.setColor(Color.BLACK);

        //for every strand
        for (int i = 0; i < dotsArray.size(); i++){
            //for every dot in said strand
            for (int j = 0; j < dotsArray.get(i).size(); j++){
                int x = dotsArray.get(i).get(j).getX();
                int y = dotsArray.get(i).get(j).getY();
                //System.out.println(x + " " + y);
                //now for the actual dot
                //huh it's not drawing for some reason
                //or at least not visible
                g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
                //draw line
                if (j != dotsArray.get(i).size() - 1) {
                    g.drawLine(x, y, dotsArray.get(i).get(j + 1).getX(), dotsArray.get(i).get(j + 1).getY());
                }
            }
        }
        
        g.dispose();
    }
    
    private void clearDots(){
        Graphics g = theEverything.getGraphics();
        
        g.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
        g.drawImage(shownImage, 0, 0, null);

        g.dispose();
    }
    
}
