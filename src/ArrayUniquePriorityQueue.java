/*
 * @Author: Allison Ye, aye28, 251339668
 * @Date: March 20, 2024
 * @Description: This code creates the methods to help calculate the best and safest path for the frog
 */

public class ArrayUniquePriorityQueue <T> {
	
	//Declare private instance variables
	private T[] queue;
	private double[] priority;
	private int count;
	
	//Declare variables used to determine the priority queue for the frog's path
	public ArrayUniquePriorityQueue () {
		 queue = (T[]) new Object[10];
	     priority = new double[10];
	     count = 0;
	}
	
	//Add data to the priority queue
	public void add (T data, double prio) {
		//If the data is already included in the priority queue, the method is ended
		if (contains(data)) {
			return;
		}
		
		//If the priority queue is full, add 5 more spaces
		if (queue.length == count) {
			int newCapacity = count + 5;
			//Create a new array with more space
			T[] largerListQueue = (T[]) new Object[newCapacity];
			double[] largerListPrio = new double[newCapacity];
			for (int i = 0; i < count; i++) {
				largerListQueue[i] = queue[i];
				largerListPrio[i] = priority[i];
			}
			
			//Copy over existing data to new array
			queue = largerListQueue;
			priority = largerListPrio;
		}
		
        int index = count - 1;
        
        //Find where the added data will be stored depending on its priority
        while ( index >= 0 && prio < priority[index]) {
        	queue[index + 1] = queue[index];
        	priority[index + 1] = priority[index];
        	index--;
        }
        
        //Add the data to the priority queue depending on the index found by its priority score
        queue[index + 1] = data;
        priority[index + 1] = prio;
        count++;
		
	}
	
	//Checks to see if the priority queue already contains the given data
	public boolean contains (T data) {
		for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                return true;
            }
        }
		return false;
	}
	
	//Check for the data with lowest priority 
	public T peek () throws CollectionException{
		//Checks to see if priority queue is empty
		if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
		else {
			return queue[0];
		}
	}
	
	//Removes the data with the lowest priority
	public T removeMin () throws CollectionException{
		//Checks to see if priority queue is empty
		if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
		//If not empty, remove and return the lowest priority data
		else {
			//Set min to hold the data for the lowest priority
			T min = queue[0];
			//Shift all data to the left to remove min
			for(int i = 0; i < count - 1; i++) {
				queue[i] = queue[i+1];
				priority[i] = priority[i+1];
			}
			//Set last data piece to empty
			queue[count - 1] = null;
			priority[count - 1] = 0;
			count--;
			return min;
		}
	}
	
	//Update data with given new priority queue
	public void updatePriority (T data, double newPrio) throws CollectionException{
		//If the given data is not already found in the priority queue
		if (!contains(data)) {
			throw new CollectionException("Item not found in PQ");
		}
		
		int index = 0;
		
		//Find the index of the data in the given priority queue
		for(int i = 0; i < count; i++) {
			if (queue[i].equals(data)){
				index = i;
				break;
			}
		}
		
		//Remove the data with the old priority
		//If the data with new priority is at the end, set last to empty
		if(index == count-1) {
			queue[index] = null;
			priority[index] = 0;
		}
		//Otherwise, shift all data to the left after removing the old data
		else {
			for(int i = index; i < count - 1; i++) {
				queue[i] = queue[i+1];
				priority[i] = priority[i+1];
			}
			queue[count-1] = null;
			priority[count-1] = 0;
		}
		
		count--;
		
		//Add new priority score data back into the priority queue
		add(data, newPrio);
		
	}
	
	//Determines if the priority queue is empty
	public boolean isEmpty () {
		if (count == 0){
			return true;
		}
		else {
			return false;
		}
	}
	
	//Returns the size of the priority queue
	public int size () {
		return count;
	}
	
	//Return the length of the priority queue
	public int getLength () {
		return queue.length;
	}
	
	
	//Returns the priority queue with its data and corresponding priority queue
	public String toString () {
		String output = "";
		if (isEmpty()) {
            output += "The PQ is empty";
        }
		else {
			for (int i = 0; i < count; i++) {
	            output += queue[i] + " [" + priority[i] + "], ";
	        }
			output = output.substring(0, (output.length() - 2));
		}
        return output;
	}
}



