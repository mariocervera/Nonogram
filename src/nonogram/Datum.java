package nonogram;

/**
 * This class represents a piece of data from the input file (i.e., a group of cells that must be painted).
 * The boolean attribute stores whether the group of cells has already been found.
 * 
 * @author Mario Cervera
 * 
 */
public class Datum {
	
	private int value;
	private boolean completed;
	
	public Datum(int v, boolean c) {
		
		setValue(v);
		setCompleted(c);
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isCompleted() {
		return completed;
	}

	public Datum clone() {
		return (new Datum(this.getValue(), this.isCompleted()));
	}
	
}
