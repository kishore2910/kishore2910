package com.powersoaps.distributorsales.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AWSFileUploader (context: Context, private val filePaths: File, private val awsFileUploadListener: AWSFileUploadListener) {
    //    private val resultFileURLs by lazy { arrayListOf<String>() }
    private val credentialsProvider by lazy {
        CognitoCachingCredentialsProvider(
            context,
            AwsConstants.COGNITO_IDENTITY_ID,
            Regions.AP_SOUTH_1
        )
    }
    private val s3Client by lazy {
        AmazonS3Client(
            credentialsProvider,
            Region.getRegion(AwsConstants.COGNITO_REGION)
        )
    }
    private val transferUtility by lazy {
        TransferUtility.builder()
            .s3Client(s3Client)
            .context(context)
            .transferUtilityOptions(TransferUtilityOptions())
            .build()
    }
    fun uploadImage(folderName:String) {
        try {
            val transferObserver = transferUtility.upload(AwsConstants.BUCKET_NAME, folderName+"/"+  Session.filename+"."+filePaths.extension, filePaths, CannedAccessControlList.Private)
            transferObserver.setTransferListener(transferListener(transferObserver))
            Log.d(transferObserver.toString(), "onStateChanged")
        }catch (e:java.lang.Exception)
        {
            System.out.println(e)
        }

    }
    private fun transferListener(transferObserver: TransferObserver) = object : TransferListener {
        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) { }
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) {
                val imagePath = AwsConstants.S3_URL +transferObserver.key
                Log.d(AwsConstants.TAG, "onStateChanged: $imagePath")
                Util.randombannerimage.add(imagePath)
                awsFileUploadListener.onSuccess(Util.randombannerimage)
            }
        }
        override fun onError(id: Int, ex: Exception) {
            awsFileUploadListener.onFailure(ex)
        }
    }
    interface AWSFileUploadListener {
        fun onSuccess(data: List<String>)
        fun onFailure(exception: Exception)
    }


}