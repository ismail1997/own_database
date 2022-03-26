package own_database.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

	
	public Constants() {

	}
	
	public static List<String> reservedWords(){
		return Arrays.asList("select","insert","delete","update","create",
				"from","where","*","not","null","database","auto_increment",
				"int","string","double","drop","show","truncate","databases");
	}
}
