package writers;

import java.sql.*;

/**
 * 
 * @author Andrew
 *
 *write to MySQL database
 *
 */


public class DatabaseWriter {
	
	Connection myConn;		//connection to database
	Statement myStatement;	//object for executing SQL statement
	String sql,				//SQL statement in string format
	table;					//database table being appended
	
	//construct database writer with schema and table name as parameters
	public DatabaseWriter(String databasePath, String schema, String table, String user, String password){
		this.table = table;
		try {
			myConn = DriverManager.getConnection(databasePath + schema, user, password);
			myStatement = myConn.createStatement();
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
		sql = "insert into " + table +" (" + fields +") values (" + data + ");";		//construct full SQL statement
		try {
			myStatement.executeUpdate(sql);											//execute SQL statement
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sql);
		}
	}
	
//	//create tables
//	public static void main(String[] args) {
//		
//		try {
//			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/collections_2","root","password");
//			
//			Statement myStatement = myConn.createStatement();
//			
//			//create source table
//			//make sure to change schema below***
//			String sql = "CREATE TABLE `collections_2`.`sources`(`source_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL`source_number` TEXT NOT NULL, `source_call_number` TEXT NOT NULL,"
//					+ " `source_author` TEXT NOT NULL, `source_title` TEXT NOT NULL, `source_inscription` TEXT NOT NULL, `source_description` TEXT NOT NULL, PRIMARY KEY (`source_id`));";
//			
//////			//create entries table
////			String sql = "CREATE TABLE `collections_2`.`entries`(`entry_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL, `source_number` TEXT NOT NULL, `entry_location` TEXT NOT NULL,"
////					+ " `entry_title` TEXT NOT NULL, `entry_composer` TEXT NOT NULL, `entry_vocal_part` TEXT NOT NULL, `entry_key` TEXT NOT NULL, `entry_melodic_incipit` TEXT NOT NULL,"
////					+ " `entry_text_incipit` TEXT NOT NULL, `entry_is_secular` TEXT NOT NULL, `entry_notes` TEXT NOT NULL, " + 
////					+ "PRIMARY KEY (`entry_id`));";
//						
////			//create collections table		
////			String sql = "CREATE TABLE `collections_2`.`collections`(`collection_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL, `collection_description` TEXT NOT NULL, PRIMARY KEY (`collection_id`));";
//			
//
//			myStatement.executeUpdate(sql);
//			
////			ResultSet myResults = myStatement.executeQuery("SELECT * FROM city");
////			
////			while(myResults.next()) {
////				System.out.println(myResults.getString("name") + "   " + myResults.getString("population"));
////			}
//			
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

}
