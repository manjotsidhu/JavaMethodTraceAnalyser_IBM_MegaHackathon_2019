package com.github.manjotsidhu.mta.main;

import com.github.manjotsidhu.mta.Tools;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    public static void main(String[] args) {
        /*try {
            File[] files = new File[3];
            files[0] = new File("sample_logs/test2.fmt");
            files[1] = new File("sample_logs/test3.fmt");
            files[2] = new File("sample_logs/test4.fmt");
            
            Anomalies s = new Anomalies(new File("sample_logs/test1.fmt"), files);
            
            System.out.println(s.anomalies().toString());
            Object[][] c = Tools.toArray(s.anomalies(), s.anomalies().size());
            System.out.println(Arrays.deepToString(c));
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        ArrayList arr = new ArrayList();
        arr.add(1);
        arr.add(1);
        arr.add(2);
        arr.add(2);
        arr.add(3);
        arr.add(3);
        arr.add(4);
        arr.add(4);
        arr.add(5);
        arr.add(6);
        arr.add(7);
        arr.add(7);
        arr.add(8);
        arr.add(9);
        arr.add(9);
        arr.add(8);
        arr.add(6);
        arr.add(5);
        /*arr.add(5);
        arr.add(6);
        arr.add(6);
        arr.add(7);
        arr.add(7);
        arr.add(5);*/
        System.out.println(arr.toString());
        System.out.println(Arrays.deepToString(criticalLevel(arr)));
    }
    
    public static Integer[] criticalLevel(ArrayList arr) {
        Integer[] levels = new Integer[arr.size()];
        levels[0] = 0;
        int level = 0;
        
        ArrayList entryList = new ArrayList();
        ArrayList buf = new ArrayList();
        boolean c = false;
        
        for (int i = 0; i < arr.size(); i++) {
            if(!entryList.contains(arr.get(i))) {
                // entry
                entryList.add(arr.get(i));
                buf.add(arr.get(i));
                if (entryList.size() > 1 && buf.contains(entryList.get(entryList.size()-2))) {
                    level++;
                    buf.remove(entryList.get(entryList.size()-2));
                }
                levels[i] = level;
            } else {
                // exit
                entryList.remove(arr.get(i));
                if(buf.contains(arr.get(i))) buf.remove(arr.get(i));
                int index = Tools.find(arr, 0, arr.get(i));
                levels[i] = levels[index];
            }
        }
        
        return levels;
    }
}
