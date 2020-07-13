package wesoga;

import java.util.ArrayList;

import wesoga.baseMod.entities.Player;
import wesoga.blocks.BlockModel;
import wesoga.blocks.BlockModels;
import wesoga.entities.Entity;
import wesoga.entities.EntityModel;
import wesoga.entities.EntityModels;
import wesoga.textures.Textures;
import wesoga.util.ByteArrayReader;
import wesoga.util.ByteArrayWriter;

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
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.LOAD_TEXTURES);
		data.writeInt(Textures.count());

		socket.write(data.toByteArray());
	}

	private void sendBlockModels() {
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.LOAD_BLOCK_MODELS);
		data.writeInt(BlockModels.count());

		for (int i = 0; i < BlockModels.count(); i++) {
			BlockModel b = BlockModels.get(i);

			data.writeBoolean(b.visible);
			data.writeInt(b.northTexture);
			data.writeInt(b.southTexture);
			data.writeInt(b.eastTexture);
			data.writeInt(b.westTexture);
			data.writeInt(b.topTexture);
			data.writeInt(b.botTexture);
		}

		socket.write(data.toByteArray());
	}

	private void sendEntityModels() {
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.LOAD_ENTITY_MODELS);
		data.writeInt(EntityModels.count());

		for (int i = 0; i < EntityModels.count(); i++) {
			EntityModel e = EntityModels.get(i);

			data.writeInt(e.northTexture);
			data.writeInt(e.southTexture);
			data.writeInt(e.eastTexture);
			data.writeInt(e.westTexture);
			data.writeInt(e.topTexture);
			data.writeInt(e.botTexture);
			data.writeDouble(e.size);
			data.writeDouble(e.height);
		}

		socket.write(data.toByteArray());
	}

	private void sendWorld() {
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.LOAD_WORLD);
		data.writeInt(World.SIZE);
		data.writeInt(World.HEIGHT);

		for (int x = 0; x < World.SIZE; x++) {
			for (int y = 0; y < World.HEIGHT; y++) {
				for (int z = 0; z < World.SIZE; z++) {
					data.writeInt(World.getBlock(x, y, z).model);
				}
			}
		}

		socket.write(data.toByteArray());
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

	public String getUsername() {
		return username;
	}

	public void handleDeath() {
		this.state = ClientState.LOGIN;
		this.player = null;

		socket.write(new byte[] { DataID.DEATH });
	}

	public void sendEntities() {
		ArrayList<Entity> selected = World.getVisibleEntities(player);

		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.ENTITIES);
		data.writeInt(selected.size());

		for (Entity e : selected) {
			data.writeInt(e.model);
			data.writeInt(e.getUID());
			data.writeDouble(e.getX());
			data.writeDouble(e.getY());
			data.writeDouble(e.getZ());
			data.writeDouble(e.getRotation());
		}

		socket.write(data.toByteArray());
	}

	public void sendPosition() {
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.POSITION);
		data.writeDouble(player.getX());
		data.writeDouble(player.getY());
		data.writeDouble(player.getZ());

		socket.write(data.toByteArray());
	}

	public void sendHP() {
		ByteArrayWriter data = new ByteArrayWriter();

		data.writeByte(DataID.HEALTH);
		data.writeInt(player.getMaxHP());
		data.writeInt(player.getHP());

		socket.write(data.toByteArray());
	}

	private void handleLoadTexturesMessages() {
		byte[] msg = socket.read();

		if (msg == null) {
			return;
		}

		if (DataID.LOAD_TEXTURES == msg[0]) {
			sendTextures();
			state = ClientState.LOAD_BLOCK_MODELS;
		} else {
			System.err.println("Unknown data id for load textures state : " + msg[0]);
			socket.close();
		}
	}

	private void handleLoadBlockModelsMessages() {
		byte[] msg = socket.read();

		if (msg == null) {
			return;
		}

		if (DataID.LOAD_BLOCK_MODELS == msg[0]) {
			sendBlockModels();
			state = ClientState.LOAD_ENTITY_MODELS;
		} else {
			System.err.println("Unknown data id for load block models state : " + msg[0]);
			socket.close();
		}
	}

	private void handleLoadEntityModelsMessages() {
		byte[] msg = socket.read();

		if (msg == null) {
			return;
		}

		if (DataID.LOAD_ENTITY_MODELS == msg[0]) {
			sendEntityModels();
			state = ClientState.LOGIN;
		} else {
			System.err.println("Unknown data id for load entity models state : " + msg[0]);
			socket.close();
		}
	}

	private void handleLoginMessages() {
		byte[] msg = socket.read();

		if (msg == null) {
			return;
		}

		ByteArrayReader in = new ByteArrayReader(msg);

		byte dataid = in.readByte();

		if (DataID.LOGIN == dataid) {
			username = in.readString();

			sendWorld();

			player = new Player(50, 50, 50);
			World.addEntity(player);

			state = ClientState.IN_GAME;
		} else if (DataID.MOVE == dataid || DataID.ROTATION == dataid) {
			// Ignore
		} else {
			System.err.println("Unknown data id for login state : " + dataid);
			socket.close();
		}
	}

	private void handleInGameMessages() {
		while (true) {
			byte[] msg = socket.read();

			if (msg == null) {
				break;
			}

			ByteArrayReader in = new ByteArrayReader(msg);

			byte dataid = in.readByte();

			if (DataID.MOVE == dataid) {
				moveX = in.readByte();
				moveY = in.readByte();
				moveZ = in.readByte();
			} else if (DataID.ROTATION == dataid) {
				player.setRotation(in.readDouble());
			} else {
				System.err.println("Unknown data id for in game state : " + dataid);
				socket.close();
			}
		}

		double mX = 0;
		double mY = 0;
		double mZ = 0;

		if (moveX == 1) {
			mX = 0.005;
		} else if (moveX == -1) {
			mX = -0.005;
		}

		if (player.touchDown() && moveY == 1) {
			mY = 0.09;
		}

		if (moveZ == 1) {
			mZ = 0.005;
		} else if (moveZ == -1) {
			mZ = -0.005;
		}

		double sin = Math.sin(player.getRotation());
		double cos = Math.cos(player.getRotation());

		double aZ = cos * mZ - sin * mX;
		double aX = sin * mZ + cos * mX;
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
