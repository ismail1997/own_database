package utils.statementsTools;

import models.Field;
import models.Table;
import utils.Constants;
import utils.Tools;
import utils.tableTools.TableTools;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InsertStatementTools {


	public static void main(String [] args) throws Exception {
		String statement ="insert into user (email,id) values ('lm',4333)|('mDE',770) ;";
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database

		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		currentDatabase="mydb";
		/**
		 * first we split the string into two strings : one is 'insert into table_name (columns)'
		 * 											    second is 'values (value1,value3)
		 */

		String chosenString = statement.trim().replaceAll("\\s+"," ");


		String splitWithValueKeyword [] = chosenString.split("values");

		/**
		 * check now  the length of the splitWithValueKeyword Array, it should be 2
		 */
		if(splitWithValueKeyword.length != 2){
			System.out.println("invalid insert statement, check the manual for the own db");return ;
		}

		/**
		 * now we handle the first part of the statement : insert into table_name columns
		 * 	for this one there are three cases :
		 * 		1: insert into table_name
		 * 		2: insert into table_name (all columns)
		 * 		3: insert into table_name (some_of_columns)
		 */
		String firstPartString = splitWithValueKeyword[0].trim(); //the first string  which has insert into table_name
		String secondPartString = splitWithValueKeyword[1].trim();//the second string which has values (),();

		/**
		 * we start with the first string:
		 *    verify the keywords and the table name
		 *    but first we should get the type of the insert
		 *    for example : 'insert into table_name' is not like 'insert into table_name (col1,col3)'
		 *    so we should split the first string and get the type of it
		 */

		String firstStringSplit [] = firstPartString.split("\\(");
		String keywordInsertString ="";
		String columnDeclarationString="";
		
		boolean idPrimaryKey = false;
		
		int typeOfInsert = -1;
		if(firstStringSplit.length == 1){//the type of insert is like this : insert into table_name
			typeOfInsert=1;
			keywordInsertString=firstStringSplit[0];
		}else if(firstStringSplit.length ==2){//the type of insert is like this : insert into table_name (id,email)
			typeOfInsert=2;
			keywordInsertString=firstStringSplit[0];
			columnDeclarationString=firstStringSplit[1].substring(0,firstStringSplit[1].length()-1);
		}else{
			System.out.println("invalid insert type, please check the own_db manual");return;
		}

		/**
		 * now with the keyword insert string: we verify the keywords
		 * here the first part of  insert statement are the same for both types 'insert into table_name'
		 * now we should handle that string, we verify 'insert' , 'into' and 'table_name'
		 */
		String keywords [] = keywordInsertString.split(" ");

		if(keywords.length != 3) {
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[0].equalsIgnoreCase("insert")){//check if the first keyword is insert
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[1].equalsIgnoreCase("into")){//check if the second keyword is into
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(Constants.reservedWords().contains(keywords[2])){//check if the table name is not a reserved word
			System.out.format("invalid insert statement, '%s' is a reserved word%n",keywords[2]);return;
		}

		/**
		 * check if the table exist in the current database and check if the table has those fields
		 */
		
		
		
		
		Table table = TableTools.getTable(keywords[2],currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",keywords[2]);return;
		}

		List<Field> fields = table.getListOfFields();//getting the fields of the table 
		//System.out.println(fields);
		
		/***
		 * now we move to the second part of the insert statement which is fields declarations and values 
		 * here as we said before there are two types :
		 * 			2 : insert into table_name (id,name) 
		 * 			1 : insert into table_name
		 * we should verify for the 2 type 
		 */
		
		////// starting with the second type 
		//System.out.println(columnDeclarationString); //the fields are represented like this : id,email,price
			String columns [] = new String [] {};
					
					if(columnDeclarationString.length()!=0) {
						columns = columnDeclarationString.split(",");
					}
		
		if(columns.length>fields.size()) {//the fields declared in the statement are more than the fields in the table
			System.out.println("ERROR : The fields declared in the statement are more than the fields int the table");return;
		}
		if(columns.length == fields.size()) {
			//in this case the numbers of fields are the same in the table and statement declaration
			//for example table fields [id,name, price] and the statement is 'insert into tableName (id,name,price)
			//now we should verify if the fields are the same in the statement
			insertWithAllfieldsDeclared(fields, columns, table, idPrimaryKey, secondPartString, currentDatabase);
			
			
		}else if(columns.length ==0){
			insertWithSomeFieldsDeclared(fields, columns, table, idPrimaryKey, secondPartString, currentDatabase);
		
		}else if (columns.length < fields.size()) {
			for(String str : columns) {
				System.out.println(str);
			}
			for(Field str : fields) System.out.println(str);
			
			ArrayList<String> fieldsAsString = new ArrayList<String>();
			for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
			//System.out.println(fieldsAsString);
			
			for(String col : columns) {//verify if the columns are the same
				col=col.trim();
				if(!fieldsAsString.contains(col)) {
					System.out.println("ERROR : '"+col+"' field doesn't exist in the "+table.getTableName()+" table.");
					return;
				}
			}
			
			if(fieldsAsString.contains("id")) {
				Field idField = fields.stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
				if(idField.getPrimaryKey().equals("pk")) {
					idPrimaryKey=true;
				}
			}
			
			
			
			
			//now we should check the second part, the values part : 'values (1,'ISMAIL','ISMAIL@GMAIL.COM' ),(...)
			
			//System.out.println(secondPartString);
			//we should verify the cardinal of values is the same as cardinal of column declaration
			// for example if the columns are (id,email,price) the values should be (1,"h","h") not (1,"y","y","y")
			//for that we should split the value part into multiple parts 
			String [] stringValueParts = secondPartString.split("\\|");
			//for(String str : stringValueParts) System.out.println(str);
			
			int i = 1;
			for(String string : stringValueParts) {
				String cardinalValues [] = string.split(",");
				if(columns.length != cardinalValues.length) {//in this part we verify the count of column and value
					System.out.println("ERROR : Column count doesn't match value count at row "+i);
					return ;
				}
				i++;
				}
			
			//now we should get all values into one list of list to iterate over it 
			//  [ [value1],[value2],...]
			ArrayList<String> vals = new ArrayList<String>();
			for(String string : stringValueParts) {
				String verifiedStr = string.replace("(", "");
					   verifiedStr = verifiedStr.replace(")", "");
					   verifiedStr = verifiedStr.replace(";", "");
					   verifiedStr = verifiedStr.replace(",", "<>");
				vals.add(verifiedStr);
			}
			
			System.out.println(vals);
			
			ArrayList<Integer> duplicateID = new ArrayList<>();
			//System.out.println(vals);
			ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
			ArrayList<String> valeurs = new ArrayList<String>();
			
			
			for(String s : vals) {
				String splitedStr [] = s.split("<>");
				for(int k = 0 ; k<splitedStr.length;k++) {
					//System.out.println(splitedStr[k]+"   type of field is  "+
							//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
					
					String fld =splitedStr[k].trim();
					
					
					String typeOfField = TableTools.getTypeOfField(columns[k].trim(), table.getTableName(), table.getDatabase());
					
					switch(typeOfField) {
						case "int":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								if(fld.contains(".")) {
									System.out.println("ERROR : Invalid type, expected integer but given double");return;
								}else {//check now if the field is a primary key or not and if the auto incremented is activated
									//System.out.println(fld+" : is integer");
									
									//check if the field is id and primary key
									//then we should retrieve all IDS to show if it is not repeated
									if(columns[k].trim().equals("id") && idPrimaryKey) {
										ArrayList<Integer> ids=TableTools.getTheIDsIfPrimaryKey(table.getTableName(), currentDatabase);
										
										int id = Integer.valueOf(fld);
										if(ids.contains(id)) {
											System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName()
												);
											return;
										}
										if(duplicateID.contains(id)) {
											System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName());
											return;
										}else {
											duplicateID.add(id);
										}
									}
									
									valeurs.add(columns[k].trim()+":"+fld);
									break;
								}
							}else {//if the field is not numeric then throw an error 
								System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
							}
						}
						case "double":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								//System.out.println(fld+" : is double");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : Invalid type, expected doube but given other type");return ;
							}
						}
						case "char":{//TODO  add char handling 
							break;
						}
						case "date":{//TODO add date handling 
							break;
						}
						case "boolean":{//TODO add boolean handling 
							break;
						}
						default:{//the default type is String
							if(fld.startsWith("'") && fld.endsWith("'")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : You have an error in your SQL syntax near "+fld);return ;
							}
							
						}
					}
				}
				
				listOfValues.add(valeurs);
				valeurs=new ArrayList<>();
			}
			
			System.out.println(listOfValues);
			
			
			for(int z = 0 ; z< listOfValues.size();z++) {
				
				Map<Integer,String> map = new HashMap<Integer,String>();
				
				for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
					String myField = listOfValues.get(z).get(x);
					String fieldName = myField.substring(0,myField.indexOf(":"));
					String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
					int indexOfFieldInTableFields = fieldsAsString.indexOf(fieldName);
					//System.out.println(indexOfFieldInTableFields+" value :"+fieldValue);
					map.put(indexOfFieldInTableFields,fieldValue);
				}
				map=Tools.sortByKeys(map);
				String dataToBeSaved="";
				 for (Integer key: map.keySet()){
			            //System.out.println(key +" = "+map.get(key));
					 dataToBeSaved+=map.get(key)+"\t\t";
			     }
				// TableTools.createTableData(table,dataToBeSaved);
				 System.out.println(dataToBeSaved);
			}
			
		}
	}
	
	
	
	
	
	
	
	public static void validInsertStatement(String statement) throws Exception {
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database

		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		
		/**
		 * first we split the string into two strings : one is 'insert into table_name (columns)'
		 * 											    second is 'values (value1,value3)
		 */

		String chosenString = statement.trim().replaceAll("\\s+"," ");


		String splitWithValueKeyword [] = chosenString.split("values");

		/**
		 * check now  the length of the splitWithValueKeyword Array, it should be 2
		 */
		if(splitWithValueKeyword.length != 2){
			System.out.println("invalid insert statement, check the manual for the own db");return ;
		}

		/**
		 * now we handle the first part of the statement : insert into table_name columns
		 * 	for this one there are three cases :
		 * 		1: insert into table_name
		 * 		2: insert into table_name (all columns)
		 * 		3: insert into table_name (some_of_columns)
		 */
		String firstPartString = splitWithValueKeyword[0].trim(); //the first string  which has insert into table_name
		String secondPartString = splitWithValueKeyword[1].trim();//the second string which has values (),();

		/**
		 * we start with the first string:
		 *    verify the keywords and the table name
		 *    but first we should get the type of the insert
		 *    for example : 'insert into table_name' is not like 'insert into table_name (col1,col3)'
		 *    so we should split the first string and get the type of it
		 */

		String firstStringSplit [] = firstPartString.split("\\(");
		String keywordInsertString ="";
		String columnDeclarationString="";
		
		boolean idPrimaryKey = false;
		
		int typeOfInsert = -1;
		if(firstStringSplit.length == 1){//the type of insert is like this : insert into table_name
			typeOfInsert=1;
			keywordInsertString=firstStringSplit[0];
		}else if(firstStringSplit.length ==2){//the type of insert is like this : insert into table_name (id,email)
			typeOfInsert=2;
			keywordInsertString=firstStringSplit[0];
			columnDeclarationString=firstStringSplit[1].substring(0,firstStringSplit[1].length()-1);
		}else{
			System.out.println("invalid insert type, please check the own_db manual");return;
		}

		/**
		 * now with the keyword insert string: we verify the keywords
		 * here the first part of  insert statement are the same for both types 'insert into table_name'
		 * now we should handle that string, we verify 'insert' , 'into' and 'table_name'
		 */
		String keywords [] = keywordInsertString.split(" ");

		if(keywords.length != 3) {
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[0].equalsIgnoreCase("insert")){//check if the first keyword is insert
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[1].equalsIgnoreCase("into")){//check if the second keyword is into
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(Constants.reservedWords().contains(keywords[2])){//check if the table name is not a reserved word
			System.out.format("invalid insert statement, '%s' is a reserved word%n",keywords[2]);return;
		}

		/**
		 * check if the table exist in the current database and check if the table has those fields
		 */
		
		
		
		
		Table table = TableTools.getTable(keywords[2],currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",keywords[2]);return;
		}

		List<Field> fields = table.getListOfFields();//getting the fields of the table 
		//System.out.println(fields);
		
		/***
		 * now we move to the second part of the insert statement which is fields declarations and values 
		 * here as we said before there are two types :
		 * 			2 : insert into table_name (id,name) 
		 * 			1 : insert into table_name
		 * we should verify for the 2 type 
		 */
		
		////// starting with the second type 
		//System.out.println(columnDeclarationString); //the fields are represented like this : id,email,price
			String columns [] = new String [] {};
					
					if(columnDeclarationString.length()!=0) {
						columns = columnDeclarationString.split(",");
					}
		
		if(columns.length>fields.size()) {//the fields declared in the statement are more than the fields in the table
			System.out.println("ERROR : The fields declared in the statement are more than the fields int the table");return;
		}
		if(columns.length == fields.size()) {
			//in this case the numbers of fields are the same in the table and statement declaration
			//for example table fields [id,name, price] and the statement is 'insert into tableName (id,name,price)
			//now we should verify if the fields are the same in the statement
			insertWithAllfieldsDeclared(fields, columns, table, idPrimaryKey, secondPartString, currentDatabase);
			
			
		}else if(columns.length ==0){
			insertWithSomeFieldsDeclared(fields, columns, table, idPrimaryKey, secondPartString, currentDatabase);
		
		}else if (columns.length < fields.size()) {
			
		}
		
	}
	
	public static void insertWithAllfieldsDeclared(List<Field> fields,String [] columns,Table table,boolean idPrimaryKey,
							String secondPartString,String currentDatabase) throws Exception {
		ArrayList<String> fieldsAsString = new ArrayList<String>();
		for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
		//System.out.println(fieldsAsString);
		
		ArrayList<String> listToPreventDuplicateFields = new ArrayList<String> () ;
		for(String sr : columns) {
			if(listToPreventDuplicateFields.contains(sr.trim())) {
				System.out.format("ERROR : Column '%s' specified twice%n",sr);return;
			}
			listToPreventDuplicateFields.add(sr.trim());
		}
		
		for(String col : columns) {//verify if the columns are the same
			col=col.trim();
			if(!fieldsAsString.contains(col)) {
				System.out.println("ERROR : '"+col+"' field doesn't exist in the "+table.getTableName()+" table.");
				return;
			}
		}
		
		if(fieldsAsString.contains("id")) {
			Field idField = fields.stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
			if(idField.getPrimaryKey().equals("pk")) {
				idPrimaryKey=true;
			}
		}
		
		
		
		
		//now we should check the second part, the values part : 'values (1,'ISMAIL','ISMAIL@GMAIL.COM' ),(...)
		
		//System.out.println(secondPartString);
		//we should verify the cardinal of values is the same as cardinal of column declaration
		// for example if the columns are (id,email,price) the values should be (1,"h","h") not (1,"y","y","y")
		//for that we should split the value part into multiple parts 
		String [] stringValueParts = secondPartString.split("\\|");
		//for(String str : stringValueParts) System.out.println(str);
		
		int i = 1;
		for(String string : stringValueParts) {
			String cardinalValues [] = string.split(",");
			if(columns.length != cardinalValues.length) {//in this part we verify the count of column and value
				System.out.println("ERROR : Column count doesn't match value count at row "+i);
				return ;
			}
			i++;
			}
		
		//now we should get all values into one list of list to iterate over it 
		//  [ [value1],[value2],...]
		ArrayList<String> vals = new ArrayList<String>();
		for(String string : stringValueParts) {
			String verifiedStr = string.replace("(", "");
				   verifiedStr = verifiedStr.replace(")", "");
				   verifiedStr = verifiedStr.replace(";", "");
				   verifiedStr = verifiedStr.replace(",", "<>");
			vals.add(verifiedStr);
		}
		
		ArrayList<Integer> duplicateID = new ArrayList<>();
		//System.out.println(vals);
		ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
		ArrayList<String> valeurs = new ArrayList<String>();
		
		for(String s : vals) {
			String splitedStr [] = s.split("<>");
			for(int k = 0 ; k<splitedStr.length;k++) {
				//System.out.println(splitedStr[k]+"   type of field is  "+
						//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
				
				String fld =splitedStr[k].trim();
				
				
				String typeOfField = TableTools.getTypeOfField(columns[k].trim(), table.getTableName(), table.getDatabase());
				
				switch(typeOfField) {
					case "int":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							if(fld.contains(".")) {
								System.out.println("ERROR : Invalid type, expected integer but given double");return;
							}else {//check now if the field is a primary key or not and if the auto incremented is activated
								//System.out.println(fld+" : is integer");
								
								//check if the field is id and primary key
								//then we should retrieve all IDS to show if it is not repeated
								if(columns[k].trim().equals("id") && idPrimaryKey) {
									ArrayList<Integer> ids=TableTools.getTheIDsIfPrimaryKey(table.getTableName(), currentDatabase);
									
									int id = Integer.valueOf(fld);
									if(ids.contains(id)) {
										System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName()
											);
										return;
									}
									if(duplicateID.contains(id)) {
										System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName());
										return;
									}else {
										duplicateID.add(id);
									}
								}
								
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}
						}else {//if the field is not numeric then throw an error 
							System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
						}
					}
					case "double":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							//System.out.println(fld+" : is double");
							valeurs.add(columns[k].trim()+":"+fld);
							break;
						}else {
							System.out.println("ERROR : Invalid type, expected doube but given other type");return ;
						}
					}
					case "char":{//TODO  add char handling 
						break;
					}
					case "date":{//TODO add date handling 
						break;
					}
					case "boolean":{//TODO add boolean handling 
						break;
					}
					default:{//the default type is String
						if(fld.startsWith("'") && fld.endsWith("'")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(columns[k].trim()+":"+fld);
							break;
						}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(columns[k].trim()+":"+fld);
							break;
						}else {
							System.out.println("ERROR : You have an error in your SQL syntax near "+fld);return ;
						}
						
					}
				}
			}
			
			listOfValues.add(valeurs);
			valeurs=new ArrayList<>();
		}
		
		
		//System.out.println(listOfValues);
		for(int z = 0 ; z< listOfValues.size();z++) {
			
			Map<Integer,String> map = new HashMap<Integer,String>();
			
			for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
				String myField = listOfValues.get(z).get(x);
				String fieldName = myField.substring(0,myField.indexOf(":"));
				String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
				int indexOfFieldInTableFields = fieldsAsString.indexOf(fieldName);
				//System.out.println(indexOfFieldInTableFields+" value :"+fieldValue);
				map.put(indexOfFieldInTableFields,fieldValue);
			}
			map=Tools.sortByKeys(map);
			String dataToBeSaved="";
			 for (Integer key: map.keySet()){
		            //System.out.println(key +" = "+map.get(key));
				 dataToBeSaved+=map.get(key)+"\t\t";
		     }
			 TableTools.createTableData(table,dataToBeSaved);
		}
		System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
		
		
	}

	public static void insertWithSomeFieldsDeclared(List<Field> fields,String [] columns,Table table,boolean idPrimaryKey,
							String secondPartString, String currentDatabase) throws Exception {

		ArrayList<String> fieldsAsString = new ArrayList<String>();
		for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
		//System.out.println(fieldsAsString);
		
		
		//check the value parts 
		String [] stringValueParts = secondPartString.split("\\|");
		
		int i = 1;
		for(String string : stringValueParts) {
			String cardinalValues [] = string.split(",");
			if(fields.size() != cardinalValues.length) {//in this part we verify the count of column and value
				System.out.println("ERROR : Column count doesn't match value count at row "+i);
				return ;
			}
			i++;
			}
		
		//now we should get all values into one list of list to iterate over it 
		//  [ [value1],[value2],...]
		ArrayList<String> vals = new ArrayList<String>();
		for(String string : stringValueParts) {
			String verifiedStr = string.replace("(", "");
				   verifiedStr = verifiedStr.replace(")", "");
				   verifiedStr = verifiedStr.replace(";", "");
				   verifiedStr = verifiedStr.replace(",", "<>");
			vals.add(verifiedStr);
		}
		//System.out.println(vals);
		
		ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
		ArrayList<String> valeurs = new ArrayList<String>();
		
		if(fieldsAsString.contains("id")) {
			Field idField = fields.stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
			if(idField.getPrimaryKey().equals("pk")) {
				idPrimaryKey=true;
			}
		}
		
		ArrayList<Integer> duplicateID = new ArrayList<>();
		
		for(String s : vals) {
			String splitedStr [] = s.split("<>");
			for(int k = 0 ; k<splitedStr.length;k++) {
				//System.out.println(splitedStr[k]+"   type of field is  "+
						//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
				
				String fld =splitedStr[k].trim();
									
				String typeOfField = TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase());
				
				switch(typeOfField) {
				case "int":{
					if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
						if(fld.contains(".")) {
							System.out.println("ERROR : Invalid type, expected integer but given double");return;
						}else {//check now if the field is a primary key or not and if the auto incremented is activated
							//System.out.println(fld+" : is integer");
							//check if the field is id and primary key
							//then we should retrieve all IDS to show if it is not repeated
							if(fieldsAsString.get(k).equals("id") && idPrimaryKey) {
								ArrayList<Integer> ids=TableTools.getTheIDsIfPrimaryKey(table.getTableName(), currentDatabase);
								
								int id = Integer.valueOf(fld);
								if(ids.contains(id)) {
									System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName()
										);
									return;
								}
								if(duplicateID.contains(id)) {
									System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName());
									return;
								}else {
									duplicateID.add(id);
								}
								
							}
							
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}
					}else {//if the field is not numeric then throw an error 
						System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
					}
				}
				case "double":{
					if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
						//System.out.println(fld+" : is double");
						valeurs.add(fieldsAsString.get(k)+":"+fld);
						break;
					}else {
						System.out.println("ERROR : Invalid type, expected double but given other type");return ;
					}
				}
				case "char":{//TODO  add char handling 
					break;
				}
				case "date":{//TODO add date handling 
					break;
				}
				case "boolean":{//TODO add boolean handling 
					break;
				}
				default:{//the default type is String
					if(fld.startsWith("'") && fld.endsWith("'")) {
						//System.out.println(fld+ " : is string");
						valeurs.add(fieldsAsString.get(k)+":"+fld);
						break;
					}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
						//System.out.println(fld+ " : is string");
						valeurs.add(fieldsAsString.get(k)+":"+fld);
						break;
					}else {
						System.out.println("ERROR : You have an error in your SQL syntax near : "+fld+" 'string should start with quotes' ");return ;
					}
					
				}
			}
			}
			listOfValues.add(valeurs);
			valeurs=new ArrayList<>();
		}
		
		//System.out.println(listOfValues);
		
		
		for(int z = 0 ; z< listOfValues.size();z++) {
			
			String dataToBeSaved="";
			
			 for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
					String myField = listOfValues.get(z).get(x);
					String fieldName = myField.substring(0,myField.indexOf(":"));
					String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
					 dataToBeSaved +=fieldValue +"\t\t";
				}
			// System.out.println(dataToBeSaved);
			 TableTools.createTableData(table,dataToBeSaved);
		}
		System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
	}








	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void validInsertStatementOld2(String statement) throws Exception {
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database

		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		
		/**
		 * first we split the string into two strings : one is 'insert into table_name (columns)'
		 * 											    second is 'values (value1,value3)
		 */

		String chosenString = statement.trim().replaceAll("\\s+"," ");


		String splitWithValueKeyword [] = chosenString.split("values");

		/**
		 * check now  the length of the splitWithValueKeyword Array, it should be 2
		 */
		if(splitWithValueKeyword.length != 2){
			System.out.println("invalid insert statement, check the manual for the own db");return ;
		}

		/**
		 * now we handle the first part of the statement : insert into table_name columns
		 * 	for this one there are three cases :
		 * 		1: insert into table_name
		 * 		2: insert into table_name (all columns)
		 * 		3: insert into table_name (some_of_columns)
		 */
		String firstPartString = splitWithValueKeyword[0].trim(); //the first string  which has insert into table_name
		String secondPartString = splitWithValueKeyword[1].trim();//the second string which has values (),();

		/**
		 * we start with the first string:
		 *    verify the keywords and the table name
		 *    but first we should get the type of the insert
		 *    for example : 'insert into table_name' is not like 'insert into table_name (col1,col3)'
		 *    so we should split the first string and get the type of it
		 */

		String firstStringSplit [] = firstPartString.split("\\(");
		String keywordInsertString ="";
		String columnDeclarationString="";
		
		boolean idPrimaryKey = false;
		
		int typeOfInsert = -1;
		if(firstStringSplit.length == 1){//the type of insert is like this : insert into table_name
			typeOfInsert=1;
			keywordInsertString=firstStringSplit[0];
		}else if(firstStringSplit.length ==2){//the type of insert is like this : insert into table_name (id,email)
			typeOfInsert=2;
			keywordInsertString=firstStringSplit[0];
			columnDeclarationString=firstStringSplit[1].substring(0,firstStringSplit[1].length()-1);
		}else{
			System.out.println("invalid insert type, please check the own_db manual");return;
		}

		/**
		 * now with the keyword insert string: we verify the keywords
		 * here the first part of  insert statement are the same for both types 'insert into table_name'
		 * now we should handle that string, we verify 'insert' , 'into' and 'table_name'
		 */
		String keywords [] = keywordInsertString.split(" ");

		if(keywords.length != 3) {
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[0].equalsIgnoreCase("insert")){//check if the first keyword is insert
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[1].equalsIgnoreCase("into")){//check if the second keyword is into
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(Constants.reservedWords().contains(keywords[2])){//check if the table name is not a reserved word
			System.out.format("invalid insert statement, '%s' is a reserved word%n",keywords[2]);return;
		}

		/**
		 * check if the table exist in the current database and check if the table has those fields
		 */
		
		
		
		
		Table table = TableTools.getTable(keywords[2],currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",keywords[2]);return;
		}

		List<Field> fields = table.getListOfFields();//getting the fields of the table 
		//System.out.println(fields);
		
		/***
		 * now we move to the second part of the insert statement which is fields declarations and values 
		 * here as we said before there are two types :
		 * 			2 : insert into table_name (id,name) 
		 * 			1 : insert into table_name
		 * we should verify for the 2 type 
		 */
		
		////// starting with the second type 
		//System.out.println(columnDeclarationString); //the fields are represented like this : id,email,price
			String columns [] = new String [] {};
					
					if(columnDeclarationString.length()!=0) {
						columns = columnDeclarationString.split(",");
					}
		
		if(columns.length>fields.size()) {//the fields declared in the statement are more than the fields in the table
			System.out.println("ERROR : The fields declared in the statement are more than the fields int the table");return;
		}
		if(columns.length == fields.size()) {
			//in this case the numbers of fields are the same in the table and statement declaration
			//for example table fields [id,name, price] and the statement is 'insert into tableName (id,name,price)
			//now we should verify if the fields are the same in the statement
			
			ArrayList<String> fieldsAsString = new ArrayList<String>();
			for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
			//System.out.println(fieldsAsString);
			
			for(String col : columns) {//verify if the columns are the same
				col=col.trim();
				if(!fieldsAsString.contains(col)) {
					System.out.println("ERROR : '"+col+"' field doesn't exist in the "+table.getTableName()+" table.");
					return;
				}
			}
			
			if(fieldsAsString.contains("id")) {
				Field idField = fields.stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
				if(idField.getPrimaryKey().equals("pk")) {
					idPrimaryKey=true;
				}
			}
			
			
			
			
			//now we should check the second part, the values part : 'values (1,'ISMAIL','ISMAIL@GMAIL.COM' ),(...)
			
			//System.out.println(secondPartString);
			//we should verify the cardinal of values is the same as cardinal of column declaration
			// for example if the columns are (id,email,price) the values should be (1,"h","h") not (1,"y","y","y")
			//for that we should split the value part into multiple parts 
			String [] stringValueParts = secondPartString.split("\\|");
			//for(String str : stringValueParts) System.out.println(str);
			
			int i = 1;
			for(String string : stringValueParts) {
				String cardinalValues [] = string.split(",");
				if(columns.length != cardinalValues.length) {//in this part we verify the count of column and value
					System.out.println("ERROR : Column count doesn't match value count at row "+i);
					return ;
				}
				i++;
 			}
			
			//now we should get all values into one list of list to iterate over it 
			//  [ [value1],[value2],...]
			ArrayList<String> vals = new ArrayList<String>();
			for(String string : stringValueParts) {
				String verifiedStr = string.replace("(", "");
					   verifiedStr = verifiedStr.replace(")", "");
					   verifiedStr = verifiedStr.replace(";", "");
					   verifiedStr = verifiedStr.replace(",", "<>");
				vals.add(verifiedStr);
			}
			
			ArrayList<Integer> duplicateID = new ArrayList<>();
			//System.out.println(vals);
			ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
			ArrayList<String> valeurs = new ArrayList<String>();
			
			for(String s : vals) {
				String splitedStr [] = s.split("<>");
				for(int k = 0 ; k<splitedStr.length;k++) {
					//System.out.println(splitedStr[k]+"   type of field is  "+
							//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
					
					String fld =splitedStr[k].trim();
					
					
					String typeOfField = TableTools.getTypeOfField(columns[k].trim(), table.getTableName(), table.getDatabase());
					
					switch(typeOfField) {
						case "int":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								if(fld.contains(".")) {
									System.out.println("ERROR : Invalid type, expected integer but given double");return;
								}else {//check now if the field is a primary key or not and if the auto incremented is activated
									//System.out.println(fld+" : is integer");
									
									//check if the field is id and primary key
									//then we should retrieve all IDS to show if it is not repeated
									if(columns[k].trim().equals("id") && idPrimaryKey) {
										ArrayList<Integer> ids=TableTools.getTheIDsIfPrimaryKey(table.getTableName(), currentDatabase);
										
										int id = Integer.valueOf(fld);
										if(ids.contains(id)) {
											System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName()
												);
											return;
										}
										if(duplicateID.contains(id)) {
											System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName());
											return;
										}else {
											duplicateID.add(id);
										}
									}
									
									valeurs.add(columns[k].trim()+":"+fld);
									break;
								}
							}else {//if the field is not numeric then throw an error 
								System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
							}
						}
						case "double":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								//System.out.println(fld+" : is double");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : Invalid type, expected doube but given other type");return ;
							}
						}
						case "char":{//TODO  add char handling 
							break;
						}
						case "date":{//TODO add date handling 
							break;
						}
						case "boolean":{//TODO add boolean handling 
							break;
						}
						default:{//the default type is String
							if(fld.startsWith("'") && fld.endsWith("'")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : You have an error in your SQL syntax near "+fld);return ;
							}
							
						}
					}
				}
				
				listOfValues.add(valeurs);
				valeurs=new ArrayList<>();
			}
			
			
			//System.out.println(listOfValues);
			for(int z = 0 ; z< listOfValues.size();z++) {
				
				Map<Integer,String> map = new HashMap<Integer,String>();
				
				for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
					String myField = listOfValues.get(z).get(x);
					String fieldName = myField.substring(0,myField.indexOf(":"));
					String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
					int indexOfFieldInTableFields = fieldsAsString.indexOf(fieldName);
					//System.out.println(indexOfFieldInTableFields+" value :"+fieldValue);
					map.put(indexOfFieldInTableFields,fieldValue);
				}
				map=Tools.sortByKeys(map);
				String dataToBeSaved="";
				 for (Integer key: map.keySet()){
			            //System.out.println(key +" = "+map.get(key));
					 dataToBeSaved+=map.get(key)+"\t\t";
			     }
				 TableTools.createTableData(table,dataToBeSaved);
			}
			System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
			
			
			
		}else if(columns.length ==0){
			
			ArrayList<String> fieldsAsString = new ArrayList<String>();
			for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
			//System.out.println(fieldsAsString);
			
			
			//check the value parts 
			String [] stringValueParts = secondPartString.split("\\|");
			
			int i = 1;
			for(String string : stringValueParts) {
				String cardinalValues [] = string.split(",");
				if(fields.size() != cardinalValues.length) {//in this part we verify the count of column and value
					System.out.println("ERROR : Column count doesn't match value count at row "+i);
					return ;
				}
				i++;
 			}
			
			//now we should get all values into one list of list to iterate over it 
			//  [ [value1],[value2],...]
			ArrayList<String> vals = new ArrayList<String>();
			for(String string : stringValueParts) {
				String verifiedStr = string.replace("(", "");
					   verifiedStr = verifiedStr.replace(")", "");
					   verifiedStr = verifiedStr.replace(";", "");
					   verifiedStr = verifiedStr.replace(",", "<>");
				vals.add(verifiedStr);
			}
			//System.out.println(vals);
			
			ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
			ArrayList<String> valeurs = new ArrayList<String>();
			
			if(fieldsAsString.contains("id")) {
				Field idField = fields.stream().filter(x->x.getFieldName().equals("id")).findAny().orElse(null);
				if(idField.getPrimaryKey().equals("pk")) {
					idPrimaryKey=true;
				}
			}
			
			ArrayList<Integer> duplicateID = new ArrayList<>();
			
			for(String s : vals) {
				String splitedStr [] = s.split("<>");
				for(int k = 0 ; k<splitedStr.length;k++) {
					//System.out.println(splitedStr[k]+"   type of field is  "+
							//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
					
					String fld =splitedStr[k].trim();
										
					String typeOfField = TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase());
					
					switch(typeOfField) {
					case "int":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							if(fld.contains(".")) {
								System.out.println("ERROR : Invalid type, expected integer but given double");return;
							}else {//check now if the field is a primary key or not and if the auto incremented is activated
								//System.out.println(fld+" : is integer");
								//check if the field is id and primary key
								//then we should retrieve all IDS to show if it is not repeated
								if(fieldsAsString.get(k).equals("id") && idPrimaryKey) {
									ArrayList<Integer> ids=TableTools.getTheIDsIfPrimaryKey(table.getTableName(), currentDatabase);
									
									int id = Integer.valueOf(fld);
									if(ids.contains(id)) {
										System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName()
											);
										return;
									}
									if(duplicateID.contains(id)) {
										System.out.format("Duplicate entry '%s' for key '%s.PRIMARY'%n",id,table.getTableName());
										return;
									}else {
										duplicateID.add(id);
									}
									
								}
								
								valeurs.add(fieldsAsString.get(k)+":"+fld);
								break;
							}
						}else {//if the field is not numeric then throw an error 
							System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
						}
					}
					case "double":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							//System.out.println(fld+" : is double");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else {
							System.out.println("ERROR : Invalid type, expected double but given other type");return ;
						}
					}
					case "char":{//TODO  add char handling 
						break;
					}
					case "date":{//TODO add date handling 
						break;
					}
					case "boolean":{//TODO add boolean handling 
						break;
					}
					default:{//the default type is String
						if(fld.startsWith("'") && fld.endsWith("'")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else {
							System.out.println("ERROR : You have an error in your SQL syntax near : "+fld+" 'string should start with quotes' ");return ;
						}
						
					}
				}
				}
				listOfValues.add(valeurs);
				valeurs=new ArrayList<>();
			}
			
			//System.out.println(listOfValues);
			
			
			for(int z = 0 ; z< listOfValues.size();z++) {
				
				String dataToBeSaved="";
				
				 for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
						String myField = listOfValues.get(z).get(x);
						String fieldName = myField.substring(0,myField.indexOf(":"));
						String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
						 dataToBeSaved +=fieldValue +"\t\t";
					}
				// System.out.println(dataToBeSaved);
				 TableTools.createTableData(table,dataToBeSaved);
			}
			System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
		}
		
	}
	
	public static void validInsertStatementOld1(String statement) throws Exception {
		
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		
		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		
		/**
		 * first we split the string into two strings : one is 'insert into table_name (columns)'
		 * 											    second is 'values (value1,value3)
		 */

		String chosenString = statement.trim().replaceAll("\\s+"," ");


		String splitWithValueKeyword [] = chosenString.split("values");

		/**
		 * check now  the length of the splitWithValueKeyword Array, it should be 2
		 */
		if(splitWithValueKeyword.length != 2){
			System.out.println("invalid insert statement, check the manual for the own db");return ;
		}

		/**
		 * now we handle the first part of the statement : insert into table_name columns
		 * 	for this one there are three cases :
		 * 		1: insert into table_name
		 * 		2: insert into table_name (all columns)
		 * 		3: insert into table_name (some_of_columns)
		 */
		String firstPartString = splitWithValueKeyword[0].trim(); //the first string  which has insert into table_name
		String secondPartString = splitWithValueKeyword[1].trim();//the second string which has values (),();

		/**
		 * we start with the first string:
		 *    verify the keywords and the table name
		 *    but first we should get the type of the insert
		 *    for example : 'insert into table_name' is not like 'insert into table_name (col1,col3)'
		 *    so we should split the first string and get the type of it
		 */

		String firstStringSplit [] = firstPartString.split("\\(");
		String keywordInsertString ="";
		String columnDeclarationString="";
		int typeOfInsert = -1;
		if(firstStringSplit.length == 1){//the type of insert is like this : insert into table_name
			typeOfInsert=1;
			keywordInsertString=firstStringSplit[0];
		}else if(firstStringSplit.length ==2){//the type of insert is like this : insert into table_name (id,email)
			typeOfInsert=2;
			keywordInsertString=firstStringSplit[0];
			columnDeclarationString=firstStringSplit[1].substring(0,firstStringSplit[1].length()-1);
		}else{
			System.out.println("invalid insert type, please check the own_db manual");return;
		}

		/**
		 * now with the keyword insert string: we verify the keywords
		 * here the first part of  insert statement are the same for both types 'insert into table_name'
		 * now we should handle that string, we verify 'insert' , 'into' and 'table_name'
		 */
		String keywords [] = keywordInsertString.split(" ");

		if(keywords.length != 3) {
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[0].equalsIgnoreCase("insert")){//check if the first keyword is insert
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(!keywords[1].equalsIgnoreCase("into")){//check if the second keyword is into
			System.out.println("invalid insert statement, please check the manual");return;
		}
		if(Constants.reservedWords().contains(keywords[2])){//check if the table name is not a reserved word
			System.out.format("invalid insert statement, '%s' is a reserved word%n",keywords[2]);return;
		}

		/**
		 * check if the table exist in the current database and check if the table has those fields
		 */
		
		
		
		
		Table table = TableTools.getTable(keywords[2],currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",keywords[2]);return;
		}

		List<Field> fields = table.getListOfFields();//getting the fields of the table 
		//System.out.println(fields);
		
		/***
		 * now we move to the second part of the insert statement which is fields declarations and values 
		 * here as we said before there are two types :
		 * 			2 : insert into table_name (id,name) 
		 * 			1 : insert into table_name
		 * we should verify for the 2 type 
		 */
		
		////// starting with the second type 
		//System.out.println(columnDeclarationString); //the fields are represented like this : id,email,price
			String columns [] = new String [] {};
					
					if(columnDeclarationString.length()!=0) {
						columns = columnDeclarationString.split(",");
					}
		
		if(columns.length>fields.size()) {//the fields declared in the statement are more than the fields in the table
			System.out.println("ERROR : The fields declared in the statement are more than the fields int the table");return;
		}
		if(columns.length == fields.size()) {
			//in this case the numbers of fields are the same in the table and statement declaration
			//for example table fields [id,name, price] and the statement is 'insert into tableName (id,name,price)
			//now we should verify if the fields are the same in the statement
			
			ArrayList<String> fieldsAsString = new ArrayList<String>();
			for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
			//System.out.println(fieldsAsString);
			
			for(String col : columns) {//verify if the columns are the same
				col=col.trim();
				if(!fieldsAsString.contains(col)) {
					System.out.println("ERROR : '"+col+"' field doesn't exist in the "+table.getTableName()+" table.");
					return;
				}
			}
			
			
			
			
			//now we should check the second part, the values part : 'values (1,'ISMAIL','ISMAIL@GMAIL.COM' ),(...)
			
			//System.out.println(secondPartString);
			//we should verify the cardinal of values is the same as cardinal of column declaration
			// for example if the columns are (id,email,price) the values should be (1,"h","h") not (1,"y","y","y")
			//for that we should split the value part into multiple parts 
			String [] stringValueParts = secondPartString.split("\\|");
			//for(String str : stringValueParts) System.out.println(str);
			
			int i = 1;
			for(String string : stringValueParts) {
				String cardinalValues [] = string.split(",");
				if(columns.length != cardinalValues.length) {//in this part we verify the count of column and value
					System.out.println("ERROR : Column count doesn't match value count at row "+i);
					return ;
				}
				i++;
 			}
			
			//now we should get all values into one list of list to iterate over it 
			//  [ [value1],[value2],...]
			ArrayList<String> vals = new ArrayList<String>();
			for(String string : stringValueParts) {
				String verifiedStr = string.replace("(", "");
					   verifiedStr = verifiedStr.replace(")", "");
					   verifiedStr = verifiedStr.replace(";", "");
					   verifiedStr = verifiedStr.replace(",", "<>");
				vals.add(verifiedStr);
			}
			
			
			//System.out.println(vals);
			ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
			ArrayList<String> valeurs = new ArrayList<String>();
			
			for(String s : vals) {
				String splitedStr [] = s.split("<>");
				for(int k = 0 ; k<splitedStr.length;k++) {
					//System.out.println(splitedStr[k]+"   type of field is  "+
							//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
					
					String fld =splitedStr[k].trim();
					
					
					String typeOfField = TableTools.getTypeOfField(columns[k].trim(), table.getTableName(), table.getDatabase());
					
					switch(typeOfField) {
						case "int":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								if(fld.contains(".")) {
									System.out.println("ERROR : Invalid type, expected integer but given double");return;
								}else {//check now if the field is a primary key or not and if the auto incremented is activated
									//System.out.println(fld+" : is integer");
									valeurs.add(columns[k].trim()+":"+fld);
									break;
								}
							}else {//if the field is not numeric then throw an error 
								System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
							}
						}
						case "double":{
							if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
								//System.out.println(fld+" : is double");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : Invalid type, expected doube but given other type");return ;
							}
						}
						case "char":{//TODO  add char handling 
							break;
						}
						case "date":{//TODO add date handling 
							break;
						}
						case "boolean":{//TODO add boolean handling 
							break;
						}
						default:{//the default type is String
							if(fld.startsWith("'") && fld.endsWith("'")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
								//System.out.println(fld+ " : is string");
								valeurs.add(columns[k].trim()+":"+fld);
								break;
							}else {
								System.out.println("ERROR : You have an error in your SQL syntax near "+fld);return ;
							}
							
						}
					}
				}
				
				listOfValues.add(valeurs);
				valeurs=new ArrayList<>();
			}
			
			
			//System.out.println(listOfValues);
			for(int z = 0 ; z< listOfValues.size();z++) {
				
				Map<Integer,String> map = new HashMap<Integer,String>();
				
				for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
					String myField = listOfValues.get(z).get(x);
					String fieldName = myField.substring(0,myField.indexOf(":"));
					String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
					int indexOfFieldInTableFields = fieldsAsString.indexOf(fieldName);
					//System.out.println(indexOfFieldInTableFields+" value :"+fieldValue);
					map.put(indexOfFieldInTableFields,fieldValue);
				}
				map=Tools.sortByKeys(map);
				String dataToBeSaved="";
				 for (Integer key: map.keySet()){
			            //System.out.println(key +" = "+map.get(key));
					 dataToBeSaved+=map.get(key)+"\t\t";
			     }
				 TableTools.createTableData(table,dataToBeSaved);
			}
			System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
			
			
			
		}else if(columns.length ==0){
			
			ArrayList<String> fieldsAsString = new ArrayList<String>();
			for(Field fi : fields) {fieldsAsString.add(fi.getFieldName());}//convert fields to string list to compare it with statement
			//System.out.println(fieldsAsString);
			
			
			//check the value parts 
			String [] stringValueParts = secondPartString.split("\\|");
			
			int i = 1;
			for(String string : stringValueParts) {
				String cardinalValues [] = string.split(",");
				if(fields.size() != cardinalValues.length) {//in this part we verify the count of column and value
					System.out.println("ERROR : Column count doesn't match value count at row "+i);
					return ;
				}
				i++;
 			}
			
			//now we should get all values into one list of list to iterate over it 
			//  [ [value1],[value2],...]
			ArrayList<String> vals = new ArrayList<String>();
			for(String string : stringValueParts) {
				String verifiedStr = string.replace("(", "");
					   verifiedStr = verifiedStr.replace(")", "");
					   verifiedStr = verifiedStr.replace(";", "");
					   verifiedStr = verifiedStr.replace(",", "<>");
				vals.add(verifiedStr);
			}
			//System.out.println(vals);
			
			ArrayList<ArrayList<String>> listOfValues = new ArrayList<ArrayList<String>>();
			ArrayList<String> valeurs = new ArrayList<String>();
			
			
			
			
			for(String s : vals) {
				String splitedStr [] = s.split("<>");
				for(int k = 0 ; k<splitedStr.length;k++) {
					//System.out.println(splitedStr[k]+"   type of field is  "+
							//TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase()));
					
					String fld =splitedStr[k].trim();
										
					String typeOfField = TableTools.getTypeOfField(fieldsAsString.get(k), table.getTableName(), table.getDatabase());
					
					switch(typeOfField) {
					case "int":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							if(fld.contains(".")) {
								System.out.println("ERROR : Invalid type, expected integer but given double");return;
							}else {//check now if the field is a primary key or not and if the auto incremented is activated
								//System.out.println(fld+" : is integer");
								valeurs.add(fieldsAsString.get(k)+":"+fld);
								break;
							}
						}else {//if the field is not numeric then throw an error 
							System.out.println("ERROR : Invalid type, expected integer but given other type");return ;
						}
					}
					case "double":{
						if(Tools.isNumeric(fld.trim())) {//check if the field is numeric ( integer or double ) 
							//System.out.println(fld+" : is double");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else {
							System.out.println("ERROR : Invalid type, expected double but given other type");return ;
						}
					}
					case "char":{//TODO  add char handling 
						break;
					}
					case "date":{//TODO add date handling 
						break;
					}
					case "boolean":{//TODO add boolean handling 
						break;
					}
					default:{//the default type is String
						if(fld.startsWith("'") && fld.endsWith("'")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else if(fld.startsWith("\"") && fld.endsWith("\"")) {
							//System.out.println(fld+ " : is string");
							valeurs.add(fieldsAsString.get(k)+":"+fld);
							break;
						}else {
							System.out.println("ERROR : You have an error in your SQL syntax near : "+fld+" 'string should start with quotes' ");return ;
						}
						
					}
				}
				}
				listOfValues.add(valeurs);
				valeurs=new ArrayList<>();
			}
			
			//System.out.println(listOfValues);
			
			
			for(int z = 0 ; z< listOfValues.size();z++) {
				
				String dataToBeSaved="";
				
				 for(int x = 0 ; x< listOfValues.get(z).size(); x++) {
						String myField = listOfValues.get(z).get(x);
						String fieldName = myField.substring(0,myField.indexOf(":"));
						String fieldValue= myField.substring(myField.indexOf(":")+1,myField.length());
						 dataToBeSaved +=fieldValue +"\t\t";
					}
				// System.out.println(dataToBeSaved);
				 TableTools.createTableData(table,dataToBeSaved);
			}
			System.out.println("Query OK, "+listOfValues.size()+" row affected");return;
		}
		
	}
	
	
	
}



