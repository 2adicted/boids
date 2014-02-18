package com.dido.boids;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.*;
import java.util.*;

import processing.core.*;
public class ExportExcel {

	List<PVector> incoming;
	HSSFWorkbook workbook;
	HSSFSheet worksheet;
	int num_rows;

	ExportExcel() {
		incoming = new ArrayList<PVector>();
		workbook = new HSSFWorkbook();
		worksheet = workbook.createSheet("Agent traces");
		num_rows = 0;
	}

	public List<PVector> getIncoming() {
		return incoming;
	}

	public void setIncoming(List<PVector> test_list) {
		this.incoming = test_list;

	}

	public PVector getIndexOfIncoming(int x) {
		return incoming.get(x);
	}

	public void addCell(PVector pv, int num_cell) {
		HSSFCell cell = workbook.getSheetAt(0).getRow(this.num_rows)
				.createCell(num_cell);
		cell.setCellValue(pv.x+","+pv.y+","+pv.z);
	}

	public void propagate() {
		@SuppressWarnings("unused")
		HSSFRow row = worksheet.createRow(num_rows);
	}

	public void populate(List<PVector> test_list) {
		int num_cells = 0;
		for (PVector pv : test_list) {
			addCell(pv, num_cells);
			num_cells++;
		}
	}

	public void push(List<PVector> test_list) {
		this.setIncoming(test_list);
		this.propagate();
		this.populate(test_list);
		num_rows++;
	}

	public void saveString() {
		try {
			FileOutputStream fileOut = new FileOutputStream("traces.xls");
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
