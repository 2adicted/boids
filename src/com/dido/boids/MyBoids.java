package com.dido.boids;

import processing.core.*;

import java.util.*;

public class MyBoids extends PApplet {

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

	int counter = 0;

	private float z_pos;

	private float x_rot;

	int num_floors = 2;
	private int[] level = new int[num_floors];


	int level_height = 100;

	public void setup() {
		size(800, 500, OPENGL);
		lights();
		smooth();
		zones = new ArrayList<Zone>();
		agents = new ArrayList<Agent>();
		map = new FlowField(this);
		b = new Boundary(this, 8);
		death = new Death(this);
		
		for (int i = 0; i < num_floors; i++) {
			level[i] = i * level_height;
		}

		for (int i = 0; i < 10; i++)
			zones.add(new Zone(this,
					new PVector(random(width), random(height),level[(int) (random(0, num_floors))]),
					(random(10, 100)))); 
	}

	public void draw() {
		background(220);
		translate((float) (0.2 * width), -height / 3, -500 + z_pos);
		rotateX(PI / 4 + radians(x_rot));
		rotateZ(PI / 6);

		map.update();
		map.display();
		
		for(Zone z : zones){
			z.display();
		}
		
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
			if (keyPressed == true && keyCode == SHIFT)
				zones.add(new Zone(this, new PVector(mouseX, mouseY),
						(random(100))));
		}
		if (mouseButton == RIGHT) {
			for (int i = 0; i < 20; i++)
				agents.add(new Agent(this, new PVector(width / 2, height)));
		}
	}

	public void mouseDragged() {
		if (keyPressed == true && keyCode == ALT) {
			z_pos -= mouseY - pmouseY;
		} else if (keyPressed == true && keyCode == CONTROL) {
			x_rot += mouseY - pmouseY;
		}

	}

	public void keyPressed() {

		if (key == 'c') {
			death.commit();
		}

		if (key == 's' || key == 'S') {
			saveFrame("state-##.tif");
		}

		if (key == ' ') {
			zones = new ArrayList<Zone>();
			for (int i = 0; i < 10; i++)
				zones.add(new Zone(this,
						new PVector(random(width), random(height),level[(int) (random(0, num_floors))]),
						(random(10, 100)))); 
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
