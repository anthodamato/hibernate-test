/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.hibernate;

import com.test.hibernate.model.Book;
import com.test.hibernate.model.BookFormat;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class BookUtils {

    public static Book create1stFreudBook() {
        Book book = new Book();
        book.setTitle("The Interpretation of Dreams");
        book.setAuthor("Sigmund Freud");

        BookFormat bookFormat = new BookFormat();
        bookFormat.setFormat("paperback");
        bookFormat.setPages(688);

        book.setBookFormat(bookFormat);
        return book;
    }

    public static Book create2ndFreudBook() {
        Book book = new Book();
        book.setTitle("The Ego and the Id");
        book.setAuthor("Sigmund Freud");

        BookFormat bookFormat = new BookFormat();
        bookFormat.setFormat("electronic");
        bookFormat.setPages(128);

        book.setBookFormat(bookFormat);
        return book;
    }

    public static Book create1stJoyceBook() {
        Book book = new Book();
        book.setTitle("Ulysses");
        book.setAuthor("James Joyce");

        BookFormat bookFormat = new BookFormat();
        bookFormat.setFormat("paperback");
        bookFormat.setPages(1010);

        book.setBookFormat(bookFormat);
        return book;
    }

    public static Book create1stLondonBook() {
        Book book = new Book();
        book.setTitle("The Call of the Wild");
        book.setAuthor("Jack London");

        BookFormat bookFormat = new BookFormat();
        bookFormat.setFormat("hardcover");
        bookFormat.setPages(58);

        book.setBookFormat(bookFormat);
        return book;
    }

    public static Book create2ndLondonBook() {
        Book book = new Book();
        book.setTitle("South Sea Tales");
        book.setAuthor("Jack London");

        BookFormat bookFormat = new BookFormat();
        bookFormat.setFormat("electronic");
        bookFormat.setPages(215);

        book.setBookFormat(bookFormat);
        return book;
    }

}
