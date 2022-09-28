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
		
		ArrayList<String> list = new ArrayList<String>() ;
	      list.add("1");list.add("2");list.add("3");list.add("4");list.add("5");
	      list.add("6");list.add("7");list.add("8");list.add("9");list.add("10");
	      list.add("11");list.add("12");list.add("13");list.add("14");list.add("15");
	      list.add("16");list.add("17");list.add("18");list.add("19");list.add("20");
	      
	      ArrayList<ArrayList<String>> myList = new ArrayList<ArrayList<String>>();
	      int size = 5; 
	      
	      for(int i = 0 ; i <list.size() ; i = i+size){
	          for(int r = i ; r < size+i; r++){
	              System.out.println(list.get(r));
	          }
	      }
	 

	      
	   

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
