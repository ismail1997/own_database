package own_database.utils.tableTools;


import java.util.ArrayList;
import java.util.HashMap;

import own_database.models.Table;
import own_database.utils.Constants;

public class TableTools {
	public static void descriptTable(Table table) {
		
	}
	
	public static boolean creatTable (Table table) {
		
		return false ;
	}
	
	public static void main(String [] args) {
		
		Table table = new Table();
		HashMap<String, String> mapOfFields = new HashMap<String, String>();
		
		
		String createTableString ="create  table ismail ( id int , text String , age date,kora date, jee date ) ;";
		createTableString =createTableString.replaceAll("\\s+"," ");
		String array [] = createTableString.split("\\(");
		
		String firstStatement = array[0].trim().toLowerCase() ;
		System.out.println(firstStatement);
		
		//validate the first statement 
		String arrayOfFirstStm [] = firstStatement.split(" ");
		if(arrayOfFirstStm.length != 3) System.out.println("invalid statement");
		
		//check if the the third string is not a reserved word
		if(Constants.reservedWords().contains(arrayOfFirstStm[2])) {
			System.out.format("invalid statement, %s is a reserved word %n",arrayOfFirstStm[2]);
		}
		String nameOfTable = arrayOfFirstStm[2];
		System.out.println(nameOfTable);
		
		//assign the name to the table object
		table.setTableName(nameOfTable);
		table.setDatabase("");
		
		System.out.println("-----------------------------------------");
		String secondStatement = array[1].trim().toLowerCase();
		System.out.println(secondStatement);
		
		//split the second statement to get fields and the end ; 
		String splitSecondStatement [] = secondStatement.split("\\)");
		
		if(splitSecondStatement.length != 2) {
			System.out.println("invalid end of statement ");
		}
		if (!splitSecondStatement[1].equals(";")) {
			System.out.println("invalid end of statement, missing ';' ");
		}
		System.out.println("------------------------");
		ArrayList<String> createdFields = new ArrayList<String>();
		//now we split the filed statement 
		String fieldsStatement [] = splitSecondStatement[0].split(",");
		for(int r=0 ; r<fieldsStatement.length ; r++) {
			String fieldExtraction [] = fieldsStatement[r].trim().split(" ");
			if(fieldExtraction.length !=2) {
				System.out.println("invalid field inputs");return;
			}else {
				String nameOfField = fieldExtraction[0];
				String typeOfField = fieldExtraction[1];
				
				//check if the field is not repeated in the table creation
				if(createdFields.contains(nameOfField)) {
					System.out.println("duplicate name of "+nameOfField);
					return ;
				}
				//check if the field name is a reserved word or not 
				if(Constants.reservedWords().contains(nameOfField)) {
					System.out.println(nameOfField+" is a reserved word"); return;
				}
				//check if the type exist or not 
				if(!Constants.reservedTypes().contains(typeOfField)) {
					System.out.println("unrecognized type, check the manual of DB_OWN");
					return;
				}
				createdFields.add(nameOfField);
				System.out.println("The field name is:"+nameOfField+", type:"+typeOfField);
				mapOfFields.put(nameOfField, typeOfField);
			}
			
		}
		table.setFields(mapOfFields);
		
		
		System.out.println(table);
		
	}
}
