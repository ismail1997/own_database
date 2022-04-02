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
import own_database.models.Field;
import own_database.models.Table;
import own_database.utils.Constants;
import own_database.utils.CryptoUtils;
import own_database.utils.Tools;
import own_database.utils.databaseTools.DatabaseTools;

public class TableTools {
	public static void descriptTable(Table table) {

	}

	public static void writeTableToFile(Table table) throws Exception {
		Tools.writeToFile(CryptoUtils.encryptData(table.toString()), Constants.TABLES_FILES);
	}

	
	public static boolean checkIfTableExistAlreadyInDb(String tableName,String dbName) throws Exception {
		Table table = getTable(tableName,dbName);
		if( table ==null) return false;
		return true;
	}
	
	public static Table getTable(String tableName) throws Exception {
		for(Table t : getListOfTable()) {
			if(t.getTableName().equals(tableName)) {
				return t;
			}
		}
		return null;
	}
	
	public static Table getTable(String tableName, String dbName) throws Exception {
		for(Table t : getListOfTable()) {
			if(t.getDatabase().equals(dbName) && t.getTableName().equals(tableName)) return t;
		}
		return null;
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
		// check if table name contains or started with special characters or numbers
		if (Tools.checkIfStringContainsWithNumberOrChar(arrayOfFirstStm[2])) {
			System.out.format("invalid table name ,'%s' shouldn't contain anay special characters or numbers",
					arrayOfFirstStm[2]);
			return false;
		}
		String nameOfTable = arrayOfFirstStm[2];

		
		//check if the table exist already on the database 
		if(checkIfTableExistAlreadyInDb(nameOfTable,databaseName)) {
			System.out.format("Table '%s' already exists%n",nameOfTable);
			return false;
		}
		// assign the name to the table object
		table.setTableName(nameOfTable);
		table.setDatabase(databaseName);

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
		//table.setFields(mapOfFields);
		boolean addTableToDatabase =DatabaseTools.addTableDatabase(table.getDatabase(), table.getTableName());
		if(!addTableToDatabase) {
			return false;
		}
		writeTableToFile(table);
		System.out.format("Query OK, '%s' created successfully %n",table.getTableName());
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
		//tb.setFields(mapOfFields);
		
		return tb;
	}

	public static void main(String[] args) throws Exception {
		
		String tbToString="Table(tableName=usex, database=users, numberOfColumns=4, listOfFields=[Field(fieldName=id, fieldType=int, primaryKey=pk, foreignKey=), Field(fieldName=sd, fieldType=string, primaryKey=, foreignKey=), Field(fieldName=isd, fieldType=int, primaryKey=, foreignKey=fg[sdf:id]), Field(fieldName=swatid, fieldType=int, primaryKey=, foreignKey=fg[sbouaddi:id])])";
		tbToString = tbToString.substring(0,tbToString.length()-2);
		System.out.println(tbToString);
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
		for(String str : extractFields) {
			if(str.equals("")) continue;
			String fieldName = str.substring(str.indexOf("fieldName=")+"fieldName=".length(),str.indexOf(", fieldType")).trim();
			String fieldType = str.substring(str.indexOf("fieldType=")+"fieldType=".length(),str.indexOf(", primaryKey")).trim();
			String primaryKey = str.substring(str.indexOf("primaryKey=")+"primaryKey=".length(),str.indexOf(", foreignKey=")).trim();
			String foreignKey = str.substring(str.indexOf("foreignKey=")+"foreignKey=".length(),str.indexOf(")")).trim();
			
			
			Field fld = new Field();
			fld.setFieldName(fieldName);
			fld.setFieldType(fieldType);
			fld.setPrimaryKey(primaryKey);
			fld.setForeignKey(foreignKey);
			fields.add(fld);
			
		}
		
		table.setListOfFields(fields);
		System.out.println(table);
	}
	
	
	
	public static void createtable() throws Exception {
		String tbStatement ="   CREATE  table usex ( id int pk, sd string , isd int fg[sdf:id] , swatid int fg[sbouaddi:id]);   ";
		
		tbStatement = tbStatement.trim().replaceAll("\\s+", " ").toLowerCase();//trim all white spaces and replace it with single white space

		String [] splitTbStatement=tbStatement.trim().split("\\(");	//split the table create statement into two string 

		
		if(splitTbStatement.length!=2) {//check if the split array is of size 2 
			System.out.println("invalid create table statement");
			return;
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
			return ;
		}
		
		if(!firstElementOfCreateTable[0].toLowerCase().equals("create")) {//check if the first keyword is 'create'
				System.out.println("syntax error, check the manual of db_own");return ;
		}
		if(!firstElementOfCreateTable[1].toLowerCase().equals("table")) {//check if the second keyword is 'table'
			System.out.printf("invalid create statement, '%s' is not a valid keyword; check the manual of the db_won\n",firstElementOfCreateTable[1]);return;
		}
		
		
		if(Constants.reservedWords().contains(firstElementOfCreateTable[2]) ) {//check if name of table is not a reserved word or 
			System.out.printf("invalid table name, '%s' is a reserved keyword of db_own",firstElementOfCreateTable[2]);return ;
		}
		
		if(Tools.checkIfStringContainsWithNumberOrChar(firstElementOfCreateTable[2])) {//check if table name contains any special character or started with number
			System.out.printf("invalid table name '%s', check the manual of db_own",firstElementOfCreateTable[2]);return ;
		}
		
		/*****
		 * check the second split 
		*/
		String [] splitToExtractTheFieldsAndEndStatement = splitTbStatement[1].split("\\)");
		
		if(splitToExtractTheFieldsAndEndStatement.length !=2) {//check if the length of the second split is equal to 2 
			System.out.println("invalid create table statement, check the manual of own_db");return;
		}
		
		
		if(!splitToExtractTheFieldsAndEndStatement[1].equals(";")) {//check if the statement ends with ';'
			System.out.println("invalid end of statement, missing ';' at the end");return;
		}
		
		//assign variables to name
		table.setDatabase("users");
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
				System.out.println("syntax error, invalid fields of create table; check the manual of db_own");return;
			}else if(splitFlds.length==2) {// Example : id string
				//for the column name
				if(Constants.reservedWords().contains(splitFlds[0])) {//check if the column name is not a reserved word
					System.out.format("invalid statement, '%s' is a reserved word of own_db%n",splitFlds[0]);return;
				}
				if(Tools.checkIfStringContainsWithNumberOrChar(splitFlds[0])) {//check if the column name doens't not start with special character or starts with numbers
					System.out.println("invalid colummn name, '%s' is not a valid name; check the manuel of db_own");return;
				}
				if(createdFieldsAllReady.contains(splitFlds[0])) {//check if the field is created already or not 
					System.out.printf("syntax error, Duplicate column name '%s' \n",splitFlds[0]);
					return;
				}
				//for the column type 
				if(!Constants.reservedTypes().contains(splitFlds[1])) {//check if the column type is a valid type
					System.out.printf("invalid statement, '%s' is not a valid type of own_db\n",splitFlds[1]);return;
				}
				
				createdFieldsAllReady.add(splitFlds[0]);//adding the column name to the created fields list
				//TODO adding field to the table object 
				myField.setFieldName(splitFlds[0].trim());
				myField.setFieldType(splitFlds[1].trim());
				myField.setPrimaryKey("");
				myField.setForeignKey("");

			}else if(splitFlds.length==3) { //example : id integer pk or | groupid int fg[table_name:key]
				if(Constants.reservedWords().contains(splitFlds[0])) {//check if the column name is not a reserved word
					System.out.format("invalid statement, '%s' is a reserved word of own_db%n",splitFlds[0]);return;
				}
				if(Tools.checkIfStringContainsWithNumberOrChar(splitFlds[0])) {//check if the column name doens't not start with special character or starts with numbers
					System.out.println("invalid colummn name, '%s' is not a valid name; check the manuel of db_own");return;
				}
				if(createdFieldsAllReady.contains(splitFlds[0])) {//check if the field is created already or not 
					System.out.printf("syntax error, Duplicate column name '%s' \n",splitFlds[0]);
					return;
				}
				
				createdFieldsAllReady.add(splitFlds[0]);//adding the column name to the created fields list
				
				//for the column type 
				if(!Constants.reservedTypes().contains(splitFlds[1])) {//check if the column type is a valid type
					System.out.printf("invalid statement, '%s' is not a valid type of own_db\n",splitFlds[1]);return;
				}
				
				if(!splitFlds[1].equals("int")) {//check if the column type is int 
					System.out.format("error : only 'int' type are allowed to be primary or foreing keys not '%s'; check the manual of db_own%n",splitFlds[1]);return;
				}
				if(!splitFlds[2].startsWith("pk") && !splitFlds[2].startsWith("fg")) {//pk indicates the primary key, and fg indicates foreing_key
					System.out.format("invalid references '%s', please check the manual of own_db",splitFlds[2]);return;
				}
			
				if(splitFlds[2].startsWith("pk")) {//if the field is declared as primary key
					
					if(duplicatedPrimaryKeys.contains(splitFlds[2])) {//check if the primary key is already registered 
						System.out.println("duplicate primary key, please check the manual of own_db");return;
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
						System.out.format("duplicate of the same foreing key '%s', please check the manual of own_db%n",foreing_key);return;
					}

					if(splitFg.length != 2) {
						System.out.format("invalid foreing key '%s', please check the manual of own_db%n",foreing_key);return;
					}
					
					String fgTableName = splitFg[0].trim();
					String fgTableColumn = splitFg[1].trim();
					
					if(Constants.reservedWords().contains(fgTableName)) {//check if the foreign table is not a reserved word
						System.out.format("invalid foreign table name, '%s' is a reserved word for own_db%n", fgTableName);return;
					}
					if(Tools.checkIfStringContainsWithNumberOrChar(fgTableName)) {//check if the table name doesn't start with a number or has any special character on it
						System.out.format("invalid table name '%s', check the manual of db_own%n",fgTableName);return;
					}
					if(Constants.reservedWords().contains(fgTableColumn)) {//check if the foreign table column is not a reserved word
						System.out.format("invalid foreign column name, '%s' is a reserved word for own_db%n", fgTableColumn);return;
					}
					if(Tools.checkIfStringContainsWithNumberOrChar(fgTableColumn)) {//check if the table column name doesn't start with a number or has any special character on it
						System.out.format("invalid column name '%s', check the manual of db_own%n",fgTableColumn);return;
					}
					
					
					duplicatedForeignKeys.add(foreing_key);
					
					
					//TODO check if foreign table is exist or not and check if it has the column
					
					myField.setFieldName(splitFlds[0].trim());
					myField.setFieldType(splitFlds[1].trim());
					myField.setPrimaryKey("");
					myField.setForeignKey(splitFlds[2].trim());
				}
				
				
				
			}else {
				System.out.println("invalid create statement, check the manual of db_own");return;
			}
			
			
			tableFields.add(myField);
			table.setListOfFields(tableFields);
			table.setNumberOfColumns(tableFields.size());
			
		}
		
		writeTableToFile(table);
		System.out.println(table);
	}
}
