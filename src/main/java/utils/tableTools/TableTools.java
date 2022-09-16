package utils.tableTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import models.Database;
import models.Field;
import models.Table;
import utils.Constants;
import utils.CryptoUtils;
import utils.Tools;
import utils.databaseTools.DatabaseTools;

public class TableTools {
	/**
	 * 
	 * @param table
	 */
	public static void descriptTable(Table table) {

	}

	/**
	 * 
	 * @param table
	 * @throws Exception
	 */
	public static void writeTableToFile(Table table) throws Exception {
		Tools.writeToFile(CryptoUtils.encryptData(table.toString()), Constants.TABLES_FILES);
	}

	/**
	 * 
	 * @param tableName
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static boolean checkIfTableExistAlreadyInDb(String tableName,String dbName) throws Exception {
		Table table = getTable(tableName,dbName);
		if( table ==null) return false;
		return true;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<Table> getTables() throws Exception{
		List<Table> listOfTables = new ArrayList<>();

		File myObj = new File(Constants.TABLES_FILES);	// get the file in which tables are stored
		if (!myObj.exists())
			return Collections.emptyList();

		Scanner myReader = new Scanner(myObj);// define a scanner object to read from the file

		while (myReader.hasNextLine()) {// read the data from file and pass it to the split method
			String data = myReader.nextLine();// and finally store it to the list of the databases
			Table table = extractTableFromFile(CryptoUtils.decryptedData(data));
			listOfTables.add(table);
		}
		myReader.close();
		return listOfTables;
	}
	
	/**
	 * 
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static List<Table> getTablesByDB(String database) throws Exception{
		List<Table> tables = new ArrayList<Table>();
		for(Table tb : getTables()) {
			if(tb.getDatabase().equals(database)) {
				tables.add(tb);
			}
		}
		if(tables == null) return null;
		return tables;
	}
	
	/**
	 * 
	 * @param table
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static Table getTable(String table, String database) throws Exception {
		for(Table tb : getTablesByDB(database)) {
			if(tb.getTableName().equals(table) && tb.getDatabase().equals(database))
				return tb;
		}
		return null;
	}
	
	/**
	 * 
	 * @param tableToString
	 * @return
	 */
	public static Table extractTableFromFile(String tableToString) {
		String tbToString = tableToString;
		tbToString = tbToString.substring(0,tbToString.length()-2);
		String name = tbToString.substring(tbToString.indexOf("(tableName=")+"(tableName=".length(),tbToString.indexOf(", database="));
		String db=tbToString.substring(tbToString.indexOf("database=")+"database=".length(),tbToString.indexOf(", numberOfColumns="));
		String numberOfColumns = tbToString.substring(tbToString.indexOf("numberOfColumns=")+"numberOfColumns=".length(),tbToString.indexOf(", listOfFields="));
		
		Table table = new Table();
		table.setDatabase(db);
		table.setTableName(name);
		table.setNumberOfColumns(Integer.parseInt(numberOfColumns));
		List<Field> fields = new ArrayList<>();
		//now getting the fields foreign 
		String splitFields [] = tbToString.trim().split("listOfFields=\\[");
		splitFields[1] = splitFields[1].trim();
		String extractFields [] = splitFields[1].split("Field\\(");
		for(String str : extractFields) {//foreing
			if(str.equals("")) continue;
			String fieldName = str.substring(str.indexOf("fieldName=")+"fieldName=".length(),str.indexOf(", fieldType")).trim();
			String fieldType = str.substring(str.indexOf("fieldType=")+"fieldType=".length(),str.indexOf(", primaryKey")).trim();
			String primaryKey = str.substring(str.indexOf("primaryKey=")+"primaryKey=".length(),str.indexOf(", foreignKey=")).trim();
			String foreignKey = str.substring(str.indexOf("foreignKey=")+"foreingKey=".length(),str.indexOf(")")).trim();
			
			
			Field fld = new Field();
			fld.setFieldName(fieldName);
			fld.setFieldType(fieldType);
			fld.setPrimaryKey(primaryKey);
			fld.setForeignKey(foreignKey);
			fields.add(fld);
			
		}
		
		table.setListOfFields(fields);
		
		return table;
	}
	
	/**
	 * 
	 * @param tb
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static List<Field> getFieldsOfTable(String tb, String database) throws Exception{
		List<Field> fields = new ArrayList<Field>();
		
		Table table = getTable(tb, database);
		if(table ==null) return null;
		
		for(Field f : table.getListOfFields()) fields.add(f);
						
		return fields;
	}
	
	/**
	 * 
	 * @param fieldName
	 * @param table
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static boolean checkIfTableContainsField(String fieldName,String table,String database) throws Exception {
		List<Field> listOfFields = getFieldsOfTable(table, database) ;
		if(listOfFields == null) return false;
		for(Field field : listOfFields) {
			if(field.getFieldName().equals(fieldName)) return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param fieldName
	 * @param tableName
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public static String getTypeOfField(String fieldName,String tableName,String databaseName) throws Exception {
		List<Field> fieldsOfTable = getFieldsOfTable(tableName, databaseName);
		if(fieldsOfTable == null) return null;
		
		for(Field f : fieldsOfTable) {
			if(f.getFieldName().equals(fieldName)) {
				return f.getFieldType();
			}
		}
		
		return null;
		
	}
	
	
	
	/**
	 * 
	 * @param statement
	 * @param currentDb
	 * @return
	 * @throws Exception
	 */
	public static boolean createTable(String statement,String currentDb) throws Exception {
		String tbStatement =statement;
		
		tbStatement = tbStatement.trim().replaceAll("\\s+", " ").toLowerCase();//trim all white spaces and replace it with single white space

		String [] splitTbStatement=tbStatement.trim().split("\\(");	//split the table create statement into two string 

		
		if(splitTbStatement.length!=2) {//check if the split array is of size 2 
			System.out.println("invalid create table statement");
			return false;
		}
		
		// Instantiate table
		Table table = new Table();
		List<Field> tableFields = new ArrayList<Field>();
		
		/*****
		 * check the first split 
		*/
		String [] firstElementOfCreateTable = splitTbStatement[0].trim().split(" ");
		if(firstElementOfCreateTable.length!=3) {//check if the first array is of size 3 ( create keyword, table keyword and table_name )
			System.out.println("invalid create table statement, missing arguments");
			return false;
		}
		
		if(!firstElementOfCreateTable[0].toLowerCase().equals("create")) {//check if the first keyword is 'create'
				System.out.println("syntax error, check the manual of db_own");return false;
		}
		if(!firstElementOfCreateTable[1].toLowerCase().equals("table")) {//check if the second keyword is 'table'
			System.out.printf("invalid create statement, '%s' is not a valid keyword; check the manual of the db_won\n",firstElementOfCreateTable[1]);return false;
		}
		
		
		if(Constants.reservedWords().contains(firstElementOfCreateTable[2]) ) {//check if name of table is not a reserved word or 
			System.out.printf("invalid table name, '%s' is a reserved keyword of db_own",firstElementOfCreateTable[2]);return false;
		}
		
		if(Tools.checkIfStringContainsWithNumberOrChar(firstElementOfCreateTable[2])) {//check if table name contains any special character or started with number
			System.out.printf("invalid table name '%s', check the manual of db_own",firstElementOfCreateTable[2]);return false;
		}
		
		if(getTable(firstElementOfCreateTable[2], currentDb)!=null) {
			System.out.format("table '%s' already exist in the '%s' database%n",firstElementOfCreateTable[2],currentDb);return false;
		}
		
		/*****
		 * check the second split 
		*/
		String [] splitToExtractTheFieldsAndEndStatement = splitTbStatement[1].split("\\)");
		
		if(splitToExtractTheFieldsAndEndStatement.length !=2) {//check if the length of the second split is equal to 2 
			System.out.println("invalid create table statement, check the manual of own_db");return false;
		}
		
		if(!splitToExtractTheFieldsAndEndStatement[1].trim().equals(";")) {//check if the statement ends with ';'
			System.out.println("invalid end of statement, missing ';' at the end");return false;
		}
		
		//assign variables to name
		table.setDatabase(currentDb);
		table.setTableName(firstElementOfCreateTable[2]);

		
		String fields = splitToExtractTheFieldsAndEndStatement[0].trim();
		String [] splitFields = fields.split(",");
		
		ArrayList<String> createdFieldsAllReady = new ArrayList<String>();//to prevent the duplicate fields
		ArrayList<String> duplicatedPrimaryKeys = new ArrayList<String>();//to prevent the duplicate primary key
		ArrayList<String> duplicatedForeignKeys = new ArrayList<String>();//to prevent duplicate of the same foreing key
		
		for(String str : splitFields) {
			Field myField = new Field();
			str=str.trim();
			String splitFlds [] = str.split(" ");
			
			if(splitFlds.length==1) {
				System.out.println("syntax error, invalid fields of create table; check the manual of db_own");return false;
			}else if(splitFlds.length==2) {// Example : id string
				//for the column name
				if(Constants.reservedWords().contains(splitFlds[0])) {//check if the column name is not a reserved word
					System.out.format("invalid statement, '%s' is a reserved word of own_db%n",splitFlds[0]);return false;
				}
				if(Tools.checkIfStringContainsWithNumberOrChar(splitFlds[0])) {//check if the column name doens't not start with special character or starts with numbers
					System.out.format("invalid colummn name, '%s' is not a valid name; check the manuel of db_own%n",splitFlds[0]);return false;
				}
				if(createdFieldsAllReady.contains(splitFlds[0])) {//check if the field is created already or not 
					System.out.printf("syntax error, Duplicate column name '%s' \n",splitFlds[0]);
					return false;
				}
				//for the column type 
				if(!Constants.reservedTypes().contains(splitFlds[1])) {//check if the column type is a valid type
					System.out.printf("invalid statement, '%s' is not a valid type of own_db\n",splitFlds[1]);return false;
				}
				
				createdFieldsAllReady.add(splitFlds[0]);//adding the column name to the created fields list
				//TODO adding field to the table object 
				myField.setFieldName(splitFlds[0].trim());
				myField.setFieldType(splitFlds[1].trim());
				myField.setPrimaryKey("");
				myField.setForeignKey("");

			}else if(splitFlds.length==3) { //example : id integer pk or | groupid int fg[table_name:key]
				if(Constants.reservedWords().contains(splitFlds[0])) {//check if the column name is not a reserved word
					System.out.format("invalid statement, '%s' is a reserved word of own_db%n",splitFlds[0]);return false;
				}
				if(Tools.checkIfStringContainsWithNumberOrChar(splitFlds[0])) {//check if the column name doens't not start with special character or starts with numbers
					System.out.println("invalid colummn name, '%s' is not a valid name; check the manuel of db_own");return false;
				}
				if(createdFieldsAllReady.contains(splitFlds[0])) {//check if the field is created already or not 
					System.out.printf("syntax error, Duplicate column name '%s' \n",splitFlds[0]);
					return false;
				}
				
				createdFieldsAllReady.add(splitFlds[0]);//adding the column name to the created fields list
				
				//for the column type 
				if(!Constants.reservedTypes().contains(splitFlds[1])) {//check if the column type is a valid type
					System.out.printf("invalid statement, '%s' is not a valid type of own_db\n",splitFlds[1]);return false;
				}
				
				if(!splitFlds[1].equals("int")) {//check if the column type is int 
					System.out.format("error : only 'int' type are allowed to be primary or foreing keys not '%s'; check the manual of db_own%n",splitFlds[1]);return false;
				}
				if(!splitFlds[2].startsWith("pk") && !splitFlds[2].startsWith("fg")) {//pk indicates the primary key, and fg indicates foreing_key
					System.out.format("invalid references '%s', please check the manual of own_db",splitFlds[2]);return false;
				}
			
				if(splitFlds[2].startsWith("pk")) {//if the field is declared as primary key
					
					if(duplicatedPrimaryKeys.contains(splitFlds[2])) {//check if the primary key is already registered 
						System.out.println("duplicate primary key, please check the manual of own_db");return false;
					}
					
					duplicatedPrimaryKeys.add(splitFlds[2]);//add the primary key to the list, to track duplication
					
					
					myField.setFieldName(splitFlds[0].trim());
					myField.setFieldType(splitFlds[1].trim());
					myField.setPrimaryKey(splitFlds[2].trim());
					myField.setForeignKey("");
				
				}else if(splitFlds[2].startsWith("fg")) {//check if the field is declared as foreign key
				
					String foreing_key=splitFlds[2].replace("fg[", "").replace("]", "");
					String splitFg [] = foreing_key.split(":");
					
					if(duplicatedForeignKeys.contains(foreing_key)) {
						System.out.format("duplicate of the same foreing key '%s', please check the manual of own_db%n",foreing_key);return false;
					}

					if(splitFg.length != 2) {
						System.out.format("invalid foreing key '%s', please check the manual of own_db%n",foreing_key);return false;
					}
					
					String fgTableName = splitFg[0].trim();
					String fgTableColumn = splitFg[1].trim();
					
					if(Constants.reservedWords().contains(fgTableName)) {//check if the foreign table is not a reserved word
						System.out.format("invalid foreign table name, '%s' is a reserved word for own_db%n", fgTableName);return false;
					}
					if(Tools.checkIfStringContainsWithNumberOrChar(fgTableName)) {//check if the table name doesn't start with a number or has any special character on it
						System.out.format("invalid table name '%s', check the manual of db_own%n",fgTableName);return false;
					}
					if(Constants.reservedWords().contains(fgTableColumn)) {//check if the foreign table column is not a reserved word
						System.out.format("invalid foreign column name, '%s' is a reserved word for own_db%n", fgTableColumn);return false;
					}
					if(Tools.checkIfStringContainsWithNumberOrChar(fgTableColumn)) {//check if the table column name doesn't start with a number or has any special character on it
						System.out.format("invalid column name '%s', check the manual of db_own%n",fgTableColumn);return false;
					}
					
					
					duplicatedForeignKeys.add(foreing_key);
					
					
					//TODO check if foreign table is exist or not and check if it has the column
					Table fgTable = getTable(fgTableName, currentDb);
					if(fgTable == null) {//check if the foreign key table exist or not 
						System.out.format("foreign key table '%s' doesn't exist%n",fgTableName);return false;
					}
					if(!checkIfTableContainsField(fgTableColumn, fgTableName, currentDb)) {
						System.out.format("'%s' field doesn't exit in '%s' %n",fgTableColumn,fgTableName);return false;
					}
					if(!getTypeOfField(fgTableColumn, fgTableName, currentDb).equals("int")) {
						System.out.println("only 'int' types are allowed to be foreing keys, check the manual of db_own");
						return false;
					}
					
					
					myField.setFieldName(splitFlds[0].trim());
					myField.setFieldType(splitFlds[1].trim());
					myField.setPrimaryKey("");
					myField.setForeignKey(splitFlds[2].trim());
				}
				
				
				
			}else {
				System.out.println("invalid create statement, check the manual of db_own");return false;
			}
			
			
			tableFields.add(myField);
			table.setListOfFields(tableFields);
			table.setNumberOfColumns(tableFields.size());
			
		}
		
		boolean addTableToDatabase =DatabaseTools.addTableDatabase(table.getDatabase(), table.getTableName());
		if(!addTableToDatabase) {
			return false;
		}
		writeTableToFile(table);
		System.out.format("Query OK, '%s' created successfully %n",table.getTableName());
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		
	}
}
