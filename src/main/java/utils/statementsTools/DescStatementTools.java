package utils.statementsTools;

import java.util.OptionalInt;

import models.Database;
import models.Field;
import models.Table;
import utils.Constants;
import utils.Tools;
import utils.tableTools.TableTools;

public class DescStatementTools {
	
	
	
	public static boolean validDescripeStatement(String statement) throws Exception {
		if(statement.equals("")) return false;
		
		String array [] = statement.trim().split(" ");
		
		if(array.length!=3) {
			System.out.println(array.length > 3 ? "invalid desc statement, check the manual of own_db" : "invalid desc statement, missing arguments; check the manual of own_db");
			return false;
		}
		
		
		if(!array[0].toLowerCase().equals("desc") && !array[0].toLowerCase().equals("describe")) {//check if the first element of array is 'desc' or 'describe'
			System.out.println("invalid statement, check the manual of own_db");
			return false;
		}
		
		if(!array[2].equals(";")) {//check if the last element of array is ';' 
			System.out.println("invalid end of desc statement, missing ';' at the end");return false;
		}
		
		
		if(Tools.checkIfStringContainsWithNumberOrChar(array[1])) {//check if the table name is a valid name and doesn't contain any special characters or start with number
			System.out.format("invalid table name '%s', check the manual of own_db%n",array[1]);
			return false ;
		}
		
		
		if(Constants.reservedWords().contains(array[1])) {//check if the table name is not a reserved word
			System.out.printf("syntax error: '%s' is a reserved word, cheack the manual of own_db\n",array[1]);
			return false;
		}
		
		
		String currentDb = UseStatementTools.getTheCurrentSessionDatabase();//check if a database is selected 
		if(currentDb.equals("") || currentDb.equals(null)) {
			System.out.println("No database selected");
			return false; 
		}
		
		
		boolean checkIfTableExistOrNot = TableTools.checkIfTableExistAlreadyInDb(array[1], currentDb);
		if(!checkIfTableExistOrNot) {
			System.out.format("error : '%s' table doesn't exist in '%s' database", array[1],currentDb);
			return false;
		}
		
		describeTable(array[1], currentDb);
		
		return true;
	}
	public static void describeTable(String tableName,String databaseName) throws Exception {
		Table table = TableTools.getTable(tableName, databaseName);
		int sizeOfFieldName=0;
		OptionalInt fs = table.getListOfFields().stream()
	                .map(Field::getFieldName)
	                .mapToInt(String::length)
	                .max();
		sizeOfFieldName=fs.getAsInt();
		
		
		int sizeOfFieldType=0;
		OptionalInt ftp = table.getListOfFields().stream()
	                .map(Field::getFieldType)
	                .mapToInt(String::length)
	                .max();
		sizeOfFieldType=ftp.getAsInt();
		
		int sizeOfFieldPrimaryKey=0;
		OptionalInt fpk = table.getListOfFields().stream()
	                .map(Field::getPrimaryKey)
	                .mapToInt(String::length)
	                .max();
		sizeOfFieldPrimaryKey=fpk.getAsInt();
		
		int sizeOfFieldForeignKey=0;
		OptionalInt ffg = table.getListOfFields().stream()
	                .map(Field::getForeignKey)
	                .mapToInt(String::length)
	                .max();
		sizeOfFieldForeignKey=ffg.getAsInt();
		
		int sizeOfKey = sizeOfFieldPrimaryKey <=sizeOfFieldForeignKey ? sizeOfFieldForeignKey :   sizeOfFieldPrimaryKey;
		
		sizeOfFieldName = sizeOfFieldName<"Field".length() ? "Field".length() : sizeOfFieldName;
		sizeOfFieldType = sizeOfFieldType<"Type".length() ? "Type".length() : sizeOfFieldType;
		sizeOfKey = sizeOfKey<"Key".length() ? "Key".length() : sizeOfKey;
		
		
		
		String format = "| %-" + sizeOfFieldName + "s | %-"+sizeOfFieldType+"s | %-"+sizeOfKey+"s |%n";
		
		System.out.println("+-" + Tools.repeatedString('-', sizeOfFieldName) + "-+"+
						   "-" + Tools.repeatedString('-', sizeOfFieldType) + "-+"+
						   "-" + Tools.repeatedString('-', sizeOfKey) + "-+");
		
		System.out.println("| Field" + Tools.repeatedString(' ', sizeOfFieldName - "Field".length()) + " |"
						+" Type" + Tools.repeatedString(' ', sizeOfFieldType - "Type".length()) + " |"+
						" Key" + Tools.repeatedString(' ', sizeOfKey - "Key".length()) + " |");
		
		System.out.println("+-" + Tools.repeatedString('-', sizeOfFieldName) + "-+"+
				   "-" + Tools.repeatedString('-', sizeOfFieldType) + "-+"+
				   "-" + Tools.repeatedString('-', sizeOfKey) + "-+");
		
		for (Field field : table.getListOfFields()) {
			String key ="";
			if(!field.getForeignKey().equals("") && !field.getForeignKey().equals(null)) {
				key=field.getForeignKey();
			}
			if(!field.getPrimaryKey().equals("") && !field.getPrimaryKey().equals(null)) {
				key=field.getPrimaryKey();
			}
			
			System.out.format(format,field.getFieldName(),field.getFieldType(), key );
		}
		System.out.println("+-" + Tools.repeatedString('-', sizeOfFieldName) + "-+"+
				   "-" + Tools.repeatedString('-', sizeOfFieldType) + "-+"+
				   "-" + Tools.repeatedString('-', sizeOfKey) + "-+");
		System.out.println(table.getListOfFields().size() + " rows in set");
		
		
	}
	public static void main(String[] args) throws Exception {
	}

}
