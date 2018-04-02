/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Snow Petrel
 */
public class EADSProject_old {
     public static void main(String[] args) {
        
        //System.out.println(pickingList);
        String csvFile = "./Data/PickingList.csv";
        CSVReader csvReader = new CSVReader();
        ArrayList<String> pickingList = csvReader.readPickingList(csvFile);
        HashMap<Integer, ArrayList<Integer>> cornerNodesMap = csvReader.readAllCornerNodes("./Data/CornerNodes.csv");
        HashMap<String, Integer> pickItemCapacityMap = csvReader.readPickItemCapacity(csvFile);
        //SubgraphDesign subgraphDesign = new SubgraphDesign();
        //System.out.println(subgraphDesign.getPickingListCornerNodes(pickingList));
        System.out.println(pickingList);
        
        Clarke c = new Clarke();
        
        ArrayList<HashMap> intialSolution = c.getInitialSolution(pickingList, "1,3");
        HashMap<String, Integer> distOfStartPtToAllPt = intialSolution.get(0);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointDistance(pickingList);
        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0);
        
        HashMap<String, Integer> savingsMap = c.getSavingsMap(pickingList, "1,3");
        
        HashMap<String, String> solutionMap = c.getSolution(pickItemCapacityMap, savingsMap, 2.00, "1,3");
        
        ArrayList<String> finalRoutes = c.getFinalRoutes(solutionMap, "1,3");
        
        System.out.println("corner nodes: ");
        System.out.println(cornerNodesMap);
        System.out.println("Step 1: ");
        System.out.println(distOfStartPtToAllPt);
        System.out.println("Step 2: ");
        System.out.println(distAmongPickItems);
        System.out.println("Step 3: ");
        System.out.println(savingsMap);
        System.out.println("Step 4 and 5:");
        System.out.println(solutionMap);
        System.out.println("Step 5b:");
        System.out.println(finalRoutes);
     }
}
