package me.jezza.lexij.lang.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * A very basic abstract lexer that provides a very good base to use.
 * (There are better and more efficient ways for Lexers that only need a lookahead of 1)
 *
 * @author Jezza
 */
public abstract class AbstractLexer {
	private static final int DEFAULT_LOOKAHEAD = 1;

	private static final int COLUMN_START = 1;
	private static final int ROW_START = 1;

	private static final int EOS = -1;

	private static final int UNINITIALISED = -1;

	private Reader in;

	private int index;

	private final int[] inputBuffer;
	private final char[] readBuffer;

	protected final int[] pos;

	protected AbstractLexer(final String input) {
		this(new StringReader(input), DEFAULT_LOOKAHEAD);
	}

	protected AbstractLexer(final String input, int lookahead) {
		this(new StringReader(input), lookahead);
	}

	protected AbstractLexer(final File file) throws FileNotFoundException {
		this(new FileReader(file), DEFAULT_LOOKAHEAD);
	}

	protected AbstractLexer(final File file, int lookahead) throws FileNotFoundException {
		this(new FileReader(file), lookahead);
	}

	protected AbstractLexer(final InputStream in) {
		this(new InputStreamReader(in), DEFAULT_LOOKAHEAD);
	}

	protected AbstractLexer(final InputStream in, int lookahead) {
		this(new InputStreamReader(in), lookahead);
	}

	protected AbstractLexer(final Reader in) {
		this(in, DEFAULT_LOOKAHEAD);
	}

	protected AbstractLexer(final Reader in, int lookahead) {
		if (in == null)
			throw new NullPointerException("Input cannot be null.");
		if (lookahead < 1)
			throw new IllegalArgumentException("Lookahead must be > 0");
		this.in = in;
		inputBuffer = new int[lookahead];
		readBuffer = new char[1];
		index = UNINITIALISED;
		pos = new int[2];
		pos[0] = COLUMN_START;
		pos[1] = ROW_START;
	}

//	private static final Times INIT = new Times("init", 1);

	private void init(int[] input) throws IOException {
//		long start = System.nanoTime();
		int l = input.length;
		char[] readBuffer = new char[l];
		int count = in.read(readBuffer, 0, l);
		if (count == EOS) {
			input[0] = EOS;
			in.close();
			in = null;
		} else if (count != l) {
			for (int i = 0; i < count; i++)
				input[i] = readBuffer[i];
			input[count] = EOS;
			in.close();
			in = null;
		} else {
			for (int i = 0; i < l; i++)
				input[i] = readBuffer[i];
		}
//		INIT.add(System.nanoTime() - start);
	}

//	private static final Times ADVANCE = new Times("advance", 4096);

	protected final int advance() throws IOException {
//		long start = System.nanoTime();
		int[] input = this.inputBuffer;
		int index = this.index;
		if (index == UNINITIALISED) {
			index = 0;
			init(input);
		}
		int c = input[index];
		if (in != null) {
			char[] readBuffer = this.readBuffer;
			if (in.read(readBuffer, 0, 1) != EOS) {
				input[index] = readBuffer[0];
			} else {
				input[index] = EOS;
				in.close();
				in = null;
			}
		} else if (c == EOS) {
//			ADVANCE.add(System.nanoTime() - start);
			return EOS;
		}
		if (c == '\n') {
			pos[0]++;
			pos[1] = ROW_START;
		} else if (c != '\r') {
			pos[1]++;
		}
		this.index = (index + 1) % input.length;
//		ADVANCE.add(System.nanoTime() - start);
		return c;
	}

	protected final String inputBuffer() {
		int[] input = this.inputBuffer;
		StringBuilder b = new StringBuilder(input.length + 4);
		b.append('[');
		for (int i = 0, l = input.length, p = index % l; i < l; i++) {
			int c = input[i];
			if (c != EOS) {
				if (i == p)
					b.append('(');
				if (Character.isWhitespace(c)) {
					b.append('{').append(c).append('}');
				} else {
					b.appendCodePoint(c);
				}
				if (i == p)
					b.append(')');
			} else
				b.append("{EOS}");
		}
		b.append(']');
		return b.toString();
	}

	protected final int peek() {
		return inputBuffer[index];
	}

	protected final int peek(int offset) {
		return peekRaw((index + offset) % inputBuffer.length);
	}

	protected final int peekRaw(int index) {
		return inputBuffer[index];
	}
}
