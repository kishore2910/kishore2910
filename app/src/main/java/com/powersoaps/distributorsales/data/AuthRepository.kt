package com.powersoaps.distributorsales.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.api.ServerApi
import com.powersoaps.distributorsales.data.model.AboutStoreData
import com.powersoaps.distributorsales.data.model.BanksDetailsData
import com.powersoaps.distributorsales.data.model.CreatePasswordData
import com.powersoaps.distributorsales.data.model.DiscountData
import com.powersoaps.distributorsales.data.model.EditOrderData
import com.powersoaps.distributorsales.data.model.ForgetPasswordData
import com.powersoaps.distributorsales.data.model.HomeData
import com.powersoaps.distributorsales.data.model.HomePendingData
import com.powersoaps.distributorsales.data.model.LocationData
import com.powersoaps.distributorsales.data.model.LoginData
import com.powersoaps.distributorsales.data.model.OrderDetailsData
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.data.model.OtpData
import com.powersoaps.distributorsales.data.model.PaymentDetailsData
import com.powersoaps.distributorsales.data.model.PreviousOrderData
import com.powersoaps.distributorsales.data.model.ProductData
import com.powersoaps.distributorsales.data.model.SearchData
import com.powersoaps.distributorsales.data.model.ServerResponse
import com.powersoaps.distributorsales.data.model.ShopDetailData
import com.powersoaps.distributorsales.data.model.ShopInsightData
import com.powersoaps.distributorsales.data.model.ShopTypeData
import com.powersoaps.distributorsales.data.model.ShopVisitData
import com.powersoaps.distributorsales.data.model.SkuSummaryData
import com.powersoaps.distributorsales.data.model.SummaryData
import com.powersoaps.distributorsales.data.model.UserData
import com.powersoaps.distributorsales.data.model.VersionCheckData
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    val userLogin = MutableLiveData<LoginData>()

    val otpVerify = MutableLiveData<OtpData>()

    val productList = MutableLiveData<ProductData>()

    val locationList = MutableLiveData<LocationData>()

    val HomeList = MutableLiveData<HomeData>()

    val HomePendingList = MutableLiveData<HomePendingData>()

    val DaySummaryList = MutableLiveData<SummaryData>()

    val skuSummaryList = MutableLiveData<SkuSummaryData>()

    val ShopDetailList = MutableLiveData<ShopDetailData>()

    val placeOrder = MutableLiveData<ServerResponse>()

    val placeOrderUpdate = MutableLiveData<ServerResponse>()

    val previousData = MutableLiveData<PreviousOrderData>()

    val editOrderData = MutableLiveData<EditOrderData>()

    val discountPercentage = MutableLiveData<DiscountData>()

    val search = MutableLiveData<SearchData>()

    val orderHistoryData = MutableLiveData<OrderHistoryData>()

    val orderDetailsData = MutableLiveData<OrderDetailsData>()

    val paymentDetailsData = MutableLiveData<PaymentDetailsData>()

    val ordertrasactiondata = MutableLiveData<ServerResponse>()
    val orderdeliverydata = MutableLiveData<ServerResponse>()

    val ShopTypeData = MutableLiveData<ShopTypeData>()

    val AddShopData = MutableLiveData<ShopTypeData>()

    val AboutStoreData = MutableLiveData<AboutStoreData>()

    val errorString = MutableLiveData<String>()

    val EditShopData = MutableLiveData<ServerResponse>()

    val shopInSightData = MutableLiveData<ShopInsightData>()

    val versionCheck = MutableLiveData<VersionCheckData>()

    val supportList = MutableLiveData<LoginData>()

    val loginNewList = MutableLiveData<UserData>()

    val bankDetails = MutableLiveData<BanksDetailsData>()

    val forgetPasswordList = MutableLiveData<ForgetPasswordData>()

    val createPasswordList = MutableLiveData<CreatePasswordData>()

    val shopVisitedList = MutableLiveData<ShopVisitData>()


    fun loginMethod(mobileNumber: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("phone_number", mobileNumber)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().salesLogin(jsonObject).enqueue(object : Callback<LoginData> {
            override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
                if (response.isSuccessful) {
                    userLogin.value = response.body()
                }
            }

            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun otpMethod(mobileNumber: String, otp: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("phone_number", mobileNumber)
            jsonObject.addProperty("otp", otp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().verifyOTP(jsonObject).enqueue(object : Callback<OtpData> {
            override fun onResponse(call: Call<OtpData>, response: Response<OtpData>) {
                if (response.isSuccessful) {
                    otpVerify.value = response.body()
                }
            }

            override fun onFailure(call: Call<OtpData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun HomeListMethod(employee_id: String, shopId: Int, searchText: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
            jsonObject.addProperty("shopId", shopId)
            jsonObject.addProperty("search_text", searchText)
            Log.d("TAG", "HomeListJsonObject: $jsonObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().homeList(jsonObject).enqueue(object : Callback<HomeData> {
            override fun onResponse(call: Call<HomeData>, response: Response<HomeData>) {
                if (response.isSuccessful) {
                    HomeList.value = response.body()
                }
            }

            override fun onFailure(call: Call<HomeData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun HomePendingListMethod(employee_id: String, shopId: Int) {

        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
            jsonObject.addProperty("shopId", shopId)

            Log.d("TAG", "HomePendingListMethod: $employee_id ")
            Log.d("TAG", "HomePendingListMethod: $shopId ")
            Log.d("TAG", "HomePendingListMethod: $jsonObject ")
        } catch (pendingListException: Exception) {
            pendingListException.printStackTrace()
        }
        ServerApi().homePendingList(jsonObject).enqueue(object : Callback<HomePendingData> {
            override fun onResponse(
                call: Call<HomePendingData>,
                response: Response<HomePendingData>
            ) {
                if (response.isSuccessful) {
                    HomePendingList.value = response.body()
                }
            }

            override fun onFailure(call: Call<HomePendingData>, t: Throwable) {
                errorString.value = t.message
            }

        })
    }

    fun DaysummaryListMethod(employee_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().daysummary(jsonObject).enqueue(object : Callback<SummaryData> {
            override fun onResponse(call: Call<SummaryData>, response: Response<SummaryData>) {
                if (response.isSuccessful) {
                    DaySummaryList.value = response.body()
                }
            }

            override fun onFailure(call: Call<SummaryData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun SkusummaryListMethod(employee_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().skusummary(jsonObject).enqueue(object : Callback<SkuSummaryData> {
            override fun onResponse(
                call: Call<SkuSummaryData>,
                response: Response<SkuSummaryData>
            ) {
                if (response.isSuccessful) {
                    skuSummaryList.value = response.body()
                }
            }

            override fun onFailure(call: Call<SkuSummaryData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }


    fun getShopDetails(employee_id: String, shop_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
            jsonObject.addProperty("shop_id", shop_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().getShopDetails(jsonObject).enqueue(object : Callback<ShopDetailData> {
            override fun onResponse(
                call: Call<ShopDetailData>,
                response: Response<ShopDetailData>
            ) {
                if (response.isSuccessful) {
                    ShopDetailList.value = response.body()
                }
            }

            override fun onFailure(call: Call<ShopDetailData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun ProductListMethod(employee_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employee_id)
            Log.d("TAG", "ProductListMethod: $employee_id")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().productList(jsonObject).enqueue(object : Callback<ProductData> {
            override fun onResponse(call: Call<ProductData>, response: Response<ProductData>) {
                if (response.isSuccessful) {
                    productList.value = response.body()
                }
            }

            override fun onFailure(call: Call<ProductData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun locationlistMethod(employee_id: String, lat: Double, longi: Double, shopid: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_token", employee_id.toInt())
            jsonObject.addProperty("latitude", lat)
            jsonObject.addProperty("longitude", longi)
            jsonObject.addProperty("shop_id", shopid)
            Log.d("TAG", "locationlistMethod: $jsonObject")
            Log.d("TAG", "onLocationResult: OrderHistory $jsonObject")
//            jsonObject.addProperty("otp", otp)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ServerApi().locationList(jsonObject).enqueue(object : Callback<LocationData> {
            override fun onResponse(call: Call<LocationData>, response: Response<LocationData>) {
                if (response.isSuccessful) {
                    locationList.value = response.body()
                }
            }

            override fun onFailure(call: Call<LocationData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun PlaceOrder(jsonObject: JsonObject) {
        ServerApi().orderPlace(params = jsonObject).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (response.isSuccessful) {
                    placeOrder.value = response.body()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun orderUpdate(jsonObject: JsonObject) {
        ServerApi().orderUpdate(params = jsonObject).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (response.isSuccessful) {
                    placeOrderUpdate.value = response.body()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }


    fun LoadPreviousOrder(employedID: String, shop_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            Log.d("TAG", "LoadPreviousOrder: $jsonObject")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().PreviousOrderList(params = jsonObject)
            .enqueue(object : Callback<PreviousOrderData> {
                override fun onResponse(
                    call: Call<PreviousOrderData>,
                    response: Response<PreviousOrderData>
                ) {
                    if (response.isSuccessful) {
                        previousData.value = response.body()
                    }
                }

                override fun onFailure(call: Call<PreviousOrderData>, t: Throwable) {
                    errorString.value = t.message
                }
            })
    }

    fun editOrder(orderToken: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("ordertoken", orderToken)
            Log.d("TAG", "editOrder:$jsonObject ")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().editOrder(params = jsonObject).enqueue(object : Callback<EditOrderData> {
            override fun onResponse(call: Call<EditOrderData>, response: Response<EditOrderData>) {
                if (response.isSuccessful) {
                    editOrderData.value = response.body()
                }
            }

            override fun onFailure(call: Call<EditOrderData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun discountPercentage(employeeId: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employeeId)
            Log.d("TAG", "discountPercentage: ")
        } catch (error: Exception) {
            error.printStackTrace()
        }
        ServerApi().discountPercent(jsonObject).enqueue(object : Callback<DiscountData> {
            override fun onResponse(
                call: Call<DiscountData>,
                response: Response<DiscountData>
            ) {
                if (response.isSuccessful) {
                    discountPercentage.value = response.body()
                }
            }

            override fun onFailure(call: Call<DiscountData>, t: Throwable) {
                errorString.value = t.message
            }

        })
    }

    fun searchApi(employedId: String, searchText: String) {
        val jsonObject = JsonObject()

        try {
            jsonObject.addProperty("employee_id", employedId)
            jsonObject.addProperty("search_text", searchText)
        } catch (error: Exception) {
            error.printStackTrace()
        }

        ServerApi().searchApi(jsonObject).enqueue(object : Callback<SearchData> {
            override fun onResponse(call: Call<SearchData>, response: Response<SearchData>) {

                if (response.isSuccessful) {
                    search.value = response.body()
                }
            }

            override fun onFailure(call: Call<SearchData>, t: Throwable) {

                errorString.value = t.message

            }

        })
    }

    fun OrderHistoryApi(employedID: String, shop_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            Log.d("TAG", "OrderHistoryApi:$jsonObject ")
            Log.d("TAG", "OrderHistoryApi:$shop_id ")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().OrderHistoryApi(params = jsonObject)
            .enqueue(object : Callback<OrderHistoryData> {
                override fun onResponse(
                    call: Call<OrderHistoryData>,
                    response: Response<OrderHistoryData>
                ) {
                    if (response.isSuccessful) {
                        orderHistoryData.value = response.body()
                    }
                }

                override fun onFailure(call: Call<OrderHistoryData>, t: Throwable) {
                    errorString.value = t.message
                }
            })
    }

    fun OrderDetailsApi(employedID: String, shop_id: String, order_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            jsonObject.addProperty("order_id", order_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().OrderDetailsApi(params = jsonObject)
            .enqueue(object : Callback<OrderDetailsData> {
                override fun onResponse(
                    call: Call<OrderDetailsData>,
                    response: Response<OrderDetailsData>
                ) {
                    if (response.isSuccessful) {
                        orderDetailsData.value = response.body()
                    }
                }

                override fun onFailure(call: Call<OrderDetailsData>, t: Throwable) {
                    errorString.value = t.message
                }
            })
    }


    fun paymentdetailApi(employedID: String, shop_id: String, order_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            jsonObject.addProperty("order_id", order_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().paymentDetailsApi(params = jsonObject)
            .enqueue(object : Callback<PaymentDetailsData> {
                override fun onResponse(
                    call: Call<PaymentDetailsData>,
                    response: Response<PaymentDetailsData>
                ) {
                    if (response.isSuccessful) {
                        paymentDetailsData.value = response.body()
                    }
                }

                override fun onFailure(call: Call<PaymentDetailsData>, t: Throwable) {
                    errorString.value = t.message
                }
            })
    }


    //deliver
    fun orderdeliverydata(employedID: String, shop_id: String, order_id: String, pending: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            jsonObject.addProperty("Number", pending)
            Log.d("TAG", "orderdeliverydata: $jsonObject")
            jsonObject.addProperty("order_id", order_id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("TAG", "jsonObject: $jsonObject")
        ServerApi().orderDeliverApi(params = jsonObject).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (response.isSuccessful) {
                    orderdeliverydata.value = response.body()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }


    fun orderTranscationApi(
        employedID: String,
        shop_id: String,
        order_id: String,
        amount_paid: String,
        payment_type: String
    ) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", employedID)
            jsonObject.addProperty("shop_id", shop_id)
            jsonObject.addProperty("order_id", order_id)
            jsonObject.addProperty("amount_paid", amount_paid)
            jsonObject.addProperty("payment_type", payment_type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().orderTransactionApi(params = jsonObject)
            .enqueue(object : Callback<ServerResponse> {
                override fun onResponse(
                    call: Call<ServerResponse>,
                    response: Response<ServerResponse>
                ) {
                    if (response.isSuccessful) {
                        ordertrasactiondata.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    errorString.value = t.message
                }
            })
    }


    fun ShopTypeApi() {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().ShopTypeApi(params = jsonObject).enqueue(object : Callback<ShopTypeData> {
            override fun onResponse(call: Call<ShopTypeData>, response: Response<ShopTypeData>) {
                if (response.isSuccessful) {
                    ShopTypeData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ShopTypeData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun AddShopApi(
        user_token: String,
        units_id: String,
        shop_name: String,
        contact_person: String,
        contact_number: String,
        shop_type: String,
        license_number: String,
        url: String,
        address: String,
        city: String,
        pincode: String,
        coordinates: String
    ) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", user_token)
            jsonObject.addProperty("units_id", units_id)
            jsonObject.addProperty("shop_name", shop_name)
            jsonObject.addProperty("contact_name", contact_person)
            jsonObject.addProperty("contact_number", contact_number)
            jsonObject.addProperty("shop_type", shop_type)
            jsonObject.addProperty("licence_number", license_number)
            jsonObject.addProperty("upload_url", url)
            jsonObject.addProperty("address", address)
            jsonObject.addProperty("city", city)
            jsonObject.addProperty("pincode", pincode)
            jsonObject.addProperty("coordinates", coordinates)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().AddShopApi(params = jsonObject).enqueue(object : Callback<ShopTypeData> {
            override fun onResponse(call: Call<ShopTypeData>, response: Response<ShopTypeData>) {
                if (response.isSuccessful) {
                    AddShopData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ShopTypeData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun AboutStore(user_token: String, shop_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("emplyee_id", user_token)
            jsonObject.addProperty("shop_id", shop_id)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().AboutStoreApi(params = jsonObject).enqueue(object : Callback<AboutStoreData> {
            override fun onResponse(
                call: Call<AboutStoreData>,
                response: Response<AboutStoreData>
            ) {
                if (response.isSuccessful) {
                    AboutStoreData.value = response.body()
                }
            }

            override fun onFailure(call: Call<AboutStoreData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun ShopInsight(user_token: String, shop_id: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("emplyee_id", user_token)
            jsonObject.addProperty("shop_id", shop_id)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().ShopInsightApi(params = jsonObject).enqueue(object : Callback<ShopInsightData> {
            override fun onResponse(
                call: Call<ShopInsightData>,
                response: Response<ShopInsightData>
            ) {
                if (response.isSuccessful) {
                    shopInSightData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ShopInsightData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun EditShopApi(
        user_token: String,
        shop_id: String,
        units_id: String,
        shop_name: String,
        contact_person: String,
        contact_number: String,
        shop_type: String,
        license_number: String,
        url: String,
        address: String,
        city: String,
        pincode: String,
        coordinates: String
    ) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("employee_id", user_token)
            jsonObject.addProperty("shop_id", shop_id)
            jsonObject.addProperty("units_id", units_id)
            jsonObject.addProperty("shop_name", shop_name)
            jsonObject.addProperty("contact_name", contact_person)
            jsonObject.addProperty("contact_number", contact_number)
            jsonObject.addProperty("shop_type", shop_type)
            jsonObject.addProperty("licence_number", license_number)
            jsonObject.addProperty("upload_url", url)
            jsonObject.addProperty("address", address)
            jsonObject.addProperty("city", city)
            jsonObject.addProperty("pincode", pincode)
            jsonObject.addProperty("coordinates", coordinates)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().EditShopApi(params = jsonObject).enqueue(object : Callback<ServerResponse> {
            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (response.isSuccessful) {
                    EditShopData.value = response.body()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun VersionCheckApi(version: String) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("device", "android")
            jsonObject.addProperty("version", version)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ServerApi().updateVersion(jsonObject).enqueue(object : Callback<VersionCheckData> {
            override fun onResponse(
                call: Call<VersionCheckData>,
                response: Response<VersionCheckData>
            ) {
                if (response.isSuccessful) {
                    versionCheck.value = response.body()
                }
            }

            override fun onFailure(call: Call<VersionCheckData>, t: Throwable) {
                errorString.value = t.message
            }
        })
    }

    fun supportRetailer(jsonObject: JsonObject) {

        ServerApi().supportSales(params = jsonObject).enqueue(object : Callback<LoginData> {
            override fun onResponse(
                call: Call<LoginData>,
                response: Response<LoginData>
            ) {
                if (response.isSuccessful) {
                    Log.d("check", "onResponse:success")
                    supportList.value = response.body()
                }
            }

            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                errorString.value = t.message
                Log.d("check", "onResponse:failure ${t.message}")
            }

        })
    }

    fun loginNew(jsonObject: JsonObject) {

        ServerApi().salesLoginNew(params = jsonObject).enqueue(object : Callback<UserData> {
            override fun onResponse(
                call: Call<UserData>,
                response: Response<UserData>
            ) {
                if (response.isSuccessful) {
                    Log.d("check", "onResponse:success")
                    loginNewList.value = response.body()
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                errorString.value = t.message
                Log.d("check", "onResponse:failure ${t.message}")
            }

        })
    }

    fun bankDetails(jsonObject: JsonObject) {

        ServerApi().bankDetails(params = jsonObject).enqueue(object : Callback<BanksDetailsData> {
            override fun onResponse(
                call: Call<BanksDetailsData>,
                response: Response<BanksDetailsData>
            ) {
                if (response.isSuccessful) {
                    Log.d("check", "onResponse:success")
                    bankDetails.value = response.body()
                }
            }

            override fun onFailure(call: Call<BanksDetailsData>, t: Throwable) {
                errorString.value = t.message
                Log.d("check", "onResponse:failure ${t.message}")
            }

        })
    }

    fun forgetPassword(jsonObject: JsonObject) {

        ServerApi().forgetPassword(params = jsonObject)
            .enqueue(object : Callback<ForgetPasswordData> {
                override fun onResponse(
                    call: Call<ForgetPasswordData>,
                    response: Response<ForgetPasswordData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("check", "onResponse:success")
                        forgetPasswordList.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ForgetPasswordData>, t: Throwable) {
                    errorString.value = t.message
                    Log.d("check", "onResponse:failure ${t.message}")
                }

            })
    }

    fun passwordUpdate(jsonObject: JsonObject) {

        ServerApi().passwordUpdate(params = jsonObject)
            .enqueue(object : Callback<CreatePasswordData> {
                override fun onResponse(
                    call: Call<CreatePasswordData>,
                    response: Response<CreatePasswordData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("check", "onResponse:success")
                        createPasswordList.value = response.body()
                    }
                }

                override fun onFailure(call: Call<CreatePasswordData>, t: Throwable) {
                    errorString.value = t.message
                    Log.d("check", "onResponse:failure ${t.message}")
                }

            })
    }

    fun shopVisited(jsonObject: JsonObject) {

        ServerApi().shopVisited(params = jsonObject).enqueue(object : Callback<ShopVisitData> {
            override fun onResponse(
                call: Call<ShopVisitData>,
                response: Response<ShopVisitData>
            ) {
                if (response.isSuccessful) {
                    Log.d("check", "onResponse:success")
                    shopVisitedList.value = response.body()
                }
            }

            override fun onFailure(call: Call<ShopVisitData>, t: Throwable) {
                errorString.value = t.message
                Log.d("check", "onResponse:failure ${t.message}")
            }

        })
    }


}