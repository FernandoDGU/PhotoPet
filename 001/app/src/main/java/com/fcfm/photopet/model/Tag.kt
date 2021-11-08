package com.fcfm.photopet.model

class Tag(var id_tag: Int? = null, var tagname: String? = null) {
    override fun toString(): String {
        return this.tagname!!
    }
}