package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentProfileBinding
import br.senac.mobile.models.User
import br.senac.mobile.services.API
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    private fun updateUI(userData: User) {
        binding.profileNameText.text = userData.name
        binding.profileEmailText.text = userData.email

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

                if (response.isSuccessful) {
                    val userData = response.body()
                    userData?.let { updateUI(userData) }
                } else {
                    Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                        "Não é possível atualizar os dados.",
                        Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                binding.profileProgressBar.visibility = View.GONE

                Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                    "Não foi possível conectar ao servidor.",
                    Snackbar.LENGTH_LONG).show()
                Log.e("ERROR", "Falha ao executar serviço", t)
            }

        }

        API().user.getUser().enqueue(callback)
        binding.profileProgressBar.visibility = View.VISIBLE
    }
}