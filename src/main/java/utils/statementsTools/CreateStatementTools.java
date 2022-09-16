package utils.statementsTools;

import java.util.Arrays;
import java.util.List;

import models.Database;
import utils.Constants;
import utils.MessageConstants;
import utils.databaseTools.DatabaseTools;
import utils.tableTools.TableTools;

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
		

		
		if(!checkIfCreateIsNotFollowedWithUnvalidKeyWord(array[1].toLowerCase())) {
			System.out.println(MessageConstants.INVALID_KEYWORD_MESSAGE+" '"+array[1]+"'");
		}else {
			
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
			switch(array[1].toLowerCase()) {
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
		if(!Arrays.asList("table","view","database").contains(keyWord)) {
			return false;
		}
		return true;
	}
	
	
}
