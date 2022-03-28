package own_database.utils.statementsTools;

import java.util.Arrays;
import java.util.List;

import own_database.models.Database;
import own_database.utils.Constants;
import own_database.utils.databaseTools.DatabaseTools;
import own_database.utils.tableTools.TableTools;

public class CreateStatementTools {
	/**
	 * 
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static boolean validCreateStatement(String statement) throws Exception {
		if(statement.equals(null) || statement.equals("")) return false;
		String array [] = statement.trim().split(" ");
		
		if(array.length==1) {
			System.out.println("Create statement necessite more arguments :(");
			return false;
		}else if(array.length==2) {
			System.out.println("Create statement necessit more arguments :(");
			return false;
		}else if(array.length==3) {
			System.out.println("Create statement necessit more arguments :(");
			return false;
		}else if(array.length >4) {
			System.out.println("invalid end of create statement");
			return false;
		}
		
		if(!checkIfCreateIsNotFollowedWithUnvalidKeyWord(array[1])) {
			System.out.println("not cool , not fucking cool , you do not know how not cool that shit is");
		}else {
			//System.out.println("valid create "+array[1]);
			//now after that we checked that create table or database is valid, we should check if
			// table or database or view is already exist 
			//also check if the name starts with valid characters 
			if(StatementTools.checkIfUnvalidName(array[2]))
			{
				System.out.println("unvalid name,"+array[1]+" name shouldn't start with digit or characters");
				return false;
			}
			//check if the word not a reserved word 
			if(Constants.reservedWords().contains(array[2])) {
				System.out.format("You have an error in your SQL syntax; %s is a reserved word",array[2]);
				return false;
			}
			switch(array[1]) {
				case "table" :{
					String tableName = array[2];
					//first check if the user selected the database
					String currentDb = UseStatementTools.getTheCurrentSessionDatabase();
					
					if(currentDb.equals("") || currentDb.equals(null)) {
						System.out.println("No database selected");
						return false; 
					}
					TableTools.createTable(statement,currentDb);
					break;
				}
				case "database" :{
					String dbName = array[2];
					//check if the name is already exist or not 
					if(DatabaseTools.checkIfDatabaseNameExist(dbName)) {
						System.out.format("Can't create database '%s'; database exists",array[2]);
						return false;
					}else {
						if(dbName.length()>25) {
							System.out.println("database name is too long, please use names less than 25 characters");
							return false ;
						}
						Database database = new Database();
						database.setDatabaseName(dbName);
						DatabaseTools.createDatabase(database);
						System.out.println("database created successfully");
					}
					
					break;
				}
				case "view" :{
					String viewName = array[2];
					break;
				}
			}
			
		}
		
		
		return false;
	}
	/**
	 * 
	 * @param keyWord
	 * @return
	 */
	public static boolean checkIfCreateIsNotFollowedWithUnvalidKeyWord(String keyWord) {
		List<String> validKeyWordsForCreateStatement = Arrays.asList("table","view","database");
		if(!validKeyWordsForCreateStatement.contains(keyWord)) {
			return false;
		}
		return true;
	}
}
