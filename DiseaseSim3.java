import java.util.*;
import java.text.DecimalFormat;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
*<h1> Disease Simulation Code #1</h1>
*This program is designed to simulated the spread of an
*epidemic in a large population.
*@author James Hollister
*@version 1.0
*@since 2018-10-20
*/


public class DiseaseSim3{


/**
*Here is the main method, which will take in parameters from the
*input file, simulate the disease, and print output to
*the out file.
*/
public static void main(String[] args) throws Exception{


//Fields
int totPop;
double totSimTime;
double currentSimTime = 0;
int eventType;
ArrayList<Double> nextEventTime = new ArrayList<Double>();
ArrayList<Person> popArray = new ArrayList<Person>();
ArrayList<Person> susArray = new ArrayList<Person>();
ArrayList<Person> infArray = new ArrayList<Person>();
ArrayList<Person> recArray = new ArrayList<Person>();
ArrayList<Tracker> infectionQueue = new ArrayList<Tracker>();
ArrayList<Tracker> recoveryQueue = new ArrayList<Tracker>();
ArrayList<Tracker> vacTimeArray = new ArrayList<Tracker>();
ArrayList<Tracker> deathQueue = new ArrayList<Tracker>();
double avgContactRate;
double avgInfectionRate;
double avgContactSD;
double avgRecoveryTime;
double infPercentage = 0.0;
double infTotal;
double vacRate;
double vacNo = 0.0;
double quarRate = 0;
double quarNo = 0;
double totVacNo = 0;
double totVacNo1 = 0;
double totInf = 0.0;
int infNo;
int recNo = 0;
Random random = new Random();
SortTrackerArray sorter = new SortTrackerArray();
int initialInfected;
int susNo = 0;
int range;
int qInfContacts;
double noVac;
int susTrack = 0;
double deathRate;
int deathNo = 0;
int vacCreate;
int maxInf = 0;
double timeOfMax = 0;

//Read in data file

File address = new File("/Users/jameshollister/Documents/GibHub/Disease-Sim/DiseaseInfo.txt");

String[] info;
Scanner reader = new Scanner(address);

//Create read file for stats (.csv)
String savefile1 = ("/Users/jameshollister/Documents/Java/DiseaseSim/DiseaseSimStats3.csv");
PrintWriter out = new PrintWriter(savefile1);
out.println("Time,SusNo,InfectedNo,RecNo,QuarNo,VacNo,TotInf,DeathNo");

//Read in data stuff for initialization
String line = reader.nextLine();
info = line.split(" ");

totSimTime = Double.parseDouble(info[0]);
totPop = Integer.parseInt(info[1]);
avgContactRate = Double.parseDouble(info[2]);
avgContactSD = Double.parseDouble(info[3]);
range = (int) Math.round(avgContactSD);
avgInfectionRate = Double.parseDouble(info[4]);
avgRecoveryTime = Double.parseDouble(info[5]);
initialInfected = Integer.parseInt(info[6]);
vacRate = Double.parseDouble(info[7]);
quarRate = Double.parseDouble(info[8]);
noVac = Double.parseDouble(info[9]);
deathRate = Double.parseDouble(info[10]);
vacCreate = Integer.parseInt(info[11]);

infNo = initialInfected;
totInf = initialInfected;

//Check contactRate:

if(avgContactRate > totPop){

   System.out.println("Error: average contact rate is larger than the total population. Closing simulation...");
   System.exit(0);

   }

//Create default objects
Person per = new Person();
Disease dis = new Disease();

//Fill popArray and susArray

for(int i = 0; i < (totPop - initialInfected); i++){

   Person person = new Person(1, 0.0, 0.0, 0.0, 0.0);
   person.avgContact = randNormal(avgContactRate, avgContactSD);
   susArray.add(person);
   popArray.add(person);

   }

   susNo = susArray.size();

for(int i = 0; i < initialInfected; i++){

   Person person = new Person(2, 0.0, 0.0, 0.0, 0.0);
   person.avgContact = randNormal(avgContactRate, avgContactSD);

   //Decide whether or not person dies
   if(random.nextDouble() < deathRate){

   person.deathTime = per.randExp(avgRecoveryTime);
   Tracker death = new Tracker(person.deathTime, i);
   deathQueue.add(death);
   infArray.add(person);
   popArray.add(person);

      }

   else{
   person.recoveredTime = per.randExp(avgRecoveryTime);
   Tracker track = new Tracker(person.recoveredTime, i);
   recoveryQueue.add(track);
   infArray.add(person);
   popArray.add(person);
   }


   }

sorter.sort(recoveryQueue);
sorter.sort(deathQueue);

Disease disease = new Disease(avgInfectionRate, 0.0, avgRecoveryTime);

nextEventTime.add(0.0);
nextEventTime.add(1.0e30);
if(recoveryQueue.size() != 0){
   nextEventTime.add(recoveryQueue.get(0).time);
}
else{
   nextEventTime.add(1.0e30);
   }

nextEventTime.add(1.0e30);
if(deathQueue.size() != 0){
   nextEventTime.add(deathQueue.get(0).time);
}
else{
   nextEventTime.add(1.0e30);
   }


//Starting simulation

while(infNo != 0){

//System.out.println("Are we getting an invalid event?");
//System.out.println("\n\n\n");
double minimumEventTime = 1.0e29;
eventType = -1;

for(int i = 0; i < 5; i++){

   if(nextEventTime.get(i) < minimumEventTime){

      minimumEventTime = nextEventTime.get(i);
      eventType = i;

      //System.out.println("Are we stuck in here?");

      }

   }



if(eventType == -1){

   System.out.println("Error with event time. Ending simulation...");
   currentSimTime = totSimTime;

   }

//Update the clock
currentSimTime = minimumEventTime;

//New day event:
if(eventType == 0){

  System.out.println("NEW DAY: calculating new infected people... Current sim time: " + currentSimTime + "\n");

   //First, clear out infected people from susArray:

   for(int i = susArray.size() - 1; i >= 0; i--){

      if(susArray.get(i).type == 2){

         susArray.remove(i);

         }

   susTrack = susArray.size();

      }


   //FIRST, calculate vaccinated ppl, IF limit has not been reached

   if(totInf > vacCreate){

   if(totVacNo <= (totPop - noVac)){
    vacNo = vacRate * susNo;
    vacNo = (int) vacNo;
    totVacNo += vacNo;
    if(totVacNo >= (totPop - noVac)){

      double tempTotVac = totVacNo;
      totVacNo = totPop - noVac;
      vacNo = vacNo - (tempTotVac - totVacNo);


      }

   //Remove vacNo people from susArray, add rec, sub sus

   for(int i = 0; i < vacNo; i++){

   susArray.remove(0);
   //System.out.println("Vaccinating person " + i);
   //recNo++;
   susTrack--;
   //Schedule Times for people to get vaccinated (These people cannot get infected in the day)
   Tracker fake = new Tracker(currentSimTime + (random.nextDouble() * 24), 0);
   vacTimeArray.add(fake);


      }

      sorter.sort(vacTimeArray);



      }
      }

     if(vacTimeArray.size() != 0){
      nextEventTime.set(3, vacTimeArray.get(0).time);
      }

      else{

      nextEventTime.set(3, 1.0e30);

         }


   infTotal = infNo;

   //Calculate the number of quarantined individuals
   quarNo = quarRate * infTotal;
   quarNo = (int) quarNo;
  // out.println(currentSimTime + "," + susNo + "," + infNo + "," + recNo + "," + quarNo + "," + totVacNo + "," + totInf);



   //Calculation of percentage of infected people in population (taking into account quarantined individuals
   double infPercentageQ = (infTotal - quarNo) / (totPop - quarNo);
   infPercentage = infTotal / totPop;

  // System.out.println("Percenage of population infected: " + infPercentage);

   int checker = 0;

   for(int i = 0; i < susArray.size(); i++){


      //First, figure out number of infected people person i comes in contact with:

      int contacts = 0;
      int infContacts = 0;
      int infChecker = 0;
      contacts = numOfContacts(susArray.get(i).avgContact, range);

      //System.out.println("Number of contacts for person " + i + ": " + contacts);
      infContacts = randBinomial(contacts, infPercentage);

      //Now, find out how many of these infected people are quarantined, and thus removed from contacts:
      qInfContacts = randBinomial(infContacts, quarRate);

      //Now subract the quarantined people
      infContacts -= qInfContacts;



     //System.out.println("Number of infected contacts for person " + i + ": " + infContacts);

      //Now, find out if person becomes infected.

      infChecker = randBinomial(infContacts, avgInfectionRate);

      //System.out.println("Does person " + i + " become infected? 0 = no, >0 = yes: " + infChecker);

      //Checking to see which people get infected, and infecting them at a time throughout the day
      if(infChecker != 0){

         //System.out.println("Person " + i + "becomes infected at time " + currentSimTime);
         susArray.get(i).infectionTime = currentSimTime + (random.nextDouble() * 24);
         Tracker track = new Tracker(susArray.get(i).infectionTime, i);
         infectionQueue.add(track);
         checker++;


         }


      }

   //SORT infectionQueue and adding the first time to the event time arrayList
   sorter.sort(infectionQueue);

   if(checker != 0){
      nextEventTime.set(1, infectionQueue.get(0).time);

      }

   nextEventTime.set(0, currentSimTime + 24.0);

   }

//Suseptible ---> Infected
if(eventType == 1){

   infNo++;

   if(infNo > maxInf){

      maxInf = infNo;
      timeOfMax = currentSimTime;

      }

   totInf++;
   //System.out.println("New person infected at " + currentSimTime + " hours!\n");
  //System.out.println("This is the no. " + infNo + " person to be infected!");
   susNo--;
   out.println(currentSimTime + "," + susNo + "," + infNo + "," + recNo + "," + quarNo + "," + totVacNo1 + "," + totInf + "," + deathNo);
   //Do the array switching and such

   susArray.get(infectionQueue.get(0).index).type = 2;

   //Decide whether or not the infected individual dies:
   if(random.nextDouble() < deathRate){

      susArray.get(infectionQueue.get(0).index).deathTime = currentSimTime + per.randExp(disease.avgRecovery);
      infArray.add(susArray.get(infectionQueue.get(0).index));

      Tracker deathTrack = new Tracker(susArray.get(infectionQueue.get(0).index).deathTime, infArray.size() - 1);

      deathQueue.add(deathTrack);

      sorter.sort(deathQueue);

      nextEventTime.set(4, deathQueue.get(0).time);



      }

   else{
   susArray.get(infectionQueue.get(0).index).recoveredTime = currentSimTime + per.randExp(disease.avgRecovery);
   infArray.add(susArray.get(infectionQueue.get(0).index));

   Tracker recTrack = new Tracker(susArray.get(infectionQueue.get(0).index).recoveredTime, infArray.size() - 1);

   recoveryQueue.add(recTrack);

   sorter.sort(recoveryQueue);

   nextEventTime.set(2, recoveryQueue.get(0).time);

   }

   infectionQueue.remove(0);

   if(infectionQueue.size() != 0){

      nextEventTime.set(1, infectionQueue.get(0).time);


      }

   else{

      nextEventTime.set(1, 1.0e30);

      }


   }

//Infected --> Recovered
if(eventType == 2){

   //System.out.println("Are we in this loop?");

   infNo--;
   recNo++;
   out.println(currentSimTime + "," + susNo + "," + infNo + "," + recNo + "," + quarNo + "," + totVacNo1 + "," + totInf + "," + deathNo);

   recoveryQueue.remove(0);

      if(recoveryQueue.size() != 0){

      nextEventTime.set(2, recoveryQueue.get(0).time);


      }

   else{

      nextEventTime.set(2, 1.0e30);

      }




   }


//Vaccines
if(eventType == 3){

   //System.out.println("VACCINE!");
   totVacNo1++;
   recNo++;
   susNo--;
   out.println(currentSimTime + "," + susNo + "," + infNo + "," + recNo + "," + quarNo + "," + totVacNo1 + "," + totInf + "," + deathNo);

   vacTimeArray.remove(0);

      if(vacTimeArray.size() != 0){

      nextEventTime.set(3, vacTimeArray.get(0).time);


      }

   else{

      nextEventTime.set(3, 1.0e30);

      }





   }


//Infected --> Recovered
if(eventType == 4){

   //System.out.println("Are we in this loop?");

   infNo--;
   deathNo++;
   out.println(currentSimTime + "," + susNo + "," + infNo + "," + recNo + "," + quarNo + "," + totVacNo1 + "," + totInf + "," + deathNo);

   deathQueue.remove(0);

      if(deathQueue.size() != 0){

      nextEventTime.set(4, deathQueue.get(0).time);


      }

   else{

      nextEventTime.set(4, 1.0e30);

      }




   }

}

/*
totSimTime = Double.parseDouble(info[0]);
totPop = Integer.parseInt(info[1]);
avgContactRate = Double.parseDouble(info[2]);
avgContactSD = Double.parseDouble(info[3]);
range = (int) Math.round(avgContactSD);
avgInfectionRate = Double.parseDouble(info[4]);
avgRecoveryTime = Double.parseDouble(info[5]);
initialInfected = Integer.parseInt(info[6]);
vacRate = Double.parseDouble(info[7]);
quarRate = Double.parseDouble(info[8]);
noVac = Double.parseDouble(info[9]);
deathRate = Double.parseDouble(info[10]);
vacCreate = Integer.parseInt(info[11]);
*/

//Print final statistics
System.out.println("------------SIMULATION COMPLETE------------");
System.out.println("Input Parameters:");
System.out.println("Population Size: " + totPop + " people.");
System.out.println("Average No. of Contacts: " + avgContactRate + " people per day.");
System.out.println("Standard Deviation of No. of Contacts: " + avgContactSD + " people per day.");
System.out.println("Probability of Becoming Infected: " + avgInfectionRate);
System.out.println("Average Recovery Time: " + avgRecoveryTime + " hours.");
System.out.println("Initial No. of Individuals Infected: " + initialInfected + " people.");
System.out.println("Vaccination Rate: " + vacRate);
System.out.println("Quarantine Percentage: " + quarRate);
System.out.println("No. of People Without Vaccination: " + noVac + " people.");
System.out.println("Probability of Dying When Infected: " + deathRate);
System.out.println("No. Of Infections Before Vaccinations Provided: " + vacCreate + " infections.");
System.out.println("\n\n\nStatistics:");
System.out.println("Length of Disease: " + currentSimTime + " hours (" + (currentSimTime / 24.0) + " days.");
System.out.println("Total No. Of People Infected: " + totInf + " people.");
System.out.println("Total Deaths: " + deathNo + " people.");
System.out.println("Max No. Of People Infected at One Time: " + maxInf + " people.");
System.out.println("Time of Max Infections: " + timeOfMax + " hours.");
System.out.println("No. Of Vaccinations: " + totVacNo1 + " vaccinations.");
System.out.println("\n\n");

if(totInf > 0.67 * (double) totPop){

System.out.println("You created a powerful disease which broke into an epidemic!");

   }

 else if(totInf < 0.67 * (double) totPop && totInf > 0.5 * (double) totPop){

   System.out.println("You created a disease which infected over half the population!");

   }

else if(totInf < 0.5 * (double) totPop && totInf > 0.25 * (double) totPop){

   System.out.println("You created a decently strong disease, but it was eventually eradicated!");

   }

else{

   System.out.println("Your disease was unable to spread throughout much of the population!");

   }
//Close file
out.close();


}

public static int randBinomial(int trials, double prob){

   Random random = new Random();
   int x = 0;

      for(int k = 0; k < trials; k++){

         if(random.nextDouble() < prob){

            x++;

            }

         }

      return(x);


   }

public static double randNormal(double mean, double sd){

   Random random = new Random();

   double randVar = random.nextGaussian() * sd + mean;
   return(randVar);

   }


public static int numOfContacts(double mean, int range){

   Random random = new Random();

   int adder;

   mean = Math.round(mean);
   int newMean = (int) mean;

   if(range % 2 == 0){

      adder = range / 2;

      }

   else{

      range += 1;
      adder = range / 2;

      }

   int max = newMean + adder;
   int min = newMean - adder;

   int randVar = random.nextInt(max - min + 1) + min;

   return(randVar);


   }
}
