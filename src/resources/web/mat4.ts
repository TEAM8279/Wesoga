/**
 * Typescript mat4 based on glmatrix mat4.js
 */

module mat4 {

	/**
	 * Creates a new identity mat4
	 *
	 * @returns a new 4x4 matrix
	 */
	export function create(): Float32Array {
		let out = new Float32Array(16);

		out[0] = 1;
		out[5] = 1;
		out[10] = 1;
		out[15] = 1;

		return out;
	}

	/**
	 * Multiplies two mat4s
	 *
	 * @param {mat4} out the receiving matrix
	 * @param {mat4} a the first operand
	 * @param {mat4} b the second operand
	 */
	export function multiply(out: Float32Array, a: Float32Array, b: Float32Array) {
		let a00 = a[0], a01 = a[1], a02 = a[2], a03 = a[3];
		let a10 = a[4], a11 = a[5], a12 = a[6], a13 = a[7];
		let a20 = a[8], a21 = a[9], a22 = a[10], a23 = a[11];
		let a30 = a[12], a31 = a[13], a32 = a[14], a33 = a[15];

		// Cache only the current line of the second matrix
		let b0 = b[0], b1 = b[1], b2 = b[2], b3 = b[3];
		out[0] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30;
		out[1] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31;
		out[2] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32;
		out[3] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33;

		b0 = b[4]; b1 = b[5]; b2 = b[6]; b3 = b[7];
		out[4] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30;
		out[5] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31;
		out[6] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32;
		out[7] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33;

		b0 = b[8]; b1 = b[9]; b2 = b[10]; b3 = b[11];
		out[8] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30;
		out[9] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31;
		out[10] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32;
		out[11] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33;

		b0 = b[12]; b1 = b[13]; b2 = b[14]; b3 = b[15];
		out[12] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30;
		out[13] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31;
		out[14] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32;
		out[15] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33;
	}

	export function translate(out: Float32Array, a: Float32Array, x: number, y: number, z: number) {
		if (a === out) {
			out[12] = a[0] * x + a[4] * y + a[8] * z + a[12];
			out[13] = a[1] * x + a[5] * y + a[9] * z + a[13];
			out[14] = a[2] * x + a[6] * y + a[10] * z + a[14];
			out[15] = a[3] * x + a[7] * y + a[11] * z + a[15];
		} else {
			let a00 = a[0]; let a01 = a[1]; let a02 = a[2]; let a03 = a[3];
			let a10 = a[4]; let a11 = a[5]; let a12 = a[6]; let a13 = a[7];
			let a20 = a[8]; let a21 = a[9]; let a22 = a[10]; let a23 = a[11];

			out[0] = a00; out[1] = a01; out[2] = a02; out[3] = a03;
			out[4] = a10; out[5] = a11; out[6] = a12; out[7] = a13;
			out[8] = a20; out[9] = a21; out[10] = a22; out[11] = a23;

			out[12] = a00 * x + a10 * y + a20 * z + a[12];
			out[13] = a01 * x + a11 * y + a21 * z + a[13];
			out[14] = a02 * x + a12 * y + a22 * z + a[14];
			out[15] = a03 * x + a13 * y + a23 * z + a[15];
		}
	}

	/**
	 * Rotates a matrix by the given angle around the X axis
	 *
	 * @param {mat4} out the receiving matrix
	 * @param {mat4} a the matrix to rotate
	 * @param {Number} rad the angle to rotate the matrix by
	 */
	export function rotateX(out: Float32Array, a: Float32Array, rad: number) {
		let s = Math.sin(rad);
		let c = Math.cos(rad);
		let a10 = a[4];
		let a11 = a[5];
		let a12 = a[6];
		let a13 = a[7];
		let a20 = a[8];
		let a21 = a[9];
		let a22 = a[10];
		let a23 = a[11];

		if (a !== out) { // If the source and destination differ, copy the unchanged rows
			out[0] = a[0];
			out[1] = a[1];
			out[2] = a[2];
			out[3] = a[3];
			out[12] = a[12];
			out[13] = a[13];
			out[14] = a[14];
			out[15] = a[15];
		}

		// Perform axis-specific matrix multiplication
		out[4] = a10 * c + a20 * s;
		out[5] = a11 * c + a21 * s;
		out[6] = a12 * c + a22 * s;
		out[7] = a13 * c + a23 * s;
		out[8] = a20 * c - a10 * s;
		out[9] = a21 * c - a11 * s;
		out[10] = a22 * c - a12 * s;
		out[11] = a23 * c - a13 * s;
	}

	/**
	 * Rotates a matrix by the given angle around the Y axis
	 *
	 * @param {mat4} out the receiving matrix
	 * @param {mat4} a the matrix to rotate
	 * @param {Number} rad the angle to rotate the matrix by
	 */
	export function rotateY(out: Float32Array, a: Float32Array, rad: number) {
		let s = Math.sin(rad);
		let c = Math.cos(rad);
		let a00 = a[0];
		let a01 = a[1];
		let a02 = a[2];
		let a03 = a[3];
		let a20 = a[8];
		let a21 = a[9];
		let a22 = a[10];
		let a23 = a[11];

		if (a !== out) { // If the source and destination differ, copy the unchanged rows
			out[4] = a[4];
			out[5] = a[5];
			out[6] = a[6];
			out[7] = a[7];
			out[12] = a[12];
			out[13] = a[13];
			out[14] = a[14];
			out[15] = a[15];
		}

		// Perform axis-specific matrix multiplication
		out[0] = a00 * c - a20 * s;
		out[1] = a01 * c - a21 * s;
		out[2] = a02 * c - a22 * s;
		out[3] = a03 * c - a23 * s;
		out[8] = a00 * s + a20 * c;
		out[9] = a01 * s + a21 * c;
		out[10] = a02 * s + a22 * c;
		out[11] = a03 * s + a23 * c;
	}

	/**
	 * Rotates a matrix by the given angle around the Z axis
	 *
	 * @param {mat4} out the receiving matrix
	 * @param {mat4} a the matrix to rotate
	 * @param {Number} rad the angle to rotate the matrix by
	 */
	export function rotateZ(out: Float32Array, a: Float32Array, rad: number) {
		let s = Math.sin(rad);
		let c = Math.cos(rad);
		let a00 = a[0];
		let a01 = a[1];
		let a02 = a[2];
		let a03 = a[3];
		let a10 = a[4];
		let a11 = a[5];
		let a12 = a[6];
		let a13 = a[7];

		if (a !== out) { // If the source and destination differ, copy the unchanged last row
			out[8] = a[8];
			out[9] = a[9];
			out[10] = a[10];
			out[11] = a[11];
			out[12] = a[12];
			out[13] = a[13];
			out[14] = a[14];
			out[15] = a[15];
		}

		// Perform axis-specific matrix multiplication
		out[0] = a00 * c + a10 * s;
		out[1] = a01 * c + a11 * s;
		out[2] = a02 * c + a12 * s;
		out[3] = a03 * c + a13 * s;
		out[4] = a10 * c - a00 * s;
		out[5] = a11 * c - a01 * s;
		out[6] = a12 * c - a02 * s;
		out[7] = a13 * c - a03 * s;
	}

	/**
	 * Generates a perspective projection matrix with the given bounds.
	 * Passing null/undefined/no value for far will generate infinite projection matrix.
	 *
	 * @param {mat4} out mat4 frustum matrix will be written into
	 * @param {number} fovy Vertical field of view in radians
	 * @param {number} aspect Aspect ratio. typically viewport width/height
	 * @param {number} near Near bound of the frustum
	 * @param {number} far Far bound of the frustum, can be null or Infinity
	 */
	export function perspective(out: Float32Array, fovy: number, aspect: number, near: number, far: number) {
		let f = 1.0 / Math.tan(fovy / 2);
		out[0] = f / aspect;
		out[1] = 0;
		out[2] = 0;
		out[3] = 0;
		out[4] = 0;
		out[5] = f;
		out[6] = 0;
		out[7] = 0;
		out[8] = 0;
		out[9] = 0;
		out[11] = -1;
		out[12] = 0;
		out[13] = 0;
		out[15] = 0;
		if (far != null && far !== Infinity) {
			let nf = 1 / (near - far);
			out[10] = (far + near) * nf;
			out[14] = (2 * far * near) * nf;
		} else {
			out[10] = -1;
			out[14] = -2 * near;
		}
	}

	export function multiplyVector(a: Float32Array, x: number, y: number, z: number) {
		return {
			x: x * a[0] + y * a[4] + z * a[8] + a[12],
			y: x * a[1] + y * a[5] + z * a[9] + a[13],
			z: x * a[2] + y * a[6] + z * a[10] + a[14]
		};
	}

	/**
	 * Returns a string representation of a mat4
	 *
	 * @param {mat4} a matrix to represent as a string
	 * @returns {String} string representation of the matrix
	 */
	export function str(a: Float32Array): string {
		return 'mat4(' + a[0] + ', ' + a[1] + ', ' + a[2] + ', ' + a[3] + ', ' +
			a[4] + ', ' + a[5] + ', ' + a[6] + ', ' + a[7] + ', ' +
			a[8] + ', ' + a[9] + ', ' + a[10] + ', ' + a[11] + ', ' +
			a[12] + ', ' + a[13] + ', ' + a[14] + ', ' + a[15] + ')';
	}
}
