package ws;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
	private static final int TPS = 100;
	private static final long INTERVAL = 1_000_000_000L / TPS;

	private static List<Client> clients = new ArrayList<>();

	private static final ScheduledExecutorService gameLoop = Executors.newSingleThreadScheduledExecutor();

	public static void start() {

		gameLoop.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				tick();
			}
		}, 0, INTERVAL, TimeUnit.NANOSECONDS);
	}

	private static synchronized void tick() {
		try {
			for (int i = clients.size() - 1; i >= 0; i--) {
				if (!clients.get(i).socket.isOpen()) {
					World.removeEntity(clients.get(i).player);
					clients.remove(i);
				}
			}

			for (Client c : clients) {
				c.readMessages();
			}

			World.tick();

			for (Client c : clients) {
				c.sendHP();
				c.sendPosition();
				c.sendEntities();
				c.sendLoad();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void addClient(Client client) {
		clients.add(client);
	}

	public static void stop() {
		gameLoop.shutdown();

		for (Client c : clients) {
			c.socket.close();
		}
	}
}
