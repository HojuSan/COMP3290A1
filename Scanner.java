//* File:                       Scanner.java
// * Course:                    COMP2240
//  * Assignment:               Assignment1
//   * Name:                    Juyong Kim  
//    * Student Number:         c3244203
//     * Purpose:               the lexical analyzer converts a sequence of characters into a sequence of 
//      *                       distinct keywords, identifiers, constants, and delimiters.
//       * Note:                Just SCANS doesn't do syntactic processing
//		  *						In computer science, an integer literal is a kind of literal for an integer 
//		   *					whose value is directly represented in source code.

import java.io.*;
import java.util.*;

public class Scanner
{
    private boolean EOF;    			//end of file
	private int CP, CR;     			//current position, current row
	private OutputController output;	//outputcontroller
	private boolean prevFlag = false; 	//flag to see if token is finished or not
	private char prevChar = 'k';		//currently saved character
	private boolean finished = false;
	
	private boolean debug = false;//true;		//if(debug == true){
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
			//Start, leads tokens to appropriate statements
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
					output.mark(20);
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
				else if (c == '-') 					//minus, includes special cases such as neg integers
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
					output.mark(20);
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

			//Variable settings

				//Identifier
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

				//String
				case STRING:
                    //Check for new line delimeters and add character
                    if (c != '\"' && !((byte)c == -1) && c != '\n' && c != '\r') 
                    {
                        buffer += c;
                    }
                    //If delimeters exist then lexical error
                    else if (c == '\n' || c == '\r' || ((byte)c == -1))
                    {
						//output.setError("!!string is not complete");
                        currentState = State.ERROR;
                    } 
                    //Else tokenize
                    else if (c == '\"') 
                    {
                        currentState = State.START;
                        //Remove initial quotation mark from the string buffer
                        buffer = buffer.substring(1);
                        foundToken = new Token(Token.TSTRG, CR, CP, buffer);
                        buffer = "";
                    }
				break;

                //Comments
				case COMMENT:	//ignores everything till new line or eof
					//\n is new line, \r is carriage return -1 is eof
                    if (c == '\n' || c == '\r' || ((byte)c == -1))
                    {
                        buffer = "";
                        currentState = State.START;
                    }
				break;

				//error cases go to output controller
				case ERROR:
					if (!isValidChar(c) && !((byte)c == -1)) 
					{
						buffer += c;
					}
					else			//if you don't know what it is set it as undefined for now
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TUNDF, CR, CP, buffer);
						buffer = "";											//reset buffer
					}
				break;

				//Integer literal
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
					else if (!Character.isDigit(c) && !finished)		//Move onto integer followed by dot state
					{
						currentState = State.ERROR;
					}
					else                                //create token but return back to this to finish it
					{
						finished = false;				//its not finished yet
						prevFlag = true;				//set prevFlag flag
						prevChar = c;					//set the prevFlag character
						currentState = State.START;		//return to start
						foundToken = new Token(Token.TILIT, CR, CP, buffer);		//create a token of type tuple
						buffer = "";
					}
				break;

				//integer with a  dot
				case INTDOT:
					if (Character.isDigit(c))	//creates the digit and sets it into a float
					{
						currentState = State.FLOLIT;
						buffer += c;
					}
					else						//else set finished
					{
						//output.setError("!!FLOAT LITERAL is not complete");
						finished = true;
						output.reset();
						currentState = State.START;
						c = buffer.charAt(0);
						buffer = "";
						buffer+=c;
					}
				break;

				//float literal
				case FLOLIT:
					//adds number and remains in state
					if (Character.isDigit(c))
					{
						buffer += c;
					}
					//else	create a token and continue
					else
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TFLIT, CR, CP, buffer);
						buffer = "";
					}
				break;

			//Something and equals

				// == equals
				case EQL:
					if (c == '=') 		//if the prevFlag character also was =, becomes an ==
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TEQEQ, CR, CP, null);
					}
					else				//else becomes a regular equal sign
					{
						prevFlag= true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TEQUL, CR, CP, null);
						buffer = "";
					}				
				break;

				//<= less than or equal to
				case LEQL:
					if (c == '=') 		//if currently equal becomes <=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TLEQL, CR, CP, null);
					}
					else				//if not just a regular < sign
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TLESS, CR, CP, null);
						buffer = "";
					}
				break;

				//>= greater than or equal to		
				case GEQL:
					if (c == '=') 		//if equal sign greater or equal to
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TGEQL, CR, CP, null);
					}
					else				//else just greater than
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TGRTR, CR, CP, null);
						buffer = "";
					}
				break;

				//!= not equal
				case NEQL:
					if (c == '=') 		//if equal !=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TNEQL, CR, CP, null);
					}
					else 				//else just a regular !
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.ERROR;
					}
				break;

				//+= add and equal
				case PLUSEQL:
					if (c == '=') 		//if = then +=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TPLEQ, CR, CP, null);
					}
					else				//else just a regular +
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TPLUS, CR, CP, null);
						buffer = "";
					}
				break;

				//-= minus then equal
				case MINEQL:
					if (c == '=') 		//if = then -=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TMNEQ, CR, CP, null);
					}
					else				//else just a regular -
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TMINS, CR, CP, null);
						buffer = "";
					}
				break;

				//*= multiple then equal to
				case MULTEQL:
					if (c == '=') 		//if = then *=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TSTEQ, CR, CP, null);
					}
					else				//else just a regular *
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TSTAR, CR, CP, null);
						buffer = "";
					}
				break;

				//%= percentage equal
				case PEREQL: 
					if (c == '=') 	//if = then %=
					{
						currentState = State.START;
						buffer = "";
						foundToken = new Token(Token.TPCEQ, CR, CP, null);
					}
					else			//else just a regular %
					{
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TPERC, CR, CP, null);
						buffer = "";
					}
				break;

				//Special case Scenarios
				case SLASH:
					// /= slash equals
					if(c == '=')		//WORKS!!!!!
					{
						currentState = State.START;
						buffer = "";					//empty the buffer
						foundToken = new Token(Token.TDVEQ, CR, CP, null);
					}
					// /-	slash dash, probably becomes a comment
					else if(c == '-' && !finished)	//this scenario transfers into SLASHDASH, probably a comment
					{
						buffer += c;				//add it to the buffer
						currentState = State.SLASHDASH;
					}
					else
					{
						finished = false;
						prevFlag = true;
						prevChar = c;
						currentState = State.START;
						foundToken = new Token(Token.TDIVD, CR, CP, null);
                        buffer = "";				//clear buffer
					}
				break;
////////////////////
				// /-
				case SLASHDASH: 
					if (c == '-') 						// if the next char is also a - its a comment
					{
						buffer += c;
						currentState = State.COMMENT;
					}
					else								//if it doesn't have the extra dash its an error
					{
						//output.setError("!!comment is missing a dash sign");
						finished = true;
						output.reset();
						currentState = State.START;
						c = buffer.charAt(0);
						buffer = "";
						buffer+=c;
					}
				break;
            }
        }

		System.out.println(foundToken.debugString());
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
        //System.out.println(" isWhiteSpace || isDigit || isLetter || isValidSymbol ");
        return Character.isWhitespace(c) || Character.isDigit(c) || Character.isLetter(c) || isValidSymbol(c);
    }
    //Check for all valid sym in the CD18
    private boolean isValidSymbol(char c)
    {
        return ";[],()=+-*/%^<>\":.!".indexOf(c) >= 0;
    }
}
