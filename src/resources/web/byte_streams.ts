const textEncoder = new TextEncoder();
const textDecoder = new TextDecoder();

class ByteArrayWriter {
	private rawData = new ArrayBuffer(32);
	private data = new DataView(this.rawData);

	private index = 0;

	constructor() {

	}

	private ensureCapacity(capacity: number) {
		if (capacity > this.rawData.byteLength) {
			let oldRaw = this.rawData;

			this.rawData = new ArrayBuffer(this.rawData.byteLength << 1);

			let oldBytes = new Uint8Array(oldRaw);
			let newBytes = new Uint8Array(this.rawData);

			for (let i = 0; i < oldBytes.length; i++) {
				newBytes[i] = oldBytes[i];
			}

			this.data = new DataView(this.rawData);
		}
	}

	public writeBoolean(b: boolean) {
		this.ensureCapacity(this.index + 1);
		this.data.setInt8(this.index, b ? 1 : 0);
		this.index++;
	}

	public writeByte(b: number) {
		this.ensureCapacity(this.index + 1);
		this.data.setInt8(this.index, b);
		this.index++;
	}

	public writeShort(s: number) {
		this.ensureCapacity(this.index + 2);
		this.data.setInt16(this.index, s);
		this.index += 2;
	}

	public writeint(i: number) {
		this.ensureCapacity(this.index + 4);
		this.data.setInt32(this.index, i);
		this.index += 4;
	}

	public writeDouble(d: number) {
		this.ensureCapacity(this.index + 8);
		this.data.setFloat64(this.index, d);
		this.index += 8;
	}

	public writeString(str: string) {
		let bytes = textEncoder.encode(str);

		this.ensureCapacity(bytes.length + 4);

		this.data.setInt32(this.index, bytes.length);
		this.index += 4;

		for (let i = 0; i < bytes.length; i++) {
			this.data.setUint8(this.index, bytes[i]);
			this.index++;
		}
	}

	public toArrayBuffer() {
		let out = new ArrayBuffer(this.index);

		let outBytes = new Uint8Array(out);
		let inBytes = new Uint8Array(this.rawData);

		for (let i = 0; i < outBytes.length; i++) {
			outBytes[i] = inBytes[i];
		}

		return out;
	}
}

class ByteArrayReader {
	private data: DataView;

	private index = 0;

	constructor(buffer: ArrayBuffer) {
		this.data = new DataView(buffer);
	}

	public readBoolean() {
		let value = this.data.getInt8(this.index);
		this.index++;
		return value != 0;
	}

	public readByte() {
		let value = this.data.getInt8(this.index);
		this.index++;
		return value;
	}

	public readShort() {
		let value = this.data.getInt16(this.index);
		this.index += 2;
		return value;
	}

	public readInt() {
		let value = this.data.getInt32(this.index);
		this.index += 4;
		return value;
	}

	public readDouble() {
		let value = this.data.getFloat64(this.index);
		this.index += 8;
		return value;
	}

	public readString() {
		let length = this.data.getInt32(this.index);
		this.index += 4;

		let byteString = new Uint8Array(length);

		for (let i = 0; i < length; i++) {
			byteString[i] = this.data.getUint8(this.index);
			this.index++;
		}

		return textDecoder.decode(byteString);
	}
}
