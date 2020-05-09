"use strict";

namespace Main {
	const socket = new WebSocket("wss://" + window.location.hostname + ":" + window.location.port);

	function loadTextures() {
		console.log("Load textures");

		socket.onmessage = function (e: MessageEvent) {
			let datas = (e.data as string).split(";");

			if (datas[0] === DataID.LOAD_TEXTURES) {
				let texturesCount = parseInt(datas[1]);

				Textures.loadTextures(texturesCount);
			} else {
				throw e.data;
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
				throw e.data;
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

				loadWorld();
			} else {
				throw e.data;
			}
		}

		socket.send(DataID.LOAD_ENTITY_MODELS);
	}

	function loadWorld() {
		console.log("Load world");

		socket.onmessage = function (e: MessageEvent) {
			let datas = (e.data as string).split(";");


			if (datas[0] === DataID.LOAD_WORLD) {
				let size = parseInt(datas[1]);
				let height = parseInt(datas[2]);

				if (size !== World.SIZE || height !== World.HEIGHT) {
					throw "Server and client world size are differents";
				}

				let index = 3;

				for (let x = 0; x < World.SIZE; x++) {
					for (let y = 0; y < World.HEIGHT; y++) {
						for (let z = 0; z < World.SIZE; z++) {
							World.set(x, y, z, parseInt(datas[index++]));
						}
					}
				}

				loadFinished();
			} else {
				throw e.data;
			}
		}

		socket.send(DataID.LOAD_WORLD);
	}

	function loadFinished() {
		console.log("Loading finished");

		Render.prepare();

		socket.onmessage = function (e) {
			let datas: string[] = e.data.split(";");

			if (datas[0] === DataID.POSITION) {
				let x = parseFloat(datas[1]);
				let y = parseFloat(datas[2]);
				let z = parseFloat(datas[3]);

				Player.x = x;
				Player.y = y;
				Player.z = z;
			} else if (datas[0] === DataID.ENTITIES) {
				let count = parseInt(datas[1]);

				let newEntities: Entity[] = [];

				for (let i = 0; i < count; i++) {
					newEntities.push(new Entity(parseInt(datas[i * 4 + 2]), parseFloat(datas[i * 4 + 3]), parseFloat(datas[i * 4 + 4]), parseFloat(datas[i * 4 + 5])));
				}

				World.entities = newEntities;
			} else {
				throw e.data;
			}
		}

		socket.send(DataID.LOAD_FINISHED);
		startGame();
	}

	function startGame() {
		console.log("Game loop started");

		let spaceDown = false;
		let shiftDown = false;

		let wDown = false;
		let sDown = false;
		let aDown = false;
		let dDown = false;

		window.onkeydown = function (e: KeyboardEvent) {
			let code = e.which;

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
			const code = e.which;

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
			Render.canvas.requestPointerLock();
		}

		window.onmousemove = (e: MouseEvent) => {
			if (document.pointerLockElement !== null) {
				Player.rotY += e.movementX * 0.001;
				Player.rotX += e.movementY * 0.001;

				if (Player.rotX < -Math.PI / 2) {
					Player.rotX = -Math.PI / 2;
				} else if (Player.rotX > Math.PI / 2) {
					Player.rotX = Math.PI / 2;
				}
			}
		};

		function draw() {
			if (socket.readyState !== WebSocket.OPEN) {
				return;
			}

			let xMove = 0;
			if (wDown) {
				xMove++;
			}
			if (sDown) {
				xMove--;
			}

			let yMove = 0;
			if (spaceDown) {
				yMove++;
			}
			if (shiftDown) {
				yMove--;
			}

			let zMove = 0;
			if (aDown) {
				zMove--;
			}
			if (dDown) {
				zMove++;
			}

			socket.send(DataID.MOVE + ";" + xMove + ";" + yMove + ";" + zMove);
			socket.send(DataID.ROTATION + ";" + Player.rotY);

			Render.drawScene();

			window.requestAnimationFrame(draw);
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
