package utils.statementsTools;

import java.util.Arrays;
import java.util.List;

public class StatementTools {
	public static String getFirstKeyWordFromStatement(String statement ) {
		if(statement.equals("") || statement.equals(null)) return "";
		
		String array[]  = statement.trim().split(" ");
		return array[0];
	}
	public static String getSecondKeyWordFromStatement(String statement ) {
		if(statement.equals("") || statement.equals(null)) return "";
		
		String array[]  = statement.trim().split(" ");
		return array[1];
	}
	
	public static boolean checkIfUnvalidName(String str) {
		if ( Character.isDigit(str.charAt(0)) )
		{
		    return true;
		}else {
			return false;
		}
	}
	public static boolean checkIfFirstKeyWordIsResevedWord(String keyWord) {
		List<String> reservedWords = Arrays.asList("select","create","update","delete","insert",
				"drop","truncate","show","use","desc","describe","shcdb","limit");
		boolean check = false;
		for(String str : reservedWords) {
			if(str.equals(keyWord.toLowerCase())) {
				check = true;
				break;
			}
		}
		
		return check;
	}
}
