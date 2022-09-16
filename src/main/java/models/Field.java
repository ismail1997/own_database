package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @ToString @NoArgsConstructor @AllArgsConstructor
public class Field {
	private String fieldName;
	private String fieldType;
	private String primaryKey;
	private String foreignKey;
}
