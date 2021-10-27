-- USE photopetdb; 

DROP TABLE IF EXISTS publication_tag;
DROP TABLE IF EXISTS liked_publications;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS publication;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
	email 					VARCHAR(60)	NOT NULL PRIMARY KEY,
    name					VARCHAR(50) NOT NULL, 
    password				VARCHAR(50) NOT NULL,
    phone 					INT NULL,
    description 			TEXT NULL,
    image					MEDIUMBLOB
);

CREATE TABLE IF NOT EXISTS tag (
	id_tag 					INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    tagname					VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS publication (
	id_publication 			INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    description				TEXT NOT NULL,
    email					VARCHAR(60) NOT NULL,
	
    CONSTRAINT FK_PUBLICATION_USER FOREIGN KEY (email) REFERENCES user(email)
);

CREATE TABLE IF NOT EXISTS album (
	id_album 				INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    id_publication			INT NOT NULL,
    image					MEDIUMBLOB NOT NULL,
    description 			TEXT NULL,
	
    CONSTRAINT FK_ALBUM_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication)
);

CREATE TABLE IF NOT EXISTS liked_publications (
	id_liked_publications	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	id_publication 			INT NOT NULL,
    email					VARCHAR(60) NOT NULL,
    
	CONSTRAINT FK_LP_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication),
	CONSTRAINT FK_LP_USER FOREIGN KEY (email) REFERENCES user(email)
);

CREATE TABLE IF NOT EXISTS publication_tag (
	id_publication_tag		INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	id_publication 			INT NOT NULL,
    id_tag					INT NOT NULL,
    
	CONSTRAINT FK_PT_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication),
	CONSTRAINT FK_PT_TAG FOREIGN KEY (id_tag) REFERENCES tag(id_tag)
);