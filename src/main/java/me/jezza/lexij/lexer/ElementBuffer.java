package me.jezza.lexij.lexer;

import java.util.Arrays;

/**
 * @author jezza
 * @date 4/12/2015
 */
final class ElementBuffer {
	protected int[] position;
	protected int[] length;
	protected byte[] type;
	protected int size = 0;

	ElementBuffer(final int capacity) {
		position = new int[capacity];
		length = new int[capacity];
		type = new byte[capacity];
	}

	void clip(int size) {
		this.size = size;
		position = Arrays.copyOf(position, size);
		length = Arrays.copyOf(length, size);
		type = Arrays.copyOf(type, size);
	}
}
