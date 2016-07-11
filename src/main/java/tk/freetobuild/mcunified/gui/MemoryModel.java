package tk.freetobuild.mcunified.gui;

import javax.swing.*;

/**
 * Created by liz on 7/8/16.
 */
public class MemoryModel extends AbstractSpinnerModel {
    private String value = "128 MB";
    public MemoryModel() {}
    public MemoryModel(int def) { value = format(def);}
    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        String out = value.toString();
        if(!(out.endsWith(" GB")||out.endsWith(" MB")))
            out+=" MB";
        this.value = format(parseToMB(out));
        fireStateChanged();
    }

    @Override
    public Object getNextValue() {
        int value = parseToMB(getValue().toString());
        if(value<1024)
            value+=128;
        else
            value+=512;
        return format(value);
    }

    @Override
    public Object getPreviousValue() {
        int value = parseToMB(getValue().toString());
        if(value>128&&value<=1024)
            value-=128;
        else
            value-=512;
        return format(value);
    }
    public static int parseToMB(String value) {
        String[] data = value.split(" ");
        double v = Double.parseDouble(data[0]);
        if(data[1].equalsIgnoreCase("mb")) {
            return (int)v;
        } else if(data[1].equalsIgnoreCase("gb")) {
            return (int)(v*1024);
        } else {
            return -1;
        }
    }
    private String format(int mb) {
        if(mb<1024)
            return mb+" MB";
        else
            return ((double)mb/1024.0)+" GB";
    }
}
