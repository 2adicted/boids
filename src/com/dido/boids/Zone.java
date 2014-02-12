package com.dido.boids;

import processing.core.PApplet;
import processing.core.PVector;

public class Zone {
	PApplet parent;
	PVector origin;
	int magnitude;

	Zone(PApplet p, PVector o, float f) {
		parent = p;
		origin = o.get();
		magnitude = (int) f;
	}

	Zone(PVector o) {
		origin = o.get();
	}

	void mag(int m) {
		magnitude = m;
	}

	void display() {
		parent.fill(250, 120);
		parent.ellipse(origin.x, origin.y, magnitude * 2, magnitude * 2);
	}
}
