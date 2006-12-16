/*
 * FPManager.java
 *
 * Created on 25 de Setembro de 2006, 19:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.fingerprintprocessing;

import br.com.teoni.fpreader.imageprocessing.*;
import br.com.teoni.fpreader.math.Moda;
import br.com.teoni.fpreader.model.Fingerprint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Teoni
 */
public class FPManager {
    
    public static Fingerprint getFingerprint(String url){
        BufferedImage bfImage = null;
        try {
            bfImage = ImageIO.read(new File(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Fingerprint fingerprint = new Fingerprint(url);
        fingerprint.setWidth(bfImage.getWidth());
        fingerprint.setHeight(bfImage.getHeight());
        fingerprint.setBinaryImage(BasicOperations.applyFilters(bfImage));
        fingerprint.setBufferedImage(bfImage);
        fingerprint.setSkeleton(fingerprint.getBinaryImage());
        return fingerprint;
    }
    
    public static Fingerprint getFingerprint(ImageProducer producer){
        Image image = Toolkit.getDefaultToolkit().createImage(producer);
        BufferedImage bfImage = new BufferedImage(image.getWidth(null)-50, image.getHeight(null)-50,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bfImage.createGraphics();
        g2d.drawImage(image,-50,-50,null);
        g2d.dispose();
        
        Fingerprint fingerprint = new Fingerprint("");
        fingerprint.setWidth(bfImage.getWidth());
        fingerprint.setHeight(bfImage.getHeight());
        fingerprint.setBinaryImage(BasicOperations.applyFilters(bfImage));
        fingerprint.setBufferedImage(bfImage);
        fingerprint.setSkeleton(fingerprint.getBinaryImage());
        return fingerprint;
    }
    
    public static BufferedImage getDirectionalImage(Fingerprint fingerprint){
        Image image = fingerprint.getImage();
        BufferedImage bfImage2 = new BufferedImage(fingerprint.getWidth(), fingerprint.getHeight(),image.SCALE_SMOOTH);
        BufferedImage bfImage = fingerprint.getBufferedImage();
        Graphics2D g2d = bfImage2.createGraphics();
        
        int window = 9;
        int vSquares = (int)Math.floor(fingerprint.getWidth()/window);
        int hSquares = (int)Math.floor(fingerprint.getHeight()/window);
        int n = (int)Math.floor(window/2);
        
        int[][] directions = new int[vSquares-2][hSquares-2];
        
        g2d.drawImage(image,0,0,null);
        
        //Tratando cada 9x9
        g2d.setColor(Color.red);
        for(int i=1; i<vSquares-1; i++){
            for(int j=1; j<hSquares-1; j++){
                //Pixel central
                Point pixel = new Point((i*window)+n,(j*window)+n);
                int greyLevel = 255-(new Color(bfImage.getRGB(pixel.x, pixel.y))).getRed();
                
                //Outros dados
                int direction = 0;
                int sums = 0;
                
                int[] s = new int[]{0,0,0,0,0,0,0,0};
                 
                for(int w=-n; w<=n/2; w++){
                    s[0] += 255-(new Color(bfImage.getRGB(pixel.x,pixel.y+2*w)).getRed());
                    s[1] += 255-(new Color(bfImage.getRGB(pixel.x+w,pixel.y-2*w)).getRed());
                    s[2] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y-2*w)).getRed());
                    s[3] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y-w)).getRed());
                    s[4] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y)).getRed());
                    s[5] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y+w)).getRed());
                    s[6] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y+2*w)).getRed());
                    s[7] += 255-(new Color(bfImage.getRGB(pixel.x+w,pixel.y+2*w)).getRed());
                }
                
                for(int k=0; k<s.length; k++){
                    s[k] = s[k]-greyLevel;
                }
                
                int smin = 0;
                int smax = 0;
                
                for(int w=0; w<s.length; w++){
                    if(s[w]<s[smin]){
                        smin = w;
                    }
                    if(s[w]>s[smax]){
                        smax = w;
                    }
                }
                
                /*
                 Arrays.sort(s);
                 smin = s[0];
                 smax = s[s.length-1];
                 */
                
                if((4*greyLevel+smin+smax)<((3*sums)/s.length)){
                    directions[i-1][j-1] = smax;
                }else{
                    directions[i-1][j-1] = smin;
                }
            }
        }
        
        /*
        int dirWindow = 3;
        int dirVSquares = (int)Math.floor(directions.length/dirWindow);
        int dirHSquares = (int)Math.floor(directions[0].length/dirWindow);
        n = (int)Math.floor(dirWindow/2);
        int[][] newDirections = new int[directions.length][directions[0].length];
        
        for(int i=0; i<dirVSquares; i++){
            for(int j=0; j<dirHSquares; j++){
                int x = (i*dirWindow)+n;
                int y = (j*dirWindow)+n;
                
                Moda moda = new Moda();
                
                moda.adicionar(directions[x-1][y]);
                moda.adicionar(directions[x-1][y+1]);
                moda.adicionar(directions[x][y+1]);
                moda.adicionar(directions[x+1][y+1]);
                moda.adicionar(directions[x+1][y]);
                moda.adicionar(directions[x+1][y-1]);
                moda.adicionar(directions[x][y-1]);
                moda.adicionar(directions[x-1][y-1]);
                moda.adicionar(directions[x][y]);
                
                int newDirection = moda.calcular().intValue();
                
                directions[x-1][y] = newDirection;
                directions[x-1][y+1] = newDirection;
                directions[x][y+1] = newDirection;
                directions[x+1][y+1] = newDirection;
                directions[x+1][y] = newDirection;
                directions[x+1][y-1] = newDirection;
                directions[x][y-1] = newDirection;
                directions[x-1][y-1] = newDirection;
                directions[x][y] = newDirection;
                
            }
        }
        */
        //Desenhando as linhas
        for(int i=0; i<directions.length; i++){
            for(int j=0; j<directions[0].length; j++){
                switch(directions[i][j]){
                    case 0:
                        g2d.drawLine((i*window),(j*window)+6,(i*window)+10,(j*window)+6);
                        break;
                    case 1:
                        g2d.drawLine((i*window),(j*window)+8,(i*window)+10,(j*window)+4);
                        break;
                    case 2:
                        g2d.drawLine((i*window),(j*window)+10,(i*window)+10,(j*window));
                        break;
                    case 3:
                        g2d.drawLine((i*window)+4,(j*window)+10,(i*window)+8,(j*window));
                        break;
                    case 4:
                        g2d.drawLine((i*window)+6,(j*window)+10,(i*window)+6,(j*window));
                        break;
                    case 5:
                        g2d.drawLine((i*window)+8,(j*window)+10,(i*window)+4,(j*window));
                        break;
                    case 6:
                        g2d.drawLine((i*window)+10,(j*window)+10,(i*window),(j*window));
                        break;
                    case 7:
                        g2d.drawLine((i*window)+10,(j*window)+8,(i*window),(j*window)+4);
                        break;
                }
            }
        }
        
        g2d.dispose();
        return bfImage2;
    }
    
    public static Fingerprint mapMinutiaes(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50;
        int bif = 0;
        int eol = 0;
        
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
        
        for(int i=margin+20; i<width-margin-20; i++){
            for(int j=margin; j<height-margin; j++){
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1){
                    if(patterns==1){
                        outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                    }
                    if(patterns==3){
                        outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                    }
                }
            }
        }
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
    
    private static byte[][] drawRectangle(int x, int y, byte[][] skeleton, int color){
        int size = 3;
        for(int i=-size; i<=size; i++){
            skeleton[x-i][y+size] = (byte)color;
            skeleton[x+i][y-size] = (byte)color;
            skeleton[x-size][y+i] = (byte)color;
            skeleton[x+size][y-i] = (byte)color;
        }
        return skeleton;
    }
    
    public static Image toImage(byte[][] image){
        int width = image.length;
        int height = image[0].length;
        BufferedImage bfImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                if(image[i][j]==0){
                    bfImage.setRGB(i,j,Color.white.getRGB());
                }
                if(image[i][j]==1){
                    bfImage.setRGB(i,j,Color.black.getRGB());
                }
                if(image[i][j]==2){
                    bfImage.setRGB(i,j,Color.blue.getRGB());
                }
                if(image[i][j]==3){
                    bfImage.setRGB(i,j,Color.red.getRGB());
                }
                if(image[i][j]==4){
                    bfImage.setRGB(i,j,Color.green.getRGB());
                }
            }
        }
        return bfImage.getScaledInstance(width, height,0);
    }
    
}
