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
import br.com.teoni.fpreader.model.Output;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
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
        
        //Pegando a imagem binarizada
        byte[][] image = BasicOperations.binarizeImage(bfImage);
        
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
    
    public static Fingerprint getFingerprint(BufferedImage bfImage){
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        
        //Pegando a imagem binarizada
        byte[][] image = BasicOperations.binarizeImage(bfImage);
        
        //Removendo as bordas
        for(int i=0; i<width; i++){
            image[i][0] = 0;
            image[i][height-1] = 0;
        }
        
        for(int j=0; j<height; j++){
            image[0][j] = 0;
            image[width-1][j] = 0;
        }
        
        Fingerprint fingerprint = new Fingerprint("");
        fingerprint.setWidth(width);
        fingerprint.setHeight(height);
        fingerprint.setBinaryImage(image);
        
        return fingerprint;
    }
    
    public static Fingerprint mapMinutiaes(Fingerprint fingerprint){
        
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        
        for(int i=100; i<width-100; i++){
            for(int j=100; j<height-100; j++){
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
        
        //Point core = getCore(fingerprint,10);
        //outSkeleton = drawRectangle(core.x,core.y,outSkeleton,4);
        
        fingerprint.setSkeleton(outSkeleton);
        return fingerprint;
    }
    
    public static Point getCore(Fingerprint fingerprint, int searchRadius){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] skeleton = BasicOperations.copy(fingerprint.getSkeleton());
        
        Point core = new Point(0,0);
        Point previous = new Point(0,0);
        
        Double gradCur = 0.0;
        Double gradPrev = 0.0;
        Double gradChangeBig = 0.0;
        Double gradChange = 0.0;
        Double gradDistanceBig = 0.0;
        Double gradDistance = 0.0;
        
        for(int i=50; i<width-50; i++){
            for(int j=50; j<height-50; j++){
                if(skeleton[i][j]==1){
                    int control = 0;
                    Point p1 = new Point();
                    Point p2 = new Point();
                    
                    for(int m=-1*searchRadius; m<=searchRadius; m++){
                        for(int n=-1*searchRadius; n<=searchRadius; n++){
                            if(m==searchRadius||m==-1*searchRadius||n==searchRadius||n==-1*searchRadius){
                                int x = i+m;
                                int y = j+n;
                                if(skeleton[x][y]==1){
                                    control++;
                                    if(control==1){
                                        p1.setLocation(x,y);
                                    }else if(control==2){
                                        p2.setLocation(x,y);
                                    }
                                }
                            }
                        }
                    }
                    if(control==2){
                        if((p2.x-p1.x)>0){
                            gradCur = Double.valueOf(((double)(p2.y - p1.y)/(double)(p2.x - p1.x)));
                            // if(gradCur>0.0 && gradPrev<0.0){
                            gradChange = Double.valueOf(Math.abs(gradCur)+Math.abs(gradPrev));
                            gradDistance = Double.valueOf(Math.abs(i)-Math.abs(previous.x));
                            if(gradChangeBig<gradChange){
                                if(gradDistanceBig<gradDistance){
                                    gradChangeBig = gradChange;
                                    gradDistanceBig = gradDistance;
                                    core.setLocation(i,j);
                                }
                                break;
                            }
                            //}
                            gradPrev = gradCur;
                            gradCur = 0.0;
                            previous.setLocation(i,j);
                        }
                    }
                }
            }
        }
        return core;
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
