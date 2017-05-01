package me.jezza.lexij;

import java.io.IOException;

import me.jezza.lexij.lang.LexiJLexer;
import me.jezza.lexij.lang.Token;
import me.jezza.lexij.lang.Tokens;
import me.jezza.lexij.lang.interfaces.Lexer;

/**
 * @author Jezza
 */
public final class LexiJ {
	public static final String EXT = "lx";

	public static void main(String[] args) throws IOException {
		Lexer lexer = new LexiJLexer(LexiJ.class.getResourceAsStream("/dawn.lx"));

		Token token;
		int count = 0;
		long start = System.nanoTime();
		while ((token = lexer.next()).type != Tokens.EOS) {
			System.out.println(token);
			count++;
		}
		long end = System.nanoTime();
		System.out.println(count);
		System.out.println(end - start);
	}
}
