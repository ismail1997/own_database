package utils.statementsTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Field;
import models.Table;
import utils.Tools;
import utils.tableTools.TableTools;

public class SelectStatementTools {
	public static void validSelectStatementOLDVERSION(String statement) throws Exception {
		String chosenSelect = statement.toLowerCase().replaceAll("\\s+"," ").trim();
		String selectKeyword=chosenSelect.toLowerCase().trim();
			   selectKeyword=selectKeyword.substring(selectKeyword.indexOf("select"),"select".length());
			   
			   
	    //we should verify if the string is ended with ;
	    String end = ""+chosenSelect.charAt(chosenSelect.length()-1);
	    if(!end.equals(";")) {
	    	System.out.println("ERROR : invalid end of statement, missing ';' ");return;
	    }
	    
	    
	   
			   
	    //first we should check if the string contains form 
	    //for that we should divide the string 
	    ArrayList<String> dividedSelect = new ArrayList<>();
	    dividedSelect.addAll(Arrays.asList(chosenSelect.split(" ")));
	    
	    
	    //check if there is another ; in the statement
	    if(Tools.countFrequencies(dividedSelect, ";")>1) {
	    	System.out.println("ERROR : invalid select statement, please check the manual");return;
	    }
	    
	    boolean containsWhereClause=false;
	    boolean containsWhereClauseAndAlso = false;
	    boolean containsAsterix = false;
		
	    if(!dividedSelect.contains("select")) {
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'select' keyword");return;
	    }
	    if(!dividedSelect.contains("from")) {//throw exception if there is no from keyword
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'from' keyword");return;
	    }
	    
	    if(dividedSelect.contains("where")) {
	    	containsWhereClause=true;
	    }
	    
	    if(dividedSelect.contains("and") && !dividedSelect.contains("where")) {//if there is and keyword but no where, then throw exception
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'where' clause");return;
	    }
	    if(dividedSelect.contains("and")) containsWhereClauseAndAlso=true;
	    
	    if(dividedSelect.contains("*")) {//if the statement is with * not with fields declarations
	    	containsAsterix= true;
	    }
	    
	    //we should verify that the keywords 'select','from','where' frequencies are 1 not a lot in the statement;
	
	    if(Tools.countFrequencies(dividedSelect, "select")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'select' keyword is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "*")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; '*' is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "from")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'from' keyword is repeated");return;
	    }
	    
	    //now verify if it has *, then it should not contain field declaration
	    if(containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("*")+1,chosenSelect.indexOf("from"));
	    	if(d.trim().length()>1) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; '*' must not followed with any columns declarations");
	    		return ;
	    	}
	    }
	    
	    //now verify if there is column declaration if not * 
	    // and save fields 
	    String columns [] = new String [] {} ;
	    ArrayList<String> fields = new ArrayList<String>();//array list to store fields after trimming white space
	    
	    if(!containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("select")+"select".length(),chosenSelect.indexOf("from"));
	    	d=d.trim();
	    	
	    	if(d.length()==0 || d.length()<0) {//check if the columns are missing 
	    		System.out.println("ERROR : you have syntax error in your sql statement; missing columns declaration");return;
	    	}
	    	
	    	columns = d.split(",");
	    	 
	    	
	    	
	    	for(String s : columns) {
	    		fields.add(s.trim());
	    	}
	    	
	    }
	    
	    //now we should get the information about the table
	    //there is two cases : 1 if there is a where clause , if there is not where 
	    String tableName ="";
	    if(containsWhereClause) {//if there is a where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf("where"));
	 
	    	tableName=tableName.trim();
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }else {//if there is no where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf(";"));
	    	tableName=tableName.trim();
	    	
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }
	    
	    
	    //now we should get table informations 
	    String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		
		Table table = TableTools.getTable(tableName,currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",tableName);return;
		}
		
		List<Field> tableFields = table.getListOfFields();//getting the fields of the table 
		
		ArrayList<String> nameOfFields = new ArrayList<String> () ; //store the fields name in the list 
		for(Field fld : tableFields) {
			nameOfFields.add(fld.getFieldName());
		}
		
		for(String fld : fields) {//verify if the fields declared in the statement are the same in the table
			if(!nameOfFields.contains(fld)) {
				System.out.println("ERROR : the field '"+fld+"' dosen't exist in "+tableName);return;
			}
		}
		
		if(containsAsterix && !containsWhereClause) { // we handled the select * , without where  clause 
			TableTools.readDataFromTableAsterixWithoutWhereClause(tableName,table.getDatabase());
			
		}
		
		if(!containsAsterix && !containsWhereClause) {
			TableTools.readDataFromTableWithFieldsWithoutWhereClause(tableName, currentDatabase, fields);
		}
		
		return ;
	}
	
	public static void validSelectStatement(String statement)  throws Exception{
		String chosenSelect = statement.toLowerCase().replaceAll("\\s+"," ").trim();
		String selectKeyword=chosenSelect.toLowerCase().trim();
			   selectKeyword=selectKeyword.substring(selectKeyword.indexOf("select"),"select".length());
			   
			   
	    //we should verify if the string is ended with ;
	    String end = ""+chosenSelect.charAt(chosenSelect.length()-1);
	    if(!end.equals(";")) {
	    	System.out.println("ERROR : invalid end of statement, missing ';' ");return;
	    }
	    
	    
	   
			   
	    //first we should check if the string contains form 
	    //for that we should divide the string 
	    ArrayList<String> dividedSelect = new ArrayList<>();
	    dividedSelect.addAll(Arrays.asList(chosenSelect.split(" ")));
	    
	    
	    //check if there is another ; in the statement
	    if(Tools.countFrequencies(dividedSelect, ";")>1) {
	    	System.out.println("ERROR : invalid select statement, please check the manual");return;
	    }
	    
	    boolean containsWhereClause=false;
	    boolean containsWhereClauseAndAlso = false;
	    boolean containsAsterix = false;
	    boolean containsLimit =false;
		
	    
	    
	    if(!dividedSelect.contains("select")) {
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'select' keyword");return;
	    }
	    if(!dividedSelect.contains("from")) {//throw exception if there is no from keyword
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'from' keyword");return;
	    }
	    
	    if(dividedSelect.contains("where")) {
	    	containsWhereClause=true;
	    }
	    
	    if(dividedSelect.contains("and") && !dividedSelect.contains("where")) {//if there is and keyword but no where, then throw exception
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'where' clause");return;
	    }
	    if(dividedSelect.contains("and")) containsWhereClauseAndAlso=true;
	    
	    if(dividedSelect.contains("*")) {//if the statement is with * not with fields declarations
	    	containsAsterix= true;
	    }
	    if(dividedSelect.contains("limit")) {
	    	containsLimit=true;
	    }
	    
	    //we should verify that the keywords 'select','from','where' frequencies are 1 not a lot in the statement;
	
	    if(Tools.countFrequencies(dividedSelect, "select")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'select' keyword is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "*")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; '*' is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "from")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'from' keyword is repeated");return;
	    }
	    
	    //now verify if it has *, then it should not contain field declaration
	    if(containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("*")+1,chosenSelect.indexOf("from"));
	    	if(d.trim().length()>1) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; '*' must not followed with any columns declarations");
	    		return ;
	    	}
	    }
	    
	    //now verify if there is column declaration if not * 
	    // and save fields 
	    String columns [] = new String [] {} ;
	    ArrayList<String> fields = new ArrayList<String>();//array list to store fields after trimming white space
	    
	    if(!containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("select")+"select".length(),chosenSelect.indexOf("from"));
	    	d=d.trim();
	    	
	    	if(d.length()==0 || d.length()<0) {//check if the columns are missing 
	    		System.out.println("ERROR : you have syntax error in your sql statement; missing columns declaration");return;
	    	}
	    	
	    	columns = d.split(",");
	    	 
	    	
	    	
	    	for(String s : columns) {
	    		fields.add(s.trim());
	    	}
	    	
	    }
	    
	    //now we should get the information about the table
	    //there is two cases : 1 if there is a where clause , if there is not where 
	    String tableName ="";
	    int limitNumber =0; // if exist or not 
	    
	    if(containsWhereClause && !containsLimit) {//if there is a where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf("where"));
	 
	    	tableName=tableName.trim();
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }
	    else if(!containsWhereClause && !containsLimit) {//if there is no where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf(";"));
	    	tableName=tableName.trim();
	    	
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }else if(!containsWhereClause && containsLimit) {
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf("limit"));
	    	tableName=tableName.trim();
	    	String numberOfLimit =chosenSelect.substring(chosenSelect.indexOf("limit")+"limit".length(),chosenSelect.indexOf(";"));
	    	numberOfLimit=numberOfLimit.trim();
	    	
	    	if(numberOfLimit.length()>6) {
	    		System.out.println("ERROR : invalid limit number ["+numberOfLimit+"] for select statement");return;
	    	}
	    	
	    	limitNumber=Integer.valueOf(numberOfLimit);
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    	
	    	
	    }
	    
	    if(limitNumber<0 ) {
	    	System.out.println("ERROR : invalid limit number ["+limitNumber+"] for select statement");return;
	    }
	    
	    //now we should get table informations 
	    String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		
		Table table = TableTools.getTable(tableName,currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",tableName);return;
		}
		
		List<Field> tableFields = table.getListOfFields();//getting the fields of the table 
		
		ArrayList<String> nameOfFields = new ArrayList<String> () ; //store the fields name in the list 
		for(Field fld : tableFields) {
			nameOfFields.add(fld.getFieldName());
		}
		
		for(String fld : fields) {//verify if the fields declared in the statement are the same in the table
			if(!nameOfFields.contains(fld)) {
				System.out.println("ERROR : the field '"+fld+"' dosen't exist in "+tableName);return;
			}
		}
		
		if(containsAsterix && !containsWhereClause && !containsLimit) { // we handled the select * , without where  clause and without limit
			TableTools.readDataFromTableAsterixWithoutWhereClause(tableName,table.getDatabase());
			
		}
		
		if(!containsAsterix && !containsWhereClause && !containsLimit) {//handle select with fields and without limit keyword
			TableTools.readDataFromTableWithFieldsWithoutWhereClause(tableName, currentDatabase, fields);
		}
		
		if(containsAsterix && containsLimit && !containsWhereClause) {//select * from a limit 3; without where and with limit and with *
			TableTools.readDataFromTableWithAsterixWithoutWhereWithLimit(tableName, currentDatabase, limitNumber);
		}
		if(!containsAsterix && containsLimit && !containsWhereClause) {//select id,email from a limit 3 ; without where with fields with limit
			TableTools.readDataFromTableWithFieldsWithoutWhereWithLimit(tableName, currentDatabase, fields, limitNumber);
		}
		
	
	}
	
	public static void main(String[] args) throws Exception{
		
		String statement="select * from user limit 2;" ;
		String chosenSelect = statement.toLowerCase().replaceAll("\\s+"," ").trim();
		String selectKeyword=chosenSelect.toLowerCase().trim();
			   selectKeyword=selectKeyword.substring(selectKeyword.indexOf("select"),"select".length());
			   
			   
	    //we should verify if the string is ended with ;
	    String end = ""+chosenSelect.charAt(chosenSelect.length()-1);
	    if(!end.equals(";")) {
	    	System.out.println("ERROR : invalid end of statement, missing ';' ");return;
	    }
	    
	    
	   
			   
	    //first we should check if the string contains form 
	    //for that we should divide the string 
	    ArrayList<String> dividedSelect = new ArrayList<>();
	    dividedSelect.addAll(Arrays.asList(chosenSelect.split(" ")));
	    
	    
	    //check if there is another ; in the statement
	    if(Tools.countFrequencies(dividedSelect, ";")>1) {
	    	System.out.println("ERROR : invalid select statement, please check the manual");return;
	    }
	    
	    boolean containsWhereClause=false;
	    boolean containsWhereClauseAndAlso = false;
	    boolean containsAsterix = false;
	    boolean containsLimit =false;
		
	    
	    
	    if(!dividedSelect.contains("select")) {
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'select' keyword");return;
	    }
	    if(!dividedSelect.contains("from")) {//throw exception if there is no from keyword
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'from' keyword");return;
	    }
	    
	    if(dividedSelect.contains("where")) {
	    	containsWhereClause=true;
	    }
	    
	    if(dividedSelect.contains("and") && !dividedSelect.contains("where")) {//if there is and keyword but no where, then throw exception
	    	System.out.println("ERROR : you have syntax error in your sql statement; missing 'where' clause");return;
	    }
	    if(dividedSelect.contains("and")) containsWhereClauseAndAlso=true;
	    
	    if(dividedSelect.contains("*")) {//if the statement is with * not with fields declarations
	    	containsAsterix= true;
	    }
	    if(dividedSelect.contains("limit")) {
	    	containsLimit=true;
	    }
	    
	    //we should verify that the keywords 'select','from','where' frequencies are 1 not a lot in the statement;
	
	    if(Tools.countFrequencies(dividedSelect, "select")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'select' keyword is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "*")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; '*' is repeated");return;
	    }
	    if(Tools.countFrequencies(dividedSelect, "from")>1) {
	    	System.out.println("ERROR : you have error in your sql statement; 'from' keyword is repeated");return;
	    }
	    
	    //now verify if it has *, then it should not contain field declaration
	    if(containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("*")+1,chosenSelect.indexOf("from"));
	    	if(d.trim().length()>1) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; '*' must not followed with any columns declarations");
	    		return ;
	    	}
	    }
	    
	    //now verify if there is column declaration if not * 
	    // and save fields 
	    String columns [] = new String [] {} ;
	    ArrayList<String> fields = new ArrayList<String>();//array list to store fields after trimming white space
	    
	    if(!containsAsterix) {
	    	String d = chosenSelect.substring(chosenSelect.indexOf("select")+"select".length(),chosenSelect.indexOf("from"));
	    	d=d.trim();
	    	
	    	if(d.length()==0 || d.length()<0) {//check if the columns are missing 
	    		System.out.println("ERROR : you have syntax error in your sql statement; missing columns declaration");return;
	    	}
	    	
	    	columns = d.split(",");
	    	 
	    	
	    	
	    	for(String s : columns) {
	    		fields.add(s.trim());
	    	}
	    	
	    }
	    
	    //now we should get the information about the table
	    //there is two cases : 1 if there is a where clause , if there is not where 
	    String tableName ="";
	    int limitNumber =0; // if exist or not 
	    
	    if(containsWhereClause && !containsLimit) {//if there is a where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf("where"));
	 
	    	tableName=tableName.trim();
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }
	    else if(!containsWhereClause && !containsLimit) {//if there is no where clause 
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf(";"));
	    	tableName=tableName.trim();
	    	
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    }else if(!containsWhereClause && containsLimit) {
	    	tableName = chosenSelect.substring(chosenSelect.indexOf("from")+"form".length(),chosenSelect.indexOf("limit"));
	    	tableName=tableName.trim();
	    	String numberOfLimit =chosenSelect.substring(chosenSelect.indexOf("limit")+"limit".length(),chosenSelect.indexOf(";"));
	    	numberOfLimit=numberOfLimit.trim();
	    	
	    	if(numberOfLimit.length()>6) {
	    		System.out.println("ERROR : invalid limit number ["+numberOfLimit+"] for select statement");return;
	    	}
	    	
	    	limitNumber=Integer.valueOf(numberOfLimit);
	    	if(tableName.contains(" ") || tableName.equals("")) {
	    		System.out.println("ERROR : you have syntax error in your sql statement; invalid table name='"+tableName+"'.");return;
	    	}
	    	
	    	
	    }
	    
	    if(limitNumber<0 ) {
	    	System.out.println("ERROR : invalid limit number ["+limitNumber+"] for select statement");return;
	    }
	    
	    //now we should get table informations 
	    String currentDatabase =UseStatementTools.getTheCurrentSessionDatabase(); //get the current selected database
		
		Table table = TableTools.getTable(tableName,currentDatabase);//check if the table exists in the database
		if(table == null){
			System.out.format("ERROR : Table '%s' doesn't exist%n",tableName);return;
		}
		
		List<Field> tableFields = table.getListOfFields();//getting the fields of the table 
		
		ArrayList<String> nameOfFields = new ArrayList<String> () ; //store the fields name in the list 
		for(Field fld : tableFields) {
			nameOfFields.add(fld.getFieldName());
		}
		
		for(String fld : fields) {//verify if the fields declared in the statement are the same in the table
			if(!nameOfFields.contains(fld)) {
				System.out.println("ERROR : the field '"+fld+"' dosen't exist in "+tableName);return;
			}
		}
		
		if(containsAsterix && !containsWhereClause && !containsLimit) { // we handled the select * , without where  clause and without limit
			TableTools.readDataFromTableAsterixWithoutWhereClause(tableName,table.getDatabase());
			
		}
		
		if(!containsAsterix && !containsWhereClause && !containsLimit) {//handle select with fields and without limit keyword
			TableTools.readDataFromTableWithFieldsWithoutWhereClause(tableName, currentDatabase, fields);
		}
		
		if(containsAsterix && containsLimit && !containsWhereClause) {//select * from a limit 3; without where and with limit and with *
			TableTools.readDataFromTableWithAsterixWithoutWhereWithLimit(tableName, currentDatabase, limitNumber);
		}
		if(!containsAsterix && containsLimit && !containsWhereClause) {//select id,email from a limit 3 ; without where with fields with limit
			TableTools.readDataFromTableWithFieldsWithoutWhereWithLimit(tableName, currentDatabase, fields, limitNumber);
		}
		
	
	}
	
	
	
}
