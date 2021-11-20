package br.senac.mobile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.activities.login.LoginActivity
import br.senac.mobile.activities.register.RegisterActivity
import br.senac.mobile.databinding.FragmentProfileBinding
import br.senac.mobile.models.User
import br.senac.mobile.services.API
import br.senac.mobile.services.LOGIN_FILE
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var userData: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        binding.profilePasswordCheckbox.setOnCheckedChangeListener { _, bool ->
            if (bool) {
                binding.passwordLinearLayout.visibility = View.VISIBLE
            } else {
                binding.passwordLinearLayout.visibility = View.GONE
                binding.profileNewPasswordTextInput.setText("")
                binding.profilePasswordTextInput.setText("")
                binding.profileConfirmPasswordTextInput.setText("")
            }
        }

        binding.profileButton.setOnClickListener {
            prepareUserData(mainActivity)
        }

        binding.swipeRefresh.setOnRefreshListener {
            getUserData()
        }

        binding.logoutButton.setOnClickListener {
            val preferences = mainActivity.getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE).edit()
            preferences.putString("user", "")
            preferences.putString("password", "")
            preferences.putString("userId", "")
            preferences.apply()

            val intent = Intent(mainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    private fun updateUI(userData: User) {
        binding.profileNameTextInput.setText(userData.name)
        binding.profileEmailTextInput.setText(userData.email)

        Picasso
            .get()
            .load("${API().baseUrl}image/user/${userData.id}")
            .error(R.drawable.logo)
            .into(binding.profileUserImage, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.profileImagePogressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.profileImagePogressBar.visibility = View.GONE
                }
            })
    }

    private fun getUserData() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                binding.profileProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    userData = response.body()!!
                    userData?.let { updateUI(userData) }
                } else {
                    setSnackbar(mainActivity, "Não é possível atualizar os dados.")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                binding.profileProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }

        }
        val preferences = mainActivity.getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE)
        val userId = preferences.getString("userId", "") as String
        API().user.getUser(userId.toInt()).enqueue(callback)
        binding.profileProgressBar.visibility = View.VISIBLE
    }

    private fun updateUser(user: User) {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                binding.profileProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    setSnackbar(mainActivity, "Atualizado com sucesso!")
                    getUserData()
                } else {
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                binding.profileProgressBar.visibility = View.GONE
                setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        user.id?.let { API().user.updateUser(it, user).enqueue(callback) }
        binding.profileProgressBar.visibility = View.VISIBLE
    }

    private fun prepareUserData(mainActivity: AppCompatActivity) {
        var hasError = false
        val passwordText = binding.profilePasswordTextInput.text.toString()
        val newPasswordText = binding.profileNewPasswordTextInput.text.toString()
        val confirmPasswordText = binding.profileConfirmPasswordTextInput.text.toString()
        val emailText = binding.profileEmailTextInput.text.toString()
        val nameText = binding.profileNameTextInput.text.toString()

        if (binding.profilePasswordCheckbox.isChecked) {
            if (passwordText.isNullOrEmpty() || passwordText.length < 8) {
                setSnackbar(mainActivity, "Senha atual inválida.")
                hasError = true
            }
            if (newPasswordText.isNullOrEmpty() || newPasswordText.length < 8) {
                setSnackbar(mainActivity, "A nova senha deve conter no mínimo 8 caracteres.")
                hasError = true
            }
            if (confirmPasswordText != newPasswordText) {
                setSnackbar(mainActivity, "As senhas não conferem.")
                hasError = true
            }
        }

        if (emailText.isNullOrEmpty() || !emailText.contains("@") || !emailText.contains(".com")) {
            setSnackbar(mainActivity, "Por favor, digite um e-mail válido.")
            hasError = true
        }

        if (nameText.isNullOrEmpty()) {
            setSnackbar(mainActivity, "Por favor, digite um nome válido.")
            hasError = true
        }

        if (!hasError) {
            var user: User = User(
                id = userData.id,
                name = if (nameText.isNotBlank()) nameText else userData.name,
                email = if (emailText.isNotBlank()) emailText else userData.email,
                password = if (passwordText.isNotBlank()) passwordText else userData.password,
                newPassword = if (newPasswordText.isNotBlank()) newPasswordText else null,
                role = userData.role,
                image = userData.image
            )
            updateUser(user)
        }
    }
}