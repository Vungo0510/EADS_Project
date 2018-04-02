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
    private HashMap<Integer, ArrayList<Integer>> cornerNodesMap = csvReader.readAllCornerNodes("./Data/CornerNodes.csv"); //contains the y coordinates of all the corner nodes with the same x coordinates. key is x coordinate
    
    public ArrayList<String> getPickingListCornerNodes(ArrayList<String> pickingList) {
        
        ArrayList<String> storeAllPickItemCornerNodes = new ArrayList<String>();
        
        for (int i = 0; i < pickingList.size(); i++) {
            String pickItem = pickingList.get(i);
            String[] pickItemArr = pickItem.split(",");
            Integer pickItemXCoordinate = Integer.parseInt(pickItemArr[1]);
            Integer pickItemYCoordinate = Integer.parseInt(pickItemArr[2]);
      
            ArrayList<Integer> allCornerNodesOfThisX = cornerNodesMap.get(pickItemXCoordinate); // retrieve the Y coordinates of all the corner nodes with the same x coordinates as the pick item
            int belowCornerNodeY = -1;
            int aboveConerNodeY = Integer.MAX_VALUE;
            for (int j = 0; j < allCornerNodesOfThisX.size(); j++) {
                Integer thisCornerNode = allCornerNodesOfThisX.get(j);
                
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
    
    public ArrayList<String> subgraphPartitioning(ArrayList<String> pickingList) {
        
    //this method will return all the corner nodes found within the subgraph
        ArrayList<String> pickListCornerNodes = getPickingListCornerNodes(pickingList);
        Integer largestX = -1;
        Integer largestY = -1;
        Integer smallestX = Integer.MAX_VALUE;
        Integer smallestY = Integer.MAX_VALUE;
        
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
        
        ArrayList<String> cornerNodesWithinBorder = new ArrayList<String>();  //cornerNodesWithinBorder is an ArrayList that stores all the corner nodes and pick items inside the subgraph
        Iterator<Integer> cornerNodesIterator = cornerNodesMap.keySet().iterator();
        
        while (cornerNodesIterator.hasNext()) {
            Integer cornerNodeX = cornerNodesIterator.next();
            
            if (cornerNodeX >= smallestX && cornerNodeX <= largestX) {
                ArrayList<Integer> cornerNodeY = cornerNodesMap.get(cornerNodeX);
                
                for (Integer yCoordinate : cornerNodeY) {
                    if (yCoordinate >= smallestY && yCoordinate <= largestY) {
                        cornerNodesWithinBorder.add(cornerNodeX + "," + yCoordinate);  //cornerNodesWithinBorder is an ArrayList that stores all the corner nodes and pick items inside the subgraph
                    }
                }
            }
        }
        
        for (int i = 0; i < pickingList.size(); i++) {
            // Adding pick items' x and y coordinates into cornerNodesWithinBorder ArrayList
            String pickItem = pickingList.get(i);
            String[] pickItemArr = pickItem.split(",");
            Integer pickItemXCoordinate = Integer.parseInt(pickItemArr[1]);
            Integer pickItemYCoordinate = Integer.parseInt(pickItemArr[2]);
            
            cornerNodesWithinBorder.add(pickItemXCoordinate + "," + pickItemYCoordinate);
        }
       
        System.out.println(smallestX);
        System.out.println(largestX);
        System.out.println(smallestY);
        System.out.println(largestY);
        
        return cornerNodesWithinBorder;
    }
}
