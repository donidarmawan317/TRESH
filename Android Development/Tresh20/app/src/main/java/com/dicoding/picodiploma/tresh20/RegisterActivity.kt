package com.dicoding.picodiploma.tresh20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dicoding.picodiploma.tresh20.databinding.ActivityRegisterBinding
import com.dicoding.picodiploma.tresh20.response.RegistResp
import com.dicoding.picodiploma.tresh20.service.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var activityRegisterBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(activityRegisterBinding.root)

        supportActionBar?.hide()

        activityRegisterBinding.etName.type = "name"
        activityRegisterBinding.etEmail.type = "email"
        activityRegisterBinding.etPass.type = "password"
        activityRegisterBinding.etConfpass.type = "confPassword"

        activityRegisterBinding.btnRegister.setOnClickListener {
            val inputName = activityRegisterBinding.etName.text.toString()
            val inputEmail = activityRegisterBinding.etEmail.text.toString()
            val inputPassword = activityRegisterBinding.etPass.text.toString()
            val inputconfPassword = activityRegisterBinding.etConfpass.text.toString()

            createAccount(inputName, inputEmail, inputPassword , inputconfPassword)

        }
        activityRegisterBinding.etMoveLogin.setOnClickListener {
            val intent = Intent(this , LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val logoutMenu = menu.findItem(R.id.menu_logout)

        logoutMenu.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun createAccount(inputName: String, inputEmail: String, inputPassword: String , inputconfPass : String) {
        showLoading(true)

        val client = ApiConfig.getApiService().register(inputName, inputEmail, inputPassword , inputconfPass)
        client.enqueue(object: Callback<RegistResp> {
            override fun onResponse(
                call: Call<RegistResp>,
                response: Response<RegistResp>
            ) {
                showLoading(false)
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if(response.isSuccessful && responseBody?.msg  == "register successful") {
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "onFailure1: ${response.message()}")
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegistResp>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure2: ${t.message}")
                Toast.makeText(this@RegisterActivity, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activityRegisterBinding.progressBar.visibility = View.VISIBLE
        } else {
            activityRegisterBinding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "Register Activity"
    }
}