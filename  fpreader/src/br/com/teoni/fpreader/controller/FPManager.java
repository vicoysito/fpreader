/*
 * FPManager.java
 *
 * Created on 25 de Setembro de 2006, 19:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.controller;

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
    
    public static byte[][] getSkeleton(String url) throws IOException{
        BufferedImage bfImage = ImageIO.read(new File(url));
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        byte[][] skeleton = new byte[width][height];
        
        //Binarizando a Imagem
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                Color c = new Color(bfImage.getRGB(i,j));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red<127 && green<127 && blue<127){
                    skeleton[i][j] = 1;
                }else{
                    skeleton[i][j] = 0;
                }
            }
        }
        
        //Removendo as bordas
        for(int i=0; i<width; i++){
            skeleton[i][0] = 0;
            skeleton[i][height-1] = 0;
        }
        for(int j=0; j<height; j++){
            skeleton[0][j] = 0;
            skeleton[width-1][j] = 0;
        }
        
        return skeleton;
    }
    
    public static byte[][] mapMinutiaes(byte[][] skeleton){
        int width = skeleton.length;
        int height = skeleton[0].length;
        byte[][] outSkeleton = copySkeleton(skeleton);
        
        for(int i=5; i<width-5; i++){
            for(int j=5; j<height-5; j++){
                int patterns = timesPattern01(i,j,skeleton);
                if(skeleton[i][j]==1){
                    if(patterns==1){
                        outSkeleton = drawRectangle(i,j,outSkeleton,2);
                    }
                    if(patterns==3){
                        outSkeleton = drawRectangle(i,j,outSkeleton,3);
                    }
                }
            }
        }
        
        return outSkeleton;
    }
    
    private static byte[][] copySkeleton(byte[][] skeleton){
        int width = skeleton.length;
        int height = skeleton[0].length;
        byte[][] newSkel = new byte[width][height];
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                newSkel[i][j] = skeleton[i][j];
            }
        }
        return newSkel;
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
    
    public static byte[][] holt(byte[][] image){
        int width = image.length;
        int height = image[0].length;
        
        return image;
    }
    
    public static byte[][] zhangSuen(byte[][] image){
        int width = image.length;
        int height = image[0].length;
        int changes1 = 0;
        int changes2 = 1;
        
        while(changes1!=changes2){
            changes2 = changes1;
            changes1 = 0;
            for(int i=1; i<width-1; i++){
                for(int j=1; j<height-1; j++){
                    //Armazenando a janela 3x3 em sentido horário
                    byte p1 = image[i][j];
                    byte p2 = image[i][j-1];
                    byte p3 = image[i+1][j-1];
                    byte p4 = image[i+1][j];
                    byte p5 = image[i+1][j+1];
                    byte p6 = image[i][j+1];
                    byte p7 = image[i-1][j+1];
                    byte p8 = image[i-1][j];
                    byte p9 = image[i-1][j-1];
                    
                    int nonZero = nonZeroNeighbours(i,j,image);
                    //1. entre 2 e 6 pixels não zerados ao redor
                    if(2 <= nonZero && nonZero <= 6){
                        int pattern01 = timesPattern01(i,j,image);
                        //2. sequencias de [0,1] em sentido horário
                        if(pattern01 == 1){
                            //3. Iteração 1 : p2*p4*p6==0 e p4*p6*p8==0
                            if(p2*p4*p6==0 && p4*p6*p8==0){
                                //Apaga-se o ponto
                                image[i][j] = 0;
                                changes1++;
                            }//3
                            
                            //4. Iteração 2 : p2*p4*p6==0 e p2*p4*p8==0
                            if(p2*p4*p6==0 && p2*p4*p8==0){
                                //Apaga-se o ponto
                                image[i][j] = 0;
                                changes1++;
                            }//4
                        }//2
                    }//1
                }
            }
        }
        
        return image;
    }
    
    public static byte[][] hilditch(byte[][] image){
        int width = image.length;
        int height = image[0].length;
        
        for(int i=1; i<width-1; i++){
            for(int j=1; j<height-1; j++){
                //Armazenando a janela 3x3 em sentido horário
                byte p1 = image[i][j];
                byte p2 = image[i][j-1];
                byte p3 = image[i+1][j-1];
                byte p4 = image[i+1][j];
                byte p5 = image[i+1][j+1];
                byte p6 = image[i][j+1];
                byte p7 = image[i-1][j+1];
                byte p8 = image[i-1][j];
                byte p9 = image[i-1][j-1];
                
                int nonZero = nonZeroNeighbours(i,j,image);
                //1. entre 2 e 6 pixels não zerados ao redor
                if(2 <= nonZero && nonZero <= 6){
                    int pattern01 = timesPattern01(i,j,image);
                    //2. sequencias de [0,1] em sentido horário
                    if(pattern01 == 1){
                        //3. p2*p4*p8==0 ou a sequencia de [0,1] em sentido
                        //horário de p2 !=1
                        if(p2*p4*p8==0 || timesPattern01(i, j-1, image)!=1){
                            //4. p2*p4*p6==0 ou a sequencia de [0,1] em sentido
                            //horário de p4 !=1
                            if(p2*p4*p6==0 || timesPattern01(i+1, j, image)!=1){
                                image[i][j] = 0;
                            }//4
                        }//3
                    }//2
                }//1
            }
        }
        return image;
    }
    
    private static int nonZeroNeighbours(int i, int j, byte[][] image){
        int nonZero = 0;
        
        //Contando os vizinhos não-zerados
        if(image[i][j-1]==1) nonZero++;
        if(image[i+1][j-1]==1) nonZero++;
        if(image[i+1][j]==1) nonZero++;
        if(image[i+1][j+1]==1) nonZero++;
        if(image[i][j+1]==1) nonZero++;
        if(image[i-1][j+1]==1) nonZero++;
        if(image[i-1][j]==1) nonZero++;
        if(image[i-1][j-1]==1) nonZero++;
        
        return nonZero;
    }
    
    private static int timesPattern01(int i, int j, byte[][] image){
        int pattern01 = 0;
        
        //Contando as sequencias[0,1]
        if(image[i][j-1]==0 && image[i+1][j-1]==1) pattern01++;
        if(image[i+1][j-1]==0 && image[i+1][j]==1) pattern01++;
        if(image[i+1][j]==0 && image[i+1][j+1]==1) pattern01++;
        if(image[i+1][j+1]==0 && image[i][j+1]==1) pattern01++;
        if(image[i][j+1]==0 && image[i-1][j+1]==1) pattern01++;
        if(image[i-1][j+1]==0 && image[i-1][j]==1) pattern01++;
        if(image[i-1][j]==0 && image[i-1][j-1]==1) pattern01++;
        if(image[i-1][j-1]==0 && image[i][j-1]==1) pattern01++;
        
        return pattern01;
    }
    
    public static Image convertSkeleton(byte[][] skeleton){
        int width = skeleton.length;
        int height = skeleton[0].length;
        BufferedImage bfImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                if(skeleton[i][j]==0){
                    bfImage.setRGB(i,j,Color.white.getRGB());
                }
                if(skeleton[i][j]==1){
                    bfImage.setRGB(i,j,Color.black.getRGB());
                }
                if(skeleton[i][j]==2){
                    bfImage.setRGB(i,j,Color.green.getRGB());
                }
                if(skeleton[i][j]==3){
                    bfImage.setRGB(i,j,Color.red.getRGB());
                }
            }
        }
        return bfImage.getScaledInstance(width, height,0);
    }
    
}
