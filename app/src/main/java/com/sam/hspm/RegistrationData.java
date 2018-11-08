package com.sam.hspm;

public class RegistrationData {
  public   String Name,Email,PhoneNo,Address;

 public    RegistrationData(String name, String email, String phoneNo, String address) {
        Name = name;
        Email = email;
        PhoneNo = phoneNo;
        Address = address;
    }

  public   RegistrationData(String name, String email) {
        Name = name;
        Email = email;
    }
}
