package me.jezza.lexij;

import java.io.IOException;
import java.io.InputStream;

import me.jezza.lexij.lexer.LexNavigator;
import me.jezza.lexij.lexer.LexReader;

/**
 * @author Jezza
 */
public final class LexiJ {

	public static void main(String[] args) throws IOException {
		InputStream input = LexiJ.class.getResourceAsStream("/LexiJ.lj");
		LexReader reader = new LexReader(input);
		long start = System.currentTimeMillis();
		LexNavigator navigator = reader.read();
		long end = System.currentTimeMillis();

		while (navigator.hasNext()) {
			navigator.next();
			System.out.println(navigator.typeString() + ": "+ navigator.asString());
		}
		System.out.println(end - start);
	}

}
