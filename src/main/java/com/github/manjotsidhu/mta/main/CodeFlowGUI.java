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
package com.github.manjotsidhu.mta.main;

import com.github.manjotsidhu.mta.tools.Tools;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JFrame;

/**
 * Flow Diagram for analysed method data values,
 * 
 * @author Manjot Sidhu
 */
public class CodeFlowGUI extends JFrame {

    public CodeFlowGUI(String[][] parsedText, ArrayList<String> logFiles, ArrayList logSequence, String logFile) throws IOException {
        super(logFile);
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        
        int[] yOffset = null;
        try {
            Map<String, Object> Estyle = graph.getStylesheet().getDefaultEdgeStyle();
            Estyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.SegmentConnector);
            
            Map<String, Object> Vstyle = graph.getStylesheet().getDefaultVertexStyle();
            Vstyle.put(mxConstants.STYLE_FONTSIZE, 15);
            
            mxStylesheet stylesheet = new mxStylesheet();
            stylesheet.setDefaultVertexStyle(Vstyle);
            stylesheet.setDefaultEdgeStyle(Estyle);
            graph.setStylesheet(stylesheet);
            
            int[] xOffset = new int[logFiles.size()];
            xOffset[0] = 15;
            for (int i = 1; i < xOffset.length; i++) {
                xOffset[i] = xOffset[i-1]+450;
            }
            yOffset = new int[logFiles.size()];
            for (int i = 0; i < yOffset.length; i++) {
                yOffset[i] = 15;
            }
            int xMultiplier = 100;
            int level = 0;
            
            ArrayList<Integer[]> levels = new ArrayList<>();
            for (int i = 0; i < logSequence.size(); i++) {
                levels.add(getLevels((ArrayList) logSequence.get(i)));
            }

            int t = 0;
            for (int itr = 1; itr < parsedText.length; itr++) {
    
                for (int j = 0; j < parsedText[0].length; j++) {
                    if (parsedText[itr-1][j] == null) continue;
                    HashSet hs = new HashSet();
                    for (int aj = 0; aj < parsedText[0].length-1; aj++) {
                        hs.add(parsedText[itr-1][aj]);
                    }
                            
                    if (t-1 > 0)
                        level = levels.get(j)[t-1];
                    else
                        level = 0;
                    
                    Object v1;
                    if (hs.size() > 1 && itr != 1)
                        v1 = graph.insertVertex(parent, null, parsedText[itr-1][j], xOffset[j] + level * xMultiplier, yOffset[j], parsedText[itr-1][j].length() * 8.5, 30 , "fillColor=#eb6e34");
                    else
                        v1 = graph.insertVertex(parent, null, parsedText[itr-1][j], xOffset[j] + level * xMultiplier, yOffset[j], parsedText[itr-1][j].length() * 8.5, 30);
                    
                    hs = new HashSet();
                    
                    for (int aj = 0; aj < parsedText[0].length-1; aj++) {
                        hs.add(parsedText[itr][aj]);
                    }
                    
                    if (t < levels.get(j).length)
                        level = levels.get(j)[t];
                    else
                        level = 0;
                                        
                    yOffset[j] = yOffset[j] + 50;
                    if (parsedText[itr][j] == null) continue;
                                        
                    Object v2;
                    if (hs.size() > 1 && itr != 1)
                        v2 = graph.insertVertex(parent, null, parsedText[itr][j], xOffset[j] + level * xMultiplier, yOffset[j], parsedText[itr][j].length() *8.5, 30, "fillColor=#eb6e34");
                    else
                        v2 = graph.insertVertex(parent, null, parsedText[itr][j], xOffset[j] + level * xMultiplier, yOffset[j], parsedText[itr][j].length() * 8.5, 30);
                    
                    graph.insertEdge(parent, null, "", v1, v2, mxConstants.EDGESTYLE_TOPTOBOTTOM);
                }
                t++;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            graph.getModel().endUpdate();
        }
        Arrays.sort(yOffset);
        int s = yOffset[yOffset.length-1] + 30;

        mxGraphComponent graphComponent = new mxGraphComponent(graph){
 
            @Override
            protected void paintBackground(Graphics arg0) {
                super.paintBackground(arg0);
                //int x = 275;
                //for (int i = 0; i < logFiles.size()-1; i++) {
                //    arg0.drawLine(x, 0, x, s);
                //    x += 350;
                //}
            }
             
        };
        graphComponent.setEnabled(false);
        getContentPane().add(graphComponent);
    }
    
    public static Integer[] getLevels(ArrayList arr) {
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