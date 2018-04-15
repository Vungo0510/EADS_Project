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
    
    private double distOfOneUnitOfYCoordInMeters = 0.9;
    private double distOfOneUnitOfXCoordInMeters = 1.425;
    
    //get the time among all the nodes in subgraphMap. For each pair of node A and B with xA < xB OR yA < yB, ONLY STORES THE DISTANCE FROM A to B AND NOT B TO A
    public HashMap<String, Double> getTimeAmongNodes(HashMap<Double, ArrayList<String>> subgraphMap, ArrayList<ArrayList<String>> subgraphPartitioningResult, double mheTravelTime, double mheLiftingTime) {
        //ArrayList<String> minimumSpanningTree = new ArrayList<>();
        HashMap<String, Double> timeMap = new HashMap<>();
        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);
        HashMap<Double, ArrayList<Double>> zCoordinatesAtThisX = new HashMap<>();
        
        //System.out.println(subgraphMap);
        //System.out.println("CORNER NODES:" + cornerNodesWithinBorder);
        //this subgraph contains the corner nodes (after subgraph partitioning) and the pick item nodes
        System.out.println("subgraph map: ");
        System.out.println(subgraphMap);
        Iterator<Double> subGraphIter = subgraphMap.keySet().iterator();
        int position = 0; //denote the index of element inside key set of this HashMap
        double previousXCoord = -1.0;
        HashMap<Double, Double> previousYToPreviousZMap = new HashMap<>();
        //set the time to -2 so that it won't clash with the condition of Math.abs(timeFromPreviousXToThisX) = 1 below
        double timeFromPreviousXToThisX = -2.0;
        double timeFromThisYZToNearestHigherYZ = -1.0;

        while (subGraphIter.hasNext()) {
            HashMap<Double, Double> thisYToThisZMap = new HashMap<>();
            
            Double thisXCoord = subGraphIter.next();
            if (position != 0) {
                timeFromPreviousXToThisX = Math.abs(thisXCoord - previousXCoord) * distOfOneUnitOfXCoordInMeters * mheTravelTime;
            }

            ArrayList<String> yzCoordArrOfThisXCoord = subgraphMap.get(thisXCoord);
            //Double previousYCoordForThisX = 0.0;
            //Double previousZCoordForThisX = 0.0;
            //System.out.println("prev Y to Z map: " + previousYToPreviousZMap);
            for (int i = 0; i < yzCoordArrOfThisXCoord.size() - 1; i++) {
                String yzCoord = yzCoordArrOfThisXCoord.get(i);
                Double yCoord = Double.parseDouble(yzCoord.split(",")[0]);
                Double zCoord = Double.parseDouble(yzCoord.split(",")[1]);
                thisYToThisZMap.put(yCoord, zCoord);
                
                String nearestHigherYZCoord = yzCoordArrOfThisXCoord.get(i + 1);
                Double nearestHigherYCoord = Double.parseDouble(nearestHigherYZCoord.split(",")[0]);
                Double nearestHigherZCoord = Double.parseDouble(nearestHigherYZCoord.split(",")[1]);
                
                timeFromThisYZToNearestHigherYZ = (Math.abs(nearestHigherYCoord - yCoord) * distOfOneUnitOfYCoordInMeters) * mheTravelTime + (zCoord + nearestHigherZCoord) * mheLiftingTime;
                //put the horizontal time from (previous X coordinate, this Y coordinate) to (this X coordinate, this Y coordinate)
                if (position != 0) {

                    //if (previous X coordinate, y coordinate, 0.0) is a corner node, that node and this node (x coordinate, y coordinate, 0.0) are eligible to be neighbors 
                    if (cornerNodesWithinBorder.contains(previousXCoord + "," + yCoord)) {
                        timeMap.put(previousXCoord + "," + yCoord + ",0.0-" + thisXCoord + "," + yCoord + ",0.0", timeFromPreviousXToThisX);
                    
                    //if previous X and this X are adjacent, these 2 nodes are neighbors and they are eligible to be put in the map. The time is 1 unit of X + sum of 2 Z coordinates
                    } else if (Math.abs(timeFromPreviousXToThisX) == 1) {
                        Double previousZCoord = previousYToPreviousZMap.get(yCoord);
                        timeMap.put(previousXCoord + "," + yCoord + "," + previousZCoord + thisXCoord + "," + yCoord + "," + zCoord, distOfOneUnitOfXCoordInMeters * mheTravelTime + (zCoord + previousZCoord) * mheLiftingTime);
                        
                    }

                    //if we are at the second highest Y coordinate, put in the time from (previous X coordinate, highest Y coordinate) to (this X coordinate, highest Y coordinate) as well cos we won't be iterating through the highest Y coordinate
                    //we only do so if (previous X coordinate, y coordinate) is a corner node OR if previous X and this X are adjacent, these 2 nodes are neighbors and thus their time is eligible to be put in the map
                    if (i == yzCoordArrOfThisXCoord.size() - 2 && (cornerNodesWithinBorder.contains(previousXCoord + "," + nearestHigherYCoord) || Math.abs(timeFromPreviousXToThisX) == 1)) {
                        Double previousHighestZCoord = previousYToPreviousZMap.get(nearestHigherYCoord);
                        //System.out.println("nearest higher y coord: " + nearestHigherYCoord + "--- previous highest z coord: " + previousHighestZCoord);
                        timeMap.put(previousXCoord + "," + nearestHigherYCoord + "," + previousHighestZCoord + "-" + thisXCoord + "," + nearestHigherYCoord + "," + nearestHigherZCoord, Math.abs(previousXCoord - thisXCoord)*distOfOneUnitOfXCoordInMeters * mheTravelTime + (previousHighestZCoord + nearestHigherZCoord) * mheLiftingTime);
                    }
                }

                //put the vertical time from (this X coordinate, this Y coordinate) to (this X coordinate, nearest higher Y coordinate)
                timeMap.put(thisXCoord + "," + yCoord + "," + zCoord +"-" + thisXCoord + "," + nearestHigherYCoord + "," + nearestHigherZCoord, timeFromThisYZToNearestHigherYZ);
            }
            String yzCoord = yzCoordArrOfThisXCoord.get(yzCoordArrOfThisXCoord.size() - 1);
            Double lastYCoord = Double.parseDouble(yzCoord.split(",")[0]);
            Double lastZCoord = Double.parseDouble(yzCoord.split(",")[1]);
            thisYToThisZMap.put(lastYCoord, lastZCoord);
            
            previousXCoord = thisXCoord;
            previousYToPreviousZMap = thisYToThisZMap;
            //previousZCoord = zCoord;
            position++;
        }

        List<String> mapKeys = new ArrayList<>(timeMap.keySet());
        List<Double> mapValues = new ArrayList<>(timeMap.values());

        //sort time map ascendingly
        Collections.sort(mapValues, new Comparator<Double>() {
            @Override
            public int compare(Double e1, Double e2) {
                return e1.intValue() - e2.intValue();
            }
        });

        LinkedHashMap<String, Double> sortedTimeMap = new LinkedHashMap<>();

        Iterator<Double> valueIterator = mapValues.iterator();
        while (valueIterator.hasNext()) {
            Double value = valueIterator.next();
            Iterator<String> keyIterator = mapKeys.iterator();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                Double originalTime = timeMap.get(key);
                Double sortedTime = value;

                if (originalTime == sortedTime) {
                    keyIterator.remove();
                    sortedTimeMap.put(key, value);
                    break;
                }
            }
        }

        return sortedTimeMap;
    }

    //get minimum spanning tree using Kruskal
    public HashMap<String, ArrayList<String>> getMinimumSpanningMap(HashMap<String, Double> sortedTimeMap, ArrayList<ArrayList<String>> subgraphPartitioningResult) {
        HashMap<String, ArrayList<String>> minimumSpanningMap = new HashMap<>();
        Iterator timeMapIterator = sortedTimeMap.keySet().iterator();

        ArrayList<String> cornerNodesWithinBorder = subgraphPartitioningResult.get(0);
        ArrayList<String> nodesWithinBorder = subgraphPartitioningResult.get(1);
        ArrayList<String> originalNodesWithinBorder = new ArrayList<>(nodesWithinBorder);

        //An ArrayList that contains strings, with each string representing a "Tree". If 2 nodes appear in the same string it means that it's possible to reach one node from the other node
        ArrayList<String> treesOfNodes = new ArrayList<String>();

        //boolean to check if any string ("tree") contains all nodes within the subgraph (corner nodes + pick nodes)
        boolean treeWithAllNodesExist = false;

        while (timeMapIterator.hasNext() && !treeWithAllNodesExist) {//nodesWithinBorder.size() > 0) {
            String thisPairOfNodes = (String) timeMapIterator.next();
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

            Double thisTime = sortedTimeMap.get(thisPairOfNodes);
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

    public TreeMap getMinimumSpanningTree(HashMap<String, ArrayList<String>> minimumSpanningMap, String startingPoint, ArrayList<HashMap> initialSolution, ArrayList<HashMap> ptToPtRouteAndTimeArr, HashMap<String, Double> pickItemCapacityMap, Double mheCapacity, Double mheTravelTime, Double mheLiftingTime) {
        ArrayList<String> minimumSpanningTree = new ArrayList<>();
        Iterator nodesOfTreeArrIter = minimumSpanningMap.keySet().iterator();
        
        HashMap timeFromStartPtToAllPt = initialSolution.get(0);
        HashMap originalTimeFromStartPtToAllPt = new HashMap<>(timeFromStartPtToAllPt);
        HashMap routeFromStartPtToAllPt = initialSolution.get(1);
        
        HashMap timeAmongPickNodes = ptToPtRouteAndTimeArr.get(0);
        HashMap routeAmongPickNodes = ptToPtRouteAndTimeArr.get(1);
        
        Set pickItemSet = timeFromStartPtToAllPt.keySet();
        
        String[] startPtSplit = startingPoint.split(",");

        //find the corner node/pick node that is closest to starting point and let it be the tree's starting point
        double startPtXCoord = Double.parseDouble(startPtSplit[0]);
        double startPtYCoord = Double.parseDouble(startPtSplit[1]);

        double xCoordOfPtNearestToStartPt = Double.MAX_VALUE;
        double yCoordOfPtNearestToStartPt = Double.MAX_VALUE;
        double zCoordOfPtNearestToStartPt = Double.MAX_VALUE;
        
        double minDistFromStartPtToANode = Double.MAX_VALUE;
        
        while (nodesOfTreeArrIter.hasNext()) {
            String thisNode = (String) nodesOfTreeArrIter.next();
            String[] thisNodeSplit = thisNode.split(",");
            double thisNodeXCoord = Double.parseDouble(thisNodeSplit[0]);
            double thisNodeYCoord = Double.parseDouble(thisNodeSplit[1]);
            double thisNodeZCoord = Double.parseDouble(thisNodeSplit[2]);
            
            double distFromStartPtToThisNode = Math.abs(thisNodeXCoord - startPtXCoord)*distOfOneUnitOfXCoordInMeters + Math.abs(thisNodeYCoord - startPtYCoord)*distOfOneUnitOfYCoordInMeters + zCoordOfPtNearestToStartPt;
            if (distFromStartPtToThisNode <= minDistFromStartPtToANode) {
                xCoordOfPtNearestToStartPt = thisNodeXCoord;
                yCoordOfPtNearestToStartPt = thisNodeYCoord;
                zCoordOfPtNearestToStartPt = thisNodeZCoord;
            }
        }

        String ptNearestToStartPt = xCoordOfPtNearestToStartPt + "," + yCoordOfPtNearestToStartPt + "," + zCoordOfPtNearestToStartPt;
        ArrayList<String> interimSol = new ArrayList<>();
        HashMap<String, ArrayList<String>> neighborsMap = new HashMap<>();
        minimumSpanningTree = recursiveDFSTraversal(ptNearestToStartPt, interimSol, neighborsMap, minimumSpanningMap, pickItemSet);
        ArrayList<String> originalMinSpanTree = new ArrayList<>(minimumSpanningTree);
        
        for (String node : originalMinSpanTree) {
            String[] nodeSplit = node.split(",");
            //Check 
            if (nodeSplit[2].contains("0.0")) { //if this node is a corner node, remove it
                minimumSpanningTree.remove(node);
            }
        }
        
        TreeMap<String, Double> finalRoutesTimeMap = new TreeMap<>();
        double totalTime = 0.0;
        double totalCapacityOfThisRoute = 0.0;
        String thisFinalRoute = "";
        boolean newRoute = true;
        String startNodeOfThisRoute = startingPoint;
        
        System.out.println("pick item capacity map: " + pickItemCapacityMap);
        System.out.println("min span tree: " + minimumSpanningTree);
        
        for (int i = 0; i < minimumSpanningTree.size() - 1; i++) {
            String pickNode = minimumSpanningTree.get(i);
            String nextPickNode = minimumSpanningTree.get(i+1);
            double pickNodeCapacity = pickItemCapacityMap.get(pickNode);
            double nextPickNodeCapacity = pickItemCapacityMap.get(nextPickNode);
            
            if (newRoute) {
                if (pickNodeCapacity <= mheCapacity) {
                    if (pickNodeCapacity == 0) {
                        pickNodeCapacity++;
                    }
                    totalCapacityOfThisRoute += pickNodeCapacity;
                }
            }
            
            if (totalCapacityOfThisRoute + nextPickNodeCapacity <= mheCapacity) {
                finalRoutesTimeMap.remove(thisFinalRoute);
                newRoute = false;
                if (nextPickNodeCapacity == 0) {
                    nextPickNodeCapacity++;
                }
                totalCapacityOfThisRoute += nextPickNodeCapacity;
                
                double timeFromThisNodeToNextPickNode = 0.0;
                
                if (timeAmongPickNodes.get(pickNode + "to" + nextPickNode) == null) {
                    timeFromThisNodeToNextPickNode = (double) timeAmongPickNodes.get(nextPickNode + "to" + pickNode);
                    
                } else {
                    timeFromThisNodeToNextPickNode = (double) timeAmongPickNodes.get(pickNode + "to" + nextPickNode);
                }
                totalTime += timeFromThisNodeToNextPickNode;

                String routeFromThisNodeToNextPickNode = (String) routeAmongPickNodes.get(pickNode  + "to" + nextPickNode);

                //if we can't retrieve the route, we need to flip the key, and then flip the route that we retrieve
                if (routeFromThisNodeToNextPickNode == null) {
                    routeFromThisNodeToNextPickNode = (String) routeAmongPickNodes.get(nextPickNode  + "to" + pickNode);
                    String[] routeFromThisNodeToNextPickNodeSplit = routeFromThisNodeToNextPickNode.split("-");
                    routeFromThisNodeToNextPickNode = "";
                    for (int index = routeFromThisNodeToNextPickNodeSplit.length -1 ; index >= 0; index--) {
                        if (index != 0) {
                            routeFromThisNodeToNextPickNode += routeFromThisNodeToNextPickNodeSplit[index] + "-";
                        } else {
                            routeFromThisNodeToNextPickNode += routeFromThisNodeToNextPickNodeSplit[index];
                        }
                    }
                }
                //System.out.println("Pick path from " + pickNode + "to" + nextPickNode + ": " + routeFromThisNodeToNextPickNode);
                if (i != minimumSpanningTree.size() - 2) {
                    //System.out.println("this route before cut: " + routeFromThisNodeToNextPickNode);
                    routeFromThisNodeToNextPickNode = routeFromThisNodeToNextPickNode.substring(0, routeFromThisNodeToNextPickNode.lastIndexOf("-"));
                    //System.out.println("this route: " + routeFromThisNodeToNextPickNode);
                }

                thisFinalRoute += routeFromThisNodeToNextPickNode + "-";  
                finalRoutesTimeMap.put(thisFinalRoute, totalTime);
                //System.out.println("final route so far: " + thisFinalRoute);
                System.out.println("this node:" + pickNode +" -- Capacity of this node: " + pickNodeCapacity + "-- Capacity of next node: " + nextPickNodeCapacity + "-- This route capacity so far: " + totalCapacityOfThisRoute);
            } else {
                finalRoutesTimeMap.remove(thisFinalRoute);
                System.out.println("this node:" + pickNode +" -- Capacity of this node: " + pickNodeCapacity + "-- Capacity of next node: " + nextPickNodeCapacity + "-- This route capacity so far: " + totalCapacityOfThisRoute);
                System.out.println("This route capacity final: " + totalCapacityOfThisRoute);
                if (!thisFinalRoute.equals("")) {
                    String[] thisFinalRouteSplit = thisFinalRoute.split("-");
                    String firstPickNode = thisFinalRouteSplit[0];
                    String[] lastPickNodeSplit = thisFinalRouteSplit[thisFinalRouteSplit.length - 1].split(",");

                    Double lastPickNodeXCoord = Double.parseDouble(lastPickNodeSplit[0]);
                    Double lastPickNodeYCoord = Double.parseDouble(lastPickNodeSplit[1]);
                    Double lastPickNodeZCoord = Double.parseDouble(lastPickNodeSplit[2]);

                    //add the time and route to travel from starting pt to first pick node
                    System.out.println("first pick node of this node: " + firstPickNode);
                    System.out.println("time frm start pt map: " + originalTimeFromStartPtToAllPt);
                    String routeFromStartToFirstPickNode = "";

                    if (startNodeOfThisRoute.equals(startingPoint)) {
                        System.out.println("GOT INTO SMALLER IF");
                        totalTime += (double) originalTimeFromStartPtToAllPt.get(firstPickNode);
                        routeFromStartToFirstPickNode = (String) routeFromStartPtToAllPt.get(firstPickNode);
                        routeFromStartToFirstPickNode = routeFromStartToFirstPickNode.substring(0, routeFromStartToFirstPickNode.lastIndexOf("-"));
                    } else {
                        //add the time and route to travel from start node to first pick node
                        System.out.println("GOT INTO SMALLER ELSE");

                        String[] firstPickNodeSplit = firstPickNode.split(",");
                        Double firstPickNodeXCoord = Double.parseDouble(firstPickNodeSplit[0]);
                        Double firstPickNodeYCoord = Double.parseDouble(firstPickNodeSplit[1]);
                        Double firstPickNodeZCoord = Double.parseDouble(firstPickNodeSplit[2]);

                        String[] startNodeSplit = startNodeOfThisRoute.split(",");
                        Double startNodeXCoord = Double.parseDouble(startNodeSplit[0]);
                        Double startNodeYCoord = Double.parseDouble(startNodeSplit[1]);
                        Double startNodeZCoord = Double.parseDouble(startNodeSplit[2]);

                        Double distFromStartNodeToFirstPickNode = (Math.abs(firstPickNodeXCoord - startNodeXCoord)*distOfOneUnitOfXCoordInMeters + Math.abs(firstPickNodeYCoord - startNodeYCoord)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + firstPickNodeZCoord * mheLiftingTime;
                        totalTime += distFromStartNodeToFirstPickNode;
                        routeFromStartToFirstPickNode = startNodeOfThisRoute + "-" + firstPickNodeXCoord + "," + startNodeYCoord + "," + startNodeZCoord + "-" + firstPickNode;
                    }
                    System.out.println("Pick path from " + startNodeOfThisRoute + "to" + firstPickNode + ": " + routeFromStartToFirstPickNode);

                    //add the time and route to travel from last pick node to last node
                    String lastNode = lastPickNodeXCoord + "," + "1.0,0.0";
                    totalTime += (Math.abs(lastPickNodeYCoord - 1.0) * distOfOneUnitOfYCoordInMeters * mheTravelTime + lastPickNodeZCoord * mheLiftingTime);
                    System.out.println("Pick path from " + thisFinalRouteSplit[thisFinalRouteSplit.length - 1] + "to" + lastNode + ": " + routeFromStartToFirstPickNode);

                    //System.out.println("route from start to pick node: " + routeFromStartToFirstPickNode);
                    thisFinalRoute = routeFromStartToFirstPickNode + "-" + thisFinalRoute + lastNode;

                    System.out.println("this route " + thisFinalRoute + " ---- TATT time for this route: " + totalTime);


                    finalRoutesTimeMap.put(thisFinalRoute,totalTime);
                    
                    startNodeOfThisRoute = lastNode;
                    
                    //create a new route just for the last item if we happen to end the route on the last iteration
                    if (i == minimumSpanningTree.size() - 2 && nextPickNodeCapacity <= mheCapacity) {
                        String[] nextPickNodeSplit = nextPickNode.split(",");
                        Double nextPickNodeXCoord = Double.parseDouble(nextPickNodeSplit[0]);
                        Double nextPickNodeYCoord = Double.parseDouble(nextPickNodeSplit[1]);
                        Double nextPickNodeZCoord = Double.parseDouble(nextPickNodeSplit[2]);

                        String[] startNodeSplit = startNodeOfThisRoute.split(",");
                        Double startNodeXCoord = Double.parseDouble(startNodeSplit[0]);
                        Double startNodeYCoord = Double.parseDouble(startNodeSplit[1]);
                        Double startNodeZCoord = Double.parseDouble(startNodeSplit[2]);

                        Double distFromStartNodeToNextPickNode = (Math.abs(nextPickNodeXCoord - startNodeXCoord)*distOfOneUnitOfXCoordInMeters + Math.abs(nextPickNodeYCoord - startNodeYCoord)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + nextPickNodeZCoord * mheLiftingTime;
                        totalTime = distFromStartNodeToNextPickNode;
                        routeFromStartToFirstPickNode = startNodeOfThisRoute + "-" + nextPickNodeXCoord + "," + startNodeYCoord + "," + startNodeZCoord + "-" + nextPickNode;
                    
                        lastNode = nextPickNodeXCoord + "," + "1.0,0.0";
                        totalTime += (Math.abs(nextPickNodeYCoord - 1.0) * distOfOneUnitOfYCoordInMeters * mheTravelTime + nextPickNodeZCoord * mheLiftingTime);
                        System.out.println("Pick path from " + thisFinalRouteSplit[thisFinalRouteSplit.length - 1] + "to" + lastNode + ": " + routeFromStartToFirstPickNode);

                        //System.out.println("route from start to pick node: " + routeFromStartToFirstPickNode);
                        thisFinalRoute = routeFromStartToFirstPickNode + "-" + lastNode;
                        finalRoutesTimeMap.put(thisFinalRoute,totalTime);
                    } else {
                        newRoute = true;
                        totalCapacityOfThisRoute = 0;
                        thisFinalRoute = "";
                        totalTime = 0.0;
                    }        
                }
            }
            
        }
        Iterator finalRoutesDescIter = finalRoutesTimeMap.descendingKeySet().iterator();
        String lastRoute = "";
        while (finalRoutesDescIter.hasNext()) {
            String thisRoute = (String) finalRoutesDescIter.next();
            if (thisRoute.charAt(thisRoute.length() - 1) == '-') {
                lastRoute = thisRoute;
                break;
            }
        }
        
        System.out.println("final routes time map: " + finalRoutesTimeMap);
        System.out.println("final routes time map desc keyset: " + finalRoutesTimeMap.descendingKeySet());
        
        if (!lastRoute.equals("")) {
            System.out.println("last route raw: " + lastRoute);
            String[] lastRouteSplit = lastRoute.split("-");
            String firstPickNode = lastRouteSplit[0];
            String[] lastPickNodeSplit = lastRouteSplit[lastRouteSplit.length - 1].split(",");
            Double lastRouteTime = finalRoutesTimeMap.get(lastRoute);

            Double lastPickNodeXCoord = Double.parseDouble(lastPickNodeSplit[0]);
            Double lastPickNodeYCoord = Double.parseDouble(lastPickNodeSplit[1]);
            Double lastPickNodeZCoord = Double.parseDouble(lastPickNodeSplit[2]);

            //add the time and route to travel from starting pt to first pick node
            String routeFromStartToFirstPickNode = "";

            if (startNodeOfThisRoute.equals(startingPoint)) {
                totalTime += (double) originalTimeFromStartPtToAllPt.get(firstPickNode);
                routeFromStartToFirstPickNode = (String) routeFromStartPtToAllPt.get(firstPickNode);
                routeFromStartToFirstPickNode = routeFromStartToFirstPickNode.substring(0, routeFromStartToFirstPickNode.lastIndexOf("-"));
            } else {
                //add the time and route to travel from start node to first pick node

                String[] firstPickNodeSplit = firstPickNode.split(",");
                Double firstPickNodeXCoord = Double.parseDouble(firstPickNodeSplit[0]);
                Double firstPickNodeYCoord = Double.parseDouble(firstPickNodeSplit[1]);
                Double firstPickNodeZCoord = Double.parseDouble(firstPickNodeSplit[2]);

                String[] startNodeSplit = startNodeOfThisRoute.split(",");
                Double startNodeXCoord = Double.parseDouble(startNodeSplit[0]);
                Double startNodeYCoord = Double.parseDouble(startNodeSplit[1]);
                Double startNodeZCoord = Double.parseDouble(startNodeSplit[2]);

                Double distFromStartNodeToFirstPickNode = (Math.abs(firstPickNodeXCoord - startNodeXCoord)*distOfOneUnitOfXCoordInMeters + Math.abs(firstPickNodeYCoord - startNodeYCoord)*distOfOneUnitOfYCoordInMeters) * mheTravelTime + firstPickNodeZCoord * mheLiftingTime;
                routeFromStartToFirstPickNode = startNodeOfThisRoute + "-" + firstPickNodeXCoord + "," + startNodeYCoord + "," + startNodeZCoord + "-" + firstPickNode;
            }

            //add the time and route to travel from last pick node to last node
            String lastNode = lastPickNodeXCoord + "," + "1.0,0.0";
            lastRouteTime += (Math.abs(lastPickNodeYCoord - 1.0) * distOfOneUnitOfYCoordInMeters * mheTravelTime + lastPickNodeZCoord * mheLiftingTime);

            finalRoutesTimeMap.remove(lastRoute);
            //System.out.println("route from start to pick node: " + routeFromStartToFirstPickNode);
            lastRoute = routeFromStartToFirstPickNode + "-" + lastRoute + lastNode;
            System.out.println("last route after edit: " + lastRoute);
            finalRoutesTimeMap.put(lastRoute, lastRouteTime);
        
        }
        
        
        return finalRoutesTimeMap;
    }

    public ArrayList<String> recursiveDFSTraversal(String currentNode, ArrayList<String> interimSolution, HashMap<String, ArrayList<String>> neighborsMap, HashMap<String, ArrayList<String>> minimumSpanningMap, Set pickItemSet) {
        while(!pickItemSet.isEmpty()) {
            interimSolution.add(currentNode);
            /*boolean isLastIter = false;
            
            if (pickItemSet.size() == 1) {
                Iterator pickListSetIter = pickItemSet.iterator();
                String lastPickNode = (String) pickListSetIter.next();
                interimSolution.add(lastPickNode);
                pickItemSet.remove(lastPickNode);
                isLastIter = true;
            }
            System.out.println("line 346");
            if (!isLastIter) {*/
                ArrayList<String> currNodeNeighbor = minimumSpanningMap.get(currentNode);
                neighborsMap.put(currentNode, currNodeNeighbor); //this map contains parents nodes as keys and array lists of their neighbors as children
                boolean setNextNode = false;
                pickItemSet.remove(currentNode);

                for (String neighbor : currNodeNeighbor) {
                    if (!interimSolution.contains(neighbor)) {
                        setNextNode = true;
                        currentNode = neighbor;

                        break;
                    }
                }

                //if all neighbors of this node are already in the route, keep going back to previous nodes and see if we can travel to their neighbors
                if (!setNextNode) {
                    for (int i = interimSolution.size() - 1; i>= 0; i--) {
                        String thisNodeInSolution = interimSolution.get(i);
                        ArrayList<String> neighborsOfThisNodes = neighborsMap.get(thisNodeInSolution);
                        for (String thisNeighbor : neighborsOfThisNodes) {
                            if (!interimSolution.contains(thisNeighbor)) {
                                setNextNode = true;
                                currentNode = thisNeighbor;
                                break;
                            }
                        }
                    }
                }
                if (setNextNode) {
                    interimSolution = recursiveDFSTraversal(currentNode, interimSolution, neighborsMap, minimumSpanningMap, pickItemSet);
                }
            }
            //System.out.println("line 381");
        //}
        //System.out.println("before return" + interimSolution);
        return interimSolution;
    }
}
