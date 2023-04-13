package com.example.sqlitetest.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = myDbHelper.writableDatabase
    }
    fun insertToDb(title: String, content: String, uri: String, time: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    fun updateItem(title: String, content: String, uri: String, id: Int, time: String){
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    fun readDbArrayList(searchText:String): ArrayList<ListItem>{
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(MyDbNameClass.TABLE_NAME,null, selection,
            arrayOf("%$searchText%"),null,null,null)

        while (cursor?.moveToNext()!!)//(this.moveToNext()!=null)
        {
            val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataDesc = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataUri = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
            val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TIME))
            val item = ListItem(dataTitle,dataDesc,dataUri,dataId,dataTime)
            dataList.add(item)
        }
        cursor.close()
        return dataList
    }

    fun closeDb(){
        myDbHelper.close()
    }

}