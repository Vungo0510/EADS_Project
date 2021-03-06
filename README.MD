# EADS_Project Team 5, AY2017-2018 Term 2

- How to run:
	+ Double click on "dist" folder.
	+ Double click on EADSProject.jar to start the application. Java Runtime Environment is required to run the application.
	
	+ Key in necessary inputs accordingly. Note that all fields have to be filled.
	
	+ For current MHE position, Lane has to be between 04 and 18 and Rack has to be between 06 and 57. For odd racks (1, 3, 5...), 'middle racks' are 
	valid location as well. For example we can have "05,06","05,07" and "05,07M" as current MHE position but not "05,06".
	
	+ For corner nodes and picking list input file, only CSV files are accepted. The application can accept picking list and corner nodes files that 
	are not among those that we provide, however they have to follow the format we define:

		i) For corner nodes file, the first line has to be the column headers and subsequent line has to contain the following details:
		   C to C,Start Corner,End Corner,Start Corner X-coordinate,Start Corner Y-coordinate,End Corner X-coordinate,End Corner Y-coordinate,Distance (mm)
		   (Each line represents the distance from one corner node to another corner node. The numbers are derived using Excel in our data preprocessing 
		   part) 
	
		ii) For picking list file, the first line has to be the column headers and subsequent line has to contain the following details:
			Location description,Lane,Rack,Shelf/ Pallet (aka the height),X coordinate,Y coordinate,Height,No. of cartons
			(Each line represents one pick item and corresponds to 1 line in YCH picking list. The numbers are derived using Excel in our data 
			preprocessing part as well) 
	
	+ Input validation is present as well, thus it will inform you should there be any invalid inputs in any field.
	
	  Example of a set of inputs we used: 
		MHE Capacity = 50 (cartons)
		Time taken to travel 1 meter = 1 (second)
		Time taken for MHE to lift/lower 1 meter = 1 (second)
		Current MHE = 08,37M (means we start at lane 08, rack 37M)
		PickingListTwo.csv
		CornerNodes.csv
		
	+ After all the fields are filled in, click "Submit".
	
	+ Three different windows will pop up, each corresponding to visualization of the result from different method:
		- Clarke-Wright (without local search)
		- Clarke-Wright (WITH local search)
		- Twice Around The Tree
	
	+ There are 2 components to each window: a text input which prints out the route sequence as well as time taken to travel each route and a
	visualization pane to map out the routes
	
	+ Different routes will be represented in different colors. 
	
	+ Although the start and end point of each route as well as direction of the route is not shown, we can easily trace the route by looking at the
	text input ("route to take") and look for the location of each point on the graph. For example "08,*,17" correspond to the point with x axis 
	coordinate = 08 and y axis coordinate = 17. Z-coordinate (height) is ignore in this case, therefore a pick node such as "08Z23" will correspond to
	the point with x axis coordinate = 08 and Y axis coordinate = 23
	
	+ The Data Input Form doesn't close after the visualization windows popped up, thus if you wish to change any input parameter, we can go back to 
	the form, edit that parameter and click 'Submit' again.

	+ The project folder can be opened, edited and run using Netbeans as well. 
	
- Folder explanation
	+ dist: contains the main executable file (EADSProject.jar) and the CPLEX library (cplex.jar) in "lib" subfolder
	
	+ Data: contains the csv files to be used as picking list (PickingListSmall, PickingListSampleOne, PickingListSampleTwo, PickingListSampleThree) 
	  and corner node csv file (CornerNodes)
	
	+ nbproject: contains the configuration files for Netbeans to recognize this folder as a Netbeans project
	
	+ src: contains the Java application's source code 