package own_database.utils.statementsTools;

import java.util.Arrays;
import java.util.List;

import own_database.utils.databaseTools.DatabaseTools;

public class ShowStatementTools {
	
	public static boolean validShowStatement(String statement) throws Exception {
		if(statement.equals(null) || statement.equals("")) return false;
		String array [] = statement.trim().split(" ");
		
		if(array.length==1) {
			System.out.println("show statement necessite more arguments :(");
			return false;
		}else if(array.length==2) {
			if(!array[1].equals("databases") && !array[1].equals("tables")) {
				System.out.println("show must followed with databases or tables");
				return false ;
			}else if(array[1].equals("databases") || array[1].equals("tables")) {
				System.out.println("invalid end of statement");
				return false ;
			}
			System.out.println("show statement necessit more arguments :(");
			return false;
		}else if(array.length >3) {
			System.out.println("invalid end of show statement");
			return false;
		}
		
		
		if(!array[1].equals("databases") && !array[1].equals("tables")) {
			System.out.println("show must followed with databases or tables");
			return false;
		}else {
			if(!array[2].equals(";")) {
				System.out.println("invalid end of statemenet");return false;
			}else {
				DatabaseTools.showDatabases();
			}
			
		}
		
		return false;
	}
	
	public static boolean checkIfShowIsNotFollowedWithUnvalidKeyWord(String keyWord) {
		List<String> validKeyWordsForDropStatement = Arrays.asList("tables","databases");
		if(!validKeyWordsForDropStatement.contains(keyWord)) {
			return false;
		}
		return true;
	}
}
