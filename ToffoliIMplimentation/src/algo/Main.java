package algo;

import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		//		System.out.println(" === Running Ant Colony Optimization algorithm ==  ");
		/*	ACO aco = new ACO(3);
		aco.startAntOptimization();
		 */	//System.out.println("==== Running Ant Colony Optimization algorithm ==");

		int arr[] = new int [] {0,5,2,7,4,1,6,3};

		//		System.out.println(applyToffoliGate(new int [] {2,0,0}, 1, 6));

		System.out.println("Final Result:");
		for (int i = 0; i < arr.length; i++) {
			System.out.print(applyToffoliGate(new int [] {0,2,2}, 3, arr[i]) +",");
		}

		//		System.out.println(toggleKthBit(5, 1));

	}

	public static int	applyToffoliGate(int[] vectorC, int targetBit, int actualNumber) { 

		int resultingNumber = 0;
		boolean isNotGate = true;
		
		Map<Integer, Integer> positionMap = new HashMap<>();
		int counter =3;
		for (int i = 0; i < vectorC.length; i++) {
			if(vectorC[i] != 2)
				positionMap.put(counter, vectorC[i]);
               
			if(vectorC[i] != 2) {
				isNotGate = false;
			}
			counter--;
		}
		if(!isNotGate) { 
			if(vectorC[targetBit -1] != 2) {
				System.out.println("Invalid toffoli operation found !! TargetBit:"+ targetBit + "Actual control vector:"+vectorC);
			}
			Map<Integer, Integer> indexUpdatedBit= new HashMap<>();
            System.out.println(positionMap);
			for(Map.Entry<Integer,Integer> positionEntry: positionMap.entrySet()) {
				int bitValue =  getIthBit(actualNumber, 3-positionEntry.getKey());
				if(positionEntry.getValue() ==1)
					indexUpdatedBit.put(positionEntry.getKey(), toggleKthBit(actualNumber, 3-targetBit));
				else if(positionEntry.getValue() == 0) {
					indexUpdatedBit.put(positionEntry.getKey(), bitValue);
				} else {
					System.out.println("Wrong control vector found, Vector Value:"+positionEntry.getValue() +" control vector "+vectorC);
				}
			}
			boolean flipBit = true;
			for(Map.Entry<Integer, Integer> entry : indexUpdatedBit.entrySet()) {
				if(entry.getValue()==0) {
					flipBit = false;
					break;
				}
			}
			if(flipBit) {
				resultingNumber = toggleKthBit(actualNumber, targetBit);
			} else {
				resultingNumber = actualNumber;
			}
		}else {
			resultingNumber = toggleKthBit(actualNumber, targetBit);
		}
		return resultingNumber;
	}

	private static int toggleKthBit(int n, int k) { 
		return (n ^ (1 << (k-1))); 
	} 

	private static int getIthBit(int number, int bitPosition) {
		return Character.getNumericValue(convertTo3DigitBinary(number).charAt(bitPosition));
	}

	private static String convertTo3DigitBinary(int number) { 
		String binaryString = Integer.toBinaryString(number);
		String prependedString ="00"+binaryString;
		 String binary = prependedString.substring(prependedString.length()-3);
		return binary;
	}

}
