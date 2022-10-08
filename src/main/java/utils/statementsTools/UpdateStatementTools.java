package utils.statementsTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import initpackage.FirstInit;
import models.Database;
import models.Field;
import models.Table;
import utils.CryptoUtils;
import utils.Tools;
import utils.tableTools.TableTools;

public class UpdateStatementTools {
	public static void main(String [] args ) throws Exception {
		String statement ="update user set email='john@ablert.com',firstname='mr john' where   id=2 ;";
		statement = statement.toLowerCase().replaceAll("\\s+", " ").trim();

		String currentDatabase = UseStatementTools.getTheCurrentSessionDatabase(); // get the current selected database
		if (currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");
			return;
		}

		// verify if the end of statement is not ended with ;
		if (statement.charAt(statement.length() - 1) != ';') {
			System.out.println("ERROR : invalid end of statement, missing ';' at the end");
			return;
		}
		
		
		boolean whereExist = false;//variable to verify if there is where clause or not 
		String[] dividedStr = statement.split(" ");
		
		ArrayList<String> listOfTrimedStrings = new ArrayList<String>();//trim the string to get the keywords 
		for (String s : dividedStr)
			listOfTrimedStrings.add(s.trim());
		
		if (!listOfTrimedStrings.get(0).equals("update")) {// check if the keyword is update
			System.out.println("ERROR : invalid statement, please check the manual of db_own");
			return;
		}
		if (Tools.countFrequencies(listOfTrimedStrings, "where") > 1) {// check if where is repeated more than once
			System.out.println("ERROR : invalid update statement,'where'? please check the manual of db_own");
			return;
		}
		if (Tools.countFrequencies(listOfTrimedStrings, "set") != 1) {// check if where is repeated more than once
			System.out.println("ERROR : invalid update statement,'set'? please check the manual of db_own");
			return;
		}

		String valueToUpdate = "";
		if (Tools.countFrequencies(listOfTrimedStrings, "where") == 1) {
			whereExist = true;
			valueToUpdate = statement.substring(statement.indexOf("where") + "where".length(), statement.indexOf(";"))
					.trim();
		} else {
			whereExist = false;
		}
		
		String tableName = statement//get the table name 
				.substring(statement.indexOf("update") + "update".length(), statement.indexOf("set")).trim();
		
		
		String fieldToUpdateSt ="";//get the values passed as arguments 
		if(whereExist) {
			fieldToUpdateSt = statement.substring(statement.indexOf("set") + "set".length(), statement.indexOf("where")).trim() ;
		}else {
			fieldToUpdateSt = statement.substring(statement.indexOf("set")+"set".length(), statement.indexOf(";")).trim();
		}

		if (tableName.equals("") || tableName.equals(null)) {
			System.out.println("ERROR : invalid update statement, 'missing table name' please check the manual of own_db");
			return;
		}
		
		if (fieldToUpdateSt.equals("") && whereExist) {
			System.out.println("ERROR : invalid update statement, 'conditions missing' please check the manual of own_db");
			return;
		}
		
		

		/***
		 * info about table
		 */

		Table table = TableTools.getTable(tableName, currentDatabase);
		ArrayList<String> listOfFields = new ArrayList<String>();

		for (int i = 0; i < table.getListOfFields().size(); i++) {
			listOfFields.add(table.getListOfFields().get(i).getFieldName());
		}

		
		

		


		/*
		 * now we are going to handle the fields to update
		 */
		ArrayList<String> fieldsDeclaration = new ArrayList<String>();
		ArrayList<String> fieldsValues = new ArrayList<String>();
		String[] array = fieldToUpdateSt.split(",");
		
		
		for (String s : array) {
			String subArray[] = s.trim().split("=");
			if (subArray.length != 2) {
				System.out.println("ERROR : invalid update statement, please check the manual of own_db");
				return;
			}

			// check if table contains that field
			if (!listOfFields.contains(subArray[0].trim())) {
				System.out.format("ERROR : field '%s' doesn't exist in '%s' table %n", subArray[0],
						table.getTableName());
				return;
			}

			if (fieldTypeVerification(subArray[0].trim(), subArray[1].trim(), table) < 0) {
				System.out.format("ERROR : you have syntax error in your sql statement near %s = %s , type mismatch %n",
						subArray[0], subArray[1]);
				return;
			}
			fieldsDeclaration.add(subArray[0].trim());
			fieldsValues.add(subArray[1].trim());
			//

		}
		
		
		System.out.println("declaration fields are : "+fieldsDeclaration);
		System.out.println("value fields are : "+fieldsValues);
		
		
		/*********
		 * now we handle the new values
		 */
		ArrayList<String> nwFields = new ArrayList<String>();
		ArrayList<String> nwValues = new ArrayList<String>();
		if (whereExist) {
			ArrayList<String> flds = new ArrayList<String>();
			ArrayList<String> vls = new ArrayList<String>();
			String[] array2 = valueToUpdate.split("and");

			for (String r : array2) {
				String[] arr = r.trim().split("=");
				if (arr.length != 2) {
					System.out.println("ERROR : invalid update statement, please check the manual of own_db");
					return;
				}

				// check if table contains that field
				if (!listOfFields.contains(arr[0].trim())) {
					System.out.format("ERROR : field '%s' doesn't exist in '%s' table %n", arr[0],
							table.getTableName());
					return;
				}

				if (fieldTypeVerification(arr[0].trim(), arr[1].trim(), table) < 0) {
					System.out.format(
							"ERROR : you have syntax error in your sql statement near %s = %s , type mismatch %n",
							arr[0], arr[1]);
					return;
				}
				nwFields.add(arr[0].trim());
				nwValues.add(arr[1].trim());
			}

		}
		
		System.out.println("condition fields declared : "+nwFields);
		System.out.println("condition fields values   : "+nwValues);
		
		
		
		if (whereExist) {
			updateTableWithWhere(table, fieldsDeclaration, fieldsValues, nwFields, nwValues);
		} else {
			updateTableWithoutWhere(table, fieldsDeclaration, fieldsValues);
		}

	}

	public static void validUpdateStatement(String statement) throws Exception {
		//String statement = "update user set  email='alpha@Romeao',firstname='kooraisnotsafe',email='nour'    ;";
		statement = statement.toLowerCase().replaceAll("\\s+", " ").trim();

		String currentDatabase = UseStatementTools.getTheCurrentSessionDatabase(); // get the current selected database
		if (currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");
			return;
		}

		// verify if the end of statement is not ended with ;
		if (statement.charAt(statement.length() - 1) != ';') {
			System.out.println("ERROR : invalid end of statement, missing ';' at the end");
			return;
		}
		
		
		boolean whereExist = false;//variable to verify if there is where clause or not 
		String[] dividedStr = statement.split(" ");
		
		ArrayList<String> listOfTrimedStrings = new ArrayList<String>();//trim the string to get the keywords 
		for (String s : dividedStr)
			listOfTrimedStrings.add(s.trim());
		
		if (!listOfTrimedStrings.get(0).equals("update")) {// check if the keyword is update
			System.out.println("ERROR : invalid statement, please check the manual of db_own");
			return;
		}
		if (Tools.countFrequencies(listOfTrimedStrings, "where") > 1) {// check if where is repeated more than once
			System.out.println("ERROR : invalid update statement,'where'? please check the manual of db_own");
			return;
		}
		if (Tools.countFrequencies(listOfTrimedStrings, "set") != 1) {// check if where is repeated more than once
			System.out.println("ERROR : invalid update statement,'set'? please check the manual of db_own");
			return;
		}

		String valueToUpdate = "";
		if (Tools.countFrequencies(listOfTrimedStrings, "where") == 1) {
			whereExist = true;
			valueToUpdate = statement.substring(statement.indexOf("where") + "where".length(), statement.indexOf(";"))
					.trim();
		} else {
			whereExist = false;
		}
		
		String tableName = statement//get the table name 
				.substring(statement.indexOf("update") + "update".length(), statement.indexOf("set")).trim();
		
		
		String fieldToUpdateSt ="";//get the values passed as arguments 
		if(whereExist) {
			fieldToUpdateSt = statement.substring(statement.indexOf("set") + "set".length(), statement.indexOf("where")).trim() ;
		}else {
			fieldToUpdateSt = statement.substring(statement.indexOf("set")+"set".length(), statement.indexOf(";")).trim();
		}

		if (tableName.equals("") || tableName.equals(null)) {
			System.out.println("ERROR : invalid update statement, 'missing table name' please check the manual of own_db");
			return;
		}
		
		if (fieldToUpdateSt.equals("") && whereExist) {
			System.out.println("ERROR : invalid update statement, 'conditions missing' please check the manual of own_db");
			return;
		}
		
		

		/***
		 * info about table
		 */

		Table table = TableTools.getTable(tableName, currentDatabase);
		ArrayList<String> listOfFields = new ArrayList<String>();

		for (int i = 0; i < table.getListOfFields().size(); i++) {
			listOfFields.add(table.getListOfFields().get(i).getFieldName());
		}

		
		

		


		/*
		 * now we are going to handle the fields to update
		 */
		ArrayList<String> fieldsDeclaration = new ArrayList<String>();
		ArrayList<String> fieldsValues = new ArrayList<String>();
		String[] array = fieldToUpdateSt.split(",");
		
		
		for (String s : array) {
			String subArray[] = s.trim().split("=");
			if (subArray.length != 2) {
				System.out.println("ERROR : invalid update statement, please check the manual of own_db");
				return;
			}

			// check if table contains that field
			if (!listOfFields.contains(subArray[0].trim())) {
				System.out.format("ERROR : field '%s' doesn't exist in '%s' table %n", subArray[0],
						table.getTableName());
				return;
			}

			if (fieldTypeVerification(subArray[0].trim(), subArray[1].trim(), table) < 0) {
				System.out.format("ERROR : you have syntax error in your sql statement near %s = %s , type mismatch %n",
						subArray[0], subArray[1]);
				return;
			}
			fieldsDeclaration.add(subArray[0].trim());
			fieldsValues.add(subArray[1].trim());
			//

		}
		
		
		/*********
		 * now we handle the new values
		 */
		ArrayList<String> nwFields = new ArrayList<String>();
		ArrayList<String> nwValues = new ArrayList<String>();
		if (whereExist) {
			ArrayList<String> flds = new ArrayList<String>();
			ArrayList<String> vls = new ArrayList<String>();
			String[] array2 = valueToUpdate.split("and");

			for (String r : array2) {
				String[] arr = r.trim().split("=");
				if (arr.length != 2) {
					System.out.println("ERROR : invalid update statement, please check the manual of own_db");
					return;
				}

				// check if table contains that field
				if (!listOfFields.contains(arr[0].trim())) {
					System.out.format("ERROR : field '%s' doesn't exist in '%s' table %n", arr[0],
							table.getTableName());
					return;
				}

				if (fieldTypeVerification(arr[0].trim(), arr[1].trim(), table) < 0) {
					System.out.format(
							"ERROR : you have syntax error in your sql statement near %s = %s , type mismatch %n",
							arr[0], arr[1]);
					return;
				}
				nwFields.add(arr[0].trim());
				nwValues.add(arr[1].trim());
			}

		}

		if (whereExist) {
			updateTableWithWhere(table, fieldsDeclaration, fieldsValues, nwFields, nwValues);
		} else {
			updateTableWithoutWhere(table, fieldsDeclaration, fieldsValues);
		}

	}

	public static void updateTableWithoutWhere(Table table, ArrayList<String> fieldDeclared,
			ArrayList<String> fieldDeclaredValues) throws Exception {
		// get the file in which databases are stored
		File myObj = new File(FirstInit.USER_HOME_DIRECTORY + "/" + FirstInit.DB_FILE_NAME + "/" + table.getTableName()
				+ "_" + table.getDatabase() + ".owndb");
		if (!myObj.exists()) {
			System.out.println("table doesn't exist");
			return;
		}
		// define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);

		// read the data from file and pass it to the split method
		String header = myReader.nextLine();
		String[] fields = CryptoUtils.decryptedData(header).split("\t\t");
		ArrayList<String> flds = new ArrayList<String>();
		for (String str : fields) {
			flds.add(str.trim());
		}

		
		ArrayList<ArrayList<String>> oldData = new ArrayList<ArrayList<String>>();
		while (myReader.hasNextLine()) {

			String data = myReader.nextLine();
			data = CryptoUtils.decryptedData(data).trim();
			//System.out.println(data);
			String[] splitData = data.split("\t\t");
			ArrayList<String> temp = new ArrayList<String>();
			for(String s : splitData) temp.add(s.trim());
			
			
			oldData.add(temp);
			
		}
		
		
		ArrayList<ArrayList<String>> newData = new ArrayList<ArrayList<String>>();
		for(int j = 0 ;j< oldData.size(); j++) {
			
			ArrayList<String> tmp = oldData.get(j);
			for(int e = 0 ; e <fieldDeclared.size();e++) {
				int index = flds.indexOf(fieldDeclared.get(e));

				
				if(fieldDeclared.get(e).equals("id")) {
					Field idField = table.getListOfFields().stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
					if(idField.getPrimaryKey().equals("pk")) {
						System.out.format("ERROR : Duplicate entry '%s' for key '%s.PRIMARY'%n",fieldDeclaredValues.get(e),table.getTableName());
						return;
					}
				}
				
				tmp.set(index, fieldDeclaredValues.get(e));
				
			}
			newData.add(tmp);
		}
		myReader.close();
		//now we should save the new data to file , and clear the oldest file 
		writeTheUpdateDataToFile(newData,table);
		
		System.out.println("Query OK, "+oldData.size()+" rows affected");
	}

	private static void writeTheUpdateDataToFile(ArrayList<ArrayList<String>> newData, Table table) throws Exception {
		String fileName=FirstInit.USER_HOME_DIRECTORY + "/" + FirstInit.DB_FILE_NAME + "/" + table.getTableName()
		+ "_" + table.getDatabase() + ".owndb";
		File myObj = new File(fileName);
		
		Scanner scanner = new Scanner (myObj);
		
		String header= scanner.nextLine();
		header = CryptoUtils.decryptedData(header);
		
		/**
		 *  now we should clear the file 
		 */
		Tools.clearFile(fileName);
		
		Tools.writeToFile(CryptoUtils.encryptData(header), fileName);
		for(ArrayList<String> str : newData) {
			String m="";
			for(String s : str) {
				m+=s+"\t\t";
			}
			Tools.writeToFile(CryptoUtils.encryptData(m), fileName);
			
		}
		
		scanner.close();
	}

	public static void updateTableWithWhere(Table table, ArrayList<String> fieldDeclared,
			ArrayList<String> fieldDeclaredValues, ArrayList<String> fieldConditions,
			ArrayList<String> valuesConditions) throws Exception {
		// get the file in which databases are stored
		File myObj = new File(FirstInit.USER_HOME_DIRECTORY + "/" + FirstInit.DB_FILE_NAME + "/" + table.getTableName()
				+ "_" + table.getDatabase() + ".owndb");
		if (!myObj.exists()) {
			System.out.println("table doesn't exist");
			return;
		}

		// define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);

		// read the data from file and pass it to the split method
		String header = myReader.nextLine();
		String[] fields = CryptoUtils.decryptedData(header).split("\t\t");
		ArrayList<String> flds = new ArrayList<String>();

		for (String str : fields) {
			flds.add(str.trim());
		}

		
		//System.out.println(flds);
		//if(true) return;
		ArrayList<String> newData = new ArrayList<String>();
		int updatedRow = 0;
		while (myReader.hasNextLine()) {

			String data = myReader.nextLine();
			data = CryptoUtils.decryptedData(data).trim();
			//System.out.println(data);
			String[] splitData = data.split("\t\t");
			ArrayList<String> temp = new ArrayList<String>();
			for(String str : splitData) {
				temp.add(str);
			}
			int counter = 0;
			for(String str : valuesConditions) {
				if(temp.contains(str)) {
					counter++;
				}
			}
			
			if(counter==valuesConditions.size()) {
				//System.out.println(data+"      --->     counter=true");
				for(int i = 0 ; i<fieldDeclared.size();i++) {
					int index=flds.indexOf(fieldDeclared.get(i));
					temp.set(index, fieldDeclaredValues.get(i));
				}
				String mut ="";
				for(String s : temp) mut+=s+"\t\t";
			    newData.add(mut);
			    updatedRow++;
			}else {
				//System.out.println(data+"      --->     counter=false");
				newData.add(data);
			}
			
			
			//oldData.add(temp);
			
		}
		//newData.forEach(System.out::println);
		
		writeTheUpdatedWithWhereDataToFile(newData,table);
		
		System.out.println("Query OK, "+updatedRow+" rows affected");
	}

	private static void writeTheUpdatedWithWhereDataToFile(ArrayList<String> newData, Table table) throws Exception {
		
		String fileName=FirstInit.USER_HOME_DIRECTORY + "/" + FirstInit.DB_FILE_NAME + "/" + table.getTableName()
		+ "_" + table.getDatabase() + ".owndb";
		File myObj = new File(fileName);
		
		Scanner scanner = new Scanner (myObj);
		
		String header= scanner.nextLine();
		header = CryptoUtils.decryptedData(header);
		
		/**
		 *  now we should clear the file 
		 */
		Tools.clearFile(fileName);
		
		Tools.writeToFile(CryptoUtils.encryptData(header), fileName);

		for(String str : newData) {
			Tools.writeToFile(CryptoUtils.encryptData(str), fileName);
			
		}
		
		scanner.close();
	}

	public static int fieldTypeVerification(String field, String valueGiveen, Table table) throws Exception {

		String choice = TableTools.getTypeOfField(field, table.getTableName(), table.getDatabase());
		switch (choice) {
		case "string": {
			if (!valueGiveen.startsWith("'") && !valueGiveen.endsWith("'")) {
				return -1;
			} else {
				return 3;
			}
		}
		case "int": {
			if (valueGiveen.trim().equals("null")) {
				return 23;
			}
			if (Tools.isNumeric(valueGiveen.trim())) {
				if (valueGiveen.contains(".")) {
					return -3;
				}
				return 4;
			} else {
				return -43;
			}
		}
		case "double": {
			if (Tools.isNumeric(valueGiveen.trim())) {
				return 54;
			} else {
				return -3;
			}
		}
		default: {
			return -4;
		}
		}

	}

}
