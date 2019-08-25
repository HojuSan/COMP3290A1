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
	private OutputController output;	//outputcontroller
	private boolean prevFlag = false; 	//flag to see if token is finished or not
	private char prevChar = 'k';		//currently saved character
	private boolean finished = false;
	
	private boolean debug = false;//true;		//	if(debug == true){
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
    public Scanner(OutputController output)
    {
		this.output = output;
        this.EOF = false;		//when made its currently not at the end of file yet
        this.CP = 0;			//current position is 0
		this.CR = 0;			//current row is 0
		if(debug == true){System.out.println("scanner");}
    }

	public void nextToken() throws IOException
    {
        Token foundToken = null;
		String buffer = "";
		State currentState = State.START;

		while(foundToken == null)
        {
            char c;
            //if a token needs to be finished off
            if (prevFlag)
            {
				if(debug == true){System.out.println("state 1");}
                c = prevChar;
                prevFlag = false;
            }
            //or start on next token
            else
            {
//repeats since it has no other switch statement to go into
				if(debug == true){System.out.println("state2, cp is at "+CP);}
                CP++;									//start new position
                c = (char)output.readChar();			//get new character from outputcontroller
            }
            //when its a newline reset coutners
            if(c == '\n')
            {
				if(debug == true){System.out.println("state3");}
                CR++;
                CP = 0;
			}
			
			//FINITE STATEMACHINE
            switch(currentState)
            {
				//testing just numbers and chars
//incomplete
				case START:
				if(debug == true){System.out.println("inside switch statement start");}
				//ripped directly from token class, not to be confused with state
				//for single symbols just create a token
				else if (isValidSymbol(c))
				{
					if (c=='^') foundToken = new Token(Token.TCART, CR, CP, null);
					if (c=='(') foundToken = new Token(Token.TLPAR, CR, CP, null);
					if (c==')') foundToken = new Token(Token.TRPAR, CR, CP, null);
					if (c=='[') foundToken = new Token(Token.TLBRK, CR, CP, null);
					if (c==']') foundToken = new Token(Token.TRBRK, CR, CP, null);
					if (c==',') foundToken = new Token(Token.TCOMA, CR, CP, null);
					if (c=='.') foundToken = new Token(Token.TDOT, CR, CP, null);
					if (c==';') foundToken = new Token(Token.TSEMI, CR, CP, null);
					if (c==':') foundToken = new Token(Token.TCOLN, CR, CP, null);
				}
				//
				if(Character.isWhitespace(c))			//white spaces
				{
					//At 4 am in the morning, my mind is as blank
					//as this whitespace character
					//which does nothing but be empty and do nothing
				}
				else if (Character.isDigit(c)) 			//DIGIT-Mons
				{
					currentState = State.INTLIT;
					buffer += c;
				}
				else if (Character.isLetter(c))			//Alpahbets
				{
					currentState = State.IDENT;
					buffer += c;
				}

				//Operations
				else if (c == '+') 					//plus
				{
					currentState = State.PLUSEQL;
					buffer += c;
				}
				else if (c == '-') 					//minus
				{
					currentState = State.MINEQL;
					buffer += c;
				}
				else if (c == '*') 					//multiplication
				{
					currentState = State.MULTEQL;
					buffer += c;
				}
				else if (c == '/') 					//divison
				{
					output.mark(4);
					currentState = State.SLASH;
					buffer += c;
				}
				else if (c == '%') 					//percentage
				{
					currentState = State.PEREQL;
					buffer += c;
				}
				else if (c == '\"')					//string
				{
					currentState = State.STRING;
					buffer += c;
				}
				else if (c == '!')					//exclamation
				{
					currentState = State.NEQL;
					buffer += c;
				}
				else if (c == '=') 					//equal sign
				{
					currentState = State.EQL;
					buffer += c;
				} 
				else if (c == '<') 					//less than
				{
					currentState = State.LEQL;
					buffer += c;
				} 
				else if (c == '>') 					//greater than
				{
					currentState = State.GEQL;
					buffer += c;
				}

				//converting char c to EOF value into token
				else if ((byte)c == -1)					//Reached end of file
				{
					EOF = true;
					foundToken = new Token(Token.TEOF, CP, CR, null);
				}
				else
				{
					currentState = State.ERROR;
					buffer += c;
				}
				break;

				//Identifier
//finished?
				case IDENT:
					if (Character.isDigit(c) || Character.isLetter(c)) //check if valid character
					{
						buffer += c;
					}
					else 											   //tokenise and continue work
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TIDEN, CR, CP, buffer);	//tuple token
						buffer = "";
					}
				break; 
//Finished
				case INTLIT:
                    if (Character.isDigit(c))			//remains in current state, adds to buffer
                    {
                        buffer += c;
                    }
                    else if (c == '.' && !finished)		//Move onto integer followed by dot state
                    {
                        currentState = State.INTDOT;
                        buffer += c;
                    }
                    else                                //create token but return back to this to finish it
                    {
                        finished = false;				//its not finished yet
                        prevFlag = true;				//set previous flag
                        prevChar = c;					//set the previous character
                        currentState = State.START;		//return to start
                        foundToken = new Token(Token.TILIT, CR, CP, buffer);		//create a token of type tuple
                        buffer = "";
                    }
				break;
				case INTDOT:
				break;

				case STRING:
				break;
				case FLOLIT:
				break;
				case LEQL:
				break;
				case GEQL:
				break;
				case NEQL:
				break;
				case EQL:
				break;
				case PLUSEQL:
				break;
				case MINEQL:
				break;
				case MULTEQL:
				break;
				case SLASH:
				break;
				case PEREQL: 
				break;
				case SLASHDASH: 
				break;
				case COMMENT:
				break;
				case ERROR:
					if (!isValidChar(c) && !((byte)c == -1)) 
					{
						buffer += c;
					}
					else
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TUNDF, CR, CP, buffer);
						buffer = "";											//reset buffer
					}
				break;
            }
        }

		System.out.println(foundToken.toString());
		if(debug == true){System.out.println("end of a loop");}
        //return foundToken;
    }

	//getters
	//end of file check
	public boolean isEOF()
	{
		return EOF;
	}

    //Used For Debugging purposes and to check

    //Check for all valid char within the CD18 language
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
