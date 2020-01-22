package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.BaseMod;
import wesoga.baseMod.entities.Arrow;
import wesoga.baseMod.entities.Player;
import wesoga.entities.Entities;
import wesoga.entities.Entity;
import wesoga.entities.EntityModel;
import wesoga.textures.Textures;
import wesoga.tiles.Tiles;

public class Client {
	private int viewDistance = 5;

	private final WebSocket socket;

	private Player player = null;

	private String username = null;

	private int accelX = 0;
	private int accelY = 0;

	private boolean loading = false;

	private double load = 0;

	public Client(WebSocket socket) {
		this.socket = socket;

		sendGameData();
	}

	private void sendGameData() {
		StringBuilder builder = new StringBuilder(Integer.toString(Textures.count()));

		builder.append(";");
		builder.append(Entities.count());

		for (int i = 0; i < Entities.count(); i++) {
			EntityModel model = Entities.getModel(i);

			builder.append(";");
			builder.append(Entities.textureID(model));
			builder.append(";");
			builder.append(model.getSize());
		}

		builder.append(";");
		builder.append(World.SIZE);

		for (int y = 0; y < World.SIZE; y++) {
			for (int x = 0; x < World.SIZE; x++) {
				builder.append(";");
				builder.append(Tiles.textureID(World.getTile(x, y)));
			}
		}

		socket.write(builder.toString());
	}

	private void sendValidUsername() {
		socket.write(DataID.VALID_USERNAME.toString());
	}

	private void sendInvalidUsername() {
		socket.write(DataID.INVALID_USERNAME.toString());
	}

	private void sendDead() {
		socket.write(DataID.DEAD.toString());
	}

	public void playerDeath() {
		sendDead();
		player = null;
	}

	public boolean isConnected() {
		return socket.isOpen();
	}

	public void kick() {
		socket.close();
	}

	public void sendEntities() {
		ArrayList<Entity> selected = World.getVisibleEntities(player);

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
		socket.write(DataID.POSITION + ";" + player.getX() + ";" + player.getY());
	}

	public void sendViewDistance() {
		socket.write(DataID.VIEW_DIST + ";" + viewDistance);
	}

	public void sendHP() {
		socket.write(DataID.HEALTH + ";" + player.getMaxHP() + ";" + player.getHP());
	}

	public void sendLoad() {
		if (loading) {
			load += 0.01;

			if (load > 1) {
				load = 1;
			}
		}

		socket.write(DataID.LOAD + ";" + load);
	}

	public Player getPlayer() {
		return player;
	}

	public void readMessages() {
		if (player != null) {
			double aX = 0;
			double aY = 0;

			if (accelX == 1) {
				aX = 0.005;
			} else if (accelX == -1) {
				aX = -0.005;
			}

			if (accelY == 1) {
				aY = 0.005;
			} else if (accelY == -1) {
				aY = -0.005;
			}

			player.accel(aX, aY);
		}

		while (true) {
			String msg = socket.read();

			if (msg == null) {
				break;
			}

			String[] parts = msg.split(";");

			if (player == null) {
				if (DataID.USERNAME.same(parts[0])) {
					if (parts.length > 1) {
						username = parts[1];

						player = new Player(1, 1);

						World.addEntity(player);

						sendValidUsername();
					} else {
						sendInvalidUsername();
					}
				} else {

				}
			} else {
				if (DataID.MOVE.same(parts[0])) {
					accelX = Integer.parseInt(parts[1]);
					accelY = Integer.parseInt(parts[2]);
				} else if (DataID.ROTATION.same(parts[0])) {
					player.setRotation(Double.parseDouble(parts[1]));
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
				} else if (DataID.PRIMARY.same(parts[0])) {
					if (parts[1].equals("1")) {
						loading = true;
					} else if (parts[1].equals("0")) {
						loading = false;

						if (load == 1) {
							double rot = Double.parseDouble(parts[2]);

							double x = player.getX() - Math.sin(rot) + BaseMod.PLAYER_MODEL.getSize() / 2
									- BaseMod.ARROW_MODEL.getSize() / 2;
							double y = player.getY() - Math.cos(rot) + BaseMod.PLAYER_MODEL.getSize() / 2
									- BaseMod.ARROW_MODEL.getSize() / 2;

							World.addEntity(
									new Arrow(x, y, player.getRotation(), player.getSpeedX(), player.getSpeedY()));
						}

						load = 0;
					}
				} else {
					System.err.println("Unknown data id : " + parts[0]);
					socket.close();
				}
			}
		}
	}
}
