"use strict";

namespace Main {
	const SMOOTH_FACTOR = 0.03

	let died = false;

	const socket = new WebSocket("wss://" + window.location.hostname + ":" + window.location.port);

	const username = document.getElementById("username") as HTMLInputElement;

	function loadTextures() {
		console.log("Load textures");

		socket.onmessage = function (e: MessageEvent) {
			let datas = (e.data as string).split(";");

			if (datas[0] === DataID.LOAD_TEXTURES) {
				let texturesCount = parseInt(datas[1]);

				Textures.loadTextures(texturesCount);
			} else {
				throw Error("Unknown data id : " + datas[0]);
			}
		}

		socket.send(DataID.LOAD_TEXTURES);
	}

	export function loadBlockModels() {
		console.log("Load block models");

		socket.onmessage = function (e: MessageEvent) {
			let datas = (e.data as string).split(";");

			if (datas[0] === DataID.LOAD_BLOCK_MODELS) {
				let modelsCount = parseInt(datas[1]);

				for (let i = 0; i < modelsCount; i++) {
					BlockModels.add(new BlockModel(datas[2 + i * 7] == "1", parseInt(datas[3 + i * 7]), parseInt(datas[4 + i * 7]), parseInt(datas[5 + i * 7]), parseInt(datas[6 + i * 7]), parseInt(datas[7 + i * 7]), parseInt(datas[8 + i * 7])));
				}

				loadEntityModels();
			} else {
				throw Error("Unknown data id : " + datas[0]);
			}
		}

		socket.send(DataID.LOAD_BLOCK_MODELS);
	}

	function loadEntityModels() {
		console.log("Load entity models");

		socket.onmessage = function (e: MessageEvent) {
			let datas = (e.data as string).split(";");

			if (datas[0] === DataID.LOAD_ENTITY_MODELS) {
				let modelsCount = parseInt(datas[1]);

				for (let i = 0; i < modelsCount; i++) {
					EntityModels.add(new EntityModel(parseInt(datas[2 + i * 8]), parseInt(datas[3 + i * 8]), parseInt(datas[4 + i * 8]), parseInt(datas[5 + i * 8]), parseInt(datas[6 + i * 8]), parseInt(datas[7 + i * 8]), parseFloat(datas[8 + i * 8]), parseFloat(datas[9 + i * 8])));
				}

				showTitle();
			} else {
				throw Error("Unknown data id : " + datas[0]);
			}
		}

		socket.send(DataID.LOAD_ENTITY_MODELS);
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

				socket.send(DataID.LOGIN + ";" + btoa(username.value));

				Render3D.prepare();

				socket.onmessage = function (e) {
					let datas: string[] = e.data.split(";");

					if (datas[0] === DataID.POSITION) {
						Player.x = parseFloat(datas[1]);
						Player.y = parseFloat(datas[2]);
						Player.z = parseFloat(datas[3]);
					} else if (datas[0] === DataID.ENTITIES) {
						let count = parseInt(datas[1]);

						let newEntities: Entity[] = [];

						for (let i = 0; i < count; i++) {
							newEntities.push(new Entity(parseInt(datas[i * 6 + 2]), parseInt(datas[i * 6 + 3]), parseFloat(datas[i * 6 + 4]), parseFloat(datas[i * 6 + 5]), parseFloat(datas[i * 6 + 6]), parseFloat(datas[i * 6 + 7])));
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
					} else if (datas[0] === DataID.HEALTH) {
						Player.maxHP = parseInt(datas[1]);
						Player.hp = parseInt(datas[2]);
					} else if (datas[0] === DataID.DEATH) {
						died = true;
					} else if (datas[0] === DataID.LOAD_WORLD) {
						let size = parseInt(datas[1]);
						let height = parseInt(datas[2]);

						if (size !== World.SIZE || height !== World.HEIGHT) {
							throw new Error("Server and client world size are differents");
						}

						let index = 3;

						for (let x = 0; x < World.SIZE; x++) {
							for (let y = 0; y < World.HEIGHT; y++) {
								for (let z = 0; z < World.SIZE; z++) {
									World.set(x, y, z, parseInt(datas[index++]));
								}
							}
						}

						Render3D.prepare();
					} else {
						throw Error("Unknown data id : " + datas[0]);
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

			socket.send(DataID.MOVE + ";" + xMove + ";" + yMove + ";" + zMove);
			socket.send(DataID.ROTATION + ";" + Player.rotY);

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
