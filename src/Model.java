import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is the model portion of the MVC
 * It will store a calendar, treemap of events, and a arraylist of listeners
 * @author Damon Luu
 * copyright 2016
 * version 1
 */
public class Model 
{
	private GregorianCalendar gCal = new GregorianCalendar();
	private ArrayList<ChangeListener> listeners = new ArrayList<>();
	private HashMap<String, ArrayList<Event>> events = new HashMap<>();

	/**
	 * Constructor for the model, will load events stored in savedEvents.ser
	 */
	public Model() 
	{
		load();
	}

	/**
	 * method that attaches change listeners to the model 
	 * @param l the change listener to attach
	 */
	public void attach(ChangeListener l)
	{
		listeners.add(l);
	}

	/**
	 * method that updates every listener
	 */
	public void update()
	{
		for (ChangeListener l : listeners)
		{
			l.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Creates a new event with given information
	 * @param eventName the event name
	 * @param date the event date
	 * @param startTime the start time of event
	 * @param endTime the end time of event
	 */
	public void newEvent(String eventName, String date, String startTime, String endTime)
	{
		Event event = new Event(eventName, date, startTime, endTime);
		String dateKey = event.getEventYear() + event.getEventMonth() + event.getEventDay();
		ArrayList<Event> eventArrayList = events.get(dateKey);

		if (eventArrayList == null)
		{
			eventArrayList = new ArrayList<>();
		}

		eventArrayList.add(event);
		events.put(dateKey, eventArrayList);
	}

	/**
	 * Gets all the events from a specified date
	 * @param date the date to get all events for
	 * @return allEvents all the events in string form
	 */
	public String allEvents(String date)
	{
		String[] split = date.split("/");
		String eventMonth = split[0];
		String eventDay = split[1];
		String eventYear = split[2];
		String dateKey = eventYear + eventMonth + eventDay;
		ArrayList<Event> e = events.get(dateKey);
		Collections.sort(e);
		String allEvents = "";
		for (Event tempEvent : e)
		{
			allEvents = allEvents + tempEvent.getEventStartTime() + " - " +  tempEvent.getEventEndTime() + ": " + tempEvent.getEventName() + "\n";
		}
		return allEvents;
	}

	/**
	 * increments the calendar by one day
	 */
	public void incrementDay()
	{
		gCal.add(Calendar.DAY_OF_MONTH, 1);
	}

	/**
	 * decrements the calendar by one day
	 */
	public void decrementDay()
	{
		gCal.add(Calendar.DAY_OF_MONTH, -1);
	}

	/**
	 * gets the 2 digit month in string form 
	 * @return the month
	 */
	public String getMonth()
	{
		String month = "" + (gCal.get(Calendar.MONTH)+1);
		if (month.length() == 1)
		{
			month = "0" + month;
		}
		return month;
	}

	/**
	 * gets the 4 digit year in string form
	 * @return the year
	 */
	public String getYear()
	{
		return "" + gCal.get(Calendar.YEAR);
	}

	/**
	 * gets the 2 digit day in string form
	 * @return the day
	 */
	public String getDay()
	{
		String day = "" + gCal.get(Calendar.DAY_OF_MONTH);
		if(day.length() == 1)
		{
			day = "0" + day;
		}
		return day;
	}

	/**
	 * gets the events hashmap
	 * @return events the hashmap
	 */
	public HashMap<String, ArrayList<Event>> getEvents()
	{
		return events;
	}

	/**
	 * helper method to check if there are other events at a specified time
	 * @param startTime the start time
	 * @param endTime the end time
	 * @return boolean true if there are conflicting events
	 */
	public boolean hasConflict(String startTime, String endTime)
	{
		String month = getMonth();
		String day = getDay();
		String year = getYear();		
		String dateKey = year + month + day;
		ArrayList<Event> currentDayEvents = events.get(dateKey);
		if (currentDayEvents == null)
		{
			return false;
		}
		else
		{
			String tempStartTime = startTime.replace(":", "");
			int tempStartTime2 = Integer.parseInt(tempStartTime);
			String tempEndTime = endTime.replace(":", "");
			int tempEndTime2 = Integer.parseInt(tempEndTime);
			for (Event e : currentDayEvents)
			{
				if((tempStartTime2 >= e.getEventStartTimeInt() && tempStartTime2 < e.getEventEndTimeInt()) 
						|| (tempStartTime2 <= e.getEventStartTimeInt() && tempEndTime2 >= e.getEventStartTimeInt()))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * gets the model calendar
	 * @return the Gregorian calendar 
	 */
	public GregorianCalendar getCalendar()
	{
		return gCal;
	}

	/**
	 * saves the events hash map object into a file
	 */
	public void save()
	{
		if(!events.isEmpty())
		{
			try
			{
				FileOutputStream fileOut = new FileOutputStream("savedEvents.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(events);
				out.close();
				fileOut.close();
			}
			catch (IOException i) 
			{
				i.printStackTrace();
			}
		}
	}

	/**
	 * loads the events hash map object back
	 */
	public void load()
	{
		try {
			FileInputStream fileIn = new FileInputStream("savedEvents.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			events = (HashMap<String, ArrayList<Event>>) in.readObject();
			in.close();
			fileIn.close();
		}
		catch(IOException i) 
		{
			return;
		}
		catch(ClassNotFoundException c) 
		{
			System.out.println("HashMap class not found");
			c.printStackTrace();
			return;
		}
	}
	
	/**
	 * Method to check if the formatting of time is correct
	 * @param s the time such as 12:00
	 * @return boolean true if time is a valid time
	 */
	public Boolean validTime(String s)
	{
		return s.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$");
	}
}
