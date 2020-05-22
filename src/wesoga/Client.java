package wesoga;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import wesoga.baseMod.entities.Player;
import wesoga.blocks.BlockModel;
import wesoga.blocks.BlockModels;
import wesoga.entities.Entity;
import wesoga.entities.EntityModel;
import wesoga.entities.EntityModels;
import wesoga.textures.Textures;

public class Client {
	private final WebSocket socket;

	private Player player = null;

	private int moveX = 0;
	private int moveY = 0;
	private int moveZ = 0;

	private ClientState state = ClientState.LOAD_TEXTURES;

	private String username = null;

	public Client(WebSocket socket) {
		this.socket = socket;
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

		for (int i = 0; i < BlockModels.count(); i++) {
			BlockModel b = BlockModels.get(i);

			builder.append(";");
			builder.append(b.visible ? 1 : 0);
			builder.append(";");
			builder.append(b.northTexture);
			builder.append(";");
			builder.append(b.southTexture);
			builder.append(";");
			builder.append(b.eastTexture);
			builder.append(";");
			builder.append(b.westTexture);
			builder.append(";");
			builder.append(b.topTexture);
			builder.append(";");
			builder.append(b.botTexture);
		}

		socket.write(builder.toString());
	}

	private void sendEntityModels() {
		StringBuilder builder = new StringBuilder(DataID.LOAD_ENTITY_MODELS.toString());

		builder.append(";");
		builder.append(EntityModels.count());

		for (int i = 0; i < EntityModels.count(); i++) {
			EntityModel e = EntityModels.get(i);

			builder.append(";");
			builder.append(e.northTexture);
			builder.append(";");
			builder.append(e.southTexture);
			builder.append(";");
			builder.append(e.eastTexture);
			builder.append(";");
			builder.append(e.westTexture);
			builder.append(";");
			builder.append(e.topTexture);
			builder.append(";");
			builder.append(e.botTexture);
			builder.append(";");
			builder.append(e.size);
			builder.append(";");
			builder.append(e.height);
		}

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
					builder.append(World.getBlock(x, y, z).model);
				}
			}
		}

		socket.write(builder.toString());
	}

	public boolean isConnected() {
		return socket.isOpen();
	}

	public void kick() {
		socket.close();
	}

	public Player getPlayer() {
		return player;
	}

	public void handleDeath() {
		this.state = ClientState.LOGIN;
		this.player = null;

		socket.write(DataID.DEATH.toString());
	}

	public void sendEntities() {
		ArrayList<Entity> selected = World.getVisibleEntities(player);

		StringBuilder builder = new StringBuilder(DataID.ENTITIES + ";" + selected.size());

		for (Entity e : selected) {
			builder.append(";");
			builder.append(e.model);
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

	private void handleLoadTexturesMessages() {
		String msg = socket.read();

		if (msg == null) {
			return;
		}

		String[] parts = msg.split(";");

		if (DataID.LOAD_TEXTURES.same(parts[0])) {
			sendTextures();
			state = ClientState.LOAD_BLOCK_MODELS;
		} else {
			System.err.println("Unknown data id for load textures state : " + parts[0]);
			socket.close();
		}
	}

	private void handleLoadBlockModelsMessages() {
		String msg = socket.read();

		if (msg == null) {
			return;
		}

		String[] parts = msg.split(";");

		if (DataID.LOAD_BLOCK_MODELS.same(parts[0])) {
			sendBlockModels();
			state = ClientState.LOAD_ENTITY_MODELS;
		} else {
			System.err.println("Unknown data id for load block models state : " + parts[0]);
			socket.close();
		}
	}

	private void handleLoadEntityModelsMessages() {
		String msg = socket.read();

		if (msg == null) {
			return;
		}

		String[] parts = msg.split(";");

		if (DataID.LOAD_ENTITY_MODELS.same(parts[0])) {
			sendEntityModels();
			state = ClientState.LOGIN;
		} else {
			System.err.println("Unknown data id for load entity models state : " + parts[0]);
			socket.close();
		}
	}

	private void handleLoginMessages() {
		String msg = socket.read();

		if (msg == null) {
			return;
		}

		String[] parts = msg.split(";");

		if (DataID.LOGIN.same(parts[0])) {
			username = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);

			sendWorld();

			player = new Player(50, 50, 50);
			World.addEntity(player);

			state = ClientState.IN_GAME;
		} else if (DataID.MOVE.same(parts[0]) || DataID.ROTATION.same(parts[0])) {
			// Ignore
		} else {
			System.err.println("Unknown data id for login state : " + parts[0]);
			socket.close();
		}
	}

	private void handleInGameMessages() {
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
				System.err.println("Unknown data id for in game state : " + parts[0]);
				socket.close();
			}
		}

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

	public void readMessages() {
		switch (state) {
		case LOAD_TEXTURES:
			handleLoadTexturesMessages();
			break;
		case LOAD_BLOCK_MODELS:
			handleLoadBlockModelsMessages();
			break;
		case LOAD_ENTITY_MODELS:
			handleLoadEntityModelsMessages();
			break;
		case LOGIN:
			handleLoginMessages();
			break;
		case IN_GAME:
			handleInGameMessages();
			break;
		}
	}
}
