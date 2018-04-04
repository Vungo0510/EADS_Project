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
public class TwiceAroundTheTree {
    
    //get the distance among all the nodes in subgraphMap. For each pair of node A and B with xA < xB OR yA < yB, ONLY STORES THE DISTANCE FROM A to B AND NOT B TO A
    public HashMap<String, Integer> getDistanceAmongNodes(HashMap<Integer, ArrayList<Integer>> subgraphMap, ArrayList<ArrayList<String>> subgraphPartitioningResult) {
        //ArrayList<String> minimumSpanningTree = new ArrayList<>();
        HashMap<String, Integer> distanceMap = new HashMap<>();
        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);
        
        System.out.println(subgraphMap);
        System.out.println("CORNER NODES:" + cornerNodesWithinBorder);
        
        //this subgraph contains the corner nodes (after subgraph partitioning) and the pick item nodes
        Iterator<Integer> subGraphIter = subgraphMap.keySet().iterator();
        int position = 0; //denote the index of element inside key set of this HashMap
        int previousXCoord = -1;
        //int nearestHigherY = -1;
        
        int distFromPreviousXToThisX = -1;
        int distFromThisYToNearestHigherY = -1;
        
        while(subGraphIter.hasNext()) {
            Integer thisXCoord = subGraphIter.next();
            if (position != 0) {
                distFromPreviousXToThisX = thisXCoord - previousXCoord;
            }
            
            ArrayList<Integer> yCoordArrOfThisXCoord = subgraphMap.get(thisXCoord);
            
            for (int i = 0; i < yCoordArrOfThisXCoord.size() - 1; i++) {
                Integer yCoord = yCoordArrOfThisXCoord.get(i);
                Integer nearestHigherYCoord = yCoordArrOfThisXCoord.get(i+1);
                distFromThisYToNearestHigherY = nearestHigherYCoord - yCoord;
                
                //put the horizontal distance from (previous X coordinate, this Y coordinate) to (this X coordinate, this Y coordinate)
                if (position != 0) {
                    /*if ((previousXCoord + "," + yCoord).equals("17,58")) {
                            System.out.println("ERRR: " + yCoordArrOfThisXCoord);
                        }*/
                    if (cornerNodesWithinBorder.contains(previousXCoord + "," + yCoord)) {
                        distanceMap.put(previousXCoord + "," + yCoord + "-" + thisXCoord + "," + yCoord, distFromPreviousXToThisX);
                    }
                    
                    //if we are at the second highest Y coordinate, put in the distance from (previous X coordinate, highest Y coordinate) to (this X coordinate, highest Y coordinate) as well cos we won't be iterating through the highest Y coordinate
                    if (i == yCoordArrOfThisXCoord.size() - 2 && cornerNodesWithinBorder.contains(previousXCoord + "," + nearestHigherYCoord)) {
                        distanceMap.put(previousXCoord + "," + nearestHigherYCoord + "-" + thisXCoord + "," + nearestHigherYCoord, distFromPreviousXToThisX);
                    }
                } 
                
                //put the vertical distance from (this X coordinate, this Y coordinate) to (this X coordinate, nearest higher Y coordinate)
                distanceMap.put(thisXCoord + "," + yCoord + "-" + thisXCoord + "," + nearestHigherYCoord, distFromThisYToNearestHigherY);
            }
            previousXCoord = thisXCoord;
            position++;  
        }
        
        List<String> mapKeys = new ArrayList<>(distanceMap.keySet());
        List<Integer> mapValues = new ArrayList<>(distanceMap.values());
        
        //sort distance map ascendingly
        Collections.sort(mapValues, new Comparator<Integer>() {
                @Override
                public int compare(Integer e1, Integer e2) {
                    return e1 - e2;
                }
            });
        
        LinkedHashMap<String, Integer> sortedDistMap = new LinkedHashMap<>();

        Iterator<Integer> valueIterator = mapValues.iterator();
        while (valueIterator.hasNext()) {
            Integer value = valueIterator.next();
            Iterator<String> keyIterator = mapKeys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Integer originalDistance = distanceMap.get(key);
                Integer sortedDistance = value;

                if (originalDistance == sortedDistance) {
                    keyIterator.remove();
                    sortedDistMap.put(key, value);
                    break;
                }
            }
        }
        
        return sortedDistMap;
    }
    
    //get minimum spanning tree using Kruskal
    public HashMap<String,ArrayList<String>> getMinimumSpanningMap (HashMap<String, Integer> sortedDistMap, ArrayList<ArrayList<String>> subgraphPartitioningResult) {
        HashMap<String,ArrayList<String>> minimumSpanningMap = new HashMap<>();
        Iterator distMapIterator = sortedDistMap.keySet().iterator();
        
        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);
        
        while(distMapIterator.hasNext() && nodesWithinBorder.size() > 0) {
            String thisPairOfNodes = (String) distMapIterator.next();
            String[] thisPairOfNodesArr = thisPairOfNodes.split("-");
            String thisNode = thisPairOfNodesArr[0];
            String anotherNode = thisPairOfNodesArr[1];
            
            Integer thisDist = sortedDistMap.get(thisPairOfNodes);
            ArrayList<String> thisNodeNeighborList = minimumSpanningMap.get(thisNode);
            ArrayList<String> anotherNodeNeighborList = minimumSpanningMap.get(anotherNode);
            
            if (thisNodeNeighborList == null) {
                thisNodeNeighborList = new ArrayList<>();
            }
            
            if (anotherNodeNeighborList == null) {
                anotherNodeNeighborList = new ArrayList<>();
            }
            
            thisNodeNeighborList.add(anotherNode);
            anotherNodeNeighborList.add(thisNode);
            
            minimumSpanningMap.put(thisNode, thisNodeNeighborList);
            minimumSpanningMap.put(anotherNode, anotherNodeNeighborList);
            
            nodesWithinBorder.remove(thisNode);
            nodesWithinBorder.remove(anotherNode);
        } 
        return minimumSpanningMap;
    }
}
