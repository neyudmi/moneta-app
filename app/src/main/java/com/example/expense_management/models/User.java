package com.example.expense_management.models;
public class User {
    private Integer id;
    private String sdt;
    private String password;
    private String name;

    public User(String sdt, String password, String name) {
        this.sdt = sdt;
        this.password = password;
        this.name = name;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
