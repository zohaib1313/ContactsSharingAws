package lads.contancsharing.www.models


import android.graphics.drawable.Drawable


data class ContactsInfo(

    var name: String,
    var number: String,
    var photo: String,
    var selected: Boolean = false,
    var drawable: Drawable


)