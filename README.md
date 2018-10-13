  Author ron.katz72@gmail.com
  
  Package Challenge
  
  This program accept as its first argument a path to a filename.
  The input file contains several lines, each representing a test case.
 
  Line structure: "Capacity : (id1,w1,Sc1) (id2,w2,Sc2) ... (idn,wn,Scn)
  
  Where: Capacity is the capacity of the package
         idm is the mth item's id - integer
         wm  is the mth item's weight - float (see below about its precision)
         S   is the currency sign of the cost (must be the same for all items)
         cm  is the mth item's cost - integer
         
   Output: The program return a String with line per each input line.
   		for each set of items with a capacity the respective line in the output
   		includes the selected items that we choose to put into the box such that
           the items will me with minimum weight and maximum value.
           Example: 100 : (1,100.00,$40) (2,100.00,$50) (3,50.00 $100) (4,50.00,$100)
           will return : 3,4 since those two items together weight 100$ but their cumulative value is 200$
          
          In case no item can be added to the packe
          as in the following example input:
          100 : (1,200.00,$50)
          The result output line will include only a hyphen (-) 
           
   Exception: In case input is not valid, com.mobiquityinc.excption.APIException is being thrown.
   
   Constraints:
   
 	 MAX WEIGHT              = 100
 	 MAX ITEMS NUMBER        =  15
 	 MAX PACKAGE CAPACITY    = 100
 
 	 The program can be used by calling com.mobiquityinc.packer.Packer static method:
 	 String pack(String filepath);
 	 Where filepath is an absolute file path to the input file
    The method return the results as a String as explained above.
    
    Solution:
    
    Patterns used in this solution:
    
    1) Strategy pattern:
    	Controllers are injected with concrete implementation of an interface
    	This makes it easy to change implementation.
    	In our case: the controller is not aware of the parsing or packaging algorithm
    	This usually done by Spring for us however, I didn't use here Spring framework
    	and kept the code basic
    
    2) MVC - Model Viewer Controller
    	The Packer class contains two controllers: Parser & Packer
    	The controllers decide what to do with the information received.
    	In our case the information received from a file but this can be changed easily since the
    	controllers are not aware where from the information arrives.
    	The controllers sends the information to be processed in the model which in turn
    	informs the Viewer. The Viewer in our case is just a class which create a string of ids
    	from the selected items. the Packer class then use this view to create the final String answer.
    
    	Algorithm:
    	This problem is a classic Knapsack:
    	https://en.wikipedia.org/wiki/Knapsack_problem
  		
  		Therefore I used this algorithm to solve the problem
  		The implementation of the algorithm can be found in the class KnapSackPackagingStrategy 
  		which implements the interface IPackagingAlgorithm.
  
  		There is one twist in this problem as the weight is not given as an integer
  		To overcome this obstacle I assumed a constant precision for the weight number
  		In the example given with this challenge there were two digits after the decimal dot
  		Therefore the precision in the example is 2 and by multiply each weight by 10 to the power of 2
  		We receive a valid knapsack problem.
  
  		The precision can be changed by using the designated Packer constructor which takes
  		as a parameters a filepath as well as an integer representing the weight precision
  		By using the Packer constructor which received only a filepath
  		the precision is set to 2 as a default.
  		The computed weightFactor is then transfered to the parser implementation
  		which uses it to correct the data (see documentation in the parser class itself: DefaultPackerParser)
  
  		Complexity
   	Time  Complexity: O(nW)
   	Space Complexity: O(nW)
   
   	Where: W is the max capacity fixed by the weight factor
   		   So, if capacity = w and the precision is 2 then W = 100*w
   
   		   n = number of items.
   
  		Build:
  		Standard maven - use 'mvn clean install' from root directory
 
     To test the build one can run the following command:
 
     java -jar .\target\packageChalange-mobiquity-chalange-1.0.jar <full file path>
