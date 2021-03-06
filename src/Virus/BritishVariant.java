//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Virus;
import Population.*;


public class BritishVariant implements IVirus
{
	public double contagionProbability(Person other) { //calculate the probability of person for contagion
			return 0.7 * other.contagionProbability();
	}
    public boolean tryToContagion(Person one, Person two) {
		//first person is sick , the other might be sick or not ,calculate the chances for contagion by the sick person
    	if (two instanceof Sick)
			return false;
    	if (((Sick)(one)).daysFromContagion()< 5)
    		return false;
		double d = one.getDistance(two.getLocation());
    	double p = two.contagionProbability()*Math.min(1,0.14*Math.exp(2-(0.25*d)));
    	return p >= Math.random();
    }
    public boolean tryToKill(Sick other) { //the function calculate if the sick people might die
    	double p ;
		long t = Simulation.Clock.now()-other.getContagiousTime();;
    	if(other.getAge() >= 0 && other.getAge() <=18)
			p = Math.max(0 , (0.01-(0.01*0.01)) * Math.pow(t-15,2)  );
		else
			p = Math.max(0 , (0.1-(0.01*0.1)) * Math.pow(t-15,2)  );
    	return (p>= Math.random());
    }
    public String getType() {
    	return "British";
    }
	public Boolean isEqual(IVirus other){
			return getType() == other.getType();
    }
}
