package com.cs544.finalproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class GeneralUtils {
	public static String readFile(String pathname) {

	    File file = new File(pathname);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = null;
	    String lineSeparator = System.getProperty("line.separator");

	    try {
	    	scanner = new Scanner(file);
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        
	    } catch(Exception e){
	    	e.printStackTrace();
	    }
	    finally {
	        scanner.close();
	    }
	    return fileContents.toString();
	}
	
	public static void writeToFile(String text, String path) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			out.write(text);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
