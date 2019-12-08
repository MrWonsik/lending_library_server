package model;


import repository.BookRepository;

import java.sql.SQLException;

public class Book {
    private long id;
    private String tittle;
    private String author;
    private String category;
    private String publicationDate;
    private String publishingHouse;
    private int numberOfPages;
    private String status;

    public Book(String tittle, String author, String category, String publicationDate, String publishingHouse, int numberOfPages) throws SQLException {
        this.tittle = tittle;
        this.author = author;
        this.category = category;
        this.status = "AVAILABLE";
        this.publicationDate = publicationDate;
        this.publishingHouse = publishingHouse;
        this.numberOfPages = numberOfPages;
        BookRepository.getInstance().addBook(this);
    }

    public Book(long id, String tittle, String author, String category, String status, String publicationDate, String publishingHouse, int numberOfPages) {
        this.id = id;
        this.tittle = tittle;
        this.author = author;
        this.category = category;
        this.status = status;
        this.publicationDate = publicationDate;
        this.publishingHouse = publishingHouse;
        this.numberOfPages = numberOfPages;
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

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setStatus(String status) throws SQLException {
        this.status = status;
        BookRepository.getInstance().setBookStatus(this);
    }

    @Override
    public String toString() {
        return id+";"+tittle+";"+author+";"+category+";"+status+";"+publicationDate+";"+publishingHouse+";"+numberOfPages;
    }


}
