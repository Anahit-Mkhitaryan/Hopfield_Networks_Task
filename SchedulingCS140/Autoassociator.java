import java.util.Random;

public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	private int numNeurons;
	private Random random;

	// Constructor initializes the weights matrix and other properties
	public Autoassociator(CourseArray courses) {
		numNeurons = courses.length(); // Adjusted to use the `length` method
		weights = new int[numNeurons][numNeurons];
		trainingCapacity = (int) (0.15 * numNeurons); // Approximate rule of thumb for Hopfield networks
		random = new Random();
	}

	// getTrainingCapacity returns the maximum number of patterns the network can learn
	public int getTrainingCapacity() {
		return trainingCapacity;
	}

	// training trains the network with a given binary pattern using the Hebbian learning rule
	public void training(int pattern[]) {
		for (int i = 0; i < numNeurons; i++) {
			for (int j = 0; j < numNeurons; j++) {
				if (i != j) {
					weights[i][j] += pattern[i] * pattern[j];
				}
			}
		}
	}

	// Updates a random neuron based on the sum of weighted inputs
	public int unitUpdate(int neurons[]) {
		int index = random.nextInt(numNeurons);
		unitUpdate(neurons, index);
		return index;
	}

	// Updates a specific neuron by index
	public void unitUpdate(int neurons[], int index) {
		int sum = 0;
		for (int j = 0; j < numNeurons; j++) {
			if (j != index) {
				sum += weights[index][j] * neurons[j];
			}
		}
		neurons[index] = sum >= 0 ? 1 : -1;
	}

	// Performs a series of random unit updates
	public void chainUpdate(int neurons[], int steps) {
		for (int i = 0; i < steps; i++) {
			unitUpdate(neurons);
		}
	}

	// Updates all neurons until a stable state is achieved
	public void fullUpdate(int neurons[]) {
		boolean stable;
		int[] previousState = new int[numNeurons];

		do {
			stable = true;
			System.arraycopy(neurons, 0, previousState, 0, numNeurons);

			for (int i = 0; i < numNeurons; i++) {
				unitUpdate(neurons, i);
			}

			// Check if state has stabilized
			for (int i = 0; i < numNeurons; i++) {
				if (neurons[i] != previousState[i]) {
					stable = false;
					break;
				}
			}
		} while (!stable);
	}
}

