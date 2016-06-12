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
			if (isNumber(c)) {
				pos = consumeNumber();
				continue;
			}
			switch (c) {
				case '"':
					pos = consumeString();
					break;
				case '\'':
					pos = consumeChar();
					break;
				case NEW_LINE:
					elementData(ElementTypes.NEW_LINE);
					break;
				case ' ':
					pos = consumeWhitespace();
					break;
				//
				case '{':
					elementData(LEFT_BRACE);
					break;
				case '}':
					elementData(RIGHT_BRACE);
					break;
				case '(':
					elementData(LEFT_PARENTHESIS);
					break;
				case ')':
					elementData(RIGHT_PARENTHESIS);
					break;
				case '[':
					elementData(LEFT_BRACKET);
					break;
				case ']':
					elementData(RIGHT_BRACKET);
					break;
				//
				case ':':
					if (peek('=')) {
						elementData(COLON_EQUAL, pos, 2);
						pos += 2;
					} else {
						throw error("Syntax");
					}
					break;
				case '<':
					if (peek('-')) {
						elementData(LEFT_ARROW, pos, 2);
						pos += 2;
					} else {
						throw error("Syntax");
					}
					break;
				case '-':
					if (peek('>')) {
						elementData(RIGHT_ARROW, pos, 2);
						pos += 2;
					} else {
						elementData(DASH);
					}
					break;
				case ',':
					elementData(COMMA);
					break;
				//
				case '/':
					pos = consumeComment();
					break;
				//
				case '|':
					elementData(PIPE);
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
		while (++tempPos < length && isNamespace(dataBuffer[tempPos])) {
		}
		elementData(NAMESPACE, pos, tempPos - pos);
		return tempPos;
	}

	private boolean isNumber(char c) {
		return Character.isDigit(c);
	}

	private int consumeNumber() {
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		int tempPos = pos;
		while (++tempPos < length && isNumber(dataBuffer[tempPos])) {
		}
		elementData(NUMBER, pos, tempPos - pos);
		return tempPos;
	}

	private int consumeString() {
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		int tempPos = pos;
		while (++tempPos < length) {
			if (dataBuffer[tempPos] == '"' && dataBuffer[tempPos - 1] != '\\') {
				elementData(STRING, pos, tempPos - pos + 1);
				return tempPos + 1;
			}
		}
		throw error("Unexpected end of String");
	}

	private int consumeChar() {
		if (peek('\'')) {
			throw error("EmptyChar");
		} else if (peek('\\')) {
			// '\''
			if (peek(3, '\'')) {
				elementData(CHAR, pos, 4);
				return pos + 4;
			} else {
				throw error("InvalidEscapedChar");
			}
		}
		// 'a'
		if (peek(2, '\'')) {
			elementData(CHAR, pos, 3);
			return pos + 3;
		}
		throw error("InvalidChar");
	}

	private int consumeWhitespace() {
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		int tempPos = pos;
		while (++tempPos < length && dataBuffer[tempPos] == ' ') {
		}
		elementData(WHITESPACE, pos, tempPos - pos);
		return tempPos;
	}

	private int consumeComment() {
		if (!peek('/'))
			throw error("Comment shit");
		int tempPos = pos + 1;
		char[] dataBuffer = this.dataBuffer;
		int length = dataBuffer.length;
		while (++tempPos < length && dataBuffer[tempPos] != NEW_LINE) {
		}
		elementData(COMMENT, pos, tempPos - pos);
		return tempPos;
	}

	// Useful functions

	private boolean peek(char c) {
		return peekRaw(pos + 1, c);
	}

	private boolean peek(int offset, char c) {
		return peekRaw(pos + offset, c);
	}

	private boolean peekRaw(int pos, char c) {
		return pos < dataBuffer.length && dataBuffer[pos] == c;
	}

	private boolean peek(String c) {
		return peekRaw(pos + 1, c);
	}

	private boolean peek(int offset, String c) {
		return peekRaw(pos + offset, c);
	}

	private boolean peekRaw(int pos, String c) {
		char[] dataBuffer = this.dataBuffer;
		int length = c.length();
		if (pos + length < dataBuffer.length) {
			for (int i = 0; i < length; i++)
				if (c.charAt(i) != dataBuffer[pos + i])
					return false;
			return true;
		}
		return false;
	}

	private void elementData(final byte type) {
		elementData(type, pos, 1);
	}

	private void elementData(final byte type, final int position, final int length) {
		int index = elementIndex++;
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
