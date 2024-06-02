package com.studentManagementApp;

public class Student {
	private int sId;
	private String sName;
	private String sPhone;
	private String sCity;
	
	
	public int getsId() {
		return sId;
	}

	public void setsId(int sId) {
		this.sId = sId;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getsPhone() {
		return sPhone;
	}

	public void setsPhone(String sPhone) {
		this.sPhone = sPhone;
	}

	public String getsCity() {
		return sCity;
	}

	public void setsCity(String sCity) {
		this.sCity = sCity;
	}
	
	public Student(int sId, String sName, String sPhone, String sCity) {
		this.sId = sId;
		this.sName = sName;
		this.sPhone = sPhone;
		this.sCity = sCity;
	}

	public Student(String sName, String sPhone, String sCity) {
		super();
		this.sName = sName;
		this.sPhone = sPhone;
		this.sCity = sCity;
	}

	public Student() {
		super();
	}

	@Override
	public String toString() {
		return "Student [sId=" + sId + ", sName=" + sName + ", sPhone=" + sPhone + ", sCity=" + sCity + "]";
	}
}
