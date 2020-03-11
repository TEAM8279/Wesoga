"use strict";

(() => {
	function onLoadMessage(e: MessageEvent) {
		let datas = (e.data as string).split(";");

		let index = 0;

		for (let x = 0; x < World.SIZE; x++) {
			for (let y = 0; y < World.HEIGHT; y++) {
				for (let z = 0; z < World.SIZE; z++) {
					World.set(x, y, z, parseInt(datas[index]));
					index++;
				}
			}
		}

		ready();
	}

	const socket = new WebSocket("wss://" + window.location.hostname + ":" + window.location.port);
	socket.onmessage = onLoadMessage;

	socket.onerror = function (e) {
		console.log("WebSocket error : " + e);
	}

	socket.onclose = function (e) {
		console.log("WebSocket closed : " + e.code + " : " + e.reason);
	}

	function ready() {
		console.log("Game loop started");
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

				let newEntities: World.Entity[] = [];

				for (let i = 0; i < count; i++) {
					newEntities.push(new World.Entity(parseFloat(datas[i * 3 + 2]), parseFloat(datas[i * 3 + 3]), parseFloat(datas[i * 3 + 4])));
				}

				World.entities = newEntities;
			} else {
				throw e.data;
			}
		}

		Render.prepare();

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

		window.onkeyup = (e: KeyboardEvent) => {
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

		window.onmousedown = () => {
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
		}

		function draw() {
			let xMove = 0;
			let yMove = 0;
			let zMove = 0;

			if (wDown) {
				xMove++;
			}

			if (sDown) {
				xMove--;
			}

			if (aDown) {
				zMove--;
			}

			if (dDown) {
				zMove++;
			}

			if (spaceDown) {
				yMove++;
			}

			if (shiftDown) {
				yMove--;
			}

			socket.send(DataID.MOVE + ";" + xMove + ";" + yMove + ";" + zMove);
			socket.send(DataID.ROTATION + ";" + Player.rotY);

			Render.drawScene();

			window.requestAnimationFrame(draw);
		}
		window.requestAnimationFrame(draw);
	}
})();