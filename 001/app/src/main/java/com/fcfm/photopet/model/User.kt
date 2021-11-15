package com.fcfm.photopet.model



class User(var email: String? = null,
           var fullname: String? = null,
           var firstname: String? = null,
           var lastname: String? = null,
           var password: String? = null,
           var phone: String? = null,
           var description: String? = null,
           var image:String? =  null) {
}

class SuperData(
    var users: MutableList<User>,
    var tags: MutableList<Tag>,
    var publications: MutableList<Publication>,
    var albums: MutableList<Album>,
    var likepublication: MutableList<LikedPublication>,
    var publicationtag: MutableList<PublicationTag>,
){

}