package com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.HomeData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.FragmentHomeBinding
import com.powersoaps.distributorsales.ui.base.BaseFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.introscreens.LoginActivity
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.ShopDetailActivity
import com.powersoaps.distributorsales.ui.main.activity.summary.SummaryActivity
import com.powersoaps.distributorsales.ui.main.activity.support.HelpSupportActivity
import com.powersoaps.distributorsales.ui.main.adapter.BeatAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class HomeFragment : BaseFragment() {

    private val homeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    private val beatAdapter by lazy { BeatAdapter(shopDetails, ::onClicked, ::onCountClicked) }

    private val homeViewModel: HomeViewModel by viewModels()


    companion object {
        var shopDetails = ArrayList<HomeData.ShopDetails>()
        var selectedShopName: String = ""
        var selectedShopId: String = ""
        var selectedShopType: String = ""
        var selectedShopDistance: String = ""
        var selectedUnitName: String = ""
        var todayCoveredCount: String = ""
        var todayCoveredTotalCount: String = ""
        var unit_token: String = ""
        var isPlaceOrder: Boolean = false
        var isSearch: Boolean = false
        var searchText: String? = null
        var isSwipe: Boolean = false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "onCreateView: onCreateView ")
        return homeBinding.root

    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated: onViewCreated ")

        Session.shopIdPending = 0

        homeViewModel.homeData.observe(requireActivity(), homeResponse)

        homeBinding.daytime.text = getCurrentDate()

        apiHome()

        Log.d("TAG", "onViewCreated:shopDetails  ${shopDetails.size}")
        homeBinding.notify.isEnabled = false

        homeBinding.viewSummary.setOnDebounceListener {
            startActivity(Intent(requireContext(), SummaryActivity::class.java))
        }

        homeBinding.swipeRefreshLayout.setOnRefreshListener {
            if (isNetworkConnected(requireActivity())) {
//                Session.shopId = 0
//                shopDetails.clear()
                isSwipe = true
                homeBinding.searchView.setQuery("", false)
                homeBinding.searchView.clearFocus()
                homeViewModel.home(Session.userToken, Session.shopId, Session.searchingText)
//                homeBinding.swipeRefreshLayout.isRefreshing = false
            } else requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    NoInternetActivity::class.java
                )
            )
        }

        homeBinding.support.setOnClickListener {
            startActivity(Intent(requireContext(), HelpSupportActivity::class.java))
        }

        homeBinding.logout.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Are you sure you want to Logout?")
                .setCancelable(false)
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    Session.shopId = 0
                    shopDetails.clear()
                    dialog.dismiss()
                    Preference(requireActivity()).onLogOut(requireActivity())
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
                .show()
        }


        homeBinding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText

                if (newText?.isEmpty() == true) {
                    isSearch = false
                    searchText = ""
                    Session.shopId = 0
                    shopDetails.clear()
                    if (!isSwipe) {
                        Log.d("TAG", "onQueryTextChange: isSwipe false")
                        apiHome()
                    }
                } else {
                    isSearch = true
                    Log.d("TAG", "onQueryTextChange: $newText")
                }
                return true
            }
        })

        homeBinding.searchButton.setOnClickListener {

            if (searchText?.isNotEmpty() == true) {
                loadingDialog(true)
                homeViewModel.home(Session.userToken, Session.shopId, searchText.toString())

            } else {
                Toast.makeText(
                    requireContext(),
                    "Search Field is Empty Kindly Enter Shop Name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        homeBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->

            if (isScrolledToBottom()) {
                if (isNetworkConnected(requireActivity())) {

                    /*if (homeBinding.searchView.query.toString().replace(" ", "").isNotEmpty()) {
                        Log.d("TAG", "scrollView: ${shopDetails.size}")
                        homeViewModel.home(Session.userToken, Session.shopId, Session.searchingText)
                    }*/

                    if (!isSearch) {
                        if (shopDetails.isNotEmpty()) {
                            Log.d("TAG", "scrollView: ${shopDetails.size}")
                            loadingDialog(true)
                            homeViewModel.home(
                                Session.userToken,
                                Session.shopId,
                                Session.searchingText
                            )
                            Toast.makeText(
                                requireActivity(),
                                "10 Shop Shown Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            loadingDialog(true)
                            Session.shopId = 0
                            homeViewModel.home(
                                Session.userToken,
                                Session.shopId,
                                Session.searchingText
                            )

                        }
                    }

                }
            } else {
                Log.d("TAG", "onViewCreated: ")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy")
        return sdf.format(Date())
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private val homeResponse = Observer<HomeData> { it ->
        try {
            homeBinding.swipeRefreshLayout.isRefreshing = false
            loadingDialog(false)
            if (it.status_code == 200) {
                homeBinding.noBeat.visibility = View.GONE
                todayCoveredCount = it.data.summary_details!!.cover_today_value.toString()
                todayCoveredTotalCount = it.data.summary_details.cover_today_outoff.toString()
                unit_token = it.data.summary_details.unit_token.toString()
                homeBinding.tdycovercount.text =
                    it.data.summary_details.cover_today_value.toString()
                homeBinding.tdycovertotalcount.text =
                    it.data.summary_details.cover_today_outoff.toString()
                homeBinding.deliverycount.text =
                    homeBinding.root.context.getString(R.string.rupee) + " " + it.data.summary_details.today_order.toString()
                val fmt = DecimalFormat("#,##,###")
                val billamount: Double =
                    it.data.summary_details.today_collection_amount!!.toDouble() //If your num is in String
                val currency = fmt.format(billamount)
                homeBinding.collectionamount.text =
                    homeBinding.root.context.getString(R.string.rupee) + " " + currency
                val fmtone = DecimalFormat("#,##,###")
                val billamountone: Double =
                    it.data.summary_details.overall_item_amount_value!!.toDouble() //If your num is in String
                val currencyone = fmtone.format(billamountone)
                homeBinding.totalorderamount.text =
                    homeBinding.root.context.getString(R.string.rupee) + " " + currencyone
                homeBinding.ProgressBar.progress = it.data.summary_details.productivity
                homeBinding.ProgressBar.max = 5
                homeBinding.productcount.text = it.data.summary_details.productivity.toString()
                homeBinding.producttotalcount.text = "5"

                if (Session.coverOutfit == 0) {
                    Session.coverOutfit = it.data.summary_details.cover_today_outoff
                }
                if (Session.coverOutfit != 0) {
                    if (Session.coverOutfit != it.data.summary_details.cover_today_outoff) {
                        shopDetails.clear()
                        Session.shopId = 0
                        Session.coverOutfit = 0
                        homeViewModel.home(Session.userToken, Session.shopId, Session.searchingText)
                    } else {
                        if (it.data.shop_details?.size != 0) {
                            homeBinding.searchView.visibility = View.VISIBLE
                            homeBinding.searchButton.visibility = View.VISIBLE

                            if (searchText?.isNotEmpty() == true) {
                                shopDetails.clear()
                                it.data.shop_details?.let {
                                    shopDetails.addAll(it.distinct())
                                }
                                homeBinding.beatlist.adapter = beatAdapter
                             } else {
//                                shopDetails.clear()
                                it.data.shop_details?.let {
                                    shopDetails.addAll(it.distinct())
                                }
                                Log.d("TAG", "details: $shopDetails")
                                homeBinding.noShopsTextView.visibility = View.GONE
                                homeBinding.beatlist.visibility = View.VISIBLE
                                homeBinding.beatlist.adapter = beatAdapter
                            }

                            Log.d("TAG", "shop_details: ${it.data.shop_details}")
                            Log.d("TAG", "shop_details: ${it.data.shop_details?.size}")
                            if (shopDetails.isNotEmpty()) {
                                val lastShopDetails = shopDetails.last()
                                val lastShopId = lastShopDetails.shopId
                                Session.shopId = lastShopId
                                Log.d("TAG", "shopIDlast: ${Session.shopId}")
                            }
                        } else if (isSearch && it.data.shop_details.isEmpty()) {
                            homeBinding.noShopsTextView.visibility = View.VISIBLE
//                            homeBinding.searchView.visibility = View.GONE
                            homeBinding.beatlist.visibility = View.GONE
                        }
                    }
                }
            } else {
                homeBinding.noBeat.visibility = View.VISIBLE
                homeBinding.searchView.visibility = View.GONE
                homeBinding.beatlist.visibility = View.GONE
                homeBinding.searchButton.visibility = View.GONE
                Log.d("TAG", "STATUS 400 : ")
            }

            Session.coverOutfit = it.data.summary_details!!.cover_today_outoff
        } catch (responseException: Exception) {
            Log.d("TAG", "responseException: $responseException ")
        }

    }

    private fun onClicked(beatData: HomeData.ShopDetails) {
        selectedShopId = beatData.shop_id
        selectedShopName = beatData.shop_name
        selectedShopType = beatData.category_name
        selectedShopDistance = beatData.shop_distance.toString()
        selectedUnitName = beatData.units_name.toString()
        startActivity(Intent(requireContext(), ShopDetailActivity::class.java))
    }


    fun apiHome() {
        if (isNetworkConnected(requireActivity())) {
            loadingDialog(true)
            Log.d("TAG", "onViewCreated: ${Session.userToken}")
            homeViewModel.home(Session.userToken, Session.shopId, Session.searchingText)
        } else requireActivity().startActivity(
            Intent(
                requireActivity(),
                NoInternetActivity::class.java
            )
        )
    }

    fun onCountClicked(size: String) {
    }

    private fun isScrolledToBottom(): Boolean {
        val lastVisibleItemPosition = homeBinding.scrollView.getChildAt(0).bottom
        val scrollViewHeight = homeBinding.scrollView.height
        Log.d("TAG", "scrollViewHeight: $scrollViewHeight")
        Log.d("TAG", "lastVisibleItemPosition: $lastVisibleItemPosition")
        Log.d(
            "TAG",
            "returnValue: ${scrollViewHeight + homeBinding.scrollView.scrollY >= lastVisibleItemPosition}"
        )
        return scrollViewHeight + homeBinding.scrollView.scrollY >= lastVisibleItemPosition
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "onStart: onStart")
        homeBinding.beatlist.adapter = beatAdapter
        Log.d("TAG", "onResume: $isPlaceOrder")
    }

    override fun onResume() {
        super.onResume()

        Log.d("TAG", "onResume: onResume")

        if (isNetworkConnected(requireActivity())) {
            if (isPlaceOrder) {
                shopDetails.clear()
                Log.d("TAG", "onResume:shopDetails REsume   ${shopDetails.size}")
                Log.d("TAG", "onResume: true")
                Log.d("TAG", "onResume: $isPlaceOrder")

                isPlaceOrder = false
                Log.d("TAG", "onResume: isPlaceOrder False $isPlaceOrder")
            } else {
                Log.d("TAG", "onResume: $isPlaceOrder")
                Log.d("TAG", "onResume: shopDetails  ${shopDetails.size}")
                homeBinding.beatlist.adapter = beatAdapter
            }
        } else requireActivity().startActivity(
            Intent(
                requireActivity(),
                NoInternetActivity::class.java
            )
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG", "onAttach:  ")
    }
}