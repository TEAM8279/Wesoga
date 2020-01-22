package wesoga;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocket {
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	private boolean open = true;

	private static final int MAX_QUEUE_SIZE = 100;

	private LinkedBlockingQueue<String> sendQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
	private LinkedBlockingQueue<String> receiveQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

	private final Thread receivingThread;
	private final Thread sendingThread;

	public WebSocket(Socket socket) {
		System.out.println("New client connected");

		this.socket = socket;
		try {
			this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			close();
		}

		receivingThread = new Thread() {
			@Override
			public void run() {
				try {
					while (open) {
						int b1 = input.read();

						boolean end = (b1 & 0b10000000) > 0;

						if (!end) {
							System.err.println("Non ending frame");
							close();
							break;
						}

						int opcode = b1 & 0b01111111;

						if (opcode == 8) {
							System.out.println("Client disconnected");
							close();
							break;
						}

						if (opcode != 1) {
							System.err.println("Unsupported opcode : " + opcode);
							close();
							break;
						}

						int b2 = input.read();

						boolean masked = (b2 & 0b10000000) > 0;

						if (!masked) {
							System.err.println("The message isn't masked");
							close();
							break;
						}

						int count = b2 & 0b01111111;

						if (count == 126) {
							count = input.readUnsignedShort();
						} else if (count == 127) {
							long length = input.readLong();

							if (length > Integer.MAX_VALUE) {
								System.err.println("The message is too long");
								close();
								break;
							}

							count = (int) length;
						}

						byte[] key = new byte[4];

						for (int i = 0; i < 4; i++) {
							key[i] = input.readByte();
						}

						byte[] decoded = new byte[count];

						for (int i = 0; i < count; i++) {
							decoded[i] = (byte) (input.read() ^ key[i & 0b11]);
						}

						if (!receiveQueue.offer(new String(decoded, StandardCharsets.UTF_8))) {
							System.err.println("The queue is full kicking client");
							close();
							break;
						}
					}

				} catch (IOException e) {
					close();
				}
			}
		};
		receivingThread.start();

		sendingThread = new Thread() {
			@Override
			public void run() {
				try {
					while (open) {
						byte[] msg = sendQueue.take().getBytes(StandardCharsets.UTF_8);

						output.write(0b10000001);

						if (msg.length <= 125) {
							output.write(msg.length);
						} else if (msg.length <= 32767) {
							output.write(126);
							output.writeShort(msg.length);
						} else {
							output.write(127);
							output.writeLong(msg.length);
						}

						output.write(msg);

						output.flush();
					}
				} catch (InterruptedException | IOException e) {
					close();
				}
			}
		};
		sendingThread.start();
	}

	public void close() {
		if (open) {
			open = false;
			try {
				socket.close();
			} catch (IOException e) {

			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {

				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {

				}
			}

			sendingThread.interrupt();
			receivingThread.interrupt();
		}
	}

	public String read() {
		return receiveQueue.poll();
	}

	public void write(String msg) {
		if (!sendQueue.offer(msg)) {
			close();
		}
	}

	public boolean isOpen() {
		return open;
	}
}
