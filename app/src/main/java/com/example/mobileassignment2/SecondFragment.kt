package com.example.mobileassignment2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mobileassignment2.databinding.FragmentSecondBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import my.edu.tarc.mycontact.WebDB

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), RecordClickListener  {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Refer to the View Model created by the Main Activity
    val myPlaceViewModel : PlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onRecordClickListener(index: Int) {
        //update the slected index
        myPlaceViewModel.selectedIndex = index
        //findNavController().navigate(R.id.nav_second)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        val adapter = PlaceAdapter(this)

        //Add an observer
        myPlaceViewModel.placeList.observe(
            viewLifecycleOwner,
            Observer {
//                if(it.isEmpty()){
//                    binding.textViewCount.isVisible = true
//                    binding.textViewCount.text =
//                        getString(R.string.no_record)
//                }else{
//                    binding.textViewCount.isVisible = false
//                }
                adapter.setPlace(it)
            }
        )
        binding.placesContainer.adapter = adapter
        //downloadContact(requireActivity(), getString(R.string.url_server)+getString(R.string.url_get_all))
        downloadPlace(requireContext(),"https://findmyrahmah-e29bf-default-rtdb.asia-southeast1.firebasedatabase.app/.json")
    }

    fun downloadPlace(context: Context, url: String){
        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Process the JSON
                try {
                    if (response != null) {
//                        val strResponse = response.toString()
//                        val jsonResponse = JSONObject(strResponse)
//                        val jsonArray: JSONArray = jsonResponse.getJSONArray("records")
                        val jsonArray: JSONArray = response
                        val size: Int = jsonArray.length()

                        if(myPlaceViewModel.placeList.value?.isNotEmpty()!!){
                            myPlaceViewModel.deleteAll()
                        }

                        for (i in 0..size - 1) {
                            var jsonContact: JSONObject = jsonArray.getJSONObject(i)
                            //var geometry: Geometry = jsonContact.getString("geometry")
                            var place = Place(
                                jsonContact.getString("vicinity"),
                                jsonContact.getString("name"),
                                jsonContact.getString("date_expired")
                            )
                            myPlaceViewModel.addContact(Place(place?.vicinity!!, place?.name!!, place?.date_expired!! ))
                        }
                        Toast.makeText(context, "$size record(s) downloaded", Toast.LENGTH_SHORT).show()
                        //binding.progressBar.isVisible = false
                    }
                }catch (e: UnknownHostException){
                    Log.d("ContactRepository", "Unknown Host: %s".format(e.message.toString()))
                        //binding.progressBar.isVisible = false
                }
                catch (e: Exception) {
                    Log.d("ContactRepository", "Response: %s".format(e.message.toString()))
                    //binding.progressBar.isVisible = false
                }
            },
            { error ->
                Log.d("ContactRepository", "Error Response: %s".format(error.message.toString()))
            },
        )

        //Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        // Access the RequestQueue through your singleton class.
        WebDB.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}