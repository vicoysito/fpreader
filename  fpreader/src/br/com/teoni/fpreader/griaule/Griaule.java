/*
 * Griaule.java
 *
 * Created on 17 de Outubro de 2006, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.griaule;

import com.griaule.grFinger.FingerCallBack;
import com.griaule.grFinger.FingerprintImage;
import com.griaule.grFinger.GrErrorException;
import com.griaule.grFinger.GrFinger;
import com.griaule.grFinger.ImageCallBack;
import com.griaule.grFinger.StatusCallBack;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;

/**
 *
 * @author Administrador
 */
public class Griaule implements StatusCallBack, FingerCallBack{
    
    private GrFinger finger;
    private ImageCallBack imageCallBack;
    private boolean power;
    
    /** Creates a new instance of Griaule */
    public Griaule(ImageCallBack imgCbk) {
        try {
            finger = new GrFinger();
            imageCallBack = imgCbk;
        } catch (GrErrorException ex) {
            ex.printStackTrace();
        }
    }
    
    public void onPlug(String string) {
        try {
            finger.startCapture(string,this,imageCallBack);
        } catch (GrErrorException ex) {
            ex.printStackTrace();
        }
    }
    
    public void onUnplug(String string) {
        try {
            finger.stopCapture(string);
        } catch (GrErrorException ex) {
            ex.printStackTrace();
        }
    }
    
    public void onFingerDown(String string) {
    }
    
    public void onFingerUp(String string) {
    }
    
    public void powerOn(){
        try {
            finger.initializeCapture(this);
            this.power = true;
        } catch (GrErrorException ex) {
            ex.printStackTrace();
        }
    }
    
    public void powerOff(){
        try {
            finger.finalizeCapture();
            this.power = false;
        } catch (GrErrorException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isOn(){
        return this.power;
    }
}
