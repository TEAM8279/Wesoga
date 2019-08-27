package ws;

import java.util.ArrayList;
import java.util.List;

import ws.tiles.Tiles;

public class Client {
	private int viewDistance = 5;

	public final WebSocket socket;

	public final Player p = new Player(1, 1);

	public int accelX = 0;
	public int accelY = 0;

	public Client(WebSocket socket) {
		this.socket = socket;

		World.entities.add(p);

		sendTilesTextures();
		sendWorld();
		sendViewDistance();
		sendReady();
	}

	private void sendTilesTextures() {
		socket.write(DataID.TILES_TEXTURES + ";" + Tiles.texturesCount());
	}

	private void sendWorld() {
		StringBuilder builder = new StringBuilder(DataID.WORLD + ";" + World.SIZE);

		for (int y = 0; y < World.SIZE; y++) {
			for (int x = 0; x < World.SIZE; x++) {
				builder.append(";");
				builder.append(Tiles.textureID(World.getTile(x, y)));
			}
		}

		socket.write(builder.toString());
	}

	private void sendReady() {
		socket.write(DataID.READY.toString());
	}

	public void sendEntities(List<Entity> entities) {
		ArrayList<Entity> selected = new ArrayList<>();

		for (Entity e : entities) {
			if (e != p) {
				selected.add(e);
			}
		}

		StringBuilder builder = new StringBuilder(DataID.ENTITIES + ";" + selected.size());

		for (Entity e : selected) {
			builder.append(";");
			builder.append(e.getID());
			builder.append(";");
			builder.append(e.getX());
			builder.append(";");
			builder.append(e.getY());
		}

		socket.write(builder.toString());
	}

	public void sendPosition() {
		socket.write(DataID.POSITION + ";" + p.getX() + ";" + p.getY());
	}

	public void sendViewDistance() {
		socket.write(DataID.VIEW_DIST + ";" + viewDistance);
	}

	public Player getPlayer() {
		return p;
	}

	public void readMessages() {
		if (accelX == 1) {
			p.accelX(0.01);
		} else if (accelX == -1) {
			p.accelX(-0.01);
		}

		if (accelY == 1) {
			p.accelY(0.01);
		} else if (accelY == -1) {
			p.accelY(-0.01);
		}

		while (true) {
			String msg = socket.read();

			if (msg == null) {
				break;
			}

			String[] parts = msg.split(";");

			if (DataID.ACCEL.same(parts[0])) {
				accelX = Integer.parseInt(parts[1]);
				accelY = Integer.parseInt(parts[2]);
			} else if (DataID.ZOOM.same(parts[0])) {
				viewDistance++;

				if (viewDistance > 22) {
					viewDistance = 22;
				}

				sendViewDistance();
			} else if (DataID.UNZOOM.same(parts[0])) {
				viewDistance--;

				if (viewDistance < 3) {
					viewDistance = 3;
				}

				sendViewDistance();
			} else {
				System.err.println("Unknown data id : " + parts[0]);
				socket.close();
			}
		}
	}
}
