package com.neoproductionco.paperar;

/**
 * Created by Neo on 18.06.2017.
 */

public class ScenarioStep {
	private String name;
	private Color color;

	public ScenarioStep(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public static class Color{
		public int r, g, b;

		public Color(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
}
