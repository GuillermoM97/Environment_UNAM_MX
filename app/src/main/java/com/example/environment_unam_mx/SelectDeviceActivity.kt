package com.example.environment_unam_mx

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast

class SelectDeviceActivity : AppCompatActivity() {
    //A late-init var is a variable which is not initialized in that moment (late) (init)
    //To make sure m_bluetoothAdapter can actually be a nullable object we add a ? (a lateinit
    //cannot be a nullable object)
    //These variables could be private, otherwise other classes can actually affect and interact
    //with them, so I'm changing them to private.
    private var m_bluetoothAdapter: BluetoothAdapter?=null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private lateinit var namedevice: String
    private val REQUEST_ENABLE_BLUETOOTH = 1

    //A companion objects works when I'd like to access them from other classes.
    //When we are going to use them as the key for our extras prior intent extras so when
    // we are moving data from one page to other
    companion object{
        val EXTRA_ADDRESS: String="Device_address"
    }

    //onCreate starts whenever the page is started
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //For data base
        //var database = FirebaseDatabase.getInstance().reference
        //database.setValue("UNAM_Mexico")
        //


        setContentView(R.layout.select_device_layout)

        m_bluetoothAdapter=BluetoothAdapter.getDefaultAdapter()
        toast(getString(R.string.What2Do1))
        toast(getString(R.string.What2Do2))
        //Make sure it is not null
        if(m_bluetoothAdapter==null){
            toast(getString(R.string.BTnotsupported))
            return
        }
        //With !! that is saying that's not going to be null (Should work fine)
        //These lines are targeting to let us enable or disable bluetooth
        if(!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }
        //onClick listener to refresh list
        //We use curly braces{}
        select_device_refresh.setOnClickListener{pairedDeviceList()}
        btntestloc.setOnClickListener{
            //Calling another activity (ControlActivity, pointing out the address)
            val intent2 = Intent(this,LocTest::class.java)
            startActivity(intent2)
        }



    }


    private fun pairedDeviceList(){
        //toast(getString(R.string.try1))
        //I'm getting the devices
        m_pairedDevices=m_bluetoothAdapter!!.bondedDevices
        //Creating the ArrayList
        val list: ArrayList<BluetoothDevice> =ArrayList()
        val list2: ArrayList<String> = ArrayList()
        if (!m_pairedDevices.isEmpty()){
            for(device:BluetoothDevice in m_pairedDevices){
                namedevice= device.getName()
                list.add(device)
                list2.add(getString(R.string.name)+namedevice)
                //toast("1st is called: "+mmm)
                Log.i("Device",""+device)
            }

        } else {
            toast(getString(R.string.NoPairedDevFound))
        }
        //Show in the context the item stylings and the data:
        val adapter=ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val adapter2=ArrayAdapter(this,android.R.layout.simple_list_item_1,list2)
        //Showing the list of devices
        //select_device_list.adapter=adapter
        aux_list.adapter=adapter2


        //I just need position so that I send just underscores
//        select_device_list.onItemClickListener = AdapterView.OnItemClickListener{_,_,position,_ ->
//            val device:BluetoothDevice = list[position]
//            val address: String = device.address
//            //Calling another activity (ControlActivity, pointing out the address)
//            val intent = Intent(this,ControlActivity::class.java)
//            intent.putExtra(EXTRA_ADDRESS,address)
//            startActivity(intent)
//        }

        aux_list.onItemClickListener=AdapterView.OnItemClickListener{_,_,position,_ ->
        val device:BluetoothDevice = list[position]
        val address: String = device.address
        //Calling another activity (ControlActivity, pointing out the address)
        val intent = Intent(this,ControlActivity::class.java)
        intent.putExtra(EXTRA_ADDRESS,address)
        startActivity(intent)
    }




    }

    //To tell the user the BT enabling was successful or not
    override fun onActivityResult(requestCode: Int,resultCode:Int,data:Intent?){
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode== Activity.RESULT_OK){
                if(m_bluetoothAdapter!!.isEnabled){
                    toast(getString(R.string.BTenabled))
                } else{
                    toast(getString(R.string.BTdisabled))
                }
            } else if (resultCode == Activity.RESULT_CANCELED){
                toast(getString(R.string.BTen_cancelled))
            }

        }
    }

}