namespace Render2D {
	export const canvas = document.getElementById("canvas2d") as HTMLCanvasElement;
	const gc = canvas.getContext("2d");

	let tx = 0;
	let ty = 0;
	let f = 0;

	function fillRect(x: number, y: number, width: number, height: number) {
		gc.fillRect(x * f + tx, y * f + ty, width * f, height * f);
	}

	export function render() {
		canvas.height = window.innerHeight;
		canvas.width = window.innerWidth;

		f = Math.min(canvas.height, canvas.width);

		tx = (canvas.width - f) / 2;
		ty = (canvas.height - f) / 2;


		gc.fillStyle = "#882222";
		fillRect(0.1, 0.9, 0.2, 0.05);

		gc.fillStyle = "#ff4444";
		fillRect(0.1, 0.9, 0.2 * Player.hp / Player.maxHP, 0.05);
	}
}
