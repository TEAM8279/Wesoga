"use strict";


(() => {
	const socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port);

	socket.onmessage = function (e) {
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

		socket.close();

		ready();
	}

	function ready() {
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
			if (wDown) {
				Player.z -= Math.cos(Player.rotY) * 0.1;
				Player.x += Math.sin(Player.rotY) * 0.1;
			}

			if (sDown) {
				Player.z += Math.cos(Player.rotY) * 0.1;
				Player.x -= Math.sin(Player.rotY) * 0.1;
			}

			if (aDown) {
				Player.z -= Math.sin(Player.rotY) * 0.1;
				Player.x -= Math.cos(Player.rotY) * 0.1;
			}

			if (dDown) {
				Player.z += Math.sin(Player.rotY) * 0.1;
				Player.x += Math.cos(Player.rotY) * 0.1;
			}

			if (spaceDown) {
				Player.y += 0.1;
			}

			if (shiftDown) {
				Player.y -= 0.1;
			}

			Render.drawScene();

			window.requestAnimationFrame(draw);
		}
		window.requestAnimationFrame(draw);
	}
})();