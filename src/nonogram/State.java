package nonogram;

import java.util.ArrayList;
import java.util.List;

/**
 * Nonograms are Constraint Satisfaction Problems (CSP), which are defined as a set of objects whose state
 * must satisfy a number of constraints or limitations. One way to solve these problems is by using a backtracking
 * algorithm that incrementally builds candidates (states) solutions, and abandons each partial candidate
 * ("backtracks") as soon as the algorithm determines that the candidate cannot possibly be completed to
 * a valid solution.
 * 
 * This class represents a candidate solution; that is, a state of the problem.
 * 
 * Each cell in a Nonogram grid has three possibilities: 0 (uncolored), 1 (colored), 2 (unknown).
 * 
 * @author Mario Cervera
 * 
 */
public class State {
	
	private List<List<Integer>> solution; //A candidate Nonogram solution
	
	private List<List<Datum>> data; //The data obtained from the input file
	
	public State() {
		
		setSolution(new ArrayList<List<Integer>>());
		setData(new ArrayList<List<Datum>>());
	}
	
	
	public void setSolution(List<List<Integer>> solution) {
		this.solution = solution;
	}
	
	public List<List<Integer>> getSolution() {
		return solution;
	}

	public void setData(List<List<Datum>> data) {
		this.data = data;
	}

	public List<List<Datum>> getData() {
		return data;
	}
	
	public void initialize(int n) {
		
		for(int i = 0; i < n; i++) 	{
			
			List<Integer> l = new ArrayList<Integer>();
			
			for(int j = 0; j < n; j++) {
				
				l.add(2);
			}
			
			getSolution().add(l);
		}
	}
	
	/*
	 * "Depth" indicates the depth of the state in the search tree
	 * 
	 * Returns a list of children states, which are generated applying permutations
	 */
	public List<State> completions(int depth)
	{
		List<List<Integer>> completions = new ArrayList<List<Integer>>();
		List<State> childrenStates = new ArrayList<State>();
		
		//Initial completion
		
		List<Integer> initialCompletion = initialCompletion(depth);
				
		//The rest of completions
		
		List<Integer> emptyHead = new ArrayList<Integer>();
		 
		permutation(initialCompletion, emptyHead, completions);
		
		//Create children states
		
		for(List<Integer> c: completions) {
			
			State st = new State();
			st.setData(this.getData());
			st.setSolution2(this.getSolution());
			st.getSolution().set(depth, c);
			childrenStates.add(st);
		}	
		
		return childrenStates;
	}
	
	
	/* -----------------------------------
	 * --------- Private methods ---------
	 * -----------------------------------
	 */
	
	
	/*
	 * This method generates an initial completion (solution) for a row.
	 * The cells with value "1" are located to their leftmost possible position.
	 */
	private List<Integer> initialCompletion(int row) {
		
		List<Integer> initialCompletion = new ArrayList<Integer>();
		
		int N = this.getSolution().size();
		
		List<Datum> rowData = this.getData().get(row);
		
		int j = 0;
		
		for(int i = 0; i < rowData.size(); i++) {
			
			int datum = rowData.get(i).getValue();
			
			for(int k = 0; k < datum; k++) {
				initialCompletion.add(1);
				j++;
			}
			if(j < N) {
				initialCompletion.add(0); //Must be a "space" between groups
				j++;
			}
		}
		
		for(int k = j; k < N; k++) {
			initialCompletion.add(0); //Fill the remainder cells with "0"
		}
		
		return initialCompletion;
	}
	
	/*
	 * Shifts the group starting at "i" one position to the right
	 */
	private static void groupShiftRight(List<Integer> row, int i) {
		
		int groupLength = 0;
		
		for(int j = i; j < row.size(); j++)
		{
			Integer element = row.get(j);
			if(element == 1) {
				groupLength++;
			}
			else {
				break;
			}
		}
		
		if(i + groupLength < row.size()) {
			
			Integer aux = row.get(i + groupLength);
			row.set(i + groupLength, row.get(i));
			row.set(i, aux);
		}
	}
	
	
	/*
	 * Shifts the completion one position to the right
	 */
	private static void completionShiftRight(List<Integer> completion) {
		
		List<Integer> shiftedCompletion = new ArrayList<Integer>();
		shiftedCompletion.add(0);
		
		for(int i = 0; i < completion.size() - 1; i++)
		{
			Integer element = completion.get(i);
			shiftedCompletion.add(element);
		}
		
		completion.clear();
		
		for(Integer element: shiftedCompletion) {
			completion.add(element);
		}
	}
	
	/*
	 * Determines whether the completion ends with a painted cell
	 */
	private static boolean endsWithOne(List<Integer> completion) {
		try {
			return (completion.get(completion.size() - 1) == 1);
		}
		catch(Exception e) {
			return false; 
		}
	}
	
	/*
	 * Returns the number of groups of a completion
	 */
	private static int numberOfGroups(List<Integer> completion) {
		
		int result = 0;
		boolean groupCounted = false;
		
		for(Integer element: completion) {
			
			if(element == 1 && !groupCounted) {
				result++;
				groupCounted = true;
			}
			else if(element == 0) {
				groupCounted = false;
			}
		}
		
		return result;
	}
	
	/*
	 * Obtains a list that contains the first group together with all the "0" until the next group
	 */
	private static List<Integer> head(List<Integer> completion) {
		
		List<Integer> head = new ArrayList<Integer>();
		
		boolean firstGroupFound = false;
		boolean firstGroupFinished = false;
		
		for(Integer element: completion) {
		
			if(element == 1 && !firstGroupFinished) {
				firstGroupFound = true;
			}
			else if(element == 1 && firstGroupFinished) {
				break;
			}
			else if(element == 0 && firstGroupFound) {
				firstGroupFinished = true;
			}
			
			head.add(element);
		}
		
		return head;
	}
	
	/*
	 * Removes the head
	 */
	private static List<Integer> remainder(List<Integer> completion)
	{
		List<Integer> remainder = new ArrayList<Integer>();
		
		boolean firstGroupFound = false;
		
		int i = 0;
		
		for(Integer element: completion) {
			
			if(element == 1) {
				firstGroupFound = true;
			}
			else if(element == 0 && firstGroupFound) {
				i++;
				break;
			}
			i++;
		}
		
		for(int j = i; j < completion.size(); j++) {
			
			Integer element = completion.get(j);
			remainder.add(element);
		}
		
		return remainder;
		
	}
	
	
	/*
	 * Obtains the position of the first group
	 */
	private static int firstGroupPosition(List<Integer> completion) {
		
		int position = -1;
		
		for(int i = 0; i < completion.size() && position == -1; i++) 
		{
			int element = completion.get(i);
			
			if(element == 1) {
				position = i;
			}
		}
		
		return position;
	}
	
	
	/*
	 * Appends head and remainder; it also adds the completion to the "completions" array
	 */
	private static void addCompletion(List<Integer> head, List<Integer> remainder, List<List<Integer>> completions) {
		
		List<Integer> aux = new ArrayList<Integer>();
		aux.addAll(head);
		aux.addAll(remainder);
		completions.add(aux);
	}
	
	/*
	 * Calculate the permutations. This method is useful to generate the children of the different states
	 * in the backtracking algorithm
	 */
	private static void permutation(List<Integer> remainder, List<Integer> head, List<List<Integer>> completions) {
		
		int numberOfGroups = numberOfGroups(remainder);
		
		if(numberOfGroups == 0) { //Base case: every element is "0"
			
			addCompletion(head, remainder, completions);
		}
		else if(numberOfGroups == 1) { //Base case: shift group to the right repeatedly
			
			while(!endsWithOne(remainder)) {
				
				int firstGroupPosition = firstGroupPosition(remainder);
				
				addCompletion(head, remainder, completions);
				
				groupShiftRight(remainder, firstGroupPosition);
			}
			
			addCompletion(head, remainder, completions);
		}
		else { //Recursive case
			
			while(!endsWithOne(remainder)) {
				
				List<Integer> rem = remainder(remainder);
				List<Integer> h = head(remainder);
				
				List<Integer> head_aux = new ArrayList<Integer>();
				head_aux.addAll(head);
				head_aux.addAll(h);
				
				permutation(rem, head_aux, completions);
				
				completionShiftRight(remainder);
			}
			
			addCompletion(head, remainder, completions);
		}
	}
		
	private void setSolution2(List<List<Integer>> solution) {
		
		List<List<Integer>> sol = new ArrayList<List<Integer>>();
		
		for(List<Integer> l: solution) {
			
			List<Integer> vector = new ArrayList<Integer>();
			
			for(Integer i: l) {
				vector.add(i);
			}
			sol.add(vector);
		}
		
		this.setSolution(sol);
	}
	
}
