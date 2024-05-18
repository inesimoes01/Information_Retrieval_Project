package unipi.it.mircv.compression;

import java.util.ArrayList;

public class ToReturn {
    private ArrayList<String> highBits;
    private ArrayList<String> lowerBits;
    private int l;
    private int h;

    public ToReturn() {
    }
    public ToReturn(int h, int l) {
        this.l=l;
        this.h=l;
    }



    public void setHighBits(ArrayList<String> highBits) {
        this.highBits = highBits;
    }

    public void setLowerBits(ArrayList<String> lowerBits) {
        this.lowerBits = lowerBits;
    }

    public ArrayList<String> getHighBits() {
        return highBits;
    }

    public ArrayList<String> getLowerBits() {
        return lowerBits;
    }
}
