/*
 * Copyright 2018 Manjot Sidhu <manjot.techie@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.manjotsidhu.mta.analyser;

import com.github.manjotsidhu.mta.Tools;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * compares and brings out anomalies from log files mutually.
 * 
 * @author Manjot Sidhu
 */
public class BatchAnomalies {
    
    private final ArrayList batchAnomalies = new ArrayList();
    
    BatchAnomalies (ArrayList files, ArrayList analysedTime, ArrayList analysedNMethods, ArrayList logSequence, ArrayList analysedJST) { 
        for (int method = 0; method < ((ArrayList) analysedTime.get(0)).size(); method++) {
            ArrayList timeArr = new ArrayList();
            ArrayList nMethodsArr = new ArrayList();
            ArrayList JSTArr = new ArrayList();
            String currentMethod = (String) ((ArrayList) analysedTime.get(0)).get(method);
            
            for (int file = 1; file < files.size(); file++) {
                timeArr.add(((ArrayList) analysedTime.get(file)).get(method));
                nMethodsArr.add(((ArrayList) analysedNMethods.get(file)).get(method));
                JSTArr.add(((ArrayList) analysedJST.get(file)).get(method));
            }
            findJSTAnomaly(files, JSTArr, currentMethod);
            findMethodsAnomaly(files, timeArr, nMethodsArr, currentMethod);
        }
        findCodeFlowAnomaly(files, logSequence);
    }
    
    private void findMethodsAnomaly(ArrayList files, ArrayList timeArray, ArrayList nMethodsArray, String method) {
        Object[] arr1 = timeArray.toArray();
        Object[] arr2 = nMethodsArray.toArray();
        
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        
        Long avg1 = 0l;
        Long avg2 = 0l;
        
        for (int i = 0; i < arr1.length; i++) {
            avg1 += (Long) arr1[i];
            avg2 += (Integer) arr2[i];
        }
        
        avg1 -= (Long) arr1[arr1.length-1];
        avg1 /= arr1.length;
        
        avg2 -= (Integer) arr2[arr2.length-1];
        avg2 /= arr2.length;
        
        if ((Long) arr1[arr1.length-1] - (Long) arr1[arr1.length-2] > avg1) {
            String file = (String) files.get(Tools.find(timeArray, 0, arr1[arr1.length-1])+1);
            addLongerExecutionTimeAnomaly(file, method);
            
            if ((Integer) arr2[arr2.length-1] - (Integer) arr2[arr2.length-2] > avg2) {
                addHangAnomaly(file);
            }
        }
    }
    
    private void findCodeFlowAnomaly(ArrayList files, ArrayList logSequence) {
        Object[] arr3 = new Object[logSequence.size()];
        
        int avg3 = 0;
        for (int i = 0; i < logSequence.size(); i++) {
            arr3[i] = ((ArrayList) logSequence.get(i)).size();
            avg3 += (Integer) arr3[i];
        }
        
        Object[] arr3Backup = new Object[arr3.length];
        System.arraycopy(arr3, 0, arr3Backup, 0, arr3.length);
        Arrays.sort(arr3);
        avg3 /= arr3.length;

        if ((Integer) arr3[arr3.length-1] > avg3) {
            String file = (String) files.get(Tools.find(arr3Backup, 0, arr3[arr3.length-1])+1);
            
            addCodeFlowAnomaly(file);
        }
    }
    
    private void findJSTAnomaly(ArrayList files, ArrayList JSTArr, String currentMethod) {
        ArrayList tempArr = new ArrayList();
        ArrayList filesArr = new ArrayList();
        for (int i = 0 ; i< JSTArr.size();i++){
            tempArr.add(JSTArr.get(i));
            filesArr.add(JSTArr.get(i));
        }
        
        JSTArr = Tools.removeDuplicates(tempArr);
        
        for (int j = 0; j < tempArr.size(); j++) {
            Object s = tempArr.get(j);
            tempArr.remove(s);
            if (tempArr.contains(s)) {
                JSTArr.remove(s);    
            }
        }
        if (JSTArr.size() == 1) {
            addJSTAnomaly((String) files.get(filesArr.indexOf(JSTArr.get(0))+1), currentMethod);
        }
    }
    
    private void addLongerExecutionTimeAnomaly(String file, String method) {
        batchAnomalies.add("Method '" + method + "' of File '" + file + "' is taking longer to execute.");
    }
    
    private void addHangAnomaly(String file) {
        batchAnomalies.add("Log file '" + file + "' is experiencing hang/performance issues.");
    }
    
    void addMethodMissingAnomaly(String file, String method) {
        batchAnomalies.add("Method '" + method + "' didn't completed its execution.");
    }
    
    private void addCodeFlowAnomaly(String file) {
        batchAnomalies.add("File '" + file + "' has odd code-flow from others.");
    }
    
    private void addJSTAnomaly(String file, String method) {
        batchAnomalies.add("Method '" + method + "' of file '" + file + "' has different jStackTrace.");
    }
    
    /**
     * returns the results calculated by batch anomalies.
     * 
     * @return ArrayList batchAnomalies
     */
    public ArrayList getBatchAnomalies() {
        return batchAnomalies;
    }
}
