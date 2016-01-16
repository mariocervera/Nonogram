package nonogram;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains direct rules that are applied to partially (and, sometimes, totally) solve
 * the Nonogram without resorting to backtracking.
 * 
 * @author Mario Cervera
 * 
 */
public class DirectRules {
	
	public static List<List<Integer>> applyRules(List<List<Datum>> data) {
		
		List<List<Integer>> solution = new ArrayList<List<Integer>>();
		
		boolean changed = false;
		
		List res = new ArrayList();
		
		int N = data.size() / 2;
		
		initializeSolution(solution, N);
		
		//First rules (it is not necessary to iterate, yet)
		
		applyRule1(data, solution, N);
		applyRule2(data, solution, N);
		
		do {
			changed = false;
			
			res = applyRule3(data, solution);
			
			changed = Boolean.parseBoolean(res.get(0).toString());
			solution = (List<List<Integer>>)res.get(1);
			data = (List<List<Datum>>)res.get(2);	
			
			res = applyRule4(data, solution);
			
			if (!changed) changed = Boolean.parseBoolean(res.get(0).toString());
			solution = (List<List<Integer>>)res.get(1);
			data = (List<List<Datum>>)res.get(2);
			
			res = applyRule5(data, solution);
			
			if (!changed) changed = Boolean.parseBoolean(res.get(0).toString());
			solution = (List<List<Integer>>)res.get(1);
			data = (List<List<Datum>>)res.get(2);
			
			res = applyRule6(data, solution);
			
			if (!changed) changed = Boolean.parseBoolean(res.get(0).toString());
			solution = (List<List<Integer>>)res.get(1);
			data = (List<List<Datum>>)res.get(2);
			
			res = completeRows(data, solution);
			
			if (!changed) changed = Boolean.parseBoolean(res.get(0).toString());
			solution = (List<List<Integer>>)res.get(1);
			data = (List<List<Datum>>)res.get(2);
		
		} while(changed);
		
		return solution;
	}
	
	public static List<List<Integer>> transposeMatrix(List<List<Integer>> matrix) {
		
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		for(int i = 0; i < matrix.size(); i++) {
			
			List<Integer> row = new ArrayList<Integer>();
			
			for(int j = 0; j < matrix.size(); j++) {
				
				Integer element = ((List<Integer>)matrix.get(j)).get(i);
				
				row.add(element);
			}
			
			result.add(row);
		}
		
		return result;
	}
	

	// -----------------------------------
	// --------- Private methods ---------
	// -----------------------------------
	
	/*
	 * Rule 1: If a line of data is equal to 0 (or N), set all the elements of the corresponding solution
	 * row to "0" (or "1").
	 */
	private static void applyRule1(List<List<Datum>> data, List<List<Integer>> solution, int N) {	
		
		for(int i = 0; i < data.size(); i++) {
			
			List<Datum> datumList = data.get(i);
			
			for(int j = 0; j <datumList.size(); j++) {
				
				int datum = datumList.get(j).getValue();
				boolean completed = datumList.get(j).isCompleted();
				
				if((datum == 0 || datum == N) && !completed) {
					
					if(i < N) { //Row
						
						for(int k = 0; k < N; k++) {
							if(datum == 0) {
								solution.get(i).set(k, 0);
							}
							else {
								solution.get(i).set(k, 1);
							}
						}
					}
					else { //Column
						
						int columnNum = i - N;
						
						for(int k = 0; k < N; k++) {
							
							if(datum == 0) {
								solution.get(k).set(columnNum, 0);
							}
							else {
								solution.get(k).set(columnNum, 1);
							}
						}
					}
					
					datumList.get(j).setCompleted(true);
				}
				
			}
		}
		
	}
	
	/*
	 * Rule 2: If there are lines where the sum of the data and the blanks is equal to N, the
	 * line can be completed
	 */
	private static void applyRule2(List<List<Datum>> data, List<List<Integer>> solution, int N) {
	
		for(int i = 0; i < data.size(); i++) {
			
			List<Datum> datumList = data.get(i);
			
			int total = 0;
			
			for(int j = 0; j < datumList.size(); j++) {
				
				int value = datumList.get(j).getValue();
				boolean completed = datumList.get(j).isCompleted();
				if(!completed) {
					total += value;
				}
				else {
					total = -1;
					break;
				}
			}
			
			int numSpaces = datumList.size() - 1;
			
			//If the sum of the data and the spaces is equal to N ...
			
			if((total + numSpaces) == N) {
				
				int counter = 0;
				int columnNum = i - N; //Only required if i >= N
				
				//For each element of the line, we set the corresponding cells to "1"
				
				for(int j = 0; j <datumList.size(); j++) {
					
					int value = datumList.get(j).getValue();
					
					for(int k = 0; k < value; k++) {
						
						//How to access the element changes for rows or columns
						
						if(i < N) {
							solution.get(i).set(counter + k, 1);
						}
						else {
							solution.get(counter + k).set(columnNum, 1);
						}
					}
					
					counter += value;
					
					//If it is not the last element, add "0"
					
					if(j < datumList.size() - 1) {
						
						if(i < N) {
							solution.get(i).set(counter, 0);
						}
						else {
							solution.get(counter).set(columnNum, 0);
						}
					}
					
					counter++;
					
					datumList.get(j).setCompleted(true);
				}
			
			}
		}
	}
	
	
	/*
	 * Rule 3: When a cell is painted in the beginning (or the end) of a line, the first (or the last) group can be completed
	 */
	private static List applyRule3(List<List<Datum>> data, List<List<Integer>> solution) {
		
		boolean changed = false;
		
		//One iteration for rows and another one for colums
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) solution = transposeMatrix(solution);
			
			List<List<Datum>> data_backup = cloneData(data);
			List<List<Integer>> solution_backup = cloneSolution(solution);

			//Remove extra elements
			
			data = trimData(data);
			solution = trimSolution(solution);		
			
			//Apply rule 3
			
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				//Check whether the first cell is painted
				
				if(row.size() > 0 && row.get(0) == 1) {
					
					//Obtain the first datum
					
					Datum firstGroup = null;
					
					if(z == 0) {
						if(data.get(i).size() > 0) {
							firstGroup = data.get(i).get(0);
						}
					}
					else {
						if(data.get(i + solution.size()).size() > 0) {
							firstGroup = data.get(i + solution.size()).get(0);
						}
					}
					
					if(firstGroup != null && !firstGroup.isCompleted()) {
						
						int value = firstGroup.getValue();
						
						for(int j = 0; j < value; j++) {
							
							row.set(j, 1);
						}
						
						firstGroup.setCompleted(true);
						changed = true;
					}
					
					//Set the corresponding cell at the end of the group as "0"
					if(firstGroup != null && firstGroup.getValue() < row.size() && row.get(firstGroup.getValue()) == 2) {
						row.set(firstGroup.getValue(), 0);
						changed = true;
					}
				}
				
				//Check whether the last cell is painted
				
				if(row.size() > 0 && row.get(row.size() - 1) == 1) {
					
					Datum lastGroup = null;
					
					if(z == 0){
						if(data.get(i).size() > 0) lastGroup = data.get(i).get(data.get(i).size() - 1);
					}
					else {
						if(data.get(i + solution.size()).size() > 0) {
							lastGroup = data.get(i + solution.size()).get(data.get(i + solution.size()).size() - 1);
						}
					}
					
					if(lastGroup != null && !lastGroup.isCompleted()) {
						
						int value = lastGroup.getValue();
						
						for(int j = row.size() - 1; j >= row.size() - value; j--) {
							row.set(j, 1);
						}
						
						lastGroup.setCompleted(true);
						changed = true;
					}
					
					if(lastGroup != null && row.size() - lastGroup.getValue() - 1 >= 0 && row.get(row.size() - lastGroup.getValue() - 1) == 2) {
						
						row.set(row.size() - lastGroup.getValue() - 1, 0);
						changed = true;
					}
					
				}
				
				
			}
			
			data = restoreData(data_backup, data);
			solution = restoreSolution(solution_backup, solution);
			
			if(z == 0) {
				updateDataArray(data, solution);
			}
			
		}
		
		solution = transposeMatrix(solution);
		
		updateDataArray(data, solution);
			
		ArrayList res = new ArrayList();
			
		res.add(changed);
		res.add(solution);
		res.add(data);
			
		return res;
	}
	
	
	
	/*
	 * Rule 4: Overlapping
	 */
	private static List applyRule4(List<List<Datum>> data, List<List<Integer>> solution) {
	
		boolean changed = false;	
	
		//One iteration for rows and another one for columns
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) {
				solution = transposeMatrix(solution);
			}
			
			List<List<Datum>> data_backup = cloneData(data);
			List<List<Integer>> solution_backup = cloneSolution(solution);
			
			data = trimData(data);
			solution = trimSolution(solution);		
			
			//Apply rule 4
			
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				//Calculate degree of freedom
				
				int rowLength, groupsSum, groupsNum;
				
				rowLength = row.size();
				if(rowLength == 0) continue;
				List<Datum> groupsRow = null;
				if(z == 0) groupsRow = data.get(i);
				else groupsRow = data.get(i + solution.size());
				
				groupsSum = groupsNum = 0;
				for(Datum d: groupsRow) groupsSum += d.getValue();
				
				groupsNum = groupsRow.size();
			
				int gl = rowLength - groupsSum - groupsNum + 1;
				
				//Iterate the groups of this file to calculate the fixed cells
				
				for(int j = 0; j < groupsRow.size(); j++) {
					Datum group = groupsRow.get(j);
					
					int fixedCellsCounter = 0;
					if(group.getValue() > gl) fixedCellsCounter = group.getValue() - gl;
					
					//Mark them as "1"
					for(int k = 0; k < fixedCellsCounter; k++)
					{
						int sumPrecedingGroups = 0;
						
						for(int l = 0; l < j; l++) sumPrecedingGroups += groupsRow.get(l).getValue();
						
						if(Integer.parseInt(row.get(sumPrecedingGroups + j + gl + k).toString()) != 1) changed = true;
						row.set(sumPrecedingGroups + j + gl + k, 1);	
					}
				}
				
			}
		
			data = restoreData(data_backup, data);
			solution = restoreSolution(solution_backup, solution);
			
			if(z == 0) {
				updateDataArray(data, solution);
			}
			
		}
		
		solution = transposeMatrix(solution);
		
		updateDataArray(data, solution);
			
		ArrayList res = new ArrayList();
			
		res.add(changed);
		res.add(solution);
		res.add(data);
			
		return res;
		
	}
	
	
	
	/*
	 * Rule 5: Maximum range
	 */
	private static List applyRule5(List<List<Datum>> data, List<List<Integer>> solution) {
	
		boolean changed = false;	
	
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) {
				solution = transposeMatrix(solution);
			}
			
			List<List<Datum>> data_backup = cloneData(data);
			List<List<Integer>> solution_backup = cloneSolution(solution);
			
			data = trimData(data);
			solution = trimSolution(solution);		
			
			//Apply rule 5
			
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				List<Datum> dataRow = null;
				if(z == 0) {
					dataRow = data.get(i);
				}
				else {
					dataRow = data.get(i + solution.size());
				}
				
				//If one datum is left for completion ...
				if(dataRow.size() == 1) {
					
					//Look for a cell that is marked as "1"; maybe we can set some "0"
					//Iterate backwards and forward
					
					for(int j = 0; j < row.size(); j++) {
						
						Integer element = row.get(j);
						
						if(element == 1) {
							
							int datum = dataRow.get(0).getValue();
							
							//Set as "0"
							for(int k = j + datum; k < row.size(); k++)
							{
								Integer elem =row.get(k);
								
								if(elem == 2) {
									row.set(k, 0);
									changed = true;
								}
							}
							
							break;
						}
					}
					
					
					for(int j = row.size() - 1; j >= 0; j--) {
						Integer element = row.get(j);
						
						if(element == 1) {
							
							int datum = dataRow.get(0).getValue();
							
							//Set as "0"
							for(int k = j - datum; k >= 0; k--) {
								
								Integer elem =row.get(k);
								
								if(elem == 2) {
									row.set(k, 0);
									changed = true;
								}
							}
							
							break;
						}
					}
					
					
				}
				
			}
			
			data = restoreData(data_backup, data);
			solution = restoreSolution(solution_backup, solution);
			
			if(z == 0) {
				updateDataArray(data, solution);
			}
			
		}
		
		solution = transposeMatrix(solution);
		
		updateDataArray(data, solution);
			
		ArrayList res = new ArrayList();
			
		res.add(changed);
		res.add(solution);
		res.add(data);
			
		return res;
	}
	
	
	/*
	 * Rule 6: Small gaps
	 */
	private static List applyRule6(List<List<Datum>> data, List<List<Integer>> solution) {
	
		boolean changed = false;	
		
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) {
				solution = transposeMatrix(solution);
			}
			
			List<List<Datum>> data_backup = cloneData(data);
			List<List<Integer>> solution_backup = cloneSolution(solution);
			
			data = trimData(data);
			solution = trimSolution(solution);		
			
			//Apply rule 6
			
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				List<Datum> dataRow = null;
				if(z == 0) {
					dataRow = data.get(i);
				}
				else {
					dataRow = data.get(i + solution.size());
				}
				
				int j = 0;
				while(j < row.size())
				{	
					int gapLength = 0;
					
					Integer element = row.get(j);
					
					if(element == 2 && !previousIsOne(j, row)) { //Possible gap
					
						int k = j;
						
						while(k < row.size()) {
							
							Integer elem = row.get(k);
							
							if(elem != 2) break;
							
							gapLength++;
							k++;
						}
						
						if(k >= row.size() ||row.get(k) == 0) { // It was actually a gap
						
							if(isSmallGap(gapLength, dataRow)) {
								
								for(int l = j; l < (j + gapLength); l++) {
									row.set(l, 0);
								}
								
								changed = true;
							}
						}
						
						j += gapLength;
						
					}
					else j++;
					
				}
				
			}
			
			
			data = restoreData(data_backup, data);
			solution = restoreSolution(solution_backup, solution);
			
			if(z == 0) {
				updateDataArray(data, solution);
			}
			
		}
		
		solution = transposeMatrix(solution);
		
		updateDataArray(data, solution);
			
		ArrayList res = new ArrayList();
			
		res.add(changed);
		res.add(solution);
		res.add(data);
			
		return res;
	}
	
	
	
	
	/*
	 * Final rule: Set as "0" the remainder cells ("2") of the rows whose groups have already been completed
	 */
	private static ArrayList completeRows(List<List<Datum>> data, List<List<Integer>> solution) {
	
		boolean changed = false;	
		
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) {
				solution = transposeMatrix(solution);
			}
			
			List<List<Datum>> data_backup = cloneData(data);
			List<List<Integer>> solution_backup = cloneSolution(solution);
			
			data = trimData(data);
			solution = trimSolution(solution);		
			
			//Apply rule
			
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				List<Datum> dataRow = null;
				
				if(z == 0) {
					dataRow = data.get(i);
				}
				else {
					dataRow = data.get(i + solution.size());
				}
				
				if(dataRow.size() == 0) {
					
					//Set as "0" the elements of the row (which are not "0" or "1")
					
					for(int j = 0; j < row.size(); j++) {
						
						if(row.get(j) == 2) {
							row.set(j, 0);
							changed = true;
						}
					}
				}
				
			}
		
			
			data = restoreData(data_backup, data);
			solution = restoreSolution(solution_backup, solution);
			
			if(z == 0) {
				updateDataArray(data, solution);
			}
			
		}
		
		solution = transposeMatrix(solution);
		
		updateDataArray(data, solution);
			
		ArrayList res = new ArrayList();
			
		res.add(changed);
		res.add(solution);
		res.add(data);
			
		return res;
		
	}

	private static void initializeSolution(List<List<Integer>> solution, int N) {
		
		for(int i = 0; i < N; i++) {
			
			List<Integer> x = new ArrayList<Integer>();
			
			for(int j = 0; j < N; j++) {
				x.add(2);
			}
			
			solution.add(x);
		}
	}
	
	private static List<List<Integer>> cloneSolution(List<List<Integer>> list) {
		
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		for(List<Integer> l: list) {
			
			List<Integer> aux = new ArrayList<Integer>();
			
			for(Integer i: l) {
				
				aux.add(i);
			}
			
			result.add(aux);
		}
		
		return result;
	}
	
	private static List<List<Datum>> cloneData(List<List<Datum>> list) {
		
		List<List<Datum>> result = new ArrayList<List<Datum>>();
		
		for(List<Datum> l: list) {
			
			List<Datum> aux = new ArrayList<Datum>();
			
			for(Datum d: l) {
				
				aux.add(d.clone());
			}
			
			result.add(aux);
		}
		
		return result;
	}
	
	private static void updateDataArray(List<List<Datum>> data, List<List<Integer>> solution) {
		
		//One iteration for rows and another one for columns
		
		for(int z = 0; z < 2; z++) {
			
			if(z == 1) {
				solution = transposeMatrix(solution);
			}
		
			for(int i = 0; i < solution.size(); i++) {
				
				List<Integer> row = solution.get(i);
				
				int groupCounter = 0;
				boolean cellFound = false;
				int j = 0;
				while(j < row.size()) {
					
					Integer element = row.get(j);
					
					if(element == 0 && cellFound) {
						
						if(z == 0) {
							data.get(i).get(groupCounter).setCompleted(true);
						}
						else {
							data.get(i + solution.size()).get(groupCounter).setCompleted(true);
						}
						groupCounter++;
						cellFound = false;
					}
					else if(element == 1) cellFound = true;
					else if(element == 2) break;
					
					j++;
				}
				
				groupCounter = 0;
				cellFound = false;
				j = row.size() - 1;
				
				while(j >=0) {
					
					Integer element = row.get(j);
					
					if(element == 0 && cellFound) {
						
						if(z == 0) {
							data.get(i).get(data.get(i).size() - 1 - groupCounter).setCompleted(true);
						}
						else {
							data.get(i + solution.size()).get(data.get(i  + solution.size()).size() - 1 - groupCounter).setCompleted(true);
						}
						groupCounter++;
						cellFound = false;
					}
					else if(element == 1) cellFound = true;
					else if(element == 2) break;
					
					j--;
				}
			}
		}
		
		solution = transposeMatrix(solution);
		
	}
	
	
	/*
	 * Removes completed data
	 */
	private static List<List<Datum>> trimData(List<List<Datum>> data) {
		
		List<List<Datum>> result = new ArrayList<List<Datum>>();
		
		for(List<Datum> dList: data) {
			
			List<Datum> aux = new ArrayList<Datum>();
			
			int infCounter = 0;
			int supCounter = dList.size() - 1;
			
			while(infCounter < dList.size()) {
				
				if(dList.get(infCounter).isCompleted()) infCounter++;
				else break;
			}
			
			while(supCounter >= 0) {
				
				if(dList.get(supCounter).isCompleted()) supCounter--;
				else break;
			}
			
			if(infCounter <= supCounter) {
				
				for(int i = infCounter; i <= supCounter; i++) {
					aux.add(dList.get(i));
				}
			}
			result.add(aux);
		}
		
		return result;
	}
	
	
	/*
	 * Removes useless cells 
	 */
	private static List<List<Integer>> trimSolution(List<List<Integer>> solution) {
		
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		for(List<Integer> a: solution) {
			
			List<Integer> aux = new ArrayList<Integer>();
			
			int infCounter = 0;
			int supCounter = a.size() - 1;
			int auxCounter = 0;
			int i = 0;
			
			while(i < a.size()) {
				
				Integer element = a.get(i);
				
				if(element == 1) {
					auxCounter++;
				}
				else if(element == 2) {
					break;
				}
				else {
					infCounter += auxCounter + 1;
					auxCounter = 0;
				}
				i++;
			}
			
			auxCounter = 0;
			i = a.size() -1;
			
			while(i >= 0) {
				
				Integer element = a.get(i);
				
				if(element == 1) {
					auxCounter++;
				}
				else if(element == 2) {
					break;
				}
				else {
					supCounter -= (auxCounter + 1);
					auxCounter = 0;
				}
				i--;
			}
			
			if(infCounter <= supCounter) {
				
				for(int j = infCounter; j <= supCounter; j++) aux.add(a.get(j));
			}
			result.add(aux);
		}
		
		return result;
	}
	
	
	/*
	 * Restores the data array, which has been previously reduced by removing completed elements
	 */
	private static List<List<Datum>> restoreData(List<List<Datum>> dataBackup, List<List<Datum>> data) {
		
		List<List<Datum>> result = new ArrayList<List<Datum>>();
		
		int i = 0;
		
		for(List<Datum> a: dataBackup) {
			
			List<Datum> aux = new ArrayList<Datum>();
			
			int infCounter = 0;
			int supCounter = a.size() - 1;
			
			while(infCounter < a.size()) {
				
				if(a.get(infCounter).isCompleted()) infCounter++;
				else break;
			}
			
			while(supCounter >= 0) {
				
				if(a.get(supCounter).isCompleted()) supCounter--;
				else break;
			}
			
			if(infCounter <= supCounter) {
				
				for(int j = 0; j < infCounter; j++) aux.add(a.get(j));
				for(int j = 0; j < supCounter - infCounter + 1; j++) aux.add(data.get(i).get(j));
				for(int j = supCounter + 1; j < a.size(); j++) aux.add(a.get(j));	
			}
			else {
				aux = a;
			}
			
			result.add(aux);
			
			i++;
		}
		
		return result;
	}
	
	
	
	/*
	 * Restores the solution array, which has been previously reduced by removing useless cells
	 */ 
	private static List<List<Integer>> restoreSolution(List<List<Integer>> solutionBackup, List<List<Integer>> solution) {
		
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		
		int k = 0;
		
		for(List<Integer> a: solutionBackup) {
			
			List<Integer> aux = new ArrayList<Integer>();
			
			int infCounter = 0;
			int supCounter = a.size() - 1;
			int auxCounter = 0;
			int i = 0;
			
			while(i < a.size()) {
				
				Integer element = a.get(i);
				
				if(element == 1) {
					auxCounter++;
				}
				else if(element == 2) {
					break;
				}
				else {
					infCounter += auxCounter + 1;
					auxCounter = 0;
				}
				i++;
			}
			
			auxCounter = 0;
			i = a.size() -1;
			
			while(i >= 0) {
				
				Integer element = a.get(i);
				
				if(element == 1) {
					auxCounter++;
				}
				else if(element == 2) {
					break;
				}
				else {
					supCounter -= (auxCounter + 1);
					auxCounter = 0;
				}
				i--;
			}
			
			if(infCounter <= supCounter) {
				
				for(int j = 0; j < infCounter; j++) aux.add(a.get(j));
				for(int j = 0; j < supCounter - infCounter + 1; j++) aux.add(solution.get(k).get(j));
				for(int j = supCounter + 1; j < a.size(); j++) aux.add(a.get(j));
				
			}
			else {
				aux = a;
			}
			
			result.add(aux);
			
			k++;
		}
		
		return result;
	}
	
	
	/*
	 * Checks whether there is a datum with lenght greater than "gap"
	 */
	private static boolean isSmallGap(int gap, List<Datum> dataRow) {
	
		for(Datum d: dataRow) {
			
			int value = d.getValue();
			
			if(value <= gap) {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Checks whether, once I find an element "2", the previous element (other than "2") is "1"
	 */
	private static boolean previousIsOne(int index, List<Integer> row) {
		
		while(index >= 0) {
			
			Integer element = row.get(index);
			
			if(element == 1) {
				return true;
			}
			
			index--;
		}
		
		return false;
	}
	
}
