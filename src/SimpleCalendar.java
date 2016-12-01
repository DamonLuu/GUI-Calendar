
/**
 * Class that initializes the model and view and attaches to model
 * @author Damon Luu
 * copyright 2016
 * version 1
 */
public class SimpleCalendar 
{
	/**
	 * simple calendar runner
	 * @param args not used
	 */
	public static void main(String[] args) 
	{
		Model model = new Model();
		View view = new View(model);
		model.attach(view);
	}
}
