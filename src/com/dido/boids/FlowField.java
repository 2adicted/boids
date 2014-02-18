package com.dido.boids;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class FlowField {
	MyBoids parent;
	PVector[][] field;
	PVector[][] location;
	PVector origin;
	int cols, rows;
	int resolution;
	int cell;
	int n_min;
	float zoff = 0.0f;

	FlowField(MyBoids p) {
		parent = p;
		n_min = 6;
		for (int i = n_min; i < 50; i++) {
			if ((parent.width % i == 0) && (parent.height % i == 0)) {
				cols = parent.width / i;
				rows = parent.height / i;
				break;
			}
		}
		cell = parent.width / cols;
		field = new PVector[cols][rows];
		location = new PVector[cols][rows];
		origin = new PVector(parent.width / 2, parent.height / 2);
		init();
	}

	private void init() {
		float x = 0.0f;
		float y = 0.0f;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				field[i][j] = new PVector(0, 0);
				x = (float) (i * cell + cell * 0.5);
				y = (float) (j * cell + cell * 0.5);
				location[i][j] = new PVector(x, y);
			}
		}
	}

	private void randomize() {
		float xoff = 0;
		for (int i = 0; i < cols; i++) {
			float yoff = 0;
			for (int j = 0; j < rows; j++) {
				float theta = PApplet.map(parent.noise(xoff, yoff, zoff), 0, 1,
						0, PConstants.TWO_PI);
				PVector rand = new PVector((float) Math.cos(theta),
						(float) Math.sin(theta));
				rand.mult((float) 0.2);
				field[i][j].add(rand);
				yoff += 0.1;
			}
			xoff += 0.1;
		}
		zoff += 0.01;
	}

	public void update() {
		if (parent.zones.size() > 0) {
			for (int i = 0; i < cols; i++) {
				for (int j = 0; j < rows; j++) {
					PVector carry = new PVector();
					for (Zone z : parent.zones) {
						PVector desired = PVector.sub(new PVector(i * cell, j
								* cell), z.origin);
						float dis = z.magnitude - desired.mag();
						if (dis > 0) {
							dis = PApplet.map(dis, 0, z.magnitude, 2, 5);
							desired.normalize();
							desired.mult(-dis);
							carry.add(desired);
						}
					}
					field[i][j] = carry;
				}
			}
		}
		randomize();
	}

	void display() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				drawVector(field[i][j], i, j);
			}
		}
	}

	@SuppressWarnings("deprecation")
	void drawVector(PVector v, int x, int y) {
		parent.pushMatrix();
		parent.translate(location[x][y].x, location[x][y].y);
		parent.stroke(20);
		parent.strokeWeight(0.4f);
		parent.rotate(v.heading2D());

		float len = v.mag();
		float arrowsize = len * 0.5f;

		parent.line(0, 0, len, 0);
		parent.line(len, 0, len - arrowsize, +arrowsize / 2);
		parent.line(len, 0, len - arrowsize, -arrowsize / 2);

		parent.popMatrix();
	}

	PVector lookup(PVector lookup) {
		float tempi = (float) (lookup.x - cell * 0.5) / cell;
		float tempj = (float) (lookup.y - cell * 0.5) / cell;
		int column = (int) PApplet.constrain(tempi, 0, cols - 1);
		int row = (int) (PApplet.constrain(tempj, 0, rows - 1));
		return field[column][row].get();
	}
}
