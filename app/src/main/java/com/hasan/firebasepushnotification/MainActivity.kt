package com.hasan.firebasepushnotification


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hasan.firebasepushnotification.adapters.UserAdapter
import com.hasan.firebasepushnotification.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC = "/topics/myTopic2"

class MainActivity : AppCompatActivity(), UserAdapter.OnItemClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val userRef = database.child("user")

        recyclerView.layoutManager = LinearLayoutManager(this)

        //crating an arraylist to store users using the data class user
        val users = ArrayList<User>()
        //creating our adapter
        val adapter = UserAdapter(users, this)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    users.clear()
                    for (data in snapshot.children) {
                        val user = data.getValue(User::class.java)
                        users.add(user!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })


        //now adding the adapter to recyclerview
        recyclerView.adapter = adapter

        /* FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
         FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
             FirebaseService.token = it.token
             //etToken.setText(it.token)
             Log.d(TAG, "token: ${it.token}")
         }
         FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)*/

        /* btnSend.setOnClickListener {
             val title = etTitle.text.toString()
             val message = etMessage.text.toString()
             val recipientToken = etToken.text.toString()
             if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                 PushNotification(NotificationData(title, message), recipientToken).also {
                     sendNotification(it)
                 }
             }
         }*/


        //adding some dummy data to the list
        /* users.add(User("Belal Khan", "Ranchi Jharkhand"))
         users.add(User("Ramiz Khan", "Ranchi Jharkhand"))
         users.add(User("Faiz Khan", "Ranchi Jharkhand"))
         users.add(User("Yashar Khan", "Ranchi Jharkhand"))*/



        signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }




    }



    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        if (currentUser == null){
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

    }


    override fun onItemClick(token: String) {
        Log.d(TAG, "onItemClick: $token")

        val title = "Hasan"
        val message = "etMessage.text.toString()"
        //val recipientToken = token
        if (title.isNotEmpty() && message.isNotEmpty() && token.isNotEmpty()) {
            PushNotification(NotificationData(title, message), token).also {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
}
