package com.gavin.easypharm.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import com.gavin.easypharm.R
import com.gavin.easypharm.firestore.FirestoreClass
import com.gavin.easypharm.models.User
import com.gavin.easypharm.utils.Button
import com.gavin.easypharm.utils.TextViewBold
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

        val tvLoginId = findViewById<TextViewBold>(R.id.tv_login)
        tvLoginId.setOnClickListener{
            //Launch login screen when user clicks on login text
//            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
              onBackPressed()
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener{
            registerUser()
//            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//            startActivity(intent)
        }
    }
    private fun setupActionBar(){
        val toolbarRegisterActivity = findViewById<Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbarRegisterActivity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_back_arrow)
        }
        toolbarRegisterActivity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun validateRegisterDetails(): Boolean {
        val firstName = findViewById<EditText>(R.id.et_first_name)
        val lastName = findViewById<EditText>(R.id.et_last_name)
        val email = findViewById<EditText>(R.id.et_email)
        val password = findViewById<EditText>(R.id.et_password)
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val tnc = findViewById<AppCompatCheckBox>(R.id.cb_terms_and_condition)

        return when {
            TextUtils.isEmpty(firstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(lastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(confirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            password.text.toString().trim { it <= ' ' } != confirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !tnc.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
//                showErrorSnackBar("Thanks for Registering...", false)
                true
            }
        }
    }

    private fun registerUser() {

        val etFirstName = findViewById<EditText>(R.id.et_first_name)
        val etLastName = findViewById<EditText>(R.id.et_last_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            // Show the progress dialog.
            showProgressDialog("Please wait...")

            val email: String = etEmail.text.toString().trim { it <= ' ' }
            val password: String = etPassword.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!


                            // Instance of User data model class.
                            val user = User(
                                firebaseUser.uid,
                                etFirstName.text.toString().trim { it <= ' ' },
                                etLastName.text.toString().trim { it <= ' ' },
                                etEmail.text.toString().trim { it <= ' ' }
                            )

                            // Pass the required values in the constructor.
                            FirestoreClass().registerUser(this@RegisterActivity, user)


                        } else {

                            // Hide the progress dialog
                            hideProgressDialog()

                            // If the registering is not successful then show error message.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    fun userRegistrationSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()


        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
        finish()
    }
}