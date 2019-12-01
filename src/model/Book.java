package model;


import repository.BookRepository;

import java.sql.SQLException;

public class Book {
    private long id;
    private String tittle;
    private String author;
    private String category;
    private String status;

    public Book(String tittle, String author, String category) throws SQLException {
        this.tittle = tittle;
        this.author = author;
        this.category = category;
        this.status = "AVAILABLE";
        BookRepository.getInstance().addBook(this);
    }

    public Book(long id, String tittle, String author, String category, String status) {
        this.id = id;
        this.tittle = tittle;
        this.author = author;
        this.category = category;
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setStatus(String status) throws SQLException {
        this.status = status;
        BookRepository.getInstance().setBookStatus(this);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", tittle='" + tittle + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
