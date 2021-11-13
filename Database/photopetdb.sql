-- CREATE DATABASE photopetdb;
-- USE photopetdb; 

DROP TABLE IF EXISTS publication_tag;
DROP TABLE IF EXISTS liked_publications;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS publication;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS user;

-- select * from publication
-- select * from album

CREATE TABLE IF NOT EXISTS user (
	email 					VARCHAR(60)	NOT NULL PRIMARY KEY,
    fullname				VARCHAR(100) NOT NULL,
    firstname				VARCHAR(50) NOT NULL,
    lastname				VARCHAR(50) NOT NULL,
    password				VARCHAR(50) NOT NULL,
    phone 					VARCHAR(10) NULL,
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
	
    CONSTRAINT FK_PUBLICATION_USER FOREIGN KEY (email) REFERENCES user(email) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS album (
	id_album 				INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    id_publication			INT NOT NULL,
    image					MEDIUMBLOB NOT NULL,
    description 			TEXT NULL,
	
    CONSTRAINT FK_ALBUM_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS liked_publications (
	id_liked_publications	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	id_publication 			INT NOT NULL,
    email					VARCHAR(60) NOT NULL,
    
	CONSTRAINT FK_LP_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication) ON DELETE CASCADE,
	CONSTRAINT FK_LP_USER FOREIGN KEY (email) REFERENCES user(email) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS publication_tag (
	id_publication_tag		INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	id_publication 			INT NOT NULL,
    id_tag					INT NOT NULL,
    
	CONSTRAINT FK_PT_PUBLICATION FOREIGN KEY (id_publication) REFERENCES publication(id_publication) ON DELETE CASCADE,
	CONSTRAINT FK_PT_TAG FOREIGN KEY (id_tag) REFERENCES tag(id_tag) ON DELETE CASCADE
);

-- /////////SP AND FUNCTION//////////

-- SP TODAS LAS PUBLICACIONES CON LA PRIMERA FOTO 

DROP PROCEDURE IF EXISTS Post_images;

DELIMITER // 
CREATE PROCEDURE Post_images (

)
BEGIN
	SELECT distinct P.id_publication, P.description, P.email, A.image as imgArray, U.image as 'authorImage', U.fullname as 'author', publication_likes(P.id_publication) as 'likes'
	FROM publication P 
    INNER JOIN album A
    ON P.id_publication = A.id_publication
    INNER JOIN user U
    ON p.email = U.email
    Group by P.id_publication;
    
END //
DELIMITER ; 

DROP PROCEDURE IF EXISTS Post_images_id;

DELIMITER // 
CREATE PROCEDURE Post_images_id (
	pId_publication INT
)
BEGIN
	SELECT distinct P.id_publication, P.description, P.email, A.image as imgArray, U.image as 'authorImage', U.fullname as 'author', publication_likes(P.id_publication) as 'likes'
	FROM publication P 
    INNER JOIN album A
    ON P.id_publication = A.id_publication
    INNER JOIN user U
    ON p.email = U.email
    WHERE P.id_publication = pId_publication
    Group by P.id_publication;
    
END //
DELIMITER ; 

DROP PROCEDURE IF EXISTS Posts_images_user;

DELIMITER // 
CREATE PROCEDURE Posts_images_user (
	pEmail VARCHAR(60)
)
BEGIN
	SELECT distinct P.id_publication, P.description, P.email, A.image as imgArray, U.image as 'authorImage', U.fullname as 'author', publication_likes(P.id_publication) as 'likes'
	FROM publication P 
    INNER JOIN album A
    ON P.id_publication = A.id_publication
    INNER JOIN user U
    ON p.email = U.email
    WHERE P.email = pEmail
    Group by P.id_publication;
    
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS Post_images_user_likes;


DELIMITER // 
CREATE PROCEDURE Post_images_user_likes (
	pEmail VARCHAR(60)
)
BEGIN
	SELECT distinct P.id_publication, P.description, P.email, A.image as imgArray, U.image as 'authorImage', U.fullname as 'author', publication_likes(P.id_publication) as 'likes'
	FROM publication P 
    INNER JOIN album A
    ON P.id_publication = A.id_publication
    INNER JOIN user U
    ON p.email = U.email
    INNER JOIN liked_publications LP
    ON LP.email = pEmail
    WHERE LP.id_publication = P.id_publication
    Group by P.id_publication
    ORDER BY P.id_publication DESC;
    
END //
DELIMITER ; 

DROP PROCEDURE IF EXISTS Post_by_tag;



DELIMITER // 
CREATE PROCEDURE Post_by_tag (
	pId_tag INT
)
BEGIN
	SELECT distinct P.id_publication, P.description, P.email, A.image as imgArray, U.image as 'authorImage', U.fullname as 'author', publication_likes(P.id_publication) as 'likes'
	FROM publication P 
    INNER JOIN album A
    ON P.id_publication = A.id_publication
    INNER JOIN user U
    ON P.email = U.email
    INNER JOIN publication_tag PT
    ON P.id_publication = PT.id_publication
    WHERE PT.id_tag = pId_tag
    Group by P.id_publication;
    
END //
DELIMITER ; 

DROP PROCEDURE IF EXISTS Tags_by_Post;


DELIMITER // 
CREATE PROCEDURE Tags_by_Post (
	pId_publication INT
)
BEGIN
	SELECT distinct T.id_tag, T.tagname
	FROM tag T
    INNER JOIN publication P
    ON pId_publication = P.id_publication
    
    INNER JOIN publication_tag PT
    ON P.id_publication = PT.id_publication
    
    WHERE PT.id_tag = T.id_tag
    Group by T.id_tag;
    
END //
DELIMITER ; 


-- FUNCION QUE RETORNA EL ID DE LA ULTIMA PUBLICACION DE UN USUARIO
DROP FUNCTION IF EXISTS Last_publication;

DELIMITER // 
CREATE FUNCTION Last_publication(
	pa_email VARCHAR(60)
)
RETURNS INT 
READS SQL DATA 
BEGIN 
	DECLARE last_id INT;
    SELECT P.id_publication INTO last_id
    FROM publication P
	INNER JOIN user U 
	ON P.email = U.email
    WHERE P.email = pa_email
	ORDER BY P.id_publication DESC LIMIT 1;

	RETURN last_id;
END //
DELIMITER ; 


DROP FUNCTION IF EXISTS publication_likes;

DELIMITER // 
CREATE FUNCTION publication_likes(
	pId_publication INT
)
RETURNS INT 
READS SQL DATA 
BEGIN 
	DECLARE likes INT;
    SELECT COUNT(id_publication) INTO likes
    FROM liked_publications
    WHERE pId_publication = id_publication;

	RETURN likes;
END //
DELIMITER ; 

-- SELECT Last_publication('email@.com');