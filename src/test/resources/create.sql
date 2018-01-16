CREATE TABLE authors (
  id int(11) NOT NULL PRIMARY KEY,
  name varchar(255) NOT NULL,
  country varchar(255) NOT NULL
);

CREATE TABLE books (
  id int(11) NOT NULL PRIMARY KEY,
  title varchar(255) NOT NULL,
  author_id int(11) NOT NULL REFERENCES authors(id)
);