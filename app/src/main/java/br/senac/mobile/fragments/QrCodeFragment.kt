package br.senac.mobile.fragments

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentQrCodeBinding
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat

class QrCodeFragment : Fragment() {
    lateinit var binding: FragmentQrCodeBinding
    private lateinit var codeScanner: CodeScanner
    var grantedPermission = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        var mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        checkCameraPermission()

        return binding.root
    }

    private fun initializeReader() {
        codeScanner = CodeScanner(activity as AppCompatActivity, binding.codeScannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner.isAutoFocusEnabled = true
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.isFlashEnabled = false
        codeScanner.scanMode = ScanMode.SINGLE

        codeScanner.setDecodeCallback {
            val fragment = ItemFragment.newInstance(it.text.toInt())
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }

        codeScanner.setErrorCallback {
            Toast.makeText(activity as AppCompatActivity, "Não encontrado.", Toast.LENGTH_LONG).show()
        }

        codeScanner.startPreview()
    }

    private fun checkCameraPermission() {
        if (checkSelfPermission(activity as AppCompatActivity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            grantedPermission = true
            initializeReader()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grantedPermission = true
                initializeReader()
            } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                retryPermission()
            } else {
                grantedPermission = false
                Toast.makeText(activity as AppCompatActivity, "Acesso a câmera não autorizado.", Toast.LENGTH_LONG).show()
                val fragment = HomeFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true
            }
        }
    }

    private fun retryPermission() {
        val mainActivity = activity as AppCompatActivity
        AlertDialog.Builder(mainActivity)
            .setTitle("Permissão da Câmera")
            .setMessage("É necessário habilitar a permissão de acesso a câmera.")
            .setCancelable(false)
            .setPositiveButton("Ir para configurações") { _: DialogInterface, _: Int ->
                val fragment = HomeFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true

                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                i.data = Uri.fromParts("package", mainActivity.packageName, null)
                startActivity(i)
            }
            .setNegativeButton("Cancelar") { _: DialogInterface, _: Int ->
                val fragment = HomeFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true
            }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (grantedPermission) codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        if (grantedPermission) codeScanner.releaseResources()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (grantedPermission) codeScanner.releaseResources()
    }
}