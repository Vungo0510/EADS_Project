/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

/**
 *
 * @author Cathy
 */
import java.util.HashMap;
import javafx.application.Application; 
import javafx.scene.Group; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.text.*;
import javafx.scene.Scene; 
import javafx.stage.Stage; 
import javafx.scene.shape.LineTo; 
import javafx.scene.shape.MoveTo; 
import javafx.scene.shape.Path; 
         
public class VisualisationResult { 
  // @Override 
   public void startResult(HashMap<String, Double> modifiedRoutes) {     
        Stage stage = new Stage(); 
       
        // Creating a border pane
        BorderPane borderPane = new BorderPane();
        Text text = new Text(); // creating this for the top of our border pane
        borderPane.setTop(text);
        
        //Creating a pane object  
        Pane centerPane = new Pane(); 
        borderPane.setCenter(centerPane);
        
        //Creating horizontal box
        HBox hbox = new HBox();
        double totalTimeInSecs = modifiedRoutes.get("1.0,4.0,0.0-20.0,58.0,5.1-16.0,56.0,2.1-17.0,47.0,6.1-8.0,50.0,2.1-8.0,17.0,2.1-8.0,1,0");
        
        Text localSearchText = new Text("Route to take: " + modifiedRoutes.toString());
        Text totalTimeInSecsText = new Text("Total time: " + Double.toString(totalTimeInSecs));
        hbox.getChildren().addAll(localSearchText, totalTimeInSecsText);
        borderPane.setBottom(hbox);
        
        //creating a scene object
        Scene scene = new Scene(borderPane, 800, 800);
       
        //Creating a Path 
        Path path = new Path(); 

        //Moving to the starting point 
        MoveTo moveTo = new MoveTo(108, 71); 

        //Creating 1st line 
        LineTo line1 = new LineTo(321, 161);  

        //Creating 2nd line 
        LineTo line2 = new LineTo(126,232);       

        //Creating 3rd line 
        LineTo line3 = new LineTo(232,52);  

        //Creating 4th line 
        LineTo line4 = new LineTo(269, 250);   

        //Creating 4th line 
        LineTo line5 = new LineTo(108, 71);  

        //Adding all the elements to the path 
        path.getElements().add(moveTo); 
        path.getElements().addAll(line1, line2, line3, line4, line5);        
         

        //Setting title to the Stage 
        stage.setTitle("Drawing an arc through a path");

        //Adding scene to the stage 
        stage.setScene(scene);

        //Displaying the contents of the stage 
        stage.show();         
   } 
 
}       
