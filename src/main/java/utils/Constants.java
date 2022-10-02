package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String DATABASE_FILE="database.owndb";
	public static final String DATABASE_CURRENT="crdb.owndb";
	public static final String TABLES_FILES="tb.owndb";
	
	public Constants() {

	}
	
	public static List<String> reservedWords(){
		return Arrays.asList("select","insert","delete","update","create","tables",
				"from","where","*","not","null","database","auto_increment","date","char",
				"int","string","double","drop","show","truncate","databases","table","use","fg","pk");
	}
	
	public static List<String> reservedTypes(){
		return Arrays.asList("string","double","int","date","boolean","char");
	}
}
