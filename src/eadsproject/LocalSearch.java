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


public class LocalSearch {
    
    public TreeMap<String, Double> localSearch(ArrayList<String> finalRoutes, ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime){
    
        Clarke c = new Clarke();
        // retrieve time from start pt to all pt
        ArrayList<HashMap> initialSolution = c.getInitialSolution(pickingList, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        HashMap<String, Double> distOfStartPtToAllPt = initialSolution.get(0); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node
        HashMap<String, String> routeOfStartPtToAllPt =initialSolution.get(1);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointTime(pickingList,cornerNodeFilePath,mheTravelTime, mheLiftingTime); 
        HashMap<String, Double> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance
        HashMap<String, String> routeFromCurrPickNodeToOtherPickNode = ptToPtRouteAndDistanceArr.get(1);//Key is in format: x-y-z coordinate of current pick node + "to" + x-y-z coordinate of other pick node, value is the full route from current pick node to other pick node
        
        HashMap<String, Double> finalRoutesDistHashMap = c.getTimeOfFinalRoutes ( pickingList,finalRoutes, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        
        TreeMap<String, Double> localSearchRoutesMap = new TreeMap<>();
        
        String[] startPtSplit = startingPoint.split(","); //need to add Z coordinate to starting point string and convert X Y coordinates from integer to double
        startingPoint = Double.parseDouble(startPtSplit[0]) + "," + Double.parseDouble(startPtSplit[1]) + ",0.0"; 
            
        
        for(int i = 0; i < finalRoutes.size(); i++){
            // loop through the arrayList of finalRoutes to do local search on each route
            String finalRoute = finalRoutes.get(i);
            double finalRouteTotalDist = finalRoutesDistHashMap.get(finalRoute);
            //System.out.println(" finalRouteTotalDist " + finalRouteTotalDist);
            
            String[] finalRouteArr = finalRoute.split("-"); //first and last element are to be ignored: they are the starting position and packing position
            String[] modifiedRouteArr = finalRoute.split("-"); // to store the better modified route later
            String[] replicateFinalRouteArr = finalRoute.split("-");
            
            //ArrayList<String> finalAns = new ArrayList<String>();
            
            String[] toKeepRoute = new String[finalRouteArr.length];
            String bestLocalSearchRoute = finalRoute;
            Double bestDist = finalRouteTotalDist;
                
            if(finalRouteArr.length<= 3){
                //System.out.println("      check local search   ");
                
                //System.out.println(finalRoute);
            }
            
            if( finalRouteArr.length>3 ){
                int totalSwap = finalRouteArr.length/2;
                int noOfSwapSoFar = 0;
                
                Random rand = new Random();
                int firstPositionToSwap = rand.nextInt(finalRouteArr.length -2) +1;
                int secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2) +1;
                
                while(secondPositionToSwap == firstPositionToSwap){
                    //ensures that the two elements that are to be swap are not the same element
                    secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2)+1;
                }
                
                for(int j=0; j< totalSwap; j++){
                    //swap  two random elements after starting position
                   
                    String firstPositionToSwapStr = replicateFinalRouteArr[firstPositionToSwap];
                    String secondPositionToSwapStr = replicateFinalRouteArr[secondPositionToSwap];
                    replicateFinalRouteArr[firstPositionToSwap] = secondPositionToSwapStr;
                    replicateFinalRouteArr[secondPositionToSwap] = firstPositionToSwapStr;
                    
                    // calculate total dist of new route
                    double totalDist = 0.0;
                    double distOfStartPtToFirstPickNode = 0.0;
                    
                    
                    if (replicateFinalRouteArr[0].equals(startingPoint)) { //if this is the first route
                        distOfStartPtToFirstPickNode = distOfStartPtToAllPt.get(replicateFinalRouteArr[1]);
                    } else { // if this is not the first route, starting point will be last node of previous route
                        //System.out.println("HEEEREE");
                        String[] firstNodeSplit = replicateFinalRouteArr[0].split(",");
                        String[] firstPickNodeSplit = replicateFinalRouteArr[1].split(",");
                        
                        Double firstNodeXCoord = Double.parseDouble(firstNodeSplit[0]);
                        Double firstPickNodeXCoord = Double.parseDouble(firstPickNodeSplit[0]);
                        Double firstPickNodeYCoord = Double.parseDouble(firstPickNodeSplit[1]);
                        Double firstPickNodeZCoord = Double.parseDouble(firstPickNodeSplit[2]);
                        
                        distOfStartPtToFirstPickNode = (Math.abs(firstNodeXCoord - firstPickNodeXCoord) * 1.425 + Math.abs(firstPickNodeYCoord - 1) * 0.9) * mheTravelTime + firstPickNodeZCoord * mheLiftingTime;
                        //System.out.println("nodes: " + firstNodeXCoord + "--" + firstPickNodeXCoord  + "--" + firstPickNodeYCoord + "--" + firstPickNodeZCoord);
                    }
                    //System.out.println("start to first pick: " + distOfStartPtToFirstPickNode);
                    totalDist += distOfStartPtToFirstPickNode;
                   
                    
                    for(int k = 1; k<replicateFinalRouteArr.length-2; k++ ){
                        // find the distance of all the pick nodes in the routes all the way to the packing location in the route that has finished swapping 2 pick items
                        String pickNode1 = replicateFinalRouteArr[k];                     
                        
                        
                        String pickNode2 = replicateFinalRouteArr[k+1];
                        
                        double distBetweenPickNodes = 0;
                        if(distAmongPickItems.get(pickNode1 +"to" + pickNode2) !=null){
                            
                            distBetweenPickNodes = distAmongPickItems.get(pickNode1 +"to" + pickNode2);
                        }else{
                            distBetweenPickNodes = distAmongPickItems.get(pickNode2 +"to" + pickNode1);
                        }
                    
                        totalDist +=distBetweenPickNodes;
                        //System.out.println("pick to pick: " + distBetweenPickNodes + "-- dist so far: " + totalDist);
                        //System.out.println("dist so far: " +totalDist );
                      
                    
                    }
                     // adding in the z coord distance of the last pick node into the total distance
                    String lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                    double ycoordOfLastPickNode = Double.parseDouble(lastPickNode.split(",")[1]);
                    double zcoordOfLastPickNode = Double.parseDouble(lastPickNode.split(",")[2]);
                    double distFromLastPickNodeToLastNode = (ycoordOfLastPickNode - 1.0)* 0.9 * mheTravelTime + (zcoordOfLastPickNode * mheLiftingTime);
                 
                    totalDist += distFromLastPickNodeToLastNode;
                    //System.out.println("last pick to last: " + distFromLastPickNodeToLastNode +"--" + totalDist);
                    
                    if(totalDist < bestDist){
                        String thisLocalSearchRoute = "";
                        //System.out.println("Stores j " + j);
                        //finalAns = replicateFinalRouteArr;
                        finalRouteTotalDist = totalDist;
                        //System.out.println(" stored j route: " );
                        
                        //finalAns.clear();
                        //finding the x and y coord of last pick node
                         lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                        double xcoordOfLastPickNode = Double.parseDouble(lastPickNode.split(",")[0]);
                        //changing the pack node to be of the same x coord of the last pick node, with y coord = 1
                        String changePackNode = xcoordOfLastPickNode + ",1.0,0.0";
                                         
                        
                        for(int index = 0; index < replicateFinalRouteArr.length - 1; index++){
                            String s = replicateFinalRouteArr[index];
                            thisLocalSearchRoute += s + "-";
                            
                        }
                        thisLocalSearchRoute += changePackNode;
                        
                        //System.out.println("best this loc search route so far: " + thisLocalSearchRoute);
                        //System.out.println("best dist so far for this route: " + totalDist);
                        bestLocalSearchRoute = thisLocalSearchRoute;
                        bestDist = totalDist;
                        
                    }
                    
                    
                
                }                   
            
            }
            
     
            localSearchRoutesMap.put(bestLocalSearchRoute, bestDist);
            
        
        }
        
       
        return localSearchRoutesMap;
    
    }
    
    public TreeMap<String,Double> addCornerNodesToRoutes (TreeMap<String,Double> routesMap, HashMap<String, String> routeOfStartPtToAllPt, HashMap<String, String> routeFromCurrPickNodeToOtherPickNode, String startingPoint) {
        TreeMap<String, Double> modifiedRoutes = new TreeMap<String, Double>(); //to store all the routes after local search is completed
        String modifiedRouteStr = "";
        Set lsRoutesKeySet = routesMap.descendingKeySet();
        //System.out.println("routes map keyset: " + lsRoutesKeySet);
        
        ArrayList<String> finalRoutes = new ArrayList<String>(lsRoutesKeySet);
        //System.out.println("Final routes arr: " + finalRoutes);
        
        for (int i = 0; i < finalRoutes.size(); i++) {
            String finalAnsWithCornerNodes = "";
            
            ArrayList<String> finalAns = new ArrayList<String>();
            String thisRoute = finalRoutes.get(i);
            //System.out.println("this route iz: " + thisRoute);
            
            String[] startPtSplit = startingPoint.split(","); //need to add Z coordinate to starting point string
            startingPoint = Double.parseDouble(startPtSplit[0]) + "," + Double.parseDouble(startPtSplit[1]) + ",0.0"; 
            //System.out.println("starting point after edit is: " + startingPoint);
            
            Double finalRouteTotalDist = routesMap.get(thisRoute);
            String[] thisRouteSplit = thisRoute.split("-");
            
            for (String node : thisRouteSplit) {
                finalAns.add(node);
            }
            
            if(finalAns.get(0).equals(startingPoint)){ //this is the first route with current MHE location as start node
                String routeFromStartNodeToFirstPickNode = routeOfStartPtToAllPt.get( finalAns.get(1));
                routeFromStartNodeToFirstPickNode = routeFromStartNodeToFirstPickNode.substring(0, routeFromStartNodeToFirstPickNode.lastIndexOf("-"));
                finalAnsWithCornerNodes +=routeFromStartNodeToFirstPickNode + "-";
                //System.out.println("start node is starting pt, first pick node: " + finalAns.get(1) + " - first inter. route: " + routeFromStartNodeToFirstPickNode);
                for(int p = 1; p< finalAns.size()-2; p++){
                    
                        String node = finalAns.get(p);
                        String nxtNode = finalAns.get(p+1);
                        Double nodeXCoord = Double.parseDouble(node.split(",")[0]);
                        Double nxtNodeXCoord = Double.parseDouble(nxtNode.split(",")[0]);
                         //System.out.println(node + " node ");
                         //System.out.println(nxtNode + " nxt node ");
                    if (Math.abs(nodeXCoord - nxtNodeXCoord) > 1) {
                        String intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(node +"to" +nxtNode);
                        
                        if(intermediateRouteWithCornerNode == null){
                             intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(nxtNode +"to" + node);
                             //System.out.println("before flip: " + intermediateRouteWithCornerNode);
                             String[] intermediateRouteWithCornerNodeArr= intermediateRouteWithCornerNode.split("-");
                             //System.out.println("intermediate route split arr: " + intermediateRouteWithCornerNodeArr);

                             String routeIWant = "";
                             for(int z = intermediateRouteWithCornerNodeArr.length-1; z >-1; z--){
                                 routeIWant += intermediateRouteWithCornerNodeArr[z];
                                 routeIWant+="-";


                             }
                            routeIWant= routeIWant.substring(0,routeIWant.length()-1);
                             intermediateRouteWithCornerNode = routeIWant;



                        }

                           ;
                            // cut off the second pick node
                            //System.out.println(intermediateRouteWithCornerNode + " intermediateRouteWithCornerNode ");
                            int indexOfDash = intermediateRouteWithCornerNode.lastIndexOf("-");
                            intermediateRouteWithCornerNode = intermediateRouteWithCornerNode.substring(0,indexOfDash+1);

                           // System.out.println(intermediateRouteWithCornerNode + " intermediateRouteWithCornerNode Aftercut");
                            finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                          //  System.out.println(finalAnsWithCornerNodes + " finalAnsWithCornerNodes");
                            modifiedRouteStr += node;
                            if(p!= finalAns.size()-1){
                                modifiedRouteStr +="-";
                            }
                    } else {
                        finalAnsWithCornerNodes += node + "-";
                    }
                }
                String node = finalAns.get(finalAns.size()-2);
                String nxtNode = finalAns.get(finalAns.size()-1);
                String intermediateRouteWithCornerNode = node + "-" + nxtNode;
                finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;





            }else{

                String startNodeOfRoute2 = finalAns.get(0);
                String firstPickNodeOfRoute2 = finalAns.get(1);
                
                String xCoordOfFirstPickNode =firstPickNodeOfRoute2.split(",")[0];
                String cornerNodeBetweenStartNodeAndFirstPickNode = xCoordOfFirstPickNode + ",1.0,0.0";
                String firstIntermediateRoute = startNodeOfRoute2 + "-" + cornerNodeBetweenStartNodeAndFirstPickNode + "-";
                //System.out.println("start node 2: " + startNodeOfRoute2 +" - first pick node 2: " + firstPickNodeOfRoute2 + " - first inter. route: " + firstIntermediateRoute);
                
                finalAnsWithCornerNodes +=firstIntermediateRoute;
                
                for(int p = 1; p< finalAns.size()-2; p++){
                    String node = finalAns.get(p);
                    String nxtNode = finalAns.get(p+1);
                    Double nodeXCoord = Double.parseDouble(node.split(",")[0]);
                    Double nxtNodeXCoord = Double.parseDouble(nxtNode.split(",")[0]);

                    if (Math.abs(nodeXCoord - nxtNodeXCoord) > 1) {
                        String intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(node +"to" +nxtNode);
                        //System.out.println("key is: " + node +"to" + nxtNode);
                        
                        if(intermediateRouteWithCornerNode == null){
                            //System.out.println("key is: " + nxtNode +"to" + node);
                            intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(nxtNode +"to" + node);
                            String[] intermediateRouteWithCornerNodeArr= intermediateRouteWithCornerNode.split("-");
                            String routeIWant = "";
                            for(int z = intermediateRouteWithCornerNodeArr.length-1; z >=0; z--){
                                routeIWant += intermediateRouteWithCornerNodeArr[z];
                                routeIWant+="-";


                            }
                           routeIWant= routeIWant.substring(0,routeIWant.length()-1);
                            intermediateRouteWithCornerNode = routeIWant;



                       }
                        // cut off the second pick node
                        int indexOfDash = intermediateRouteWithCornerNode.lastIndexOf("-");
                        intermediateRouteWithCornerNode = intermediateRouteWithCornerNode.substring(0,indexOfDash+1);
                        finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                        modifiedRouteStr += node;
                        if(p!= finalAns.size()-1){
                            modifiedRouteStr +="-";
                        }
                    } else {
                        finalAnsWithCornerNodes += node + "-";
                    }
                        
                }

                String node = finalAns.get(finalAns.size()-2);
                String nxtNode = finalAns.get(finalAns.size()-1);
                String intermediateRouteWithCornerNode = node + "-" + nxtNode;
                finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;


                //modifiedRoutes.put(modifiedRouteStr,finalRouteTotalDist);
            }
            //System.out.println("this route w corner nodes iz: " + finalAnsWithCornerNodes);
            modifiedRoutes.put(finalAnsWithCornerNodes,finalRouteTotalDist);
        }
        return modifiedRoutes;
    }
    
    public TreeMap<String, Double> convertXYZCoordToOriginalLocation (ArrayList<String> pickingList, TreeMap<String, Double> modifiedRoutes, String csvFile, String startingPoint, String startingPtInXYCoordinates){
        TreeMap<String, Double> routeInOriginalLocationMap = new TreeMap<String, Double>(); // key is route, value is the distance for the route
        String[] startingPointSplit = startingPoint.split(",");
        startingPoint = startingPointSplit[0] + ",*," + startingPointSplit[1]; 
        
        Iterator iter = modifiedRoutes.keySet().iterator();
        CSVReader reader = new CSVReader();
        HashMap<String, String> locationMap = reader.retrieveOriginalLocationOfPickItem(csvFile);
        String[] startingPtInXYCoordinatesSplit = startingPtInXYCoordinates.split(",");
        startingPtInXYCoordinates = startingPtInXYCoordinatesSplit[0] + ".0," + startingPtInXYCoordinatesSplit[1] + ".0,0.0";
                
        while(iter.hasNext()){
            String thisRoute = (String) iter.next();
            
            double distanceOfThisRoute = modifiedRoutes.get(thisRoute);
            String[] thisRouteArr = thisRoute.split("-");
            String routeInOriginalLocation = "";     
            
            for(String node: thisRouteArr){
                if (node.equals(startingPtInXYCoordinates)) {
                    routeInOriginalLocation +=startingPoint + "-";
                } else {
                    String thisNodeOriginalLocation = locationMap.get(node);
                    if(thisNodeOriginalLocation!= null){
                        //means that this node is a pick node
                         routeInOriginalLocation +=thisNodeOriginalLocation + "-";

                    }else{
                        // means that this node is a corner node
                        String[] nodeArr = node.split(",");
                        String nodeYCoord = nodeArr[1]; // 


                        Double oldNodeXCoord = Double.parseDouble(nodeArr[0]); //our X coordinate 
                        Integer oldNodeXCoordInInt = oldNodeXCoord.intValue();
                        //System.out.println("our x coord: " + oldNodeXCoord);

                        Integer newNodeXCoord = -1; //X coordinate in their system

                        if (oldNodeXCoord % 2 == 0) { //if our X coordinate is even
                            newNodeXCoord = (oldNodeXCoordInInt + 6) / 2;
                        } else { //if our X coordinate is odd
                            newNodeXCoord = (oldNodeXCoordInInt + 7) / 2;
                        }

                        String nodeZCoord = "*";

                        if(nodeYCoord.equals("1.0")){
                            nodeYCoord = "06"; // if y coord = 1, must change it to 06


                        }else if(nodeYCoord.equals("77.0")){                       
                            // converting the last corner node at y coordinate = 77
                            nodeYCoord = "57.0";


                        }else if(nodeYCoord.equals("35.0")){                       
                            // converting the last corner node at y coordinate = 77
                            nodeYCoord = "29";


                        }else if(nodeYCoord.equals("37.0")){                       
                            // converting the last corner node at y coordinate = 77
                            nodeYCoord = "30";
                        } else {
                            
                            Double nodeYCoordInDouble = Double.parseDouble(nodeYCoord);
                            Double nodeYCoordOriginalCoord = (nodeYCoordInDouble * 2 + 19)/3;
                            System.out.println("node Y coord: " + nodeYCoord + "original coord: " + nodeYCoordOriginalCoord);
                            if (nodeYCoordOriginalCoord % 1 != 0.0) {
                                nodeYCoordOriginalCoord = (nodeYCoordInDouble * 2 + 17)/3;
                            }
                            
                            nodeYCoord = nodeYCoordOriginalCoord.intValue() + "";
                        }
                        
                        
                        String cornerNodeToDisplay = "";
                        
                        if (newNodeXCoord.toString().length() == 1) {
                            cornerNodeToDisplay = "0" + newNodeXCoord +"," + nodeZCoord + ","+ nodeYCoord; // this is trying to convert it to the original location style
                        } else {
                            cornerNodeToDisplay = newNodeXCoord +"," + nodeZCoord + ","+ nodeYCoord; // this is trying to convert it to the original location style
                        }
                        

                        routeInOriginalLocation +=cornerNodeToDisplay + "-";

                    }
                
                }
            
            }
            
            routeInOriginalLocation = routeInOriginalLocation.substring(0, routeInOriginalLocation.length()-1);
            routeInOriginalLocationMap.put(routeInOriginalLocation,distanceOfThisRoute);
            
    
    
        }
    
        
        
        
        
        return routeInOriginalLocationMap;
    
    }
    
    
}
