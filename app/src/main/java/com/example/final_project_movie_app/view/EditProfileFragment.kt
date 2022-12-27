package com.example.final_project_movie_app.view

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.listenercallback.BackListener
import com.example.final_project_movie_app.listenercallback.MovieListener
import com.example.final_project_movie_app.listenercallback.ProfileListener
import de.hdodenhof.circleimageview.CircleImageView

@Suppress("DEPRECATION")
class EditProfileFragment : Fragment() {

    private lateinit var mImgAvatar : CircleImageView
    private lateinit var mNameText : EditText
    private lateinit var mEmailText : EditText
    private lateinit var mDobText : EditText
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mMaleRadioButton: RadioButton
    private lateinit var mFemaleRadioButton: RadioButton
    private lateinit var mSaveButton: Button

    private lateinit var mGender: String
    private var mBitmapProfile: Bitmap? = null

    private lateinit var mProfileListener: ProfileListener
    private lateinit var mMovieListener: MovieListener
    private lateinit var mBackListener: BackListener

    private val REQUEST_IMG_PICTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        mImgAvatar = view.findViewById(R.id.img_avatar_edit)
        mNameText = view.findViewById(R.id.et_name)
        mEmailText = view.findViewById(R.id.et_email)
        mDobText = view.findViewById(R.id.et_dob)
        mRadioGroup = view.findViewById(R.id.radio_group)
        mMaleRadioButton = view.findViewById(R.id.rbtn_male)
        mFemaleRadioButton = view.findViewById(R.id.rbtn_female)
        mSaveButton = view.findViewById(R.id.btn_save_profile)
        onClickChanged()

        val bundle = arguments
        if (bundle!=null){
            var nameBundle = bundle.getString("name")
            var emailBundle = bundle.getString("email")
            var dobBundle = bundle.getString("dob")
            if (nameBundle == "No data") nameBundle = ""
            if (emailBundle =="No data") emailBundle = ""
            if (dobBundle == "No data") dobBundle = ""
            mGender =""
            mNameText.setText(nameBundle,TextView.BufferType.EDITABLE)
            mEmailText.setText(emailBundle,TextView.BufferType.EDITABLE)
            mDobText.setText(dobBundle,TextView.BufferType.EDITABLE)
        }
        mSaveButton.setOnClickListener{
            val name = mNameText.text.toString()
            val email = mEmailText.text.toString()
            val dob = mDobText.text.toString()
            if(name==""||email==""||dob==""||mGender==""){
                Toast.makeText(context,"Fill all information!",Toast.LENGTH_SHORT).show()
            }else{
                mProfileListener.onSaveProfile(name,email,dob,mGender,mBitmapProfile)
                mBackListener.backToHome()
            }
        }
        mImgAvatar.setOnClickListener{
            dispatchTakePictureIntent()
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMG_PICTURE && resultCode == RESULT_OK){
            val imgbitmap = data!!.extras!!.get("data") as Bitmap
            this.mBitmapProfile = imgbitmap
            mImgAvatar.setImageBitmap(imgbitmap)
        }
    }

    private fun onClickChanged(){
        mRadioGroup.setOnCheckedChangeListener{_,i->
            if(i == R.id.rbtn_male){
                mGender = "Male"
            }else if(i==R.id.rbtn_female){
                mGender = "Female"
            }
        }
    }

    fun setProfileListener(profileListener: ProfileListener) {
        this.mProfileListener = profileListener
    }
    fun setMovieListener(movieListener: MovieListener) {
        this.mMovieListener = movieListener
    }
    fun setBackListener(backListener: BackListener) {
        this.mBackListener = backListener
    }
    private fun dispatchTakePictureIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent,REQUEST_IMG_PICTURE)
        }catch (e:ActivityNotFoundException){
        }
    }

    override fun onPause() {
        super.onPause()
        mMovieListener.onUpdateTitleFromDetail()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}
