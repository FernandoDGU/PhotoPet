package com.fcfm.photopet.utils.sqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.fcfm.photopet.model.*

class DataDB (var context: Context): SQLiteOpenHelper(context, SetDB.DB_NAME, null, SetDB.DB_VERSION){

    override fun onCreate(db: SQLiteDatabase?) {
        try {

            //TABLAS CORREGIDAS
            // USUARIO
            val createUserTable:String = ("CREATE TABLE " + SetDB.tblUser.TABLE_NAME + "(" +
                    SetDB.tblUser.COL_EMAIL + " VARCHAR(60) PRIMARY KEY NOT NULL," +
                    SetDB.tblUser.COL_FULLNAME + " VARCHAR(100) NOT NULL," +
                    SetDB.tblUser.COL_FIRSTNAME + " VARCHAR(50) NOT NULL,"+
                    SetDB.tblUser.COL_LASTNAME + " VARCHAR(50) NOT NULL," +
                    SetDB.tblUser.COL_PHONE + " VARCHAR(10)," +
                    SetDB.tblUser.COL_DESCRIPTION + " TEXT,"+
                    SetDB.tblUser.COL_IMAGE + " BLOB)")

            db?.execSQL(createUserTable)

            // TAG
            val createTagTable:String = "CREATE TABLE " + SetDB.tblTag.TABLE_NAME + "(" +
                    SetDB.tblTag.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    SetDB.tblTag.COL_TAGNAME + " VARCHAR(50) NOT NULL)"

            db?.execSQL(createTagTable)


            // PUBLICATION
            val createPublicationTable:String = ("CREATE TABLE " + SetDB.tblPublication.TABLE_NAME + "(" +
                    SetDB.tblPublication.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    SetDB.tblPublication.COL_DESCRIPTION + " TEXT NOT NULL, " +
                    SetDB.tblPublication.COL_EMAIL + " VARCHAR(60) NOT NULL, " +
                    SetDB.tblPublication.COL_LIKES + " INTEGER NOT NULL," +
                    " FOREIGN KEY("+ SetDB.tblPublication.COL_EMAIL + ") REFERENCES " +
                    SetDB.tblUser.TABLE_NAME + "("+  SetDB.tblUser.COL_EMAIL + ") ON DELETE CASCADE" +
                    ")")

            db?.execSQL(createPublicationTable)


            // ALBUM
            val createAlbumTable:String = "CREATE TABLE " + SetDB.tblAlbum.TABLE_NAME + "(" +
                    SetDB.tblAlbum.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    SetDB.tblAlbum.COL_PUBLICATION + " INTEGER NOT NULL," +
                    SetDB.tblAlbum.COL_IMAGE + " BLOB," +
                    SetDB.tblAlbum.COL_DESCRIPTION + " TEXT," +
                    "FOREIGN KEY("+ SetDB.tblAlbum.COL_PUBLICATION + ") REFERENCES " +
                    SetDB.tblPublication.TABLE_NAME + "("+ SetDB.tblPublication.COL_ID + ") ON DELETE CASCADE" +
                    ")"

            db?.execSQL(createAlbumTable)


            // LIKED_PUBLICATIONS
            val createLiked_publicationTable:String = "CREATE TABLE " + SetDB.tblLiked_publication.TABLE_NAME + "(" +
                    SetDB.tblLiked_publication.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    SetDB.tblLiked_publication.COL_ID_PUBLICATION + " INTEGER NOT NULL," +
                    SetDB.tblLiked_publication.COL_EMAIL + " VARCHAR(60) NOT NULL," +
                    " FOREIGN KEY("+  SetDB.tblLiked_publication.COL_ID_PUBLICATION + ") REFERENCES " +
                    SetDB.tblPublication.TABLE_NAME + "("+ SetDB.tblPublication.COL_ID + ") ON DELETE CASCADE," +
                    " FOREIGN KEY("+ SetDB.tblLiked_publication.COL_EMAIL + ") REFERENCES " +
                    SetDB.tblUser.TABLE_NAME + "("+ SetDB.tblUser.COL_EMAIL + ") ON DELETE CASCADE" +
                    ")"

            db?.execSQL(createLiked_publicationTable)


            // PUBLICATION TAGS
            val createPublication_tag:String = "CREATE TABLE " + SetDB.tblPublication_tag.TABLE_NAME + "(" +
                    SetDB.tblPublication_tag.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    SetDB.tblPublication_tag.COL_ID_PUBLICATION + " INTEGER NOT NULL," +
                    SetDB.tblPublication_tag.COL_ID_TAG + " INTEGER NOT NULL," +
                    " FOREIGN KEY("+  SetDB.tblPublication_tag.COL_ID_PUBLICATION + ") REFERENCES " +
                    SetDB.tblPublication.TABLE_NAME + "("+ SetDB.tblPublication.COL_ID + ") ON DELETE CASCADE," +
                    " FOREIGN KEY("+ SetDB.tblPublication_tag.COL_ID_TAG + ") REFERENCES " +
                    SetDB.tblTag.TABLE_NAME + "("+ SetDB.tblTag.COL_ID + ") ON DELETE CASCADE" +
                    ")"
            db?.execSQL(createPublication_tag)
            Log.e("ENTRO","CREO TABLAS")

        }catch (e: Exception){
            Log.e("Exception", e.toString())
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + SetDB.tblUser.TABLE_NAME)
        onCreate(db)
    }



    // --- USUARIO INSERT Y GET ---
    public fun insertUser(user: User):Boolean{
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()
        var boolResult:Boolean = true


        values.put(SetDB.tblUser.COL_EMAIL, user.email)
        values.put(SetDB.tblUser.COL_FULLNAME, user.fullname)
        values.put(SetDB.tblUser.COL_FIRSTNAME, user.firstname)
        values.put(SetDB.tblUser.COL_LASTNAME, user.lastname)
        values.put(SetDB.tblUser.COL_PHONE, user.phone)
        values.put(SetDB.tblUser.COL_DESCRIPTION, user.description)
        values.put(SetDB.tblUser.COL_IMAGE, user.image)

        try {
            val result =  dataBase.insert(SetDB.tblUser.TABLE_NAME, null, values)

            /*if (result == (0).toLong()) {
                Toast.makeText(this.context, "Failed", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()
            }*/


        }catch (e: Exception){
            Log.e("Exception", e.toString())
            boolResult = false
        }

        dataBase.close()

        return boolResult
    }

    @SuppressLint("Range")
    public fun getUser002 (email:String) :User?{
        var user:User? = User()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT user.email, user.fullname, user.firstname, user.lastname, user.phone, user.description, user.image " +
                "FROM user " +
                "WHERE user.email" + " == '${email.toString()}'", null)

        if (data != null && data.moveToFirst()){
            user = User()
            user.email = data.getString(data.getColumnIndex("email")).toString()
            user.fullname = data.getString(data.getColumnIndex("fullname")).toString()
            user.firstname = data.getString(data.getColumnIndex("firstname")).toString()
            user.lastname = data.getString(data.getColumnIndex("lastname")).toString()

            if(data.getString(data.getColumnIndex("phone")) == null) {user.phone = null}
            else {user.phone = data.getString(data.getColumnIndex("phone")).toString()}

            if(data.getString(data.getColumnIndex("description")) == null){user.description = null}
            else {user.description = data.getString(data.getColumnIndex("description")).toString()}
            user.image = data.getString(data.getColumnIndex("image")).toString()
        }

        data.close()
        return user
    }

    public fun deleteUserTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblUser.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    // --- TAG INSERT Y GET ALL ---
    public fun insertTag(tag: Tag):Boolean{
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()
        var boolResult:Boolean = true

        values.put(SetDB.tblTag.COL_ID, tag.id_tag)
        values.put(SetDB.tblTag.COL_TAGNAME, tag.tagname)

        try{
            val result = dataBase.insert(SetDB.tblTag.TABLE_NAME, null, values)
            /*if (result == (0).toLong()){
                Toast.makeText(this.context, "Tag insert Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "Tag insert Success", Toast.LENGTH_SHORT).show()
            }*/
        }catch (e: Exception){
            Log.e("Exception", e.toString())
            boolResult = false
        }

        dataBase.close()
        return boolResult
    }

    @SuppressLint("Range")
    public fun getAllTags():MutableList<Tag>{
        val ListTags:MutableList<Tag> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT tag.id_tag, tag.tagname " +
                "FROM tag ", null)

        if(data!= null && data.moveToFirst()){
            do{
                val tag:Tag = Tag()
                tag.id_tag = data.getString(data.getColumnIndex(SetDB.tblTag.COL_ID)).toInt()
                tag.tagname = data.getString(data.getColumnIndex(SetDB.tblTag.COL_TAGNAME)).toString()

                ListTags.add(tag)
            }while (data.moveToNext())
        }
        data.close()
        return ListTags
    }



    public fun deleteTagTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblTag.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    // ---- POST INSERT, GET ALL, GET -----
    public fun insertPost(Post: Publication){
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()

        values.put(SetDB.tblPublication.COL_ID, Post.id_publication)
        values.put(SetDB.tblPublication.COL_DESCRIPTION, Post.description)
        values.put(SetDB.tblPublication.COL_EMAIL, Post.email)
        values.put(SetDB.tblPublication.COL_LIKES, Post.likes)

        try{
            val result = dataBase.insert(SetDB.tblPublication.TABLE_NAME, null, values)
            /*if (result == (0).toLong()){
                Toast.makeText(this.context, "Publication insert Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "Publication insert Success", Toast.LENGTH_SHORT).show()
            }*/
        }catch (e: Exception){
            Log.e("Exception", e.toString())
        }
        dataBase.close()
    }

    @SuppressLint("Range")
    public fun getPost(idPost:Int) : Publication?{
        var Post:Publication? = Publication()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT publication.id_publication, publication.description, publication.email, publication.likes " +
                "FROM publication " +
                "WHERE publication.id_publication" + "== ${idPost}", null)

        if (data != null && data.moveToFirst()) {
            Post = Publication()
            Post.id_publication = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_ID)).toInt()
            Post.description = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_DESCRIPTION)).toString()
            Post.email = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_EMAIL)).toString()
            Post.likes = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_LIKES)).toInt()
        }

        data.close()
        return Post
    }

    @SuppressLint("Range")
    public fun getAllPost():MutableList<Publication>{
        val ListPost:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT publication.id_publication, publication.description, publication.email, publication.likes " +
                "FROM publication", null)

        if(data!= null && data.moveToFirst()){
            do{
                val Post:Publication = Publication()
                Post.id_publication = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_ID)).toInt()
                Post.description = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_DESCRIPTION)).toString()
                Post.email = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_EMAIL)).toString()
                Post.likes = data.getString(data.getColumnIndex(SetDB.tblPublication.COL_LIKES)).toInt()

                ListPost.add(Post)
            }while (data.moveToNext())
        }
        data.close()
        return ListPost
    }

    public fun deletePostTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblPublication.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    // --- Album INSERT, GET ALL, GET ---
    public fun insertAlbum(Album:Album){
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()

        values.put(SetDB.tblAlbum.COL_ID, Album.id_album)
        values.put(SetDB.tblAlbum.COL_PUBLICATION, Album.id_publication)
        values.put(SetDB.tblAlbum.COL_IMAGE, Album.imageString)
        values.put(SetDB.tblAlbum.COL_DESCRIPTION, Album.description)


        try{
            val result = dataBase.insert(SetDB.tblAlbum.TABLE_NAME, null, values)
            /*if (result == (0).toLong()){
                Toast.makeText(this.context, "Album insert Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "Album insert Success", Toast.LENGTH_SHORT).show()
            }*/
        }catch (e: Exception){
            Log.e("Exception", e.toString())
        }
        dataBase.close()
    }

    @SuppressLint("Range")
    public fun getAlbum(idAlbum: Int): Album?{
        var Album:Album? = Album()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT album.id_album, album.id_publication, album.image, album.description " +
                "FROM album " +
                "WHERE album.id_album" + "== ${idAlbum}", null)

        if (data != null && data.moveToFirst()) {
            Album = Album()
            Album.id_publication = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_ID)).toInt()
            Album.id_publication = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_PUBLICATION)).toInt()
            Album.imageString = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_PUBLICATION)).toString() //Duda de si cambiar por blob
            Album.description = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_DESCRIPTION)).toString()
        }
        data.close()
        return Album
    }

    @SuppressLint("Range")
    public fun getAllAlbums():MutableList<Album>{
        val ListAlbums:MutableList<Album> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT album.id_album, album.id_publication, album.image, album.description " +
                "FROM album", null)

        if(data!= null && data.moveToFirst()){
            do{
                val Album:Album = Album()
                Album.id_publication = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_ID)).toInt()
                Album.id_publication = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_PUBLICATION)).toInt()
                Album.imageString = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_PUBLICATION)).toString() //Duda de si cambiar por blob
                Album.description = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_DESCRIPTION)).toString()

                ListAlbums.add(Album)
            }while (data.moveToNext())
        }
        data.close()
        return ListAlbums

    }

    public fun getAlbumsPost(id_post:Int):MutableList<Album>{
        val ListAlbums:MutableList<Album> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT album.id_album, album.id_publication, album.image, album.description " +
                "FROM album WHERE album.id_publication == " + id_post, null)

        if(data!= null && data.moveToFirst()){
            do{
                val Album:Album = Album()
                Album.id_album = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_ID)).toInt()
                Album.id_publication = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_PUBLICATION)).toInt()
                Album.imageString = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_IMAGE)).toString() //Duda de si cambiar por blob
                Album.description = data.getString(data.getColumnIndex(SetDB.tblAlbum.COL_DESCRIPTION)).toString()

                ListAlbums.add(Album)
            }while (data.moveToNext())
        }
        data.close()
        return ListAlbums

    }

    public fun deleteAlbumTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblAlbum.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    // --- Like INSERT, GET ALL---
    public fun insertLike(Like:LikedPublication){
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()

        values.put(SetDB.tblLiked_publication.COL_ID, Like.id_liked_publications)
        values.put(SetDB.tblLiked_publication.COL_ID_PUBLICATION, Like.id_publication)
        values.put(SetDB.tblLiked_publication.COL_EMAIL, Like.email)


        try{
            val result = dataBase.insert(SetDB.tblLiked_publication.TABLE_NAME, null, values)
            /*if (result == (0).toLong()){
                Toast.makeText(this.context, "Like insert Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "Like insert Success", Toast.LENGTH_SHORT).show()
            }*/
        }catch (e: Exception){
            Log.e("Exception", e.toString())
        }
        dataBase.close()

    }

    @SuppressLint("Range")
    public fun getAllLikes():MutableList<LikedPublication>{
        val ListLikes:MutableList<LikedPublication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT liked_publications.id_liked_publications, liked_publications.id_publication, liked_publications.email " +
                "FROM liked_publications", null)

        if(data!= null && data.moveToFirst()){
            do{
                val Likes:LikedPublication = LikedPublication()
                Likes.id_liked_publications = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_ID)).toInt()
                Likes.id_publication = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_ID_PUBLICATION)).toInt()
                Likes.email = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_EMAIL)).toString()

                ListLikes.add(Likes)
            }while (data.moveToNext())
        }

        data.close()
        return ListLikes
    }

    @SuppressLint("Range")
    public fun searchUserLike(id_post:Int, email:String):LikedPublication{
        val Likes:LikedPublication = LikedPublication()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT liked_publications.id_liked_publications, liked_publications.id_publication, liked_publications.email " +
                "FROM liked_publications WHERE liked_publications.id_publication == " + id_post +
                " AND liked_publications.email == '${email}'", null)

        if(data!= null && data.moveToFirst()){
            do{

                Likes.id_liked_publications = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_ID)).toInt()
                Likes.id_publication = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_ID_PUBLICATION)).toInt()
                Likes.email = data.getString(data.getColumnIndex(SetDB.tblLiked_publication.COL_EMAIL)).toString()

            }while (data.moveToNext())
        }

        data.close()
        return Likes
    }

    public fun deleteLikesTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblLiked_publication.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    // --- Publication tags INSERT, GET ALL---
    public fun insertPostTag(Pt: Publication_tag){
        val dataBase: SQLiteDatabase = this.writableDatabase
        val values: ContentValues = ContentValues()

        values.put(SetDB.tblPublication_tag.COL_ID, Pt.id_publication_tag)
        values.put(SetDB.tblPublication_tag.COL_ID_PUBLICATION, Pt.id_publication)
        values.put(SetDB.tblPublication_tag.COL_ID_TAG, Pt.id_tag)


        try{
            val result = dataBase.insert(SetDB.tblPublication_tag.TABLE_NAME, null, values)
            /*if (result == (0).toLong()){
                Toast.makeText(this.context, "PostTag insert Failed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this.context, "PostTag insert Success", Toast.LENGTH_SHORT).show()
            }*/
        }catch (e: Exception){
            Log.e("Exception", e.toString())
        }
        dataBase.close()
    }

    @SuppressLint("Range")
    public fun getAllPt():MutableList<Publication_tag>{
        val Pt:MutableList<Publication_tag> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT publication_tag.id_publication_tag, publication_tag.id_publication, publication_tag.id_tag " +
                "FROM publication_tag", null)

        if(data!= null && data.moveToFirst()){
            do{
                val PostTag:Publication_tag = Publication_tag()
                PostTag.id_publication_tag = data.getString(data.getColumnIndex(SetDB.tblPublication_tag.COL_ID)).toInt()
                PostTag.id_publication = data.getString(data.getColumnIndex(SetDB.tblPublication_tag.COL_ID_PUBLICATION)).toInt()
                PostTag.id_tag = data.getString(data.getColumnIndex(SetDB.tblPublication_tag.COL_ID_TAG)).toInt()

                Pt.add(PostTag)
            }while (data.moveToNext())
        }

        data.close()
        return Pt
    }

    public fun deletePostTagTable():Boolean{
        val db = this.writableDatabase
        var boolResult:Boolean =  false
        try{


            val _success = db.delete(SetDB.tblPublication_tag.TABLE_NAME, null, null)
            db.close()

            boolResult = Integer.parseInt("$_success") != -1


        }catch (e: java.lang.Exception){

            Log.e("Execption", e.toString())
        }

        return  boolResult
    }



    //SP
    @SuppressLint("Range")
    public fun Post_images():MutableList<Publication>{
        val spPosts:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT DISTINCT publication.id_publication, publication.description, publication.email, album.image AS imgArray, user.image AS authorImage, user.fullname AS author , publication.likes AS likes " +
                "FROM publication INNER JOIN album ON publication.id_publication == album.id_publication INNER JOIN user ON publication.email == user.email ORDER BY publication.id_publication DESC", null)

        if(data!=null && data.moveToFirst()){
            do{
                val sp:Publication = Publication()
                sp.id_publication = data.getString(data.getColumnIndex("id_publication")).toInt()
                sp.description = data.getString(data.getColumnIndex("description")).toString()
                sp.email = data.getString(data.getColumnIndex("email")).toString()
                sp.imgArray = data.getString(data.getColumnIndex("imgArray")).toString()
                sp.authorImage = data.getString(data.getColumnIndex("authorImage")).toString()
                sp.author = data.getString(data.getColumnIndex("author")).toString()
                sp.likes = data.getString(data.getColumnIndex("likes")).toInt()

                spPosts.add(sp)
            }while (data.moveToNext())
        }
        data.close()
        return spPosts
    }

    @SuppressLint("Range")
    public fun Post_images_id(id_post:Int):MutableList<Publication>{
        val spPost_imageid:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT DISTINCT publication.id_publication, publication.description, publication.email, album.image AS imgArray, user.image AS authorImage, user.fullname AS author , publication.likes AS likes " +
                "FROM publication INNER JOIN album ON publication.id_publication == album.id_publication INNER JOIN user ON publication.email == user.email " +
                "WHERE publication.id_publication == ${id_post} " +
                "GROUP BY publication.id_publication ", null)


        if(data!=null && data.moveToFirst()){
            do{
                val sp:Publication = Publication()
                sp.id_publication = data.getString(data.getColumnIndex("id_publication")).toInt()
                sp.description = data.getString(data.getColumnIndex("description")).toString()
                sp.email = data.getString(data.getColumnIndex("email")).toString()
                sp.imgArray = data.getString(data.getColumnIndex("imgArray")).toString()
                sp.authorImage = data.getString(data.getColumnIndex("authorImage")).toString()
                sp.author = data.getString(data.getColumnIndex("author")).toString()
                sp.likes = data.getString(data.getColumnIndex("likes")).toInt()

                spPost_imageid.add(sp)
            }while (data.moveToNext())
        }
        data.close()
        return spPost_imageid
    }


    @SuppressLint("Range")
    public fun Posts_images_user(email: String):MutableList<Publication>{
        val spPost_images_user:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT DISTINCT publication.id_publication, publication.description, publication.email, album.image AS imgArray, user.image AS authorImage, user.fullname AS author , publication.likes AS likes " +
                "FROM publication INNER JOIN album ON publication.id_publication == album.id_publication INNER JOIN user ON publication.email == user.email " +
                "WHERE publication.email == '${email}' " +
                "GROUP BY publication.id_publication ", null)


        if(data!=null && data.moveToFirst()){
            do{
                val sp:Publication = Publication()
                sp.id_publication = data.getString(data.getColumnIndex("id_publication")).toInt()
                sp.description = data.getString(data.getColumnIndex("description")).toString()
                sp.email = data.getString(data.getColumnIndex("email")).toString()
                sp.imgArray = data.getString(data.getColumnIndex("imgArray")).toString()
                sp.authorImage = data.getString(data.getColumnIndex("authorImage")).toString()
                sp.author = data.getString(data.getColumnIndex("author")).toString()
                sp.likes = data.getString(data.getColumnIndex("likes")).toInt()

                spPost_images_user.add(sp)
            }while (data.moveToNext())
        }
        data.close()
        return spPost_images_user
    }


    @SuppressLint("Range")
    public fun Post_images_user_likes(email:String):MutableList<Publication>{
        val spPostIUL:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT DISTINCT publication.id_publication, publication.description, publication.email, album.image AS imgArray, user.image AS authorImage, user.fullname AS author, publication.likes AS likes " +
                "FROM publication INNER JOIN album ON publication.id_publication == album.id_publication " +
                "INNER JOIN user ON publication.email == user.email " +
                "INNER JOIN liked_publications ON liked_publications.email == '${email}' " +
                "WHERE  liked_publications.id_publication == publication.id_publication ", null)

        if(data!=null && data.moveToFirst()){
            do{
                val sp:Publication = Publication()
                sp.id_publication = data.getString(data.getColumnIndex("id_publication")).toInt()
                sp.description = data.getString(data.getColumnIndex("description")).toString()
                sp.email = data.getString(data.getColumnIndex("email")).toString()
                sp.imgArray = data.getString(data.getColumnIndex("imgArray")).toString()
                sp.authorImage = data.getString(data.getColumnIndex("authorImage")).toString()
                sp.author = data.getString(data.getColumnIndex("author")).toString()
                sp.likes = data.getString(data.getColumnIndex("likes")).toInt()

                spPostIUL.add(sp)
            }while (data.moveToNext())
        }
        data.close()
        return spPostIUL
    }


    @SuppressLint("Range")
    public fun Post_by_tag(id_tag:Int):MutableList<Publication>{
        val spPosttag:MutableList<Publication> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase


        val data = database.rawQuery("SELECT DISTINCT publication.id_publication, publication.description, publication.email, album.image AS imgArray, user.image AS authorImage, user.fullname AS author , publication.likes AS likes " +
                "FROM publication INNER JOIN album ON publication.id_publication == album.id_publication " +
                "INNER JOIN user ON publication.email == user.email " +
                "INNER JOIN publication_tag ON publication.id_publication == publication_tag.id_publication " +
                "WHERE  publication_tag.id_tag == ${id_tag} ", null)

        if(data!=null && data.moveToFirst()){
            do{
                val sp:Publication = Publication()
                sp.id_publication = data.getString(data.getColumnIndex("id_publication")).toInt()
                sp.description = data.getString(data.getColumnIndex("description")).toString()
                sp.email = data.getString(data.getColumnIndex("email")).toString()
                sp.imgArray = data.getString(data.getColumnIndex("imgArray")).toString()
                sp.authorImage = data.getString(data.getColumnIndex("authorImage")).toString()
                sp.author = data.getString(data.getColumnIndex("author")).toString()
                sp.likes = data.getString(data.getColumnIndex("likes")).toInt()

                spPosttag.add(sp)
            }while (data.moveToNext())
        }
        data.close()

        return spPosttag
    }

    @SuppressLint("Range")
    public fun Tags_by_Posts(id_post: Int):MutableList<Tag>{
        val spTag:MutableList<Tag> = ArrayList()
        val database: SQLiteDatabase = this.writableDatabase

        val data = database.rawQuery("SELECT DISTINCT tag.id_tag, tag.tagname " +
                "FROM tag " +
                "INNER JOIN publication ON ${id_post} ==  publication.id_publication " +
                "INNER JOIN publication_tag ON publication.id_publication == publication_tag.id_publication " +
                "WHERE publication_tag.id_tag == tag.id_tag ", null)

        if(data!=null && data.moveToFirst()) {
            do {
                val sp: Tag = Tag()
                sp.id_tag = data.getString(data.getColumnIndex("id_tag")).toInt()
                sp.tagname = data.getString(data.getColumnIndex("tagname")).toString()

                spTag.add(sp)

            } while (data.moveToNext())

        }
        data.close()
        return spTag
    }

}
