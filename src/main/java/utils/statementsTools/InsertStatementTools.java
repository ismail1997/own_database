package utils.statementsTools;

import models.Field;
import models.Table;
import utils.Constants;
import utils.Tools;
import utils.tableTools.TableTools;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InsertStatementTools {


	public static void main( String [] args ) throws Exception {
		String insertOne  = "insert into jee values (4,5,9);";
		String insertTwo = " insert into jee values (4,5,6), (4,4,4); ";

		String insertThree = "insert into jee (id) values (3) ,(5) ;";
		String insertFour = "insert into jee (id) values (4) ;";

		String insertFive = "insert into jee (id,name ) values (3,'ismail');";
		String insertSex = "insert into jee (id,name) values ( 4,'ismail') , (6,'jee');";

		String insertSev  = "insert into jee (id,name,email) values (4,'ismail','email');";
		String insertHei = "insert into users               (id,email,price) values      (4      ,    'ismail','google'),(3,'ismail','swat');";


		/**
		 * first we split the string into two strings : one is 'insert into table_name (columns)'
		 * 											    second is 'values (value1,value3)
		 */

		String chosenString = insertHei.trim().replaceAll("\\s+"," ");


		String splitWithValueKeyword [] = chosenString.split("values");

		/**
		 * check now  the length of the splitWithValueKeyword Array, it should be 2
		 */
		if(splitWithValueKeyword.length != 2){
			System.out.println("invalid insert statement, check the manual for the own db");return;
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
		
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		currentDatabase="alpha";
		
		Table table = TableTools.getTable(keywords[2],currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",keywords[2]);return;
		}

		List<Field> fields = table.getListOfFields();//getting the fields of the table 
		System.out.println(fields);



	}
	
	public static void jpe(String[] args) throws Exception {
		Table table = TableTools.getTable("user","users");
		List<Field> fields = table.getListOfFields();
		System.out.println(fields);

		if(fields != null) return ;

		String insert ="insert into user  values (2,'ismail','ismail') , (3,'shl','ismail'),(4,'shl','ismail');";
		insert = insert.trim().replaceAll("\\s+", " ");

		String [] splitInsert = insert.split("values");
		if(splitInsert.length !=2){
			System.out.println("invalid insert statement, check the manual of the db_own");
			return;
		}

		String firstSplit = splitInsert[0].trim();
		String secondSplit = splitInsert[1].trim();

		/**
		 * the first insert has three possibilities
		 *     1 with no arguments like this insert into user values (3,"ismail"),(3,"ahmed");
		 *     2 with less arguments like this insert into user (id ) values (3),(4);
		 *     3 with full arguments like this insert into user (id , email ) values (3,"ismail"),(4,"mohamed");
		 */

		String firstSplitInsert[] = firstSplit.split("\\(");
		System.out.println(firstSplitInsert.length);

		ArrayList<String> fieldsAdded = new ArrayList<>();
		if(firstSplitInsert.length == 1){
			int sizeOfTableFields = table.getListOfFields().size(); //get the number of fields that table contains
			secondSplit = secondSplit.replaceAll("\\s","");//remove the white space
			char lastCharacter = secondSplit.charAt(secondSplit.length()-1);
			if(lastCharacter!=';'){
				System.out.println("invalid end of statement, missing ';' at the last ");return;
			}
			//if the last character is ';' then we should remove it from the string
			secondSplit=secondSplit.substring(1,secondSplit.length()-2);//we are removing also the parentheses from the beginning and the last
			System.out.println(secondSplit);
			String [] getValues = secondSplit.split("\\),\\(");
			for (String str : getValues) {
				String[] getContentFromValues = str.split(",");
				if(getContentFromValues.length != sizeOfTableFields){//check if the number of given values match the number of columns exist in table
					System.out.println("Column count doesn't match value count at row");return;
				}
				for(String value : getContentFromValues){

				}
			}

			return;
		}else if(firstSplitInsert.length == 2){
			/**
			 * handle the first keyword 'insert into table_name'
			 */
			String keywordsSplit [] = firstSplitInsert[0].split(" ");
			if(keywordsSplit.length != 3){//check if the first of element contains three keywords 'insert' 'into' 'table_name'
				System.out.println("invalid insert statement, check the manual of own_db");return;
			}

			if(!keywordsSplit[0].trim().equalsIgnoreCase("insert")){//check if the statement is started with 'insert'
				System.out.println("error insert statement should start with 'insert' key word");return;
			}
			if(!keywordsSplit[1].trim().equalsIgnoreCase("into")){//check if the into keyword is in the right place
				System.out.println("error: invalid insert statement, check the manual of own_db");return;
			}
			if(Constants.reservedWords().contains(keywordsSplit[2])){//check if table name is not a reserved word
				System.out.printf("invalid table name, '%s' is a reserved word \n",keywordsSplit[2]);return;
			}
			if(Tools.checkIfStringContainsWithNumberOrChar(keywordsSplit[2])){//check if the table name doesn't contain any special characters and starts with number
				System.out.printf("invalid table name, '%s' should not contain any special characters or start with number\n",keywordsSplit[2]);return;
			}

			/**
			 * handle the second statement '(id,email,..)
			 */
			firstSplitInsert[1]=firstSplitInsert[1].trim().substring(0,firstSplitInsert[1].length()-1);

			String fieldSplits [] = firstSplitInsert[1].split(",");
			int numberOfFieldsToAssign = fieldSplits.length;


			for (int i = 0 ; i < fieldSplits.length ; i++){
				if(Constants.reservedWords().contains(fieldSplits[i])){
					System.out.format("invalid field name, '%s' is a reserved type\n",fieldSplits[i]);return;
				}
				if(Tools.checkIfStringContainsWithNumberOrChar(fieldSplits[i])){
					System.out.format("invalid field name, '%s' is not a compatible name \n",fieldSplits[i]);return;
				}
				if(fieldsAdded.contains(fieldSplits[i])){
					System.out.printf("field '%s' already added to statement\n",fieldSplits[i]);return;
				}

				fieldsAdded.add(fieldSplits[i]);
			}

			for(String str : fieldsAdded)//check if the table contains those declared fields
				if(!TableTools.checkIfTableContainsField(str,table.getTableName(),table.getDatabase())){
					System.out.printf("table '%s' doesn't have field named '%s'\n",table.getTableName(),str);
					return;
				}
		}

		System.out.println("_________________________________");

		//System.out.println(secondSplit);
		secondSplit="(2,'ismail'),(3,'shl'),(4,'shl');";
		secondSplit = secondSplit.replaceAll("\\s","");//remove the white space
		char theLastCharacterOfInsertStatement=secondSplit.charAt(secondSplit.length()-1);
		if(theLastCharacterOfInsertStatement!=';'){
			System.out.println("invalid end of insert statement, missing ';' at the end");return;
		}
		secondSplit=secondSplit.substring(0,secondSplit.length()-1);//remove ';' from the last of string

		System.out.println(secondSplit);

		String vals [] = secondSplit.split("\\),");
		for(String str : vals) {
			System.out.println(str.replaceAll("[()]",""));
		}






		}
	}

