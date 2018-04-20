package myPackage;

import java.util.*;

//Stack class with operations push and pop
class Stack{ 
	int top=-1;//top pointer
	int stackArray[]=new int[8];//array of stack will hold airport index integers
	void push(int x)//adding to stack
	{
		stackArray[++top]=x;
	}
	int pop()//removing from stack
	{
		if(top==-1)
			return 0;
		return stackArray[top--];
	}
}

//ArrDepData class stores the main database of flight data. For each airport the entire schedule of arrivals and departures is stored in the object of this class
class ArrDepData{
	String Airline[]=new String[8];//Airline name
	int flightNumber[]=new int[8];//Flight Number
	long DepartureTime[]=new long[8];//Time of departure 
	long ArrivalTime[]=new long[8];//Time of arrival
	ArrDepData(String A[],int flno[],long DT[],long AT[])//passed in constructor
	{
		Airline=A;
		flightNumber=flno;
		DepartureTime=DT;
		ArrivalTime=AT;
	}
}

//Time in this program is stored ALWAYS AS A LONG INTEGER of the MINUTES. In a day, there are 24 hours so the time would be a minute value from 0 (0:00) to 1439 (23:59). This class contains functions which manipulate this minute value.
class Time{
	long MinutetoMins(long x)//converts the minute value to the remaining minutes of an hour. ex: 124 minutes = 2 hours (discard) and 4 minutes
	{
		return(x%60);
	}
	long MinutetoHrs(long x)//Converts the minute value to the hour of the day.
	{
		return(x/60);
	}
	long HourstoMins(long x)//Converts an input hour of the day to the corresponding minute value
	{
		return(x*60);
	}
}

//This class stores an integer used in the program which is fixed to refer to a corresponding airport code
class VertexNames{ 
	String VertexNames[]=new String[8];
	VertexNames()
	{
		VertexNames[0]="DEL";//Delhi
		VertexNames[1]="BOM";//Mumbai
		VertexNames[2]="MAA";//Chennai
		VertexNames[3]="BLR";//Bangalore
		VertexNames[4]="HYD";//Hyderabad
		VertexNames[5]="GOI";//Goa
		VertexNames[6]="CCU";//Kolkata
		VertexNames[7]="COK";//Cochin
	}
	int getAirportasIndex(String DepAirpt) //Used during input, this function returns the corresponding airport index given the string of airport code
	{
		int i=0;
		while(VertexNames[i].equalsIgnoreCase(DepAirpt)==false)//checks if strings are equal. Case insensitive
		{
			i++;
		}
		return i;//returns index
	}
}

//Main class performing the function of the problem
public class Flights {
	
	//constants, arrays and objects used globally throughout the Flights class programs and functions:
	public static int tot_nodes=8; //Total Number of Airports
	public static int tot_edges=20; //The total number of flight routes between all airports
	public static int path[]=new int[10];//priority queue containing vertex index
	static Scanner s=new Scanner(System.in); //Scanner object for input
	static VertexNames AIRPORT=new VertexNames(); //Object of type VertexNames
	static Time TimeConverter=new Time();//Object of type time
	static ArrDepData Schedule[]=new ArrDepData[8];//Object array of type ArrDepData. This holds the database of flight and airport information.
	static Stack Buffer=new Stack();//A temporary buffer of stack data structure used during displaying information
	static long MinimumTime;//This holds the limiting time which the user enters. Flights before this time cannot be caught hence are not shown.
	
	//Main function
	public static void main(String[] args){
		int i,j;
		long cost[][]=new long[8][8];//cost adjacency matrix containing intercity flight durations
		long dist[]=new long[8];//Matrix which will store the shortest path (quickest flight time) between the source and destination airports
		String StartTimeString;//The time input is a string of the from HH:MM. This is processed later
		long StartTimeReader[]=new long[2];//Used in processing to input time string. Lower address (index 0) will hold hours. Higher address (index 1) will store minutes.
		long startH,startM;//Stores departure time HH and MM in separate variables
		long startT;//Stores the Minute value corresponding to the 24 hour time
		String DepartureAirport;//Source airport
		String ArrivalAirport;//Destination airport
		System.out.print("FLIGHT AGENDA\n\n");
		create(cost);//Initialize the database and the cost matrix
		System.out.print("Enter the departure airport code: ");
		DepartureAirport=s.next();//Gets 3 letter departure airport code
		i=AIRPORT.getAirportasIndex(DepartureAirport);//the Source airport is converted to the corresponding integer value as in VertexNames
		System.out.print("Enter the departure time (HH:MM): ");
		StartTimeString=s.next();//The time is input as a formatted string HH:MM
		StringTokenizer SplitTime=new StringTokenizer(StartTimeString,":");//String tokenizer class has functions to read an input string token by token separated by the defined delimiter ':'
		int k=0;//index pointer for the StartTimeReader[] array which will store hours in index 0 and minutes in index 1
		while(SplitTime.hasMoreTokens())//While there are characters in the input time, do:
		{
			StartTimeReader[k]=Long.parseLong(SplitTime.nextToken());//Converts the read string into a long integer value and stores this long into the array
			k++;//increment pointer
		}
		startH=StartTimeReader[0];//transfer hours
		startM=StartTimeReader[1];//transfer minutes
		MinimumTime=startT=TimeConverter.HourstoMins(startH)+startM;//Stores the departure time as a minute value.
		System.out.print("Enter the destination airport code: ");
		ArrivalAirport=s.next();//Get destination airport code
		int A=AIRPORT.getAirportasIndex(ArrivalAirport);//converts the destination airport code to the corresponding integer
		System.out.println("\nFlights departing from  "+(AIRPORT.VertexNames[i])+" airport at or after "+startH+":"+startM+" to "+(AIRPORT.VertexNames[A])+" are: \n");
		j=A;
		Dijkstra(cost,i,dist);//Calls Dijkstras function which will compute the quickest route from source to destination
		if(dist[i]==1441)//If the flight duration from source to destination is infinite, it means there is no possible route to that destination
			System.out.println("\nNo Path from "+AIRPORT.VertexNames[i]+" to "+AIRPORT.VertexNames[j]);
		else
			display(i,j,dist);//Display the quickest route and the flight schedules
	}
	
	//This function is used to initialize the database and the cost (Flight duration in minutes is cost) adjacency matrix
	public static void create(long cost[][])
	{
		int i,j;
		String Airline[];//Airline name array
		int flightNumber[];//Flight numbers array
		long DepartureTime[];//Departure times from an airport
		long ArrivalTime[];//arrival times to a destination from an airport
		for(i=0;i<tot_nodes;i++)
		{
			for(j=0;j<tot_nodes;j++)
			{
				if(i==j)
					cost[i][j]=0;//The time (cost) of traveling from an airport to the same airport is 0 minutes
				else
					cost[i][j]=1441;//1440 minutes in 24 hours so say 1441 is infinity. Initialize all other times as infinite (unreachable). This is updated later.
			}
		}
		//Following are to initialize the flight times. We assign a cost of flight duration in the cost adjacency matrix
		cost[0][1]=cost[1][0]=125;//DEL-->BOM 2h05m 
		cost[0][6]=cost[6][0]=135;//DEL-->CCU 2h15m
		cost[1][2]=cost[2][1]=120;//BOM-->MAA 2h00m
		cost[1][3]=cost[3][1]=100;//BOM-->BLR 1h40m
		cost[1][5]=cost[5][1]=75;//BOM-->GOA 1h15m
		cost[2][3]=cost[3][2]=60;//MAA-->BLR 1h00m
		cost[2][4]=cost[4][2]=75;//MAA-->HYD 1h15m
		cost[3][5]=cost[5][3]=75;//BLR-->GOI 1h15m
		cost[3][7]=cost[7][3]=70;//BLR-->COK 1h10m
		cost[4][6]=cost[6][4]=130;//HYD-->CCU 2h10m
		//Here we create the database. For each airport we store the data in an array of objects of class type ArrDepData. The index number of the object corresponds to the index number of the airport as defined in VertexNames class. Note: flight number of '-1' is used to indicate end of records
		//Kolkata
		Airline=new String[] {"Alliance Air","Royal Airways","Alliance Air"};
		flightNumber=new int[] {784,486,777,-1};
		DepartureTime=new long[] {630,1050,1080};
		ArrivalTime=new long[] {765,1180,1215};
		Schedule[6]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Cochin
		Airline=new String[] {"Royal Airways","Royal Airways","Royal Airways","Alliance Air"};
		flightNumber=new int[] {433,223,213,197,-1};
		DepartureTime=new long[] {420,720,1020,1320};
		ArrivalTime=new long[] {490,790,1090,1390};
		Schedule[7]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Hyderabad
		Airline=new String[] {"Country Airlines", "Royal Airways","Alliance Air", "Royal Airways"};
		flightNumber=new int[] {566,311,259,448,-1};
		DepartureTime=new long[] {420,480,660,870};
		ArrivalTime=new long[] {495,610,735,1000};
		Schedule[4]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Delhi
		Airline=new String[] {"Alliance Air","Alliance Air","Royal Airways","Royal Airways","Alliance Air"};
		flightNumber=new int[] {648,448,742,445,287,-1};
		DepartureTime=new long[] {240,270,720,810,1065};
		ArrivalTime=new long[] {365,405,845,945,1190};
		Schedule[0]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Mumbai
		Airline=new String[] {"Country Airlines","Alliance Air","Royal Airways","Royal Airways","Alliance Air"};
		flightNumber=new int[] {124,667,446,824,334,-1};
		DepartureTime=new long[] {300,360,690,840,1215};
		ArrivalTime=new long[] {425,485,810,940,1290};
		Schedule[1]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Goa
		Airline=new String[] {"Country Airlines", "Alliance Air","Country Airlines","Country Airlines","Royal Airways"};
		flightNumber=new int[] {156,187,934,438,555,-1};
		DepartureTime=new long[] {480,600,1050,1125,1330};
		ArrivalTime=new long[] {555,675,1125,1200,1405};
		Schedule[5]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Chennai
		Airline=new String[] {"Alliance Air","Royal Airways", "Country Airlines", "Alliance Air","Royal Airways","Alliance Air"};
		flightNumber=new int[] {789,963,846,748,225,499,-1};
		DepartureTime=new long[] {470,480,660,840,1050,1290};
		ArrivalTime=new long[] {590,540,720,900,1125,1365};
		Schedule[2]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
		//Bangalore
		Airline=new String[] {"Royal Airways","Royal Airways","Country Airlines", "Alliance Air","Alliance Air","Alliance Air"};
		flightNumber=new int[] {986,45,965,102,202,333,-1};
		DepartureTime=new long[] {480,510,555,960,1020,1080};
		ArrivalTime=new long[] {580,580,655,1020,1095,1150};
		Schedule[3]=new ArrDepData(Airline,flightNumber,DepartureTime,ArrivalTime);
	}
	
	//Computes the quickest route to the destination from source
	public static void Dijkstra(long[][] cost, int source, long[] dist)
	{
		int i,j,v1,v2;//Vertex pointers
		long minD;//Will hold quickest time
		int src[]=new int[10];//src[] is an integer boolean type matrix to store whether a vertex (airport) has been already visited or not
		for(i=0;i<tot_nodes;i++)//Runs the loop for all vertices
		{
			dist[i]=cost[source][i];//initially put flight duration from source to i
			src[i]=0;//Set all airports as unvisited initially
			path[i]=source;//The shortest path priority queue is initially set at source for all i airports
		}
		src[source]=1;//Set source as visited
		for(i=1;i<tot_nodes;i++)//Run the loop for all vertices after the source (i=1)
		{
			minD=1441;//initialize minimum flight time to 1441 (infinity)
			v1=-1;//Reset previous value of source;
			for(j=0;j<tot_nodes;j++)//Run loop to compare all destinations and choose the unvisted airport with minimum flight duration
			{
				if(src[j]==0)//If Airport unvisited
				{
					if(dist[j]<minD)//if duration to the jth airport from source is less than current minimum, select it
					{
						minD=dist[j];
						v1=j;
					}
				}
			}
			src[v1]=1;//set minimum duration airport as visited
			for(v2=0;v2<tot_nodes;v2++)//Update the duration values of all adjacent airports
			{
				if(src[v2]==0)//if v2 is unvisited
				{
					if((dist[v1]+cost[v1][v2])<dist[v2])//if total flight time of v2<--v1<--source is less than the direct flight duration of v2<--source then relax this edge
					{
						dist[v2]=dist[v1]+cost[v1][v2];//Route is from source to v1 to v2
						path[v2]=v1;//path is via v1
					}
				}
			}
		}
	}
	//Displays the order of the route to be taken to reach the destination in the shortest possible time and also display the flight schedules.
	public static void display(int Source,int Destination,long dist[])
	{
		int i;
		System.out.println("The route from "+AIRPORT.VertexNames[Source]+" to "+AIRPORT.VertexNames[Destination]+" is: \n");
		for(i=Destination;i!=Source;i=path[i])//Route will be printed from destination to source
		{
			System.out.print(AIRPORT.VertexNames[i]+" <-- ");//Airport code will be printed based on the index
			Buffer.push(i);//Path index is stored in a stack for later use (display of Arrival/Departure data)
		}
		System.out.println(" "+AIRPORT.VertexNames[i]);//Prints source
		Buffer.push(i);//Push the source
		System.out.println("\nThe Flight Details on your route are: \n");
		showData(Destination);//Function to show Arrivals and Departures Data of all visited airports on the route
		System.out.println("\nThe total flight time (excluding halts) is: "+TimeConverter.MinutetoHrs(dist[Destination])+" hours "+TimeConverter.MinutetoMins(dist[Destination])+" minutes");//Displays total flight time. The Minute value is converted to a 24h time.
	}
	//Function will display Arrival and Departure data of the Airports on the route. The path is stored in the buffer stack by the display() function
	public static void showData(int dest)
	{
		int i=Buffer.pop();//pops the top element. This is the departure airport
		Stack StackToObtainArrivalTime=new Stack();//Another stack is defined to help with time restriction to display details of only the flights which can be caught
		while(i!=dest)//while the airport index (popped from buffer) is not equal to the destination airport we display data
		{
			System.out.println("From Airport "+AIRPORT.VertexNames[i]+"\n\nFLIGHT\t\t\tDESTINATION\tDEPARTURE\tARRIVAL\n_______________________________________________________________________");
			for(int j=0;Schedule[i].flightNumber[j]!=-1;j++)//run the loop to display all records. when flight number of '-1' is encountered, we stop.
			{
				int k=Buffer.pop();//we pop the next airport (destination of current leg) to obtain the arrival time at that airport. This is to prevent display of past uncatchable flights
				Buffer.push(k);//push 'k' back to ensure its data is also displayed in the next iteration 
				if(Schedule[i].DepartureTime[j]<MinimumTime)//If the departure time of the flight is already elapsed, (MinimumTime stores departure time), then that flight should not be diplayed. The loop is run to the next iteration.
					continue;
				StackToObtainArrivalTime.push(j);//Push the current flight index into the stack. 
				System.out.println(Schedule[i].Airline[j]+" "+Schedule[i].flightNumber[j]+"\t    "+AIRPORT.VertexNames[k]+"\t\t"+TimeConverter.MinutetoHrs(Schedule[i].DepartureTime[j])+":"+TimeConverter.MinutetoMins(Schedule[i].DepartureTime[j])+"\t\t   "+TimeConverter.MinutetoHrs(Schedule[i].ArrivalTime[j])+":"+TimeConverter.MinutetoMins(Schedule[i].ArrivalTime[j]));
			}
			System.out.println();
			int LIMIT=0;//LIMIT will hold the index of the first flight out of the current airport
			while(StackToObtainArrivalTime.top!=-1)//Until we do not pop all elements from stack do:
			{
				LIMIT=StackToObtainArrivalTime.pop();//Assign LIMIT, the index of the first flight out of the current airport. 
			}
			MinimumTime=Schedule[i].ArrivalTime[LIMIT];//Set the MinimumTime as the arrival time of the earliest flight reaching the next leg of the journey.
			i=Buffer.pop();//update the next airport in the route by popping the buffer.
		}
		System.out.println();
		Buffer.pop();//Empties the buffer stack
	}
}
