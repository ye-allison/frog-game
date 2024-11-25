/*
 * @Author: Allison Ye, aye28, 251339668
 * @Date: March 20, 2024
 * @Description: This code creates the path that the frog will take
 * The best, and safest path is calculated for the frog
 */

import java.io.FileNotFoundException;
import java.io.IOException;

public class FrogPath {

	//Declare private instance variable
	private Pond pond;
	
	//Compute the priority of neighbouring cells of the frog's current cell
	private double computePrio(Hexagon neighbour) {
		//Sets the priority score depending on the neighbouring cell
		double prio = 0.0;
		if(neighbour.isEnd()) {
			prio = 3.0;
		}
		else if(neighbour.isLilyPadCell()) {
			prio = 4.0;
		}
		//If the neighbouring cell is a reed, check if there is an alligator beside the reed cell to determine priority score
		else if(neighbour.isReedsCell()) {
			int i = 0;
			for(i = 0; i < 6; i++) {
				Hexagon secondNeighbour = neighbour.getNeighbour(i);
				if(secondNeighbour != null && (secondNeighbour.isAlligator())) {
					prio = 10.0;
					break;
				}
			}
			if(6 == i) {
				prio = 5.0;
			}
		}
		else if(neighbour.isWaterCell()) {
			prio = 6.0;
		}
		//If the neighbouring cell has flies, determine the number of flies in order to compute priority score
		else if (neighbour instanceof FoodHexagon) {
			if(((FoodHexagon) neighbour).getNumFlies() == 3){
				prio = 0.0;
			}
			else if(((FoodHexagon) neighbour).getNumFlies() == 2){
				prio = 1.0;
			}
			else if(((FoodHexagon) neighbour).getNumFlies() == 1){
				prio = 2.0;
			}
			
		}
		
		return prio;
	}
	
	//Determine whether a cell is safe for the frog
	private boolean isCellSafe(Hexagon cell) {
		if(cell == null) {
			return false;
		}
		//Unsafe if the cell has an alligator or is a mud cell
		else if(cell.isAlligator()||cell.isMudCell()) {
			return false;
		}
		//If the cell beside an alligator is a reed cell, it is safe
		for(int i = 0; i < 6; i++) {
			Hexagon cellNeighbour = cell.getNeighbour(i);
			if(cellNeighbour != null && (cellNeighbour.isAlligator())) {
				if(!cell.isReedsCell()) {
					return false;
				}
			}
		}

		return true;
	}
	
	//Creates the path for the frog
	public FrogPath (String filename) {
		try {
			pond = new Pond(filename);
		}
		//Catches exceptions when determing the 
		catch(InvalidMapException e){
			System.out.println("Invalid Map File");
		}
		catch(FileNotFoundException e) {
			System.out.println("File Not Found");
		}
		catch(IOException e){
			System.out.println("Input Incorrect");
		}
	}
	
	//Determines the best path for the frog
	public Hexagon findBest(Hexagon currCell) {
		ArrayUniquePriorityQueue<Hexagon> pq = new ArrayUniquePriorityQueue<>();
		
		//Determine the priority score of neighbouring cells if it is a safe cell
		for(int i = 0; i < 6; i++) {
			Hexagon neighbour = currCell.getNeighbour(i);
			if(neighbour != null && (!neighbour.isMarked())) {
				double prioNum = computePrio(neighbour);
				if(isCellSafe(neighbour)) {
					pq.add(neighbour, prioNum);
				}
			}
		}
		
		//If the frog is on a lily pad, it can jump two spaces
		if(currCell.isLilyPadCell()) {
			for(int i = 0; i < 6; i++) {
				Hexagon neighbour = currCell.getNeighbour(i);
				if(neighbour != null) {
					//IF the cell is in a straight line, add 0.5 priority score, otherwise add 1.0
					for(int j = 0; j < 6; j++) {
						Hexagon secondNeighbour = neighbour.getNeighbour(j);
						if(secondNeighbour != null && (!secondNeighbour.isMarked())) {
							double prioNum = computePrio(secondNeighbour);
							if(i == 0) {
								if(j == 5) {
									prioNum += 1.0;
								}
								if(j == 0) {
									prioNum += 0.5;
								}
								if(j == 1) {
									prioNum += 1.0;
								}
							}
							if(i == 5) {
								if(j == 4) {
									prioNum += 1.0;
								}
								if(j == 5) {
									prioNum += 0.5;
								}
								if(j == 0) {
									prioNum += 1.0;
								}
							}
							else if (1 <= i && i <= 4){
								if(j == i - 1) {
									prioNum += 1.0;
								}
								if(j == i) {
									prioNum += 0.5;
								}
								if(j == i + 1) {
									prioNum += 1.0;
								}
							}
							if(isCellSafe(secondNeighbour)) {
								pq.add(secondNeighbour, prioNum);
							}
						}
					}
				}
			}
		}
		
		//If there are reachable unmarked cells, return the one with the lowest priority
        if (!pq.isEmpty()) {
        	return pq.removeMin();
        }
        // Return null if pq is empty 
        else {
        	return null;
        }

	}
	
	//Find the path the frog will take
	public String findPath() {
		
		ArrayStack<Hexagon> S = new ArrayStack<>();
		Hexagon start = pond.getStart();
        S.push(start);
        start.markInStack();
        int fliesEaten = 0;
        
        String output = "";
        
        //Determine what cell the frog is currently on and add it to the final output
        while (!S.isEmpty()) {
        	Hexagon curr = S.peek();
        	output += curr.getID() + " ";
        	
        	if (curr.isEnd()) {
        		break;
        	}
        	
        	//Count number of flies eaten, and remove it from the cell
        	if (curr instanceof FoodHexagon) {
                FoodHexagon flies = (FoodHexagon) curr;
                fliesEaten += flies.getNumFlies();
                flies.removeFlies();
            }
        	
        	Hexagon next = findBest(curr);
            
        	//Mark the current cell, or check the next one
        	if (next == null) {
                 S.pop();
                 curr.markOutStack();
            } 
        	else {
                 S.push(next);
                 next.markInStack();
            }

        	
        }
       
        //Create the final output for the path
        if (S.isEmpty()) {
        	output = "No solution";
        } 
        else {
        	output += "ate " + fliesEaten + " flies";
        }
	    
        return output;
	}
	
	public static void main (String[] args) {
		 if (args.length != 1) {
			 System.out.println("No map file specified in the arguments");
			 return;
		 }
		 
		 FrogPath fp = new FrogPath(args[0]);
		 Hexagon.TIME_DELAY = 500; // Change this time delay as desired.
		 String result = fp.findPath();
		 System.out.println(result);
	}
}
