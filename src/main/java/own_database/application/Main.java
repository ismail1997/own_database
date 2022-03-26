package own_database.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import own_database.utils.Constants;
import own_database.utils.CryptoUtils;
import own_database.utils.Tools;
import own_database.utils.databaseTools.DatabaseTools;
import own_database.utils.statementsTools.CreateStatementTools;
import own_database.utils.statementsTools.DropStatementTools;
import own_database.utils.statementsTools.ShowStatementTools;
import own_database.utils.statementsTools.StatementTools;

public class Main {

	public static void main(String[] args) throws Exception {
		


		

			Scanner scanner= new Scanner(System.in);
			String statement = "";
			
			boolean endOfProgram = false;
			
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
						if(keyWord.toLowerCase().equals("create")) {
							CreateStatementTools.validCreateStatement( statement);
						}else if(keyWord.toLowerCase().equals("drop")) {
							DropStatementTools.validDropStatement(statement);
						}else if(keyWord.toLowerCase().equals("show")) {
							ShowStatementTools.validShowStatement(statement);
						}
					}
				}
				
			}
 
		
	}
	


	

	




	
	

	

	

	
	
	
	

	
	
	
	
	
	
	
	

}