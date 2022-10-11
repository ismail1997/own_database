package utils.statementsTools;

import java.util.ArrayList;

import utils.Tools;

public class DeleteStatementTools {

	
	public static void main ( String [] args ) throws Exception {
		String statement= "delete from user where id=2 and email ='ismail';";
		
		String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		currentDatabase="mydb";
		
		if(currentDatabase.equals("") || currentDatabase.equals(null)) {
			System.out.println("No database selected :-(");return;
		}
		
		if(statement.equals("") || statement==null) {
			System.out.println("ERROR : invalid truncate statement");return;
		}
		
		statement = statement.trim().replaceAll("\\s+"," ");
		
		String splitStatement [] = statement.split(" ");
		
		
		
		ArrayList<String> list =new ArrayList<String>();
		for(String str : splitStatement) list.add(str);
		
		if(!list.get(0).equalsIgnoreCase("delete")){
			System.out.println("ERROR : invalid delete statement, check the manual of db_own");return;
		} 
		if(!list.get(1).equalsIgnoreCase("from")){
			System.out.println("ERROR : invalid delete statement, check the manual of db_own");return;
		} 
		if(!list.get(list.size()-1).equalsIgnoreCase(";")) {
			System.out.println("ERROR : invalid delete statement, missing ';' at the end of statement");return;
		}
		
		//if there is where to variable then we should do different
		boolean whereExist = false;
		if(list.contains("where")) {
			whereExist=true;
		}
		
		if(Tools.countFrequencies(list, "where")>1) {
			System.out.println("ERROR : invalid delete statement, duplicate 'where' keyword");return;
		}
		if(Tools.countFrequencies(list, "from")>1) {
			System.out.println("ERROR : invalid delete statement, duplicate 'from' keyword");return ;
		}
		if(Tools.countFrequencies(list, ";")>1) {
			System.out.println("ERROR : invalid delete statement, duplicate ';' ");return ;
		}
		if(Tools.countFrequencies(list, "delete")>1) {
			System.out.println("ERROR : invalid delete statement, duplicate 'delete' keyword");return;
		}
		
		String tableName ="";
		String conditionalFields= "";
		
		if(list.get(2)==null || list.get(2).equals("") || list.get(2).equals(null)) {
			System.out.println("ERROR : invalid delete statment, missing table name");return;
		}
		
		tableName = list.get(2).trim();
		
		if(!whereExist) {
			TruncateStatementTools.validTruncateStatement("truncate "+tableName+" ;");return;
		}
		
		
		System.out.println(tableName);
		
		
		
		
		
	
	}
	public static void validDeleteStatement(String statement) {
		
		
	}

}
