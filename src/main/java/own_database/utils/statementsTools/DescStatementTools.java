package own_database.utils.statementsTools;

import own_database.models.Table;
import own_database.utils.Constants;
import own_database.utils.Tools;
import own_database.utils.tableTools.TableTools;

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
			System.out.format("error : '%s' table doesn't exist", array[1]);
			return false;
		}
		
		System.out.println("good descriping the table");
		
		return true;
	}
	public static boolean descripeTable(String tableName) {
		return false;
	}
	public static void main(String[] args) {
		

	}

}
