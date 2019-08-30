package ws;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
	private static final int TPS = 100;
	private static final long INTERVAL = 1_000_000_000L / TPS;

	public static List<Client> clients = new ArrayList<>();

	private static final ScheduledExecutorService gameLoop = Executors.newSingleThreadScheduledExecutor();

	public static void start() {

		gameLoop.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = clients.size() - 1; i >= 0; i--) {
						if (!clients.get(i).socket.isOpen()) {
							World.removeEntity(clients.get(i).p);
							clients.remove(i);
						}
					}

					for (Client c : clients) {
						c.readMessages();
					}

					World.tick();

					for (Client c : clients) {
						c.sendPosition();
						c.sendEntities();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, INTERVAL, TimeUnit.NANOSECONDS);
	}

	public static void stop() {
		gameLoop.shutdown();

		for (Client c : clients) {
			c.socket.close();
		}
	}
}
