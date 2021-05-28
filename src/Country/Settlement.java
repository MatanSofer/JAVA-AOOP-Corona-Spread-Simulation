//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Country;
import java.util.*;

import IO.LogFile;
import Population.*;
import Virus.BritishVariant;
import Virus.ChineseVariant;
import Virus.IVirus;
import Virus.SouthAfricanVariant;
import Location.*;

public class Settlement implements Runnable {

	private String name; // all fields the included in settlement
	private Location location;
	private RamzorColor ramzorcolor;
	private double ramzorRating; // this is additional field for saving ramzor VALUE
	private int maxPopulation;
	private int vaccineDose = 0;
	private int deadpopulation = 0;
	private int deadCounterOnePer= 0; 
	private List<Settlement> connectedSettlements = new ArrayList<Settlement>();
	private List<Person> nonSickPeople;
	private List<Person> sickPeople = new ArrayList<Person>();
	private Map map;
	public Settlement() // default constructor (there isn't use now , maybe in future )
	{
		this.name = "NULL";
		this.location = null;
		this.nonSickPeople = null;
		this.ramzorcolor = null;
		this.ramzorRating = 0.4;
		this.vaccineDose = 0;
	}

	public Settlement(String name, Location location, List<Person> nonsick, RamzorColor ramzorcolor, int maxPopulation) // constructor
																														// for
																														// all
																														// fields
	{
		this.name = new String(name);
		this.location = new Location(location);
		this.nonSickPeople = new ArrayList<Person>(nonsick);
		this.ramzorcolor = ramzorcolor;
		this.maxPopulation = maxPopulation;
		this.vaccineDose = 0;
	}

	public Settlement(Settlement other) // copy constructor
	{
		this.name = new String(other.getName());
		this.location = new Location(other.getLocation());
		this.nonSickPeople = other.nonSickPeople;
		this.ramzorcolor = other.getColor();
		this.sickPeople = other.getSickPeople();
		this.maxPopulation = other.maxPopulation;

	}
	public void addReference(Map map)
	{
		this.map=map;
	}
	
	public  void simulateMapContagious() {
		IVirus virus;
		Person newSicko;
		//for (int i = 0; i < map.getSettlements().length; i++) // run for each settle
		//{
			for (int j = 0; j < getSickPeople().size() * 0.2; j++) // run for 20% of people
																								// in the settle
			{
				virus = ((Sick) getSickPeople().get(j)).getVirus();
				for (int k = 0; k < 3; k++) {
					if (getNonSickPeople().size()+1 < k )
						if (virus.tryToContagion(getSickPeople().get(j),getNonSickPeople().get(k))) {
							newSicko = getNonSickPeople().get(k).contagion(virus);
							addPerson(newSicko);
							getNonSickPeople().remove(k);
							setColor(calculateRamzorGrade());
						}
				}

			}

	//	}

	}
	
	public void simulateMapRecover() {
		// for each settle all sick people who past 25 days from the day they were

		Person conv;
		//for (int i = 0; i < map.getSettlements().length; i++) // run for each settle
		//{

			for (int j = 0; j < getSickPeopleSize(); j++) {
				if (((Sick) getSickPeople().get(j)).daysFromContagion() > 25) {
					conv = ((Sick) getSickPeople().get(j)).recover();
					addPerson(conv);
					getSickPeople().remove(j);
					setColor(calculateRamzorGrade());

				}
			}
		//}
	}
	
	public  void simulateMoveSettle() {
		// for each settle we have to check 0.03 from people who try to move to a
		// connected settle and to transfer them
		int size;
		int randomSettleIndex;
		List<Person> currentPpl;
		Settlement currentDestenation;
		//for (Settlement settlement : map.getSettlements()) {
			if (getconnectedSettlements().size() > 0) {
				size = (int) (getSickPeopleSize() * 0.3);
				for (int i = 0; i < size; i++) { // For 30% of sick ppl
					currentPpl =getSickPeople();
					randomSettleIndex = ((int) ((Math.random()) * getconnectedSettlements().size()));
					currentDestenation = getconnectedSettlements().get(randomSettleIndex);
					if (currentPpl.get(i) != null)
						if (currentDestenation.transferPerson(currentPpl.get(i),this)) {// Attempts to transfer
																								// (Boolean)
							currentDestenation.addPerson(currentPpl.get(i));
							currentPpl.remove(i);
							setColor(calculateRamzorGrade());
						}
				}
				size = (int) (getNonSickPeopleSize() * 0.3);
				for (int i = 0; i < size; i++) { // For 30% of Non-sick ppl
					currentPpl = getNonSickPeople();
					randomSettleIndex = ((int) ((Math.random()) * getconnectedSettlements().size()));
					currentDestenation = getconnectedSettlements().get(randomSettleIndex);
					if (currentPpl.get(i) != null)
						if (currentDestenation.transferPerson(currentPpl.get(i), this)) {// Attempts to transfer
																								// (Boolean)
							currentDestenation.addPerson(currentPpl.get(i));
							currentPpl.remove(i);
							setColor(calculateRamzorGrade());
						}
				}
			}
		//}
	}
	
	public void vaccinateMap() {
		
			if (getNonSickPeopleSize() > 0 && getVaccineDose() > 0) {
				for (int i = 0; i < getNonSickPeopleSize(); i++)
					if (getNonSickPeopleSize() > 0 && getVaccineDose() > 0)
						if (getNonSickPeople().get(i) instanceof Healthy) {
							addPerson(((Healthy) getNonSickPeople().get(i)).vaccinate());
							getNonSickPeople().remove(getNonSickPeople().get(i));
							reduceOneVaccineDose();
						}

			}
		

	}

	
	public void killPeople()
	{
		//int deadCounter= 0; 
		int onePercOfPoPopulation= (int)((getPeopleSize()+deadpopulation)*0.01);
		
		
		for(int i = 0 ; i < sickPeople.size() ; i++)
		{
			IVirus virus = ((Sick)sickPeople.get(i)).getVirus();
			if(virus.tryToKill(((Sick)sickPeople.get(i))))
			{
				
				sickPeople.remove(i);
				deadpopulation++;
				
				deadCounterOnePer++;
				
				
				if(deadCounterOnePer >= onePercOfPoPopulation )
				{
					
					deadCounterOnePer=0;
					LogFile.logSettlement(this);
					///logFile
				}
				
			}
			
		}
		
	}

	
	
	
	
	
	
	
	
	
	public RamzorColor calculateRamzorGrade() // this method implemented in "sons"
	{
		return ramzorcolor;
	}

	public double contagiousPercent() // calculate the percent value between 0 to 1 of sick people
	{
		double precent = (double) sickPeople.size() / ((double) getPeopleSize());

		return precent; // return the percent
	}

	public String contagiousPrecentString() {

		double precent = contagiousPercent();
		int precent_i = (int) ((precent) * 100);
		String precent_s = precent_i + "%";
		return precent_s;
	}

	public static Point randomLocation(Point point, Size size) // return random location in the settle
	{
		int x = ((int) (Math.random() * size.getWidth())) + point.getX();
		int y = ((int) (Math.random() * size.getHeight())) + point.getY();
		return new Point(x, y);
	}

	public boolean addPerson(Person other) // add another person to settle people array
	{
		if (other instanceof Sick) {
			this.sickPeople.add(other);
		} else {
			this.nonSickPeople.add(other);
		}
		return true;
	}

	public void jtableMakeSick() //for update add sick button in random varient
	{
		
		Person temp;
		IVirus[] random = { new ChineseVariant(), new BritishVariant(), new SouthAfricanVariant() };
		int randomVarient;
		for (int i = 0; i < 0.001 * nonSickPeople.size(); i++) {
			randomVarient = ((int) (Math.random() * 3));
			temp = nonSickPeople.get(i).contagion(random[randomVarient]);
			sickPeople.add(temp);
			nonSickPeople.remove(i);

		}
	}


	public boolean transferPerson(Person other, Settlement one) // for transfer person between settles
	{
		if (this.getPeopleSize() >= this.maxPopulation) {
			return false;
		}
		double randomProbability = Math.random();
		if (one.getColor().getInSettleProbability()
				* other.getSettlement().getColor().getInSettleProbability() <= randomProbability) {
			return false;
		}
		return true;
	}

	public List<Settlement> getconnectedSettlements() {  //returns connected settle array
		return this.connectedSettlements;
	}

	public void appendconnectedSettlements(Settlement other) { //add connected settle
		this.connectedSettlements.add(other);
	}

	public void printconnectedSettlements() {  //print all connected settles
		System.out.println("For settlement " + getName() + " connected settlements are :");
		for (int i = 0; i < this.connectedSettlements.size(); i++) {
			System.out.println(this.connectedSettlements.get(i).getName());
		}
	}

	public void setColor(RamzorColor newColor) // set the ramzor color
	{
		this.ramzorcolor = newColor;

	}

	public double getRamzorRating() // get ramzor rating
	{
		return this.ramzorRating;
	}

	public void setRamzorRating(double newRating) // set ramzor rating
	{
		this.ramzorRating = newRating;
	}

	public RamzorColor getColor() // get ramzor color
	{
		return this.ramzorcolor;
	}

	public List<Person> getSickPeople() // get sick population list
	{
		return this.sickPeople;
	}

	public List<Person> getNonSickPeople() // get non sick population list
	{
		return this.nonSickPeople;
	}

	public int getPeopleSize() {   //get all population size
		return getNonSickPeople().size() + getSickPeople().size();
	}

	public int getSickPeopleSize() { //get sickpeople size
		return sickPeople.size();
	}

	public int getNonSickPeopleSize() { //get non sick people size
		return nonSickPeople.size();
	}

	public int getVaccineDose() {  //returns number of vaccineDoses
		return this.vaccineDose;
	}

	public void addVaccineDose(int newDoses) { //for add vaccine button
		this.vaccineDose += newDoses;
	}

	public int getdeadPopulation() {  //return dead population number
		return this.deadpopulation;
	}

	public void reduceOneVaccineDose() { //reduce number of vaccine doses
		this.vaccineDose -= 1;
	}
	public String getName() // return name
	{
		return this.name;
	}

	public Location getLocation() // return location
	{
		return this.location;
	}

	public String getType() {
		return "Settlement";
	}

	public String toString() // to string
	{
		return "The name is: " + name + "\nThe location on the map is:" + location + "\nThe ramzor color is: "
				+ ramzorcolor + "\n";
	}

	public boolean isEqual(Settlement other) // is equal
	{
		return (this.getName() == other.getName() && this.getLocation() == other.getLocation()
				&& this.getSickPeople() == other.getSickPeople() && this.getColor() == other.getColor()
				&& this.getRamzorRating() == other.getRamzorRating()
				&& this.getNonSickPeople() == other.getNonSickPeople());
	}

	@Override
	public void run() {
		
		//
		
	}
	
	
	
}
	

