package com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.data.model.HomeData
import com.powersoaps.distributorsales.data.model.HomePendingData
import com.powersoaps.distributorsales.databinding.FragmentPendingBinding
import com.powersoaps.distributorsales.ui.base.BaseFragment
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment.Companion.selectedShopId
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment.Companion.selectedShopName
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.OrderHistoryActivity
import com.powersoaps.distributorsales.ui.main.adapter.PendingAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.Session.shopIdPending
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel

class PendingFragment : BaseFragment() {

    private val pendingBinding by lazy { FragmentPendingBinding.inflate(layoutInflater) }

    private val pendingAdapter by lazy { PendingAdapter(pendingDetails,::onPendingClicked, ::onCountClicked ) }

    private val homeViewModel: HomeViewModel by viewModels()

    companion object {
        var pendingDetails = ArrayList<HomePendingData.ShopPendingDetails>()

//        var shopID = 0
//        var selectedShopName:String=""
//        var selectedShopId:String=""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAGPendingFragment", "onCreateView: onCreateView")
           shopIdPending = 0
        return pendingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.homePendingData.observe(requireActivity(),homePendingResponse)

        if (isNetworkConnected(requireActivity())) {
            loadingDialog(true)
            Log.d("TAGPendingFragment", "onViewCreated: ${Session.userToken}")
//            homeViewModel.homePending(Session.userToken,shopIdPending )
        }
        else requireActivity().startActivity(Intent(requireActivity(), NoInternetActivity::class.java))

        pendingBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText?.length!! >= 1) {
//                    Log.d("TAG", "onQueryTextChange: $newText")
//                    pendingAdapter.filter.filter(newText)
//                } else {
//                    pendingAdapter.appPendingData(pendingDetails)
//                    pendingBinding.pendingList.adapter = pendingAdapter
//                }
//                return true
//            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length!! >= 1) {
                    Log.d("TAG", "onQueryTextChange: $newText")
                    pendingAdapter.filter.filter(newText)
                    if (HomeFragment.shopDetails.isNotEmpty()) {
                        pendingBinding.noShopsTextView.visibility = View.VISIBLE
                    }
                } else {
                    pendingBinding.noShopsTextView.visibility = View.INVISIBLE
                    pendingAdapter.appPendingData(pendingDetails)
                    pendingBinding.pendingList.adapter = pendingAdapter
                }
                return true
            }

        })

        pendingBinding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->

            if (isScrolledToBottom()) {

                if (pendingDetails.isNotEmpty()){
                    Log.d("TAG", "scrollView: ${pendingDetails.size}")
                    homeViewModel.homePending(Session.userToken, shopIdPending)
                }
            }
        })
    }


    private val homePendingResponse = Observer<HomePendingData> { it ->
        loadingDialog(status = false)

        if (it.status_code == 200){

            if (it.data.shop_Pending_details?.size != 0){
                pendingBinding.noBeat.visibility = View.GONE
                pendingBinding.pendingList.visibility = View.VISIBLE
                pendingBinding.searchView.visibility = View.VISIBLE

                it.data.shop_Pending_details?.let { it1 -> pendingDetails.addAll(it1) }

                if (pendingDetails.isNotEmpty()){

                    Log.d("TAG", "shopPendingFragment:$pendingDetails")
                    Log.d("TAG", "pendingDetailSize:${pendingDetails.size}")

                    val lastShopDetails = pendingDetails.last()
                    val lastShopId = lastShopDetails.shopId
                    shopIdPending = lastShopId
                    Log.d("TAG", "lastShopId :$lastShopId ")

                    if (pendingDetails.size ==0) {
                        pendingBinding.noBeat.visibility = View.VISIBLE
                        pendingBinding.pendingList.visibility = View.GONE
                        pendingBinding.searchView.visibility = View.GONE
                    }
                    else{
                        pendingBinding.pendingList.visibility = View.VISIBLE
                        pendingBinding.searchView.visibility = View.GONE
                        pendingBinding.noBeat.visibility = View.GONE
                        pendingBinding.pendingList.adapter = pendingAdapter
                        pendingBinding.searchView.visibility = View.VISIBLE
                        Log.d("TAG", ":pendingDetails.size ")
                    }
                }else{
                    Log.d("TAG", "pendingData is empty: $pendingDetails")
                }

            }

        } else {
            pendingBinding.noBeat.visibility = View.VISIBLE
            pendingBinding.searchView.visibility = View.GONE
            pendingBinding.pendingList.visibility = View.GONE
            Log.d("TAG", "STATUS: 400 Pending")
        }

    }

    private fun isScrolledToBottom(): Boolean {
        val lastVisibleItemPosition =
            pendingBinding.scrollView.getChildAt(0).bottom
        val scrollViewHeight = pendingBinding.scrollView.height
        Log.d("TAG", "scrollViewHeight: $scrollViewHeight")
        Log.d("TAG", "lastVisibleItemPosition: $lastVisibleItemPosition")
        Log.d("TAG", "returnValue: ${scrollViewHeight + pendingBinding.scrollView.scrollY >= lastVisibleItemPosition}")
        return scrollViewHeight + pendingBinding.scrollView.scrollY >= lastVisibleItemPosition
    }

    private fun onPendingClicked(pendingData : HomePendingData.ShopPendingDetails){
        //        setting the data to the respected variable
        selectedShopId = pendingData.shop_id
        selectedShopName = pendingData.shop_name
        startActivity(Intent(requireContext(), OrderHistoryActivity::class.java))

    }

    fun onCountClicked(size: String) {
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAGPendingFragment", "onStart: ")
        pendingBinding.pendingList.adapter = pendingAdapter
    }

    override fun onResume() {
        super.onResume()

        Log.d("TAG", "onResume:pendingDetails REsume  ${pendingDetails.size}")
        if (isNetworkConnected(requireActivity())) {
            loadingDialog(true)
            Log.d("TAGPendingFragment", "onViewCreated: ${Session.userToken}")
            homeViewModel.homePending(Session.userToken,shopIdPending )
//            pendingBinding.pendingList.adapter = pendingAdapter
        }
        else requireActivity().startActivity(Intent(requireActivity(), NoInternetActivity::class.java))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAGPendingFragment", "onAttach:  ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pendingDetails.clear()
    }

}