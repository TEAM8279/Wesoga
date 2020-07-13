package wesoga.util;

import java.nio.charset.StandardCharsets;

public class ByteArrayReader {
	private byte[] data;
	private int index = 0;

	public ByteArrayReader(byte[] data) {
		this.data = data;
	}

	public final boolean readBoolean() {
		byte value = data[index];
		index++;

		return (value != 0);
	}

	public final byte readByte() {
		byte value = data[index];
		index++;

		return value;
	}

	public final short readShort() {
		int b1 = data[index] & 255;
		index++;
		int b2 = data[index] & 255;
		index++;

		return (short) ((b1 << 8) + (b2 << 0));
	}

	public final int readInt() {
		int b1 = data[index] & 255;
		index++;
		int b2 = data[index] & 255;
		index++;
		int b3 = data[index] & 255;
		index++;
		int b4 = data[index] & 255;
		index++;

		return (b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0);
	}

	private final long readLong() {
		long b1 = data[index] & 255;
		index++;
		long b2 = data[index] & 255;
		index++;
		long b3 = data[index] & 255;
		index++;
		long b4 = data[index] & 255;
		index++;
		long b5 = data[index] & 255;
		index++;
		long b6 = data[index] & 255;
		index++;
		long b7 = data[index] & 255;
		index++;
		long b8 = data[index] & 255;
		index++;

		return (b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24) + (b6 << 16) + (b7 << 8) + (b8 << 0);
	}

	public final double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public final String readString() {
		int length = readInt();

		byte[] bytes = new byte[length];

		for (int i = 0; i < length; i++) {
			bytes[i] = readByte();
		}

		return new String(bytes, StandardCharsets.UTF_8);
	}
}
