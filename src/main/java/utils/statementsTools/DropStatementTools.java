package utils.statementsTools;

import java.util.Arrays;
import java.util.List;

import utils.Constants;
import utils.MessageConstants;
import utils.databaseTools.DatabaseTools;

public class DropStatementTools {
	/**
	 * 
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static boolean validDropStatement(String statement) throws Exception {
		if(statement.equals(null) || statement.equals("")) return false;
		String array [] = statement.trim().split(" ");
		if(array.length==1) {
			System.out.println("drop statement necessite more arguments :(");
			return false;
		}else if(array.length==2) {
			System.out.println("drop statement necessit more arguments :(");
			return false;
		}else if(array.length==3) {
			System.out.println("drop statement necessit more arguments :(");
			return false;
		}else if(array.length >4) {
			System.out.println("ERROR : invalid end of drop statement");
			return false;
		}
		
		if(!checkIfDropIsNotFollowedWithUnvalidKeyWord(array[1])) {
			System.out.println(MessageConstants.INVALID_DROP_STATEMENT);
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
			
			//verify if the statement ended with ';'
			if(!array[3].equals(";")) {
				System.out.println("ERROR : missing ';' at the last of statement");
				return false;
			}
			
			switch(array[1]) {
				case "table":{
					String tableName =array[2];
					break;
				}
				case "database":{
					String databaseName =array[2];
					if(!DatabaseTools.checkIfDatabaseNameExist(databaseName)) {
						System.out.format("Can't drop database '%s'; database doesn't exists",array[2]);
						return false;
					}else {
							DatabaseTools.dropDatabase(databaseName);
							
					}
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
	public static boolean checkIfDropIsNotFollowedWithUnvalidKeyWord(String keyWord) {
		List<String> validKeyWordsForDropStatement = Arrays.asList("table","database");
		if(!validKeyWordsForDropStatement.contains(keyWord)) {
			return false;
		}
		return true;
	}
}
