package com.example.acdms_profile;
public class ReadWriteUserDetails {
    public String Firstname, Lastname,School, YearLevel,Birthday, Age;

    public ReadWriteUserDetails(String textfname, String textlname, String textschoolname, String textyearlevel, String textbirthday, String textage){
        this.Firstname = textfname;
        this.Lastname = textlname;
        this.School = textschoolname;
        this.YearLevel = textyearlevel;
        this.Birthday = textbirthday;
        this.Age = textage;
    }

}

