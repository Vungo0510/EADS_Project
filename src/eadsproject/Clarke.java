/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

import java.util.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

/**
 *
 * @author Snow Petrel
 */
public class Clarke {

    //private String startingPoint = "1,3"; //Store x,y coordinates of the starting point of the vehicle "x,y". To be passed in from UI
    private CSVReader csvReader = new CSVReader();    
    private TreeMap<Double, ArrayList<Double>> cornerNodesMap;
    private double distOfOneUnitOfYCoordInMeters = 0.9;
    private double distOfOneUnitOfXCoordInMeters = 1.425;
    private DecimalFormat df = new DecimalFormat("#.###");
    private double mheTravelTime; //number of seconds it takes for MHE to travel 1 meter
    private double mheLiftingTime; //number of seconds it takes for MHE to lift/lower 1 meter vertically
    
    //Step 1
    public ArrayList<HashMap> getInitialSolution(ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime) {
        //Declare rounding mode to round up if decimal unit is 5 or more. Round down otherwise
        df.setRoundingMode(RoundingMode.HALF_UP);
        // this method calculates the time from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        
        HashMap<String, Double> timeFromStartPtToAllPt = new HashMap<String, Double>(); //key stores x & y coordinates of pick nodes, values time from start node to this pick node
        HashMap<String, String> routeOfStartPtToAllPt = new HashMap<String, String>(); //// key is x,y, z coord of pick item, value is the route of start pt to that pick item
        ArrayList<HashMap> initialSolution = new ArrayList<HashMap>();
        
        double xCoordinateOfStartPt = Double.parseDouble(startingPoint.split(",")[0]);
        double yCoordinateOfStartPt = Double.parseDouble(startingPoint.split(",")[1]);
        double zCoordinateOfStartPt = 0.0;
        startingPoint = xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt;
        
        //ArrayList of y coordinates corner nodes that have the same x as start pt
        ArrayList<Double> yCoordinatesOfCornerNodesNearStartPt = cornerNodesMap.get(xCoordinateOfStartPt);
        Double yCoordNearestCornerToStartPt = -1.0;
        double currDiff = Double.MAX_VALUE;
        
        //Iterate through the ArrayList above and find the y coordinate that is nearest to start pt's y coordinate
        for (Double yCoordCornerPt : yCoordinatesOfCornerNodesNearStartPt) {
            if (Math.abs(yCoordCornerPt - yCoordinateOfStartPt) < currDiff) {
                yCoordNearestCornerToStartPt = yCoordCornerPt;
                currDiff = Math.abs(yCoordCornerPt - yCoordinateOfStartPt);
            }
        }
        
        //Assume that there are corner nodes at the same y coordinate of corner node nearest to start point for all x coordinates of pick items
        /*Example:
            Start point is (2,3) => Nearest corner node to start point is (2, 1)
            => Assume that corner nodes (8, 1), (16, 1), (17, 1), (20,1) exist. (The items that correspond to each corner nodes are (8,17), (16,56), (17,47) and (20,58)
        */
        for (String s : pickingList) {
            
            double xCoordinateOfPickItem = Double.parseDouble(s.split(",")[1]);
            double yCoordinateOfPickItem = Double.parseDouble(s.split(",")[2]);
            double zCoordinateOfPickItem = Double.parseDouble(s.split(",")[3]);
            
            if (!startingPoint.equals(xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem)) {
                double timeFromStartPtToPickItem = -1.0;

                //if pick item and start point have the same X coordinate, just go straight to pick item
                if (Math.abs(xCoordinateOfPickItem - xCoordinateOfStartPt) == 0.0) {

                    //time here is just the absolute difference of y coordinates between start node and pick node
                    timeFromStartPtToPickItem = (Math.abs(yCoordinateOfStartPt - yCoordinateOfPickItem)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + zCoordinateOfPickItem * mheLiftingTime;

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem);
                } 
                //if pick item and start point are on 2 adjacent X coordinates, turn left/right (hence the +1 in time) and go straight to pick item 
                else if (Math.abs(xCoordinateOfPickItem - xCoordinateOfStartPt) == 1.0) {

                    //time in this case = time from start node to the node with adjacent X and same Y + time from the adjacent node to pick node 
                    timeFromStartPtToPickItem = (Math.abs(yCoordinateOfStartPt - yCoordinateOfPickItem)*distOfOneUnitOfYCoordInMeters + distOfOneUnitOfXCoordInMeters) * mheTravelTime + zCoordinateOfPickItem * mheLiftingTime;

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem);
                } else {
                    //time from start to pick item = time from start to nearest corner node A with same x coordinate + time from corner node A to corner node B with same y coordinate as A and x coordinate of pick item + time from corner node B to pick item  
                    timeFromStartPtToPickItem = (Math.abs(yCoordinateOfStartPt - yCoordNearestCornerToStartPt)*distOfOneUnitOfYCoordInMeters + Math.abs(xCoordinateOfStartPt - xCoordinateOfPickItem)*distOfOneUnitOfXCoordInMeters + Math.abs(yCoordNearestCornerToStartPt - yCoordinateOfPickItem)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + zCoordinateOfPickItem * mheLiftingTime;

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem  + "," + zCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfStartPt + "," + yCoordNearestCornerToStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordNearestCornerToStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem);
                }

                //key is x-y coordinate of the pick node, value is time from start node to pick node
                timeFromStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem  + "," + zCoordinateOfPickItem, timeFromStartPtToPickItem);//Double.parseDouble(df.format(timeFromStartPtToPickItem)));
                
                /*if (xCoordinateOfPickItem == 9.0 && yCoordinateOfPickItem == 26.0) {
                    System.out.println("ROUTE: " + xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfStartPt + "," + yCoordNearestCornerToStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordNearestCornerToStartPt + "," + zCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem + "," + zCoordinateOfPickItem);
                    System.out.println("Y coords: " + yCoordinateOfStartPt + "---" + yCoordNearestCornerToStartPt + "---" + yCoordinateOfPickItem);
                    System.out.println("Dist from node 1 to node 2: " + Math.abs(yCoordinateOfStartPt - yCoordNearestCornerToStartPt)*timeOfOneUnitOfYCoordInMeters);
                    System.out.println("Dist from node 2 to node 3: " + Math.abs(xCoordinateOfStartPt - xCoordinateOfPickItem)*timeOfOneUnitOfXCoordInMeters);
                    System.out.println("Dist from node 3 to node 4: " + Math.abs(yCoordNearestCornerToStartPt - yCoordinateOfPickItem)*timeOfOneUnitOfYCoordInMeters);
                    
                }*/
            }
        }
        
        initialSolution.add(timeFromStartPtToAllPt);
        initialSolution.add(routeOfStartPtToAllPt);
        return initialSolution;
    }
    
    //Step 2
    public ArrayList<HashMap> getPointToPointTime(ArrayList<String> pickingList, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime) {
        //Declare rounding mode to round up if decimal unit is 5 or more. Round down otherwise
        df.setRoundingMode(RoundingMode.HALF_UP);
        // this method calculates the time from each pick node to all pick nodes, pickingList passed in contains all the pick nodes
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        
        HashMap<String, Double> timeAmongPickItems = new HashMap<String, Double>(); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is time
        HashMap<String, String> routeFromCurrPickNodeToOtherPickNode = new HashMap<String, String>();
        ArrayList<HashMap> ptToPtRouteAndTimeArr = new ArrayList<HashMap>();
        
        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            double xCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[1]);
            double yCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[2]);
            double zCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[3]);
            
            //ArrayList of y coordinates corner nodes that have the same x as current pick node
            ArrayList<Double> yCoordinatesOfCornerNodesNearCurrPickNode = cornerNodesMap.get(xCoordinateOfCurrentPickNode);
            Double yCoordNearestCornerToCurrPickNode = -1.0;
            double currDiff = Double.MAX_VALUE;
            
            //Iterate through the ArrayList above and find the y coordinate that is nearest to current pick node's y coordinate
            for (Double yCoordCornerPt : yCoordinatesOfCornerNodesNearCurrPickNode) {
                if (Math.abs(yCoordCornerPt - yCoordinateOfCurrentPickNode) < currDiff) {
                    yCoordNearestCornerToCurrPickNode = yCoordCornerPt;
                    currDiff = Math.abs(yCoordCornerPt - yCoordinateOfCurrentPickNode);
                }
            }

            //Assume that there are corner nodes at the same y coordinate of corner node nearest to start point for all x coordinates of pick items
            /*Example:
                Start point is (2,3) => Nearest corner node to start point is (2, 1)
                => Assume that corner nodes (8, 1), (16, 1), (17, 1), (20,1) exist. (The items that correspond to each corner nodes are (8,17), (16,56), (17,47) and (20,58)
            */
            for (int j = i + 1; j < pickingList.size(); j++) {
                String otherPickNode = pickingList.get(j);
                
                double xCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[1]);
                double yCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[2]);
                double zCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[3]);
                
                String otherPickNodeXYZCoord = xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode;
                String thisPickNodeXYZCoord = xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode;
                
                if (!otherPickNodeXYZCoord.equals(thisPickNodeXYZCoord)) {    
                    double timeFromCurrentPickNodeToOtherPickNode = -1.0;

                    //if current pick node and other pick node have the same X AND Y coordinate, items are at same location but different height => time is the height
                    if (Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) == 0.0 && Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode) == 0.0) {
                        //time here is just the absolute difference of z coordinates between current pick node and other pick node
                        timeFromCurrentPickNodeToOtherPickNode = Math.abs(zCoordinateOfOtherPickNode - zCoordinateOfCurrentPickNode) * mheLiftingTime;

                        //save the full route in case we need to retrieve later. Key is in format: x-y-z coordinate of current pick node + "to" + x-y coordinate of other pick node, value is the full route from current pick node to other pick node
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode);
                    
                    //if current pick node and other pick node have the same X coordinate, just go straight to other pick node
                    } else if (Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) == 0.0) {
                        
                        //time here is just the absolute difference of y coordinates between current pick node and other pick node and the sum of 2 z coordinates of 2 pick items (because we need to go down from z coordinate of item 1 to ground level, travel to item 2, then go up from ground level to z coordinate of item 2
                        timeFromCurrentPickNodeToOtherPickNode = (Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + (zCoordinateOfOtherPickNode + zCoordinateOfCurrentPickNode) * mheLiftingTime;
                        
                        //if (yCoordinateOfCurrentPickNode == 58.0 || yCoordinateOfOtherPickNode == 59.0 && xCoordinateOfOtherPickNode == 20.0) {
                        //    System.out.println("TEST: " + (Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode)*distOfOneUnitOfYCoordInMeters + zCoordinateOfOtherPickNode + zCoordinateOfCurrentPickNode));
                        //}
                        //save the full route in case we need to retrieve later. Key is in format: x-y-z coordinate of current pick node + "to" + x-y coordinate of other pick node, value is the full route from current pick node to other pick node
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode);
                    } 
                    //if pick item and start point are on 2 adjacent X coordinates, turn left/right (hence the +1 unit of X in time) and go straight to pick item 
                    else if (Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) == 1) {

                        //time in this case = time from current pick node to the node with adjacent X and same Y + time from that node to other pick node + the sum of 2 z coordinates of 2 pick items (because we need to go down from z coordinate of item 1 to ground level, travel to item 2, then go up from ground level to z coordinate of item 2
                        timeFromCurrentPickNodeToOtherPickNode = (Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode)*distOfOneUnitOfYCoordInMeters + distOfOneUnitOfXCoordInMeters) * mheTravelTime + (zCoordinateOfOtherPickNode + zCoordinateOfCurrentPickNode) * mheLiftingTime;

                        //save the full route in case we need to retrieve later. Key is x-y-z coordinate of the pick node, value is the full route from start node to pick node. Add in height = 0 for corner nodes
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfCurrentPickNode + "," + "0" + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode);
                    } else {
                        //time from current pick node to other pick node = time from current pick node to nearest corner node A with same x coordinate as current pick node + time from corner node A to corner node B with same y coordinate as A and x coordinate of other pick item + time from corner node B to other pick node + the z coordinates (height) of 2 pick items
                        timeFromCurrentPickNodeToOtherPickNode = (Math.abs(yCoordinateOfOtherPickNode - yCoordNearestCornerToCurrPickNode)*distOfOneUnitOfYCoordInMeters + Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode)*distOfOneUnitOfXCoordInMeters + Math.abs(yCoordNearestCornerToCurrPickNode - yCoordinateOfCurrentPickNode)*distOfOneUnitOfYCoordInMeters) * mheTravelTime +  (zCoordinateOfOtherPickNode + zCoordinateOfCurrentPickNode) * mheLiftingTime;

                        //save the full route in case we need to retrieve later. Key is x-y-z coordinate of the pick node, value is the full route from start node to pick node. Add in height = 0 for corner nodes
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "-" + xCoordinateOfCurrentPickNode + "," + yCoordNearestCornerToCurrPickNode + ",0.0-" + xCoordinateOfOtherPickNode + "," + yCoordNearestCornerToCurrPickNode + ",0.0-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode);
                    }

                    timeAmongPickItems.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode, timeFromCurrentPickNodeToOtherPickNode);//Double.parseDouble(df.format(timeFromCurrentPickNodeToOtherPickNode)));   

                }
                
            }
        }
        
        ptToPtRouteAndTimeArr.add(timeAmongPickItems);
        ptToPtRouteAndTimeArr.add(routeFromCurrPickNodeToOtherPickNode);
        return ptToPtRouteAndTimeArr;
    }
    
    //Step 3
    public HashMap getSavingsMap(ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime) {
        HashMap<String, Double> savingsMap = new HashMap<String, Double>();
        
        ArrayList<HashMap> intialSolution = getInitialSolution(pickingList, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        HashMap<String, Double> timeOfStartPtToAllPt = intialSolution.get(0);
        
        ArrayList<HashMap> ptToPtRouteAndTimeArr = getPointToPointTime(pickingList, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        HashMap<String, Double> timeAmongPickItems = ptToPtRouteAndTimeArr.get(0);

        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            double xCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[1]);
            double yCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[2]);
            double zCoordinateOfCurrentPickNode = Double.parseDouble(currentPickNode.split(",")[3]);

            for (int j = i + 1; j < pickingList.size(); j++) {
                String otherPickNode = pickingList.get(j);

                double xCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[1]);
                double yCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[2]);
                double zCoordinateOfOtherPickNode = Double.parseDouble(otherPickNode.split(",")[3]);
                
                String otherPickNodeXYZCoord = xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode + "," + zCoordinateOfOtherPickNode;
                String thisPickNodeXYZCoord = xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "," + zCoordinateOfCurrentPickNode;
                
                //System.out.println("this node coords: " + thisPickNodeXYZCoord);
                //System.out.println("other node coords: " + otherPickNodeXYZCoord);
                
                if (!otherPickNodeXYZCoord.equals(thisPickNodeXYZCoord)) {  
                    double timeFromStartPtToNodei = timeOfStartPtToAllPt.get(thisPickNodeXYZCoord); //c0i
                    double timeFromStartPtToNodej = timeOfStartPtToAllPt.get(otherPickNodeXYZCoord); //c0j

                    double timeFromNodeiToNodej = timeAmongPickItems.get(thisPickNodeXYZCoord + "to" + otherPickNodeXYZCoord);

                    double savingsForEdgeij = timeFromStartPtToNodei + timeFromStartPtToNodej - timeFromNodeiToNodej;

                    savingsMap.put(thisPickNodeXYZCoord + "to" + otherPickNodeXYZCoord, savingsForEdgeij);
                }
            }
        }
        
        List<String> mapKeys = new ArrayList<>(savingsMap.keySet());
        List<Double> mapValues = new ArrayList<>(savingsMap.values());
        
        //sort savings map descendingly
        Collections.sort(mapValues, new Comparator<Double>() {
                @Override
                public int compare(Double e1, Double e2) {
                    return e2.intValue() - e1.intValue();
                }
            });
        
        LinkedHashMap<String, Double> sortedSavingsMap = new LinkedHashMap<>();

        Iterator<Double> valueIterator = mapValues.iterator();
        while (valueIterator.hasNext()) {
            Double value = valueIterator.next();
            Iterator<String> keyIterator = mapKeys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Double originalSaving = savingsMap.get(key);
                Double sortedSaving = value;

                if (originalSaving == sortedSaving) {
                    keyIterator.remove();
                    sortedSavingsMap.put(key, value);
                    break;
                }
            }
        }
        return sortedSavingsMap;
    }
    
    //Step 4
    public HashMap<String, String> getSolution(HashMap<String, Double> pickItemCapacityMap, HashMap<String, Double> savingsMap, Double mheCapacity, String startingPoint) {
        HashMap<String, String> solutionMap = new HashMap<>(); 
        HashMap<String, Double> capacityMap = new HashMap<>();
        
        double totalCapacityOfThisRoute = 0.0;
        Set solutionMapKeySet = savingsMap.keySet();
        
        Iterator<String> keySetIter = solutionMapKeySet.iterator();
        
        while (keySetIter.hasNext()) { //iterate through all records in savingsMap
            String thisKey = keySetIter.next();
            String[] thisKeyArr = thisKey.split("to");
            
            String thisItem = thisKeyArr[0]; //this contains x-y coordinate of the first pick item (before "to") from savings map
            String anotherItem = thisKeyArr[1]; //this contains x-y coordinate of the second pick item (after "to") from savings map
            
            //System.out.println("this item: " + thisItem);
            
            //if capacity of thisItem is 0, make it 1 as we're using at least 1 carton. Else keep it as it is
            double thisItemCapacity = pickItemCapacityMap.get(thisItem) > 0 ? pickItemCapacityMap.get(thisItem) : 1;
            
            //System.out.println("another item: " + anotherItem);
            //if capacity of anotherItem is 0, make it 1 as we're using at least 1 carton. Else keep it as it is
            double anotherItemCapacity = pickItemCapacityMap.get(anotherItem) > 0 ? pickItemCapacityMap.get(anotherItem) : 1;
            
            //If both pick items are not found in solutionMap
            if (solutionMap.get(thisItem) == null && solutionMap.get(anotherItem) == null) {
                totalCapacityOfThisRoute = 0.0;
                
                if (thisItemCapacity <= mheCapacity) {
                    totalCapacityOfThisRoute += thisItemCapacity;  
                    solutionMap.put(thisItem, thisItem);
                    capacityMap.put(thisItem, totalCapacityOfThisRoute);
                }
                
                if (totalCapacityOfThisRoute + anotherItemCapacity <= mheCapacity) {
                    totalCapacityOfThisRoute += anotherItemCapacity;
                    
                    solutionMap.put(thisItem, thisItem + "-" + anotherItem);
                    solutionMap.put(anotherItem, thisItem + "-" + anotherItem);
                    
                    capacityMap.put(thisItem, totalCapacityOfThisRoute);
                    capacityMap.put(anotherItem, totalCapacityOfThisRoute);
                
                } else if (anotherItemCapacity <= mheCapacity) { //if anotherItem cannot be in the same route with thisItem
                    solutionMap.put(anotherItem, anotherItem);
                    capacityMap.put(anotherItem, anotherItemCapacity);
                }
                           
            } else if (solutionMap.get(thisItem) == null) { //if anotherItem is found in solutionMap while thisItem isn't
                String currentRoute = solutionMap.get(anotherItem);
                Double currentRouteCapacity = capacityMap.get(anotherItem);
                
                String[] currentRouteSplit = currentRoute.split("-");
                String newRoute = currentRoute;
                        
                if (currentRouteSplit[0].equals(anotherItem)) {
                    newRoute = thisItem + "-" + currentRoute;        
                } else if (currentRouteSplit[currentRouteSplit.length - 1].equals(anotherItem)) {
                    newRoute = currentRoute + "-" + thisItem;       
                } 
                
                if (!newRoute.equals(currentRoute) && currentRouteCapacity + thisItemCapacity <= mheCapacity) { //if anotherItem is either the first or last node AND new capacity after adding in thisItem is not exceeded
                    currentRouteCapacity += thisItemCapacity;  
                    for (int i = 0; i < currentRouteSplit.length; i++) {
                        solutionMap.put(currentRouteSplit[i], newRoute);
                        capacityMap.put(currentRouteSplit[i], currentRouteCapacity);
                    }
                    
                    solutionMap.put(thisItem, newRoute);
                    capacityMap.put(thisItem, currentRouteCapacity);
                    
                } else if (thisItemCapacity <= mheCapacity) { //if anotherItem's position in the route isn't the first or last node OR if adding thisItem in anotherItem's route will exceed capacity, we have to create a new route for thisItem
                    solutionMap.put(thisItem, thisItem);
                    capacityMap.put(thisItem,thisItemCapacity);  
                }
   
            } else if (solutionMap.get(anotherItem) == null) { //if only thisItem is found in solutionMap
                
                String currentRoute = solutionMap.get(thisItem);
                Double currentRouteCapacity = capacityMap.get(thisItem);
                
                String[] currentRouteSplit = currentRoute.split("-");
                String newRoute = currentRoute;
                
                if (currentRouteSplit[0].equals(thisItem)) {
                    newRoute = anotherItem + "-" + currentRoute;        
                } else if (currentRouteSplit[currentRouteSplit.length - 1].equals(thisItem)) {
                    newRoute = currentRoute + "-" + anotherItem;       
                } 
                
                if (!newRoute.equals(currentRoute) && currentRouteCapacity + anotherItemCapacity <= mheCapacity) { //if thisItem is either the first or last node AND new capacity after adding in anotherItem is not exceeded
                    currentRouteCapacity += anotherItemCapacity;  
                    for (int i = 0; i < currentRouteSplit.length; i++) {
                        solutionMap.put(currentRouteSplit[i], newRoute);
                        capacityMap.put(currentRouteSplit[i], currentRouteCapacity);
                    }
                    
                    solutionMap.put(anotherItem, newRoute);
                    capacityMap.put(anotherItem, currentRouteCapacity);
                    
                } else if (anotherItemCapacity <= mheCapacity) { //if thisItem's position in the route isn't the first or last node OR if adding anotherItem in thisItem's route will exceed capacity, we have to create a new route for anotherItem
                    solutionMap.put(anotherItem, anotherItem);
                    capacityMap.put(anotherItem,anotherItemCapacity);  
                }
            } else { //if both thisItem and anotherItem are currently assigned to a route
                String thisRoute = solutionMap.get(thisItem);
                Double thisRouteCapacity = capacityMap.get(thisItem);
                
                String anotherRoute = solutionMap.get(anotherItem);
                Double anotherRouteCapacity = capacityMap.get(anotherItem);
                
                String[] thisRouteSplit = thisRoute.split("-");
                String[] anotherRouteSplit = anotherRoute.split("-");
                
                String newRoute = "";
                Double combinedCapacity = thisRouteCapacity + anotherRouteCapacity;
                
                //System.out.println(thisRoute);
                //System.out.println(anotherRoute);
                //System.out.println(thisItem + "----" + anotherItem);
                //if thisItem and anotherItem belong to diff routes & if capacity of combined route doesn't exceed capacity limit
                if (!thisRoute.equals(anotherRoute) && combinedCapacity <= mheCapacity) {
                    
                    //if thisItem and anotherItem both appear at either the first or last position of their routes: we have to flip the shorter route before merging
                    if ((thisRouteSplit[0].equals(thisItem) && anotherRouteSplit[0].equals(anotherItem)) || (thisRouteSplit[thisRouteSplit.length - 1].equals(thisItem) && anotherRouteSplit[anotherRouteSplit.length - 1].equals(anotherItem))) {
                        
                        if (thisRouteSplit.length == 1) {
                            newRoute = thisRoute + "-" + anotherRoute;
                        } else if (anotherRouteSplit.length == 1) {
                            newRoute = anotherRoute + "-" + thisRoute;
                        } else if (thisRouteSplit.length > anotherRouteSplit.length) { //if thisRoute is longer than anotherRoute
                            
                            if (thisRouteSplit[0].equals(thisItem)) { //if thisItem and anotherItem are both first nodes in their routes, new route = flipped route + this route
                                String flippedAnotherRoute = "";
                            
                                for (int i = anotherRouteSplit.length -1; i >= 0; i--) {
                                    flippedAnotherRoute += anotherRouteSplit[i] + "-";
                                }
                                
                                
                                newRoute = flippedAnotherRoute + thisRoute;
                                //System.out.println("new route 426: " + newRoute);
                                
                            } else { //if thisItem and anotherItem are both last nodes in their routes, new route = this route + flipped route 
                                newRoute = thisRoute;
                                
                                String flippedAnotherRoute = "";
                            
                                for (int i = anotherRouteSplit.length -1; i >= 0; i--) {
                                    flippedAnotherRoute += anotherRouteSplit[i] + "-";
                                }
                                flippedAnotherRoute = flippedAnotherRoute.substring(0, flippedAnotherRoute.length() - 1);
                                newRoute += "-" + flippedAnotherRoute;
                                //System.out.println("new route 438: " + thisItem + "//" + thisRoute + "---" + anotherItem + "//" + anotherRoute + ": " + newRoute);
                            }
                            
                        } else { //if thisRoute is shorter than or as long as anotherRoute
                            
                            if (thisRouteSplit[0].equals(thisItem)) { //if thisItem and anotherItem are both first nodes in their routes, new route = flipped route + this route
                                String flippedThisRoute = "";
                            
                                for (int i = thisRouteSplit.length -1; i >= 0; i--) {
                                    flippedThisRoute += thisRouteSplit[i] + "-";
                                }

                                newRoute = flippedThisRoute + anotherRoute;
                                //System.out.println("new route 451: " + newRoute);
                                
                            } else { //if thisItem and anotherItem are both last nodes in their routes, new route = this route + flipped route 
                                newRoute = anotherRoute;
                                
                                String flippedThisRoute = "";
                            
                                for (int i = thisRouteSplit.length -1; i >= 0; i--) {
                                    flippedThisRoute += thisRouteSplit[i] + "-";
                                }
                                flippedThisRoute = flippedThisRoute.substring(0, flippedThisRoute.length() - 1);
                                newRoute += "-" + flippedThisRoute;
                                //System.out.println("new route 467: " + thisItem + "//" + thisRoute + "---" + anotherItem + "//" + anotherRoute + ": " + newRoute);
                            
                            }
                        }
                    } 
                    //if thisItem and anotherItem appear at different ends of their own routes, just need to merge the 2 routes without flipping
                    else if ((thisRouteSplit[0].equals(thisItem) && anotherRouteSplit[anotherRouteSplit.length - 1].equals(anotherItem)) || (thisRouteSplit[thisRouteSplit.length - 1].equals(thisItem) && anotherRouteSplit[0].equals(anotherItem))) {
                        //System.out.println("different ends: " + "this item: " + thisItem + " -- this route: " + thisRoute + "/// another item: " + anotherItem + "--- another route: " + anotherRoute);
                        if (thisRouteSplit[0].equals(thisItem)) { //if thisItem appears at the start of its route and anotherItem appears at the end, we have new route = another route + this route
                            newRoute = anotherRoute + "-" + thisRoute;
                        } else {
                            newRoute = thisRoute + "-" + anotherRoute;
                        }  
                    }
                }
                
                if (!newRoute.equals("")) { //if there was a route merge
                    for (String nodeInThisRoute : thisRouteSplit) {
                        solutionMap.put(nodeInThisRoute, newRoute);
                        capacityMap.put(nodeInThisRoute, combinedCapacity);
                    }
                    
                    for (String nodeInAnotherRoute : anotherRouteSplit) {
                        solutionMap.put(nodeInAnotherRoute, newRoute);
                        capacityMap.put(nodeInAnotherRoute, combinedCapacity);
                    }
                }
            }
        }
        
        Set updatedSolutionMapKeySet = solutionMap.keySet();
        Iterator<String> secondKeySetIter = updatedSolutionMapKeySet.iterator();
        
        while (secondKeySetIter.hasNext()) { //iterate through all routes in solutionMap and add in the end node. End node has the same X coordinate as the last pick node and Y coordinate = 1
            String thisKey = secondKeySetIter.next();
            String thisRoute = solutionMap.get(thisKey);
            String[] thisRouteArr = thisRoute.split("-");
            String lastPickItem = thisRouteArr[thisRouteArr.length - 1];
            String lastPickItemXCoordinate = lastPickItem.split(",")[0];
            
            solutionMap.put(thisKey, thisRoute + "-" + lastPickItemXCoordinate + ",1.0,0.0");
        }
        return solutionMap;
    }
    
    //This method retrieves unique routes from solutionMap by iterating through them and compare the HashMap values
    public ArrayList<String> getFinalRoutes (HashMap<String, String> solutionMap, String startingPoint) {
        ArrayList<String> finalRoutes = new ArrayList<String>();
        ArrayList<String> finalRoutesBeforeAddingStartPt = new ArrayList<String>();
        Set solutionMapKeySet = solutionMap.keySet();
        Iterator<String> keySetIter = solutionMapKeySet.iterator();
        boolean isFirstRoute = true;
        String lastNodeOfPrevRoute = "";
        
        double xCoordinateOfStartPt = Double.parseDouble(startingPoint.split(",")[0]);
        double yCoordinateOfStartPt = Double.parseDouble(startingPoint.split(",")[1]);
        double zCoordinateOfStartPt = 0.0;
        startingPoint = xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "," + zCoordinateOfStartPt;
        
        
        while (keySetIter.hasNext()) { //iterate through all routes in solutionMap and add in the end node. End node has the same X coordinate as the last pick node and Y coordinate = 1
            String thisKey = keySetIter.next();
            String thisRoute = solutionMap.get(thisKey);
            String[] thisRouteSplit = thisRoute.split("-");
            
            if(!finalRoutesBeforeAddingStartPt.contains(thisRoute)) {
                finalRoutesBeforeAddingStartPt.add(thisRoute);
                
                if (isFirstRoute) {
                    thisRoute = startingPoint + "-" + thisRoute;
                } else {
                    thisRoute = lastNodeOfPrevRoute + "-" + thisRoute;
                }
                
                finalRoutes.add(thisRoute);
                isFirstRoute = false;
                lastNodeOfPrevRoute = thisRouteSplit[thisRouteSplit.length - 1];
            }  
        }
        return finalRoutes;
    }
    
    public HashMap<String, Double> getTimeOfFinalRoutes (ArrayList<String> pickingList, ArrayList<String> finalRoutes, String startingPoint, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime) {
        HashMap<String, Double> finalRoutesDistHashMap = new HashMap<>();
        ArrayList<HashMap> initialSolution = getInitialSolution(pickingList, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        ArrayList<HashMap> ptToPtTimeArr = getPointToPointTime(pickingList, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        
        HashMap timeFromStartPtToPickItem = initialSolution.get(0);
        HashMap routeFromStartPtToPickItem = initialSolution.get(1);
        
        HashMap timeFromPickItemToPickItem = ptToPtTimeArr.get(0);
        HashMap routeFromPickItemToPickItem = ptToPtTimeArr.get(1);
        
        //System.out.println("route start to pick: " + routeFromStartPtToPickItem);
        //System.out.println("route pick to pick: " + routeFromPickItemToPickItem);
        //boolean isFirstRoute = true;
        String lastNodeOfPrevRoute = "";
        
        for (String finalRoute : finalRoutes) {
            /*if (isFirstRoute) {
                finalRoute = startingPoint + "-" + finalRoute;
            } else {
                finalRoute = lastNodeOfPrevRoute + "-" + finalRoute;
            }
            
            isFirstRoute = false;*/
            String[] finalRouteSplit = finalRoute.split("-");
            Double thisRouteTotalTime = 0.0; 
            
            //System.out.println("this route: " + finalRoute);
            
            if (finalRouteSplit.length >= 3) {
                String startNodeToFirstPickNodeKey = "";
                
                //System.out.println("TRY: " + timeFromPickItemToPickItem);
                boolean calculated = false;
                
                if (lastNodeOfPrevRoute.equals("")) {
                    startNodeToFirstPickNodeKey = finalRouteSplit[1];
                    if (timeFromStartPtToPickItem.get(startNodeToFirstPickNodeKey) != null) {
                        thisRouteTotalTime += (Double) timeFromStartPtToPickItem.get(startNodeToFirstPickNodeKey);
                        //System.out.println("Start: " + routeFromStartPtToPickItem.get(startNodeToFirstPickNodeKey));
                        //System.out.println("start 1 key: " + startNodeToFirstPickNodeKey + ", time: " + timeFromStartPtToPickItem.get(startNodeToFirstPickNodeKey) + " --- total time so far: " + thisRouteTotalTime);
                    }
                    calculated = true;
                } else {
                    
                    //calculate time from start node to one pick node
                    if (timeFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1]) != null) {
                        thisRouteTotalTime += (Double) timeFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1]);
                        //System.out.println("route detail: " + routeFromPickItemToPickItem.get(finalRouteSplit[0] + "to" + finalRouteSplit[1]));
                        //System.out.println("start 2 key: " + finalRouteSplit[0] + "-" + finalRouteSplit[1] + ", time: " + timeFromStartPtToPickItem.get(timeFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1])) + " --- total time so far: " + thisRouteTotalTime);
                        calculated = true;
                    } else if (timeFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0]) != null) {
                        thisRouteTotalTime += (Double) timeFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0]);
                        //System.out.println("route detail: " + routeFromPickItemToPickItem.get(finalRouteSplit[1] + "to" + finalRouteSplit[0]));
                        //System.out.println("start 3 key: " + finalRouteSplit[0] + "-" + finalRouteSplit[1] + ", time: " + timeFromStartPtToPickItem.get(timeFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0])) + " --- total time so far: " + thisRouteTotalTime);
                        calculated = true;
                    }
                    /*if (!calculated) {
                        System.out.println("Got here: " + finalRouteSplit[0] + "-" + finalRouteSplit[1]);
                    }*/
                }
                if(!calculated) {
                    
                    
                    String[] startNodeSplit = finalRouteSplit[0].split(",");
                    String[] firstPickNodeSplit = finalRouteSplit[1].split(",");

                    Double startNodeXCoord = Double.parseDouble(startNodeSplit[0]);
                    Double firstPickNodeXCoord = Double.parseDouble(firstPickNodeSplit[0]);
                    Double startNodeYCoord = Double.parseDouble(startNodeSplit[1]);
                    Double firstPickNodeYCoord = Double.parseDouble(firstPickNodeSplit[1]);
                    Double startNodeZCoord = Double.parseDouble(startNodeSplit[2]);
                    Double firstPickNodeZCoord = Double.parseDouble(firstPickNodeSplit[2]);

                    double distance = (Math.abs(startNodeXCoord - firstPickNodeXCoord)*distOfOneUnitOfXCoordInMeters + Math.abs(startNodeYCoord - firstPickNodeYCoord)*distOfOneUnitOfYCoordInMeters)*mheTravelTime + Math.abs(startNodeZCoord + firstPickNodeZCoord)*mheLiftingTime;
                    //System.out.println("Distance: " + distance);
                    
                    thisRouteTotalTime += distance;
                }
                
                
                      
                
                //calculate time among intermediate nodes
                for (int i = 1; i < finalRouteSplit.length - 2; i++) {
                    String thisNode = finalRouteSplit[i];
                    String nextNode = finalRouteSplit[i+1];
                    //Double xCoordOfThisNode = Double.parseDouble(thisNode.split(",")[0]);
                    //Double xCoordOfNextNode = Double.parseDouble(nextNode.split(",")[0]);
                    String thisPath = thisNode + "to" + nextNode;
                    
                    if (timeFromPickItemToPickItem.get(thisPath) != null) {
                        //System.out.println("this path: " + thisPath);
                        thisRouteTotalTime += (Double) timeFromPickItemToPickItem.get(thisPath);
                        //System.out.println("route key: " + thisPath + ", time: " + timeFromPickItemToPickItem.get(thisPath) + " --- total time so far: " + thisRouteTotalTime);
                    } else {
                        thisPath = nextNode + "to" + thisNode;
                        //System.out.println("this path: " + thisPath);
                        if ( timeFromPickItemToPickItem.get(thisPath) != null) {
                            thisRouteTotalTime += (Double) timeFromPickItemToPickItem.get(thisPath);
                        } else {
                            String[] thisNodeSplit = thisNode.split(",");
                            String[] nextNodeSplit = nextNode.split(",");
                            Double thisNodeX = Double.parseDouble(thisNodeSplit[0]);
                            Double thisNodeY = Double.parseDouble(thisNodeSplit[1]);
                            Double thisNodeZ = Double.parseDouble(thisNodeSplit[2]);
                            
                            Double nextNodeX = Double.parseDouble(nextNodeSplit[0]);
                            Double nextNodeY = Double.parseDouble(nextNodeSplit[1]);
                            Double nextNodeZ = Double.parseDouble(nextNodeSplit[2]);
                            
                            thisRouteTotalTime += ((Math.abs(thisNodeY - nextNodeY)*distOfOneUnitOfYCoordInMeters + Math.abs(thisNodeX - nextNodeX)* distOfOneUnitOfYCoordInMeters) * mheTravelTime + Math.abs(thisNodeZ - nextNodeZ) * mheLiftingTime);
               
                        }
                        //System.out.println("route key: " + thisPath + ", time: " + timeFromPickItemToPickItem.get(thisPath) + " --- total time so far: " + thisRouteTotalTime);
                        
                    }
                    
                }
                
               String lastPickNode = finalRouteSplit[finalRouteSplit.length - 2];
               String lastNode = finalRouteSplit[finalRouteSplit.length - 1];
               Double lastPickNodeY = Double.parseDouble(lastPickNode.split(",")[1]);
               Double lastPickNodeZ = Double.parseDouble(lastPickNode.split(",")[2]);
               Double lastNodeY = Double.parseDouble(lastNode.split(",")[1]);
               Double lastNodeZ = Double.parseDouble(lastNode.split(",")[2]);
               
               lastNodeOfPrevRoute = lastNode;
               
               //System.out.println("time frm last pick node to last node: " + (Math.abs(lastPickNodeY - lastNodeY)*distOfOneUnitOfYCoordInMeters + Math.abs(lastPickNodeZ - lastNodeZ)));
               thisRouteTotalTime += (Math.abs(lastPickNodeY - lastNodeY)*distOfOneUnitOfYCoordInMeters * mheTravelTime + Math.abs(lastPickNodeZ - lastNodeZ) * mheLiftingTime);
               
                //lastNodeOfPrevRoute = lastNode;
               //System.out.println("route " + finalRoute + " total time: " + thisRouteTotalTime); 
               
               finalRoutesDistHashMap.put(finalRoute, thisRouteTotalTime);
            }
        }
        
        return finalRoutesDistHashMap;
    }
}
