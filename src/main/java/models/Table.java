package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @ToString @AllArgsConstructor @NoArgsConstructor
public class Table implements Serializable{

	private static final long serialVersionUID = 2386231852869108805L;
	private String tableName ;
	private String database ;
	private int numberOfColumns =0;
	//private HashMap<String, String> fields ;
	private List<Field> listOfFields;
}
