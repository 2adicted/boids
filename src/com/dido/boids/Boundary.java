package com.dido.boids;

import processing.core.PApplet;
import processing.core.PConstants;

public class Boundary {
	PApplet parent;
	int s_x;
	int e_x;
	int s_y;
	int e_y;

	Boundary(PApplet p, int offset) {
		parent = p;
		s_x = offset;
		s_y = offset;
		e_x = parent.width - offset;
		e_y = parent.height - offset;
	}

	void draw() {
		parent.rectMode(PConstants.CORNER);
		parent.strokeWeight(0.5f);
		parent.stroke(10, 50, 80);
		parent.noFill();
		parent.rect(s_x, s_y, e_x - s_x, e_y - s_y);
	}
}
