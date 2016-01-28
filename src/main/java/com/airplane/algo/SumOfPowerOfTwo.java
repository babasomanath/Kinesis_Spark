package com.airplane.algo;

import java.util.Scanner;
/**
 * 
 * @author Somanath Nanda
 * 
 * Problem Statement : Write function to calculate sum of first N powers of 2 starting from 1. 
 * You shouldn't use any built-in function for calculating power. Implement the most efficient solution.
 *
 */
public class SumOfPowerOfTwo {
	public static void main(String args[]){
		Scanner inNum = new Scanner(System.in);
		int nTimes = inNum.nextInt();
		int sum = 0;
		for(int i=1;i>=1&&i<=nTimes;i++){
			sum = sum+(1<<i);
			System.out.println("Result : "+sum);
		}
		System.out.println("Total Result : "+sum);
	}
}
