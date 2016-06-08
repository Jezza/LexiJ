package me.jezza.lexij.lexer;

/**
 * @author jezza
 * @date 4/12/2015
 */
public final class ElementTypes {

	private ElementTypes() {
		throw new IllegalStateException();
	}

	// Standard stuffs
	public static final byte NAMESPACE = 1; // a block of characters with no defined meaning.
	public static final byte NUMBER = 2; // 1
	public static final byte STRING = 3; // "
	public static final byte COMMENT = 4; // Line or block comments.

	// Method stuff
	public static final byte BRACE_L = 10; // {
	public static final byte BRACE_R = 11; // }
	public static final byte PARENTHESIS_L = 12; // (
	public static final byte PARENTHESIS_R = 13; // )
	public static final byte BRACKET_L = 14; // [
	public static final byte BRACKET_R = 15; // ]

	public static final byte COLON_EQUAL = 20; // :

//	public static final byte COLON = 20; // :
//	public static final byte QUESTION = 21; // ?
//	public static final byte SEMI_COLON = 22; // ;
//	public static final byte COMMA = 23; // ,
//	public static final byte PERIOD = 24; // .
//	public static final byte HASH = 25; // #

	// Operators
//	public static final byte EQUAL = 30; // =
//	public static final byte ADD = 31; // +
//	public static final byte SUB = 32; // -
//	public static final byte MUL = 33; // *
//	public static final byte DIV = 34; // /
//	public static final byte CONCAT = 35; // ..
//	public static final byte VARARGS = 36; // ...
	public static final byte PIPE = 36; // |

	// Logic

//	public static final byte LOGIC_NOT = 46; // ~

	// Keywords

//	public static final byte AND = 70;
//	public static final byte BREAK = 71;
//	public static final byte DO = 72;
//	public static final byte ELSE = 73;
//	public static final byte ELSEIF = 74;
//	public static final byte END = 75;
//	public static final byte FALSE = 76;
//	public static final byte FOR = 77;
//	public static final byte FUNCTION = 78;
//	public static final byte IF = 79;
//	public static final byte IN = 80;
//	public static final byte LOCAL = 81;
//	public static final byte NIL = 82;
//	public static final byte NOT = 83;
//	public static final byte OR = 84;
//	public static final byte REPEAT = 85;
//	public static final byte RETURN = 86;
//	public static final byte THEN = 87;
//	public static final byte TRUE = 88;
//	public static final byte UNTIL = 89;
//	public static final byte WHILE= 90;
}