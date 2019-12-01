package model;


import java.time.LocalDate;

public class Rent {
    private long id;
    private long idUser;
    private long idBook;
    private String status;
    private LocalDate dateOfAcction;

    public Rent(long idUser, long idBook, String status) {
        this.idUser = idUser;
        this.idBook = idBook;
        this.status = status;
    }

    public Rent(long id, long idUser, long idBook, String status) {
        this.id = id;
        this.idUser = idUser;
        this.idBook = idBook;
        this.status = status;
    }

    public long getIdUser() {
        return idUser;
    }

    public long getIdBook() {
        return idBook;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDateOfAcction() {
        return dateOfAcction;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
