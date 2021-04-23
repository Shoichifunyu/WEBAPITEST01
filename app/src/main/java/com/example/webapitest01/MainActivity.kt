package com.example.samplehttpconnection

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.samplehttpconnection.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler()
    private var button: Button? = null
    private var button2: Button? = null
    private var textView: TextView? = null
    private var textView2: TextView? = null
    private val urlIpText = "http://httpbin.org/ip"
    private val urlPostText = "http://httpbin.org/post"
    private var ip = ""
    private var nameAndType = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //button = findViewById(R.id.btn)
        //button2 = findViewById(R.id.btn2)
        //textView = findViewById(R.id.txtView)
        //textView2 = findViewById(R.id.txtView2)
        binding.btn.setOnClickListener(View.OnClickListener {
            val thread = Thread {
                var response = ""
                try {
                    response = aPI
                    val rootJSON = JSONObject(response)
                    ip = rootJSON.getString("origin")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                handler.post { binding.txtView.setText(ip) }
            }
            thread.start()
        })
        binding.btn2.setOnClickListener(View.OnClickListener {
            val thread = Thread {
                var response = ""
                try {
                    response = postAPI()
                    val rootJSON = JSONObject(response)
                    val formJSON = rootJSON.getJSONObject("form")
                    nameAndType = formJSON.getString("name") + "/" + formJSON.getString("type")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                handler.post { binding.txtView2.setText(nameAndType) }
            }
            thread.start()
        })
    }

    val aPI: String
        get() {
            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            var result = ""
            var str = ""
            try {
                val url = URL(urlIpText)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 10000
                urlConnection!!.readTimeout = 10000
                urlConnection.addRequestProperty("User-Agent", "Android")
                urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString())
                urlConnection.requestMethod = "GET"
                urlConnection.doInput = true
                urlConnection.doOutput = false
                urlConnection.connect()
                val statusCode = urlConnection.responseCode
                if (statusCode == 200) {
                    inputStream = urlConnection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
                    result = bufferedReader.readLine()
                    while (result != null) {
                        str += result
                        result = bufferedReader.readLine()
                    }
                    bufferedReader.close()
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return str
        }

    fun postAPI(): String {
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var result = ""
        var str = ""
        try {
            val url = URL(urlPostText)
            urlConnection = url.openConnection() as HttpURLConnection
            val postData = "name=foge&type=fogefoge"
            urlConnection.connectTimeout = 10000
            urlConnection!!.readTimeout = 10000
            urlConnection.addRequestProperty("User-Agent", "Android")
            urlConnection.addRequestProperty("Accept-Language", Locale.getDefault().toString())
            urlConnection.requestMethod = "POST"
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.connect()
            outputStream = urlConnection.outputStream
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
            bufferedWriter.write(postData)
            bufferedWriter.flush()
            bufferedWriter.close()
            val statusCode = urlConnection.responseCode
            if (statusCode == 200) {
                inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                result = bufferedReader.readLine()
                while (result != null) {
                    str += result
                    result = bufferedReader.readLine()
                }
                bufferedReader.close()
            }
            urlConnection.disconnect()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return str
    }
}