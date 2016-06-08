package me.jezza.lexij.interfaces;

import java.io.IOException;

/**
 * @author Jezza
 */
@FunctionalInterface
public interface Input {
	String read() throws IOException;
}
