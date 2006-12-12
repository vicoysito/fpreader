/*
 * Thinning.java
 *
 * Created on 7 de Outubro de 2006, 09:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.imageprocessing;

import br.com.teoni.fpreader.model.Fingerprint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Teoni
 */
public class Thinning {
    
    public static Fingerprint holt(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte image[][] = BasicOperations.copy(fingerprint.getBinaryImage());
        List<Point> points_to_remove = new ArrayList<Point>();
        boolean no_pixels_deleted = false;
        
        while(!no_pixels_deleted){
            
            no_pixels_deleted = true;
            
            for(int c=0; c<1; c++){
                for(int y=1; y<height-1; y++){
                    for(int x=1; x<width-1; x++){
                        
                        boolean p1 = image[x][y]==1?true:false;
                        boolean p2 = image[x][y-1]==1?true:false;
                        boolean p3 = image[x+1][y-1]==1?true:false;
                        boolean p4 = image[x+1][y]==1?true:false;
                        boolean p5 = image[x+1][y+1]==1?true:false;
                        boolean p6 = image[x][y+1]==1?true:false;
                        boolean p7 = image[x-1][y+1]==1?true:false;
                        boolean p8 = image[x-1][y]==1?true:false;
                        boolean p9 = image[x-1][y-1]==1?true:false;
                        
                        if(image[x][y]==1){
                            if(!(!BasicOperations.isEdge(x,y,image)||(p4&&p6&&(p2||p8)))){
                                no_pixels_deleted = false;
                                Point p = new Point(x,y);
                                points_to_remove.add(p);
                            }
                        }
                    }
                }
                
                for(int i=0; i<points_to_remove.size(); i++){
                    Point p = points_to_remove.remove(i);
                    image[p.x][p.y] = 0;
                }
            }
        }
        
        //Staircase removal
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                
                boolean p1 = image[x][y]==1?true:false;
                boolean p2 = image[x][y-1]==1?true:false;
                boolean p3 = image[x+1][y-1]==1?true:false;
                boolean p4 = image[x+1][y]==1?true:false;
                boolean p5 = image[x+1][y+1]==1?true:false;
                boolean p6 = image[x][y+1]==1?true:false;
                boolean p7 = image[x-1][y+1]==1?true:false;
                boolean p8 = image[x-1][y]==1?true:false;
                boolean p9 = image[x-1][y-1]==1?true:false;
                
                if(image[x][y]==1){
                    if(p2&&(p4&&!p3&&!p7&&(!p8||!p6)||(p8&&!p9&&!p5&&(!p4||!p6)))){
                        image[x][y]=0;
                        continue;
                    }
                    if(p2&&(p4&&!p3&&!p7&&(!p8||!p6)||(p8&&!p9&&!p5&&(!p4||!p6)))){
                        image[x][y]=0;
                        continue;
                    }
                }
            }
        }
        
        fingerprint.setSkeleton(image);
        return fingerprint;
    }
    
    public static Fingerprint zhangSuen(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte image[][] = BasicOperations.copy(fingerprint.getBinaryImage());
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
                    
                    int nonZero = BasicOperations.nonZeroNeighbours(i,j,image);
                    //1. entre 2 e 6 pixels não zerados ao redor
                    if(2 <= nonZero && nonZero <= 6){
                        int pattern01 = BasicOperations.timesPattern01(i,j,image);
                        //2. sequencias de [0,1] em sentido horário
                        if(pattern01 == 1){
                            //3. Iteração : p2*p4*p6==0 e (p4*p6*p8==0 ou  p2*p4*p8==0)
                            if(p2*p4*p6==0 && p4*p6*p8==0){
                                //Apaga-se o ponto
                                image[i][j] = 0;
                                changes1++;
                            }
                            
                            if(p2*p4*p8==0 && p2*p6*p8==0){
                                //Apaga-se o ponto
                                image[i][j] = 0;
                                changes1++;
                            }//3
                        }//2
                    }//1
                }
            }
        }
        
        fingerprint.setSkeleton(image);
        return fingerprint;
    }
    
    public static Fingerprint hilditch(Fingerprint fingerprint){
        byte image[][] = BasicOperations.copy(fingerprint.getBinaryImage());
        boolean changed = true;
        boolean turn = true;
        
        while (changed) {
            changed = false;
            for(int i = 3; i <= fingerprint.getWidth() - 3;i++) {
                for(int j = 3; j <= fingerprint.getHeight() - 3;j++) {
                    
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
                    
                    if (p1==1) {
                        int nonZero = BasicOperations.nonZeroNeighbours(i,j,image);
                        int pattern01 = BasicOperations.timesPattern01(i,j,image);
                        if (2 <= nonZero && nonZero <= 6) {
                            if (pattern01 == 1 ) {
                                if (p2*p4*p8==0 || BasicOperations.timesPattern01(i, j-1, image)!=1) {
                                    if (p2*p4*p6==0 || BasicOperations.timesPattern01(i+1, j, image)!=1) {
                                        image[i][j] = 0;
                                        changed = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        fingerprint.setSkeleton(image);
        return fingerprint;
    }
    
}
