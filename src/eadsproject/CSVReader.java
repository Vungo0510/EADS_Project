package eadsproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Snow Petrel
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class CSVReader {

    public TreeMap<Integer, ArrayList<Integer>> readAllCornerNodes(String csvFile) {
        
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        //System.out.println(csvFile);
        HashMap<Integer, ArrayList<Integer>> cornerNodesMap = new HashMap<Integer, ArrayList<Integer>>();
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); //skip first line
            
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] cornerToCorner = line.split(cvsSplitBy);
                Integer cornerXCoordinate = Integer.parseInt(cornerToCorner[3]);
                Integer cornerYCoordinate = Integer.parseInt(cornerToCorner[4]);
                ArrayList<Integer> listOfYCoordinates = cornerNodesMap.get(cornerXCoordinate);
                
                if (listOfYCoordinates == null) {
                    listOfYCoordinates = new ArrayList<Integer>();
                    listOfYCoordinates.add(cornerYCoordinate);
                    
                } else {                  
                    if (!listOfYCoordinates.contains(cornerYCoordinate)) {
                        listOfYCoordinates.add(cornerYCoordinate);
                    }
                }
                
                cornerNodesMap.put(cornerXCoordinate, listOfYCoordinates);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        TreeMap<Integer, ArrayList<Integer>> cornerNodesTreeMap = new TreeMap<>(cornerNodesMap);
        return cornerNodesTreeMap;
    }
    
    public ArrayList<String> readPickingList(String csvFile) {

        //String csvFile = "./Data/PickingList.csv"; //Already shifted to the correct position for ppl to walk on
       
        File f = new File(csvFile);
        System.out.println(f.getAbsolutePath());
        
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        //System.out.println(csvFile);
        ArrayList<String> pickingList = new ArrayList<String>();
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); //skip first line
            
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] pickItem = line.split(cvsSplitBy);
                String originalLocation = pickItem[0]; //this is location description in CSV file
                String xCoordinate = pickItem[4];
                String yCoordinate = pickItem[5];
                String zCoordinate = pickItem[6];
                String numOfCartons = pickItem[7];
                String pickItemString = originalLocation + "," + xCoordinate + "," + yCoordinate + "," + zCoordinate + "," + numOfCartons;
                
                if (!pickingList.contains(pickItemString)) {
                    pickingList.add(pickItemString);
                } 
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pickingList;
    }
    
    public HashMap<String, Integer> readPickItemCapacity (String csvFile) {

        //String csvFile = "./Data/PickingList.csv"; //Already shifted to the correct position for ppl to walk on
       
        File f = new File(csvFile);
        System.out.println(f.getAbsolutePath());
        
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        //System.out.println(csvFile);
        HashMap<String, Integer> pickItemCapacityMap = new HashMap<String, Integer>();
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine(); //skip first line
            
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] pickItem = line.split(cvsSplitBy);
                String xCoordinate = pickItem[4];
                String yCoordinate = pickItem[5];
                Integer numOfCartons = Integer.parseInt(pickItem[7]);
                Integer existingCapacityAtPickLocation = pickItemCapacityMap.get(xCoordinate + "," + yCoordinate);
                
                if (existingCapacityAtPickLocation == null) {
                    pickItemCapacityMap.put(xCoordinate + "," + yCoordinate, numOfCartons);
                } else {
                    pickItemCapacityMap.put(xCoordinate + "," + yCoordinate, existingCapacityAtPickLocation + numOfCartons);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pickItemCapacityMap;
    }
    
}
