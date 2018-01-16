DELETE FROM books;
DELETE FROM authors;

INSERT INTO authors(id, name, country) VALUES (1, 'Stephen Hawking', 'United Kingdom');
INSERT INTO authors(id, name, country) VALUES (2, 'Friedrich Nietzsche', 'Prussia');

INSERT INTO books(id, title, author_id) VALUES (1, 'A Brief History of Time', 1);
INSERT INTO books(id, title, author_id) VALUES (2, 'The Universe in a Nutshell', 1);
INSERT INTO books(id, title, author_id) VALUES (3, 'The Birth of Tragedy', 2);
INSERT INTO books(id, title, author_id) VALUES (4, 'Also sprach Zarathustra', 2);
