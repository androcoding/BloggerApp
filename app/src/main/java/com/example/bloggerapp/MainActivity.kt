package com.example.bloggerapp

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),BloggerItemClicked{
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadMoreBtn: Button
    private var url = ""
    private var nextToken = ""
    private lateinit var postArrayList: ArrayList<ModelPost>
    private lateinit var adapter: Adapter

    companion object {
        const val TAG: String = "MAIN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.posts)
        loadMoreBtn = findViewById(R.id.loadMoreBtn)
        progressBar=findViewById(R.id.progress)


        postArrayList = ArrayList()
        postArrayList.clear()
        progressBar.visibility= View.VISIBLE
        loadPosts()


     loadMoreBtn.setOnClickListener {
         loadPosts()
     }

    }

    private fun loadPosts() {
       progressBar.visibility=View.VISIBLE
        if (nextToken == "") {
            Log.d(TAG, "loadPosts: Next Page token is empty, no more posts")
            url = "https://www.googleapis.com/blogger/v3/blogs/${Constant.BLOG_ID}" +
                    "/posts?maxResults=${Constant.MAX_POST_RESULTS}&key=${Constant.API_KEY}"
        } else if (nextToken == "end") {
            Log.d(TAG, "loadPosts: Next Token is Empty/end, no more posts")
            Toast.makeText(this, "No more posts..", Toast.LENGTH_LONG).show()
            progressBar.visibility=View.GONE
            return
        } else {
            Log.d(TAG, "loadPosts:Next Token:$nextToken")
            url = "https://www.googleapis.com/blogger/v3/blogs/${Constant.BLOG_ID}" +
                    "/posts?maxResults=${Constant.MAX_POST_RESULTS}&pageToken${nextToken}" +
                    "&key=${Constant.API_KEY}"
        }
        Log.d(TAG, "loadPosts: URL :$url")

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                progressBar.visibility=View.GONE
                Log.d(TAG, "onRequests $response")

                try {
                    val jsonObject = JSONObject(response)
                    try {
                        nextToken = jsonObject.getString("nextPageToken")
                        Log.d(TAG, "onResponse: NextPageToken: $nextToken")
                    } catch (e: Exception) {
                        Toast.makeText(this, "Reached end of Pages...", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "onResponse: Reach end of page...${e.message}")
                        nextToken = "end"
                    }
                    val jsonArray = jsonObject.getJSONArray("items")
                    for (i in 0 until jsonArray.length()) {
                        try {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val id = jsonObject1.getString("id")
                            val title = jsonObject1.getString("title")
                            val content = jsonObject1.getString("content")
                            val published = jsonObject1.getString("published")
                            val updated = jsonObject1.getString("updated")
                            val url = jsonObject1.getString("url")
                            val selfLink = jsonObject1.getString("selfLink")
                            val authorName =
                                jsonObject1.getJSONObject("author").getString("displayName")
                            val image = jsonObject1.getJSONObject("author").getString("image")

                            val modelPost = ModelPost(
                                "" + authorName,
                                "" + content,
                                "" + id,
                                "" + published,
                                "" + selfLink,
                                "" + title,
                                "" + updated,
                                "" + url
                            )
                            postArrayList.add(modelPost)
                        } catch (e: Exception) {
                           Log.d(TAG,"onResponse: 1: ${e.message}")
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_LONG).show()

                        }
                    }
                    adapter= Adapter(postArrayList,this,this)
                    recyclerView.adapter=adapter
                    progressBar.visibility=View.GONE
                } catch (e: Exception) {
                    Log.d(TAG,"onResponse: 2: ${e.message}")
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_LONG).show()
                }

            },
            {
                Log.d(TAG,"onErrorResponse: ${it.message}")
                progressBar.visibility=View.GONE

            })

        queue.add(stringRequest)
    }
    override fun onItemClicked(modelPost: ModelPost) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(modelPost.url))
    }
}