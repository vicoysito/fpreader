/*
 * Fingerprint.java
 *
 * Created on 25 de Setembro de 2006, 19:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.teoni.fpreader.model;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Teoni
 */
public class Fingerprint {
    
    private String url;
    private int height;
    private int width;
    private BufferedImage bufferedImage;
    private byte[][] skeleton;
    private byte[][] binaryImage;
    private int bifurcations;
    private int endoflines;
    
    /**
     * Creates a new instance of Fingerprint
     */
    public Fingerprint(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[][] getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(byte[][] skeleton) {
        this.skeleton = skeleton;
    }

    public byte[][] getBinaryImage() {
        return binaryImage;
    }

    public void setBinaryImage(byte[][] binaryImage) {
        this.binaryImage = binaryImage;
    }

    public int getBifurcations() {
        return bifurcations;
    }

    public void setBifurcations(int bifurcations) {
        this.bifurcations = bifurcations;
    }

    public int getEndoflines() {
        return endoflines;
    }

    public void setEndoflines(int endoflines) {
        this.endoflines = endoflines;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
    
    public Image getImage(){
        return this.bufferedImage.getScaledInstance(this.width, this.height,Image.SCALE_SMOOTH);
    }
    
}
