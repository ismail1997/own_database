package own_database.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import own_database.utils.databaseTools.DatabaseTools;

public class Tools {

	public static void writeToFile(String data , String fileName) throws Exception {
		File fout = new File(fileName+".jbs");
		
		FileWriter fw = new FileWriter(fout,true);
		
		BufferedWriter bw = new BufferedWriter(fw);
	 
		bw.write(data);
		bw.newLine();
	 
		bw.close();
	}
	
	//create string from repeated chars 
	public static String repeatedString(char c , int n) {
		char[] chars = new char[n];
		Arrays.fill(chars, c);
		return new String(chars);
	}
}
