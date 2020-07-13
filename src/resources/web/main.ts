"use strict";

namespace Main {
	const SMOOTH_FACTOR = 0.03

	let died = false;

	const socket = new WebSocket("wss://" + window.location.hostname + ":" + window.location.port);
	socket.binaryType = "arraybuffer";

	const username = document.getElementById("username") as HTMLInputElement;

	function loadTextures() {
		console.log("Load textures");

		socket.onmessage = function (e: MessageEvent) {
			let reader = new ByteArrayReader(e.data);

			let dataid = reader.readByte();

			if (dataid === DataID.LOAD_TEXTURES) {
				let texturesCount = reader.readInt();

				Textures.loadTextures(texturesCount);
			} else {
				throw Error("Unknown data id : " + dataid);
			}
		}

		socket.send(new Int8Array([DataID.LOAD_TEXTURES]));
	}

	export function loadBlockModels() {
		console.log("Load block models");

		socket.onmessage = function (e: MessageEvent) {
			let reader = new ByteArrayReader(e.data);

			let dataid = reader.readByte();

			if (dataid === DataID.LOAD_BLOCK_MODELS) {
				let modelsCount = reader.readInt();

				for (let i = 0; i < modelsCount; i++) {
					let visible = reader.readBoolean();
					let north = reader.readInt();
					let south = reader.readInt();
					let east = reader.readInt();
					let west = reader.readInt();
					let top = reader.readInt();
					let bot = reader.readInt();

					BlockModels.add(new BlockModel(visible, north, south, east, west, top, bot));
				}

				loadEntityModels();
			} else {
				throw Error("Unknown data id : " + dataid);
			}
		}


		socket.send(new Int8Array([DataID.LOAD_BLOCK_MODELS]));
	}

	function loadEntityModels() {
		console.log("Load entity models");

		socket.onmessage = function (e: MessageEvent) {
			let reader = new ByteArrayReader(e.data);

			let dataid = reader.readByte();

			if (dataid === DataID.LOAD_ENTITY_MODELS) {
				let modelsCount = reader.readInt();

				for (let i = 0; i < modelsCount; i++) {
					let north = reader.readInt();
					let south = reader.readInt();
					let east = reader.readInt();
					let west = reader.readInt();
					let top = reader.readInt();
					let bot = reader.readInt();
					let size = reader.readDouble();
					let height = reader.readDouble();

					EntityModels.add(new EntityModel(north, south, east, west, top, bot, size, height));
				}

				showTitle();
			} else {
				throw Error("Unknown data id : " + dataid);
			}
		}

		socket.send(new Int8Array([DataID.LOAD_ENTITY_MODELS]));
	}

	function showDeath() {
		window.onmousedown = null;

		document.exitPointerLock();

		window.onkeyup = null;

		window.onkeydown = function (e: KeyboardEvent) {
			if (e.keyCode === 13) {
				showTitle();
				window.onkeydown = null;
			}
		}

		Screens.showDeath();
	}

	function showTitle() {
		username.onkeydown = function (e) {
			if (e.keyCode === 13 && username.value != "") {
				username.onkeydown = null;

				let writer = new ByteArrayWriter();

				writer.writeByte(DataID.LOGIN);
				writer.writeString(username.value);

				socket.send(writer.toArrayBuffer());

				Render3D.prepare();

				socket.onmessage = function (e) {
					let reader = new ByteArrayReader(e.data);

					let dataid = reader.readByte();

					if (dataid === DataID.POSITION) {
						Player.x = reader.readDouble();
						Player.y = reader.readDouble();
						Player.z = reader.readDouble();
					} else if (dataid === DataID.ENTITIES) {
						let count = reader.readInt();

						let newEntities: Entity[] = [];

						for (let i = 0; i < count; i++) {
							let model = reader.readInt();
							let uid = reader.readInt();
							let x = reader.readDouble();
							let y = reader.readDouble();
							let z = reader.readDouble();
							let rot = reader.readDouble();

							newEntities.push(new Entity(model, uid, x, y, z, rot));
						}

						for (let i = 0; i < World.entities.length; i++) {
							let oldEntity = World.entities[i];

							for (let j = 0; j < newEntities.length; j++) {
								let newEntity = newEntities[j];

								if (newEntity.uid === oldEntity.uid && newEntity.model === oldEntity.model) {
									newEntity.aX = oldEntity.aX;
									newEntity.aY = oldEntity.aY;
									newEntity.aZ = oldEntity.aZ;
									break;
								}
							}
						}

						World.entities = newEntities;
					} else if (dataid === DataID.HEALTH) {
						Player.maxHP = reader.readInt();
						Player.hp = reader.readInt();
					} else if (dataid === DataID.DEATH) {
						died = true;
					} else if (dataid === DataID.LOAD_WORLD) {
						let size = reader.readInt();
						let height = reader.readInt();

						if (size !== World.SIZE || height !== World.HEIGHT) {
							throw new Error("Server and client world size are differents");
						}

						for (let x = 0; x < World.SIZE; x++) {
							for (let y = 0; y < World.HEIGHT; y++) {
								for (let z = 0; z < World.SIZE; z++) {
									World.set(x, y, z, reader.readInt());
								}
							}
						}

						Render3D.prepare();
					} else {
						throw Error("Unknown data id : " + dataid);
					}
				}

				startGame();
			}
		}

		Screens.showTitle();
	}

	function startGame() {
		Screens.showGame();

		console.log("Game loop started");

		let spaceDown = false;
		let shiftDown = false;

		let wDown = false;
		let sDown = false;
		let aDown = false;
		let dDown = false;

		window.onkeydown = function (e: KeyboardEvent) {
			let code = e.keyCode;

			if (code === 32) {
				spaceDown = true;
			} else if (code === 16) {
				shiftDown = true;
			} else if (code === 87) {
				wDown = true;
			} else if (code === 83) {
				sDown = true;
			} else if (code === 65) {
				aDown = true;
			} else if (code === 68) {
				dDown = true;
			}
		}

		window.onkeyup = function (e: KeyboardEvent) {
			const code = e.keyCode;

			if (code === 32) {
				spaceDown = false;
			} else if (code === 16) {
				shiftDown = false;
			} else if (code === 87) {
				wDown = false;
			} else if (code === 83) {
				sDown = false;
			} else if (code === 65) {
				aDown = false;
			} else if (code === 68) {
				dDown = false;
			}
		}

		window.onmousedown = function () {
			Render3D.canvas.requestPointerLock();
		}

		window.onmousemove = (e: MouseEvent) => {
			if (document.pointerLockElement !== null) {
				Player.rotY -= e.movementX * 0.001;
				Player.rotX += e.movementY * 0.001;

				if (Player.rotX < -Math.PI / 2) {
					Player.rotX = -Math.PI / 2;
				} else if (Player.rotX > Math.PI / 2) {
					Player.rotX = Math.PI / 2;
				}
			}
		};

		let previous = Date.now();

		function draw() {
			if (socket.readyState !== WebSocket.OPEN) {
				return;
			}

			let xMove = 0;
			if (aDown) {
				xMove--;
			}
			if (dDown) {
				xMove++;
			}

			let yMove = 0;
			if (spaceDown) {
				yMove++;
			}
			if (shiftDown) {
				yMove--;
			}

			let zMove = 0;
			if (wDown) {
				zMove--;
			}
			if (sDown) {
				zMove++;
			}

			let writer = new ByteArrayWriter();
			writer.writeByte(DataID.MOVE);
			writer.writeByte(xMove);
			writer.writeByte(yMove);
			writer.writeByte(zMove);
			socket.send(writer.toArrayBuffer());

			writer = new ByteArrayWriter();
			writer.writeByte(DataID.ROTATION);
			writer.writeDouble(Player.rotY);
			socket.send(writer.toArrayBuffer());

			let now = Date.now();

			let smooth = Math.min(1, SMOOTH_FACTOR * (now - previous));

			previous = now;

			for (let i = 0; i < World.entities.length; i++) {
				World.entities[i].aX += (World.entities[i].x - World.entities[i].aX) * smooth;
				World.entities[i].aY += (World.entities[i].y - World.entities[i].aY) * smooth;
				World.entities[i].aZ += (World.entities[i].z - World.entities[i].aZ) * smooth;
			}

			Player.aX += (Player.x - Player.aX) * smooth;
			Player.aY += (Player.y - Player.aY) * smooth;
			Player.aZ += (Player.z - Player.aZ) * smooth;

			Render3D.render();
			Render2D.render();

			if (died) {
				died = false;
				showDeath();
			} else {
				window.requestAnimationFrame(draw);
			}
		}
		window.requestAnimationFrame(draw);
	}

	export function main() {
		socket.onopen = function () {
			loadTextures();
		}

		socket.onerror = function (e) {
			console.log("WebSocket error : " + e);
		}

		socket.onclose = function (e) {
			console.log("WebSocket closed : " + e.code + " : " + e.reason);
		}
	}
}

Main.main();
