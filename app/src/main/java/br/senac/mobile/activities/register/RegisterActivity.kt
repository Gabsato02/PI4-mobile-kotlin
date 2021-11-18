package br.senac.mobile.activities.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import br.senac.mobile.R
import br.senac.mobile.activities.login.LoginActivity
import br.senac.mobile.activities.main.MainActivity
import br.senac.mobile.databinding.ActivityRegisterBinding
import br.senac.mobile.models.User
import br.senac.mobile.services.API
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    var activity = AppCompatActivity()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            prepareUserData()
        }

        val intent = Intent(this, LoginActivity::class.java)
    }

    private fun createUser(user: User) {
        val callback = object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    localSnackbar("Usuário criado com sucesso!")
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    setSnackbar(AppCompatActivity(), getResponseMessage(response.code()))
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                localSnackbar("Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().user.createUser(user).enqueue(callback)
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun prepareUserData() {
        var hasError = false
        val nicknameText = binding.textInputNickname.text.toString()
        val emailText = binding.textInputEmail.text.toString()
        val passwordText = binding.textInputPassword.text.toString()
        val confirmPasswordText = binding.textInputConfirmPassword.text.toString()

        if (nicknameText.isNullOrEmpty()) {
            localSnackbar("Por favor, digite um nome válido.")
            hasError = true
        }

        if (emailText.isNullOrEmpty() || !emailText.contains("@") || !emailText.contains(".com")) {
            localSnackbar("Por favor, digite um e-mail válido.")
            hasError = true
        }

        if (passwordText.isNullOrEmpty() || passwordText.length < 8) {
            localSnackbar("Nova senha inválida. Mínimo 8 caracteres.")
            hasError = true
        }

        if (confirmPasswordText != passwordText) {
            localSnackbar("As senhas não conferem.")
            hasError = true
        }

        if (!hasError) {
            var newUser: User = User(
                name = nicknameText.toString(),
                email = emailText.toString(),
                password = passwordText.toString(),
                role = "user"
            )
            createUser(newUser)
        }
    }

    private fun localSnackbar(message: String) {
        Snackbar.make(
            this.findViewById(R.id.registerConstraintLayout),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}