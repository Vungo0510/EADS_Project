/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

import java.util.*;

/**
 *
 * @author Snow Petrel
 */
public class Clarke {

    //private String startingPoint = "1,3"; //Store x,y coordinates of the starting point of the vehicle "x,y". To be passed in from UI
    private CSVReader csvReader = new CSVReader();    
    private TreeMap<Integer, ArrayList<Integer>> cornerNodesMap;
    
    //Step 1
    public ArrayList<HashMap> getInitialSolution(ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath) {
        // this method calculates the distance from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        
        HashMap<String, Integer> distOfStartPtToAllPt = new HashMap<String, Integer>(); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node
        HashMap<String, String> routeOfStartPtToAllPt = new HashMap<String, String>();
        ArrayList<HashMap> initialSolution = new ArrayList<HashMap>();
        
        int xCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[0]);
        int yCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[1]);
        
        //ArrayList of y coordinates corner nodes that have the same x as start pt
        ArrayList<Integer> yCoordinatesOfCornerNodesNearStartPt = cornerNodesMap.get(xCoordinateOfStartPt);
        Integer yCoordNearestCornerToStartPt = -1;
        int currDiff = Integer.MAX_VALUE;
        
        //Iterate through the ArrayList above and find the y coordinate that is nearest to start pt's y coordinate
        for (Integer yCoordCornerPt : yCoordinatesOfCornerNodesNearStartPt) {
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
            
            int xCoordinateOfPickItem = Integer.parseInt(s.split(",")[1]);
            int yCoordinateOfPickItem = Integer.parseInt(s.split(",")[2]);
            
            if (!startingPoint.equals(xCoordinateOfPickItem + "," + yCoordinateOfPickItem)) {
                int distFromStartPtToPickItem = -1;

                //if pick item and start point have the same X coordinate, just go straight to pick item
                if (Math.abs(xCoordinateOfPickItem - xCoordinateOfStartPt) == 0) {

                    //distance here is just the absolute difference of y coordinates between start node and pick node
                    distFromStartPtToPickItem = Math.abs(yCoordinateOfStartPt - yCoordinateOfPickItem);

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem);
                } 
                //if pick item and start point are on 2 adjacent X coordinates, turn left/right (hence the +1 in distance) and go straight to pick item 
                else if (Math.abs(xCoordinateOfPickItem - xCoordinateOfStartPt) == 1) {

                    //distance in this case = distance from start node to the node with adjacent X and same Y + distance from that node to pick node 
                    distFromStartPtToPickItem = Math.abs(yCoordinateOfStartPt - yCoordinateOfPickItem) + 1;

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem);
                } else {
                    //dist from start to pick item = dist from start to nearest corner node A with same x coordinate + dist from corner node A to corner node B with same y coordinate as A and x coordinate of pick item + dist from corner node B to pick item  
                    distFromStartPtToPickItem = Math.abs(yCoordinateOfStartPt - yCoordNearestCornerToStartPt) + Math.abs(xCoordinateOfStartPt - xCoordinateOfPickItem) + Math.abs(yCoordNearestCornerToStartPt - yCoordinateOfPickItem);

                    //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                    routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "-" + xCoordinateOfStartPt + "," + yCoordNearestCornerToStartPt + "-" + xCoordinateOfPickItem + "," + yCoordNearestCornerToStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem);
                }

                //key is x-y coordinate of the pick node, value is distance from start node to pick node
                distOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, distFromStartPtToPickItem);
            }
        }
        initialSolution.add(distOfStartPtToAllPt);
        initialSolution.add(routeOfStartPtToAllPt);
        return initialSolution;
    }
    
    //Step 2
    public ArrayList<HashMap> getPointToPointDistance(ArrayList<String> pickingList, String cornerNodeFilePath) {
        // this method calculates the distance from each pick node to all pick nodes, pickingList passed in contains all the pick nodes
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        
        HashMap<String, Integer> distAmongPickItems = new HashMap<String, Integer>(); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance
        HashMap<String, String> routeFromCurrPickNodeToOtherPickNode = new HashMap<String, String>();
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = new ArrayList<HashMap>();
        
        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            int xCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[1]);
            int yCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[2]);
            
            //ArrayList of y coordinates corner nodes that have the same x as current pick node
            ArrayList<Integer> yCoordinatesOfCornerNodesNearCurrPickNode = cornerNodesMap.get(xCoordinateOfCurrentPickNode);
            Integer yCoordNearestCornerToCurrPickNode = -1;
            int currDiff = Integer.MAX_VALUE;
            
            //Iterate through the ArrayList above and find the y coordinate that is nearest to current pick node's y coordinate
            for (Integer yCoordCornerPt : yCoordinatesOfCornerNodesNearCurrPickNode) {
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
                
                int xCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[1]);
                int yCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[2]);
                
                String otherPickNodeXAndY = xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode;
                String thisPickNodeXAndY = xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode;
                
                if (!otherPickNodeXAndY.equals(thisPickNodeXAndY)) {    
                    int distFromCurrentPickNodeToOtherPickNode = -1;

                    //if current pick node and other pick node have the same X coordinate, just go straight to other pick node
                    if (Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) == 0) {

                        //distance here is just the absolute difference of y coordinates between current pick node and other pick node
                        distFromCurrentPickNodeToOtherPickNode = Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode);

                        //save the full route in case we need to retrieve later. Key is in format: x-y coordinate of current pick node + "to" + x-y coordinate of other pick node, value is the full route from current pick node to other pick node
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);
                    } 
                    //if pick item and start point are on 2 adjacent X coordinates, turn left/right (hence the +1 in distance) and go straight to pick item 
                    else if (Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) == 1) {

                        //distance in this case = distance from current pick node to the node with adjacent X and same Y + distance from that node to other pick node 
                        distFromCurrentPickNodeToOtherPickNode = Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode) + 1;

                        //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfCurrentPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);
                    } else {
                        //dist from current pick node to other pick node = dist from current pick node to nearest corner node A with same x coordinate as current pick node + dist from corner node A to corner node B with same y coordinate as A and x coordinate of other pick item + dist from corner node B to other pick node
                        distFromCurrentPickNodeToOtherPickNode = Math.abs(yCoordinateOfOtherPickNode - yCoordNearestCornerToCurrPickNode) + Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) + Math.abs(yCoordNearestCornerToCurrPickNode - yCoordinateOfCurrentPickNode);

                        //save the full route in case we need to retrieve later. Key is x-y coordinate of the pick node, value is the full route from start node to pick node
                        routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "-" + xCoordinateOfCurrentPickNode + "," + yCoordNearestCornerToCurrPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordNearestCornerToCurrPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);
                    }

                    distAmongPickItems.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, distFromCurrentPickNodeToOtherPickNode);   

                }
                
            }
        }
        
        ptToPtRouteAndDistanceArr.add(distAmongPickItems);
        ptToPtRouteAndDistanceArr.add(routeFromCurrPickNodeToOtherPickNode);
        return ptToPtRouteAndDistanceArr;
    }
    
    //Step 3
    public HashMap getSavingsMap(ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath) {
        HashMap<String, Integer> savingsMap = new HashMap<String, Integer>();
        
        ArrayList<HashMap> intialSolution = getInitialSolution(pickingList, startingPoint, cornerNodeFilePath);
        HashMap<String, Integer> distOfStartPtToAllPt = intialSolution.get(0);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = getPointToPointDistance(pickingList, cornerNodeFilePath);
        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0);

        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            int xCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[1]);
            int yCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[2]);

            for (int j = i + 1; j < pickingList.size(); j++) {
                String otherPickNode = pickingList.get(j);

                int xCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[1]);
                int yCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[2]);
                
                String otherPickNodeXAndY = xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode;
                String thisPickNodeXAndY = xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode;
                
                if (!otherPickNodeXAndY.equals(thisPickNodeXAndY)) {  
                    int distFromStartPtToNodei = distOfStartPtToAllPt.get(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode); //c0i
                    int distFromStartPtToNodej = distOfStartPtToAllPt.get(xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode); //c0j

                    int distFromNodeiToNodej = distAmongPickItems.get(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);

                    int savingsForEdgeij = distFromStartPtToNodei + distFromStartPtToNodej - distFromNodeiToNodej;

                    savingsMap.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, savingsForEdgeij);
                }
            }
        }
        
        List<String> mapKeys = new ArrayList<>(savingsMap.keySet());
        List<Integer> mapValues = new ArrayList<>(savingsMap.values());
        
        //sort savings map descendingly
        Collections.sort(mapValues, new Comparator<Integer>() {
                @Override
                public int compare(Integer e1, Integer e2) {
                    return e2 - e1;
                }
            });
        
        LinkedHashMap<String, Integer> sortedSavingsMap = new LinkedHashMap<>();

        Iterator<Integer> valueIterator = mapValues.iterator();
        while (valueIterator.hasNext()) {
            Integer value = valueIterator.next();
            Iterator<String> keyIterator = mapKeys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Integer originalSaving = savingsMap.get(key);
                Integer sortedSaving = value;

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
    public HashMap<String, String> getSolution(HashMap<String, Integer> pickItemCapacityMap, HashMap<String, Integer> savingsMap, Double mheCapacity, String startingPoint) {
        HashMap<String, String> solutionMap = new HashMap<>(); 
        HashMap<String, Integer> capacityMap = new HashMap<>();
        
        int totalCapacityOfThisRoute = 0;
        Set solutionMapKeySet = savingsMap.keySet();
        
        Iterator<String> keySetIter = solutionMapKeySet.iterator();
        
        while (keySetIter.hasNext()) { //iterate through all records in savingsMap
            String thisKey = keySetIter.next();
            String[] thisKeyArr = thisKey.split("to");
            
            String thisItem = thisKeyArr[0]; //this contains x-y coordinate of the first pick item (before "to") from savings map
            String anotherItem = thisKeyArr[1]; //this contains x-y coordinate of the second pick item (after "to") from savings map
            
            //if capacity of thisItem is 0, make it 1 as we're using at least 1 carton. Else keep it as it is
            int thisItemCapacity = pickItemCapacityMap.get(thisItem) > 0 ? pickItemCapacityMap.get(thisItem) : 1;
            
            //if capacity of anotherItem is 0, make it 1 as we're using at least 1 carton. Else keep it as it is
            int anotherItemCapacity = pickItemCapacityMap.get(anotherItem) > 0 ? pickItemCapacityMap.get(anotherItem) : 1;
            
            //If both pick items are not found in solutionMap
            if (solutionMap.get(thisItem) == null && solutionMap.get(anotherItem) == null) {
                totalCapacityOfThisRoute = 0;
                
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
                Integer currentRouteCapacity = capacityMap.get(anotherItem);
                
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
                Integer currentRouteCapacity = capacityMap.get(thisItem);
                
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
                Integer thisRouteCapacity = capacityMap.get(thisItem);
                
                String anotherRoute = solutionMap.get(anotherItem);
                Integer anotherRouteCapacity = capacityMap.get(anotherItem);
                
                String[] thisRouteSplit = thisRoute.split("-");
                String[] anotherRouteSplit = anotherRoute.split("-");
                
                String newRoute = "";
                Integer combinedCapacity = thisRouteCapacity + anotherRouteCapacity;
                
                //System.out.println(thisRoute);
                //System.out.println(anotherRoute);
                //System.out.println(thisItem + "----" + anotherItem);
                //if thisItem and anotherItem belong to diff routes & if capacity of combined route doesn't exceed capacity limit
                if (!thisRoute.equals(anotherRoute) && combinedCapacity <= mheCapacity) {
                    
                    //if thisItem and anotherItem both appear at either the first or last position of their routes: we have to flip the shorter route before merging
                    if ((thisRouteSplit[0].equals(thisItem) && anotherRouteSplit[0].equals(anotherItem)) || (thisRouteSplit[thisRouteSplit.length - 1].equals(thisItem) && anotherRouteSplit[anotherRouteSplit.length - 1].equals(anotherItem))) {
                        
                        if (thisRouteSplit.length > anotherRouteSplit.length) { //if thisRoute is longer than anotherRoute
                            
                            if (thisRouteSplit[0].equals(thisItem)) { //if thisItem and anotherItem are both first nodes in their routes, new route = flipped route + this route
                                String flippedAnotherRoute = "";
                            
                                for (int i = anotherRouteSplit.length -1; i >= 0; i--) {
                                    flippedAnotherRoute += anotherRouteSplit[i] + "-";
                                }

                                newRoute = flippedAnotherRoute + thisRoute;
                                
                            } else { //if thisItem and anotherItem are both last nodes in their routes, new route = this route + flipped route 
                                newRoute = thisRoute;
                                
                                String flippedAnotherRoute = "";
                            
                                for (int i = anotherRouteSplit.length -1; i >= 0; i--) {
                                    flippedAnotherRoute += anotherRouteSplit[i] + "-";
                                }

                                newRoute += flippedAnotherRoute;
                                
                            }
                            
                        } else { //if thisRoute is shorter than or as long as anotherRoute
                            
                            if (thisRouteSplit[0].equals(thisItem)) { //if thisItem and anotherItem are both first nodes in their routes, new route = flipped route + this route
                                String flippedThisRoute = "";
                            
                                for (int i = thisRouteSplit.length -1; i >= 0; i--) {
                                    flippedThisRoute += thisRouteSplit[i] + "-";
                                }

                                newRoute = flippedThisRoute + anotherRoute;
                                
                            } else { //if thisItem and anotherItem are both last nodes in their routes, new route = this route + flipped route 
                                newRoute = anotherRoute;
                                
                                String flippedThisRoute = "";
                            
                                for (int i = thisRouteSplit.length -1; i >= 0; i--) {
                                    flippedThisRoute += thisRouteSplit[i] + "-";
                                }
                                
                                newRoute += flippedThisRoute;
                                
                            }
                        }
                    } 
                    //if thisItem and anotherItem appear at different ends of their own routes, just need to merge the 2 routes without flipping
                    else if ((thisRouteSplit[0].equals(thisItem) && anotherRouteSplit[anotherRouteSplit.length - 1].equals(anotherItem)) || (thisRouteSplit[thisRouteSplit.length - 1].equals(thisItem) && anotherRouteSplit[0].equals(anotherItem))) {
                        
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
            
            solutionMap.put(thisKey, thisRoute + "-" + lastPickItemXCoordinate + ",1");
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
    
    public HashMap<String, Integer> getDistanceOfFinalRoutes (ArrayList<String> pickingList, ArrayList<String> finalRoutes, String startingPoint, String cornerNodeFilePath) {
        HashMap<String, Integer> finalRoutesDistHashMap = new HashMap<>();
        ArrayList<HashMap> initialSolution = getInitialSolution(pickingList, startingPoint, cornerNodeFilePath);
        ArrayList<HashMap> ptToPtDistanceArr = getPointToPointDistance(pickingList, cornerNodeFilePath);
        
        HashMap distFromStartPtToPickItem = initialSolution.get(0);
        HashMap routeFromStartPtToPickItem = initialSolution.get(1);
        
        HashMap distFromPickItemToPickItem = ptToPtDistanceArr.get(0);
        HashMap routeFromPickItemToPickItem = ptToPtDistanceArr.get(1);
        
        //System.out.println("route start to pick: " + routeFromStartPtToPickItem);
        //System.out.println("route pick to pick: " + routeFromPickItemToPickItem);
        //boolean isFirstRoute = true;
        //String lastNodeOfPrevRoute = "";
        
        for (String finalRoute : finalRoutes) {
            /*if (isFirstRoute) {
                finalRoute = startingPoint + "-" + finalRoute;
            } else {
                finalRoute = lastNodeOfPrevRoute + "-" + finalRoute;
            }
            
            isFirstRoute = false;*/
            String[] finalRouteSplit = finalRoute.split("-");
            Integer thisRouteTotalDistance = 0; 
            
            //System.out.println("this route: " + finalRoute);
            
            if (finalRouteSplit.length >= 3) {
                String startNodeToFirstPickNodeKey = finalRouteSplit[1];
                
                if (distFromStartPtToPickItem.get(startNodeToFirstPickNodeKey) != null) {
                    thisRouteTotalDistance += (Integer) distFromStartPtToPickItem.get(startNodeToFirstPickNodeKey);
                    //System.out.println("Start: " + routeFromStartPtToPickItem.get(startNodeToFirstPickNodeKey));
                    //System.out.println("start key: " + startNodeToFirstPickNodeKey + ", dist: " + distFromStartPtToPickItem.get(startNodeToFirstPickNodeKey) + " --- total dist so far: " + thisRouteTotalDistance);
                } else if (distFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1]) != null) {
                    thisRouteTotalDistance += (Integer) distFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1]);
                    //System.out.println("route detail: " + routeFromPickItemToPickItem.get(finalRouteSplit[0] + "to" + finalRouteSplit[1]));
                    //System.out.println("start key: " + finalRouteSplit[0] + "-" + finalRouteSplit[1] + ", dist: " + distFromStartPtToPickItem.get(distFromPickItemToPickItem.get(finalRouteSplit[0] + "-" + finalRouteSplit[1])) + " --- total dist so far: " + thisRouteTotalDistance);
                    
                } else if (distFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0]) != null) {
                    thisRouteTotalDistance += (Integer) distFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0]);
                    //System.out.println("route detail: " + routeFromPickItemToPickItem.get(finalRouteSplit[1] + "to" + finalRouteSplit[0]));
                    //System.out.println("start key: " + finalRouteSplit[0] + "-" + finalRouteSplit[1] + ", dist: " + distFromStartPtToPickItem.get(distFromPickItemToPickItem.get(finalRouteSplit[1] + "-" + finalRouteSplit[0])) + " --- total dist so far: " + thisRouteTotalDistance);
                }      
                
                for (int i = 1; i < finalRouteSplit.length - 2; i++) {
                    String thisNode = finalRouteSplit[i];
                    String nextNode = finalRouteSplit[i+1];
                    Integer xCoordOfThisNode = Integer.parseInt(thisNode.split(",")[0]);
                    Integer xCoordOfNextNode = Integer.parseInt(nextNode.split(",")[0]);
                    String thisPath = "";
                    
                    thisPath = thisNode + "to" + nextNode;
                    if (distFromPickItemToPickItem.get(thisPath) != null) {
                        thisRouteTotalDistance += (Integer) distFromPickItemToPickItem.get(thisPath);
                        //System.out.println("route key: " + thisPath + ", dist: " + distFromPickItemToPickItem.get(thisPath) + " --- total dist so far: " + thisRouteTotalDistance);
                    } else {
                        thisPath = nextNode + "to" + thisNode;
                        thisRouteTotalDistance += (Integer) distFromPickItemToPickItem.get(thisPath);
                        //System.out.println("route key: " + thisPath + ", dist: " + distFromPickItemToPickItem.get(thisPath) + " --- total dist so far: " + thisRouteTotalDistance);
                    }
                    
                }
                
               String lastPickNode = finalRouteSplit[finalRouteSplit.length - 2];
               String lastNode = finalRouteSplit[finalRouteSplit.length - 1];
               
               thisRouteTotalDistance += Math.abs(Integer.parseInt(lastNode.split(",")[1]) - Integer.parseInt(lastPickNode.split(",")[1]));
               //lastNodeOfPrevRoute = lastNode;
               //System.out.println("route " + finalRoute + " total dist: " + thisRouteTotalDistance); 
               
               finalRoutesDistHashMap.put(finalRoute, thisRouteTotalDistance);
            }
        }
        
        return finalRoutesDistHashMap;
    }
}
