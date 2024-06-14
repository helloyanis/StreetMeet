package com.helloyanis.streetmeet.utils

import android.content.Context
import com.helloyanis.streetmeet.R

class SharedPreferencesTalker(private val context: Context) {
    fun getMessageFromSharedPreferences(): String{
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )

        return sharedPreferences.getString("savedMessage","Hello there") ?: "Hello there"
    }

    fun setMessageInSharedPreferences( newMessage: String){
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )
        with(sharedPreferences.edit()){
            putString("savedMessage",newMessage)
            apply()
        }
    }

    fun getNameFromSharedPreferences(): String{
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )

        return sharedPreferences.getString("savedName","Anonyme") ?: "Hello!"
    }

    fun setNameInSharedPreferences( newMessage: String){
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )
        with(sharedPreferences.edit()){
            putString("savedName",newMessage)
            apply()
        }
    }
}