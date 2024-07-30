package com.powersoaps.distributorsales.ui.utils

import com.amazonaws.regions.Regions

object AwsConstants {
    const val TAG = "S3_UPLOAD_TRACKER"

    val COGNITO_IDENTITY_ID: String = "ap-south-1:ebb1348b-859f-46dc-9f8a-8d6a91a79053"
    val COGNITO_REGION: Regions = Regions.AP_SOUTH_1 // Region
    val BUCKET_NAME: String = "powersoapapp"

    val S3_URL: String = "https://d8yt9z8a0r4xc.cloudfront.net"
    val folderPath = "documents/"

}