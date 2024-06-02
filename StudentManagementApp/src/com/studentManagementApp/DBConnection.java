package com.studentManagementApp;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	static Connection connection;
	public static Connection connect() {
		try {
			//load the driver 
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			String username = "happypotter";
			String password = "password";
			String url = "jdbc:mysql://localhost:3306/studentManagementApp";
			
			connection = DriverManager.getConnection(url, username, password);

		}catch(Exception e){
			e.printStackTrace();
		}
		return connection;	
	}
}
