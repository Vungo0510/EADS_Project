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
public class EADSProject {
     public static void main(String[] args) {
        
        //System.out.println(pickingList);
        String csvFile = "./Data/PickingList.csv";
        CSVReader csvReader = new CSVReader();
        ArrayList<String> pickingList = csvReader.readPickingList(csvFile);
        //SubgraphDesign subgraphDesign = new SubgraphDesign();
        //System.out.println(subgraphDesign.getPickingListCornerNodes(pickingList));
        
        Clarke c = new Clarke();
        
        ArrayList<HashMap> intialSolution = c.getInitialSolution(pickingList);
        HashMap<String, Integer> distOfStartPtToAllPt = intialSolution.get(0);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointDistance(pickingList);
        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0);
        
        HashMap<String, Integer> savingsMap = c.getSavingsMap(pickingList);
        
        HashMap<String, String> solutionMap = c.getSolution(savingsMap, 3000.00);
        
        HashMap<Integer, ArrayList<Integer>> cornerNodesMap = csvReader.readAllCornerNodes();
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
     }
}
