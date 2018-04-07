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
    
    public HashMap<String, Double> localSearch(ArrayList<String> finalRoutes, ArrayList<String> pickingList, String startingPoint, String cornerNodeFilePath, double mheTravelTime, double mheLiftingTime){
    
        HashMap<String, Double> modifiedRoutes = new HashMap<String, Double>(); //to store all the routes after local search is completed
        Clarke c = new Clarke();
        // retrieve distance from start pt to all pt
        ArrayList<HashMap> initialSolution = c.getInitialSolution(pickingList, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        HashMap<String, Double> distOfStartPtToAllPt = initialSolution.get(0); //key stores x & y coordinates of pick nodes, values distance from start node to this pick node
         HashMap<String, String> routeOfStartPtToAllPt =initialSolution.get(1);
        
        ArrayList<HashMap> ptToPtRouteAndDistanceArr = c.getPointToPointTime(pickingList,cornerNodeFilePath,mheTravelTime, mheLiftingTime); 
        HashMap<String, Double> distAmongPickItems = ptToPtRouteAndDistanceArr.get(0); //key is x,y coordinate of current pick node "to" x,y coordinate of other pick node. value is distance
        HashMap<String, String> routeFromCurrPickNodeToOtherPickNode = ptToPtRouteAndDistanceArr.get(1);//Key is in format: x-y-z coordinate of current pick node + "to" + x-y-z coordinate of other pick node, value is the full route from current pick node to other pick node
        
        HashMap<String, Double> finalRoutesDistHashMap = c.getTimeOfFinalRoutes ( pickingList,finalRoutes, startingPoint, cornerNodeFilePath, mheTravelTime, mheLiftingTime);
        
        for(int i = 0; i < finalRoutes.size(); i++){
            // loop through the arrayList of finalRoutes to do local search on each route
            String finalRoute = finalRoutes.get(i);
            double finalRouteTotalDist = finalRoutesDistHashMap.get(finalRoute);
            System.out.println(" finalRouteTotalDist " + finalRouteTotalDist);
            
            String[] finalRouteArr = finalRoute.split("-"); //first and last element are to be ignored: they are the starting position and packing position
            String[] modifiedRouteArr = finalRoute.split("-"); // to store the better modified route later
            String[] replicateFinalRouteArr = finalRoute.split("-");
            
            ArrayList<String> finalAns = new ArrayList<String>();
            
            String[] toKeepRoute = new String[finalRouteArr.length];
            if( finalRouteArr.length>3 ){
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
                  //  System.out.println("j times " + j);
                   
                    String firstPositionToSwapStr = replicateFinalRouteArr[firstPositionToSwap];
                    String secondPositionToSwapStr = replicateFinalRouteArr[secondPositionToSwap];
                    replicateFinalRouteArr[firstPositionToSwap] = secondPositionToSwapStr;
                    replicateFinalRouteArr[secondPositionToSwap] = firstPositionToSwapStr;
                    
                    // calculate total dist of new route
                    double totalDist = 0.0;
                    double distOfStartPtToFirstPickNode = distOfStartPtToAllPt.get(replicateFinalRouteArr[1]);
                    totalDist += distOfStartPtToFirstPickNode;
                   
                    
                    for(int k = 1; k<replicateFinalRouteArr.length-2; k++ ){
                        // find the distance of all the pick nodes in the routes all the way to the packing location in the route that has finished swapping 2 pick items
                        String pickNode1 = replicateFinalRouteArr[k];                     
                        
                    //    System.out.println("pick node 1 : " + pickNode1);
                        
                        String pickNode2 = replicateFinalRouteArr[k+1];
                  //      System.out.println("pick node 2: " + pickNode2);
                        
                        double distBetweenPickNodes = 0;
                        if(distAmongPickItems.get(pickNode1 +"to" + pickNode2) !=null){
                            
                            distBetweenPickNodes = distAmongPickItems.get(pickNode1 +"to" + pickNode2);
                           // System.out.println(" dist " +distBetweenPickNodes );
                        }else{
                            distBetweenPickNodes = distAmongPickItems.get(pickNode2 +"to" + pickNode1);
                            //System.out.println(" dist " +distBetweenPickNodes );
                        }
                    
                        totalDist +=distBetweenPickNodes;
                        System.out.println("dist so far: " +totalDist );
                      
                    
                    }
                     // adding in the z coord distance of the last pick node into the total distance
                    String lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                    double zcoordOfLastPickNode = Double.parseDouble(lastPickNode.split(",")[2]);
                    totalDist +=zcoordOfLastPickNode ;
                 
                   
                    
                    if(totalDist < finalRouteTotalDist){
                       System.out.println("Stores j " + j);
                        //finalAns = replicateFinalRouteArr;
                        finalRouteTotalDist = totalDist;
                        System.out.println(" stored j route: " );
                        
                        finalAns.clear();
                        //finding the x and y coord of last pick node
                         lastPickNode = replicateFinalRouteArr[replicateFinalRouteArr.length-2];
                        double xcoordOfLastPickNode = Double.parseDouble(lastPickNode.split(",")[0]);
                        //changing the pack node to be of the same x coord of the last pick node, with y coord = 1
                        String changePackNode = xcoordOfLastPickNode + ",1,0";
                                          
                        
                        for(String s: replicateFinalRouteArr){
                         //   System.out.print(s + "  " );
                            finalAns.add(s);
                            
                        }
                        finalAns.remove(finalAns.size()-1);
                        finalAns.add(changePackNode);
                    
                    
                    }
                    
                    
                
                }
            
                String finalAnsWithCornerNodes = "";
             
                String modifiedRouteStr = "";
                if(i ==0){
                    String routeFromStartNodeToFirstPickNode = routeOfStartPtToAllPt.get( finalAns.get(1));
                    finalAnsWithCornerNodes +=routeFromStartNodeToFirstPickNode + "-";
                    for(int p = 1; p< finalAns.size()-2; p++){
                            String node = finalAns.get(p);
                            String nxtNode = finalAns.get(p+1);
                             System.out.println(node + " node ");
                             System.out.println(nxtNode + " nxt node ");
                            String intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(node +"to" +nxtNode);
                            if(intermediateRouteWithCornerNode == null){
                                 intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(nxtNode +"to" + node);
                                 String[] intermediateRouteWithCornerNodeArr= intermediateRouteWithCornerNode.split(",");
                                 String routeIWant = "";
                                 for(int z = intermediateRouteWithCornerNodeArr.length-1; z >-1; z--){
                                     routeIWant += intermediateRouteWithCornerNodeArr[z];
                                     routeIWant+=",";
                                     
                                 
                                 }
                                routeIWant= routeIWant.substring(0,routeIWant.length()-1);
                                 intermediateRouteWithCornerNode = routeIWant;
                                 
                                 
                                 
                            }
                                
                               ;
                                // cut off the second pick node
                                System.out.println(intermediateRouteWithCornerNode + " intermediateRouteWithCornerNode ");
                                int indexOfDash = intermediateRouteWithCornerNode.lastIndexOf("-");
                                intermediateRouteWithCornerNode = intermediateRouteWithCornerNode.substring(0,indexOfDash+1);
                                finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                                System.out.println(finalAnsWithCornerNodes + " finalAnsWithCornerNodes");
                                modifiedRouteStr += node;
                                if(p!= finalAns.size()-1){
                                    modifiedRouteStr +="-";
                                }

                    }
                    String node = finalAns.get(finalAns.size()-2);
                    String nxtNode = finalAns.get(finalAns.size()-1);
                    String intermediateRouteWithCornerNode = node + "-" + nxtNode;
                    finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                    
                    


                    //modifiedRoutes.put(modifiedRouteStr,finalRouteTotalDist);
                    modifiedRoutes.put(finalAnsWithCornerNodes,finalRouteTotalDist);



                }else{
                    
                    String startNodeOfRoute2 = finalAns.get(0);
                    String firstPickNodeOfRoute2 = finalAns.get(1);

                   // String yCoordOfFirstPickNode =firstPickNodeOfRoute2.split(",")[1];
                    String xCoordOfFirstPickNode =firstPickNodeOfRoute2.split(",")[0];
                    String cornerNodeBetweenStartNodeAndFirstPickNode = xCoordOfFirstPickNode + ",1.0,0.0";
                    String firstInterMediateRoute = startNodeOfRoute2 + "-" + cornerNodeBetweenStartNodeAndFirstPickNode + "-";
                    finalAnsWithCornerNodes +=firstInterMediateRoute;

                     for(int p = 1; p< finalAns.size()-2; p++){
                            String node = finalAns.get(p);
                            String nxtNode = finalAns.get(p+1);
                            String intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(node +"to" +nxtNode);
                            
                            if(intermediateRouteWithCornerNode == null){
                                 intermediateRouteWithCornerNode = routeFromCurrPickNodeToOtherPickNode.get(nxtNode +"to" + node);
                                 String[] intermediateRouteWithCornerNodeArr= intermediateRouteWithCornerNode.split(",");
                                 String routeIWant = "";
                                 for(int z = intermediateRouteWithCornerNodeArr.length-1; z >=0; z--){
                                     routeIWant += intermediateRouteWithCornerNodeArr[z];
                                     routeIWant+=",";
                                     
                                 
                                 }
                                routeIWant= routeIWant.substring(0,routeIWant.length()-1);
                                 intermediateRouteWithCornerNode = routeIWant;
                                 
                                 
                                 
                            }
                                // cut off the second pick node
                                int indexOfDash = intermediateRouteWithCornerNode.lastIndexOf("-");
                                intermediateRouteWithCornerNode = intermediateRouteWithCornerNode.substring(0,indexOfDash+1);
                                System.out.println(intermediateRouteWithCornerNode + "intermediateRouteWithCornerNode");
                                finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                                modifiedRouteStr += node;
                                if(p!= finalAns.size()-1){
                                    modifiedRouteStr +="-";
                                }

                    }
                     
                    String node = finalAns.get(finalAns.size()-2);
                    String nxtNode = finalAns.get(finalAns.size()-1);
                    String intermediateRouteWithCornerNode = node + "-" + nxtNode;
                    finalAnsWithCornerNodes +=intermediateRouteWithCornerNode;
                
                
                    //modifiedRoutes.put(modifiedRouteStr,finalRouteTotalDist);
                    modifiedRoutes.put(finalAnsWithCornerNodes,finalRouteTotalDist);
                
            
            }
            
              
                               
            
            }
            
     
                      
            
        
        }
        
       
        return modifiedRoutes;
    
    }
    
    
    
    
    
}
