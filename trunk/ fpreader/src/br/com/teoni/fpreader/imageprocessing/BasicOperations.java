/*
 * BasicOperations.java
 *
 * Created on 7 de Outubro de 2006, 09:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.imageprocessing;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Teoni
 */
public class BasicOperations {
    
    public static byte[][] copy(byte[][] image){
        int width = image.length;
        int height = image[0].length;
        byte[][] newImage = new byte[width][height];
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                newImage[i][j] = image[i][j];
            }
        }
        return newImage;
    }
    
    public static int nonZeroNeighbours(int i, int j, byte[][] image){
        int nonZero = 0;
        
        //Contando os vizinhos não-zerados
        if(image[i-1][j]==1) nonZero++;
        if(image[i-1][j+1]==1) nonZero++;
        if(image[i][j+1]==1) nonZero++;
        if(image[i+1][j+1]==1) nonZero++;
        if(image[i+1][j]==1) nonZero++;
        if(image[i+1][j-1]==1) nonZero++;
        if(image[i][j-1]==1) nonZero++;
        if(image[i-1][j-1]==1) nonZero++;
        
        return nonZero;
    }
    
    public static int timesPattern01(int i, int j, byte[][] image){
        int pattern01 = 0;
        
        //Contando as sequencias[0,1]
        if(image[i-1][j]==0 && image[i-1][j+1]==1) pattern01++;
        if(image[i-1][j+1]==0 && image[i][j+1]==1) pattern01++;
        if(image[i][j+1]==0 && image[i+1][j+1]==1) pattern01++;
        if(image[i+1][j+1]==0 && image[i+1][j]==1) pattern01++;
        if(image[i+1][j]==0 && image[i+1][j-1]==1) pattern01++;
        if(image[i+1][j-1]==0 && image[i][j-1]==1) pattern01++;
        if(image[i][j-1]==0 && image[i-1][j-1]==1) pattern01++;
        if(image[i-1][j-1]==0 && image[i-1][j]==1) pattern01++;
        
        return pattern01;
    }
    
    public static byte[][] binarizeImage(BufferedImage bfImage){
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        byte[][] image = new byte[width][height];
        
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                Color c = new Color(bfImage.getRGB(i,j));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red<200 && green<200 && blue<200){
                    image[i][j] = 1;
                }else{
                    image[i][j] = 0;
                }
            }
        }
        return image;
    }
}
