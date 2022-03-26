package own_database.utils.databaseTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import own_database.utils.CryptoUtils;
import own_database.utils.Tools;

public class DatabaseTools {
	
	public static boolean createDatabase(String dataBaseName) throws Exception {
		String encodedDataBaseName = CryptoUtils.encryptData(dataBaseName);
		if(encodedDataBaseName.equals(null) || encodedDataBaseName.equals("")) return false;
		Tools.writeToFile(encodedDataBaseName,"databases");
		return true;
	}
	
	public  static void dropDatabase(String databaseName) throws Exception {
		removeDatabase(getListOfDatabase(), databaseName);
	}
	
	public static boolean checkIfDatabaseNameExist(String dbName) throws Exception {
		List<String> listOfDbNames = DatabaseTools.getListOfDatabase ();
		if(listOfDbNames.contains(dbName)) return true;
		return false;
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
	
	public static List<String> getListOfDatabase () throws Exception{
	      File myObj = new File("databases.jbs");
	      if(!myObj.exists()) return Collections.emptyList();
	      List<String> listOfDb = new ArrayList<String>();
	      Scanner myReader = new Scanner(myObj);
	      while (myReader.hasNextLine()) {
	        String data = myReader.nextLine();
	        listOfDb.add(CryptoUtils.decryptedData(data));
	      }
	      myReader.close();

	return listOfDb;
}
	public static void showDatabases() throws Exception {
		Optional<String> maxString  = getListOfDatabase ().stream().max(Comparator.comparingInt(String::length));
		int sizeOfStringFormat =maxString.get().length();
		sizeOfStringFormat = sizeOfStringFormat < "Database".length() ? "Database".length() : sizeOfStringFormat ;
		String format ="| %-"+sizeOfStringFormat+"s |%n";
		System.out.println("+-"+Tools.repeatedString('-', sizeOfStringFormat)+"-+");
		System.out.println("| Database"+Tools.repeatedString(' ', sizeOfStringFormat-"Database".length())+" |");
		System.out.println("+-"+Tools.repeatedString('-', sizeOfStringFormat)+"-+");
		for(String str : getListOfDatabase ()) {
			 System.out.format(format, str);
		}
		System.out.println("+-"+Tools.repeatedString('-', sizeOfStringFormat)+"-+");
		System.out.println(getListOfDatabase ().size()+" rows in set");
	}
	
	public static void main(String[] args) throws Exception {
		showDatabases();
	}
}
