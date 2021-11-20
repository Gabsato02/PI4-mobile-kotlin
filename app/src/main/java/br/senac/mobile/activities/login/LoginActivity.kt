package br.senac.mobile.activities.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import br.senac.mobile.R
import br.senac.mobile.activities.main.MainActivity
import br.senac.mobile.activities.register.RegisterActivity
import br.senac.mobile.databinding.ActivityLoginBinding
import br.senac.mobile.models.CustomResponse
import br.senac.mobile.models.Login
import br.senac.mobile.models.User
import br.senac.mobile.services.API
import br.senac.mobile.services.LOGIN_FILE
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.loginButton.setOnClickListener {
            checkCredentials()
        }

        binding.textNewUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        requestSavedLogin()
    }

    private fun requestSavedLogin() {
        val preferences = getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE)
        val savedLogin = Login(
            email = preferences.getString("user", "") as String,
            password = preferences.getString("password", "") as String
        )
        val saved = true

        requestLogin(savedLogin, saved)
    }

    private fun setPreferences(login: Login, userId: String) {
        val preferences = getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE).edit()
        preferences.putString("user", login.email)
        preferences.putString("password", login.password)
        preferences.putString("userId", userId)
        preferences.apply()
    }

    private fun requestLogin(login: Login, saved: Boolean) {
        val callback = object: Callback<CustomResponse> {
            override fun onResponse(call: Call<CustomResponse>, response: Response<CustomResponse>) {
                binding.loginProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    setPreferences(login, response.body()?.message.toString())
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.textErrorMessage.text = "Credenciais inválidas"
                    if (!saved) localSnackbar("Não foi possível realizar o login")
                    Log.e("ERROR", "Falha ao executar serviço")
                }
            }
            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                binding.loginProgressBar.visibility = View.GONE
                localSnackbar("Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().user.postLogin(login).enqueue(callback)
        binding.loginProgressBar.visibility = View.VISIBLE
    }

    private fun checkCredentials() {
        val email = binding.textInputName
        val password = binding.textInputPassword
        val saved = false

        when {
            email.text.isNullOrEmpty() || !email.text.toString().contains("@") || !email.text.toString().contains(".com") -> {
                localSnackbar("Por favor, digite um e-mail válido.")
                email.requestFocus()
            }
            password.text.isNullOrEmpty() || password.text.toString().length < 8 -> {
                localSnackbar("Por favor, digite uma senha válida")
                password.requestFocus()
            }
            else -> {
                var login = Login(
                    email = email.text.toString(),
                    password = password.text.toString()
                )
                requestLogin(login, saved)
            }
        }
    }

    private fun localSnackbar(message: String) {
        Snackbar.make(
            this.findViewById(R.id.loginConstraintLayout),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}