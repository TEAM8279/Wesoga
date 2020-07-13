package wesoga;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocketFactory;

public class WebServer {
	private static final MessageDigest SHA1;

	static {
		try {
			SHA1 = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static final int PORT = 6160;

	private static ServerSocket serverSocket = null;

	public static void start() {
		new Thread() {
			@Override
			public void run() {
				if (serverSocket != null) {
					throw new RuntimeException("The server can't be started twice");
				}

				try {
					ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
					serverSocket = ssf.createServerSocket(PORT);

					System.out.println("server started");
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					while (true) {
						Socket client = serverSocket.accept();

						handleRequest(client);
					}
				} catch (IOException e) {
					// Ignore
				}
			}
		}.start();
	}

	public static void stop() {
		if (serverSocket == null) {
			throw new RuntimeException("The server isn't started, you can't stop it");
		} else {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static synchronized void handleRequest(final Socket connect) {
		new Thread() {
			@Override
			public void run() {
				BufferedReader in = null;
				PrintWriter out = null;
				BufferedOutputStream dataOut = null;

				try {
					in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
					out = new PrintWriter(connect.getOutputStream());
					dataOut = new BufferedOutputStream(connect.getOutputStream());

					String request = in.readLine();

					String webSocketKey = null;

					while (true) {
						String line = in.readLine();

						if (line == null || line.isEmpty()) {
							break;
						}

						if (line.startsWith("Sec-WebSocket-Key: ")) {
							webSocketKey = line.split(" ")[1];
						}
					}

					StringTokenizer parse = new StringTokenizer(request);
					String method = parse.nextToken();
					String fileRequested = parse.nextToken();

					if (method.equalsIgnoreCase("get")) {
						if (webSocketKey == null) {
							byte[] fileData = WebResources.getResource(fileRequested);

							if (fileData == null) {
								out.println("HTTP/1.1 404 File Not Found");
								out.println();
								out.flush();
							} else {
								out.println("HTTP/1.1 200 OK");
								out.println();
								out.flush();

								dataOut.write(fileData);
								dataOut.flush();
							}
						} else {
							String hash = Base64.getEncoder()
									.encodeToString(SHA1.digest((webSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
											.getBytes(StandardCharsets.UTF_8)));
							out.println("HTTP/1.1 101 Switching Protocols");
							out.println("Upgrade: websocket");
							out.println("Connection: Upgrade");
							out.println("Sec-WebSocket-Accept: " + hash);
							out.println();
							out.flush();

							Server.addClient(new Client(new WebSocket(connect)));

							// Return to prevent closing
							return;
						}
					} else {
						out.println("HTTP/1.1 501 Not Implemented");
						out.println();
						out.flush();
					}
				} catch (SSLHandshakeException e) {
					// Ignore
				} catch (SSLException e) {
					// Ignore
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					connect.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error closing stream");
				}
			}
		}.start();
	}
}
