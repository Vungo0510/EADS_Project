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
import javafx.scene.chart.XYChart.*;
         
public class VisualisationResult { 
  // @Override 
   public void startResult(TreeMap<String, Double> modifiedRoutes,  TreeMap<String, Double> routeInOriginalLocationMap, String Name) {     
        Stage stage = new Stage(); 
                       
        // Creating a border pane
        BorderPane borderPane = new BorderPane();
        Text text = new Text(); // creating this for the top of our border pane
        borderPane.setTop(text);     
        
        double textX = 0.0;
        double textY = 24.0;
        
        
        //Creating a top pane object  
        Pane topPane = new Pane(); 
        
        Text localSearchText = new Text();
        
        Text titleText = new Text(textX, textY, Name + " RESULTS: ");
        titleText.setFont( Font.font("Arial", 20.0));
        
        topPane.getChildren().addAll(titleText);
       
        Iterator modifiedRoutesKeySetIter = modifiedRoutes.keySet().iterator();
        Iterator originalLocationRoutesKeySetIter1 = routeInOriginalLocationMap.keySet().iterator();

        Iterator originalLocationRoutesKeySetIter = routeInOriginalLocationMap.keySet().iterator();

        while (originalLocationRoutesKeySetIter1.hasNext()) {
            //print out the routes & time in prose            
            textY +=30.0;
            String thisOriginalLocationRoute = (String) originalLocationRoutesKeySetIter1.next();

            double totalTimeInSecs = routeInOriginalLocationMap.get(thisOriginalLocationRoute);
            localSearchText = new Text(textX, textY, " Route to take: " + thisOriginalLocationRoute);
         

            textY +=15.0;
            Text totalTimeInSecsText = new Text(textX, textY, " Total time: " + Double.toString(totalTimeInSecs));
            //setting font size for route and time taken
            localSearchText.setFont( Font.font("Arial", 15.0));
            totalTimeInSecsText.setFont( Font.font("Arial", 15.0));
            topPane.getChildren().addAll(localSearchText);
            topPane.getChildren().addAll(totalTimeInSecsText);

        }
        
        
        
        borderPane.setTop(topPane);
        
        
        // creating a center pane object to be used for the diagram
         Pane centerPane = new Pane(); 
  
        NumberAxis xAxis = new NumberAxis ();
        NumberAxis  yAxis = new NumberAxis ();      
        xAxis.setLabel("x axis");
        yAxis.setLabel("y axis");
        
        LineChart<Double, Double> lineChart = new LineChart(xAxis, yAxis);      
        
        
        
        Iterator modifiedRoutesKeySetIter2 = modifiedRoutes.keySet().iterator();

        originalLocationRoutesKeySetIter.hasNext();
        
        
        while (modifiedRoutesKeySetIter2.hasNext() ) {
            int counter = 0;
            XYChart.Series<Double, Double> series = new XYChart.Series<>();
            series.setName("Route " + counter);
            series.getData().clear();

            //adding all the nodes into the diagram
            textY +=30.0;
            String thisModifiedRoute = (String) modifiedRoutesKeySetIter2.next();
            String thisOriginalLocationRoute = (String) originalLocationRoutesKeySetIter.next(); // returns one route in original location
            String[] thisModifiedRouteArr = thisModifiedRoute.split("-");
            String[] thisOriginalLocationRouteArr = thisOriginalLocationRoute.split("-");
            
            
            
            for(String nodesInOriginalLocationRoute: thisOriginalLocationRouteArr){
                // to loop through all the nodes in thisOriginalLocationRouteArr
                         
                
                
                 System.out.println(nodesInOriginalLocationRoute + "  nodesInOriginalLocationRoute");
                 Double nodesInOriginalLocationRouteXCoord = 0.0;
                 Double nodesInOriginalLocationRouteYCoord = 0.0;
                 
                 if(nodesInOriginalLocationRoute.contains(",")){
                     // this is either a start node or corner nodes
                    nodesInOriginalLocationRouteXCoord = Double.parseDouble(nodesInOriginalLocationRoute.split(",")[0]);
                    nodesInOriginalLocationRouteYCoord = Double.parseDouble(nodesInOriginalLocationRoute.split(",")[2]); 
                 
                 
                 }else{
                     // this is a pick node
                    nodesInOriginalLocationRouteXCoord = Double.parseDouble(nodesInOriginalLocationRoute.substring(0,2));
                    nodesInOriginalLocationRouteYCoord = Double.parseDouble(nodesInOriginalLocationRoute.substring(3,5)); 
                 
                 
                 }
                            
           
                //populate series with data
                Data<Double, Double> d = new XYChart.Data<>(nodesInOriginalLocationRouteXCoord, nodesInOriginalLocationRouteYCoord);               
                series.getData().add(d);
                 
                counter +=1;
            
            }
            
             lineChart.getData().add(series);
            
        

           // hbox.getChildren().addAll(localSearchText, totalTimeInSecsText);
        }
        

      
       
       
        //series.getData().add(new XYChart.Data<>(2, 14));
          
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
       
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
