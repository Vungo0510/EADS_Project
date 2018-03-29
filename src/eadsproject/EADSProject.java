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
        SubgraphDesign subgraphDesign = new SubgraphDesign();
        //System.out.println(subgraphDesign.subgraphPartitioning());
        //System.out.println(pickingList);
        String csvFile = "./Data/PickingList.csv";
        CSVReader csvReader = new CSVReader();
        ArrayList<String> pickingList = csvReader.readPickingList(csvFile);
        
        Clarke c = new Clarke();
        HashMap<String, Integer> distOfStartPtToAllPt = c.getInitialSolution(pickingList);
        
        HashMap<String, Integer> distAmongPickItems = c.getPointToPointDistance(pickingList);
        
        HashMap<String, Integer> savingsMap = c.getSavingsMap(pickingList);
        
        //System.out.println(distOfStartPtToAllPt);
        //System.out.println(distAmongPickItems);
        System.out.println(savingsMap);
     }
}
