package com.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author abhi
 */
public class StringHelper {

	public static String n2s(Object d) {
		String dual = "";
		if (d == null) {
			dual = "";
		} else
			dual = d.toString().trim();

		return dual;
	}

	public static boolean n2b(Object d) {
		boolean suc = false;
		if (d == null) {
			suc = false;
		} else
			suc = new Boolean(d.toString().trim()).booleanValue();

		return suc;
	}

	public static String n2s(String d) {
		String dual = "";
		if (d == null) {
			dual = "";
		} else
			dual = d.toString().trim();

		return dual;
	}

	public static List n2l(Object d) {
		List dual = null;
		if (d == null) {
			dual = new ArrayList();
		} else if (d instanceof List)
			dual = (List) d;

		return dual;
	}

	public static int n2i(Object d) {
		int dual = 0;
		if (d == null) {
			dual = 0;
		} else {

			dual = new Integer(d.toString().trim()).intValue();
		}
		return dual;
	}

	public static String nullObjectToStringEmpty(Object d) {
		String dual = "";
		if (d == null) {
			dual = "";
		} else
			dual = d.toString().trim();

		return dual;
	}

	public static float nullObjectToFloatEmpty(Object d) {
		float i = 0;
		if (d != null) {
			String dual = d.toString().trim();
			try {
				i = new Float(dual).floatValue();
			} catch (Exception e) {
				System.out.println("Unable to find integer value");
			}
		}
		return i;
	}

	public static void main(String args[]) {
		// new StringHelper().split("sadas:asdasd:asdas");

	}

}
