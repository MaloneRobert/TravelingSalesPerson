package main;

/*This is my gui class that contains all the methods necessary 
 *for my program
 */

import javax.swing.*; //First import these packages
import java.awt.event.*; 
import java.util.*;

public class TSP_GUI extends JFrame implements ActionListener 
{
   JPanel myPanel; //Declare the gui
   JLabel myCommand; 
   JButton enterButton; 
   JTextArea cityNumOutput; 
   JTextArea cityNamOutput;
   JScrollPane namScroll;
   JTextArea minCostOutput;
   
   int N; //Then I declare some global integer and integer arrays that contain the min cost and route
   int[] minCity;
   int min = 5000000;
   
   public TSP_GUI()//Here I create the gui
   {
      setSize(600,540); 
      setDefaultCloseOperation(EXIT_ON_CLOSE); 
      
      myPanel = new JPanel(); 
      
      myCommand = new JLabel ("Click enter to pick a matrix file of cities to find the minimum route to all cities");
      myCommand.setBounds(50, 50, 500, 40);
      
      enterButton = new JButton ("Enter"); 
      enterButton.addActionListener(this);
      enterButton.setBounds(100, 110, 100, 40);
      
      cityNumOutput = new JTextArea ("Number of Cities: ");
      cityNumOutput.setBounds(50, 190, 200, 20);
      cityNamOutput = new JTextArea("Names of City in Cheapest Orders: "); 
      namScroll = new JScrollPane (cityNamOutput); 
      namScroll.setBounds(50, 260, 300, 180);
      minCostOutput = new JTextArea ("Minimum cost of traveling: ");
      minCostOutput.setBounds(330, 190, 200, 20); 
      
      myPanel.setLayout(null); 
      myPanel.add(myCommand);
      myPanel.add(enterButton); 
      myPanel.add(cityNumOutput);
      myPanel.add(namScroll); 
      myPanel.add(minCostOutput);  
      
      add(myPanel); 
      setVisible(true); 
   }
   
   public void actionPerformed (ActionEvent evt)//Here is what happens when a button is clicked 
   {  
      try{
               
         JFileChooser myChooser = new JFileChooser();//I use a jfilechooser to let the user pick a file
         int input = myChooser.showOpenDialog(null);
         Scanner myScanner = new Scanner(myChooser.getSelectedFile());
         
         double start = System.nanoTime();//Here I begin the timer for my program

         int N = myScanner.nextInt();//Here I get the amount of cities
         int[][] cityCost = new int[N][N];//Create my 2d array
         while(myScanner.hasNextInt()) {//Scan the matrix into the 2d array
            for(int i=0; i<N; i++) {
               for(int j=0; j<N; j++) {
                  cityCost [i][j] = myScanner.nextInt();
               }
            }
         }
         int[] cities = new int[N-1];//Here is the cities array
         for(int r=0; r<cities.length; r++) {
            cities[r] = r+1;
         }
         minCity = new int[N-1];//Here I declare the min route array
      
         cityNumOutput.setText("Number of Cities: " + N);
          
         permRoute(cities, 0, cityCost);//Here I all the permutation on the cities to find the cheapest cost
        
         cityNamOutput.append("\n0"); 
         for(int i=0; i<minCity.length; i++) {//Here I print out the cities in order
            cityNamOutput.append("\n" + minCity[i]);
         }
         cityNamOutput.append("\n0");
         
         minCostOutput.setText("Minimum cost of traveling: " + min);//Here I print out the the minimum cost
         
         double end = System.nanoTime(); //Here I end the timer, then convert the nano to regular seconds
         double elapsedTime = (end - start)/1000000000;
         System.out.println(elapsedTime +"sec");
      }
      catch(Exception e)
      {
         System.out.println("Error! Try again." + e);
      }
   }
   
   public void permRoute(int[] input, int p, int[][] cost) //This is my permutations method
   {
      if(p == input.length)//This occurs when the program reaches a full input array
      {
         int i = 0;
         int myCost = 0;//Then I add up the cost of this array
         if(i == 0) {
            myCost = myCost + cost[0][input[i]];
            i++;
         }
         i = 0;
         while(i<input.length-1) {
            int j = i + 1;
            myCost = myCost + cost[input[i]][input[j]];
            i++;
         }
         i = input.length-1;
         if(i == input.length-1) {
            myCost = myCost + cost[input[i]][0];
         }
         if(myCost<min) {//If this cost is less than the minimum, then I save the cost and route
            min = myCost;
            for(int k=0; k<input.length; k++) {
               minCity[k] = input[k];
            }
         } 
      }
      else
      {
         int temp;
         
         if(getCost(input, p, cost)<min) { //Here is my branch and bound, this prevents routes that would already be larger than the min 
            for(int i=p; i<input.length; i++)
            {
               temp = input[i]; 
               input[i] = input[p]; 
               input[p] = temp; 
                 
               permRoute(input, p+1, cost); //Here is the recursion
                              
               temp = input[p]; 
               input[p] = input[i]; 
               input[i] = temp;
            }
         } 
         else {
         }
      }
   }
   
   public int estimateCost (int[] input, int p, int[][] cost) {//This is my method to estimate the minimum bound cost for an array
      int max = 0; 
      for(int i=0; i<N; i++) {//First I find the max
         for(int j=0; j<N; j++) {
            int temp = cost[i][j]; 
            if(temp>max) {
               max = temp;
            }
         }
      }
      for(int i=0; i<N; i++) { //Then I set the 0s in the matrix equal to the max + 1
         cost[i][i] = max + 1; 
      }
      for(int i=0; i<N; i++) {//Then I set the cities ive already gone to equal to the max +1
         for(int k=0; k<p-1; k++) {
            cost[i][input[k]] = max + 1;
         }      
      }
      int myCost = 0; 
      int w = 0; //Then I calculate the cost of this estimated min bound
      if(w==0) {
         myCost = myCost + cost[0][input[0]];
         w++;
      }
      w = 0;
      while(w<p-1) { 
         int d = w + 1;
         myCost = myCost + cost[w][d];
         w++;
      }
      int minEst = 0;
      while(p<input.length+1) {
         minEst = minEst + getMin(p-1, cost);//Here is where I estimate it, I can my getMin method 
         p++;
      }
      myCost = myCost + minEst;
      return myCost;//Then i return this cost
   }
   public int getMin (int p, int[][] cost) {//This getMin method returns the minimum length in an certain row in the matrix
      int minRow = 50000000;
      int temp;
      for(int i=0; i<N; i++) {
         for(int j=0; j<N; j++) {
            temp = cost[p][j];
            if(temp<minRow) {
               minRow = temp;
            }
         }
      }
      return minRow;//Here I return the value
   }
   
   public int getCost (int[] input, int p, int[][]cost) {//Since I couldn't get the min bound method to work, I did a simpler branch and bound method. 
      int i=0; 
      int minCost = 0; //Here I find the cost of the cities we have already gone to
      minCost = minCost + cost[0][input[0]]; 
      for(int b=0; b<p-1; b++) {
         int c = b + 1; 
         minCost = minCost + cost[input[b]][input[c]];
      }
      return minCost;//Then return the cost of the cities already on the list.
   }
}