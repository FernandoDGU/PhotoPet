package com.fcfm.photopet.utils.sqlite

class SetDB {

    companion object{
        val DB_NAME = "photopetdb"
        val DB_VERSION = 1
    }


    //ESQUEMA DE LAS TABLAS

    //USUARIO
    abstract class tblUser{
        //ATRIBUTOS
        companion object{
            val TABLE_NAME = "user"
            val COL_EMAIL = "email"
            val COL_FULLNAME =  "fullname"
            val COL_FIRSTNAME =  "firstname"
            val COL_LASTNAME =  "lastname"
            val COL_PHONE = "phone"
            val COL_DESCRIPTION =  "description"
            val COL_IMAGE =  "image"
        }
    }

    // TAG
    abstract class tblTag{
        companion object{
            val TABLE_NAME = "tag"
            val COL_ID = "id_tag"
            val COL_TAGNAME = "tagname"
        }
    }

    // PUBLICATION
    abstract class tblPublication{
        companion object{
            val TABLE_NAME = "publication"
            val COL_ID = "id_publication"
            val COL_DESCRIPTION = "description"
            val COL_EMAIL = "email"
            val COL_LIKES = "likes"
        }
    }

    // ALBUM
    abstract class tblAlbum{
        companion object{
            val TABLE_NAME = "album"
            val COL_ID = "id_album"
            val COL_PUBLICATION = "id_publication"
            val COL_IMAGE = "image"
            val COL_DESCRIPTION = "description"
        }
    }

    // LIKED_PUBLICATIONS
    abstract class tblLiked_publication{
        companion object{
            val TABLE_NAME = "liked_publications"
            val COL_ID = "id_liked_publications"
            val COL_ID_PUBLICATION = "id_publication"
            val COL_EMAIL = "email"
        }
    }

    // PUBLICATION TAG
    abstract class tblPublication_tag{
        companion object{
            val TABLE_NAME = "publication_tag"
            val COL_ID = "id_publication_tag"
            val COL_ID_PUBLICATION = "id_publication"
            val COL_ID_TAG = "id_tag"
        }
    }
}