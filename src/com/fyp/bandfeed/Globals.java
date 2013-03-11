package com.fyp.bandfeed;

import java.util.ArrayList;

public class Globals {

	private static String BANDNAME = null;
	private static String GENRE1 = null;
	private static String GENRE2 = null;
	private static String GENRE3 = null;
	private static int NUMOFMEMBERS = 0;
	private static String COUNTY = null;
	private static String TOWN = null;
	private static ArrayList<String> MEMBERS = new ArrayList<String>();
	private static ArrayList<String> ROLES = new ArrayList<String>();
	private static String WEBPAGE = null;
	private static String SAMPLES = null;
	
	public static String getBANDNAME() {
		return BANDNAME;
	}
	public static void setBANDNAME(String bANDNAME) {
		BANDNAME = bANDNAME;
	}
	public static String getGENRE1() {
		return GENRE1;
	}
	public static void setGENRE1(String gENRE1) {
		GENRE1 = gENRE1;
	}
	public static String getGENRE2() {
		return GENRE2;
	}
	public static void setGENRE2(String gENRE2) {
		GENRE2 = gENRE2;
	}
	public static String getGENRE3() {
		return GENRE3;
	}
	public static void setGENRE3(String gENRE3) {
		GENRE3 = gENRE3;
	}
	public static int getNUMOFMEMBERS() {
		return NUMOFMEMBERS;
	}
	public static void setNUMOFMEMBERS(int nUMOFMEMBERS) {
		NUMOFMEMBERS = nUMOFMEMBERS;
	}
	public static String getCOUNTY() {
		return COUNTY;
	}
	public static void setCOUNTY(String cOUNTY) {
		COUNTY = cOUNTY;
	}
	public static String getTOWN() {
		return TOWN;
	}
	public static void setTOWN(String tOWN) {
		TOWN = tOWN;
	}
	public static ArrayList<String> getMEMBERS() {
		return MEMBERS;
	}
	public static void setMEMBERS(ArrayList<String> mEMBERS) {
		MEMBERS = mEMBERS;
	}
	public static ArrayList<String> getROLES() {
		return ROLES;
	}
	public static void setROLES(ArrayList<String> rOLES) {
		ROLES = rOLES;
	}
	public static String getWEBPAGE() {
		return WEBPAGE;
	}
	public static void setWEBPAGE(String wEBPAGE) {
		WEBPAGE = wEBPAGE;
	}
	public static String getSAMPLES() {
		return SAMPLES;
	}
	public static void setSAMPLES(String sAMPLES) {
		SAMPLES = sAMPLES;
	}
	
	
}
