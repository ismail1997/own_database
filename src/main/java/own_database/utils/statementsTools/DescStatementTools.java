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
		
		//check if the first element of array is 'desc' or 'describe'
		if(!array[0].toLowerCase().equals("desc") || !array[0].toLowerCase().equals("describe")) {
			System.out.println("invalid statement, check the manual of own_db");
			return false;
		}
		//check if the last element of array is ';' 
		if(!array[2].equals(";")) {
			System.out.println("invalid end of desc statement, ");return false;
		}
		
		//check if the second element is a  table name not a reserved word or start with a special character
		if(Tools.checkIfStringContainsWithNumberOrChar(array[1])) {
			System.out.println("invalid desc statement, check the manual of own_db");
			return false ;
		}
		
		//check if the second element is not a reserved word
		if(Constants.reservedWords().contains(array[1])) {
			System.out.printf("syntax error: '%s' is a reserved word, cheack the manual of own_db\n",array[1]);
			return false;
		}
		
		//check if a database is selected 
		String currentDb = UseStatementTools.getTheCurrentSessionDatabase();
		if(currentDb.equals("") || currentDb.equals(null)) {
			System.out.println("No database selected");
			return false; 
		}
		
		//check if table exist in database 
		boolean checkIfTableExistOrNot = TableTools.checkIfTableExistAlreadyInDb(array[1], currentDb);
		if(!checkIfTableExistOrNot) {
			System.out.format("error : '%s' table doesn't exist", array[1]);
			return false;
		}
		
		return true;
	}
	public static boolean descripeTable(String tableName) {
		return false;
	}
	public static void main(String[] args) {
		

	}

}
