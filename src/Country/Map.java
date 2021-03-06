//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Country;

import java.util.*;
import java.util.concurrent.CyclicBarrier;
import javax.swing.SwingUtilities;
import GUI.MainWindow;
import GUI.StatWindow;
import Simulation.Main;
import Virus.VirusManager;


public class Map implements Iterable<Settlement> {

	private List<Settlement> settlements = new ArrayList<Settlement>();  //implements iterable
	private boolean playing = false;
	private boolean stop = false;
	public CyclicBarrier cyclic ;
	

	 public Iterator<Settlement> iterator() {
	        return settlements.stream().iterator();
	    }
	
	public void spawn_all(){
		for(Settlement s : this)
		{
			new Thread(s).start();
		}
	}
	public CyclicBarrier getBarrier() {
		return cyclic;
	}
	public Map(List<Settlement> other) { // constructor get a dynamic array but we convert it to static array(as field type)
		this.settlements = other;
	}
	
	public Map() {
		this.settlements = new ArrayList<Settlement>();
	}
	
	public void setMap(Map map){
		System.out.println("number of settlements are " + this.settlements.size());
		this.settlements = map.getSettlements();
		for (int i = 0; i < map.getSettlements().size(); i++) {
			this.settlements.set(i,map.getSettlements().get(i));
		}
		
	}

	public List<Settlement> getSettlements(){ // return the list of settlements
		return this.settlements;
	}

	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean getPlay() {
		return playing;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean getStop() {
		return stop;
	}

	public  void updateAll(MainWindow mainwindow , StatWindow statwindow) { //update windows

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainwindow.updateMap();
				statwindow.updateTable();
			}
		});

	}
	
	
	public boolean isEqual(Map other) { // is equal
		if (this.getSettlements().size() != (other.getSettlements().size()))
			return false;
		for (int i = 0; i <= this.getSettlements().size(); i++) {
			if (!this.getSettlements().get(i).isEqual(other.getSettlements().get(i)))
				return false;
		}
		return true;
	}


	public void printSickPpl() {

		// check number of sick for each settle , then print it .
		int sickCounter;

		for (Settlement settle : this) {
			sickCounter = settle.getSickPeopleSize();
			System.out
			.println("Settlement " + settle.getName() + " Sick number are : " + sickCounter);

		}
	}

	

	public String toString() {
		String settlementlist = "-------toString for all settlements-------\n";
		for( Settlement settle : this)
		{
			
			settlementlist += settle.toString()+"\n";
		}
		return settlementlist;
	}

	public Settlement at(int rowIndex) 
	{
		
		return this.getSettlements().get(rowIndex);
	}
	
	

}
