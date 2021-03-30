package com.android.code.app.data.local.pref

import android.content.Context
import android.content.SharedPreferences
import com.android.code.app.data.local.pref.SharedPreferencesKeys.Companion.ACCESS_TOKEN
import com.android.code.app.data.local.pref.SharedPreferencesKeys.Companion.PRIVATE_MODE
import com.android.code.app.data.local.pref.SharedPreferencesKeys.Companion.SHAREPRE_NAME
import com.android.code.app.data.local.pref.SharedPreferencesKeys.Companion.USER_PROFILE
import com.google.gson.Gson

/**
 * @AUTHOR Amandeep Singh
 * */
class PrefManager
private constructor() : SharedPreferencesKeys {
    var pref: SharedPreferences? = null

    var accessToken: String
        get() = pref!!.getString(ACCESS_TOKEN, "")!!
        set(accessToken) {
            pref!!.edit().putString(ACCESS_TOKEN, accessToken).apply()
        }


   /* var userProfile: UserDetailObject?
        get() = (if (pref!!.getString(USER_PROFILE, "")!=""){
            Gson().fromJson(pref!!.getString(USER_PROFILE, ""), UserDetailObject::class.java)
        }else{
            null
        })
        set(userProfile) {
            pref!!.edit().putString(USER_PROFILE, Gson().toJson(userProfile)).apply()
        }*/


    /**
     * init shared preference
     * @param context application context
     */

    fun initPref(context: Context): PrefManager {
        pref = context.getSharedPreferences(SHAREPRE_NAME, PRIVATE_MODE)
        return this
    }

    fun clearPrefData() {
        pref!!.edit()/*.remove(USER_PROFILE)*/
                .remove(ACCESS_TOKEN)
                .apply()
    }

    companion object {
        private var instance: PrefManager? = null

        /**
         * Create shared preference class instance
         *
         * @return instance
         */
        fun getInstance(): PrefManager {
            if (instance == null) {
                instance = PrefManager()
            }
            return instance!!
        }
    }

}