import java.util.*;

public class Person{

//Fields

int type;
double avgContact;
double infectionTime;
double recoveredTime;
double deathTime;


//Constructors:

public Person(){

}

public Person(int type, double avgContact, double infectionTime, double recoveredTime, double deathTime){

   this.type = type;
   this.avgContact = avgContact;
   this.infectionTime = infectionTime;
   this.recoveredTime = recoveredTime;
   this.deathTime = deathTime;


   }


//Methods

public double randExp(double mean){

   Random random = new Random();
   double randVar = Math.log(1 - random.nextDouble())/(-1/mean);
   return(randVar);


   }   









}