package com.dido.boids;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import processing.core.PVector;

public class DeathTest {

	PVector test_vector = new PVector(1, 2, 3);
	ArrayList<PVector> test_list = new ArrayList<PVector>();

	@Test
	public void testDeathCommit() {
		Death d = new Death(null);
		for (int i = 0; i < 10; i++) {
			test_list.add(test_vector);
		}
		for (int i = 0; i < 10; i++) {
			d.leavetrace(test_list);
		}
		d.commit();
	}
}
