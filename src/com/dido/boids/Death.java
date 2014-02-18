package com.dido.boids;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Death {
	PApplet parent;
	ArrayList<ArrayList<PVector>> life;

	ExportExcel exporter;

	Death(PApplet p) {
		parent = p;
		life = new ArrayList<ArrayList<PVector>>();
		exporter = new ExportExcel();
	}

	public void leavetrace(ArrayList<PVector> trace) {
		life.add(trace);
	}

	void showtrace() {
		parent.fill(0, 0, 10, 10);
		parent.noStroke();
		for (ArrayList<PVector> trace : life) {
			for (PVector tr : trace) {
				parent.fill(0);
				parent.ellipse(tr.x, tr.y, 1, 1);
			}
		}
	}

	public void commit() {
		if (this.life.size() > 0) {
			System.out.println(true);
			for (ArrayList<PVector> aList : this.life) {
				exporter.push(aList);
				exporter.saveString();
			}
		}

	}
}
