package com.fcfm.photopet.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.User
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.LoadingDialog
import com.fcfm.photopet.utils.UserApplication.Companion.prefs
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.RetrofitMessage
import com.fcfm.photopet.utils.retrofit.ServiceUser
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterActivity: AppCompatActivity(), View.OnClickListener {

    var imgArray:ByteArray? =  null
    var destiny: String? = null
    var choosedPhoto: Boolean = false
    private val REQUEST_GALLERY = 1001
    private lateinit var loading : LoadingDialog.ActivityLD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.hide()
        loading = LoadingDialog().ActivityLD(this)
         destiny = intent.getStringExtra("destiny");
        if(destiny == "modify"){
            loading.startLoading()
            val actualUser = loggedUser.getUser()
            editTextName.setText(actualUser.firstname)
            editTextLast.setText(actualUser.lastname)

            editTextEmailRegister.setText(actualUser.email)
            editTextEmailRegister.isEnabled = false

            editTextDescRegister.setText(actualUser.description)
            editTextPhoneRegister.setText(actualUser.phone)

            var byteArray: ByteArray?
            val strImage:String =  actualUser.image!!.replace("data:image/png;base64,","")
            byteArray =  Base64.getDecoder().decode(strImage)
            RegisterImage!!.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))

            buttonRegister.visibility = View.GONE
            buttonModify.visibility = View.VISIBLE
            checkPass.visibility = View.VISIBLE

            buttonDelete.visibility = View.VISIBLE
            buttonModify.setOnClickListener(this)
            buttonDelete.setOnClickListener(this)
            loading.isDismiss()
        }else if(destiny == "register"){
            buttonModify.visibility = View.GONE
            buttonRegister.visibility = View.VISIBLE
            buttonRegister.setOnClickListener(this)
        }



        RegisterImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
            if(v!= null){
                when(v!!.id){
                    R.id.buttonRegister ->{
                        loading.startLoading()
                        if(RestEngine.hasInternetConnection(this)){
                            if(validate()){
                                createUser()
                            }else{
                                loading.isDismiss()

                                //Toast.makeText(this, R.string.ErrData, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            loading.isDismiss()
                            Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                        }
                        

                    }
                    R.id.buttonModify ->{
                        loading.startLoading()
                        if(RestEngine.hasInternetConnection(this)){
                            if(validate()){
                                modifyUser()
                            }else{
                                loading.isDismiss()

                            //Toast.makeText(this, R.string.ErrData, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            loading.isDismiss()
                            Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                        }


                    }
                    R.id.buttonDelete -> {
                        if(RestEngine.hasInternetConnection(this)){
                            deleteDialog()
                        }
                        else{
                            Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                        }
                    }
                    R.id.RegisterImage ->{
                        loadImage()
                    }
                }
            }
    }

    private fun createUser(){
        if(this.imgArray == null){
            val bitmap = (RegisterImage!!.getDrawable() as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            this.imgArray = baos.toByteArray()
        }
        val encodedString: String =  Base64.getEncoder().encodeToString(this.imgArray)
        val strEncodeImage: String = "data:image/png;base64," + encodedString


        val user = User(
            editTextEmailRegister.text.toString(),
            "${editTextName.text} ${editTextLast.text}",
            editTextName.text.toString(),
            editTextLast.text.toString(),
            editTextPasslRegister.text.toString(),
            editTextPhoneRegister.text.toString(),
            editTextDescRegister.text.toString(),
            strEncodeImage
        );

        val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
        val result: Call<RetrofitMessage> = service.insertUser(user)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(this@RegisterActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        loggedUser.setUser(user)
                        prefs.saveEmail(loggedUser.getUser().email!!)
                        prefs.savePost(Publication())
                        showHome()
                    }
                    "23000" -> {
                        loading.isDismiss()
                        Toast.makeText(this@RegisterActivity,R.string.Err23000_R,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }

    private fun modifyUser(){
        var pass:String = ""
        if(!checkPass.isChecked){
            pass = loggedUser.getUser().password!!
        }else{
            pass = editTextPasslRegister.text.toString()
        }

        var strEncodeImage = loggedUser.getUser().image
        if(choosedPhoto){
        val bitmap = (RegisterImage!!.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        this.imgArray = baos.toByteArray()
        val encodedString: String =  Base64.getEncoder().encodeToString(this.imgArray)
        strEncodeImage = "data:image/png;base64," + encodedString
        }

        val user = User(
            editTextEmailRegister.text.toString(),
            "${editTextName.text} ${editTextLast.text}",
            editTextName.text.toString(),
            editTextLast.text.toString(),
            pass,
            editTextPhoneRegister.text.toString(),
            editTextDescRegister.text.toString(),
            strEncodeImage
        );

        val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
        val result: Call<RetrofitMessage> = service.updateUser(user.email!!, user)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(this@RegisterActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        loggedUser.setUser(user)

                        loading.isDismiss()
                        finish()
                    }
                    "0" -> {
                        loading.isDismiss()
                        Toast.makeText(this@RegisterActivity,R.string.Err0_G,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }

    private fun showHome(){
        val intent = Intent(this, FragmentsActivity::class.java)
        loading.isDismiss()
        startActivity(intent)
        finish()
    }

    private fun validateName():Boolean {
        if(editTextName.text.toString().isEmpty()){
            NameError.text = getString(R.string.ErrEmpty_L)
            Toast.makeText(this, R.string.ErrEmpty_L, Toast.LENGTH_SHORT).show()
            editTextName.requestFocus()
            return false
        }
        NameError.text = ""
        return true
    }

    private fun validateLast():Boolean{
        if(editTextLast.text.toString().isEmpty()){
            LastError.text = getString(R.string.ErrEmpty_L)
            Toast.makeText(this, R.string.ErrEmpty_L, Toast.LENGTH_SHORT).show()
            editTextLast.requestFocus()
            return false
        }
        LastError.text = ""
        return true
    }

    private fun validateEmail():Boolean{
        if(editTextEmailRegister.text.toString().isEmpty()){
            EmailError.text = getString(R.string.ErrEmpty_L)
            Toast.makeText(this, R.string.ErrEmpty_L, Toast.LENGTH_SHORT).show()
            editTextEmailRegister.requestFocus()
            return false
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmailRegister.text).matches()){
            EmailError.text = getString(R.string.ErrEmail)
            Toast.makeText(this, R.string.ErrEmail, Toast.LENGTH_SHORT).show()
            editTextEmailRegister.requestFocus()
            return false
        }
        EmailError.text = ""
        return true
    }


    fun isValidPassword(password: String?) : Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    private fun validatePass():Boolean{
            if(checkPass.isChecked){
            if(editTextPasslRegister.text.toString().isEmpty()){
                //PassError.text = getString(R.string.ErrEmpty_L)
                Toast.makeText(this, R.string.ErrEmpty_L, Toast.LENGTH_SHORT).show()
                editTextPasslRegister.requestFocus()
                return false
            }else if (!isValidPassword(editTextPasslRegister.text.toString())){
                PassError.text = getString(R.string.ErrCharacter)
                editTextPasslRegister.requestFocus()
                return false
            }else if(editTextPasslRegister.text.toString().length < 8){
                //PassError.text = getString(R.string.ErrPass)
                Toast.makeText(this, R.string.ErrPass, Toast.LENGTH_SHORT).show()
                editTextPasslRegister.requestFocus()
                return false
            }
            }
        PassError.text = ""
        return true
    }

    private fun validatePhone():Boolean{
        if(editTextPhoneRegister.text.toString().length < 10 && !editTextPhoneRegister.text.isEmpty()){
            PhoneError.text = getString(R.string.ErrPhone)
            Toast.makeText(this, R.string.ErrPhone, Toast.LENGTH_SHORT).show()
            editTextPhoneRegister.requestFocus()
            return false
        }else if(editTextPhoneRegister.text.toString().length > 10){
            PhoneError.text = getString(R.string.ErrPhone)
            Toast.makeText(this, R.string.ErrPhone, Toast.LENGTH_SHORT).show()
            editTextPhoneRegister.requestFocus()
            return false
        }
        PhoneError.text = ""
        return true
    }

    private fun validate():Boolean{
        if(!validateName() || !validateLast() || !validateEmail() || !validatePass() || !validatePhone()){
            return false
        }

        return true
    }


    private fun loadImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY){
            //val photo = data?.extras?.get("data") as Bitmap
            val photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data!!.data)
            val stream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            imgArray =  stream.toByteArray()
            this.RegisterImage!!.setImageBitmap(photo)
            if(destiny == "modify") choosedPhoto = true
            //Photo.setImageURI(data!!.data)

        }
    }

    private fun deleteDialog() {

        val simpleDialog =  AlertDialog.Builder(this)
            .setTitle(getString(R.string.deleteAccount))
            .setMessage(getString(R.string.dialogDeleteAccountMessage))
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton(getString(R.string.dialogYes)){ _,_ ->
                prefs.saveEmail("")
                prefs.savePost(Publication())
                deleteUser()

            }
            .setNegativeButton(getString(R.string.dialogNo)){_,_->


            }.create()

        simpleDialog.show()
    }

    private fun deleteUser(){
        loading.startLoading()
        val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
        val result: Call<RetrofitMessage> = service.deleteUser(loggedUser.getUser().email!!)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(this@RegisterActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        loading.isDismiss()
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else -> {
                        loading.isDismiss()
                        Toast.makeText(this@RegisterActivity,item.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }


}