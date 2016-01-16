package nonogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Main class. Reads data from an input file and obtains the solution
 * 
 * @author Mario Cervera
 * 
 */
public class Nonogram {
	
	/*
	 * The input files are stored in the "inputFiles" folder. Each of these files represent an unsolved Nonogram (N x N).
	 */
	public static void main(String[] args) {
		
		long milliseconds = System.currentTimeMillis();

		try {
			
			//Read file
			
			File fich = new File("./inputFiles/Player.txt");

			BufferedReader in = new BufferedReader(new FileReader(fich));

			//Resolve Nonogram
			
			resolveNonogram(in);

			//Calculate execution time
			
			long executionTimeMs = System.currentTimeMillis() - milliseconds;

			System.out.println("\nExecution time: " + (executionTimeMs / 1000.0) + " seconds");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	/*
	 * Returns an array that contains the data read from the input file
	 */
	private static List<List<Datum>> createInputData(BufferedReader in) throws Exception {
		
		List<List<Datum>> result = new ArrayList<List<Datum>>();
		
		String str = "";
		
		while((str = in.readLine()) != null) {
			
			if(str.length() > 0 && str.charAt(0) == '#') {
				continue; // The '#' symbol separates rows and columns in the input file
			}
			
			List<Datum> data = new ArrayList<Datum>();
			
			StringTokenizer st = new StringTokenizer(str);
            
			while(st.hasMoreTokens()) {
            	
				int datum = Integer.parseInt(st.nextToken());
            	
				if(datum < 0) {
					Exception e = new Exception("Incorrect input file format: all numbers must be greater or equal to 0.");
            		throw e;
				}

        		data.add(new Datum(datum, false));	
            }

            result.add(data);
		}

		if(result.size() % 2 != 0) {
			
			Exception e = new Exception("Incorrect input file format: only square matrices (N x N) are supported.");
    		throw e;
		}
		
		return result;
	}
	
	private static void resolveNonogram(BufferedReader in) throws Exception {
		
		List<List<Datum>> data= new ArrayList<List<Datum>>();
		List<List<Integer>> solution = new ArrayList<List<Integer>>();
		
		data = createInputData(in);

		//Number of rows (the grid is N x N)
		
		int N = data.size() / 2;
		
		//First, apply direct rules to solve the Nonogram as much as possible without resorting to backtracking
		
		DFS.initialState = DirectRules.applyRules(data); 
		
		//After the direct rules, apply the backtracking algorithm
		
		State s = new State();
		s.initialize(N);
		s.setData(data);
		solution = DFS.dfs(s, 0, N);
		
		//Print result
		
		if(solution == null) {
			System.out.println("\nA solution could not be found.");
		}
		else {
			for(List<Integer> integers: solution) {
				for(Integer i: integers) {
					System.out.print(i + " ");
				}
				System.out.println();
			}
		}
	}

}
