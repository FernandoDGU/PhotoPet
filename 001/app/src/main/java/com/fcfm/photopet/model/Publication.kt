package com.fcfm.photopet.model

class Publication(var id_publication: Int? = null, var description: String? = null, var email: String? = null, var imgArray:ByteArray? =  null,
                  var intIdImage:Int? =  null, var albums: MutableList<Album>? = null) {
    fun test(){
        this.albums!!.get(0)
    }
}