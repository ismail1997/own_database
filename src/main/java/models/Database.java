package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @ToString @AllArgsConstructor @NoArgsConstructor
public class Database implements Serializable{
	private static final long serialVersionUID = 7059446628403605455L;

	private String databaseName;
	private List<String> listOfTables = new ArrayList<>();
}
