package wesoga.util;

public class OpenSimplexNoise {
	private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
	private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
	private static final double NORM_CONSTANT_2D = 47;

	private static final long DEFAULT_SEED = 0;

	private static final byte[] gradients2D = new byte[] { 5, 2, 2, 5, -5, 2, -2, 5, 5, -2, 2, -5, -5, -2, -2, -5, };

	private short[] perm;

	public OpenSimplexNoise() {
		this(DEFAULT_SEED);
	}

	public OpenSimplexNoise(short[] perm) {
		this.perm = perm;
	}

	public OpenSimplexNoise(long seed) {
		perm = new short[256];
		short[] source = new short[256];
		for (short i = 0; i < 256; i++)
			source[i] = i;
		seed = seed * 6364136223846793005l + 1442695040888963407l;
		seed = seed * 6364136223846793005l + 1442695040888963407l;
		seed = seed * 6364136223846793005l + 1442695040888963407l;
		for (int i = 255; i >= 0; i--) {
			seed = seed * 6364136223846793005l + 1442695040888963407l;
			int r = (int) ((seed + 31) % (i + 1));
			if (r < 0)
				r += (i + 1);
			perm[i] = source[r];
			source[r] = source[i];
		}
	}

	public double eval(double x, double y) {
		double stretchOffset = (x + y) * STRETCH_CONSTANT_2D;
		double xs = x + stretchOffset;
		double ys = y + stretchOffset;

		int xsb = fastFloor(xs);
		int ysb = fastFloor(ys);

		double squishOffset = (xsb + ysb) * SQUISH_CONSTANT_2D;
		double xb = xsb + squishOffset;
		double yb = ysb + squishOffset;

		double xins = xs - xsb;
		double yins = ys - ysb;

		double inSum = xins + yins;

		double dx0 = x - xb;
		double dy0 = y - yb;

		double dx_ext, dy_ext;
		int xsv_ext, ysv_ext;

		double value = 0;

		double dx1 = dx0 - 1 - SQUISH_CONSTANT_2D;
		double dy1 = dy0 - 0 - SQUISH_CONSTANT_2D;
		double attn1 = 2 - dx1 * dx1 - dy1 * dy1;
		if (attn1 > 0) {
			attn1 *= attn1;
			value += attn1 * attn1 * extrapolate(xsb + 1, ysb + 0, dx1, dy1);
		}

		double dx2 = dx0 - 0 - SQUISH_CONSTANT_2D;
		double dy2 = dy0 - 1 - SQUISH_CONSTANT_2D;
		double attn2 = 2 - dx2 * dx2 - dy2 * dy2;
		if (attn2 > 0) {
			attn2 *= attn2;
			value += attn2 * attn2 * extrapolate(xsb + 0, ysb + 1, dx2, dy2);
		}

		if (inSum <= 1) {
			double zins = 1 - inSum;
			if (zins > xins || zins > yins) {
				if (xins > yins) {
					xsv_ext = xsb + 1;
					ysv_ext = ysb - 1;
					dx_ext = dx0 - 1;
					dy_ext = dy0 + 1;
				} else {
					xsv_ext = xsb - 1;
					ysv_ext = ysb + 1;
					dx_ext = dx0 + 1;
					dy_ext = dy0 - 1;
				}
			} else {
				xsv_ext = xsb + 1;
				ysv_ext = ysb + 1;
				dx_ext = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
				dy_ext = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
			}
		} else {
			double zins = 2 - inSum;
			if (zins < xins || zins < yins) {
				if (xins > yins) {
					xsv_ext = xsb + 2;
					ysv_ext = ysb + 0;
					dx_ext = dx0 - 2 - 2 * SQUISH_CONSTANT_2D;
					dy_ext = dy0 + 0 - 2 * SQUISH_CONSTANT_2D;
				} else {
					xsv_ext = xsb + 0;
					ysv_ext = ysb + 2;
					dx_ext = dx0 + 0 - 2 * SQUISH_CONSTANT_2D;
					dy_ext = dy0 - 2 - 2 * SQUISH_CONSTANT_2D;
				}
			} else {
				dx_ext = dx0;
				dy_ext = dy0;
				xsv_ext = xsb;
				ysv_ext = ysb;
			}
			xsb += 1;
			ysb += 1;
			dx0 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
			dy0 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
		}

		double attn0 = 2 - dx0 * dx0 - dy0 * dy0;
		if (attn0 > 0) {
			attn0 *= attn0;
			value += attn0 * attn0 * extrapolate(xsb, ysb, dx0, dy0);
		}

		double attn_ext = 2 - dx_ext * dx_ext - dy_ext * dy_ext;
		if (attn_ext > 0) {
			attn_ext *= attn_ext;
			value += attn_ext * attn_ext * extrapolate(xsv_ext, ysv_ext, dx_ext, dy_ext);
		}

		return value / NORM_CONSTANT_2D;
	}

	private double extrapolate(int xsb, int ysb, double dx, double dy) {
		int index = perm[(perm[xsb & 0xFF] + ysb) & 0xFF] & 0x0E;
		return gradients2D[index] * dx + gradients2D[index + 1] * dy;
	}

	private static int fastFloor(double x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}
}
