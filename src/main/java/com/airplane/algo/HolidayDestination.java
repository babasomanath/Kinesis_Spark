package com.airplane.algo;

/***
 * 
 * 
 
Find the Most Popular Destination:

Your task is to find the most popular holiday destination from a list of destinations searched for by users. You are given as standard input the integer size of the list, followed by the names of the destinations themselves. The input has the following format:

    on the first line, the count of items in the list
    on the subsequent lines, the name of each destination searched for, one per line (each destination is a single word with no spaces, destinations can be searched for and appear more than once) The input is correct. There is at least one destination in the input. Write a program that reads the input from stdin and then outputs out the name of the most searched for destination i.e. the destination that appears most in the list. One destination is guaranteed to be the outright winner in the input.

Examples:

    Input:

    6
    Barcelona
    Edinburgh
    Barcelona
    Miami
    Miami
    Barcelona

    Output:

    Barcelona

    Input:

    5
    Singapore
    Bangkok
    Singapore
    Bangkok
    Singapore

    Output:

    Singapore

 * 
 * 
 */


import java.util.Comparator;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @author Somanath Nanda : I solved this in 21 minutes. And I was so happy about it.
 *
 */

class ValueCompare implements Comparator<String>{
	Map<String,Integer> toCompare;
	public ValueCompare(Map toCompare) {
		this.toCompare = toCompare;
	}
	@Override
	public int compare(String searchStringA, String searchStringB) {
		if (toCompare.get(searchStringA) >= toCompare.get(searchStringB)) {
			return -1;
		} else {
			return 1;

		}

	}
}

public class HolidayDestination {
	public static void main(String args[]){
		try{
			Scanner inNum = new Scanner(System.in);
			Scanner inString = null;
			Map<String, Integer> searchMap = new HashMap<String, Integer>();
			ValueCompare vc = new ValueCompare(searchMap);
			TreeMap<String, Integer> sortMap = new TreeMap(vc);
			int nextInt = inNum.nextInt();
			for(int i=0;i<nextInt;i++){
				inString = new Scanner(System.in);
				String searchString = inString.next();
				if(searchMap.containsKey(searchString)){
					Integer val = searchMap.get(searchString);
					searchMap.put(searchString, ++val);
				}else{
					searchMap.put(searchString,1);
				}
			}
			System.out.println(searchMap);
			sortMap.putAll(searchMap);
			System.out.println(sortMap);
			System.out.println("top Search : "+sortMap.firstKey());
		}catch(InputMismatchException ime){
			System.err.print("not a valid number");
		}
	}
}

