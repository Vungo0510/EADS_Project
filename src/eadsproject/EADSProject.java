/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

/**
 *
 * @author Snow Petrel
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.NumberFormatException;

import javafx.application.Application; 
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList; 

import javafx.geometry.Insets; 
import javafx.geometry.Pos; 

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene; 
import javafx.scene.control.Button; 
import javafx.scene.control.CheckBox; 
import javafx.scene.control.ChoiceBox; 
import javafx.scene.control.DatePicker; 
import javafx.scene.control.ListView; 
import javafx.scene.control.RadioButton; 
import javafx.scene.layout.GridPane; 
import javafx.scene.text.Text; 
import javafx.scene.control.TextField; 
import javafx.scene.control.ToggleGroup;  
import javafx.scene.control.ToggleButton; 
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.stage.FileChooser;
import javafx.stage.Stage; 
         
public class EADSProject extends Application { 
   private File pickListFile;
   private File cornerNodesFile;
      
   public void start(Stage stage) {    
      //Label for MHE capacity 
      Text mheCapacityLabel = new Text("MHE Capacity"); 
      
      //Text field for MHE Capacity 
      TextField mheCapacityText = new TextField(); 
      
      //Label for MHE capacity units
      Text mheCapacityUnitLabel = new Text("(in terms of cartons)");  
      
      Text mheCapacityError = new Text();
      mheCapacityError.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      //Label for num of MHE
      Text numOfMHELabel = new Text("Total number of MHEs"); 
     
      //Text field for num of MHE 
      TextField numOfMHEText = new TextField(); 
      
      Text numOfMHEError = new Text();
      numOfMHEError.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      //Label for starting point
      Text startingPointLabel = new Text("Current MHE position"); 
     
      //Text field for num of MHE 
      TextField startingPointText = new TextField(); 
      
      Text startingPointError = new Text();
      startingPointError.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      FileChooser fileChooser = new FileChooser();
      // Set extension filter
        FileChooser.ExtensionFilter extFilter = 
            new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

      
      //Label for picking list csv file chooser
      Text pickingListCSVLabel = new Text("Picking list CSV file"); 
      Button choosePickingListBtn = new Button("Upload picking list...");
      
      Text pickListError = new Text();
      pickListError.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      //Label for corner node csv file chooser
      Text cornerNodesCSVLabel = new Text("Corner nodes CSV file"); 
      Button chooseCornerNodesBtn = new Button("Upload Corner nodes...");
      Text cornerNodeError = new Text();
      cornerNodeError.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      Text pickListFileName = new Text();
      pickListFileName.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      Text cornerNodesFileName = new Text();
      cornerNodesFileName.setFont(Font.font("Calibri", FontWeight.NORMAL, 15));
      
      choosePickingListBtn.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    pickListFile = fileChooser.showOpenDialog(stage);
                    if (pickListFile != null) {
                        pickListFileName.setText(pickListFile.getName());
                    }
                }
            });

      chooseCornerNodesBtn.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    cornerNodesFile = fileChooser.showOpenDialog(stage);
                    if (cornerNodesFile != null) {
                        cornerNodesFileName.setText(cornerNodesFile.getName());
                    }
                }
            });
      
      //Label for submit button 
      Button buttonSubmit = new Button("Submit");  
      
      buttonSubmit.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    CSVReader csvReader = new CSVReader();
                    boolean hasError = false;
                    
                    if (pickListFile == null) {
                        hasError = true;
                        pickListError.setText("Pick list file is required");
                    }
                    
                    if (cornerNodesFile == null) {
                        hasError = true;
                        cornerNodeError.setText("Corner node file is required");
                    }
                    
                    if (startingPointText.getText().equals("")) {
                        hasError = true;
                        startingPointError.setText("Starting point is required");
                    }
                    
                    String[] startingPtSplit = startingPointText.getText().split(",");
                    if (startingPtSplit.length != 2) {
                        hasError = true;
                        startingPointError.setText("Starting point has to be in the format d,d");
                    }
                    
                    try {
                        Integer.parseInt(startingPtSplit[0]);
                        Integer.parseInt(startingPtSplit[1]);
                    } catch (NumberFormatException nfe) {
                        hasError = true;
                        mheCapacityError.setText("X and Y coordinate of starting point must be an integer");
                    }
                    
                    if (mheCapacityText.getText().equals("")) {
                        hasError = true;
                        mheCapacityError.setText("MHE capacity is required");
                    }
                    
                    try {
                        Integer.parseInt(mheCapacityText.getText());
                    } catch (NumberFormatException nfe) {
                        hasError = true;
                        mheCapacityError.setText("MHE capacity must be a number");
                    }
                    
                    if (numOfMHEText.getText().equals("")) {
                        hasError = true;
                        numOfMHEError.setText("Number of MHE is required");
                    }
                    
                    try {
                        Integer.parseInt(mheCapacityText.getText());
                    } catch (NumberFormatException nfe) {
                        hasError = true;
                        mheCapacityError.setText("MHE capacity must be an integer");
                    }
                    
                    if (!hasError) {
                        ArrayList<String> pickingList = csvReader.readPickingList(pickListFile.getAbsolutePath());
                        HashMap<Integer, ArrayList<Integer>> cornerNodesMap = csvReader.readAllCornerNodes(cornerNodesFile.getAbsolutePath());
                        HashMap<String, Integer> pickItemCapacityMap = csvReader.readPickItemCapacity(pickListFile.getAbsolutePath());

                        System.out.println(pickingList);

                        Clarke c = new Clarke();

                        ArrayList<HashMap> intialSolution = c.getInitialSolution(pickingList, startingPointText.getText());
                        HashMap<String, Integer> distOfStartPtToAllPt = intialSolution.get(0);

                        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointDistance(pickingList);
                        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0);

                        HashMap<String, Integer> savingsMap = c.getSavingsMap(pickingList, startingPointText.getText());

                        HashMap<String, String> solutionMap = c.getSolution(pickItemCapacityMap, savingsMap, Double.parseDouble(mheCapacityText.getText()), startingPointText.getText());

                        ArrayList<String> finalRoutes = c.getFinalRoutes(solutionMap);

                        System.out.println("corner nodes: ");
                        System.out.println(cornerNodesMap);
                        System.out.println("Step 1: ");
                        System.out.println(distOfStartPtToAllPt);
                        System.out.println("Step 2: ");
                        System.out.println(distAmongPickItems);
                        System.out.println("Step 3: ");
                        System.out.println(savingsMap);
                        System.out.println("Step 4 and 5:");
                        System.out.println(solutionMap);
                        System.out.println("Step 5b:");
                        System.out.println(finalRoutes);
                        
                        System.out.println("Final routes and distance:");
                        System.out.println(c.getDistanceOfFinalRoutes(pickingList, finalRoutes, startingPointText.getText()));
                    } 
                }
            });
      
      //Creating a Grid Pane 
      GridPane gridPane = new GridPane();    
      
      //Setting size for the pane 
      gridPane.setMinSize(600, 400); 
       
      //Setting the padding    
      gridPane.setPadding(new Insets(10, 10, 10, 10));  
      
      //Setting the vertical and horizontal gaps between the columns 
      gridPane.setVgap(5); 
      gridPane.setHgap(5);       
      
      //Setting the Grid alignment 
      gridPane.setAlignment(Pos.CENTER); 
       
      //Arranging all the nodes in the grid 
      gridPane.add(mheCapacityLabel, 0, 0); 
      gridPane.add(mheCapacityText, 1, 0); 
      gridPane.add(mheCapacityUnitLabel, 2, 0);
      gridPane.add(mheCapacityError, 1, 1);
      
      gridPane.add(numOfMHELabel, 0, 2);       
      gridPane.add(numOfMHEText, 1, 2); 
      gridPane.add(numOfMHEError, 1, 3);
      
      gridPane.add(startingPointLabel, 0, 4);       
      gridPane.add(startingPointText, 1, 4); 
      gridPane.add(startingPointError, 1, 5);
              
      gridPane.add(pickingListCSVLabel, 0, 6); 
      gridPane.add(choosePickingListBtn, 1, 6); 
      gridPane.add(pickListFileName, 2, 6);
      gridPane.add(pickListError, 1, 7);
      
      gridPane.add(cornerNodesCSVLabel, 0, 8); 
      gridPane.add(chooseCornerNodesBtn, 1, 8); 
      gridPane.add(cornerNodesFileName, 2, 8);
      gridPane.add(cornerNodeError, 1, 9);
       
      gridPane.add(buttonSubmit, 2, 10);      
      
      //Styling nodes   
      buttonSubmit.setStyle("-fx-background-color: \n" +
"        linear-gradient(#f2f2f2, #d6d6d6),\n" +
"        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
"        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
"    -fx-background-radius: 8,7,6;\n" +
"    -fx-background-insets: 0,1,2;\n" +
"    -fx-text-fill: black;\n" +
"    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"); 
       
      mheCapacityLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
      mheCapacityUnitLabel.setStyle("-fx-font: normal 15px 'serif' ");
      numOfMHELabel.setStyle("-fx-font: normal bold 15px 'serif' "); 
      startingPointLabel.setStyle("-fx-font: normal bold 15px 'serif' "); 
      pickingListCSVLabel.setStyle("-fx-font: normal bold 15px 'serif' "); 
      cornerNodesCSVLabel.setStyle("-fx-font: normal bold 15px 'serif' "); 
       
      //Setting the back ground color 
      gridPane.setStyle("-fx-background-color: BEIGE;");       
       
      //Creating a scene object 
      Scene scene = new Scene(gridPane); 
      
      //Setting title to the Stage 
      stage.setTitle("Data Input Form"); 
         
      //Adding scene to the stage 
      stage.setScene(scene);  
      
      //Displaying the contents of the stage 
      stage.show(); 
   }      
   public static void main(String args[]){ 
      launch(args); 
   } 
}