package initpackage;

import java.io.File;

public class FirstInit {
	
	public static final String USER_HOME_DIRECTORY=System.getProperty("user.home");
	public static final String DB_FILE_NAME       ="myOwnDB";
	
	public static void createDatabaseFolder() {
		File file = new File(USER_HOME_DIRECTORY+"/"+DB_FILE_NAME);
		if(file.exists()) {
			return ;
		}
		else {
			file.mkdir();
		}
	}

	public static void main(String[] args) {
		createDatabaseFolder();
	}
}
