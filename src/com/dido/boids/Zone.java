package com.dido.boids;

import controlP5.Println;
import processing.core.PApplet;
import processing.core.PVector;

public class Zone {
	PApplet parent;
	PVector origin;
	int magnitude;
	int level;

	Zone(PApplet p, PVector o, float m) {
		parent = p;
		origin = o.get();
		magnitude = (int) m;
	}

	Zone(PVector o) {
		origin = o.get();
	}

	void mag(int m) {
		magnitude = m;
	}

	void display() {
		parent.fill(250, 120);
		parent.pushMatrix();
		parent.translate(origin.x, origin.y,origin.z);
		parent.ellipse(0, 0, magnitude * 2, magnitude * 2);
		parent.popMatrix();
	}
}
