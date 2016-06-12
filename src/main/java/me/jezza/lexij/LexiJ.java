package me.jezza.lexij;

import java.io.IOException;
import java.io.InputStream;

import me.jezza.lexij.lexer.ElementTypes;
import me.jezza.lexij.lexer.LexNavigator;
import me.jezza.lexij.lexer.LexReader;

/**
 * @author Jezza
 */
public final class LexiJ {

	public static final String FILE_EXTENSION = "lx";

	public static void main(String[] args) throws IOException {
		InputStream input = LexiJ.class.getResourceAsStream("/LexiJ.lx");
		LexReader reader = new LexReader(input);
		LexNavigator navigator = reader.read();

		StringBuilder output = new StringBuilder();
		StringBuilder tokens = new StringBuilder();

		while (navigator.hasNext()) {
			navigator.next();
			tokens.append(navigator.typeString()).append(' ');
			if (navigator.type() == ElementTypes.NEW_LINE) {
				System.out.println(output);
				tokens.deleteCharAt(tokens.length() - 1);
				System.out.println(tokens);
				System.out.println();
				output.setLength(0);
				tokens.setLength(0);
			} else {
				output.append(navigator.asString());
			}
		}
		System.out.println(output);
		System.out.println(tokens);
	}

}
