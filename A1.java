//* File:                       A1.java
// * Course:                    COMP2240
//  * Assignment:               Assignment1
//   * Name:                    Juyong Kim  
//    * Student Number:         c3244203
//     * Purpose:               Main file
//      * Note:                 very minimum is done here

import java.io.*;
import java.util.*;

public class A1
{
	public static void main(String args[])  throws IOException
	{
		File file = new File(args[0]);									//adds file
		PrintWriter pw = new PrintWriter(System.out);					//Prints formatted representations of objects to a text-output stream
		StringBuffer sb = new StringBuffer("");							//a string that can be modified
		BufferedReader br = new BufferedReader(new FileReader(file));	//read char by char
		OutputController output = new OutputController(br, pw, sb);
		
		//currently not sure how things will print
		System.out.println();
		pw.close();
	}
}