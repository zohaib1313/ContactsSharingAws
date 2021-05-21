package lads.contancsharing.www.utils

class AppConstant {

    companion object {


        const val STATUS_ACCEPTED = "Accepted"
        const val STATUS_REJECTED = "Rejected"

        const val APP_NAME = "contactSharing"
        const val KEY_DATA = "key_data"
        const val KEY_NOTIFICATION_DATA = "key_notification_data"
        const val KEY_ID = "key_id"
        const val KEY_TYPE = "key_type"
        const val KEY_NOTIFICATION_ORDER = "Order"
        const val LANGUAGE = APP_NAME + "_language"
        const val PREF_NAME = "${APP_NAME}_preference"
        const val USER_INFO = "${APP_NAME}_userInfo"
        const val KEY_IS_LOGGED_IN = "${APP_NAME}_is_logged_in"
        const val IS_VISITED_INTRO = "${APP_NAME}_isVisited"

        const val ANIMATION_DURATION_SEC = 1L
        var FADE_IN_ANIMATION_DURATION = 1000 * ANIMATION_DURATION_SEC
        var TITLE_ANIMATION_DURATION = 1000 * ANIMATION_DURATION_SEC
        const val SPLASH_SLEEP_TIME = 1000 * 3  // 2 seconds

        const val MYSQL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val MYSQL_DATE_FORMAT = "yyyy-MM-dd"
        const val MSSQL_DATE_FORMAT = "yyyy-MM-dd"
        const val APP_DATE_FORMAT = "dd MMM, yyyy"

        const val PUSH_TOKEN = "push_token"
        val NOTIFICATION_ID = 1211


    }


}