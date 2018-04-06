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
public class SubgraphDesign {
    
    private CSVReader csvReader = new CSVReader();
    private TreeMap<Double, ArrayList<Double>> cornerNodesMap; //contains the y coordinates of all the corner nodes with the same x coordinates. key is x coordinate
    
    public ArrayList<String> getPickingListCornerNodes(ArrayList<String> pickingList, String cornerNodeFilePath) {
        
        ArrayList<String> storeAllPickItemCornerNodes = new ArrayList<String>();
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        
        for (int i = 0; i < pickingList.size(); i++) {
            String pickItem = pickingList.get(i);
            String[] pickItemArr = pickItem.split(",");
            Integer pickItemXCoordinate = Integer.parseInt(pickItemArr[1]);
            Integer pickItemYCoordinate = Integer.parseInt(pickItemArr[2]);
      
            ArrayList<Double> allCornerNodesOfThisX = cornerNodesMap.get(pickItemXCoordinate); // retrieve the Y coordinates of all the corner nodes with the same x coordinates as the pick item
            double belowCornerNodeY = -1.0;
            double aboveConerNodeY = Integer.MAX_VALUE;
            
            for (int j = 0; j < allCornerNodesOfThisX.size(); j++) {
                Double thisCornerNode = allCornerNodesOfThisX.get(j);
                
                if (thisCornerNode < pickItemYCoordinate) {
                    if (thisCornerNode > belowCornerNodeY) {
                        //finding the corner node directly below the pick item
                        belowCornerNodeY = thisCornerNode;
                    }
                } else {
                    if (thisCornerNode < aboveConerNodeY) {
                        //finding the corner node directly above the pick item
                        aboveConerNodeY = thisCornerNode;
                    }
                }
            }
            storeAllPickItemCornerNodes.add(pickItemXCoordinate + "," + belowCornerNodeY);
            storeAllPickItemCornerNodes.add(pickItemXCoordinate + "," + aboveConerNodeY);
        }
        
        return storeAllPickItemCornerNodes;
    }
    
    public ArrayList<ArrayList<String>> subgraphPartitioning(ArrayList<String> pickingList, String cornerNodeFilePath) {
        
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        ArrayList<Double> boundaryList = getPickListBoundary(pickingList, cornerNodeFilePath);
        Double smallestX = boundaryList.get(0);
        Double smallestY = boundaryList.get(1);
        Double largestX = boundaryList.get(2);
        Double largestY = boundaryList.get(3);
        
        ArrayList<ArrayList<String>> toReturn = new ArrayList<>();
        ArrayList<String> cornerNodesWithinBorder = new ArrayList<String>();  //cornerNodesWithinBorder is an ArrayList that stores all the corner nodes inside the subgraph
        
        Iterator<Double> cornerNodesIterator = cornerNodesMap.keySet().iterator();
        
        while (cornerNodesIterator.hasNext()) {
            Double cornerNodeX = cornerNodesIterator.next();
            
            if (cornerNodeX >= smallestX && cornerNodeX <= largestX) {
                ArrayList<Double> cornerNodeY = cornerNodesMap.get(cornerNodeX);
                
                for (Double yCoordinate : cornerNodeY) {
                    if (yCoordinate >= smallestY && yCoordinate <= largestY) {
                        cornerNodesWithinBorder.add(cornerNodeX + "," + yCoordinate);  //cornerNodesWithinBorder is an ArrayList that stores all the corner nodes and pick items inside the subgraph
                    }
                }
            }
        }
        
        //nodesWithinBorder is an ArrayList that stores all the corner nodes AND pick items inside the subgraph
        ArrayList<String> nodesWithinBorder = new ArrayList<>(cornerNodesWithinBorder);
        
        for (int i = 0; i < pickingList.size(); i++) {
            // Adding pick items' x and y coordinates into cornerNodesWithinBorder ArrayList
            String pickItem = pickingList.get(i);
            String[] pickItemArr = pickItem.split(",");
            Integer pickItemXCoordinate = Integer.parseInt(pickItemArr[1]);
            Integer pickItemYCoordinate = Integer.parseInt(pickItemArr[2]);
            
            if (!nodesWithinBorder.contains(pickItemXCoordinate + "," + pickItemYCoordinate)) {
                nodesWithinBorder.add(pickItemXCoordinate + "," + pickItemYCoordinate);
            }
        }
       
        //System.out.println(smallestX);
        //System.out.println(largestX);
        //System.out.println(smallestY);
        //System.out.println(largestY);
        toReturn.add(cornerNodesWithinBorder);
        toReturn.add(nodesWithinBorder);
        
        //First ArrayList contains corner nodes within border, second ArrayList contains corner nodes AND pick item nodes within border
        return toReturn;
    }
    
    public HashMap getSubgraphMap(ArrayList<String> pickingList, String cornerNodeFilePath) {
        cornerNodesMap = csvReader.readAllCornerNodes(cornerNodeFilePath);
        HashMap<Double, ArrayList<Double>> subgraphMap = new HashMap<>(cornerNodesMap);
        
        ArrayList<Double> boundaryList = getPickListBoundary(pickingList, cornerNodeFilePath);
        HashMap<Double, ArrayList<Double>> pickItemMap = new HashMap<>();
        
        for (int i = 0; i < pickingList.size(); i++) {
            // adding pick items to a map with item's X coordinate as key and Y coordinate(s) as value(s)
            String pickItem = pickingList.get(i);
            String[] pickItemArr = pickItem.split(",");
            Double pickItemXCoordinate = Double.parseDouble(pickItemArr[1]);
            Double pickItemYCoordinate = Double.parseDouble(pickItemArr[2]);
            
            ArrayList<Double> pickItemYCoordArr = pickItemMap.get(pickItemXCoordinate);
            
            if (pickItemYCoordArr == null) {
                pickItemYCoordArr = new ArrayList<>();
            } 
            
            //if the array of all existing Y coordinates of pick items for this X coordinate doesn't contain the Y coordinate of this item, add it in the array
            if (!pickItemYCoordArr.contains(pickItemYCoordinate)) {
                pickItemYCoordArr.add(pickItemYCoordinate);
                pickItemMap.put(pickItemXCoordinate, pickItemYCoordArr);
            }
        }
        
        Double smallestX = boundaryList.get(0);
        Double smallestY = boundaryList.get(1);
        Double largestX = boundaryList.get(2);
        Double largestY = boundaryList.get(3);
        
        Iterator<Double> cornerNodesIterator = cornerNodesMap.keySet().iterator();
        
        while (cornerNodesIterator.hasNext()) {
            Double cornerNodeX = cornerNodesIterator.next();
            if (cornerNodeX > largestX || cornerNodeX < smallestX) {
                subgraphMap.remove(cornerNodeX);
            }
        }
        
        Iterator<Double> pickItemsIterator = pickItemMap.keySet().iterator();
        
        while (pickItemsIterator.hasNext()) {
            Double pickItemXCoord = pickItemsIterator.next();
            ArrayList<Double> pickItemYCoordArr = pickItemMap.get(pickItemXCoord);
            
            //initially, this array only contains corner nodes's y coordinates for this x coordinates
            ArrayList<Double> nodesYCoordArr = subgraphMap.get(pickItemXCoord);
            
            //adding all pick items' y coordinate to the above array
            nodesYCoordArr.addAll(pickItemYCoordArr);
            
            subgraphMap.put(pickItemXCoord, nodesYCoordArr);
        }
        
        Iterator<Double> subgraphMapIterator = subgraphMap.keySet().iterator();
        
        while (subgraphMapIterator.hasNext()) {
            //node can be either an pick item or a corner node
            Double nodeXCoord = subgraphMapIterator.next();
            ArrayList<Double> nodesYCoordArr = subgraphMap.get(nodeXCoord);
        
            nodesYCoordArr.sort(new Comparator<Double>() {
                    public int compare (Double s1, Double s2) {
                        return s1.intValue() - s2.intValue();
                    }
                });
            
            subgraphMap.put(nodeXCoord, nodesYCoordArr);
        }
              
        return subgraphMap;
    }
    
    public ArrayList<Double> getPickListBoundary(ArrayList<String> pickingList, String cornerNodeFilePath) {
         //this method will return all the corner nodes found within the subgraph
        ArrayList<Double> boundaryList = new ArrayList<>();
        ArrayList<String> pickListCornerNodes = getPickingListCornerNodes(pickingList, cornerNodeFilePath);
        double largestX = -1;
        double largestY = -1;
        double smallestX = Integer.MAX_VALUE;
        double smallestY = Integer.MAX_VALUE;
        
        for (int i = 0; i < pickListCornerNodes.size(); i++)  {
        //finding the 4 border corner nodes to draw the subgraph which contains all pick items
            
            String[] thisCornerNode = pickListCornerNodes.get(i).split(",");
            Integer cornerX = Integer.parseInt(thisCornerNode[0]);
            Integer cornerY = Integer.parseInt(thisCornerNode[1]);
            
            if (largestX < cornerX) {
                largestX = cornerX;
            } else if (smallestX > cornerX) {
                smallestX = cornerX;
            }
            
            if (largestY < cornerY) {
                largestY = cornerY;
            } else if (smallestY > cornerY) {
                smallestY = cornerY;
            }
        }
        boundaryList.add(smallestX);
        boundaryList.add(smallestY);
        boundaryList.add(largestX);
        boundaryList.add(largestY);
        
        return boundaryList;
    }
}
