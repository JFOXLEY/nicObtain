package org.json.simple.generics;

import java.util.List;

public class ListHolder {
	private List<Object> list;
	public ListHolder(List<Object> list) {
		this.list = list;
	}
	public List<Object> list() {
		return list;
	}
}
