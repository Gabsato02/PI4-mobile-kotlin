package br.senac.mobile.utils

import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import com.google.android.material.snackbar.Snackbar

fun setSnackbar(activity: AppCompatActivity, message: String) {
    Snackbar.make(
        activity.findViewById(R.id.mainConstraintLayout),
        message,
        Snackbar.LENGTH_LONG
    ).show()
}