package com.github.manjotsidhu.mta.analyser;

import com.github.manjotsidhu.mta.Tools;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Anomalies {
    
    private final ArrayList pTime = new ArrayList();
    private final ArrayList pTimeMethods = new ArrayList();
    private final ArrayList pNMethods = new ArrayList();
    private final ArrayList pJST = new ArrayList();
    
    private ArrayList fTime = new ArrayList();
    private ArrayList fTimeMethods = new ArrayList();
    private ArrayList fMethodMissing = new ArrayList();
    private ArrayList fNMethods = new ArrayList();
    private ArrayList fJST = new ArrayList();
    
    // Format [[Failing Exceptions for file 1], ...]
    private final ArrayList anomaliesExceptions = new ArrayList();
    // Format [[[Method Name], [Passing Method Time], [Failing MethodTime]]]
    private final ArrayList anomaliesTime = new ArrayList();
    // Format [[[Method Name], [Passing NMethods], [Failing NMethods]]]
    private final ArrayList anomaliesNMethods = new ArrayList();
    // Format [[[Passing JST], [FailingJST]]]
    private final ArrayList anomaliesJST = new ArrayList();
    
    private final ArrayList anomalies = new ArrayList();
    
    public Anomalies(File passingLogFile, File failingLogFile) throws IOException {
        // PASSING LOG PART
        
        ArrayList parsedPLog = Parser.parse(passingLogFile);
      
        ArrayList<String> parsedPTime = (ArrayList<String>) parsedPLog.get(0);
        ArrayList parsedPText = (ArrayList) parsedPLog.get(1);
        ArrayList<Integer> parsedPSequence = (ArrayList) parsedPLog.get(2);
        Integer nPMethods = parsedPSequence.size() / 2;
        ArrayList<String> parsedPJST = (ArrayList<String>) parsedPLog.get(4);
        ArrayList<String> parsedPException = (ArrayList<String>) parsedPLog.get(5);
        
        analyseTime(nPMethods, parsedPTime, parsedPText, parsedPSequence, true);
        analyseNMethods(parsedPText, parsedPSequence, nPMethods, true);
        analyseJST(parsedPJST, parsedPSequence, parsedPText, nPMethods, true);
        
        // FAILING LOG PART
        
        anomalies.add(new ArrayList());
        ((ArrayList) anomalies.get(anomalies.size()-1)).add(failingLogFile.getName());
        
        ArrayList parsedFLog = Parser.parse(failingLogFile);
      
        ArrayList<String> parsedFTime = (ArrayList<String>) parsedFLog.get(0);
        ArrayList parsedFText = (ArrayList) parsedFLog.get(1);
        ArrayList<Integer> parsedFSequence = (ArrayList) parsedFLog.get(2);
        Integer nFMethods = parsedFSequence.size() / 2;
        ArrayList<String> parsedFJST = (ArrayList<String>) parsedFLog.get(4);
        ArrayList<String> parsedFException = (ArrayList<String>) parsedFLog.get(5);
        
        analyseTime(nFMethods, parsedFTime, parsedFText, parsedFSequence, false);
        analyseNMethods(parsedFText, parsedFSequence, nFMethods, false);
        analyseJST(parsedFJST, parsedFSequence, parsedFText, nFMethods, false);
        
        // COMMON
        
        anomaliesExceptions(parsedPException, parsedFException);
        anomaliesMethodTime();
        anomaliesNMethods();
        anomaliesJST();
    }
    
    public Anomalies(File passingLogFile, File[] failingLogFiles) throws IOException {
        // PASSING LOG PART
        
        ArrayList parsedPLog = Parser.parse(passingLogFile);
      
        ArrayList<String> parsedPTime = (ArrayList<String>) parsedPLog.get(0);
        ArrayList parsedPText = (ArrayList) parsedPLog.get(1);
        ArrayList<Integer> parsedPSequence = (ArrayList) parsedPLog.get(2);
        Integer nPMethods = parsedPSequence.size() / 2;
        ArrayList<String> parsedPJST = (ArrayList<String>) parsedPLog.get(4);
        ArrayList<String> parsedPException = (ArrayList<String>) parsedPLog.get(5);
        
        analyseTime(nPMethods, parsedPTime, parsedPText, parsedPSequence, true);
        //System.out.println("pTime" + pTime.toString());
        
        analyseNMethods(parsedPText, parsedPSequence, nPMethods, true);
        //System.out.println("pNMethods" + pNMethods.toString());
        
        analyseJST(parsedPJST, parsedPSequence, parsedPText, nPMethods, true);
        //System.out.println("pPassingJST " + pJST.toString());
        
        // FAILING LOG PART
        
        for (File f: failingLogFiles) {
            fTime = new ArrayList();
            fTimeMethods = new ArrayList();
            fMethodMissing = new ArrayList();
            fNMethods = new ArrayList();
            fJST = new ArrayList();
            
            anomalies.add(new ArrayList<String>());
            ((ArrayList) anomalies.get(anomalies.size()-1)).add(f.getName());
            
            ArrayList parsedFLog = Parser.parse(f);

            ArrayList<String> parsedFTime = (ArrayList<String>) parsedFLog.get(0);
            ArrayList parsedFText = (ArrayList) parsedFLog.get(1);
            ArrayList<Integer> parsedFSequence = (ArrayList) parsedFLog.get(2);
            Integer nFMethods = parsedFSequence.size() / 2;
            ArrayList<String> parsedFJST = (ArrayList<String>) parsedFLog.get(4);
            ArrayList<String> parsedFException = (ArrayList<String>) parsedFLog.get(5);

            analyseTime(nFMethods, parsedFTime, parsedFText, parsedFSequence, false);
            //System.out.println("fTime " + f.getName() + " " + fTime.toString());

            analyseNMethods(parsedFText, parsedFSequence, nFMethods, false);
            //System.out.println("fNMethods" + f.getName() + " " + fNMethods.toString());

            analyseJST(parsedFJST, parsedFSequence, parsedFText, nFMethods, false);
            //System.out.println("fFailingJST "  + f.getName() + " " + pJST.toString());

            // COMMON
            
            anomaliesExceptions(parsedPException, parsedFException);
            anomaliesMethodTime();
            anomaliesNMethods();
            anomaliesJST();
        }
    }
    
    /**
     * Analysis for time taken by every method of every log.
     *
     * @param nMethods number of methods exist in the log
     * @param parsedTime time of each methods at entry and exit parsed by the
     * parser
     * @param parsedText message of each event and methods in the log
     * @param parsedSequence sequence of methods donated by unique numbers
     * generated by parser
     * {@link com.github.ibmhackchallenge.methodtraceanalyser.parser}
     */
    private void analyseTime(Integer nMethods, ArrayList<String> parsedTime, ArrayList parsedText, ArrayList parsedSequence, boolean isPassingLog) {

        for (int iteration = 1; iteration <= nMethods; iteration++) {
            int firstIndex = Tools.find(parsedSequence, 0, iteration);
            int secondIndex = Tools.find(parsedSequence, firstIndex + 1, iteration);
            // if secondIndex is not found then surely method is not out.
            if (!isPassingLog && secondIndex == -1)
                anomaliesMethodMissing(parsedText.get(firstIndex));
            long timeTaken = 0;
            try {
                timeTaken = ChronoUnit.MILLIS.between(LocalTime.parse(parsedTime.get(firstIndex)), LocalTime.parse(parsedTime.get(secondIndex)));
            } catch (Exception e) {
                //System.out.println("failed");
            }
            if(isPassingLog) {
                if (pTimeMethods.contains((String) parsedText.get(firstIndex))) {
                    int methodOccur = Tools.find(pTimeMethods, 0, (String) parsedText.get(firstIndex));

                    Tools.extendArrayIndex(pTime, methodOccur);
                    if (methodOccur < pTime.size() && pTime.get(methodOccur) != null)
                        timeTaken = (Long) pTime.get(methodOccur);
                    pTime.set(methodOccur, timeTaken);
                } else {
                    pTimeMethods.add((String) parsedText.get(firstIndex));
                    pTime.add(timeTaken);
                }
                
            } else {
                if (fTimeMethods.contains((String) parsedText.get(firstIndex))) {
                    int methodOccur = Tools.find(fTimeMethods, 0, (String) parsedText.get(firstIndex));

                    Tools.extendArrayIndex(fTime, methodOccur);
                    if (methodOccur < fTime.size() && fTime.get(methodOccur) != null)
                        timeTaken = (Long) fTime.get(methodOccur);
                    fTime.set(methodOccur, timeTaken);
                } else {
                    fTimeMethods.add((String) parsedText.get(firstIndex));
                    fTime.add(timeTaken);
                }
            }
        }
    }
    
    /**
     * Analysis number of times methods are executed.
     *
     * @param parsedText message of each event and methods in the log parsed by
     * parser
     * @param parsedSequence sequence of methods donated by unique numbers
     * generated by parser
     * {@link com.github.ibmhackchallenge.methodtraceanalyser.parser}
     */
    private void analyseNMethods(ArrayList parsedText, ArrayList parsedSequence, int nMethods, boolean passingLog) {
        ArrayList<String> methods;
        
        if (passingLog)
            methods = pTimeMethods;
        else
            methods = fTimeMethods;

        for (int iteration = 1; iteration <= nMethods; iteration++) {
            int timeIndex = Tools.find(parsedSequence, 0, iteration);
            int index = Tools.find(methods, 0, (String) parsedText.get(timeIndex));
            int count = Tools.count(parsedText, (String) parsedText.get(timeIndex)) / 2;
            if (passingLog) {
                Tools.extendArrayIndex(pNMethods, index);
                pNMethods.set(index, count);
            } else {
                Tools.extendArrayIndex(fNMethods, index);
                fNMethods.set(index, count);
            }
        }
    }
    
    /**
     * Analyzes jStackTrace for one log file.
     * 
     */
    private void analyseJST(ArrayList<String> parsedJST, ArrayList parsedSequence, ArrayList parsedText, int nMethods, boolean passingLog) {
        for (int method = 1; method <= nMethods; method++) {
            int index = Tools.find(parsedSequence, 0, method);
            String stackTrace = (String) parsedJST.get(index);
            
            if (passingLog) {
                if(pTimeMethods.contains((String) parsedText.get(index))) {
                    int methodIndex = Tools.find(pTimeMethods, 0, (String) parsedText.get(index));
                    Tools.extendArrayIndex(pJST, methodIndex);
                    pJST.set(methodIndex, stackTrace);
                } else {
                    pTimeMethods.add((String) parsedText.get(index));
                    pJST.add(stackTrace);
                }
            } else {
                if(fTimeMethods.contains((String) parsedText.get(index))) {
                    int methodIndex = Tools.find(fTimeMethods, 0, (String) parsedText.get(index));
                    Tools.extendArrayIndex(fJST, methodIndex);
                    fJST.set(methodIndex, stackTrace);
                } else {
                    fTimeMethods.add((String) parsedText.get(index));
                    fJST.add(stackTrace);
                }
            }
        }
    }
    
    private void anomaliesExceptions(ArrayList<String> passingEx, ArrayList<String> failingEx) {
        anomaliesExceptions.add(new ArrayList());
        for (int i = 0; i < passingEx.size() + failingEx.size(); i++) {
            if (i < passingEx.size()) {
                if(!failingEx.contains(passingEx.get(i)) && !((ArrayList) anomaliesExceptions.get(anomaliesExceptions.size()-1)).contains(passingEx.get(i))) {
                    ((ArrayList) anomaliesExceptions.get(anomaliesExceptions.size()-1)).add(passingEx.get(i));
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Caught Exception: " + passingEx.get(i));
                }
            } else {
                if(!passingEx.contains(failingEx.get(i-passingEx.size())) && !((ArrayList) anomaliesExceptions.get(anomaliesExceptions.size()-1)).contains(failingEx.get(i-passingEx.size()))) {
                    ((ArrayList) anomaliesExceptions.get(anomaliesExceptions.size()-1)).add(failingEx.get(i-passingEx.size()));
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Caught Exception: " + failingEx.get(i-passingEx.size()));
                }
            }
        }
    }
    
    private void anomaliesMethodTime() {
        anomaliesTime.add(new ArrayList());
        ArrayList passingMethodTimes = new ArrayList();
        ArrayList failingMethodTimes = new ArrayList();
        ArrayList<String> MethodNames = new ArrayList();
        
        for (int i = 0; i < pTime.size() + fTime.size(); i++) {            
            if (i < pTime.size()) {
                if(!fTime.contains(pTime.get(i)) && !passingMethodTimes.contains(pTime.get(i)) && !MethodNames.contains((String) pTimeMethods.get(i))) {
                    passingMethodTimes.add(pTime.get(i));
                    MethodNames.add((String) pTimeMethods.get(i));
                    failingMethodTimes.add(fTime.get(Tools.find(fTimeMethods, 0, pTimeMethods.get(i))));
  
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Method " + pTimeMethods.get(i) + " took " + fTime.get(Tools.find(fTimeMethods, 0, pTimeMethods.get(i))) + "ms, was " + pTime.get(i) + "ms previously.");
                }
            } else {
                if(!pTime.contains(fTime.get(i-pTime.size())) && !failingMethodTimes.contains(pTime.get(i-pTime.size())) && !MethodNames.contains((String) fTimeMethods.get(i-pTime.size()))) {
                    failingMethodTimes.add(fTime.get(i-pTime.size()));
                    MethodNames.add((String) fTimeMethods.get(i-pTime.size()));
                    passingMethodTimes.add(pTime.get(Tools.find(pTimeMethods, 0, fTimeMethods.get(i-pTime.size()))));

                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Method " + fTimeMethods.get(i-pTime.size()) + " took " + fTime.get(i-pTime.size()) + "ms, was " + pTime.get(Tools.find(pTimeMethods, 0, fTimeMethods.get(i-pTime.size()))) + "ms previously.");
                }
            }
        }
                
        ((ArrayList) anomaliesTime.get(anomaliesTime.size()-1)).add(MethodNames);
        ((ArrayList) anomaliesTime.get(anomaliesTime.size()-1)).add(passingMethodTimes);
        ((ArrayList) anomaliesTime.get(anomaliesTime.size()-1)).add(failingMethodTimes);
    }
    
    private void anomaliesMethodMissing(Object methodName) {
        fMethodMissing.add(new ArrayList());
        ((ArrayList) fMethodMissing.get(fMethodMissing.size()-1)).add((String)methodName);
        ((ArrayList) anomalies.get(anomalies.size()-1)).add("Method " + methodName + " was not exited");
    }
    
    private void anomaliesNMethods() {
        anomaliesNMethods.add(new ArrayList());
        ArrayList passingNMethods = new ArrayList();
        ArrayList failingNMethods = new ArrayList();
        ArrayList<String> MethodNames = new ArrayList();
        
        for (int i = 0; i < pNMethods.size() + fNMethods.size(); i++) {
            if (i < pNMethods.size()) {
                if(!fNMethods.contains(pNMethods.get(i)) && !MethodNames.contains((String) pTimeMethods.get(i))) {
                    passingNMethods.add(pNMethods.get(i));
                    Object methodName = pTimeMethods.get(i);
                    failingNMethods.add(fNMethods.get(Tools.find(fTimeMethods, 0, methodName)));
                    MethodNames.add((String) methodName);
                    
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Method " + pTimeMethods.get(i) + " was executed " + fNMethods.get(Tools.find(fTimeMethods, 0, methodName)) + " times than " + pNMethods.get(i) + " times previously.");
                }
            } else {
                if(!pNMethods.contains(fNMethods.get(i-pNMethods.size())) && !MethodNames.contains((String) fTimeMethods.get(i-pNMethods.size()))) {
                    failingNMethods.add(fNMethods.get(i-pNMethods.size()));
                    Object methodName = fTimeMethods.get(i-pNMethods.size());
                    passingNMethods.add(pNMethods.get(Tools.find(pTimeMethods, 0, methodName)));
                    MethodNames.add((String) methodName);
                
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("Method " + fTimeMethods.get(i-pNMethods.size()) + " was executed " + fNMethods.get(i-pNMethods.size()) + " times than " + pNMethods.get(Tools.find(pTimeMethods, 0, methodName)) + " times previously.");
                }
            }
        }
        
        ((ArrayList) anomaliesNMethods.get(anomaliesNMethods.size()-1)).add(MethodNames);
        ((ArrayList) anomaliesNMethods.get(anomaliesNMethods.size()-1)).add(passingNMethods);
        ((ArrayList) anomaliesNMethods.get(anomaliesNMethods.size()-1)).add(failingNMethods);
    }
    
    private void anomaliesJST() {
        anomaliesJST.add(new ArrayList());
        ArrayList passingJST = new ArrayList();
        ArrayList failingJST = new ArrayList();;
        ArrayList<String> MethodNames = new ArrayList();;
        
        for (int i = 0; i < pJST.size() + fJST.size(); i++) {
            if (i < pJST.size()) {
                if(!fJST.contains(pJST.get(i)) && !MethodNames.contains((String) pTimeMethods.get(i))) {
                    passingJST.add(pJST.get(i));
                    Object methodName = pTimeMethods.get(i);
                    failingJST.add(fJST.get(Tools.find(fTimeMethods, 0, methodName)));
                    MethodNames.add((String) methodName);
                
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("jStackTrace found: " + fJST.get(Tools.find(fTimeMethods, 0, methodName)) + "\n was: " + pJST.get(i));
                }
            } else {
                if(!pJST.contains(fJST.get(i-pJST.size())) && !MethodNames.contains((String) fTimeMethods.get(i-pJST.size()))) {
                    failingJST.add(fJST.get(i-pJST.size()));
                    Object methodName = fTimeMethods.get(i-pJST.size());
                    passingJST.add(pJST.get(Tools.find(pTimeMethods, 0, methodName)));
                    MethodNames.add((String) methodName);
                    
                    ((ArrayList) anomalies.get(anomalies.size()-1)).add("jStackTrace found: " + fJST.get(i-pJST.size()) + "\n was: " + pJST.get(Tools.find(pTimeMethods, 0, methodName)));
                }
            }
        }
        
        ((ArrayList) anomaliesJST.get(anomaliesJST.size()-1)).add(MethodNames);
        ((ArrayList) anomaliesJST.get(anomaliesJST.size()-1)).add(passingJST);
        ((ArrayList) anomaliesJST.get(anomaliesJST.size()-1)).add(failingJST);
    }
    
    /**
     * returns exception anomalies.
     * 
     * @return ArrayList of String
     */
    public ArrayList getAnomaliesExceptions() {
        return anomaliesExceptions;
    }
    
    /**
     * returns method time anomalies.
     * 
     * @return ArrayList of String
     */
    public ArrayList getAnomaliesTime() {
        return anomaliesTime;
    }
    
    /**
     * returns number of times method executed anomalies.
     * 
     * @return ArrayList of String
     */
    public ArrayList getAnomaliesNMethods() {
        return anomaliesNMethods;
    }
    
    /**
     * returns jStacktrace of anomalies.
     * 
     * @return ArrayList of String
     */
    public ArrayList getAnomaliesJST() {
        return anomaliesJST;
    }
    
    /**
     * returns all the anomalies which are found.
     * 
     * @return ArrayList of String
     */
    public ArrayList anomalies() {
        return anomalies;
    }
}
