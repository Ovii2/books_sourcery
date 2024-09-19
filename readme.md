# Books Project

## Overview

The Books Project is a web application that allows users to browse and rate books, while providing administrative
functionality for managing the book inventory. This application includes features for both users and administrators,
ensuring a comprehensive book management experience.

**This project uses local MySql database. So use your favorite mysql database manager.
Adjust `application.properties` by your needs. Create database and run application. Tables will be created
automatically.**

**After registering user go to your database users table and change `ROLE_USER` to `ROLE_ADMIN` manually. In this way you
will
have
and admin.**

## How to start this project

* Clone or download this repository.
* Open with intellij (recommended).
* Start your local mysql server.
* Start the application.

## Authentication

* Register: Users can create an account by navigating to http://localhost:8080/api/auth/register and providing a
  username, email, and password. This will allow users to rate books and manage their own book lists.

* Login: Users can log in to their account by navigating to http://localhost:8080/api/auth/login and providing their
  username and password. This will allow them to view books and rate them.

* Logout: Users can log out of their account by navigating to http://localhost:8080/api/auth/logout

## Features

### User Features

* View Books: Browse through a collection of books with detailed information including title, author, release year and
  rating.
* Rate Books: Rate books based on personal experience. Ratings help other users make informed decisions.

### Admin Features

* Add Books: Introduce new books to the system by providing details such as title, author and release year.
* Update Books: Modify existing book details to ensure information remains accurate and up-to-date.
* Delete Books: Remove books from the system that are no longer relevant or needed.

## API Endpoints

### Books

* GET /api/v1/books: Retrieve a list of all books.
* POST /api/v1/books/add: Add a new book (Admin only).
* PATCH /api/v1/books/update/{bookId}: Update book details (Admin only).
* DELETE /api/v1/books/{bookId}: Remove a book from the collection (Admin only).
* POST /api/v1/books/{bookId}/rate: Rate a book.

### Authentication

* POST /api/auth/register: Register a new user account.
* POST /api/auth/login: Login to an existing account.
* POST /api/auth/logout: Logout of the current session.