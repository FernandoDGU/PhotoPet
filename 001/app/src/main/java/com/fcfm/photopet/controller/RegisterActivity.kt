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
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R
import com.fcfm.photopet.model.User
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.LoadingDialogActivity
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
    private lateinit var loading : LoadingDialogActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.hide()
         destiny = intent.getStringExtra("destiny");
        if(destiny == "modify"){
            val actualUser = loggedUser.getUser()
            editTextName.setText(actualUser.firstname)
            editTextLast.setText(actualUser.lastname)

            editTextEmailRegister.setText(actualUser.email)
            editTextEmailRegister.isEnabled = false

            editTextDescRegister.setText(actualUser.description)
            editTextPhoneRegister.setText(actualUser.phone)

            var byteArray:ByteArray? = null
            val strImage:String =  actualUser.image!!.replace("data:image/png;base64,","")
            byteArray =  Base64.getDecoder().decode(strImage)
            RegisterImage!!.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))

            buttonRegister.visibility = View.GONE
            buttonModify.visibility = View.VISIBLE
            buttonModify.setOnClickListener(this)
        }else if(destiny == "register"){
            buttonModify.visibility = View.GONE
            buttonRegister.visibility = View.VISIBLE
            buttonRegister.setOnClickListener(this)
        }


        loading = LoadingDialogActivity(this)
        RegisterImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
            if(v!= null){
                when(v!!.id){
                    R.id.buttonRegister ->{
                        loading.startLoading()
                        if(validate()){
                            createUser()
                        }else{
                            loading.isDismiss()
                            Toast.makeText(this, R.string.ErrData, Toast.LENGTH_SHORT).show()
                        }

                    }
                    R.id.buttonModify ->{
                        loading.startLoading()
                        if(validate()){
                            modifyUser()
                        }else{
                            loading.isDismiss()
                            Toast.makeText(this, R.string.ErrData, Toast.LENGTH_SHORT).show()
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
        var pass = editTextPasslRegister.text.toString()
        if(pass.isNullOrEmpty()) pass = loggedUser.getUser().password!!

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
    }

    private fun validateName():Boolean {
        if(editTextName.text.toString().isEmpty()){
            NameError.text = getString(R.string.ErrEmpty_L)
            return false
        }
        NameError.text = ""
        return true
    }

    private fun validateLast():Boolean{
        if(editTextLast.text.toString().isEmpty()){
            LastError.text = getString(R.string.ErrEmpty_L)
            return false
        }
        LastError.text = ""
        return true
    }

    private fun validateEmail():Boolean{
        if(editTextEmailRegister.text.toString().isEmpty()){
            EmailError.text = getString(R.string.ErrEmpty_L)
            return false
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmailRegister.text).matches()){
            EmailError.text = getString(R.string.ErrEmail)
            return false
        }
        EmailError.text = ""
        return true
    }


    fun isValidPassword(password: String?) : Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    private fun validatePass():Boolean{
        if(editTextPasslRegister.text.toString().isEmpty() || destiny == "register"){
            if(editTextPasslRegister.text.toString().isEmpty()){
                PassError.text = getString(R.string.ErrEmpty_L)
                return false
            }else if (!isValidPassword(editTextPasslRegister.text.toString())){
                PassError.text = getString(R.string.ErrCharacter)
                return false
            }else if(editTextPasslRegister.text.toString().length < 8){
                PassError.text = getString(R.string.ErrPass)
                return false
            }
        }

        PassError.text = ""
        return true
    }

    private fun validatePhone():Boolean{
        if(editTextPhoneRegister.text.toString().length < 10 && !editTextPhoneRegister.text.isEmpty()){
            PhoneError.text = getString(R.string.ErrPhone)
            return false
        }else if(editTextPhoneRegister.text.toString().length > 10){
            PhoneError.text = getString(R.string.ErrPhone)
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


}