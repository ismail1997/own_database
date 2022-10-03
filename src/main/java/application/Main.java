package application;


import java.util.Scanner;

import utils.statementsTools.CreateStatementTools;
import utils.statementsTools.DescStatementTools;
import utils.statementsTools.DropStatementTools;
import utils.statementsTools.InsertStatementTools;
import utils.statementsTools.SelectStatementTools;
import utils.statementsTools.ShowStatementTools;
import utils.statementsTools.StatementTools;
import utils.statementsTools.TruncateStatementTools;
import utils.statementsTools.UseStatementTools;

public class Main {

	public static void main(String[] args) throws Exception {

			Scanner scanner= new Scanner(System.in);
			String statement = "";
			
			boolean endOfProgram = false;
			
			UseStatementTools.clearCurrentSession();
			
			while(!endOfProgram)
			{
				System.out.println();
				System.out.print("db_own_v1> ");
				statement = scanner.nextLine();
				statement=statement.replaceAll("\\s+"," ").trim();
				if(statement.toLowerCase().equals("exit") || statement.toLowerCase().equals("quit") || statement.toLowerCase().equals("quit()"))
				{
					endOfProgram = true;
				}else {
					String keyWord =StatementTools.getFirstKeyWordFromStatement(statement);
					//check if the keyWord is a reserved word
					boolean check = StatementTools.checkIfFirstKeyWordIsResevedWord(keyWord);
					if(!check) {
						System.out.println("Syntax Error, statement should start with a valid keyword");
					}else {
						if (keyWord.toLowerCase().equals("use")) {
							UseStatementTools.validUseStatememt(statement);
						}else if(keyWord.toLowerCase().equals("create")) {
							CreateStatementTools.validCreateStatement( statement);
						}else if(keyWord.toLowerCase().equals("drop")) {
							DropStatementTools.validDropStatement(statement);
						}else if(keyWord.toLowerCase().equals("show")) {
							ShowStatementTools.validShowStatement(statement);
						}else if(keyWord.toLowerCase().equals("desc") || keyWord.toLowerCase().equals("describe")) {
							DescStatementTools.validDescripeStatement(statement);
						}else if(keyWord.toLowerCase().equals("insert")) {
							InsertStatementTools.validInsertStatement(statement);
						}else if(keyWord.toLowerCase().equals("select")) {
							SelectStatementTools.validSelectStatement(statement);
						}else if(keyWord.toLowerCase().equals("shcdb")) {
							UseStatementTools.showCurrentDatabase (statement);
						}else if(keyWord.toLowerCase().equals("truncate")) {
							TruncateStatementTools.validTruncateStatement(statement);
						}else if(keyWord.toLowerCase().equals("update")) {
							
						}
						
					}
				}
				
			}
			scanner.close();
 
		
	}
	


	

	




	
	

	

	

	
	
	
	

	
	
	
	
	
	
	
	

}