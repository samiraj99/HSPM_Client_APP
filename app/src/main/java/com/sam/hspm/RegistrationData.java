package com.sam.hspm;

class RegistrationData {
    String Name,Email,PhoneNo,Address;

    RegistrationData(String name, String email, String phoneNo, String address) {
        Name = name;
        Email = email;
        PhoneNo = phoneNo;
        Address = address;
    }


    public RegistrationData(String name, String email, String phoneNo) {
        Name = name;
        Email = email;
        PhoneNo = phoneNo;
    }
}
