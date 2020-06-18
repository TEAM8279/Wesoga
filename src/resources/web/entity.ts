class Entity {
	public model: number;

	public uid: number;

	public x: number;
	public y: number;
	public z: number;

	public aX: number;
	public aY: number;
	public aZ: number;

	public rot: number;

	constructor(model: number, uid: number, x: number, y: number, z: number, rot: number) {
		this.model = model;

		this.uid = uid;

		this.x = x;
		this.y = y;
		this.z = z;

		this.aX = x;
		this.aY = y;
		this.aZ = z;

		this.rot = rot;
	}
}
