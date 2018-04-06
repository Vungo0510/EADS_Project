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
    public HashMap<String, Double> getTimeAmongNodes(HashMap<Double, ArrayList<Double>> subgraphMap, ArrayList<ArrayList<String>> subgraphPartitioningResult) {
        //ArrayList<String> minimumSpanningTree = new ArrayList<>();
        HashMap<String, Double> distanceMap = new HashMap<>();
        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);

        //System.out.println(subgraphMap);
        //System.out.println("CORNER NODES:" + cornerNodesWithinBorder);
        //this subgraph contains the corner nodes (after subgraph partitioning) and the pick item nodes
        System.out.println("BRRR: ");
        System.out.println(subgraphMap);
        Iterator<Double> subGraphIter = subgraphMap.keySet().iterator();
        int position = 0; //denote the index of element inside key set of this HashMap
        double previousXCoord = -1.0;

        //set the distance to -2 so that it won't clash with the condition of Math.abs(distFromPreviousXToThisX) = 1 below
        double distFromPreviousXToThisX = -2.0;
        double distFromThisYToNearestHigherY = -1.0;

        while (subGraphIter.hasNext()) {
            Double thisXCoord = subGraphIter.next();
            if (position != 0) {
                distFromPreviousXToThisX = thisXCoord - previousXCoord;
            }

            ArrayList<Double> yCoordArrOfThisXCoord = subgraphMap.get(thisXCoord);

            for (int i = 0; i < yCoordArrOfThisXCoord.size() - 1; i++) {
                Double yCoord = yCoordArrOfThisXCoord.get(i);
                Double nearestHigherYCoord = yCoordArrOfThisXCoord.get(i + 1);
                distFromThisYToNearestHigherY = nearestHigherYCoord - yCoord;

                //put the horizontal distance from (previous X coordinate, this Y coordinate) to (this X coordinate, this Y coordinate)
                if (position != 0) {

                    //if (previous X coordinate, y coordinate) is a corner node OR if previous X and this X are adjacent, these 2 nodes are neighbors and thus their distance is eligible to be put in the map
                    if (cornerNodesWithinBorder.contains(previousXCoord + "," + yCoord) || Math.abs(distFromPreviousXToThisX) == 1) {
                        distanceMap.put(previousXCoord + "," + yCoord + "-" + thisXCoord + "," + yCoord, distFromPreviousXToThisX);
                    }

                    //if we are at the second highest Y coordinate, put in the distance from (previous X coordinate, highest Y coordinate) to (this X coordinate, highest Y coordinate) as well cos we won't be iterating through the highest Y coordinate
                    //we only do so if (previous X coordinate, y coordinate) is a corner node OR if previous X and this X are adjacent, these 2 nodes are neighbors and thus their distance is eligible to be put in the map
                    if (i == yCoordArrOfThisXCoord.size() - 2 && (cornerNodesWithinBorder.contains(previousXCoord + "," + nearestHigherYCoord) || Math.abs(distFromPreviousXToThisX) == 1)) {
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
        List<Double> mapValues = new ArrayList<>(distanceMap.values());

        //sort distance map ascendingly
        Collections.sort(mapValues, new Comparator<Double>() {
            @Override
            public int compare(Double e1, Double e2) {
                return e1.intValue() - e2.intValue();
            }
        });

        LinkedHashMap<String, Double> sortedDistMap = new LinkedHashMap<>();

        Iterator<Double> valueIterator = mapValues.iterator();
        while (valueIterator.hasNext()) {
            Double value = valueIterator.next();
            Iterator<String> keyIterator = mapKeys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Double originalDistance = distanceMap.get(key);
                Double sortedDistance = value;

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
    public HashMap<String, ArrayList<String>> getMinimumSpanningMap(HashMap<String, Double> sortedDistMap, ArrayList<ArrayList<String>> subgraphPartitioningResult) {
        HashMap<String, ArrayList<String>> minimumSpanningMap = new HashMap<>();
        Iterator distMapIterator = sortedDistMap.keySet().iterator();

        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);
        ArrayList<String> originalNodesWithinBorder = new ArrayList<>(nodesWithinBorder);

        //An ArrayList that contains strings, with each string representing a "Tree". If 2 nodes appear in the same string it means that it's possible to reach one node from the other node
        ArrayList<String> treesOfNodes = new ArrayList<String>();

        //boolean to check if any string ("tree") contains all nodes within the subgraph (corner nodes + pick nodes)
        boolean treeWithAllNodesExist = false;

        while (distMapIterator.hasNext() && !treeWithAllNodesExist) {//nodesWithinBorder.size() > 0) {
            String thisPairOfNodes = (String) distMapIterator.next();
            String[] thisPairOfNodesArr = thisPairOfNodes.split("-");
            String thisNode = thisPairOfNodesArr[0];
            String anotherNode = thisPairOfNodesArr[1];

            int thisNodeArrIndex = -1;
            int anotherNodeArrIndex = -1;

            for (int i = 0; i < treesOfNodes.size(); i++) {
                String thisTree = treesOfNodes.get(i);

                int numberOfDash = 0;
                for (int j = 0; j < thisTree.length(); j++) {
                    if (thisTree.substring(j, j + 1).equals("-")) {
                        numberOfDash++;
                    }
                }

                if (numberOfDash == (originalNodesWithinBorder.size() - 1)) {
                    //System.out.println("num of dash: " + numberOfDash);
                    //System.out.println("num of nodes in total: " + originalNodesWithinBorder.size());
                    treeWithAllNodesExist = true;
                    break;
                }

                if (thisTree.contains(thisNode)) {
                    thisNodeArrIndex = i;
                }

                if (thisTree.contains(anotherNode)) {
                    anotherNodeArrIndex = i;
                }
            }

            if (thisNodeArrIndex == -1 && anotherNodeArrIndex == -1) { //if can't find both node, create a new tree in the array

                treesOfNodes.add(thisNode + "-" + anotherNode);

            } else if (thisNodeArrIndex == -1) { //if found another node but not this node, add this node to another node's tree
                String anotherNodeTree = treesOfNodes.get(anotherNodeArrIndex);
                anotherNodeTree += "-" + thisNode;
                treesOfNodes.set(anotherNodeArrIndex, anotherNodeTree);

            } else if (anotherNodeArrIndex == -1) { //if found another node but not this node, add this node to another node's tree
                String thisNodeTree = treesOfNodes.get(thisNodeArrIndex);
                thisNodeTree += "-" + anotherNode;
                treesOfNodes.set(thisNodeArrIndex, thisNodeTree);

            } else if (anotherNodeArrIndex != thisNodeArrIndex) { //if the 2 nodes belong to 2 different trees, merge them and keep the one with lower index
                String thisNodeTree = treesOfNodes.get(thisNodeArrIndex);
                String anotherNodeTree = treesOfNodes.get(anotherNodeArrIndex);

                if (anotherNodeArrIndex < thisNodeArrIndex) {
                    anotherNodeTree += "-" + thisNodeTree;
                    treesOfNodes.set(anotherNodeArrIndex, anotherNodeTree);
                    treesOfNodes.remove(thisNodeArrIndex);

                } else {
                    thisNodeTree += "-" + anotherNodeTree;
                    treesOfNodes.set(thisNodeArrIndex, thisNodeTree);
                    treesOfNodes.remove(anotherNodeArrIndex);
                }

            }

            Double thisDist = sortedDistMap.get(thisPairOfNodes);
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
        //System.out.println("Tree of nodes: " + treesOfNodes);
        return minimumSpanningMap;
    }

    public ArrayList<String> getMinimumSpanningTree(HashMap<String, ArrayList<String>> minimumSpanningMap, String startingPoint) {
        ArrayList<String> minimumSpanningTree = new ArrayList<>();
        String[] nodesOfTreeArr = (String[]) minimumSpanningMap.keySet().toArray();
        String[] originalNodesOfTreeArr = new String[nodesOfTreeArr.length];
        originalNodesOfTreeArr = nodesOfTreeArr.clone();

        String[] startPtSplit = startingPoint.split(",");

        //find the corner node/pick node that is closest to starting point and let it be the tree's starting point
        double startPtXCoord = Double.parseDouble(startPtSplit[0]);
        double startPtYCoord = Double.parseDouble(startPtSplit[1]);

        double xCoordOfPtNearestToStartPt = Integer.MAX_VALUE;
        double yCoordOfPtNearestToStartPt = Integer.MAX_VALUE;

        for (int i = 0; i < nodesOfTreeArr.length; i++) {
            String[] thisNodeSplit = nodesOfTreeArr[i].split(",");
            double thisNodeXCoord = Double.parseDouble(thisNodeSplit[0]);
            double thisNodeYCoord = Double.parseDouble(thisNodeSplit[1]);

            if (Math.abs(thisNodeXCoord - startPtXCoord) <= Math.abs(xCoordOfPtNearestToStartPt - startPtXCoord)) {

                if (Math.abs(thisNodeYCoord - startPtYCoord) < Math.abs(yCoordOfPtNearestToStartPt - startPtYCoord)) {

                    xCoordOfPtNearestToStartPt = thisNodeXCoord;
                    yCoordOfPtNearestToStartPt = thisNodeYCoord;
                }
            }
        }

        String ptNearestToStartPt = xCoordOfPtNearestToStartPt + "," + yCoordOfPtNearestToStartPt;
        ArrayList<String> interimSol = new ArrayList<>();
        HashMap<String, ArrayList<String>> parentsMap = new HashMap<>();
        minimumSpanningTree = recursiveDFSTraversal(originalNodesOfTreeArr, ptNearestToStartPt, interimSol, parentsMap);
        return minimumSpanningTree;
    }

    public ArrayList<String> recursiveDFSTraversal(String[] nodesOfTreeArr, String ptNearestToStartPt, ArrayList<String> interimSolution, HashMap<String, ArrayList<String>> parentsMap) {
        while (nodesOfTreeArr.length > 0) {
            String currentNode = "";
            
            if (interimSolution.size() == 0) { //if this is the first iteration
                currentNode = ptNearestToStartPt;
            }
            
            interimSolution.add(currentNode);
            
        }
        return interimSolution;
    }
}
