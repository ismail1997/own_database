package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import initpackage.FirstInit;
import models.Database;
import utils.Constants;
import utils.CryptoUtils;
import utils.Tools;

public class Test {

	public static void main(String[] args) throws Exception {
		
		
		String databaseFile=FirstInit.USER_HOME_DIRECTORY+"/"+FirstInit.DB_FILE_NAME+"/"+ Constants.DATABASE_FILE;
		String currentFile ="";
		String tableFile ="C:\\Users\\pc\\myOwnDB\\tb.owndb";
		File myObj = new File(tableFile);
		
		
		//define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);
		ArrayList<String> saveOtherTables=new ArrayList<>() ;
		
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			String decryptedData=(CryptoUtils.decryptedData(data));
			System.out.println(decryptedData);
			
			String trimedDatabaseName= decryptedData.substring
						(decryptedData.indexOf("database=")+"database=".length(),decryptedData.indexOf(", numberOfColumns"));
			System.out.println(trimedDatabaseName);
			
			if(!trimedDatabaseName.equalsIgnoreCase("spotify")){
				saveOtherTables.add(decryptedData);
			}
		}
		
		myReader.close();
		//now we should delete the content of file 
//		// clear the file
//		BufferedWriter clearBuffer = new BufferedWriter(new FileWriter(new File(tableFile)));
//		clearBuffer.write("");
//		clearBuffer.close();
//		
//		//write the remaining tables to the file
//		for(String str : saveOtherTables) {
//			Tools.writeToFile(CryptoUtils.encryptData(str),tableFile);
//		}
//		
		
	
	 

	      
	   

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
