package com.fcfm.photopet.model

class Publication(var id_publication: Int? = null, var description: String? = null, var email: String? = null, var imgArray: String? =  null,
                  var intIdImage:Int? =  null, var albums: MutableList<Album>? = null, var tags: MutableList<Tag>? = null) {

}