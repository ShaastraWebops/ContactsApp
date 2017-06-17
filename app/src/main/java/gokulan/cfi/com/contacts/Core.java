package gokulan.cfi.com.contacts;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gokulan on 4/18/17.
 */

public class Core implements Serializable{
    private String name, roll_num, department;
    private ArrayList<String> emails;
    private ArrayList<String> phones;

    public Core(String nam, String roll, String dept)
    {
        name = nam;
        roll_num = roll;
        department = dept;
        emails = new ArrayList<>();
        phones = new ArrayList<>();
    }

    public void addEmail(String email)
    {
        emails.add(email);
    }

    public void addPhone(String phone)
    {
        phones.add(phone);
    }

    public String getName(){
        return name;
    }
    public String getRollNum(){
        return roll_num;
    }
    public String getDepartment(){
        return department;
    }
    public ArrayList<String> getEmails(){
        return emails;
    }
    public ArrayList<String> getPhones(){
        return phones;
    }
    public String getStringEmails(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<emails.size(); i++){
            builder.append(emails.get(i)+"#");
        }
        return builder.toString();
    }
    public String getStringPhones(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<phones.size(); i++){
            builder.append(phones.get(i)+"#");
        }
        return builder.toString();
    }
    public String getData(){
        return name+","+roll_num+","+department+","+getStringPhones()+","+getStringEmails();
    }

}
