namespace EntityModels {
	let models: EntityModel[] = [];

	export function add(model: EntityModel) {
		models.push(model);
	}

	export function count() {
		return models.length;
	}

	export function get(id: number) {
		return models[id];
	}
}
