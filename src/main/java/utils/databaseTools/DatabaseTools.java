package utils.databaseTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;

import models.Database;
import models.Table;
import utils.Constants;
import utils.CryptoUtils;
import utils.Tools;

public class DatabaseTools {

	

	public static void dropDatabase(String databaseName) throws Exception {
		removeDatabase( databaseName);
	}

	public static boolean checkIfDatabaseNameExist(String dbName) throws Exception {
		List<Database> listOfDbNames = DatabaseTools.getListOfDatabases();
		for(int i = 0 ; i< listOfDbNames.size();i++) {
			if(listOfDbNames.get(i).getDatabaseName().equals(dbName)) {
				return true;
			}
		}
		return false;
	}

	public static void removeDatabase( String db) throws Exception {
		
		List<Database> listOfDatabase = getListOfDatabases();
		
		int exist = 0 ;
		for(int k = 0 ; k<listOfDatabase.size();k++) {
			if(!listOfDatabase.get(k).getDatabaseName().equals(db)) {
				exist++;
			}
		}
		
		if (exist == listOfDatabase.size()) {
			System.out.format("can't drop database %s, database dosen't exist", db);
			System.out.println("");
			return;

		}
		
		//remove the database from list and clear the file
		List<Database> newRecordDatabase = new ArrayList<Database> ();
		for(int i = 0 ; i< listOfDatabase.size() ; i++) {
			if(!listOfDatabase.get(i).getDatabaseName().equals(db)) {
				newRecordDatabase.add(listOfDatabase.get(i));
			}
		}

		// clear the file
		BufferedWriter clearBuffer = new BufferedWriter(new FileWriter(new File(Constants.DATABASE_FILE)));
		clearBuffer.write("");
		clearBuffer.close();

		// save the new list to the file
		
		for (Database database : newRecordDatabase) {
			createDatabase(database);
		}
		
		System.out.println("database removed successfully");
	}



	/**
	 * 
	 * @throws Exception
	 */
	public static void showDatabases() throws Exception {
		int sizeOfStringFormat=0;
		if(getListOfDatabases().size()==0) {
			sizeOfStringFormat=0;
		}else {
			OptionalInt oi = getListOfDatabases().stream()
	                .map(Database::getDatabaseName)
	                .mapToInt(String::length)
	                .max();
			
			sizeOfStringFormat=oi.getAsInt();
		}
		
		
		sizeOfStringFormat = sizeOfStringFormat < "Database".length() ? "Database".length() : sizeOfStringFormat;
		String format = "| %-" + sizeOfStringFormat + "s |%n";
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		System.out.println("| Database" + Tools.repeatedString(' ', sizeOfStringFormat - "Database".length()) + " |");
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		for (Database database : getListOfDatabases()) {
			System.out.format(format, database.getDatabaseName());
		}
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		System.out.println(getListOfDatabases().size() + " rows in set");
	}



	public static boolean createDatabase(Database database) throws Exception {
		String formatOfSavingDatabase = database.getDatabaseName() + "@";
		for (int i = 0; i < database.getListOfTables().size(); i++) {
			formatOfSavingDatabase = formatOfSavingDatabase + database.getListOfTables().get(i) + ",";
		}
		String encodedDataBaseName = CryptoUtils.encryptData(formatOfSavingDatabase);
		if (encodedDataBaseName.equals(null) || encodedDataBaseName.equals(""))
			return false;
		Tools.writeToFile(encodedDataBaseName, Constants.DATABASE_FILE);
		return true;
	}
	
	
	public static Database splitData(String data) {
		if(data.equals("") || data.equals(null)) return null;
		
		// remove the last ',' from the string
		data = data.substring(0,data.length()-1);
		
		//split the string into database and its tables 
		String [] databaseSplit = data.split("@");
		
		//create Database object and Tables object
		Database database = new Database();
		database.setDatabaseName(databaseSplit[0]);
		
		//split the databaseSplit string to extract tables 
		if(databaseSplit.length<=1) return database;
		String [] tableSplit = databaseSplit[1].split(",");
		
		
		
		//create table and add them to list of tables 
		List<String> listOfTables = new ArrayList<>();
		for(int r = 0; r<tableSplit.length ; r++) {
			listOfTables.add(tableSplit[r]);
		}
		
		//assign table list to databasese object
		database.setListOfTables(listOfTables);
		
		
		return database;
	}
	public static List<Database> getListOfDatabases() throws Exception {
		
		//get the file in which databases are stored
		File myObj = new File(Constants.DATABASE_FILE);
		if (!myObj.exists())
			return Collections.emptyList();
		
		//define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);
		
		//read the data from file and pass it to the split method 
		//and finally store it to the list of the databases
		List<Database> listOfDb = new ArrayList<>();
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			Database database = splitData(CryptoUtils.decryptedData(data));
			if(database !=null )listOfDb.add(database);
		}
		return listOfDb;
	}
	/**
	 * 
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public static Database getDatabase(String databaseName) throws Exception {
		Database database = new Database();
		for(int i = 0 ; i<getListOfDatabases().size() ; i++) {
			database = getListOfDatabases().get(i);
			if(database.getDatabaseName().equals(databaseName)) {
				return database;
			}
		}
		
		return null;
	}
	
	public static void showTablesOfDatabase(String dbName) throws Exception {
		Database database = getDatabase(dbName);
		if(database == null) return ;
		
		List<String> listOfTables = database.getListOfTables();
		int sizeOfStringFormat=0;
		if(listOfTables.size()==0) {
			sizeOfStringFormat =0;
		}else {
			OptionalInt oi = listOfTables.stream()
	                .mapToInt(String::length)
	                .max();
			sizeOfStringFormat=oi.getAsInt();
		}
		
		sizeOfStringFormat = sizeOfStringFormat < dbName.length() ? dbName.length() : sizeOfStringFormat;
		String format = "| %-" + sizeOfStringFormat + "s |%n";
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		System.out.println("| "+dbName + Tools.repeatedString(' ', sizeOfStringFormat - dbName.length()) + " |");
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		for (String string : listOfTables) {
			System.out.format(format, string);
		}
		System.out.println("+-" + Tools.repeatedString('-', sizeOfStringFormat) + "-+");
		System.out.println(listOfTables.size() + " rows in set");
	}
	
	/**
	 * 
	 * @param databaseName
	 * @param tableNameToAdd
	 * @return
	 * @throws Exception
	 */
	public static boolean addTableDatabase(String databaseName, String tableNameToAdd) throws Exception {
		Database oldDatabase = getDatabase(databaseName);
		Database newDatabase = new Database();
		
		if(oldDatabase == null) {
			System.out.println("database dosen't exist");
			return false;
		}
		
		//store the list of tables of the given database and add the new table
		List<String> tablesOfGivenDatabase = oldDatabase.getListOfTables();
		//check if table already exist or not 
		if(tablesOfGivenDatabase.contains(tableNameToAdd)) {
			System.out.format("table '%s' is already exist%n",tableNameToAdd);return false;
		}
		if(tableNameToAdd.trim().equals("")) {
			System.out.println("invalid name of table");return false;
		}
		tablesOfGivenDatabase.add(tableNameToAdd);
		
		//update the new database 
		newDatabase.setDatabaseName(oldDatabase.getDatabaseName());
		newDatabase.setListOfTables(tablesOfGivenDatabase);
		
		///remove the oldest database  from list and add the new one
		List<Database> listOfDatabase = new ArrayList<Database>();
		for(int i= 0 ; i<getListOfDatabases().size() ; i++) {
			if(getListOfDatabases().get(i).getDatabaseName().equals(oldDatabase.getDatabaseName())) {
				listOfDatabase.add(newDatabase);
			}else {
				listOfDatabase.add(getListOfDatabases().get(i));
			}
		}
		//clear the database file 
		BufferedWriter clearBuffer = new BufferedWriter(new FileWriter(new File(Constants.DATABASE_FILE)));
		clearBuffer.write("");
		clearBuffer.close();
		
		// save the new list to the file
		for(Database db : listOfDatabase) {
			createDatabase(db);
		}
		
		return true;
	}
	

	
	public static void main(String[] args) throws Exception {
		 //showDatabases();
		// dropDatabase("http");
		//showDatabases();
		//System.out.println(getDatabase("users"));
		showTablesOfDatabase("users");
		//addTableDatabase("users", "usex");
		
		
		//getListOfDatabases().forEach(System.out::println);
	//	Database database = new Database();
	//	database.setDatabaseName("roles");
//		Table table = new Table();
//		table.setTableName("persons");
//		Table table1 = new Table();
//		table1.setTableName("locations");
//		database.setListOfTables(Arrays.asList("http","nothing"));
	//	createDatabase(database);
		/*
		 * //save the db to file with no tables first int tablesNumbers
		 * =database.getListOfTables().size(); String
		 * formatOfSavingDatabase=database.getDatabaseName()+"@"; for(int i= 0;
		 * i<database.getListOfTables().size() ; i++) {
		 * formatOfSavingDatabase=formatOfSavingDatabase+database.getListOfTables().get(
		 * i).getTableName()+","; } System.out.println("before creating tables");
		 * System.out.println(formatOfSavingDatabase); //add tables to database Table
		 * table = new Table();table.setTableName("persons"); Table table1 = new
		 * Table(); table1.setTableName("locations");
		 * 
		 * database.setListOfTables(Arrays.asList(table,table1));
		 * 
		 * for(int i= 0; i<database.getListOfTables().size() ; i++) {
		 * formatOfSavingDatabase=formatOfSavingDatabase+database.getListOfTables().get(
		 * i).getTableName()+","; }
		 * 
		 * System.out.println("after creating tables");
		 * System.out.println(formatOfSavingDatabase);
		 */
		
		//List<Database> list=getListOfDatabases();
	}
}
