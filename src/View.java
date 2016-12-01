import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 *	enum for each of the months
 */
enum MONTHS
{
	January, February, March, April, May, June, July, August, September, October, November, December;
}

/**
 * enum for each of the week days
 *
 */
enum DAYS
{
	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
}

/**
 * This class is the view portion of the MVC
 * It will show a frame that has multiple panels
 * @author Damon Luu
 * copyright 2016
 * version 1
 */
public class View extends JFrame implements ChangeListener
{
	private Model model;
	private static DAYS[] arrayOfDays = DAYS.values();
	private static MONTHS[] arrayOfMonths = MONTHS.values();
	private JTextPane eventsArea = new JTextPane();
	private ArrayList<JButton> dayButtons = new ArrayList<>();
	private int lastHighlight = -1;
	private int currentMonth;
	private JTextField monthTextField;
	private JPanel days = new JPanel();
	private JPanel leftPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JTextField weekDays = new JTextField("  Sun   Mon   Tue   Wed   Thu    Fri     Sat");
	private JLabel eventLabel;

	public View(Model model) 
	{
		this.model = model;
		currentMonth = model.getCalendar().get(Calendar.MONTH);
		setTitle("GUI Calendar");
		setPreferredSize(new Dimension(750, 425));
		setLayout(new FlowLayout());

		JButton leftButton = new JButton("<");
		leftButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				model.decrementDay();
				model.update();
			}
		});

		JButton rightButton = new JButton(">");
		rightButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				model.incrementDay();
				model.update();
			}
		});

		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				createClicked();
				model.update();
			}
		});

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				model.save();
				dispose();
			}
		});

		dayButtonGenerator();
		days.setLayout(new GridLayout(0,7));
		days.setPreferredSize(new Dimension(350, 200));
		for (JButton b : dayButtons)
		{
			days.add(b);
		}

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setPreferredSize(new Dimension(350, 350));
		showMonth();
		showWeekDays();
		buttonPanel.add(createButton);
		buttonPanel.add(leftButton);
		buttonPanel.add(rightButton);
		buttonPanel.add(quitButton);
		leftPanel.add(buttonPanel);
		leftPanel.add(monthTextField);		
		leftPanel.add(weekDays);
		leftPanel.add(days);		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(350, 350));
		String weekDay = "" + arrayOfDays[model.getCalendar().get((Calendar.DAY_OF_WEEK))-1];
		eventLabel = new JLabel(weekDay + " " + model.getMonth() + "/" + model.getDay() + "/" + model.getYear());
		eventLabel.setPreferredSize(new Dimension(150, 50));
		eventLabel.setFont(new Font("Serif", Font.BOLD, 30));
		rightPanel.add(eventLabel);
		rightPanel.add(eventsArea);	
		dayButtonClicked(model.getCalendar().get(Calendar.DAY_OF_MONTH));
		eventsArea.setPreferredSize(new Dimension(350, 350));
		eventsArea.setEditable(false);
		add(leftPanel);
		add(rightPanel);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * method that will change will show all events on the day clicked
	 * @param n the day to show events for
	 */
	public void dayButtonClicked(int n)
	{
		model.getCalendar().set(Calendar.DAY_OF_MONTH, n);
		String dateKey = model.getYear() + model.getMonth() + model.getDay();
		String date =  model.getMonth() + "/" + model.getDay() + "/" + model.getYear();
		String allEvents = "";
		if (model.getEvents().get(dateKey) != null)
		{
			allEvents = model.allEvents(date);
		}
		String weekDay = "" + arrayOfDays[model.getCalendar().get((Calendar.DAY_OF_WEEK))-1];
		eventLabel.setText(weekDay + ", " + model.getMonth() + "/" + model.getDay() + "/" + model.getYear());

		if (allEvents.isEmpty())
		{
			eventsArea.setText("No Events Today!! \n");
		}
		else
		{
			eventsArea.setText("All Events: \n" + allEvents);
		}
		highlightDay(model.getCalendar().get(Calendar.DAY_OF_MONTH)-1);

	}

	/**
	 * Method that is called whenever a user makes a change, required by the model
	 */
	public void stateChanged(ChangeEvent e) 
	{
		if (model.getCalendar().get(Calendar.MONTH) != currentMonth)
		{
			currentMonth = model.getCalendar().get(Calendar.MONTH);
			monthTextField.setText(arrayOfMonths[model.getCalendar().get(Calendar.MONTH)] + " " + model.getCalendar().get(Calendar.YEAR));
			leftPanel.remove(days);
			dayButtons.clear();
			days.removeAll();
			dayButtonGenerator();
			lastHighlight = -1;
			highlightDay(model.getCalendar().get(Calendar.DAY_OF_MONTH)-1);

			for(JButton b : dayButtons)
			{
				days.add(b);
			}

			leftPanel.add(days);

			dayButtonClicked(model.getCalendar().get(Calendar.DAY_OF_MONTH));
			repaint();
		}
		else
		{
			int currentDay = model.getCalendar().get(Calendar.DAY_OF_MONTH);
			dayButtonClicked(currentDay);
			highlightDay(currentDay-1);
		}
	}

	/**
	 * Helper method to create invisible unclickable buttons for the days before the
	 * start day on calendar
	 */
	public void dummyDays()
	{
		GregorianCalendar temp = (GregorianCalendar)model.getCalendar().clone();
		temp.set(Calendar.DAY_OF_MONTH, 1);
		int blank = temp.get(Calendar.DAY_OF_WEEK);
		for (int i = 1; i < blank; i++)
		{
			JButton blankButton = new JButton();
			blankButton.setBorder(BorderFactory.createEmptyBorder());
			blankButton.setEnabled(false);
			days.add(blankButton);
		}
	}

	/**
	 * Helper method to generate a button for each day of the month
	 * and add it into the button ArrayList
	 */
	public void dayButtonGenerator()
	{
		dummyDays();
		for (int i = 1; i <= model.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH); i++)
		{
			JButton dayI = new JButton("" + (i));
			dayI.setBackground(Color.WHITE);
			final int j = i;
			dayI.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dayButtonClicked(j);
					highlightDay(j-1);
				}
			});
			dayButtons.add(dayI);
		}
	}

	/**
	 * Method that highlights the current day that is showing
	 * @param n the number of the day
	 */
	public void highlightDay(int n)
	{
		dayButtons.get(n).setBorder(BorderFactory.createLineBorder(Color.RED, 3));
		if (lastHighlight != n && lastHighlight != -1)
		{
			JButton temp = new JButton();
			Border defaultBorder = temp.getBorder();
			dayButtons.get(lastHighlight).setBorder(defaultBorder);
		}
		lastHighlight = n;
	}

	/**
	 * Helper method to make a a JTextfield that displays the month and year
	 */
	public void showMonth()
	{
		monthTextField = new JTextField(arrayOfMonths[model.getCalendar().get(Calendar.MONTH)] + " " + model.getCalendar().get(Calendar.YEAR));
		monthTextField.setPreferredSize(new Dimension(150, 60));
		monthTextField.setFont(new Font("Serif", Font.BOLD, 30));
		monthTextField.setOpaque(false);
		monthTextField.setBorder(null);
		monthTextField.setEditable(false);
		monthTextField.setHorizontalAlignment(JTextField.CENTER);
	}

	/**
	 * Helper method that shows the weekdays Sunday - Saturday
	 */
	public void showWeekDays()
	{
		weekDays.setPreferredSize(new Dimension(350, 40));
		weekDays.setSize(weekDays.getPreferredSize());
		weekDays.setFont(new Font("Serif", Font.BOLD, 20));
		weekDays.setOpaque(false);
		weekDays.setBorder(null);
		weekDays.setEditable(false);
	}

	/**
	 * Method that creates a frame when a user clicks on "create"
	 * Allows the user to set a event name, start time, and end time
	 * Save button will accept event and store it or throw an error if format is incorrect
	 */
	public void createClicked()
	{
		JFrame createFrame = new JFrame("Create a new Event");
		createFrame.setPreferredSize(new Dimension(350, 250));
		createFrame.setLayout(new BorderLayout());

		String weekDay = "" + arrayOfDays[model.getCalendar().get((Calendar.DAY_OF_WEEK))-1];
		String date = weekDay + " " + model.getMonth() + "/" + model.getDay() + "/" + model.getYear();
		JLabel dateView = new JLabel(date);
		dateView.setPreferredSize(new Dimension(150, 60));
		dateView.setFont(new Font("Serif", Font.BOLD, 20));
		dateView.setHorizontalAlignment(JTextField.CENTER);	

		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		JPanel eventPanel = new JPanel();
		eventPanel.setPreferredSize(new Dimension(10, 20));
		JLabel eventNameLabel = new JLabel("Event Name: ");
		JTextField eventNameText = new JTextField("Untitled Event", 20);
		eventPanel.add(eventNameLabel);
		eventPanel.add(eventNameText);

		JPanel startTimePanel = new JPanel();
		startTimePanel.setPreferredSize(new Dimension(10, 20));
		JLabel startTimeLabel = new JLabel("  Start Time: ");
		JTextField startTimeText = new JTextField("HH:MM 24 Hour Format", 20);
		startTimePanel.add(startTimeLabel);
		startTimePanel.add(startTimeText);

		JPanel endTimePanel = new JPanel();
		endTimePanel.setPreferredSize(new Dimension(10, 20));
		JLabel endTimeLabel = new JLabel("    End Time: ");
		JTextField endTimeText = new JTextField("HH:MM 24 Hour Format", 20);
		endTimePanel.add(endTimeLabel);
		endTimePanel.add(endTimeText);

		JButton saveButton = new JButton("Save Event");
		saveButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (!model.validTime(startTimeText.getText()) || !model.validTime(endTimeText.getText()))
				{
					timeFormatErrorFrame();							
				}
				else
				{
					if(!eventNameLabel.getText().isEmpty())
					{
						String eventName = eventNameText.getText();
						String startTime = startTimeText.getText();
						String endTime = endTimeText.getText();

						if(!model.hasConflict(startTime, endTime))
						{
							String date2 = model.getMonth() + "/" + model.getDay() + "/" + model.getYear();
							model.newEvent(eventName, date2, startTime, endTime);
							createFrame.dispose();
							dayButtonClicked(model.getCalendar().get(Calendar.DAY_OF_MONTH));
						}
						else
						{
							conflictingFrame();
						}
					}
				}
			}
		});

		createFrame.add(dateView, BorderLayout.NORTH);
		container.add(eventPanel);
		container.add(startTimePanel);
		container.add(endTimePanel);	

		createFrame.add(container, BorderLayout.CENTER);
		createFrame.add(saveButton, BorderLayout.SOUTH);
		createFrame.pack();
		createFrame.setVisible(true);
	}

	/**
	 * Helper method to create a new JFrame that displays when user enters a conflicting time
	 */
	public void conflictingFrame()
	{
		JFrame conflictingFrame = new JFrame("Conflicting Time");
		conflictingFrame.setLayout(new BorderLayout());
		JLabel errorText = new JLabel("Another event during this time", SwingConstants.CENTER);
		errorText.setPreferredSize(new Dimension(300,100));
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				conflictingFrame.dispose();
			}
		});

		conflictingFrame.add(errorText, BorderLayout.NORTH);
		conflictingFrame.add(closeButton, BorderLayout.SOUTH);
		conflictingFrame.pack();
		conflictingFrame.setVisible(true);
	}

	/**
	 * Helper method to create a new JFrame that displays when user enters an incorrect time format
	 */
	public void timeFormatErrorFrame() 
	{
		JFrame timeErrorFrame = new JFrame("Time Format Error");
		timeErrorFrame.setLayout(new BorderLayout());
		JLabel errorText = new JLabel("Incorrect time format HH:MM 24 Hours, Example 05:30", SwingConstants.CENTER);
		errorText.setPreferredSize(new Dimension(300,100));
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				timeErrorFrame.dispose();
			}
		});

		timeErrorFrame.add(errorText, BorderLayout.NORTH);
		timeErrorFrame.add(closeButton, BorderLayout.SOUTH);
		timeErrorFrame.pack();
		timeErrorFrame.setVisible(true);
	}
}
