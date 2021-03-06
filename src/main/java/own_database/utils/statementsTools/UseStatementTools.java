package own_database.utils.statementsTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import own_database.models.Database;
import own_database.utils.Constants;
import own_database.utils.CryptoUtils;
import own_database.utils.databaseTools.DatabaseTools;

public class UseStatementTools {
	public static void saveCurrentSession(String database) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constants.DATABASE_CURRENT)));
		bw.write(CryptoUtils.encryptData(database));
		bw.close();
	}
	
	public static void clearCurrentSession() throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Constants.DATABASE_CURRENT)));
		bw.write("");
		bw.close();
	}

	public static String getTheCurrentSessionDatabase() throws Exception {
		FileInputStream fis = new FileInputStream(Constants.DATABASE_CURRENT);
		Scanner sc = new Scanner(fis);
		String read = "";
		while (sc.hasNextLine()) {
			read=sc.nextLine(); 
		}
		sc.close(); 

		return CryptoUtils.decryptedData(read);
	}

	public static boolean validUseStatememt(String statement) throws Exception {
		if (statement.equals(null) || statement.equals(""))
			return false;
		String array[] = statement.trim().split(" ");

		if (array.length == 1) {
			System.out.println("Use statement necessite more arguments :(");
			return false;
		}
		if (array.length == 2) {
			System.out.println("invalid end of statement :(");
			return false;
		}
		if (array.length > 3) {
			System.out.println("invalid use statement");
			return false;
		}
		if (array.length == 3 && !array[2].equals(";")) {
			System.out.println("invalid end of state");
			return false;
		}

		if (Constants.reservedWords().contains(array[1])) {
			System.out.println("invalid use statement; " + array[1] + " is a reserved word");return false ;
		}

		// check if the database exist or not
		Database database = DatabaseTools.getDatabase(array[1]);
		if (database == null) {
			System.out.println("database doesn't exist");
			return false;
		}
		System.out.println("Database changed");
		// save the current database to the session
		saveCurrentSession(array[1]);
		return true;
	}
	
	
	public static void main(String[] args) throws Exception {
		validUseStatememt("use dsdf ;");
		
		System.out.println(getTheCurrentSessionDatabase());
	}
}
