package com.dido.boids;

import processing.core.PApplet;

public class Pointer {
	PApplet parent;
	Zone my_zone;
	long time;

	Pointer(PApplet p, Zone z, long t) {
		parent = p;
		my_zone = z;
		time = t;
	}
}
