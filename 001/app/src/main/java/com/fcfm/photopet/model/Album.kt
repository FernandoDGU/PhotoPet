package com.fcfm.photopet.model

import java.io.Serializable

class Album(var id_album: Int? = null, var id_publication: Int? = null, var image: ByteArray? = null, var description: String? = null,
            var imageString: String? = null)  : Serializable {
}