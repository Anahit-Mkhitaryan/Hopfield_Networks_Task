import java.io.*;
import java.util.StringTokenizer;

public class CourseArray {

	private Course elements[];
	private int period;

	public CourseArray(int numOfCourses, int numOfSlots) {
		period = numOfSlots;
		elements = new Course[numOfCourses];
		for (int i = 1; i < elements.length; i++)
			elements[i] = new Course();
	}

	public int[] createPatternForTraining() {
		int[] pattern = new int[elements.length];
		for (int i = 1; i < elements.length; i++) {
			// Assuming that a slot with 0 clashes is what we consider as 'ideal'
			// We mark it as 1 (active neuron), else -1 (inactive neuron)
			pattern[i] = elements[i].clashSize() == 0 ? 1 : -1;
		}
		return pattern;
	}


	public Course getElement(int index) {
		return elements[index];  // Retrieve a specific course
	}

	public int getPeriod() {
		return period;  // Retrieve the number of slots
	}

	public int clashesAtSlot(int slot) {
		int totalClashes = 0;
		for (int i = 1; i < elements.length; i++) {
			if (elements[i].mySlot == slot) {
				totalClashes += elements[i].clashSize();
			}
		}
		return totalClashes;  // Calculate total clashes for a given slot
	}

	public int[] getCurrentPattern() {
		int[] pattern = new int[elements.length];
		for (int i = 1; i < elements.length; i++) {
			pattern[i] = elements[i].mySlot;
		}
		return pattern;
	}

	public void applyPattern(int[] pattern) {
		for (int i = 1; i < elements.length; i++) {
			elements[i].mySlot = pattern[i];
		}
	}

	public int[] slotStatus(int slot) {
		int result[] = new int[2];
		for (int i=1; i < elements.length; i++)
			if (elements[i].mySlot == slot) {
				result[0]++;
				result[1] += elements[i].clashSize();
			}
		return result;

	}

	public void printSlotStatus() {
		int status[] = null;
		for (int slot = 0; slot < period; slot++) {
			status = slotStatus(slot);
			System.out.println(slot + "\t" + status[0] + "\t" + status[1]);
		}
	}

	public int[] getTimeSlot(int index) {
		int pattern[] = new int[elements.length];
		for (int i=1; i<pattern.length; i++) {
			pattern[i] = slot(i) == index ? 1 : -1;
		}
		return pattern;
	}


	public void readClashes(String filename) {
		try {
			BufferedReader file = new BufferedReader(new FileReader(filename));
			StringTokenizer line = new StringTokenizer(file.readLine());
			int count = line.countTokens(), i, j, k;
			int index[];
			while (count > 0) {
				if (count > 1) {
					index = new int[count];
					i = 0;
					while (line.hasMoreTokens()) {
						index[i] = Integer.parseInt(line.nextToken());
						i++;
					}

					for (i = 0; i < index.length; i++)
						for (j = 0; j < index.length; j++)
							if (j != i)
							{
								k = 0;
								while (k < elements[index[i]].clashesWith.size() && elements[index[i]].clashesWith.elementAt(k) != elements[index[j]])
									k++;
								if (k == elements[index[i]].clashesWith.size())
									elements[index[i]].addClash(elements[index[j]]);
							}
				}
				line = new StringTokenizer(file.readLine());
				count = line.countTokens();
			}
			file.close();
		}
		catch (Exception e) {
		}
	}

	public int length() {
		return elements.length;
	}

	public int status(int index) {
		return elements[index].clashSize();
	}

	public int slot(int index) {
		return elements[index].mySlot;
	}

	public void setSlot(int index, int newSlot) {
		elements[index].mySlot = newSlot;
	}

	public int maxClashSize(int index) {
		return elements[index] == null || elements[index].clashesWith.isEmpty() ? 0 : elements[index].clashesWith.size();
	}

	public int clashesLeft() {
		int result = 0;
		for (int i = 1; i < elements.length; i++)
			result += elements[i].clashSize();

		return result;
	}

	public void iterate(int shifts) {
		for (int index = 1; index < elements.length; index++) {
			elements[index].setForce();
			for (int move = 1; move <= shifts && elements[index].force != 0; move++) {
				elements[index].setForce();
				elements[index].shift(period);
			}
		}
	}

	public void printResult() {
		for (int i = 1; i < elements.length; i++)
			System.out.println(i + "\t" + elements[i].mySlot);
	}
}

