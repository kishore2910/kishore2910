package com.powersoaps.distributorsales.ui.utils

object Session {

    var userID: String = ""
    var userToken: String = ""
    var searchingText :String = ""
    var shopId : Int = 0
    var coverOutfit : Int = 0
    var shopIdPending : Int = 0
    var lastIndexHome : Int = 0
    var userName: String? = null
    var userCode: String? = null
    var userDepartmentToken: String? = null
    var userGender: String? = null
    var userMobile: String? = null
    var userRegion: String? = null
    var userDepName: String? = null
    var filename: String? = null
    var fileextension: String? = null
    var fireBaseToken : String = ""
    var appOpen: Boolean = false
    var bottomScreen: Boolean = false
    var messageScreen: Boolean = false
    const val NotificationHandle = "Notification"
    var isAppInForeground = false
}