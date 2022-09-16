package utils.statementsTools;

import java.util.Arrays;
import java.util.List;

import utils.databaseTools.DatabaseTools;
import utils.tableTools.TableTools;

public class ShowStatementTools {
	
	/**
	 * 
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public static boolean validShowStatement(String statement) throws Exception {
		if(statement.equals(null) || statement.equals("")) return false;
		String array [] = statement.trim().split(" ");
		
		
		switch(array.length) {
			case 1 :{
				System.out.println("show statement necessite more arguments");
				return false;
			}
			case 2 :{
				if(array[1].equals(";")) {
					System.out.println("invalid show statement, check the manual of db_own");
					return false;
				}else {
					System.out.println("missing ';' at the end of show statement");
					return false;
				}
			}
			case 3 : {
				if(!Arrays.asList("tables","databases","views").contains(array[1])) {
					System.out.println("invalid show statement, check the manual of db_own");
					return false;
				}
				
				if(!array[2].equals(";")) {
					System.out.println("missing ';' at the end of show statement");
					return false;
				}
				
				switch(array[1]) {
					case "databases":{
						DatabaseTools.showDatabases();
						break;
					}
					case "tables":{
						//first check if the user selected the database
						String currentDb = UseStatementTools.getTheCurrentSessionDatabase();
						
						if(currentDb.equals("") || currentDb.equals(null)) {
							System.out.println("No database selected");
							return false; 
						}
						
						DatabaseTools.showTablesOfDatabase(currentDb);
						break;
					}
					default:{ break;
					}
				}
				break;
			}
			default : {
				break;
			}
		}
		
		
		return true;
	}
	/**
	 * 
	 * @param keyWord
	 * @return
	 */
	public static boolean checkIfShowIsNotFollowedWithUnvalidKeyWord(String keyWord) {
		List<String> validKeyWordsForDropStatement = Arrays.asList("tables","databases");
		if(!validKeyWordsForDropStatement.contains(keyWord)) {
			return false;
		}
		return true;
	}
}
