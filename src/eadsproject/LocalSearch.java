/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eadsproject;

/**
 *
 * @author Cathy
 */

import java.util.*;


public class LocalSearch {
    
    public HashMap<String, Integer> localSearch(ArrayList<String> finalRoutes, ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath){
    
        HashMap<String, Integer> modifiedRoutes = new HashMap<String, Integer>(); //to store all the routes after local search is completed
        Clarke c = new Clarke();
        // retrieve distance from start pt to all pt
        ArrayList<HashMap> initialSolution = c.getInitialSolution(pickingList, startingPoint, cornerNodeFilePath);
        HashMap<String, Integer> distOfStartPtToAllPt = initialSolution.get(0); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointDistance(pickingList,cornerNodeFilePath); 
        HashMap<String, Integer> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance
        
        HashMap<String, Integer> finalRoutesDistHashMap = c.getDistanceOfFinalRoutes ( pickingList,finalRoutes, startingPoint,cornerNodeFilePath);
        
        for(int i = 0; i < finalRoutes.size(); i++){
            // loop through the arrayList of finalRoutes to do local search on each route
            String finalRoute = finalRoutes.get(i);
            int finalRouteTotalDist = finalRoutesDistHashMap.get(finalRoute);
            System.out.println(" finalRouteTotalDist " + finalRouteTotalDist);
            
            String[] finalRouteArr = finalRoute.split("-"); //first and last element are to be ignored: they are the starting position and packing position
            String[] modifiedRouteArr = finalRoute.split("-"); // to store the better modified route later
            String[] replicateFinalRouteArr = finalRoute.split("-");
            
            ArrayList<String> finalAns = new ArrayList<String>();
            
            String[] toKeepRoute = new String[finalRouteArr.length];
            if(finalRouteArr.length <=15 && finalRouteArr.length>3 ){
                int totalSwap = finalRouteArr.length/2;
                int noOfSwapSoFar = 0;
                Random rand = new Random();
                int firstPositionToSwap = rand.nextInt(finalRouteArr.length -2) +1;
                int secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2) +1;
                
                while(secondPositionToSwap == firstPositionToSwap){
                    //ensures that the two elements that are to be swap are not the same element
                    secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2)+1;
                }
                String improvedRoute = "";
                
                for(int j=0; j< totalSwap; j++){
                    //swap  two random elements after starting position
                    System.out.println("j times " + j);
                   
                    String firstPositionToSwapStr = replicateFinalRouteArr[firstPositionToSwap];
                    String secondPositionToSwapStr = replicateFinalRouteArr[secondPositionToSwap];
                    replicateFinalRouteArr[firstPositionToSwap] = secondPositionToSwapStr;
                    replicateFinalRouteArr[secondPositionToSwap] = firstPositionToSwapStr;
                    
                    // calculate total dist of new route
                    int totalDist = 0;
                    int distOfStartPtToFirstPickNode = distOfStartPtToAllPt.get(replicateFinalRouteArr[1]);
                    totalDist += distOfStartPtToFirstPickNode;
                    
                    for(int k = 1; k<replicateFinalRouteArr.length-2; k++ ){
                        // find the distance of all the pick nodes in the routes all the way to the packing location in the route that has finished swapping 2 pick items
                        String pickNode1 = replicateFinalRouteArr[k];                     
                        
                        System.out.println("pick node 1 : " + pickNode1);
                        
                        String pickNode2 = replicateFinalRouteArr[k+1];
                        System.out.println("pick node 2: " + pickNode2);
                        
                        int distBetweenPickNodes = 0;
                        if(distAmongPickItems.get(pickNode1 +"to" + pickNode2) !=null){
                            
                            distBetweenPickNodes = distAmongPickItems.get(pickNode1 +"to" + pickNode2);
                            System.out.println(" dist " +distBetweenPickNodes );
                        }else{
                            distBetweenPickNodes = distAmongPickItems.get(pickNode2 +"to" + pickNode1);
                            System.out.println(" dist " +distBetweenPickNodes );
                        }
                    
                        totalDist +=distBetweenPickNodes;
                        System.out.println("dist so far: " +totalDist );
                      
                    
                    }
                
                   
                    
                    if(totalDist < finalRouteTotalDist){
                        System.out.println("Stores j " + j);
                        //finalAns = replicateFinalRouteArr;
                        finalRouteTotalDist = totalDist;
                        System.out.println(" stored j route: " );
                        
                        finalAns.clear();
                        //finding the x and y coord of last pick node
                        String lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                        int ycoordOfLastPickNode = Integer.parseInt(lastPickNode.split(",")[1]);
                        int xcoordOfLastPickNode = Integer.parseInt(lastPickNode.split(",")[0]);
                        //changing the pack node to be of the same x coord of the last pick node, with y coord = 1
                        String changePackNode = xcoordOfLastPickNode + ",1";
                    
                        
                        int distFromLastPickNodeToPackNode = ycoordOfLastPickNode;
                        
                        
                        for(String s: replicateFinalRouteArr){
                            System.out.print(s + "  " );
                            finalAns.add(s);
                            
                        }
                        finalAns.remove(finalAns.size()-1);
                        finalAns.add(changePackNode);
                    
                    
                    }
                    
                    
                
                }
             /**   String modifiedRouteAns = "";
                for(String s: modifiedRouteArr){
                        modifiedRouteAns +=s + "";
                }**/
             
           //  System.out.println(" outside ");
             String modifiedRouteStr = "";
                for(int p = 0; p< finalAns.size(); p++){
                            String node = finalAns.get(p);
                            modifiedRouteStr += node;
                            if(p!= finalAns.size()-1){
                                modifiedRouteStr +="-";
                            }
                            
                }
               // String modifiedRouteStr = String.join("-", finalAns);
                modifiedRoutes.put(modifiedRouteStr,finalRouteTotalDist);
                
                
            
            }else if(finalRouteArr.length >15){
            
                int totalSwap = finalRouteArr.length/3;
                int noOfSwapSoFar = 0;
                Random rand = new Random();
                int firstPositionToSwap = rand.nextInt(finalRouteArr.length -2) + 1;
                int secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2)+ 1;
                
                while(secondPositionToSwap == firstPositionToSwap){
                    //ensures that the two elements that are to be swap are not the same element
                    secondPositionToSwap =  rand.nextInt(finalRouteArr.length-2) + 1;
                }
                
                for(int j=0; j< totalSwap; j++){
                    //swap  two random elements after starting position
                    String firstPositionToSwapStr = replicateFinalRouteArr[firstPositionToSwap];
                    String secondPositionToSwapStr = replicateFinalRouteArr[secondPositionToSwap];
                    replicateFinalRouteArr[firstPositionToSwap] = secondPositionToSwapStr;
                    replicateFinalRouteArr[secondPositionToSwap] = firstPositionToSwapStr;
                    
                    // calculate total dist of new route
                    int totalDist = 0;
                    int distOfStartPtToFirstPickNode = distOfStartPtToAllPt.get(replicateFinalRouteArr[1]);
                    totalDist += distOfStartPtToFirstPickNode;
                    
                    for(int k = 1; k<replicateFinalRouteArr.length-2; k++ ){
                        // find the distance of all the pick nodes in the routes all the way to the packing location
                        String pickNode1 = replicateFinalRouteArr[k];
                        String pickNode2 = replicateFinalRouteArr[k+1];
                        int distBetweenPickNodes = 0;
                        
                        if(distAmongPickItems.get(pickNode1 +"to" + pickNode2) !=null){
                            
                            distBetweenPickNodes = distAmongPickItems.get(pickNode1 +"to" + pickNode2);
                            System.out.println(" dist " +distBetweenPickNodes );
                        }else{
                            distBetweenPickNodes = distAmongPickItems.get(pickNode2 +"to" + pickNode1);
                            System.out.println(" dist " +distBetweenPickNodes );
                        }
                    
                        totalDist +=distBetweenPickNodes;
                    
                    }
                 
                    
                    if(totalDist < finalRouteTotalDist){
                        //System.out.println("Stores j " + j);
                        //finalAns = replicateFinalRouteArr;
                        finalRouteTotalDist = totalDist;
                       // System.out.println(" stored j route: " );
                        finalAns.clear();
                           //finding the x and y coord of last pick node
                        String lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                        int ycoordOfLastPickNode = Integer.parseInt(lastPickNode.split(",")[1]);
                        int xcoordOfLastPickNode = Integer.parseInt(lastPickNode.split(",")[0]);
                        //changing the pack node to be of the same x coord of the last pick node, with y coord = 1
                        String changePackNode = xcoordOfLastPickNode + ",1" ;
                    
                        
                        int distFromLastPickNodeToPackNode = ycoordOfLastPickNode;
                        
                        
                        for(String s: replicateFinalRouteArr){
                            System.out.print(s + "  " );
                            finalAns.add(s);
                            
                        }
                        finalAns.remove(finalAns.size()-1);
                        finalAns.add(changePackNode);
                    
                    
                    
                    } 
                    
                    
                
                }
                               
              
              //  String modifiedRouteStr = String.join("-", finalAns);
              //System.out.println(" outside ");
                String modifiedRouteStr = "";
                for(int p = 0; p< finalAns.size(); p++){
                            String node = finalAns.get(p);
                            modifiedRouteStr += node;
                            if(p!= finalAns.size()-1){
                                modifiedRouteStr +="-";
                            }
                            
                }
               // String modifiedRouteStr = String.join("-", finalAns);
                modifiedRoutes.put(modifiedRouteStr,finalRouteTotalDist);
            
            
            
            }
                      
            
        
        }
        
       
        return modifiedRoutes;
    
    }
    
    
    
    
    
}
