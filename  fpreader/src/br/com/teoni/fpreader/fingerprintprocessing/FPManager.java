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
import br.com.teoni.fpreader.model.Fingerprint;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Teoni
 */
public class FPManager {
    
    public static Fingerprint getFingerprint(String url) throws IOException{
        BufferedImage bfImage = ImageIO.read(new File(url));
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        byte[][] image = new byte[width][height];
        
        //Binarizando a Imagem
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                Color c = new Color(bfImage.getRGB(i,j));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red<127 && green<127 && blue<127){
                    image[i][j] = 1;
                }else{
                    image[i][j] = 0;
                }
            }
        }
        
        //Removendo as bordas
        for(int i=0; i<width; i++){
            image[i][0] = 0;
            image[i][height-1] = 0;
        }
        for(int j=0; j<height; j++){
            image[0][j] = 0;
            image[width-1][j] = 0;
        }
        
        Fingerprint fingerprint = new Fingerprint(url);
        fingerprint.setWidth(width);
        fingerprint.setHeight(height);
        fingerprint.setBinaryImage(image);
        
        return fingerprint;
    }
    
    public static Fingerprint mapMinutiaes(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        
        for(int i=5; i<width-5; i++){
            for(int j=5; j<height-5; j++){
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1){
                    if(patterns==1){
                        outSkeleton = drawRectangle(i,j,outSkeleton,2);
                    }
                    if(patterns==3){
                        outSkeleton = drawRectangle(i,j,outSkeleton,3);
                    }
                }
            }
        }
        
        fingerprint.setSkeleton(outSkeleton);
        return fingerprint;
    }
    
    private static byte[][] drawRectangle(int x, int y, byte[][] skeleton, int color){
        
        for(int i=0; i<3; i++){
            skeleton[x][y-i] = (byte)color;
            skeleton[x+i][y-i] = (byte)color;
            skeleton[x+i][y] = (byte)color;
            skeleton[x+i][y+i] = (byte)color;
            skeleton[x][y+i] = (byte)color;
            skeleton[x-i][y+i] = (byte)color;
            skeleton[x-i][y] = (byte)color;
            skeleton[x-i][y-i] = (byte)color;
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
                    bfImage.setRGB(i,j,Color.green.getRGB());
                }
                if(image[i][j]==3){
                    bfImage.setRGB(i,j,Color.red.getRGB());
                }
            }
        }
        return bfImage.getScaledInstance(width, height,0);
    }
    
}
