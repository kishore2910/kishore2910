package com.powersoaps.distributorsales.ui.main.activity.support

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.powersoap.sales.rep.aws.AWSFileUploader
import com.powersoaps.distributorsales.data.model.LoginData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.ActivityHelpSupportBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.utils.Util
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList

class HelpSupportActivity : BaseActivity(), AWSFileUploader.AWSFileUploadListener {

    private val helpSupportBinding by lazy { ActivityHelpSupportBinding.inflate(layoutInflater) }

    private val authViewModel : AuthViewModel by viewModels()

    var description = ""
    var image = ""
    var sales_man_token = ""
    private val uploadData: ArrayList<String> by lazy { arrayListOf() }
    private val imageData : ArrayList<String> by lazy { arrayListOf() }
    private val uploadDocument: JsonArray = JsonArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(helpSupportBinding.root)

        authViewModel.supportList.observe(this@HelpSupportActivity, supportResponse)

        sales_man_token = Preference(this).getParentDetails()["token"].toString()
//        Toast.makeText(this, "$sales_man_token", Toast.LENGTH_SHORT).show()
        helpSupportBinding.sendBtn.isEnabled = true
        helpSupportBinding.Back.setOnDebounceListener {
            onBackPressed()
        }


        helpSupportBinding.mailus.setOnDebounceListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:${"sales@powersoaps.com"}"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, R.id.message)
            startActivity(intent)
        }
        helpSupportBinding.uploadImage.visibility = View.VISIBLE
        helpSupportBinding.uploadText.visibility = View.VISIBLE
        helpSupportBinding.tickImage.visibility = View.INVISIBLE

        helpSupportBinding.uploadText.setOnClickListener {
//            Toast.makeText(this, "cickedImg", Toast.LENGTH_SHORT).show()
            getImage()
        }

        helpSupportBinding.sendBtn.setOnDebounceListener {
            description = helpSupportBinding.content.text.toString()
//            helpSupportBinding.sendBtn.isEnabled = false
            if(description.isNotEmpty()){
                val jsonObject = JsonObject().apply {
                    addProperty("sales_token", sales_man_token)
                    addProperty("description", description)
                    addProperty("attach", image)
                }
                Log.d("TAG", "onCreateJsonObject: $jsonObject")
                if (isNetworkConnected(this)){
//
//                    viewModel.supportSalesRep(jsonObject = jsonObject).observe(this,supportSalesRepResponse)
                    authViewModel.supportRetailer(jsonObject)
                }
                else
                    startActivity(Intent(this, NoInternetActivity::class.java))
            }else {
                Toast.makeText(this, "Please Add Description", Toast.LENGTH_SHORT).show()
            }

        }

        helpSupportBinding.call.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Are you sure you want to call us?")
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(Util.supportNumber))))
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .show()
        }



    }

    private val supportResponse = Observer<LoginData>{
        if(it.statusCode == 200){
            helpSupportBinding.sendBtn.isEnabled = false
            Toast.makeText(this,"Issue raised successfully",Toast.LENGTH_LONG).show()
            startActivity(Intent(this, BottomNavigationActivity::class.java))
        }else{
            errors("Error", it.message)
//            Toast.makeText(this,it.statusCode.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            ImagePicker.Companion.with(this)
                .compress(100)
                .crop().createIntent { galleryIntent ->
                    resultLauncher.launch(galleryIntent)
                    loader.onChanged(true)
                }

        } else {
            checkCameraPermission()
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val imageUrl: Uri? = result.data?.data
                    Log.d("TAG", "imageUrl: $imageUrl ")


                    if (imageData.size < 2){
                        helpSupportBinding.uploadImage.visibility = View.INVISIBLE
                        helpSupportBinding.uploadText.text = "Uploaded"
                        helpSupportBinding.tickImage.visibility = View.VISIBLE
//                        helpSupportBinding.licenseImg.setImageURI(imageUrl)

                        imageData.add(imageUrl.toString())
                    } else {
                        Toast.makeText(this, "You have upload only 1 images", Toast.LENGTH_SHORT).show()
                    }

                    loader.onChanged(false)
                    uploadData.clear()
                    val fullPath = imageUrl?.let { getPath(this, it) }
                    uploadData.add(fullPath.let { File(it.toString()).toString() })

                    val fileUploader =
                        AWSFileUploader(this, uploadData, this@HelpSupportActivity, "")
                    lifecycleScope.launch {

                        fileUploader.uploadImage()
                    }

                }

            }
            loader.onChanged(false)

        }

    @SuppressLint("ObsoleteSdkInt")
    fun getPath(context: Context?, uri: Uri): String? {
        // DocumentProvider
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context,
                uri
            ) -> {
                // ExternalStorageProvider
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }
                    }
                    isDownloadsDocument(uri) -> { // DownloadsProvider
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(id)
                        )
                        return context?.let { getDataColumn(it, contentUri, null, null) }
                    }
                    isMediaDocument(uri) -> { // MediaProvider
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null

                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }

                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return context?.let {
                            getDataColumn(it, contentUri, selection, selectionArgs)
                        }
                    }
                }
            }

            "content".equals(uri.scheme, ignoreCase = true) -> { // MediaStore (and general)
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else context?.let {
                    getDataColumn(it, uri, null, null)
                }
            }

            "file".equals(uri.scheme, ignoreCase = true) -> { // File
                return uri.path
            }
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                uri?.let { context.contentResolver.query(it, projection, selection, selectionArgs, null) }
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    override fun onSuccess(uploadTicket: ArrayList<String>, documentId: String) {

        image = uploadTicket.toString().substring(1, uploadTicket.toString().length - 1)
        uploadDocument.add(uploadTicket.toString().substring(1, uploadTicket.toString().length - 1))
        Log.d("image", "onSuccess: $uploadDocument")

    }

    override fun onFailure(exception: Exception) {
        Log.d("AWS_Error", exception.toString())
    }


}