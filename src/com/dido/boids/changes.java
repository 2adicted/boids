package com.dido.boids;

import processing.core.*;

import java.util.*;

public class changes extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrayList<Zone> zones;
	public ArrayList<Agent> agents;

	public FlowField map;
	public Boundary b;
	public Death death;

	boolean debug = false;
	boolean trace = false;
	boolean grand = false;

	public int counter = 0;

	public void setup() {
		size(500, 250);
		smooth();
		zones = new ArrayList<Zone>();
		agents = new ArrayList<Agent>();
		map = new FlowField(this);
		b = new Boundary(this, 8);
		death = new Death(this);
		for (int i = 0; i < 10; i++)
			zones.add(new Zone(this,
					new PVector(random(width), random(height)),
					(random(10, 100))));
	}

	public void draw() {
		background(220);
		map.update();
		map.display();

		Iterator<Agent> it = agents.iterator();

		while (it.hasNext()) {
			Agent a = it.next();
			a.run(agents, zones);
			if (frameCount % 5 == 0)
				a.tracepath();
			if (trace)
				a.displaypath();
			if (a.isDead()) {
				it.remove();

				println("here");
			}
			if (a.natural()) {
				death.leavetrace(new ArrayList<PVector>(a.trace));
				it.remove();
			}
		}
		if (grand) {
			death.showtrace();
		}
		b.draw();
	}


	public void mouseReleased() {
		if (mouseButton == LEFT) {
			zones.add(new Zone(this, new PVector(mouseX, mouseY), (random(100))));
		}
		if (mouseButton == RIGHT) {
			for (int i = 0; i < 20; i++)
				agents.add(new Agent(this, new PVector(width / 2, height)));
		}
	}

	public void keyPressed() {

		if (key == 'c') {
			counter++;
		}

		if (key == 's' || key == 'S') {
			saveFrame("state-##.tif");
		}

		if (key == ' ') {
			zones = new ArrayList<Zone>();
			for (int i = 0; i < 10; i++)
				zones.add(new Zone(this, new PVector(random(width),
						random(height)), random(10, 100)));
			debug = !debug;
		}
		if (key == 't' || key == 'T') {
			trace = !trace;
		}
		if (key == 'g' || key == 'G') {
			grand = !grand;
		}
	}
}
