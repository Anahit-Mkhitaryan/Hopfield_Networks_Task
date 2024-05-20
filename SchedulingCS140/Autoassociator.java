import java.util.Random;

public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	private Random random = new Random();

	public Autoassociator(CourseArray courses) {
		// creates a new Hopfield network with the same number of neurons
		// as the number of courses in the input CourseArray
		weights = new int[courses.length()][courses.length()];
		trainingCapacity = (int) (0.14 * weights.length);
	}

	public int getTrainingCapacity() {
		return trainingCapacity;
	}

	public void training(int pattern[]) {
		if (pattern.length == weights.length && trainingCapacity > 0) {
			int prod;
			for (int i = 0; i < pattern.length - 1; i++) {
				for (int j = i+1; j < pattern.length; j++) {
					prod = pattern[i] * pattern[j];
					weights[i][j] += prod;
					weights[j][i] += prod;
				}
			trainingCapacity--;
			}
		}
		// TO DO
	}

	public int unitUpdate(int neurons[]) {
		int index = random.nextInt(neurons.length);
		unitUpdate(neurons, index);
		return index;
	}

	public void unitUpdate(int neurons[], int index) {
		int sum = 0;
		for (int i = 0; i < neurons.length; i++)
			sum += weights[index][i] * neurons[i];
		neurons[index] = sum >= 0 ? 1 : -1;
	}

	public void chainUpdate(int neurons[], int steps) {
		// TO DO
		// implements the specified number of update steps
		for (; steps > 0; steps--) {
			unitUpdate(neurons);
		}
	}

	public void fullUpdate(int neurons[]) {
		boolean changed = true;
		int[] previousState = new int[neurons.length];

		while (changed) {
			System.arraycopy(neurons, 0, previousState, 0, neurons.length);  // Copy current state to previousState
			for (int i = 0; i < neurons.length; i++) {
				unitUpdate(neurons, i);  // Update each neuron
			}
			changed = false;
			for (int i = 0; i < neurons.length; i++) {
				if (neurons[i] != previousState[i]) {
					changed = true;  // Check if there has been any change
					break;
				}
			}
		}
	}

}
