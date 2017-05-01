package me.jezza.lexij.lang;

/**
 * @author Jezza
 */
public final class Tokens {
	public static final int EOS = -1;

	public static final int NAMESPACE = -2;
	public static final int STRING = -3;
	public static final int CHAR = -4;

	private Tokens() {
		throw new IllegalStateException();
	}

	public static String name(int type) {
		switch (type) {
			case EOS:
				return "EOS";
			case NAMESPACE:
				return "NAMESPACE";
			case STRING:
				return "STRING";
			case CHAR:
				return "CHAR";
			default:
				return "'" + (char) type + '\'';
		}
	}
}
