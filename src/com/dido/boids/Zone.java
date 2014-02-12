package com.dido.boids;

import processing.core.PVector;

public class Zone {
	PVector origin;
	int magnitude;

	Zone(PVector o, int m) {
		origin = o.get();
		magnitude = m;
	}

	Zone(PVector o) {
		origin = o.get();
	}

	void mag(int m) {
		magnitude = m;
	}

	void display() {
		fill(250, 120);
		ellipse(origin.x, origin.y, magnitude * 2, magnitude * 2);
	}
}
