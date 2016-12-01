import java.io.Serializable;

/**
 * Event class which holds information to an event
 * @author Damon
 * copyright 2016
 * version 1
 */
public class Event implements Serializable, Comparable<Event>
{

	/**
	 * auto generated serial 
	 */
	private static final long serialVersionUID = -5487475370554751363L;

	private String eventName;
	private String startTime;
	private String endTime;
	private String eventMonth;
	private String eventDay;
	private String eventYear;

	/**
	 * constructor for an event
	 * @param eventName the event name
	 * @param date the date of the event
	 * @param startTime start time of the event
	 * @param endTime end time of the event
	 */
	public Event(String eventName, String date, String startTime, String endTime) 
	{
		this.eventName = eventName;
		this.startTime = startTime;
		this.endTime = endTime;

		String[] split = date.split("/");
		this.eventMonth = split[0];
		this.eventDay = split[1];
		this.eventYear = split[2];
	}	

	/**
	 * gets the event month
	 * @return month
	 */
	public String getEventMonth()
	{
		return eventMonth;
	}

	/**
	 * gets the event day
	 * @return day
	 */
	public String getEventDay()
	{
		return eventDay;
	}

	/**
	 * gets the event year
	 * @return year
	 */
	public String getEventYear()
	{
		return eventYear;
	}

	/**
	 * gets the event name
	 * @return event name
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * gets the event start time
	 * @return event start time
	 */
	public String getEventStartTime()
	{
		return startTime;
	}

	/**
	 * gets the event end time
	 * @return event end time
	 */
	public String getEventEndTime()
	{
		return endTime;
	}

	/**
	 * gets the event start time in integer form
	 * @return event start time int
	 */
	public int getEventStartTimeInt()
	{
		String tempStartTime = startTime.replace(":", "");
		return Integer.parseInt(tempStartTime);
	}

	/**
	 * gets the event end time in integer form
	 * @return event end time int
	 */
	public int getEventEndTimeInt()
	{
		String tempEndTime = endTime.replace(":", "");
		return Integer.parseInt(tempEndTime);
	}

	/**
	 * compares two events by start time
	 */
	public int compareTo(Event o) 
	{
		return this.startTime.compareTo(o.startTime);
	}
}