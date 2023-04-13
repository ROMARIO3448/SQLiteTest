package com.example.sqlitetest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlitetest.databinding.EditActivityBinding
import com.example.sqlitetest.db.MyDbManager
import com.example.sqlitetest.db.MyIntentConstants
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    private var launcher: ActivityResultLauncher<Intent>? = null
    val myDbManager = MyDbManager(this)
    var id = 0
    var isEditState = false

    private lateinit var binding: EditActivityBinding
    var tempImageUri = "empty"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyIntents()

        //launcher start
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->

            if(result.resultCode == RESULT_OK){
                binding.imMainImage.setImageURI(result.data?.data)
                tempImageUri = result.data?.data.toString()
                //код ниже обеспечивает доступ к uri, то есть файлу изображения даже после
                // перезапуска приложения
                contentResolver.takePersistableUriPermission(result.data?.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        }
        //launcher end
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    fun onClickAddImage(view: View) {
        binding.mainImageLayout.visibility = View.VISIBLE
        binding.fbAddImage.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {
        binding.mainImageLayout.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: View) {
        //val intent = Intent(Intent.ACTION_PICK)//временная ссылка на картинку
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        //intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION//вылетала ошибка, исправил в onCreate
        launcher?.launch(intent)
    }

    fun onClickSave(view: View) {
        val myTitle = binding.editTitle.text.toString()
        val myDesc = binding.editDesc.text.toString()
        if(isEditState==true){
            myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
        }
        else{
            myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime())
        }
        finish()
    }

    private fun getMyIntents(){
        binding.fbEdit.visibility = View.GONE
        val i = intent
        if(i!=null) {
            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null)
            {
                binding.fbAddImage.visibility = View.GONE
                binding.editTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                binding.editDesc.setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))
                binding.editTitle.isEnabled = false
                binding.editDesc.isEnabled = false
                binding.fbEdit.visibility = View.VISIBLE
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                isEditState = true
                if(i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty")
                {
                    binding.mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    binding.imMainImage.setImageURI(Uri.parse(tempImageUri))
                    binding.imButtonDeleteImage.visibility = View.GONE
                    binding.imButtonEditImage.visibility = View.GONE
                }
            }
        }
    }

    fun onClickEditEnabled(view: View) {
        binding.editTitle.isEnabled = true
        binding.editDesc.isEnabled = true
        binding.fbEdit.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        if(tempImageUri=="empty")return
        binding.imButtonEditImage.visibility = View.VISIBLE
        binding.imButtonDeleteImage.visibility = View.VISIBLE
    }

    private fun getCurrentTime():String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }


}