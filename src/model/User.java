package model;

import repository.UserRepository;

import java.sql.SQLException;

public class User {
    private long id;
    private String firstname;
    private String lastName;
    private String city;
    private String street;
    private String postalCode;
    private String email;
    private String hash;

    public User(String firstname, String lastName, String email, String city, String street, String postalCode, String hash) throws SQLException {
        this.firstname = firstname;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.email = email;
        this.hash = hash;
        UserRepository.getInstance().addUser(this);
    }

    public User(long id, String firstname, String lastName, String email, String city, String street, String postalCode) {
        this.id = id;
        this.firstname = firstname;
        this.lastName = lastName;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.email = email;
    }

    public String getHash() {
        return hash;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return id+";"+firstname+";"+lastName+";"+email+";"+city+";"+street+";"+postalCode;
    }
}
