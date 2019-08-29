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

import com.github.manjotsidhu.mta.tools.Test;
import com.github.manjotsidhu.mta.tools.Tools;
import com.github.manjotsidhu.mta.analyser.Anomalies;
import com.github.manjotsidhu.mta.analyser.Analyser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JFrame;
import org.apache.commons.io.FileUtils;

/**
 * Main controller file for the backend of FXML.
 * @author Manjot Sidhu
 */
public class MainController {
    private String[][] anomaliesArray;
    
    private String[][] methodTimeArray;
    private String[][] methodTimeArrayTable;
    
    private String[][] nMethodsArray;
    private String[][] nMethodsArrayTable;
    
    private String[][] codeFlowArray;
    private String[][] codeFlowArrayTable;
    
    private String[][] jSTArray;
    private String[][] jSTArrayTable;
    
    private final ArrayList<File> logFiles = new ArrayList<>();
    private final ArrayList<String> stringLogFiles = new ArrayList<>();
    private ArrayList<String> tabs = new ArrayList<>();
    
    private File pFile;
    
    private ArrayList batchAnomalies;
    private ArrayList oneToManyAnomalies;
    
    Stage stage;
    
    File sampleLogFilesFolder = new File("./sample_logs");
    
    CodeFlowGUI cF;
    
    @FXML
    private RadioButton anomaliesRadio;
    
    @FXML
    private RadioButton bAnomaliesRadio;
    
    @FXML
    private Button primaryBrowseButton;
    
    @FXML
    private Button analyzeButton;
    
    @FXML
    private TableView codeFlowTable;
    
    @FXML
    private TableView methodTimeTable;

    @FXML
    private BarChart methodTimeBarChart;
    
    @FXML
    private TableView nMethodsTable;

    @FXML
    private BarChart nMethodsBarChart;
    
    @FXML
    private TableView jSTTable;
    
    @FXML
    private ListView selectedFilesListView;
    
    @FXML
    private ListView batchAnomaliesListView; 
    
    @FXML
    private Accordion OTMAccordion;
    
    @FXML
    private VBox mainVBox;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private void initialize() {
        tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
        mainVBox.setVisible(false);
        methodTimeBarChart.setAnimated(false);
        nMethodsBarChart.setAnimated(false);
    }
    
    public void setStage(Stage stage) {
         this.stage = stage;
    }
    
    @FXML
    private void anomaliesRadioAction(ActionEvent event) {
        if(anomaliesRadio.isSelected()) {
            primaryBrowseButton.setDisable(false);
        } else {
            if (pFile != null) {
                pFile = null;
                primaryBrowseButton.setText("Select Primary File");
            }
            
            primaryBrowseButton.setDisable(true);
        }
    }
        
    @FXML
    private void browseAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        
        fileChooser.setInitialDirectory(sampleLogFilesFolder);
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        
        if (selectedFiles != null) {
            if (!(selectedFiles.size() > 2)) {
                bAnomaliesRadio.setDisable(true);
            } else {
                bAnomaliesRadio.setDisable(false);
            }
               
            logFiles.addAll(selectedFiles);
            logFiles.forEach((f) -> {
                stringLogFiles.add(f.getName());
            });

            updateSelectedFilesList();
        }
    }
        
    @FXML
    private void primaryBrowseAction() {
        FileChooser fileChooser = new FileChooser();
        
        fileChooser.setInitialDirectory(sampleLogFilesFolder);
        pFile = fileChooser.showOpenDialog(stage);
        
        if (pFile != null)
            primaryBrowseButton.setText(pFile.getName());
    }
    
    @FXML
    private void analyzeAction() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        try {
            mainVBox.setVisible(true);
            
            ArrayList<File> filesList = logFiles;       
            ArrayList<String> stringFilesList = stringLogFiles;
            ArrayList<String> stringFilesListCF = new ArrayList<>();
            stringFilesList.forEach((f) -> {
                stringFilesListCF.add(f);
            });
            
            stringFilesList.add(0, "Methods");
            
            if (pFile != null) {
                filesList.add(0, pFile);
                stringFilesListCF.add(0, pFile.getName());
                stringFilesList.add(1, pFile.getName());
                
                Anomalies anomalies = new Anomalies(pFile, logFiles.toArray(new File[2]));
                oneToManyAnomalies = anomalies.anomalies();
                anomaliesArray = Tools.toStringArray(anomalies.anomalies(), Tools.maxInnerArraySize(anomalies.anomalies()));
                updateOTMAnomaliesList();
            }
            
            Analyser analyser = new Analyser(filesList.toArray(new File[filesList.size()]), bAnomaliesRadio.isSelected());
            if(logFiles.size() > 2 && bAnomaliesRadio.isSelected()) {
                batchAnomalies = analyser.getBatchAnomalies();
                updateBAnomaliesList();
            }
            
            methodTimeArray = Tools.toStringArrayAlt(analyser.getAnalysedTime(), (Integer) ((ArrayList) analyser.getAnalysedTime().get(0)).size());
            methodTimeArrayTable = Tools.toStringArray(analyser.getAnalysedTime(), (Integer) ((ArrayList) analyser.getAnalysedTime().get(0)).size(), stringFilesList.toArray(new String[stringFilesList.size()]));
        
            nMethodsArray = Tools.toStringArrayAlt(analyser.getAnalysedNMethods(), (Integer) ((ArrayList) analyser.getAnalysedNMethods().get(0)).size());
            nMethodsArrayTable = Tools.toStringArray(analyser.getAnalysedNMethods(), (Integer) ((ArrayList) analyser.getAnalysedNMethods().get(0)).size(), stringFilesList.toArray(new String[stringFilesList.size()]));
            
            //codeFlowArray = Tools.toStringArrayAlt(analyser.getLogText(), Tools.maxInnerArraySize(analyser.getLogText()));
            codeFlowArrayTable = Tools.toStringArrayMethods(analyser.getLogText(), Tools.maxInnerArraySize(analyser.getLogText()), (String[]) stringFilesListCF.toArray(new String[stringFilesListCF.size()]));
        
            cF = new CodeFlowGUI(codeFlowArrayTable, stringLogFiles, analyser.getLogSequence(), "CodeFlow");
            
            jSTArray = Tools.toStringArrayAlt(analyser.getAnalysedJST(), (Integer) ((ArrayList) analyser.getAnalysedJST().get(0)).size());
            jSTArrayTable = Tools.toStringArray(analyser.getAnalysedJST(), (Integer) ((ArrayList) analyser.getAnalysedJST().get(0)).size(), (String[]) stringFilesList.toArray(new String[stringFilesList.size()]));
            
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        populateMethodTimeTable();
        populateCodeFlowTable();
        plotMethodTimeBarChart();
        populateNMethodsTable();
        plotNMethodsBarChart();
        populateJSTTable();
        progressBar.setProgress(0);
    }
    
    @FXML
    private void clearButtonAction() {
        mainVBox.setVisible(false);
        
        logFiles.clear();
        stringLogFiles.clear();
        
        bAnomaliesRadio.setSelected(false);
        anomaliesRadio.setSelected(false);
        primaryBrowseButton.setDisable(true);
        
        batchAnomaliesListView.getItems().clear();
        OTMAccordion.getPanes().clear();
        
        if (pFile != null) {
            pFile = null;
            primaryBrowseButton.setText("Select Primary File");
        }
                
        updateSelectedFilesList();
    }
    
    @FXML
    private void visualizeCodeFlow() {
        cF.setSize(500, 500);
        cF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cF.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cF.setVisible(true);
    }
    
    @FXML
    private void traceClassAction() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Trace class files");
        dialog.setHeaderText("Class Tracing");

        ButtonType loginButtonType = new ButtonType("Trace", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField browseField = new TextField();
        TextField methodsField = new TextField();
        TextField vmField = new TextField();
        
        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
        
                fileChooser.setInitialDirectory(sampleLogFilesFolder);
                File selectedFile = fileChooser.showOpenDialog(stage);
                
                if (selectedFile != null) {
                    browseField.setText(selectedFile.getName());
                }
            }
        });
        
        grid.add(new Label("Select Class file:"), 0, 0);
        grid.add(browseField, 1, 0);
        grid.add(browseBtn, 2, 0);
        grid.add(new Label("Methods:"), 0, 1);
        grid.add(methodsField, 1, 1);
        grid.add(new Label("VM arguments:"), 0, 2);
        grid.add(vmField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String[] ar = {browseField.getText(), methodsField.getText(), vmField.getText()};
                return ar;
            }
        return null;
        });
        
        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(data -> {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            classToLogGen(data[0], data[1], data[2]);
            logFiles.add(new File("sample_logs/" + data[0].split(".class")[0] + ".fmt"));
            stringLogFiles.add(logFiles.get(logFiles.size()-1).getName());
            updateSelectedFilesList();
            progressBar.setProgress(0);
        });
    }
    
    @FXML
    private void handleFilesSelect(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            File selectedFile = logFiles.get(selectedFilesListView.getSelectionModel().getSelectedIndex());

            boolean exist = false;
            for (Tab t: tabPane.getTabs()) {
                if((t.getText()+" [Log Report]").contains(selectedFile.getName())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                String data = "";
                try {
                    data = FileUtils.readFileToString(selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }

                Tab newTab = new Tab(selectedFile.getName() + " [Log Report]");
                TextArea t = new TextArea(data);
                newTab.setContent(t);
                tabPane.getTabs().add(newTab);
                tabs.add(selectedFile.getName());
            }
        }
    }
    
    private void classToLogGen(String srcFile, String methods, String vm_args) {
        Process p;
        try {
            List<String> cmdList = new ArrayList<>();
            cmdList.add("cmd");
            cmdList.add("/c");
            cmdList.add("cd");
            cmdList.add(System.getProperty("user.dir"));
            cmdList.add("&&");
            cmdList.add("cp");
            cmdList.add("sample_logs\\"+srcFile);
            cmdList.add(srcFile);
            cmdList.add("&&");
            cmdList.add(".\\openjdk\\bin\\java.exe");
            cmdList.add("\"-Xtrace:none\"");
            cmdList.add("\"-Xtrace:output=sample_logs\\"+srcFile.split(".class")[0]+".trc,maximal=mt,methods={"+methods+"},trigger=method{"+methods+",jstacktrace}\"");
            cmdList.add(srcFile.split(".class")[0]);
            cmdList.addAll(Arrays.asList(vm_args.split(" ")));
            cmdList.add("&&");
            cmdList.add(".\\openjdk\\bin\\java.exe");
            cmdList.add("com.ibm.jvm.TraceFormat");
            cmdList.add("sample_logs\\"+srcFile.split(".class")[0]+".trc");
            cmdList.add("sample_logs\\"+srcFile.split(".class")[0]+".fmt");
            
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(cmdList);
            p = pb.start();
            p.waitFor(); 
            
            /*Scanner s = new Scanner(p.getErrorStream());
            while (s.hasNextLine()) {
                System.out.println(s.nextLine());
            }
            
            Scanner sc = new Scanner(p.getInputStream());
            while (sc.hasNext())
                System.out.println(sc.nextLine());*/
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
        
    private void updateSelectedFilesList() {
        selectedFilesListView.getItems().clear();
        ObservableList<String> data = FXCollections.<String>observableArrayList();
        data.addAll(stringLogFiles);
        
        selectedFilesListView.setOrientation(Orientation.VERTICAL);
        
        
        selectedFilesListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                final ListCell cell = new ListCell() {
                    private Text text;

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(item.toString());
                            setTextFill(Tools.colorPicker(Tools.find(stringLogFiles, 0, item.toString())+1));
                        }
                    }
                };

                return cell;
            }
        });
        selectedFilesListView.getItems().addAll(data);
    }
        
    private void updateBAnomaliesList() {
        batchAnomaliesListView.getItems().clear();
        ObservableList<String> data = FXCollections.<String>observableArrayList();
        data.addAll(batchAnomalies);
        
        batchAnomaliesListView.setOrientation(Orientation.VERTICAL);
        batchAnomaliesListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                final ListCell cell = new ListCell() {
                    private Text text;

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(batchAnomaliesListView.getWidth());
                            setGraphic(text);
                        }
                    }
                };

                return cell;
            }
        });
        batchAnomaliesListView.getItems().addAll(data);    
    }
    
    private void updateOTMAnomaliesList() {
        OTMAccordion.getPanes().clear();
        for (int i = 1; i < oneToManyAnomalies.size(); i++) {
            TitledPane pane = new TitledPane();
            ListView lView = new ListView();
            
            ObservableList<String> data = FXCollections.<String>observableArrayList();
            for (int j = 1; j < ((ArrayList) oneToManyAnomalies.get(i)).size(); j++) {
                data.addAll((String) ((ArrayList) oneToManyAnomalies.get(i)).get(j));
            }

            lView.setOrientation(Orientation.VERTICAL);
            lView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> list) {
                    final ListCell cell = new ListCell() {
                        private Text text;

                        @Override
                        public void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item.toString());
                                text.setWrappingWidth(OTMAccordion.getWidth());
                                setGraphic(text);
                            }
                        }
                    };

                    return cell;
                }
            });
            lView.getItems().addAll(data);
            
            pane.setText(pFile.getName() + " -> " + ((ArrayList) oneToManyAnomalies.get(i)).get(0));
            pane.setContent(lView);
            
            OTMAccordion.getPanes().add(pane);
        }
        
    }
    
    private void populateCodeFlowTable() {        
        codeFlowTable.getColumns().clear();
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(codeFlowArrayTable));
        data.remove(0);//remove titles from data
        for (int i = 0; i < codeFlowArrayTable[0].length; i++) {
            TableColumn tc = new TableColumn(codeFlowArrayTable[0][i]);
            tc.setSortable(false);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {           
                    return new SimpleStringProperty((p.getValue()[colNo]));                    
                }
            });
            
            tc.setCellFactory(e -> {
                return new TableCell<ObservableList<String>, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        // Always invoke super constructor.
                        super.updateItem(item, empty);
                        
                        setText(item);

                        if(item != null && item.contains("<out>")) {
                            // out
                            this.setTextFill(Color.BLUE);
                        } else if (item != null && item.contains("<in>")) {
                            // in
                            this.setTextFill(Color.GREEN);
                        } else {
                            this.setTextFill(Color.WHITE);
                        }
                    }
                };
            });

            codeFlowTable.getColumns().add(tc);
        }
        codeFlowTable.setItems(data);
    }
    
    private void populateMethodTimeTable() {
        methodTimeTable.getColumns().clear();
        methodTimeTable.setStyle("-fx-selection-bar: skyblue;");
        ObservableList<String[]> methodTimeData = FXCollections.observableArrayList();
        methodTimeData.addAll(Arrays.asList(methodTimeArrayTable));
        methodTimeData.remove(0);//remove titles from data
        for (int i = 0; i < methodTimeArrayTable[0].length; i++) {
            TableColumn tc = new TableColumn(methodTimeArrayTable[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            
            tc.setCellFactory(e -> {
                return new TableCell<ObservableList<String>, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (colNo >= 1) {
                            setTextFill(Tools.colorPicker(colNo));
                        }
                    }
                    
                    
                };
            });
            
            methodTimeTable.getColumns().add(tc);
        }
        methodTimeTable.setItems(methodTimeData);
        
    }
    
    private void populateJSTTable() {
        jSTTable.getColumns().clear();
        jSTTable.setStyle("-fx-selection-bar: skyblue;");
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(jSTArrayTable));
        data.remove(0);//remove titles from data
        for (int i = 0; i < jSTArrayTable[0].length; i++) {
            TableColumn tc = new TableColumn(jSTArrayTable[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            
            tc.setCellFactory(e -> {
                return new TableCell<ObservableList<String>, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (colNo >= 1) {
                            setTextFill(Tools.colorPicker(colNo));
                        }
                    }
                    
                    
                };
            });

            jSTTable.getColumns().add(tc);
        }
        jSTTable.setItems(data);
        
    }
    
    private void plotMethodTimeBarChart() {
        methodTimeBarChart.getData().clear();
        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel("Methods");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Method Time Invocation in milliseconds");

        for (int j=0; j < logFiles.size(); j++) {
            XYChart.Series dataSeries = new XYChart.Series();
            dataSeries.setName(logFiles.get(j).getName());

            for (int i = 0; i < methodTimeArray[0].length; i++) {
                dataSeries.getData().add(new XYChart.Data(methodTimeArray[0][i], Integer.valueOf(methodTimeArray[j+1][i])));
            }
            methodTimeBarChart.getData().add(dataSeries);
        }
    }
    
    private void populateNMethodsTable() {
        nMethodsTable.getColumns().clear();
        nMethodsTable.setStyle("-fx-selection-bar: skyblue;");
        ObservableList<String[]> nMethodsData = FXCollections.observableArrayList();
        nMethodsData.addAll(Arrays.asList(nMethodsArrayTable));
        nMethodsData.remove(0);//remove titles from data
        for (int i = 0; i < nMethodsArrayTable[0].length; i++) {
            TableColumn tc = new TableColumn(methodTimeArrayTable[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            
            tc.setCellFactory(e -> {
                return new TableCell<ObservableList<String>, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if (colNo >= 1) {
                            setTextFill(Tools.colorPicker(colNo));
                        }
                    }
                    
                    
                };
            });

            nMethodsTable.getColumns().add(tc);
        }
        nMethodsTable.setItems(nMethodsData);
        
    }
    
    private void plotNMethodsBarChart() {
        nMethodsBarChart.getData().clear();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Methods");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of times method executed");

        for (int j=0; j < logFiles.size(); j++) {
            XYChart.Series dataSeries = new XYChart.Series();
            dataSeries.setName(logFiles.get(j).getName());

            for (int i = 0; i < nMethodsArray[0].length; i++) {
                dataSeries.getData().add(new XYChart.Data(nMethodsArray[0][i], Integer.valueOf(nMethodsArray[j+1][i])));
            }
            nMethodsBarChart.getData().add(dataSeries);
        }
    }
}
