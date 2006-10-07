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

/**
 *
 * @author Teoni
 */
public class Thinning {
    
    public static Fingerprint holt(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        
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
        
        fingerprint.setSkeleton(image);
        return fingerprint;
    }
    
    public static Fingerprint hilditch(Fingerprint fingerprint){
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte image[][] = BasicOperations.copy(fingerprint.getBinaryImage());
        
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
                        //3. p2*p4*p8==0 ou a sequencia de [0,1] em sentido
                        //horário de p2 !=1
                        if(p2*p4*p8==0 || BasicOperations.timesPattern01(i, j-1, image)!=1){
                            //4. p2*p4*p6==0 ou a sequencia de [0,1] em sentido
                            //horário de p4 !=1
                            if(p2*p4*p6==0 || BasicOperations.timesPattern01(i+1, j, image)!=1){
                                image[i][j] = 0;
                            }//4
                        }//3
                    }//2
                }//1
            }
        }
        
        fingerprint.setSkeleton(image);
        return fingerprint;
    }
    
}
