//* Authors: Maor Arnon (ID: 205974553) and Matan Sofer (ID:208491811)
package Simulation;

public class Clock 
{
	private static long time =0;  //static time of the Class
	
	public static long now() // return the current time
	{
		return time;
	}

	public static void nextTick()  //add 1 time unit the the current time
	{
		time++;
	}

	
}