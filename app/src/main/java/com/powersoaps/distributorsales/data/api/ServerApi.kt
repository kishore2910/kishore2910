package com.powersoaps.distributorsales.data.api

import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.model.*
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ServerApi {

    @POST("login.php")
    fun salesLogin(@Body params: JsonObject): Call<LoginData>

    @POST("login1.php")
    fun salesLoginNew(@Body params: JsonObject): Call<UserData>

    @POST("verify_otp.php")
    fun verifyOTP(@Body params: JsonObject): Call<OtpData>

    @POST("product_list.php")
    fun productList(@Body params: JsonObject): Call<ProductData>

    @POST("update_map_detail.php")
    fun locationList(@Body params: JsonObject): Call<LocationData>

    @POST("home_screen/home_screen.php")
    fun homeList(@Body params: JsonObject): Call<HomeData>

    @POST("pendingOrderHome.php")
    fun homePendingList(@Body params: JsonObject): Call<HomePendingData>

    @POST("full_summary.php")
    fun daysummary(@Body params: JsonObject): Call<SummaryData>

    @POST("full_summary_product.php")
    fun skusummary(@Body params: JsonObject): Call<SkuSummaryData>

    @POST("shop_details.php")
    fun getShopDetails(@Body params: JsonObject): Call<ShopDetailData>

    @POST("product_insert.php")
    fun orderPlace(@Body params: JsonObject): Call<ServerResponse>

    @POST("orderUpdate.php")
    fun orderUpdate(@Body params: JsonObject): Call<ServerResponse>

    @POST("previous_list.php")
    fun PreviousOrderList(@Body params: JsonObject): Call<PreviousOrderData>

    @POST("order_histroy.php")
    fun OrderHistoryApi(@Body params: JsonObject): Call<OrderHistoryData>

    @POST("order_details.php")
    fun OrderDetailsApi(@Body params: JsonObject): Call<OrderDetailsData>

    @POST("shop_type.php")
    fun ShopTypeApi(@Body params: JsonObject): Call<ShopTypeData>

    @POST("add_shop.php")
    fun AddShopApi(@Body params: JsonObject): Call<ShopTypeData>

    @POST("edit_shop.php")
    fun EditShopApi(@Body params: JsonObject): Call<ServerResponse>

    @POST("about_store.php")
    fun AboutStoreApi(@Body params: JsonObject): Call<AboutStoreData>

    @POST("shop_insight.php")
    fun ShopInsightApi(@Body params: JsonObject): Call<ShopInsightData>

    @POST("payment_details.php")
    fun paymentDetailsApi(@Body params: JsonObject): Call<PaymentDetailsData>

    @POST("order_transaction.php")
    fun orderTransactionApi(@Body params: JsonObject): Call<ServerResponse>


    @POST("sales_delivery.php")
    fun orderDeliverApi(@Body params: JsonObject): Call<ServerResponse>

    @POST("update_version.php")
    fun updateVersion(@Body params: JsonObject): Call<VersionCheckData>

    @POST("supportSales.php")
    fun supportSales(@Body params: JsonObject): Call<LoginData>

    @POST("bankDetails.php")
    fun bankDetails(@Body params: JsonObject): Call<BanksDetailsData>

    @POST("sendotp.php")
    fun forgetPassword(@Body params: JsonObject): Call<ForgetPasswordData>

    @POST("passwordUpdate.php")
    fun passwordUpdate(@Body params: JsonObject): Call<CreatePasswordData>

    @POST("shopvisited.php")
    fun shopVisited(@Body params: JsonObject): Call<ShopVisitData>

    @POST("edit_order.php")
    fun editOrder(@Body params: JsonObject): Call<EditOrderData>

    @POST("selectOptionPercentage.php")
    fun discountPercent(@Body params: JsonObject): Call<DiscountData>

    @POST("searchShop.php")
    fun searchApi(@Body params: JsonObject) : Call<SearchData>

    companion object {

        private var certificatePinner = CertificatePinner.Builder()
            .add("powersoaps.online", "sha256/EV3thWtJGLlHQJPHkeABsj4Jfpwf6ecC8bZhNehQMuU=")
//            .add("powersoapapp.site", "sha256/d0olco07KoD5AWf5doDkc2PXFflo2CsvLrxnwLPstDM=")
//            .add("powersoapapp.in", "sha256/ePr7LtCeXWIhk1JXKcgSuwAUWo4PmO3Fak5yJKFo6Nk=")
            .build()
        private val okHttpClient by lazy {
            OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build()
        }

        operator fun invoke(): ServerApi {

            return Retrofit.Builder()

//                development
//                . baseUrl("https://powersoapapp.in/development/api/version_3.0/sales/")

//                     QA
//                    .baseUrl("https://powersoapapp.site/development/api/version_3.0/sales/")

           // Live
//                    .baseUrl("https://powersoaps.online/stage/api/version_6.0/sales/")
//                    .baseUrl("https://powersoaps.online/stage/api/version_7.0/sales/")
//                    .baseUrl("https://powersoaps.online/stage/api/version_8.0/sales/")
                    .baseUrl("https://powersoaps.online/stage/api/version_9.0/sales/")


                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ServerApi::class.java)
        }
    }
}