package com.example.coin_converter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.coin_converter.api.Endpoint
import com.example.coin_converter.databinding.ActivityMainBinding
import com.example.coin_converter.utils.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        getCurrencies()


        viewBinding.btConvert.setOnClickListener { validNum() }

    }

    private fun validNum() {
        val valueFrom = viewBinding.etValueFrom.text.toString()

        if (valueFrom != "") {

            convertMoney()

        } else {

            Toast.makeText(this, R.string.validation_value, Toast.LENGTH_SHORT).show()

        }
    } // Checa se o campo "valor" esta preenchido.


    private fun convertMoney() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getCurrencyRate(
            viewBinding.spFrom.selectedItem.toString(),
            viewBinding.spTo.selectedItem.toString()
        ).enqueue(object :
            retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()
                    ?.find { it.key == viewBinding.spTo.selectedItem.toString() }
                val rate: Double = data?.value.toString().toDouble()
                val conversion = viewBinding.etValueFrom.text.toString().toDouble() * rate

                viewBinding.tvResult.setText(conversion.toString())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("fail")
            }

        })
    } // Chama o metodo de conversão

    private fun getCurrencies() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endPoint = retrofitClient.create(Endpoint::class.java)

        endPoint.getCurrencies().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }


                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")

                val adapter =
                    ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)

                viewBinding.spFrom.adapter = adapter
                viewBinding.spTo.adapter = adapter

                viewBinding.spFrom.setSelection(posBRL)
                viewBinding.spTo.setSelection(posUSD)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("fail")

            }

        })
    } // Chama as informações das moedas para os spinners.

}