package com.powersoap.sales.rep.aws

import android.content.Context
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.File

class AWSFileUploader(
    context: Context, private val filePaths: List<String>,
    private val awsFileUploadListener: AWSFileUploadListener,
    private val documentId: String
) {

    private val uploadTicket: ArrayList<String> by lazy { arrayListOf() }

    private val credentialsProvider by lazy {
        CognitoCachingCredentialsProvider(
            context,
            AWSConstant.COGNITO_IDENTITY_ID,
            Regions.AP_SOUTH_1
        )
    }
    private val s3Client by lazy {
        AmazonS3Client(
            credentialsProvider,
            Region.getRegion(Regions.AP_SOUTH_1)
        )
    }
    private val transferUtility by lazy {
        TransferUtility.builder()
            .s3Client(s3Client)
            .context(context)
            .transferUtilityOptions(TransferUtilityOptions())
            .build()
    }

    /*fun deleteImage(document: String){
        Thread { s3Client.deleteObject(DeleteObjectRequest(AWSConstant.BUCKET_NAME, document)) }.start()
    }*/

    fun uploadImage() {
        uploadTicket.clear()
        filePaths.map {
            val file = File(it)
            /*Utils.profilePicPath = file.name
            Utils.picName = file.name*/
            Log.d("FinalImageUrl", file.name)
            //msg("AWS files upload---> ${Utils.profilePicPath}")
            val transferObserver = transferUtility.upload(
                AWSConstant.BUCKET_NAME,
                AWSConstant.FOLDER_PATH + file.name,
                file,
                CannedAccessControlList.Private
            )
            transferObserver.setTransferListener(transferListener(transferObserver))
            Log.d(transferObserver.toString(), "onStateChanged")
        }
    }
    private fun transferListener(transferObserver: TransferObserver) = object : TransferListener {

        override fun onStateChanged(id: Int, state: TransferState?) {
            when (state) {
                TransferState.COMPLETED -> {
                    val imagePath = AWSConstant.S3_URL +transferObserver.key
                    Log.d(AWSConstant.TAG, "onStateChanged: $imagePath")
                    uploadTicket.add(imagePath)
                    if (uploadTicket.size == filePaths.size) {
                        awsFileUploadListener.onSuccess(uploadTicket, documentId)
                    }
                }

                else -> {
                    println("Fail")
                }
            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//            msg("$id --> $bytesCurrent ---> $bytesTotal")
        }

        override fun onError(id: Int, ex: Exception) {
            awsFileUploadListener.onFailure(ex)
        }
    }
    interface AWSFileUploadListener {
        fun onSuccess(uploadTicket: ArrayList<String>, documentId: String)
        fun onFailure(exception: Exception)
    }
}