package com.dido.boids;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PVector;

public class Agent {
	MyBoids parent;

	int age;
	int lifeExpectancy;
	PVector location;
	PVector velocity;
	PVector acceleration;
	float r;
	float wandertheta;
	double maxforce; // Maximum steering force
	float maxspeed; // Maximum speed
	boolean hoover;
	double c; // Drag co
	int[] time;
	ArrayList<PVector> trace;
	boolean[][] check;
	float lifespan;
	int previousTime;
	ArrayList<Pointer> trace_list;

	Agent(MyBoids p, PVector l) {
		parent = p;
		age = 0;
		lifeExpectancy = 100;
		location = l.get();
		r = 1.5f;
		maxspeed = 2;
		maxforce = 0.5;
		acceleration = new PVector(0, 0);
		velocity = new PVector(0, 0);
		wandertheta = 0;
		c = 0.2;
		trace = new ArrayList<PVector>();
		PVector tr = location.get();
		trace.add(tr);
		lifespan = 100;
		trace_list = new ArrayList<Pointer>();
	}

	public void run(ArrayList<Agent> agents, ArrayList<Zone> zones) {
		monitor();
		behave(agents);
		wander();
		update();
		borders();
		display();
		getold();
	}

	void update() {
		// Update velocity
		velocity.add(acceleration);
		// Limit speed
		velocity.limit(maxspeed);
		location.add(velocity);
		// Reset acceleration to 0 each cycle
		acceleration.mult(0);
	}

	void display() {
		parent.fill(0, lifespan);
		parent.stroke(0, lifespan);
		parent.strokeWeight((float) 0.5);
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.ellipse(0, 0, 2 * r, 2 * r);
		parent.popMatrix();
	}

	void monitor() {
		for (int i = 0; i < parent.zones.size(); ++i) {
			if (hoover(parent.zones.get(i).origin,
					parent.zones.get(i).magnitude)) {
				trace_list
						.add(new Pointer(parent.zones.get(i), parent.millis()));
			}
		}

		Iterator<Pointer> pt = trace_list.iterator();

		while (pt.hasNext()) {
			Pointer a = pt.next();
			if (a.time + 5000 > parent.millis()) {
				follow();
			} else {
				unfollow();
			}
			if (a.time + 10000 < parent.millis()) {
				follow();
				pt.remove();
			}
		}
	}

	void behave(ArrayList<Agent> agents) {
		PVector sep = separate(agents);
		PVector ali = align(agents);
		PVector coh = cohesion(agents);
		sep.mult(0.5f);
		ali.mult(0.05f);
		coh.mult(0.05f);
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	void applyForce(PVector force) {
		acceleration.add(force);
	}

	void follow() {
		PVector desired = parent.map.lookup(location);
		desired.mult(5.0f);
		PVector steer = PVector.sub(desired, velocity);
		PVector dragForce = liquify();
		applyForce(dragForce);
		applyForce(steer);
	}

	void unfollow() {
		PVector desired = parent.map.lookup(location);
		desired.mult(-15.0f);
		PVector steer = PVector.sub(desired, velocity);
		PVector dragForce = liquify();
		applyForce(dragForce);
		applyForce(steer);
	}

	void wander() {
		float wanderR = 15; // Radius for our "wander circle"
		float wanderD = 80; // Distance for our "wander circle"
		float change = 20.3f;
		wandertheta += parent.random(-change, change); // Randomly change wander
														// theta

		// Now we have to calculate the new location to steer towards on the
		// wander circle
		PVector circleloc = velocity.get(); // Start with velocity
		circleloc.normalize(); // Normalize to get heading
		circleloc.mult(wanderD); // Multiply by distance
		circleloc.add(location); // Make it relative to boid's location

		@SuppressWarnings("deprecation")
		float h = velocity.heading2D(); // We need to know the heading to offset
										// wandertheta

		PVector circleOffSet = new PVector(
				(float) (wanderR * Math.cos(wandertheta + h)),
				(float) (wanderR * Math.sin(wandertheta + h)));
		PVector target = PVector.add(circleloc, circleOffSet);
		PVector locomotion = seek(target);
		applyForce(locomotion);
	}

	void tracepath() {
		PVector tr = location.get();
		trace.add(tr);
	}

	void displaypath() {
		parent.fill(0);
		parent.noStroke();
		for (PVector tr : trace) {
			parent.ellipse(tr.x, tr.y, 1, 1);
		}
	}

	PVector previousLocation = new PVector(1000, 1000);

	void isStuck() {
		PVector currentLocation = location.get();
		int currentTime = parent.millis();
		if (currentTime - previousTime > 15000) {
			previousTime = currentTime;
			previousLocation = currentLocation.get();
		}
		if (currentLocation.dist(previousLocation) < 10) {
			lifespan -= 0.2;
		}
	}

	boolean isDead() {
		isStuck();
		if (lifespan < 0.0) {
			// println("is dead");
			return true;
		} else {
			return false;
		}
	}

	boolean natural() {
		if (age > lifeExpectancy) {
			return true;
		} else {
			return false;
		}
	}

	void runcheck() {
		int ct = 0;
		for (Zone z : parent.zones) {
			if (hoover(z.origin, z.magnitude)) {
				if (check[0][ct]) {
					check[0][ct] = false;
					time[ct] = parent.millis();
				}
			}
			if (!check[0][ct]) {
				if (time[ct] + 4000 < parent.millis()) {
					check[1][ct] = false; // ban
				}
				if (time[ct] + 15000 < parent.millis()) {
					check[0][ct] = true;
					check[1][ct] = true; // unban
				}
			}
		}
	}

	// Separation
	// Method checks for nearby boids and steers away
	PVector separate(ArrayList<Agent> agents) {
		float desiredseparation = 10.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (Agent other : agents) {
			float d = PVector.dist(location, other.location);
			// If the distance is greater than 0 and less than an arbitrary
			// amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(location, other.location);
				diff.normalize();
				diff.div(d); // Weight by distance
				steer.add(diff);
				count++; // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit((float) maxforce);
		}
		return steer;
	}

	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	PVector align(ArrayList<Agent> agents) {
		float neighbordist = 20;
		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Agent other : agents) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.velocity);
				count++;
			}
		}
		if (count > 0) {
			sum.div((float) count);
			sum.normalize();
			sum.mult(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit((float) maxforce);
			return steer;
		} else {
			return new PVector(0, 0);
		}
	}

	// Cohesion
	// For the average location (i.e. center) of all nearby boids, calculate
	// steering vector towards that location
	PVector cohesion(ArrayList<Agent> agents) {
		float neighbordist = 20;
		PVector sum = new PVector(0, 0); // Start with empty vector to
											// accumulate all locations
		int count = 0;
		for (Agent other : agents) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.location); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return seek(sum); // Steer towards the location
		} else {
			return new PVector(0, 0);
		}
	}

	PVector seek(PVector target) {
		PVector desired = PVector.sub(target, location);
		desired.normalize();
		PVector steer = PVector.sub(desired, velocity);
		return steer;
	}

	PVector liquify() {
		float speed = this.velocity.mag();
		float dragMagnitude = (float) (c * speed * speed);
		PVector dragForce = this.velocity.get();
		dragForce.mult(-1);
		dragForce.normalize();
		dragForce.mult(dragMagnitude);
		return dragForce;
	}

	boolean hoover(PVector origin, int radius) {
		if (location.x > (origin.x - radius / 2)
				&& location.x < (origin.x + radius / 2)) {
			if (location.y > (origin.y - radius / 2)
					&& location.y < (origin.y + radius / 2)) {
				hoover = true;
			}
		} else {
			hoover = false;
		}
		return hoover;
	}

	void getold() {
		if (parent.frameCount % 10 == 0) {
			age++;
		}
	}

	void borders() {
		if (location.x - r < parent.b.s_x && velocity.x < 0) {
			velocity.x = -velocity.x;
		}
		if (location.y - r < parent.b.s_y && velocity.y < 0) {
			velocity.y = -velocity.y;
		}
		if (location.x + r > parent.b.e_x && velocity.x > 0) {
			velocity.x = -velocity.x;
		}
		if (location.y + r > parent.b.e_y && velocity.y > 0) {
			velocity.y = -velocity.y;
		}
	}

	class Pointer {
		Zone my_zone;
		long time;

		Pointer(Zone z, long t) {
			my_zone = z;
			time = t;
		}
	}
}
