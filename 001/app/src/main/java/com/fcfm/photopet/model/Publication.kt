package com.fcfm.photopet.model

import java.io.Serializable

class Publication(var id_publication: Int? = null, var description: String? = null, var email: String? = null, var imgArray: String? =  null,
                  var albums: MutableList<Album>? = null, var tags: MutableList<Tag>? = null,
                  var authorImage: String? = null, var author: String? = null,
                  var likes: Int? = null)  : Serializable {

}

class LikedPublication(var id_liked_publication: Int? = null, var id_publication: Int? = null, var email: String? = null){

}