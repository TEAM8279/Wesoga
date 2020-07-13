package wesoga.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ByteArrayWriter {
	private byte[] data = new byte[32];
	private int index = 0;

	public ByteArrayWriter() {

	}

	private void ensureCapacity(int capacity) {
		if (capacity > data.length) {
			data = Arrays.copyOf(data, data.length << 1);
		}
	}

	public final void writeBoolean(boolean v) {
		ensureCapacity(index + 1);
		data[index] = v ? (byte) 1 : (byte) 0;
		index++;
	}

	public final void writeByte(byte v) {
		ensureCapacity(index + 1);
		data[index] = v;
		index++;
	}

	public final void writeShort(short v) {
		ensureCapacity(index + 2);
		data[index] = (byte) (v >>> 8);
		index++;
		data[index] = (byte) (v >>> 0);
		index++;
	}

	public final void writeInt(int v) {
		ensureCapacity(index + 4);
		data[index] = (byte) (v >>> 24);
		index++;
		data[index] = (byte) (v >>> 16);
		index++;
		data[index] = (byte) (v >>> 8);
		index++;
		data[index] = (byte) (v >>> 0);
		index++;
	}

	private final void writeLong(long v) {
		ensureCapacity(index + 8);
		data[index] = (byte) (v >>> 56);
		index++;
		data[index] = (byte) (v >>> 48);
		index++;
		data[index] = (byte) (v >>> 40);
		index++;
		data[index] = (byte) (v >>> 32);
		index++;
		data[index] = (byte) (v >>> 24);
		index++;
		data[index] = (byte) (v >>> 16);
		index++;
		data[index] = (byte) (v >>> 8);
		index++;
		data[index] = (byte) (v >>> 0);
		index++;
	}

	public final void writeDouble(double v) {
		writeLong(Double.doubleToLongBits(v));
	}

	public final void writeString(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

		writeInt(bytes.length);

		for (int i = 0; i < bytes.length; i++) {
			writeByte(bytes[i]);
		}
	}

	public final byte[] toByteArray() {
		return Arrays.copyOf(data, index);
	}
}
