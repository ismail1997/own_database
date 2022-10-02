package utils.statementsTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import initpackage.FirstInit;
import utils.Constants;
import utils.CryptoUtils;
import utils.Tools;
import utils.tableTools.TableTools;

public class TruncateStatementTools {
	
	public static void validTruncateStatement(String statement) throws Exception {
		
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		
		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		if(statement.equals("") || statement==null) {
			System.out.println("ERROR : invalid truncate statement");return;
		}
		
		statement = statement.trim().replaceAll("\\s+"," ");
		
		String splitStatement [] = statement.split(" ");
		
		if(!splitStatement[0].equalsIgnoreCase("truncate")){
			System.out.println("ERROR : invalid truncate statement, check the manual of db_own");return;
		}
		
		int lengthOfStatement = splitStatement.length;
		String tableName ="";
		String endOfStm = "";
		
		switch(lengthOfStatement) {
			case 4 :{ //truncate table tableName ;
				
				if(!splitStatement[1].equalsIgnoreCase("table")) {
					System.out.println("ERROR : invalid truncate statement, check the manual of db_own");return;
				}
				 
				tableName=splitStatement[2];
				endOfStm=splitStatement[3];
				
				//check if the table name is not a reserved word
				if(Constants.reservedWords().contains(tableName)) {
					System.out.format("ERROR : invalid name of table, '%s' is a reserved word%n",tableName);return;
				}
				
				if(!endOfStm.equals(";")) {
					System.out.println("ERROR : invalid end of statement");return;
				}
				
				break;
			}
			case 3 : {//truncate tabneName ;
				tableName=splitStatement[1];
				endOfStm=splitStatement[2];
				
				//check if the table name is not a reserved word
				if(Constants.reservedWords().contains(tableName)) {
					System.out.format("ERROR : invalid name of table, '%s' is a reserved word%n",tableName);return;
				}
				
				if(!endOfStm.equals(";")) {
					System.out.println("ERROR : invalid end of statement");return;
				}
				break;
			}
			default :{//other thing we should throw exception or return error
				System.out.println("ERROR : invalid truncate statement, check the manual of db_own");return;
			}
		}
		
		//verify if table exists in database 
		if(!TableTools.checkIfTableExistAlreadyInDb(tableName, currentDatabase)) {
			System.out.format("ERROR : Table %s.%s doesn't exist",currentDatabase,tableName);return;
		}
		//now everything is good, we should truncate the table file 
		String tableFile=FirstInit.USER_HOME_DIRECTORY+"/"+FirstInit.DB_FILE_NAME+"/"+tableName+"_"+currentDatabase+".owndb";
		
		File myObj= new File(tableFile);
		
		//save the file header
		if (!myObj.exists()) {
			System.out.println("table doesn't exist");
			return;// Collections.emptyList();
		}

		// define a scanner object to read from the file
		Scanner myReader = new Scanner(myObj);

		// read the data from file and pass it to the split method
		String header = myReader.nextLine();
		
		myReader.close();
		
		// clear the file
		BufferedWriter clearBuffer = new BufferedWriter(new FileWriter(new File(tableFile)));
		clearBuffer.write("");
		clearBuffer.close();
		
		Tools.writeToFile(header,tableFile);
		
		System.out.format("Query OK, Table %s.%s successfully%n",tableName,currentDatabase);
	}
}
