package com.fcfm.photopet.utils

import com.fcfm.photopet.model.User

object loggedUser {
    private var user: User = User();
    public fun getUser() : User{
        return this.user;
    }
    public fun setUser(u:User){
        this.user = u;
    }

}