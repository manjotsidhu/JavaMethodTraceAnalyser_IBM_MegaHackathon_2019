package com.github.manjotsidhu.mta.main;

import com.github.manjotsidhu.mta.Tools;
import static com.github.manjotsidhu.mta.Tools.colorPicker;
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
       
        System.out.println(colorPicker(1));
    }
}
