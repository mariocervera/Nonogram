package nonogram;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class contains the backtracking algorithm, which uses Depth-First Search (DFS) to traverse
 * the search space.
 * 
 * @author Mario Cervera
 * 
 */
public class DFS {
	
	/*
	 * This variable contains the initial state. It is a solution obtained after applying a set
	 * of direct rules. In the cases of easy Nonograms, this initial state may already represent the
	 * final solution.
	 */
	public static List<List<Integer>> initialState = new ArrayList<List<Integer>>();

	/*
	 * Depth-First Search
	 */
	public static List<List<Integer>> dfs(State state, int depth, int N) {
		
		if(depth == N) { //Base case
			
			return state.getSolution();
		}
		else { //Recursive case
			
			if(depth < N) {
				
				//Generate a set of children states (applying permutations)
				
				List<State> childrenStates = state.completions(depth);
				
				List<List<Integer>> result = null;
				
				//Iterate the children states
				
				while(!childrenStates.isEmpty() && result == null) {
					
					State s = childrenStates.get(0);
					childrenStates.remove(s);
					
					//Use pruning conditions to discard children that cannot lead to a solution
					
					if(pruningCondition1(s, depth) && pruningCondition2(s, N)) {
						
						result = dfs(s, depth + 1, N);
					}
				}
				
				return result;
			}
			else {	
				return null;
			}
		}
	}
	
	
	/* 
	 * This condition checks whether the current state matches what has already been solved by
	 * means of direct rules
	 */
	private static boolean pruningCondition1(State state, int depth) {
		
		List<Integer> stateRow = state.getSolution().get(depth);
		List<Integer> directRulesRow = initialState.get(depth);
		
		boolean meetsCondition = true;
		
		for(int i = 0; i < stateRow.size() && meetsCondition; i++) {
			
			Integer element = stateRow.get(i);
			Integer elementDirectRules = directRulesRow.get(i);
			
			if(elementDirectRules != 2 && element != elementDirectRules) {
				meetsCondition = false;
			}
		}
		
		return meetsCondition;
	}
	
	
	/*
	 * Permutations are only generated for rows. This condition checks for each column whether
	 * the generated permutations fit.
	 */
	private static boolean pruningCondition2(State state, int N) {
		
		boolean meetsCondition = true;
		
		List<List<Datum>> data = state.getData();
		List<List<Integer>> transposedSolution = DirectRules.transposeMatrix(state.getSolution());
		
		//Obtain an array of tokens containing the columns
		
		List<String> tokens = new ArrayList<String>();
		
		for(int i = 0; i < N; i++) {
			
			String token = "";
			
			for(int j = 0; j < N; j++) {
				
				String element = transposedSolution.get(i).get(j).toString();
				
				token += element;
			}
			
			tokens.add(token);
		}
		
		//Check condition
		
		for(int i = N; i < data.size() && meetsCondition; i++) {
			
			List<Datum> dataColumn = data.get(i);
			
			String token = tokens.get(i - N);
			StringTokenizer st = new StringTokenizer(token, "0");

			int j = 0;
			while(st.hasMoreTokens() && meetsCondition)	{
				
				String tk = st.nextToken();
				
				if(j < dataColumn.size()) {
					
					Integer datum = dataColumn.get(j).getValue();
					
					if(tk.indexOf("2") != -1 && datum < tk.indexOf("2")) {
						meetsCondition = false;
					}
					else if(tk.indexOf("2") == -1 && datum != tk.length()) {
						meetsCondition = false;
					}
				}
				else if(tk.indexOf("1") != -1) meetsCondition = false;
				j++;
			}
		}
		
		return meetsCondition;
	}
	
}
