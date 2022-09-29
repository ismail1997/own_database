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
		
		
		File folder = new File(FirstInit.USER_HOME_DIRECTORY+"/"+FirstInit.DB_FILE_NAME+"/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        
		        
		        String fileName = file.getName();
		       fileName= fileName.replace("_", "@").replace(".","@");
		       System.out.println(fileName);
		       String flTB [] = fileName.split("@");
		       ArrayList<String> myArraylist = new ArrayList<String>() ;
		       for(String str : flTB) {
		    	   myArraylist.add(str);
		       }
		       
		       if(myArraylist.contains("spotify")) {
		    	   if(file.delete())                      //returns Boolean value  
		    	   {  
		    		   System.out.println(file.getName() + " deleted");   //getting and printing the file name  
		    	   }  
		       }
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
