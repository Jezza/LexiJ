package me.jezza.lexij.lang;

import java.util.Stack;

/**
 * @author Jezza
 */
public class LR1 {
	private static String exp;
	private static String token = "";
	private static int expIndex = 0;
	private static boolean valid = true;
	private static boolean complete = false;
	private static Stack<String> stack = new Stack<>();

	public static void main(String[] args) {
		exp = args[0].concat("$");
		System.out.println("Exp: " + exp);
		push("0");
		setToken();
		while (!complete && valid)
			switchCommand();
		System.out.println(valid ? "Expression is Valid" : "Expression is NOT Valid");
	}

	private static String pop() {
		String result = stack.pop();
		System.out.println("Popping: " + stack.toString() + " :: " + token + exp.substring(expIndex));
		return result;
	}

	private static void push(String s) {
		stack.push(s);
		System.out.println("Pushing: " + stack.toString() + " :: " + token + exp.substring(expIndex));
	}

	private static boolean isOperator(char c) {
		return c == '+' || c == '*' || c == '(' || c == ')' || c == '$';
	}

	private static boolean isInt(char c) {
		return c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0';
	}

	private static boolean isInt() {
		return isInt(token.charAt(0));
	}

	private static void setToken() {
		char c = exp.charAt(expIndex);
		if (isOperator(c)) {
			token = "" + c;
			expIndex++;
		} else if (isInt(c)) {
			token = "";
			while (isInt(c = exp.charAt(expIndex))) {
				token += c;
				expIndex++;
			}
		} else {
			valid = false;
			expIndex++;
			setToken();
		}
	}

	private static boolean tokenIs(char c) {
		return token.charAt(0) == c;
	}

	private static void switchCommand() {
		int command = isInt(stack.peek().charAt(0)) ? Integer.parseInt(stack.peek()) : -1;
		System.out.println("State: " + command);
		switch (command) {
			case 0:
				state0();
				break;
			case 1:
				state1();
				break;
			case 2:
				state2();
				break;
			case 3:
				state3();
				break;
			case 4:
				state4();
				break;
			case 5:
				state5();
				break;
			case 6:
				state6();
				break;
			case 7:
				state7();
				break;
			case 8:
				state8();
				break;
			case 9:
				state9();
				break;
			case 10:
				state10();
				break;
			case 11:
				state11();
				break;
			default:
				System.out.println("Invalid action");
				valid = false;
				break;
		}
	}

	private static void pushE() {
		if (stack.peek().equals("0")) {
			push("E");
			push("1");
		} else if (stack.peek().equals("4")) {
			push("E");
			push("8");
		} else
			valid = false;
	}

	private static void pushT() {
		if (stack.peek().equals("0")) {
			push("T");
			push("2");
		} else if (stack.peek().equals("4")) {
			push("T");
			push("2");
		} else if (stack.peek().equals("6")) {
			push("T");
			push("9");
		} else
			valid = false;
	}

	private static void pushF() {
		if (stack.peek().equals("0")) {
			push("F");
			push("3");
		} else if (stack.peek().equals("4")) {
			push("F");
			push("3");
		} else if (stack.peek().equals("6")) {
			push("F");
			push("3");
		} else if (stack.peek().equals("7")) {
			push("F");
			push("10");
		} else
			valid = false;
	}

	private static void state0() {
		if (isInt()) {
			//shift 5
			push(token);
			push("5");
			setToken();
		} else if (tokenIs('(')) {
			//shift 4
			push(token);
			push("4");
			setToken();
		}
	}

	private static void state1() {
		if (tokenIs('+')) {
			//shift 6
			push(token);
			push("6");
			setToken();
		} else if (tokenIs('$')) {
			//accept
			complete = true;
		} else {
			valid = false;
		}
	}

	private static void state2() {
		if (tokenIs('+') || tokenIs(')') || tokenIs('$')) {
			//E->T
			pop();
			if (pop().charAt(0) != 'T') {
				valid = false;
			}
			pushE();
		} else if (tokenIs('*')) {
			//shift 7
			push(token);
			push("7");
			setToken();
		} else {
			valid = false;
		}
	}

	private static void state3() {
		if (tokenIs('+') || tokenIs('*') || tokenIs(')') || tokenIs('$')) {
			//T->F
			pop();
			if (pop().charAt(0) != 'F') {
				valid = false;
			}
			pushT();
		} else {
			valid = false;
		}
	}

	private static void state4() {
		if (isInt()) {
			//shift 5
			push(token);
			push("5");
			setToken();
		} else if (tokenIs('(')) {
			//shift 4
			push(token);
			push("4");
			setToken();
		} else {
			valid = false;
		}
	}

	private static void state5() {
		if (tokenIs('+') || tokenIs('*') || tokenIs(')') || tokenIs('$')) {
			//F->id
			pop();
			if (!isInt(pop().charAt(0))) {
				valid = false;
			}
			pushF();
		} else {
			valid = false;
		}
	}

	private static void state6() {
		if (isInt()) {
			//shift 5
			push(token);
			push("5");
			setToken();
		} else if (tokenIs('(')) {
			//shift 4
			push(token);
			push("4");
			setToken();
		} else {
			valid = false;
		}
	}

	private static void state7() {
		if (isInt()) {
			//shift 5
			push(token);
			push("5");
			setToken();
		} else if (tokenIs('(')) {
			//shift 4
			push(token);
			push("4");
			setToken();
		} else {
			valid = false;
		}
	}

	private static void state8() {
		if (tokenIs('+')) {
			//shift 6
			push(token);
			push("6");
			setToken();
		} else if (tokenIs(')')) {
			//shift 11
			push(token);
			push("11");
			setToken();
		} else {
			valid = false;
		}
	}

	private static void state9() {
		if (tokenIs('+') || tokenIs(')') || tokenIs('$')) {
			//E->E+T
			pop();
			if (pop().charAt(0) != 'T') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != '+') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != 'E') {
				valid = false;
			}
			pushE();
		} else if (tokenIs('*')) {
			//shift 7
			push(token);
			push("7");
			setToken();
		} else
			valid = false;
	}

	private static void state10() {
		if (tokenIs('+') || tokenIs('*') || tokenIs(')') || tokenIs('$')) {
			//T->T*F
			pop();
			if (pop().charAt(0) != 'F') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != '*') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != 'T') {
				valid = false;
			}
			pushT();
		} else
			valid = false;
	}

	private static void state11() {
		if (tokenIs('+') || tokenIs('*') || tokenIs(')') || tokenIs('$')) {
			//F->(E)
			pop();
			if (pop().charAt(0) != ')') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != 'E') {
				valid = false;
			}
			pop();
			if (pop().charAt(0) != '(') {
				valid = false;
			}
			pushF();
		} else
			valid = false;
	}
}
