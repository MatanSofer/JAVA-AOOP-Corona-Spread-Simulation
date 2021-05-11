package IO;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import Country.Settlement;

public abstract class LogFile {

	private static File file = null;

	public static File SaveTable(String fileName) {

		file = saveFileFunc(fileName);
		System.out.println("Created a Log File\n");
		try (FileWriter out = new FileWriter(file,true)) {
			out.append("Log File");
			out.append('\n');
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return file;
	}

	public static void logSettlement(Settlement settlement) {
		
		if (file != null) {
			int day = (int) (Simulation.Clock.now() / Simulation.Clock.getticksPerDay());
			long tick = Simulation.Clock.now();
			int numberOfSick = settlement.getSickPeopleSize();
			int numberOfDead = settlement.getdeadPopulation();
			String name = settlement.getName();

			try (FileWriter out = new FileWriter(file,true)) {
				out.append("Log: Simulation Day-" + day + " Tick- " + tick + " Settlement Name- " + name + " Sick population- " + numberOfSick + " Dead population- " + numberOfDead);
				out.append('\n');
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			
		}
	}

	private static File saveFileFunc(String fileName) {
		// Instead of "(Frame) null" use a real frame, when GUI is learned
		FileDialog fd = new FileDialog((Frame) null, "Please choose a file:", FileDialog.SAVE);
		fd.setFile(fileName + ".txt");
		fd.setVisible(true);
		if (fd.getFile() == null)
			return null;
		File f = new File(fd.getDirectory(), fd.getFile());
		System.out.println(f.getPath());
		return f;
	}
}
