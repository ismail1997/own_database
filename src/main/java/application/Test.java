package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utils.CryptoUtils;

public class Test {

	public static void main(String[] args) throws Exception {
		
		/*
		 * String db="jee";
		 * 
		 * 
		 * String al
		 * ="             create                database         jee        ;       ";
		 * al = al.replaceAll("\\s+"," ").trim(); System.out.println(al.trim()); String
		 * [] myArray=al.split(" ");
		 * 
		 * System.out.println(myArray.length);
		 */
		
		List<String> listOfDb = getDatabases();
		listOfDb.forEach(System.out::println);
//		removeDatabase(listOfDb,db);
//		listOfDb.forEach(System.out::println);
	 

	      
	   

	}
	
	public  static List<String> getDatabases() throws Exception{
		Scanner scanner = new Scanner(new File("databases.jbs"));
		
		List<String> listOfDb = new ArrayList<String>();
		
		while (scanner.hasNextLine()) {
	        String data = scanner.nextLine();
	        listOfDb.add(CryptoUtils.decryptedData(data));
	    }
		scanner.close();
		return listOfDb;
	}
	
	public static void removeDatabase(List<String> listOfDatabase,String db) throws Exception {
		if(!listOfDatabase.contains(db)) {
			System.out.format("can't drop database %s, database dosen't exist",db);System.out.println("");return;
			
		}
		List<String> newListOfDatabase = new ArrayList<String>();
		listOfDatabase.remove(db);
		//add the new record the list 
		for(int i =0 ; i< listOfDatabase.size(); i++) {
			newListOfDatabase.add(listOfDatabase.get(i));
		}
		
		//clear the file 
		BufferedWriter clearBuffer= new BufferedWriter(new FileWriter(new File("databases.jbs")));
		clearBuffer.write("");
		clearBuffer.close();
		
		//save the new list to the file 
		BufferedWriter bw =new BufferedWriter(new FileWriter(new File("databases.jbs"),true));
		for(String str : newListOfDatabase) {
			bw.write(CryptoUtils.encryptData(str));
			bw.newLine();
		}
		bw.close();
		System.out.println("database removed successfully");
	}

}
