namespace Textures {
	export const textures = document.createElement("canvas");
	const gc = textures.getContext("2d");

	export const textureSize = 256;
	export let textureCount = 0;

	export function loadTextures(count: number) {
		textureCount = count;

		textures.height = textureSize * count;
		textures.width = textureSize;

		gc.imageSmoothingEnabled = false;

		let index = 0;

		function load() {
			let img = new Image();

			img.onload = function () {
				gc.drawImage(img, 0, textureSize * index, textureSize, textureSize);

				index++
				if (index < count) {
					load();
				} else {
					Main.loadBlockModels();
				}
			}

			img.src = "textures/" + index;
		}

		load();
	}
}
