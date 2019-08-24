//* File:                       Scanner.java
// * Course:                    COMP2240
//  * Assignment:               Assignment1
//   * Name:                    Juyong Kim  
//    * Student Number:         c3244203
//     * Purpose:               the lexical analyzer converts a sequence of characters into a sequence of 
//      *                       distinct keywords, identifiers, constants, and delimiters.
//       * Note:                Just SCANS doesn't do syntactic processing

import java.io.*;
import java.util.*;

public class Scanner
{
    private boolean EOF;    //end of file
    private int CP, CR;     //current position, current row

	//reserved words to not use
	/* 
	CD19 constants types is arrays main begin end array of func 
	void const integer real boolean for repeat until if else input 
	print printline return not and or xor true false
	*/
    private enum State
    {
		//Start, identifier, error,
		  START, IDENT, ERROR,
		//String, Integer Literal, integer., float literal
		  STRING, INTLIT, INTDOT, FLOLIT,
		//<=, 	>=,    !=,   ==,   +=,    -=,    *=,      /,     %=,      /-,      /--,
		  LEQL, GEQL, NEQL, EQL, PLUSEQL, MINEQL, MULTEQL, SLASH, PEREQL, SLASHDASH, COMMENT
    }

    //constructor
    public Scanner()
    {
        this.EOF = false;		//when made its currently not at the end of file yet
        this.CP = 0;			//current position is 0
        this.CR = 0;			//current row is 0
    }

    public Token nextToken() throws IOException
    {
        Token foundToken = null;
        String buffer = "";
        

        while(foundToken == null)
        {
            switch()
            {
    
            }
        }

        return foundToken;
    }

    //Used For Debugging purposes and to check

    //Check for all valid char in the CD18
    private boolean isValidChar(char c)
    {
        System.out.println(" isWhiteSpace || isDigit || isLetter || isValidSymbol ");
        return Character.isWhitespace(c) || Character.isDigit(c) || Character.isLetter(c) || isValidSymbol(c);
    }
    //Check for all valid sym in the CD18
    private boolean isValidSymbol(char c)
    {
        return ";[],()=+-*/%^<>\":.!".indexOf(c) >= 0;
    }
}
