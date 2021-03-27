package com.hasan.firebasepushnotification.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasan.firebasepushnotification.R
import com.hasan.firebasepushnotification.model.User
import kotlinx.android.synthetic.main.user_list_layout.view.*

class UserAdapter(
    private val userList: ArrayList<User>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        holder.nameTV.text = user.name
        holder.userEmailTV.text = user.name

        holder.notificationBtn.setOnClickListener {
            listener.onItemClick(user.token)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /*fun bindItems(user: User) {
            val nameTV = itemView.usernameTV
            val userEmailTV = itemView.userEmailTV
            val notificationBtn = itemView.imageView

           nameTV.text = user.name
            userEmailTV.text = user.email



        }*/

        val nameTV: TextView = itemView.usernameTV
        val userEmailTV: TextView = itemView.userEmailTV
        val notificationBtn: ImageView = itemView.imageView


    }

    interface OnItemClickListener {
        fun onItemClick(token: String)
    }

}