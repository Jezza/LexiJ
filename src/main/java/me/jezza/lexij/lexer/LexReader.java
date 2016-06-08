package me.jezza.lexij.lexer;

import static me.jezza.lexij.lexer.ElementTypes.*;
import static me.jezza.lexij.lib.Strings.format;

import java.io.*;
import java.util.Objects;

import me.jezza.lexij.interfaces.Input;
import me.jezza.lexij.lib.IO;

/**
 * @author Jezza
 */
public final class LexReader {
	public static final char NEW_LINE = '\n';

	private final Input in;

	private ElementBuffer elementBuffer;
	private char[] dataBuffer;

	private int elementIndex = 0;
	private int pos = 0;

	public LexReader(final String string) {
		this(() -> string);
	}

	public LexReader(final File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public LexReader(final Reader in) {
		this(() -> IO.toString(in));
	}

	public LexReader(final InputStream in) {
		this(() -> IO.toString(in));
	}

	public LexReader(final Input in) {
		this.in = Objects.requireNonNull(in, "Input is null.");
	}

	public LexNavigator read() throws IOException {
		char[] dataBuffer = this.dataBuffer = in.read().toCharArray();
		int length = dataBuffer.length;
		elementBuffer = new ElementBuffer(length);
		int expected;
		for (pos = 0; pos < length; pos = pos == expected ? pos + 1 : pos) {
			final char c = dataBuffer[pos];
			expected = pos;
			if (isNamespace(c)) {
				pos = consumeNamespace();
				continue;
			}
			if (isDigit(c)) {
				pos = consumeNumber();
				continue;
			}
			switch (c) {
				case '"':
					pos = consumeString();
					break;
				//
				case '{':
					elementData(elementIndex++, BRACE_L, pos);
					break;
				case '}':
					elementData(elementIndex++, BRACE_R, pos);
					break;
				case '(':
					elementData(elementIndex++, PARENTHESIS_L, pos);
					break;
				case ')':
					elementData(elementIndex++, PARENTHESIS_R, pos);
					break;
				case '[':
					elementData(elementIndex++, BRACKET_L, pos);
					break;
				case ']':
					elementData(elementIndex++, BRACKET_R, pos);
					break;
				//
				case ':':
					if (peek('=')) {
						elementData(elementIndex++, COLON_EQUAL, pos, 2);
					} else {
						throw error("ColonEqual shit");
					}
					break;
				//
				case '/':
					pos = skipComment();
					break;
				//
				case '|':
					elementData(elementIndex++, PIPE, pos);
					break;
				default:
			}
		}

		elementBuffer.clip(elementIndex);
		return new LexNavigator(dataBuffer, elementBuffer);
	}

	private boolean isNamespace(char c) {
		return Character.isAlphabetic(c) || c == '_';
	}

	private int consumeNamespace() {
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		int tempPos = pos;
		while (++tempPos < length) {
			if (!isNamespace(dataBuffer[tempPos])) {
				elementData(elementIndex++, NAMESPACE, pos, tempPos - pos);
				return tempPos;
			}
		}
		elementData(elementIndex++, NAMESPACE, pos, tempPos - pos);
		return tempPos;
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private int consumeNumber() {
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		int tempPos = pos;
		while (++tempPos < length) {
			if (!Character.isDigit(dataBuffer[tempPos])) {
				elementData(elementIndex++, NUMBER, pos, tempPos - pos);
				return tempPos;
			}
		}
		elementData(elementIndex++, NUMBER, pos, tempPos - pos);
		return tempPos;
	}

	private int consumeString() {
		char[] dataBuffer = this.dataBuffer;
		int tempPos = pos;
		while (true) {
			if (++tempPos >= dataBuffer.length)
				throw error("Unexpected end of String");
			switch (dataBuffer[tempPos]) {
				case '\'':
				case '"':
					if (dataBuffer[tempPos - 1] != '\\') {
						elementData(elementIndex++, STRING, pos, tempPos - pos + 1);
						return tempPos + 1;
					}
					break;
				default:
			}
		}
	}

	private int skipComment() {
		if (peek('/')) {
			int tempPos = pos + 1;
			char[] dataBuffer = this.dataBuffer;
			int length = dataBuffer.length;
			while (++tempPos < length) {
				if (dataBuffer[tempPos] == NEW_LINE) {
					elementData(elementIndex++, COMMENT, pos, tempPos - pos);
					return tempPos + 1;
				}
			}
			elementData(elementIndex++, COMMENT, pos, tempPos - pos);
			return tempPos;
		}
		throw error("Comment shit");
	}

	// Useful functions

	private boolean peek(char c) {
		return peekRaw(pos + 1, c);
	}

	private boolean peek(int offset, char c) {
		return peekRaw(pos + offset, c);
	}

	private boolean peekRaw(int position, char c) {
		return position < dataBuffer.length && dataBuffer[position] == c;
	}

	private boolean peek(String c) {
		return peekRaw(pos + 1, c);
	}

	private boolean peek(int offset, String c) {
		return peekRaw(pos + offset, c);
	}

	private boolean peekRaw(int position, String c) {
		char[] dataBuffer = this.dataBuffer;
		int length = c.length();
		if (position + length < dataBuffer.length) {
			for (int i = 0; i < length; i++)
				if (c.charAt(i) != dataBuffer[position + i])
					return false;
			return true;
		}
		return false;
	}

	private void elementData(final int index, final byte type, final int position) {
		elementData(index, type, position, 1);
	}

	private void elementData(final int index, final byte type, final int position, final int length) {
		ElementBuffer elementBuffer = this.elementBuffer;
		elementBuffer.type[index] = type;
		elementBuffer.position[index] = position;
		elementBuffer.length[index] = length;
	}

	private RuntimeException error(final String message) {
		throw new RuntimeException(message + ':' + cursorPosition());
	}

	private RuntimeException error(final String message, final Throwable cause) {
		throw new RuntimeException(message + ':' + cursorPosition(), cause);
	}

	private RuntimeException error(final String message, final int position) {
		throw new RuntimeException(message + ':' + cursorPosition(position));
	}

	private RuntimeException error(final String message, final int position, final Throwable cause) {
		throw new RuntimeException(message + ':' + cursorPosition(position), cause);
	}

	private String cursorPosition() {
		return cursorPosition(pos);
	}

	private String cursorPosition(final int position) {
		int line = 1;
		int index = 0;
		for (int i = 0; i < position; i++) {
			if (dataBuffer[i] == NEW_LINE) {
				line++;
				index = 0;
			} else {
				index++;
			}
		}
		return format("Line #{}, Char #{}", Integer.toString(line), Integer.toString(index));
	}
}
