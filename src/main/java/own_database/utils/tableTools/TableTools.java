package own_database.utils.tableTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import own_database.models.Database;
import own_database.models.Table;
import own_database.utils.Constants;
import own_database.utils.CryptoUtils;
import own_database.utils.Tools;

public class TableTools {
	public static void descriptTable(Table table) {

	}

	public static void writeTableToFile(Table table) throws Exception {
		Tools.writeToFile(CryptoUtils.encryptData(table.toString()), Constants.TABLES_FILES);
	}

	public static boolean createTable(String statement,String databaseName) throws Exception {
		Table table = new Table();
		HashMap<String, String> mapOfFields = new HashMap<String, String>();

		String createTableString = statement;
		createTableString = createTableString.replaceAll("\\s+", " ");
		String array[] = createTableString.split("\\(");

		if (array.length != 2) {
			System.out.println("invalid create table statement, missing informations");
			return false;
		}

		String firstStatement = array[0].trim().toLowerCase();

		// validate the first statement
		String arrayOfFirstStm[] = firstStatement.split(" ");
		if (arrayOfFirstStm.length != 3) {
			System.out.println("invalid statement");
			return false;
		}

		// check if the the third string is not a reserved word
		if (Constants.reservedWords().contains(arrayOfFirstStm[2])) {
			System.out.format("invalid table name, '%s' is a reserved word %n", arrayOfFirstStm[2]);
			return false;
		}
		// check if table nambe contains or started with special characters or numbers
		if (Tools.checkIfStringContainsWithNumberOrChar(arrayOfFirstStm[2])) {
			System.out.format("invalid table name ,'%s' shouldn't contain anay special characters or numbers",
					arrayOfFirstStm[2]);
			return false;
		}
		String nameOfTable = arrayOfFirstStm[2];

		// assign the name to the table object
		table.setTableName(nameOfTable);
		table.setDatabase("");

		String secondStatement = array[1].trim().toLowerCase();

		// split the second statement to get fields and the end ;
		String splitSecondStatement[] = secondStatement.split("\\)");

		if (splitSecondStatement.length != 2) {
			if (splitSecondStatement[0].charAt(splitSecondStatement[0].length() - 1) == ';') {
				System.out.println("invalid statement missing ) at the end of statement ");
				return false;
			}
			if (splitSecondStatement.length == 1 && !splitSecondStatement[0].equals(";")) {
				System.out.println("invalid end of create table statement, missing ;");
				return false;
			}
			System.out.println("invalid create table statement");
			return false;

		}

		if (splitSecondStatement[0].trim().length() == 0) {
			System.out.println("invalid create table, missing arguments");
			return false;
		}
		if (!splitSecondStatement[1].trim().equals(";")) {
			System.out.println("invalid end of statement, missing ';' ");
			return false;
		}
		ArrayList<String> createdFields = new ArrayList<String>();
		// now we split the filed statement
		String fieldsStatement[] = splitSecondStatement[0].split(",");
		for (int r = 0; r < fieldsStatement.length; r++) {
			String fieldExtraction[] = fieldsStatement[r].trim().split(" ");
			if (fieldExtraction.length != 2) {
				System.out.println("invalid field inputs");
				return false;
			} else {
				String nameOfField = fieldExtraction[0];
				String typeOfField = fieldExtraction[1];

				if (Tools.checkIfStringContainsWithNumberOrChar(nameOfField)) {
					System.out.println("invalid field names, it contains special characters or started with numbers");
					return false;
				}

				// check if the field is not repeated in the table creation
				if (createdFields.contains(nameOfField)) {
					System.out.println("duplicate name of " + nameOfField);
					return false;
				}
				// check if the field name is a reserved word or not
				if (Constants.reservedWords().contains(nameOfField)) {
					System.out.format("'%s' is a reserved word%n", nameOfField);
					return false;
				}
				// check if the type exist or not
				if (!Constants.reservedTypes().contains(typeOfField)) {
					System.out.format("unrecognized type '%s', check the manual of DB_OWN %n", typeOfField);
					return false;
				}
				createdFields.add(nameOfField);

				mapOfFields.put(nameOfField, typeOfField);
			}

		}
		table.setNumberOfColumns(mapOfFields.size());
		table.setFields(mapOfFields);
		writeTableToFile(table);
		return true;
	}

	public static List<Table> getListOfTable() throws Exception {
		List<Table> listOfTables = new ArrayList<>();

		// get the file in which tables are stored
		File myObj = new File(Constants.TABLES_FILES);
		if (!myObj.exists())
			return Collections.emptyList();

		// define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);

		// read the data from file and pass it to the split method
		// and finally store it to the list of the databases
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			Table table = splitTableFromString(CryptoUtils.decryptedData(data));
			listOfTables.add(table);
		}

		return listOfTables;
	}

	public static Table splitTableFromString(String data) {
		String table = data;
		table = table.substring(table.indexOf("Table(") + "Table(".length(), table.length() - 1);
		String tableName = table.substring(table.indexOf("tableName=") + "tableName=".length(),
				table.indexOf(", database="));
		String databaseName = table.substring(table.indexOf("database=") + "database=".length(),
				table.indexOf(", numberOfColumns="));
		String numberOfColumns = table.substring(table.indexOf("numberOfColumns=") + "numberOfColumns=".length(),
				table.indexOf(", fields="));
		String fields = table.substring(table.indexOf("fields=") + "fields={".length(), table.length() - 1);

		// create hash map and add the fields to it
		String fieldArray[] = fields.split(",");
		HashMap<String, String> mapOfFields = new HashMap<String, String>();
		for (int i = 0; i < fieldArray.length; i++) {
			String sp[] = fieldArray[i].split("=");
			mapOfFields.put(sp[0].trim(), sp[1].trim());
		}

		Table tb = new Table();
		tb.setTableName(tableName);
		tb.setDatabase(databaseName);
		tb.setNumberOfColumns(Integer.valueOf(numberOfColumns));
		tb.setFields(mapOfFields);
		
		return tb;
	}

	public static void main(String[] args) throws Exception {
		//System.out.println(createTable("create table  jee ( sd int , sophos string , wd int , alpha double) ;"));

		getListOfTable().forEach(System.out::println);
		
	}
}
