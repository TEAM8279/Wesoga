package wesoga;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Server.start();
		WebServer.start();

		Scanner sc = new Scanner(System.in);
		while (true) {
			String in = sc.nextLine();

			if (in.equalsIgnoreCase("stop")) {
				break;
			}
		}

		sc.close();

		WebServer.stop();
		Server.stop();
	}
}
