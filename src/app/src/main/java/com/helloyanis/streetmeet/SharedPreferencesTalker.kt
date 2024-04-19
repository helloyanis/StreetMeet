package com.helloyanis.streetmeet

import android.content.Context
import android.widget.Toast

class SharedPreferencesTalker(val context: Context) {
    fun getMessageFromSharedPreferences(): String{
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.sharedPreferencesMessageFileName), Context.MODE_PRIVATE
        )
        sharedPreferences.getString("savedMessage","Hello there") ?: {
            Toast.makeText(context, "no sharedPreferences" , Toast.LENGTH_SHORT).show()
        }

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
}