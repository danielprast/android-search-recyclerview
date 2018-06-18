package com.danielnimafa.androidsearchrecyclerview

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.bumptech.glide.load.engine.bitmap_recycle.IntegerArrayAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_activity.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    lateinit var contactList: ArrayList<Contact>
    lateinit var contactAdapter: ContactsAdapter
    lateinit var searchView: SearchView
    val URL = "https://api.androidhive.info/json/contacts.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.toolbar_title)
        }

        contactList = ArrayList()
        contactAdapter = ContactsAdapter(this, contactList, itemTap = {
            Toast.makeText(this, "Selected: ${it.name}, phone: ${it.phone}", Toast.LENGTH_LONG).show()
        })

        whiteNotificationBar()

        val mLayoutManager = LinearLayoutManager(this)
        recycler_view.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(MyDividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL, 36))
            adapter = contactAdapter
        }

        fetchContacts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            maxWidth = Int.MAX_VALUE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    contactAdapter.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    contactAdapter.filter.filter(newText)
                    return false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed()
    }

    private fun whiteNotificationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = recycler_view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            recycler_view.systemUiVisibility = flags
            window.statusBarColor = Color.WHITE
        }
    }

    private fun fetchContacts() {
        val request = JsonArrayRequest(URL, Response.Listener<JSONArray> {
            val items: List<Contact> = Gson().fromJson(it.toString(), object : TypeToken<List<Contact>>() {}.type)
            contactList.clear()
            contactList.addAll(items)
            contactAdapter.notifyDataSetChanged()
        }, Response.ErrorListener {
            println(it.printStackTrace())
            Toast.makeText(getApplicationContext(), "Error: " + it.message, Toast.LENGTH_SHORT).show();
        })

        MyApplication.instance.addToRequestQueue(request)
    }

}
