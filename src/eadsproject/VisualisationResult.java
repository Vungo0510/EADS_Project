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
import java.util.*;
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

import javafx.scene.chart.*;
import javafx.collections.*;
         
public class VisualisationResult { 
  // @Override 
   public void startResult(HashMap<String, Double> modifiedRoutes) {     
        Stage stage = new Stage(); 
       
        // Creating a border pane
        BorderPane borderPane = new BorderPane();
        Text text = new Text(); // creating this for the top of our border pane
        borderPane.setTop(text);     
        
        double textX = 0.0;
        double textY = 10.0;
        
        Iterator modifiedRoutesKeySetIter = modifiedRoutes.keySet().iterator();
        
        //Creating a top pane object  
        Pane topPane = new Pane(); 
        
        Text localSearchText = new Text();
        
        Text titleText = new Text(textX, textY, " RESULTS: ");
        
        topPane.getChildren().addAll(titleText);

        
        while (modifiedRoutesKeySetIter.hasNext()) {
            //textX +=100.0;
            textY +=30.0;
            String thisModifiedRoute = (String) modifiedRoutesKeySetIter.next();
            double totalTimeInSecs = modifiedRoutes.get(thisModifiedRoute);
            localSearchText = new Text(textX, textY, " Route to take: " + thisModifiedRoute);
            textY +=15.0;
            Text totalTimeInSecsText = new Text(textX, textY, " Total time: " + Double.toString(totalTimeInSecs));
                 topPane.getChildren().addAll(localSearchText);
                 topPane.getChildren().addAll(totalTimeInSecsText);

           // hbox.getChildren().addAll(localSearchText, totalTimeInSecsText);
        }
        
        
        
        borderPane.setTop(topPane);
        
        
        // creating a center pane object
         Pane centerPane = new Pane(); 
  
        NumberAxis  xAxis = new NumberAxis ();
        NumberAxis  yAxis = new NumberAxis ();      
        xAxis.setLabel("x axis");
        yAxis.setLabel("y axis");
        
        
        LineChart lineChart = new LineChart(xAxis, yAxis);
      
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        //populate series with data
        series.getData().add(new XYChart.Data<>(1, 23));
        series.getData().add(new XYChart.Data<>(2, 14));
        series.getData().add(new XYChart.Data<>(3, 15));
        series.getData().add(new XYChart.Data<>(4, 24));
        series.getData().add(new XYChart.Data<>(2, 24));
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        lineChart.getData().add(series);

       // Scene scene  = new Scene(new BorderPane(linechart),800,600);

borderPane.setCenter(lineChart);
        
        
        
        
        
        //Creating horizontal box
        HBox hbox = new HBox();
       
        
       
        borderPane.setBottom(hbox);
        
        //creating a scene object
        Scene scene = new Scene(borderPane, 1400, 800);
       
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
