package me.jezza.lexij.lang.interfaces;

import java.io.IOException;

import me.jezza.lexij.lang.Token;

/**
 * @author Jezza
 */
public interface Lexer {
	int EOS = -1;

	Token next() throws IOException;
}

