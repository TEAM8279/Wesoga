package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.entities.Player;
import wesoga.blocks.BlockModels;
import wesoga.entities.Entity;
import wesoga.textures.Textures;

public class Client {
	private final WebSocket socket;

	private Player player = new Player(50, 50, 50);

	private int moveX = 0;
	private int moveY = 0;
	private int moveZ = 0;

	public Client(WebSocket socket) {
		this.socket = socket;

		sendGameData();

		World.addEntity(player);
	}

	private void sendTextures() {
		StringBuilder builder = new StringBuilder(DataID.LOAD_TEXTURES.toString());
		
		builder.append(";");
		builder.append(Textures.count());
		
		socket.write(builder.toString());
	}

	private void sendBlockModels() {
		StringBuilder builder = new StringBuilder(DataID.LOAD_BLOCK_MODELS.toString());
		
		builder.append(";");
		builder.append(BlockModels.count());
		
		socket.write(builder.toString());
	}

	private void sendWorld() {
		StringBuilder builder = new StringBuilder(DataID.LOAD_WORLD.toString());

		builder.append(";");
		builder.append(World.SIZE);
		builder.append(";");
		builder.append(World.HEIGHT);
		
		for (int x = 0; x < World.SIZE; x++) {
			for (int y = 0; y < World.HEIGHT; y++) {
				for (int z = 0; z < World.SIZE; z++) {
					builder.append(";");
					builder.append(World.getBlock(x, y, z));
				}
			}
		}

		socket.write(builder.toString());
	}
	
	private void sendLoadFinished() {
		socket.write(DataID.LOAD_FINISHED.toString());
	}

	private void sendGameData() {
		
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
			builder.append(e.getX());
			builder.append(";");
			builder.append(e.getY());
			builder.append(";");
			builder.append(e.getZ());
		}

		socket.write(builder.toString());
	}

	public void sendPosition() {
		socket.write(DataID.POSITION + ";" + player.getX() + ";" + player.getY() + ";" + player.getZ());
	}

	public void sendHP() {
		socket.write(DataID.HEALTH + ";" + player.getMaxHP() + ";" + player.getHP());
	}

	public Player getPlayer() {
		return player;
	}

	public void readMessages() {
		while (true) {
			String msg = socket.read();

			if (msg == null) {
				break;
			}

			String[] parts = msg.split(";");

			if (DataID.MOVE.same(parts[0])) {
				moveX = Integer.parseInt(parts[1]);
				moveY = Integer.parseInt(parts[2]);
				moveZ = Integer.parseInt(parts[3]);
			} else if (DataID.ROTATION.same(parts[0])) {
				player.setRotation(Double.parseDouble(parts[1]));
			} else {
				System.err.println("Unknown data id : " + parts[0]);
				socket.close();
			}
		}

		if (player != null) {
			double mX = 0;
			double mY = 0;
			double mZ = 0;

			if (moveX == 1) {
				mX = 0.004;
			} else if (moveX == -1) {
				mX = -0.004;
			}

			if (player.isOnFloor() && moveY == 1) {
				mY = 0.09;
			}

			if (moveZ == 1) {
				mZ = 0.004;
			} else if (moveZ == -1) {
				mZ = -0.004;
			}

			double sin = Math.sin(player.getRotation());
			double cos = Math.cos(player.getRotation());

			double aZ = sin * mZ - cos * mX;
			double aX = sin * mX + cos * mZ;
			double aY = mY;

			player.accel(aX, aY, aZ);
		}
	}
}
