package com.hasan.firebasepushnotification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.hasan.firebasepushnotification.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: User
    private lateinit var database: FirebaseDatabase
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user")
        user = User()



        signUpBtn.setOnClickListener {

            val name = nameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()


            /*FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                FirebaseService.token = it.token
                //etToken.setText(it.token)
                userToken = it.token
                Log.d(TAG, "token: ${it.token}")
            }
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)*/


            Firebase.messaging.subscribeToTopic(TOPIC)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        Log.d(TAG, "Topic Created")
                    }else{
                        Log.d(TAG, "Topic Not Created")
                    }
                }


            Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                userToken = task.result.toString()

                Log.d(TAG, "Token: $userToken")

                // Log and toast


            })






            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Auth: started")
                        val firebaseUser = mAuth.currentUser
                        user.name = name
                        user.email = email
                        user.id = firebaseUser.uid
                        user.password = password
                        user.token = userToken


                        userRef.child(firebaseUser.uid).setValue(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "DB: started")
                                    Toast.makeText(this@SignUpActivity, "Successful", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                }
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@SignUpActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    companion object {
        private const val TAG = "SignUpActivity"
    }
}