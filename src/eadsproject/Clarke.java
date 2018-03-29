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

    private String startingPoint = "2,3"; //Store x,y coordinates of the starting point of the vehicle "x,y"

    public HashMap getInitialSolution(ArrayList<String> pickingList) {
        // this method calculates the distance from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        HashMap<String, Integer> distOfStartPtToAllPt = new HashMap<String, Integer>(); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node

        int xCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[0]);
        int yCoordinateOfStartPt = Integer.parseInt(startingPoint.split(",")[1]);

        for (String s : pickingList) {
            int xCoordinateOfPickItem = Integer.parseInt(s.split(",")[1]);
            int yCoordinateOfPickItem = Integer.parseInt(s.split(",")[2]);

            int distFromStartPtToPickItem = Math.abs(xCoordinateOfStartPt - xCoordinateOfPickItem) + Math.abs(yCoordinateOfStartPt - yCoordinateOfPickItem);
            distOfStartPtToAllPt.put(xCoordinateOfPickItem + "," + yCoordinateOfPickItem, distFromStartPtToPickItem);

        }
        return distOfStartPtToAllPt;
    }

    public HashMap getPointToPointDistance(ArrayList<String> pickingList) {
        // this method calculates the distance from starting pt to all pick nodes, pickingList passed in contains all the pick nodes
        HashMap<String, Integer> distAmongPickItems = new HashMap<String, Integer>(); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance

        for (int i = 0; i < pickingList.size(); i++) {
            String currentPickNode = pickingList.get(i);

            int xCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[1]);
            int yCoordinateOfCurrentPickNode = Integer.parseInt(currentPickNode.split(",")[2]);

            for (int j = i + 1; j < pickingList.size(); j++) {
                String otherPickNode = pickingList.get(j);

                int xCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[1]);
                int yCoordinateOfOtherPickNode = Integer.parseInt(otherPickNode.split(",")[2]);

                int distFromCurrentPickNodeToOtherPickNode = Math.abs(xCoordinateOfOtherPickNode - xCoordinateOfCurrentPickNode) + Math.abs(yCoordinateOfOtherPickNode - yCoordinateOfCurrentPickNode);
                distAmongPickItems.put(xCoordinateOfCurrentPickNode + "," + yCoordinateOfCurrentPickNode + "to" + xCoordinateOfOtherPickNode + "," + yCoordinateOfOtherPickNode, distFromCurrentPickNodeToOtherPickNode);
            }
        }
        return distAmongPickItems;
    }

    public HashMap getSavingsMap(ArrayList<String> pickingList) {
        HashMap<String, Integer> savingsMap = new HashMap<String, Integer>();

        HashMap<String, Integer> distOfStartPtToAllPt = getInitialSolution(pickingList);
        HashMap<String, Integer> distAmongPickItems = getPointToPointDistance(pickingList);

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

        return savingsMap;
    }
}
