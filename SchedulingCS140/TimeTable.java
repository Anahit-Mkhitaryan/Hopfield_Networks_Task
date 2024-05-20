import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

	private JPanel screen = new JPanel(), tools = new JPanel();
	private JButton tool[];
	private JTextField field[];
	private CourseArray courses;
	private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
	private Autoassociator autoassociator;

	public TimeTable() {
		super("Dynamic Time Table");
		setSize(500, 800);
		setLayout(new FlowLayout());

		Logger.initLogger("TimeTableAutoassociatorLog.txt");

		screen.setPreferredSize(new Dimension(400, 800));
		add(screen);

		setTools();
		add(tools);

		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Logger.close(); // Close the logger when the window closes
			}
		});
		courses = new CourseArray(181, 19);
		autoassociator = new Autoassociator(courses);
	}

	public void setTools() {
		String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
		field = new JTextField[capField.length];

		String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Train"};
		tool = new JButton[capButton.length];

		tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));

		for (int i = 0; i < field.length; i++) {
			tools.add(new JLabel(capField[i]));
			field[i] = new JTextField(5);
			tools.add(field[i]);
		}

		for (int i = 0; i < tool.length; i++) {
			tool[i] = new JButton(capButton[i]);
			tool[i].addActionListener(this);
			tools.add(tool[i]);
		}

		// tool[5] = new JButton(capButton[5]);
		// tool[5].addActionListener(this);
		// tools.add(tool[5]);

		field[0].setText("19");
		field[1].setText("181");
		field[2].setText("yor-f-83.stu");
		field[3].setText("1");
		field[4].setText("1");
	}

	public void draw() {
		Graphics g = screen.getGraphics();
		int width = Integer.parseInt(field[0].getText()) * 10;
		for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
			g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
			g.drawLine(0, courseIndex, width, courseIndex);
			g.setColor(CRScolor[CRScolor.length - 1]);
			g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
		}
	}

	private int getButtonIndex(JButton source) {
		int result = 0;
		while (source != tool[result]) result++;
		return result;
	}

	public void actionPerformed(ActionEvent click) {
		int min, step, clashes;

		switch (getButtonIndex((JButton) click.getSource())) {
			case 0:
				int slots = Integer.parseInt(field[0].getText());
				courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
				courses.readClashes(field[2].getText());
				draw();
				trainAutoassociator();
				break;
			case 1:
				min = Integer.MAX_VALUE;
				step = 0;
				for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);

				for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = iteration;
					}
				}
				System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
				Logger.log("Slots = " + field[0].getText() + "\tat shift " + field[4].getText() + "\tIterations " + field[3].getText());

				setVisible(true);
				runSchedulingWithAutoassociator();
				break;
			case 2:
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				break;
			case 3:
				System.out.println("Exam\tSlot\tClashes");
				for (int i = 1; i < courses.length(); i++)
					System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
				Logger.log("Exam\tSlot\tClashes");
				for (int i = 1; i < courses.length(); i++)
					Logger.log(i + "\t\t" + courses.slot(i) + "\t\t" + courses.status(i));
				break;
			case 4:
				System.exit(0);
			case 5:
				trainAutoassociator();
				break;
		}
	}

	public int[] convertToPattern(CourseArray courses, int slotIndex) {
		int[] pattern = new int[courses.length()];
		for (int i = 1; i < pattern.length; i++) {
			pattern[i] = (courses.slot(i) == slotIndex) ? 1 : -1;  // 1 if in the slot, -1 if not
		}
		return pattern;
	}

	// Example usage within your TimeTable or main class
	public void trainAutoassociator() {
		for (int i = 0; i < courses.length(); i++) {  // Assuming maxSlots is defined
			int[] pattern = convertToPattern(courses, i);
			autoassociator.training(pattern);
		}
	}

	public void updateUsingAutoassociator() {
		int[] currentPattern = courses.getCurrentPattern();  // Define this method to get current slot configuration as pattern
		autoassociator.fullUpdate(currentPattern);  // Use the fullUpdate method to let the autoassociator suggest a new configuration
		courses.applyPattern(currentPattern);  // Define this method to apply the pattern back to courses
	}

	public void runSchedulingWithAutoassociator() {
		for (int iteration = 0; iteration < 1000; iteration++) {  // Example iteration count
			if (iteration % 100 == 0) {  // Every 100 iterations, attempt to use the autoassociator
				int[] currentPattern = courses.getCurrentPattern();
				autoassociator.fullUpdate(currentPattern);
				courses.applyPattern(currentPattern);
			}
			continueScheduling();  // Continue your scheduling algorithm
		}
	}

	public void continueScheduling() {
		int clashes = courses.clashesLeft();  // Assume this method returns the number of current clashes
		int iteration = 0;
		int maxIterations = 50;  // Define a max number of iterations to prevent infinite loops

		while (clashes > 0 && iteration < maxIterations) {
			for (int i = 1; i < courses.length(); i++) {
				attemptToResolveClashes(i);
			}
			clashes = courses.clashesLeft();
			iteration++;
		}
	}

	// Helper method to attempt to resolve clashes for a given course
	private void attemptToResolveClashes(int courseIndex) {
		Course course = courses.getElement(courseIndex);  // Assume this method retrieves a specific course

		// Simple logic to find a better slot
		int currentSlot = course.mySlot;
		for (int slot = 0; slot < courses.getPeriod(); slot++) {
			if (slot != currentSlot) {
				course.mySlot = slot;  // Temporarily assign to new slot
				if (courses.clashesLeft() < courses.clashesAtSlot(currentSlot)) {  // Assume clashesAtSlot checks clashes at a specific slot
					return;  // If better, keep it
				}
			}
		}
		course.mySlot = currentSlot;  // Revert if no better slot found
	}

	public static void main(String[] args) {
		new TimeTable();
	}
}


/*
* for (int cycle=1; cycle <=cycles; cycle++) {
* 	for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
					courses.iterate(Integer.parseInt(field[4].getText()));
					draw();
					clashes = courses.clashesLeft();
					if (clashes < min) {
						min = clashes;
						step = iteration;
					}
				}
	autoassociator.unitUpdate(courses.getTimeSlot(slot))  // toPattern(slot));
	* or
	autoassociator.chainUpdate(courses.getTimeSlot(randomslot), iterations);
* }
*
* // courseArray function -  the same as getTimeSlot()  // existing function
* public int[] toPattern(int slot[]) {
* 	int result[]  = new int[]
* 	for
* }
* */
