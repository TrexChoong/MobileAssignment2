package com.example.mobileassignment2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.mobileassignment2.databinding.FragmentFirstBinding
import my.edu.tarc.mycontact.WebDB
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), MenuProvider, RecordClickListener   {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Refer to the View Model created by the Main Activity
    val myPlaceViewModel : PlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = this.requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner,
            Lifecycle.State.RESUMED)
        return binding.root

    }

    override fun onRecordClickListener(index: Int) {
        //update the slected index
        myPlaceViewModel.selectedIndex = index
        findNavController().navigate(R.id.SecondFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        //delete all to test download
            //myPlaceViewModel.deleteAll()

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.placesContainer.adapter = adapter
    }

    fun downloadPlace(context: Context, url: String){
        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Process the JSON
                try {
                    if (response != null) {
                        val jsonArray: JSONArray = response
                        val size: Int = jsonArray.length()

                        if(myPlaceViewModel.placeList.value?.isNotEmpty()!!){
                            myPlaceViewModel.deleteAll()
                        }

                        for (i in 0..size - 1) {
                            var jsonContact: JSONObject = jsonArray.getJSONObject(i)
                            var place = Place(
                                jsonContact.getString("vicinity"),
                                jsonContact.getString("name"),
                                jsonContact.getString("date_expired")
                            )
                            myPlaceViewModel.addContact(Place(place?.vicinity!!, place?.name!!, place?.date_expired!! ))
                        }
                        Toast.makeText(context, "$size record(s) downloaded", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: UnknownHostException){
                    Log.d("ContactRepository", "Unknown Host: %s".format(e.message.toString()))
                }
                catch (e: Exception) {
                    Log.d("ContactRepository", "Response: %s".format(e.message.toString()))
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


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //DO NOTHING HERE
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_upload){
//            //TODO upload contact list to firebase realtime DB
//            val sharedPreferences: SharedPreferences = requireActivity().getPreferences(
//                Context.MODE_PRIVATE)
//            val id = sharedPreferences.getString(
//                getString(R.string.name),"")
//
//            if(id.isNullOrEmpty()){
//                Toast.makeText(context, getString(R.string.place_error), Toast.LENGTH_SHORT).show()
//            }else{
                myPlaceViewModel.uploadContact("test string")
                //Toast.makeText(context, getString(R.string.place_uploaded), Toast.LENGTH_SHORT).show()
           // }
        } else if (menuItem.itemId == R.id.action_download){
            downloadPlace(requireContext(),"https://findmyrahmah-e29bf-default-rtdb.asia-southeast1.firebasedatabase.app/.json")        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}