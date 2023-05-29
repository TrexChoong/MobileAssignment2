package com.example.mobileassignment2

import android.app.AlertDialog
import android.content.Context
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
class SecondFragment : Fragment(), MenuProvider {

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
        val menuHost: MenuHost = this.requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner,
            Lifecycle.State.RESUMED)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)

        //Determine the mode of this fragment
        if(myPlaceViewModel.selectedIndex != -1){// edit mode
            var contact = myPlaceViewModel.placeList.value!!.get(myPlaceViewModel.selectedIndex)
            binding.editTextName.setText(contact.name)
            binding.editTextVicinity.setText(contact.vicinity)
            binding.editTextVicinity.requestFocus()
            binding.editTextName.isEnabled = false
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.second_menu, menu)
        if(myPlaceViewModel.selectedIndex == -1){ //add mode
            menu.findItem(R.id.action_delete).isVisible = false
        }
        // menu.findItem(R.id.action_settings).isVisible = false
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_save){
            //TODO: Insert a new contact to the DB
            binding.apply {
                val name = editTextName.text.toString()
                val vicinity = editTextVicinity.text.toString()
                val dummyDate = "2024-02-21 12:10:18+00:00"
                val newPlace = Place(vicinity, name, dummyDate)
                if(myPlaceViewModel.selectedIndex == -1){ //add mode
                    Log.d("add output:", newPlace.toString())
                    myPlaceViewModel.addContact(newPlace)
                }else{ // edit mode
                    myPlaceViewModel.updateContact(newPlace)
                    Log.d("edit output:", newPlace.toString())
                }
            }
            Toast.makeText(context, getString(R.string.place_saved), Toast.LENGTH_SHORT).show()
        }else if(menuItem.itemId == R.id.action_delete){
            val deleteAlaertDialog = AlertDialog.Builder(requireActivity())
            with(deleteAlaertDialog){
                setMessage(R.string.delete_message)
                setPositiveButton(R.string.action_delete, {dialog, id ->
                    val contact = myPlaceViewModel.placeList.value!!.get(myPlaceViewModel.selectedIndex)
                    myPlaceViewModel.deleteContact(contact)
                    findNavController().navigateUp()
                })
                setNegativeButton(R.string.cancel, {_, _->
                    //do nothing
                })
                create().show()
            }
        }else if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myPlaceViewModel.selectedIndex = -1
        _binding = null
    }
}