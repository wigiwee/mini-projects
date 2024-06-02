package com.studentManagementApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to Student Management App");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("==============MENU==============");
			System.out.println("Press 1 to ADD student");
			System.out.println("Press 2 to DELETE student");
			System.out.println("Press 3 to DISPLAY student");
			System.out.println("Press 4 to UPDATE student");
			System.out.println("Press 5 to EXIT app");
			int c = Integer.parseInt(reader.readLine());
			
			if(c==1) {
				//add student
				System.out.println("Enter student Name");
				String name = reader.readLine();
				
				System.out.println("Enter student Phone no");
				String phone = reader.readLine();
				
				System.out.println("Enter student City");
				String city = reader.readLine();
				//creating student object
				Student student = new Student(name, phone, city);
				
				boolean status = StudentDao.insertStudentToDB(student);
				
				if(status) {
					System.out.println("Student added to the database");
				}else {
					System.out.println("Something went wrong, Try again");
				}
				
			}else if(c==2) {
				//delete student
				System.out.println("Enter student Id to delete");
				int id = Integer.parseInt(reader.readLine());
				boolean status = StudentDao.deleteStudent(id);
				
				if(status) {
					System.out.println("Deleted!");
				}else {
					System.out.println("Something went wrong, Try again");
				}
				
			}else if(c==3) {
				//display all Student
				String allStudents = StudentDao.getAllStudents();
				System.out.println(allStudents);
			
			}else if(c==4) {
				//update student
				System.out.println("Enter the Id of the student you would like to update");
				int id = Integer.parseInt(reader.readLine());
				boolean status = StudentDao.updateStudent(id);
				
				if(status) {
					System.out.println("Updated!");
				}else {
					System.out.println("Something went wrong, Try again");
				}
				
			}else if(c==5) {
				break;
			}
		}
		System.out.println("Exiting application");
	}
}
