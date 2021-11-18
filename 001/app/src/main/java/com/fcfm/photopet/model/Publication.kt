package com.fcfm.photopet.model

import com.fcfm.photopet.utils.UserApplication
import java.io.Serializable

class Publication(var id_publication: Int? = null, var description: String? = "", var email: String? = "", var imgArray: String? =  "",
                  var albums: MutableList<Album>? = mutableListOf(), var tags: MutableList<Tag>? = mutableListOf(),
                  var authorImage: String? = "", var author: String? = "",
                  var likes: Int? = 0)  : Serializable {

                      inner class PublicationSQLite{
                          fun CreatePost(posts:MutableList<Publication>){
                              if(!UserApplication.dbHelper.deletePostTable()){
                                  return
                              }
                              for (p in posts){
                                  UserApplication.dbHelper.insertPost(p)
                              }
                          }

                          fun GetPost():Publication{
                              val getPost:Publication? = UserApplication.dbHelper.getPost(1)!!
                              return getPost!!

                          }

                          fun GetAllPost(): MutableList<Publication> {
                              return UserApplication.dbHelper.getAllPost()
                          }
                          fun GetPostImages(): MutableList<Publication> {
                              return UserApplication.dbHelper.Post_images()
                          }
                          fun GetPostImageid(id_post: Int): MutableList<Publication> {
                              return UserApplication.dbHelper.Post_images_id(id_post)
                          }
                          fun GetPostImageUser(email: String): MutableList<Publication> {
                              return UserApplication.dbHelper.Posts_images_user(email)
                          }
                          fun GetPostIUL(email: String): MutableList<Publication> {
                              return UserApplication.dbHelper.Post_images_user_likes(email)
                          }
                          fun GetPostTag(id_tag: Int): MutableList<Publication> {
                              return UserApplication.dbHelper.Post_by_tag(id_tag)
                          }
                      }

}

class LikedPublication(var id_liked_publications: Int? = null, var id_publication: Int? = null, var email: String? = null){

    inner class LikedPublicationSQLite{
        fun CreateLiked(likes:MutableList<LikedPublication>){

            if(!UserApplication.dbHelper.deleteLikesTable()){
                return
            }

            for (l in likes){
                UserApplication.dbHelper.insertLike(l)
            }
        }

        fun GetAllLikes(): MutableList<LikedPublication> {
            val getAllLikes: MutableList<LikedPublication> = UserApplication.dbHelper.getAllLikes()
            return getAllLikes
        }

        fun GetUserLike(id_post:Int, email:String): LikedPublication{
            return UserApplication.dbHelper.searchUserLike(id_post, email)
        }
    }

}

class Publication_tag(var id_publication_tag: Int? = null, var id_publication: Int? = null, var id_tag: Int? = null){

    inner class Publication_tagSQLite{
        fun CreatePostTag(Pts: MutableList<Publication_tag>){
            if(!UserApplication.dbHelper.deletePostTagTable()){
                return
            }

            for (pt in Pts){
                UserApplication.dbHelper.insertPostTag(pt)
            }
        }

        private fun GetAllPostTag() {
            val getAllPT: MutableList<Publication_tag> = UserApplication.dbHelper.getAllPt()

        }
    }

}