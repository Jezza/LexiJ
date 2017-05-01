package me.jezza.lexij.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;

import me.jezza.lexij.lang.base.AbstractLexer;
import me.jezza.lexij.lang.interfaces.Lexer;

/**
 * @author Jezza
 */
public final class LexiJLexer extends AbstractLexer implements Lexer {
	private static final int DEFAULT_LOOKAHEAD = 5_000;
//	private static final int DEFAULT_LOOKAHEAD = 1;

	private final StringBuilder text;

	{
		text = new StringBuilder(32);
	}

	public LexiJLexer(String input) {
		super(input, DEFAULT_LOOKAHEAD);
	}

	public LexiJLexer(File file) throws FileNotFoundException {
		super(file, DEFAULT_LOOKAHEAD);
	}

	public LexiJLexer(InputStream in) {
		super(in, DEFAULT_LOOKAHEAD);
	}

	public LexiJLexer(Reader in) {
		super(in, DEFAULT_LOOKAHEAD);
	}

	@Override
	public Token next() throws IOException {
		int c;
		int[] pos = this.pos.clone();
		while ((c = advance()) != EOS) {
			if (Character.isWhitespace(c)) {
				while (Character.isWhitespace(peek()))
					advance();
				pos = this.pos.clone();
				continue;
		}
			if (Character.isJavaIdentifierStart(c)) {
				StringBuilder text = this.text;
				if (text.length() > 0)
					text.setLength(0);
				text.append((char) c);
				while (Character.isJavaIdentifierPart(peek()))
					text.append((char) advance());
				return token(Tokens.NAMESPACE, text.toString(), pos);
			}
			if (c == '"') {
				return readString(pos);
			} else if (c == '\'') {
				Token charToken = readChar(pos);
				if (advance() != '\'') {
					throw new IllegalStateException("Too many characters in char literal @ " + Arrays.toString(pos));
				}
				return charToken;
			} else if (c == '/') {
				c = advance();
				if (c == '/') {
					// Line comment, scan until EOS or EOL
					while ((c = advance()) != EOS && c != '\n') ;
					pos = this.pos.clone();
					continue;
				} else if (c == '*') {
					skipBlockComment(pos);
					pos = this.pos.clone();
					continue;
				}
			}

			return token(c, c, pos);
		}
		return Token.EOS;
	}

	private static void error() {
	}

	private Token readChar(int[] pos) throws IOException {
		int c = advance();
		if (c != '\\')
			return token(Tokens.CHAR, c, pos);
		switch (c = advance()) {
			case 'b':
				return token(Tokens.CHAR, '\b', pos);
			case 'f':
				return token(Tokens.CHAR, '\f', pos);
			case 'n':
				return token(Tokens.CHAR, '\n', pos);
			case 'r':
				return token(Tokens.CHAR, '\r', pos);
			case 't':
				return token(Tokens.CHAR, '\t', pos);
			case '"':
				return token(Tokens.CHAR, c, pos);
			case '\\':
				return token(Tokens.CHAR, c, pos);
			case '0':
				return token(Tokens.CHAR, '\0', pos);
			case 'u':
				int code = (readHexDigit(advance()) << 4) | readHexDigit(advance());
				return token(Tokens.CHAR, String.valueOf(Character.toChars(code)), pos);
			default:
				throw new IllegalStateException("Unknown string escape: " + ((char) c));
		}
	}

	private Token readString(int[] pos) throws IOException {
		StringBuilder text = this.text;
		if (text.length() > 0)
			text.setLength(0);
		int c;
		while ((c = advance()) != EOS) {
			switch (c) {
				case '\\':
					switch (advance()) {
						case 'b':
							text.append('\b');
							continue;
						case 'f':
							text.append('\f');
							continue;
						case 'n':
							text.append('\n');
							continue;
						case 'r':
							text.append('\r');
							continue;
						case 't':
							text.append('\t');
							continue;
						case '"':
							text.append('"');
							continue;
						case '\\':
							text.append('\\');
							continue;
						case '0':
							text.append('\0');
							continue;
						case 'u':
							int code = (readHexDigit(advance()) << 4) | readHexDigit(advance());
							text.append(Character.toChars(code));
							continue;
						default:
							throw new IllegalStateException("Unknown string escape");
					}
				case '\n':
					throw new IllegalArgumentException("Illegal line end on string literal.");
				case '"':
					return token(Tokens.STRING, text.toString(), pos);
				default:
					text.append((char) c);
			}
		}
		throw new IllegalArgumentException("Illegal file end on string literal.");
	}

	private static int readHexDigit(int c) {
		switch (c) {
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'a':
			case 'A':
				return 10;
			case 'b':
			case 'B':
				return 11;
			case 'c':
			case 'C':
				return 12;
			case 'd':
			case 'D':
				return 13;
			case 'e':
			case 'E':
				return 14;
			case 'f':
			case 'F':
				return 15;
			default:
				throw new IllegalStateException("Invalid hex char: '" + c + '\'');
		}
	}

	private void skipBlockComment(int[] pos) throws IOException {
		int level = 1;
		int c;
		while ((c = advance()) != EOS) {
			if (c == '/') {
				c = advance();
				if (c == '*')
					level++;
			} else if (c == '*') {
				c = advance();
				if (c == '/' && --level == 0)
					return;
			}
		}
		throw new IllegalStateException("Illegal file end on block comment. (Comment blocks aren't balanced. Starting position: " + Arrays.toString(pos) + ')');
	}

	protected static Token token(int type, int c, int[] pos) {
		return token(type, String.valueOf((char) c), pos);
	}

	protected static Token token(int type, String text, int[] pos) {
		return new Token(type, text, pos[0], pos[1]);
	}
}
