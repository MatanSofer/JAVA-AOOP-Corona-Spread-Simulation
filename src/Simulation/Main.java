//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Simulation;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import Country.*;
import GUI.*;
import IO.SimulationFile;
import Population.*;
import Virus.*;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Main {

	private static Map map = new Map();
	public static StatWindow statwindow;
	public static MainWindow mainwindow;
	private static boolean playing = false;
	private static boolean stop = true;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statwindow = new StatWindow();
				statwindow.setVisible(false);
				mainwindow = new MainWindow(statwindow);
				mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				statwindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
		});

		map.printSickPpl();// Print sick population.
		System.out.println("\n-------Simulating virus spread-------\n");
		simulate();// Simulates virus spread.
		System.out.println("\n--------------------------");
		System.out.println("END OF SIMULATION RESULT\n");
		map.printSickPpl();// Print sick population.

	}

	public static File loadFileFunc() {
		FileDialog fd = new FileDialog((Frame) null, "Please choose a file:", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getFile() == null)
			return null;
		File f = new File(fd.getDirectory(), fd.getFile());
		System.out.println(f.getPath());
		return f;
	}

	public static Map getMap() {
		return map;
	}

	public static void setMap(Map newMap) {
		map = newMap;
	}

	public static void simulate() {
		Person temp;
		IVirus virus;
		List<Integer> NewSickIndexes = new ArrayList<Integer>(); // this helps us to recognize new sick people so we can
																	// to skip on them and dont let them try contage new
																	// people
		int numberofTryingToContagion; // number of contagion tries
		int sickCounter = 0; // count number of sick for each settle
		int randomPersonFronSettle; // this var will get a random value in range of( 0 to settle population)
		int sliderValue;

		// Wait for a file to be uploaded or to resume
		while (!playing || stop)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for (int p = 0; p >= 0; p++) {

			updateAll();// Updates Gui

			// Wait for a file to be re-uploaded or to resume
			while (!playing || stop)
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			try {
				Thread.sleep(250 * MainWindow.getSliderValue());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Clock.nextTick();// move the clock one tick forward

			System.out.println(map); // print all settle string

			NewSickIndexes.clear();

			System.out.println("\"############################################   Simulation number : " + (p + 1)
					+ "    ############################################"); // the number of simulation

			// for each sattle check 0.2 sick people and each one from sick people will try
			// to contagion 5 health people

			simulateMapContagious();

			// for each settle all sick people who past 25 days from the day they were
			// nidbeku become cov...

			simulateMapRecover();

			// for each settle we have to check 0.03 from people who try to move to a
			// connected settle and to transfer them

			simulateMoveSettle();

			// if settle has more then 0 doses and also health people we have to vaccine
			// them and decrease this.doses;(1 is enough)

			vaccinateMap();

		}

	}

	public static void updateAll() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainwindow.updateMap();
				statwindow.updateTable();
			}
		});
	}

	public static void setPlaying(boolean playing) {
		Main.playing = playing;
	}

	public static boolean getPlay() {
		return playing;
	}

	public static void setStop(boolean stop) {
		Main.stop = stop;
	}

	public static boolean getStop() {
		return stop;
	}

	private static void simulateMapContagious() {
		IVirus virus;
		Person newSicko;
		for (int i = 0; i < getMap().getSettlements().length; i++) // run for each settle
		{
			for (int j = 0; j < getMap().getSettlements()[i].getSickPeople().size() * 0.2; j++) // run for 20% of people
																								// in the settle
			{
				virus = ((Sick) getMap().getSettlements()[i].getSickPeople().get(j)).getVirus();
				for (int k = 0; k < 3; k++) {
					if (virus.tryToContagion(getMap().getSettlements()[i].getSickPeople().get(j),
							getMap().getSettlements()[i].getNonSickPeople().get(k))
							&& getMap().getSettlements()[i].getNonSickPeople().size() > 4) {
						newSicko = getMap().getSettlements()[i].getNonSickPeople().get(k).contagion(virus);
						getMap().getSettlements()[i].addPerson(newSicko);
						getMap().getSettlements()[i].getNonSickPeople().remove(k);
						getMap().getSettlements()[i].setColor(getMap().getSettlements()[i].calculateRamzorGrade());
					}
				}

			}

		}

	}

	private static void simulateMapRecover() {
		// for each settle all sick people who past 25 days from the day they were
		// nidbeku become cov...
		Person conv;
		for (int i = 0; i < getMap().getSettlements().length; i++) // run for each settle
		{

			for (int j = 0; j < getMap().getSettlements()[i].getSickPeopleSize(); j++) {
				if (((Sick) getMap().getSettlements()[i].getSickPeople().get(j)).daysFromContagion() > 25) {
					conv = ((Sick) getMap().getSettlements()[i].getSickPeople().get(j)).recover();
					getMap().getSettlements()[i].addPerson(conv);
					getMap().getSettlements()[i].getSickPeople().remove(j);
					getMap().getSettlements()[i].setColor(getMap().getSettlements()[i].calculateRamzorGrade());

				}
			}
		}
	}

	private static void simulateMoveSettle() {
		// for each settle we have to check 0.03 from people who try to move to a
		// connected settle and to transfer them
		int size;
		int randomSettleIndex;
		List<Person> currentPpl;
		Settlement currentDestenation;
		for (Settlement settlement : getMap().getSettlements()) {
			if (settlement.getconnectedSettlements().size() > 0) {
				size = (int) (settlement.getSickPeopleSize() * 0.3);
				for (int i = 0; i < size; i++) { // For 30% of sick ppl
					currentPpl = settlement.getSickPeople();
					randomSettleIndex = ((int) ((Math.random()) * settlement.getconnectedSettlements().size()));
					currentDestenation = settlement.getconnectedSettlements().get(randomSettleIndex);
					if (currentPpl.get(i) != null)
						if (currentDestenation.transferPerson(currentPpl.get(i), settlement)) {// Attempts to transfer
																								// (Boolean)
							currentDestenation.addPerson(currentPpl.get(i));
							currentPpl.remove(i);
							settlement.setColor(settlement.calculateRamzorGrade());
						}
				}
				size = (int) (settlement.getNonSickPeopleSize() * 0.3);
				for (int i = 0; i < size; i++) { // For 30% of Non-sick ppl
					currentPpl = settlement.getNonSickPeople();
					randomSettleIndex = ((int) ((Math.random()) * settlement.getconnectedSettlements().size()));
					currentDestenation = settlement.getconnectedSettlements().get(randomSettleIndex);
					if (currentPpl.get(i) != null)
						if (currentDestenation.transferPerson(currentPpl.get(i), settlement)) {// Attempts to transfer
																								// (Boolean)
							currentDestenation.addPerson(currentPpl.get(i));
							currentPpl.remove(i);
							settlement.setColor(settlement.calculateRamzorGrade());
						}
				}
			}
		}
	}

	private static void vaccinateMap() {
		for (Settlement settlement : getMap().getSettlements()) {
			if (settlement.getNonSickPeopleSize() > 0 && settlement.getVaccineDose() > 0) {
				for (int i = 0; i < settlement.getNonSickPeopleSize(); i++)
					if (settlement.getNonSickPeopleSize() > 0 && settlement.getVaccineDose() > 0)
						if (settlement.getNonSickPeople().get(i) instanceof Healthy) {
							settlement.addPerson(((Healthy) settlement.getNonSickPeople().get(i)).vaccinate());
							settlement.getNonSickPeople().remove(settlement.getNonSickPeople().get(i));
							settlement.reduceOneVaccineDose();
						}

			}
		}

	}

}
