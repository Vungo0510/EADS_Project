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

    private String startingPoint = "1,3"; //Store x,y coordinates of the starting point of the vehicle "x,y". To be passed in from UI
    private CSVReader csvReader = new CSVReader();    
    private HashMap<Integer, ArrayList<Integer>> cornerNodesMap = csvReader.readAllCornerNodes();
    
    //Step 1
    public ArrayList<HashMap> getInitialSolution(ArrayList<String> pickingList) {
        // this method calculates the distance from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        HashMap<String, Integer> distOfStartPtToAllPt = new HashMap<String, Integer>(); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node
        HashMap<String, String> routeOfStartPtToAllPt = new HashMap<String, String>();
        ArrayList<HashMap> initialSolution = new ArrayList<HashMap>();
        
        int xCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[0]);
        int yCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[1]);
        
        //ArrayList of y coordinates corner nodes that have the same x as start pt
        ArrayList<Integer> yCoordinatesOfCornerNodesNearStartPt = cornerNodesMap.get(xCoordinateOfStartPt);
        Integer yCoordNearestCornerToStartPt = -1;
        int currDiff = Integer.MAX_VALUE;
        
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
            
            //ArrayList<Integer> yCoordinatesOfCornerNodesNearPickItem = cornerNodesMap.get(xCoordinateOfPickItem);
            /*Integer nearestCornerYCoordToPickItem = -1;
            currDiff = Integer.MAX_VALUE;

            for (Integer yCoordCornerPt : yCoordinatesOfCornerNodesNearPickItem) {
                if (Math.abs(yCoordCornerPt - yCoordinateOfPickItem) < currDiff) {
                    nearestCornerYCoordToPickItem = yCoordCornerPt;
                    currDiff = Math.abs(yCoordCornerPt - yCoordinateOfPickItem);
                }
            }
            */

            int distFromStartPtToPickItem = Math.abs(yCoordinateOfStartPt - yCoordNearestCornerToStartPt) + Math.abs(xCoordinateOfStartPt - xCoordinateOfPickItem) + Math.abs(yCoordNearestCornerToStartPt - yCoordinateOfPickItem);
            distOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, distFromStartPtToPickItem);
            routeOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, xCoordinateOfStartPt + "," + yCoordinateOfStartPt + "-" + xCoordinateOfStartPt + "," + yCoordNearestCornerToStartPt + "-" + xCoordinateOfPickItem + "," + yCoordNearestCornerToStartPt + "-" + xCoordinateOfPickItem + "," + yCoordinateOfPickItem);
        }
        initialSolution.add(distOfStartPtToAllPt);
        initialSolution.add(routeOfStartPtToAllPt);
        return initialSolution;
    }
    
    //Step 2
    public ArrayList<HashMap> getPointToPointDistance(ArrayList<String> pickingList) {
        // this method calculates the distance from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        HashMap<String, Integer> distAmongPickItems = new HashMap<String, Integer>(); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance
        HashMap<String, String> routeFromCurrPickNodeToOtherPickNode = new HashMap<String, String>();
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = new ArrayList<HashMap>();
        
        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            int xCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[1]);
            int yCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[2]);
            
            ArrayList<Integer> yCoordinatesOfCornerNodesNearCurrPickNode = cornerNodesMap.get(xCoordinateOfCurrentPickNode);
            Integer yCoordNearestCornerToCurrPickNode = -1;
            int currDiff = Integer.MAX_VALUE;

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

                int distFromCurrentPickNodeToOtherPickNode = Math.abs(yCoordinateOfOtherPickNode - yCoordNearestCornerToCurrPickNode) + Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) + Math.abs(yCoordNearestCornerToCurrPickNode - yCoordinateOfCurrentPickNode);
                distAmongPickItems.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, distFromCurrentPickNodeToOtherPickNode);   
                routeFromCurrPickNodeToOtherPickNode.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "-" + xCoordinateOfCurrentPickNode + "," + yCoordNearestCornerToCurrPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordNearestCornerToCurrPickNode + "-" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);
            }
        }
        
        ptToPtRouteAndDistanceArr.add(distAmongPickItems);
        ptToPtRouteAndDistanceArr.add(routeFromCurrPickNodeToOtherPickNode);
        return ptToPtRouteAndDistanceArr;
    }
    
    //Step 3
    public HashMap getSavingsMap(ArrayList<String> pickingList) {
        HashMap<String, Integer> savingsMap = new HashMap<String, Integer>();
        
        ArrayList<HashMap> intialSolution = getInitialSolution(pickingList);
        HashMap<String, Integer> distOfStartPtToAllPt = intialSolution.get(0);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = getPointToPointDistance(pickingList);
        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0);

        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            int xCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[1]);
            int yCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[2]);

            for (int j = i + 1; j < pickingList.size(); j++) {
                String otherPickNode = pickingList.get(j);

                int xCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[1]);
                int yCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[2]);

                int distFromStartPtToNodei = distOfStartPtToAllPt.get(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode); //c0i
                int distFromStartPtToNodej = distOfStartPtToAllPt.get(xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode); //c0j
                int distFromNodeiToNodej = distAmongPickItems.get(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode);

                int savingsForEdgeij = distFromStartPtToNodei + distFromStartPtToNodej - distFromNodeiToNodej;

                savingsMap.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, savingsForEdgeij);
            }
        }
        
        List<String> mapKeys = new ArrayList<>(savingsMap.keySet());
        List<Integer> mapValues = new ArrayList<>(savingsMap.values());
        Collections.sort(mapValues, new Comparator<Integer>() {
                @Override
                public int compare(Integer e1, Integer e2) {
                    return e2 - e1;
                }
            });
        //Collections.sort(mapKeys);
        
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
    public HashMap<String, String> getSolution(HashMap<String, Integer> savingsMap, Double mheCapacity) {
        HashMap<String, String> solutionMap = new HashMap<String, String>(); 
        
        Set solutionMapKeySet = savingsMap.keySet();
        
        Iterator<String> keySetIter = solutionMapKeySet.iterator();
        while (keySetIter.hasNext()) {
            String thisKey = keySetIter.next();
            String[] thisKeyArr = thisKey.split("to");
            String item = thisKeyArr[0]; //this contains x-y coordinate of the first pick item (before "to") from savings map
            String anotherItem = thisKeyArr[1]; //this contains x-y coordinate of the second pick item (after "to") from savings map
            
            //If both pick items are not found in solutionMap
            if (solutionMap.get(item) == null && solutionMap.get(anotherItem) == null) {
                solutionMap.put(item, item + "-" + anotherItem);
                solutionMap.put(anotherItem, item + "-" + anotherItem);
                
            } else if (solutionMap.get(item) == null) { //if only anotherItem is found in solutionMap
                String currentRoute = solutionMap.get(anotherItem);
                String[] currentRouteSplit = currentRoute.split("-");
                String newRoute = currentRoute;
                        
                if (currentRouteSplit[0].equals(anotherItem)) {
                    newRoute = item + "-" + currentRoute;        
                } else if (currentRouteSplit[currentRouteSplit.length - 1].equals(anotherItem)) {
                    newRoute = currentRoute + "-" + item;       
                } else {
                    solutionMap.put(item, item);
                }
                
                if (!newRoute.equals(currentRoute)) {
                    for (int i = 0; i < currentRouteSplit.length; i++) {
                        solutionMap.put(currentRouteSplit[i], newRoute);
                    }
                    solutionMap.put(item, newRoute);
                }
   
            } else if (solutionMap.get(anotherItem) == null) { //if only item is found in solutionMap
                
                String currentRoute = solutionMap.get(item);
                String[] currentRouteSplit = currentRoute.split("-");
                String newRoute = currentRoute;
                
                if (currentRouteSplit[0].equals(item)) {
                    newRoute = anotherItem + "-" + currentRoute;        
                } else if (currentRouteSplit[currentRouteSplit.length - 1].equals(item)) {
                    newRoute = currentRoute + "-" + anotherItem;       
                } else {
                    solutionMap.put(anotherItem, anotherItem);
                }
                
                if (!newRoute.equals(currentRoute)) {
                    for (int i = 0; i < currentRouteSplit.length; i++) {
                        solutionMap.put(currentRouteSplit[i], newRoute);
                    }
                    solutionMap.put(anotherItem, newRoute);
                }
            }
        }
        
        Set updatedSolutionMapKeySet = solutionMap.keySet();
        Iterator<String> secondKeySetIter = updatedSolutionMapKeySet.iterator();
        
        while (secondKeySetIter.hasNext()) {
            String thisKey = secondKeySetIter.next();
            String thisRoute = solutionMap.get(thisKey);
            String[] thisRouteArr = thisRoute.split("-");
            String lastPickItem = thisRouteArr[thisRouteArr.length - 1];
            String lastPickItemXCoordinate = lastPickItem.split(",")[0];
            
            solutionMap.put(thisKey, startingPoint + "-" + thisRoute + "-" + lastPickItemXCoordinate + ",1");
        }
        return solutionMap;
    }
}
