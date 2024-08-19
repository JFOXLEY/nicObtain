package org.json.simple.generics;

import java.util.Map;

public class MapHolder {
	private Map<Object, Object> map;
	public MapHolder(Map<Object, Object> map) {
		this.map = map;
	}
	public Map<Object, Object> map() {
		return map;
	}
}
