package writers;

import java.sql.*;

/**
 * 
 * @author Andrew
 *
 *Write parsed inventory to MySQL database
 *
 */


public class DatabaseWriter {
	
	Connection dbConnection;		//connection to database
	Statement myStatement;	//object for executing SQL statement
	String 	user,			
			password,
			schema,			
			sqlStatement,	//SQL statement in string format
			table;			//database table being appended
	
	/**
	 * 
	 * @param databasePath
	 * @param schema
	 * @param table
	 * @param user
	 * @param password
	 */
	public DatabaseWriter(String databasePath, String schema, String table, String user, String password){
		this.table = table;
		this.schema = schema;
		try {
			dbConnection = DriverManager.getConnection(databasePath + schema, user, password);
			myStatement = dbConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DatabaseWriter(String databasePath, String schema, String user, String password){
		this.schema = schema;
		try {
			dbConnection = DriverManager.getConnection(databasePath + schema, user, password);
			myStatement = dbConnection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//construct sql string with field 
	//@param fields - string array with names of fields
	public String fieldsToSQLStr(String[] fields) {
		StringBuilder fieldsStr = new StringBuilder();
		fieldsStr.append(fields[0]);
		for(int i = 1; i < fields.length; i++) {
			fieldsStr.append(", " + fields[i]);
		}
		return fieldsStr.toString();
	}
	
	//parse database entry into string in SQL statement format
	public String entryToSQLStr(String[] entry) {
		StringBuilder entryStr = new StringBuilder();
		entryStr.append("'" + entry[0].replace("'", "''") + "'");	//replace single quotes with two single quotes to cancel
		for(int i = 1; i < entry.length; i++) {
			if(entry[i] == null) {			//if field is null
				entryStr.append(", ''");	//use empty string
			}
			else {										//if field is not null
				entryStr.append(", '" + entry[i].replace("'", "''") + "'");	//replace single quotes with two single quotes to cancel
			}
		}
		return entryStr.toString();
	}
	
	//write entry to database
	public void writeData(String fields, String data) {
		sqlStatement = "insert into `" + table +"` (" + fields +") values (" + data + ");";		//construct full SQL statement
		try {
			myStatement.executeUpdate(sqlStatement);											//execute SQL statement
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sqlStatement);
		}
	}
	
	public void initializeTables() {
		//create source table
		String createSourceTable = "CREATE TABLE `" + schema + "`.`sources`(`source_id` INT NOT NULL AUTO_INCREMENT, `collection_name` "
				+ "TEXT NOT NULL, `source_number` DECIMAL(10, 3) NOT NULL, `source_call_number` TEXT NOT NULL,"
				+ " `source_author` TEXT NOT NULL, `source_title` TEXT NOT NULL, `source_inscription` TEXT NOT NULL, "
				+ "`source_description` TEXT NOT NULL, PRIMARY KEY (`source_id`));";
		
		//create entries table
		String createEntryTable = "CREATE TABLE `" + schema + "`.`entries`(`entry_id` INT NOT NULL AUTO_INCREMENT, `collection_name` "
				+ "TEXT NOT NULL, `source_number` DECIMAL(10, 3) NOT NULL, `entry_location` TEXT NOT NULL,"
				+ " `entry_title` TEXT NOT NULL, `entry_composer` TEXT NOT NULL, `entry_vocal_part` TEXT NOT NULL, `entry_key` "
				+ "TEXT NOT NULL, `entry_melodic_incipit` TEXT NOT NULL,  `entry_text_incipit` TEXT NOT NULL, `entry_is_secular` "
				+ "TEXT NOT NULL, `entry_notes` TEXT NOT NULL, PRIMARY KEY (`entry_id`));";
					
		//create collections table		
		String createDescriptionTable = "CREATE TABLE `" + schema + "`.`collections`(`collection_id` INT NOT NULL AUTO_INCREMENT, " + 
		"`collection_name` TEXT NOT NULL, `collection_description` TEXT NOT NULL, PRIMARY KEY (`collection_id`));";
		
		try {
			myStatement.executeUpdate(createSourceTable);
			myStatement.executeUpdate(createEntryTable);
			myStatement.executeUpdate(createDescriptionTable);		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
