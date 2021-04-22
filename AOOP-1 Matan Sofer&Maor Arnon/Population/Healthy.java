//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Population;
import Location.*;
import Country.*;
import Virus.*;
import Simulation.*;


public class Healthy extends Person
{
	
	private static double infectionCoefficient=1;  //static as requested
	
	public Healthy(int age , Point location , Settlement settlement)  //constructor for all fields
	{
		super(age , location , settlement);
	}
	public Person vaccinate() // returns new similar person with different health condition , now he vaccinated
	{
		Point locationCopy = new Point(getLocation());
		Settlement settlementCopy = new Settlement(getSettlement());
		Vaccinated vaccinated = new Vaccinated(getAge(),locationCopy,settlementCopy,Clock.now());
		return vaccinated;
	}
	
	public Person contagion(IVirus virus) // returns new similar person with different health condition , now he sick
	{
		Point locationCopy = new Point(getLocation());
		Settlement settlementCopy = new Settlement(getSettlement());
		Sick sickperson = new Sick(getAge(),locationCopy,settlementCopy,Clock.now(),virus);
		return sickperson;
	}

	public double contagionProbability() // return the contagionProbability static double
	{
		return infectionCoefficient;
	}
	public String getType()
	{
		return "Healty";
	}
	
	public boolean isEqual(Healthy other) //is equal
	{
		return super.isEqual((Person)other) ;
	}
}