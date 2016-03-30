package nl.liacs.sports.football.visualization;

/**
 * Array of bounded size that returns elements in FIFO order and automatically overwrites old
 * elements when newer ones are added.
 */
public class BoundedFIFOArray {

    private final double[] elements;

    private final int capacity;

    int numElements = 0;

    int insertionPoint = 0;

    public BoundedFIFOArray(int capacity) {
        this.capacity = capacity;
        elements = new double[capacity];
    }

    public void add(double value) {
        elements[insertionPoint] = value;
        numElements = Math.min(numElements + 1, capacity);
        incrementPointer();
    }

    public double[] getElements() {
        double[] retElements = new double[numElements];
        int startIndex = (numElements <= capacity) ? 0 : insertionPoint;
        for (int i = 0; i < numElements; i++) {
            int currentIndex = getWrappedIndex(startIndex + i);
            retElements[i] = elements[currentIndex];
        }
        return retElements;
    }

    private int getWrappedIndex(int index) {
        if (index >= capacity) {
            return index - capacity;
        } else if (index < 0) {
            return capacity + index;
        } else {
            return index;
        }
    }

    private void incrementPointer() {
        insertionPoint = getWrappedIndex(insertionPoint + 1);
    }
}
