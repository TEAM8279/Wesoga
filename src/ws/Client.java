package ws;

import java.util.ArrayList;

import ws.baseMod.entities.Player;
import ws.entities.Entities;
import ws.entities.Entity;
import ws.entities.EntityModel;
import ws.textures.Textures;
import ws.tiles.Tiles;

public class Client {
	private int viewDistance = 5;

	public final WebSocket socket;

	public final Player p = new Player(1, 1);

	public int accelX = 0;
	public int accelY = 0;

	public double rotation = 0;

	public Client(WebSocket socket) {
		this.socket = socket;

		World.addEntity(p);

		sendTextures();
		sendWorld();
		sendEntityModels();
		sendViewDistance();

		sendReady();
	}

	private void sendTextures() {
		socket.write(DataID.TEXTURES + ";" + Textures.count());
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

	public void sendEntityModels() {
		StringBuilder builder = new StringBuilder(DataID.ENTITY_MODELS + ";" + Entities.count());

		for (int i = 0; i < Entities.count(); i++) {
			EntityModel model = Entities.getModel(i);

			builder.append(";");
			builder.append(Entities.textureID(model));
			builder.append(";");
			builder.append(model.getSize());
		}

		socket.write(builder.toString());
	}

	public void sendEntities() {
		ArrayList<Entity> selected = World.getVisibleEntities(p);

		StringBuilder builder = new StringBuilder(DataID.ENTITIES + ";" + selected.size());

		for (Entity e : selected) {
			builder.append(";");
			builder.append(e.hashCode());
			builder.append(";");
			builder.append(e.getModelID());
			builder.append(";");
			builder.append(e.getX());
			builder.append(";");
			builder.append(e.getY());
			builder.append(";");
			builder.append(e.getRotation());
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

			if (DataID.MOVE.same(parts[0])) {
				accelX = Integer.parseInt(parts[1]);
				accelY = Integer.parseInt(parts[2]);
			} else if (DataID.ROTATION.same(parts[0])) {
				p.setRotation(Double.parseDouble(parts[1]));
			} else if (DataID.ZOOM.same(parts[0])) {
				viewDistance++;

				if (viewDistance > 50) {
					viewDistance = 50;
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
