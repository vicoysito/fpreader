/*
 * BasicOperations.java
 *
 * Created on 7 de Outubro de 2006, 09:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.imageprocessing;

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
    
    public static int timesPattern01(int i, int j, byte[][] image){
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
}
