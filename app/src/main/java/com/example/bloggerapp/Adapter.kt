package com.example.bloggerapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.Exception
import java.text.SimpleDateFormat

class Adapter(private val postArrayList:ArrayList<ModelPost>, private val context: Context, private val listener:BloggerItemClicked) :
    RecyclerView.Adapter<Adapter.HolderPost>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPost {
        val view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false)
        val viewHolder=HolderPost(view)
        view.setOnClickListener {
            listener.onItemClicked(postArrayList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderPost, position: Int) {
        val model = postArrayList.get(position)
        val authorName: String = model.authorName
        val content: String = model.content
        val id: String = model.id
        val published: String = model.published
        val selfLink: String = model.selfLink
        val title: String = model.title
        val update: String = model.update
        val url: String = model.url

        val document: Document = Jsoup.parse(content)
        try {
            val elements: Elements? = document.select("img")
            val image: String = elements!![0].attr("src")
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.imageView)
        } catch (e: Exception) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_image_24)
        }
        val getDate: String = published
        val dateFormat1: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val dateFormat2: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy K:mm a")
        var formatDate = ""
        try {
            val date = dateFormat1.parse(getDate)
            formatDate = dateFormat2.format(date)

        } catch (e: Exception) {
            formatDate = published
            e.printStackTrace()
        }
        holder.title.text = title
        holder.description.text = document.text()
        holder.published.text = "By  $authorName $formatDate"


    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    class HolderPost(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val published: TextView = itemView.findViewById(R.id.publishInfo)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val description: TextView = itemView.findViewById(R.id.description)
    }
}

interface BloggerItemClicked {
    fun onItemClicked(modelPost: ModelPost) {

    }

}
