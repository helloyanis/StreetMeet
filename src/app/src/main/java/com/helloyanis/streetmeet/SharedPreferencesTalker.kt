package com.helloyanis.streetmeet

import android.content.Context
import android.widget.Toast

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

        Toast.makeText(context, "new Message saved" , Toast.LENGTH_SHORT).show()
    }

    fun getNameFromSharedPreferences(): String{
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )

        return sharedPreferences.getString("savedName","Sans") ?: "Hello Sans"
    }

    fun setNameInSharedPreferences( newMessage: String){
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )
        with(sharedPreferences.edit()){
            putString("savedName",newMessage)
            apply()
        }

        Toast.makeText(context, "new name saved" , Toast.LENGTH_SHORT).show()
    }
}