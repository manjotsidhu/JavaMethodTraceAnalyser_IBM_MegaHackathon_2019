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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main GUI Application using javaFX
 * @author Manjot Sidhu
 */
public class Main extends Application {

    /**
     * Main method that launches the start method
     * 
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("1.fxml"));
        Parent root = (Parent)loader.load();
        
        MainController c = (MainController)loader.getController();       
        c.setStage(primaryStage);
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Java Method Trace Analyzer");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
