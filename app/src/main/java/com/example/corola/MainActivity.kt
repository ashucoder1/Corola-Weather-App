package com.example.corola

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var CITY:String="Delhi"
    val API:String="${Your API key}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()
        var editCity:ImageView=findViewById(R.id.editCity)
        var city:EditText=findViewById(R.id.address)
        editCity.setOnClickListener{
            //city.setText(" ")
            var newCity=city.getText().toString()
            CITY=newCity
            weatherTask().execute()
        }

    }
    inner class weatherTask():AsyncTask<String,Void,String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<LottieAnimationView>(R.id.loader).visibility=View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.GONE
            findViewById<TextView>(R.id.errorText).visibility=View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response:String?
            try {
                response= URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$API")
                    .readText(Charsets.UTF_8)
            }
            catch (e: Exception)
            {
                response=null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj=JSONObject(result)
                val main=jsonObj.getJSONObject("main")
                val sys=jsonObj.getJSONObject("sys")
                val wind=jsonObj.getJSONObject("wind")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long=jsonObj.getLong("dt")
                val updatedAtText="Updated At "+SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp=main.getString("temp")
                val tempMin=main.getString("temp_min")
                val tempMax=main.getString("temp_max")
                val pressure=main.getString("pressure")
                val humidity=main.getString("humidity")
                val sunrise:Long=sys.getLong("sunrise")
                val sunset:Long=sys.getLong("sunset")
                val windSpeed=wind.getString("speed")
                val weatherDescription=weather.getString("description")
                val address=jsonObj.getString("name")+ ","+sys.getString("country")

                //populate
                findViewById<EditText>(R.id.address).setText(address)
                findViewById<TextView>(R.id.updated_at).text=updatedAtText
                findViewById<TextView>(R.id.status).text=weatherDescription.capitalize()

                var new =temp.toFloat()
                new=new-273
                var newtemp =String.format("%.2f",new)
                findViewById<TextView>(R.id.temp).text= newtemp.toString()+"°C"

                var newmin=tempMin.toFloat()
                newmin=newmin-273
                var newtempmin=String.format("%.1f",newmin)
                findViewById<TextView>(R.id.temp_min).text="Min Temp: "+newtempmin.toString()+"°C"

                var newmax=tempMax.toFloat()
                newmax=newmax-273
                var newtempmax=String.format("%.1f",newmax)
                findViewById<TextView>(R.id.temp_max).text="Max Temp: "+newtempmax.toString()+"°C"

                findViewById<TextView>(R.id.sunrise).text=SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text=SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text=windSpeed+" km/h"
                findViewById<TextView>(R.id.pressure).text=pressure+" mbar"
                findViewById<TextView>(R.id.humidity).text=humidity


                findViewById<LottieAnimationView>(R.id.loader).visibility=View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility=View.VISIBLE
                Toast.makeText(this@MainActivity,"Weather Updated",Toast.LENGTH_LONG).show()


            }
            catch (e:Exception){
                findViewById<TextView>(R.id.errorText).visibility=View.VISIBLE
                findViewById<LottieAnimationView >(R.id.loader).visibility=View.GONE

            }
        }
    }
}
