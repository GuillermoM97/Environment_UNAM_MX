package com.example.environment_unam_mx

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*
import com.example.environment_unam_mx.SplashScreenActivity.Companion.globalVar
import java.time.LocalTime
import com.example.environment_unam_mx.LocTest
import java.time.LocalDate

class ControlActivity: AppCompatActivity() {
    //Since we have an inner class, and always member variables are being accessed inside
    // we need to make the variables companion objects
    //Is able to be used, accessed in other classes.
    companion object {
        //The next explanations was taken on November 1st, 2020 from
        // https://stackoverflow.com/questions/13964342/android-how-do-bluetooth-uuids-work
        //An Android phone can connect to a device and then use the Service Discovery Protocol
        // (SDP) to find out what services it provides (UUID).
        //In Bluetooth, all objects are identified by UUIDs. These include services, characteristics
        // and many other things. Bluetooth maintains a database of assigned numbers
        // for standard objects, and assigns sub-ranges for vendors (that have paid enough for
        // a reservation). You can view this list here:
        // https://www.bluetooth.com/specifications/assigned-numbers/
        //: I've solved that problem by hardcoding the UUID for Serial port service
        //as per this answer (using UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        //To create a Bluetooth socket to send info
        var m_bluetoothSocket: BluetoothSocket? = null

        //Certain methods are deprecated in Java, but they still work for our purposes.
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var sneezecounter = 0
        var coughcounter = 0
        var dbcounter = 0
        lateinit var string_key: String
        lateinit var string_aux: String
        lateinit var string2_aux: String
        lateinit var string3_aux: String
        lateinit var string_received: String



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var database = FirebaseDatabase.getInstance().reference
        database.setValue("UNAM_Mexico_ControlActivity")

        //To set the UI
        setContentView(R.layout.control_layout)
        //This way we know for a fact that we will be getting the right value from the extras.
        //Here I made a change, the original one was
        //m_address = intent.getStringExtra
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS).toString()

        ConnectToDevice(this).execute()
        //This is where I send the information.
        control_led_on.setOnClickListener {
            sendCommand("a")
            Toast.makeText(this, "Mandaste una a", Toast.LENGTH_LONG).show()
        }
        control_led_off.setOnClickListener {
            sendCommand("b")
            //sendCommand("c")
        }


        control_led_disconnect.setOnClickListener { disconnect() }

        cloud_db_send.setOnClickListener{
            sendCommand("c")
            string_received = receiveData()
            dbcounter += 1
            string_key = "measure_"+ dbcounter.toString()
            //database.setValue("UNAM_Mexico_ControlActivity_"+ dbcounter.toString() )
            var prueba1 = 1
            var prueba2 = 2
            var ubicaciones = ""
            //var classLocTest = LocTest()
            ubicaciones = globalVar
            //ubicaciones = objeto.getLocation()
            //Log.i("-------------------Will try:","to create LocTest")
            //var anotherclass = LocTest()
            //Log.i("-------------------Done:","Created LocTest")
            //ubicaciones = anotherclass.getLocation()
            //Log.i("-------------------Done:", "Created ubicaciones"+ubicaciones)
            //Time
            val currentTime = LocalTime.now()
            val currentDate = LocalDate.now()
            var current_time_to_db = currentTime.toString()
            var current_date_to_db = currentDate.toString()
            database.child(string_key).setValue(Measurement(string_received, coughcounter, sneezecounter,ubicaciones, prueba1, current_time_to_db, current_date_to_db))
        }

        btnaux.setOnClickListener{
            sendCommand("c")
            Log.i("Entré al btnauxsetOnClickListener", "Entré al btnauxetOnClickListener")
            string_received=receiveData()
            //Here is where I have the received information. I'll try to send it to the cloud.
            textView_received.text= string_received
        }

        btncough.setOnClickListener {
            coughcounter = coughcounter + 1
            Log.i("Entré al setOnClickListener", "Entré al setOnClickListener")
            string_aux = getString(R.string.CoughReported)
            Log.i("done get string", "done get string")
            string2_aux = coughcounter.toString()
            Log.i("done get string2", "done get string2")
            string3_aux = string_aux + " " + string2_aux
            Log.i("concat", "concat")
            Toast.makeText(this, string3_aux, Toast.LENGTH_LONG).show()
            //Toast.makeText(this, R.string.CoughReported , Toast.LENGTH_LONG).show()}//+ coughcounter.toString()
        }


        btnsneeze.setOnClickListener {
            sneezecounter = sneezecounter + 1
            Log.i("Entré al setOnClickListener", "Entré al setOnClickListener")
            string_aux = getString(R.string.SneezeReported)
            Log.i("done get string", "done get string")
            string2_aux = sneezecounter.toString()
            Log.i("done get string2", "done get string2")
            string3_aux = string_aux + " " + string2_aux
            Log.i("concat", "concat")
            Toast.makeText(this, string3_aux, Toast.LENGTH_LONG).show()
            //Toast.makeText(this, R.string.SneezeReported, Toast.LENGTH_LONG).show()}//+ sneezecounter.toString()
        }
    }
        //It will send data to the Arduino
        private fun sendCommand(input: String) {
            //If the bluetoothSocket is not null, it is necessary to send it as a Byte Array
            //Just like I do when I encode from UTF-8 on Python
            if (m_bluetoothSocket != null) {
                try {
                    m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                } catch (e: IOException) {
                    //It will show why it failed in the case when that happened.
                    e.printStackTrace()
                }
            }

        }
//    fun InputStream.readUpToChar(stopChar: Char): String {
//        val stringBuilder = StringBuilder()
//        var currentChar = this.read().toChar()
//        while (currentChar != stopChar) {
//            stringBuilder.append(currentChar)
//            currentChar = this.read().toChar()
//            if (this.available() <= 0) {
//                stringBuilder.append(currentChar)
//                break
//            }
//        }
//        return stringBuilder.toString()
//    }

        private fun receiveData(): String {
            val buffer=ByteArray(1024)
            var bytes: Int
            var bytestwo: Int
            lateinit var readMessage: String
            var num_av:Int
            var stpchar: Char = 'c'
            Log.i("Entré al receiveData()", "Entré al receiveData()")
            if(m_bluetoothSocket!=null){
                num_av= m_bluetoothSocket!!.inputStream.available()
                Log.i("Entré a", "num_av= m_bluetoothSocket!!.inputStream.available()")
                Log.i("And1", "num_av is"+num_av.toString())
               try{
                    while(num_av<107){
                        num_av= m_bluetoothSocket!!.inputStream.available()
                        Log.i("Entré al ", "while num_av= m_bluetoothSocket!!.inputStream.available()")
                        Log.i("And2", "num_av is"+num_av.toString())
                    }
                    Log.i("Will try","to enter bytes= m_bluetoothSocket!!.inputStream.read(buffer)")
                    bytes= m_bluetoothSocket!!.inputStream.read(buffer)//Necessary !! to
                    Log.i("Successfully done: ","bytes= m_bluetoothSocket!!.inputStream.read(buffer)")
                    // express is not empty
                    Log.i("Entré al bytes= m_bluetoothSocket!!.inputStream.read(buffer)", "Entré al bytes= m_bluetoothSocket!!.inputStream.read(buffer)")
                    readMessage=String(buffer,0, bytes)//The bytes array and the
                    // buffer using UTF-8

                } catch(e: IOException){
                    e.printStackTrace()
                    readMessage = "ErrorToRead"
                }
            }
            return readMessage

        }


        //Disconnect function
        private fun disconnect() {
            //The same condition, if the bluetooth socket is not null.
            if (m_bluetoothSocket != null) {
                try {
                    m_bluetoothSocket!!.close()
                    m_bluetoothSocket = null
                    m_isConnected = false
                } catch (e: IOException) {
                    //Shows why it has failed in case it happen to happen
                    e.printStackTrace()
                }
            }
            finish()
        }

        //Called as initialized When we attempt to connect with Bluetooth which extends AsyncTask
        //It requires the context to be passed in
        private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {

            //We need a constructor for this class and its variables
            private var connectSuccess: Boolean = true
            private val context: Context

            //the last one needs to be initialized in the next lines.
            //It might want to us to do it differently, field leak issue. Complaining about memory
            init {
                this.context = c

            }

            override fun onPreExecute() {
                super.onPreExecute()
                //m_progress = ProgressDialog.show(context,R.string.Connecting.toString(),R.string.PlsWait.toString())    // DIALOG PROGRESS
                Toast.makeText(this.context, R.string.Connecting, Toast.LENGTH_LONG).show()
                //toast("Something")
                //toast(R.string.Connecting.toString())
                //toast("FUNCIONA PLIS ")
                //toast(R.string.Connecting.toString())

            }

            //We add the ? to it to cannot return null if it needs.
            override fun doInBackground(vararg params: Void?): String? {
                try {
                    if (m_bluetoothSocket == null || !m_isConnected) {
                        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        //The address we got that from the intent
                        val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                        //To set up the socket with the device with my UUID
                        //Sets up the connection between our phone and our Bluetooth device we want to
                        // connect to.
                        m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                        //It stops from looking for other devices trying to connect to thereby saving
                        //our backs, basically saving battery! and resources.
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        m_bluetoothSocket!!.connect()
                    }

                } catch (e: IOException) {
                    connectSuccess = false
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (!connectSuccess) {
                    Log.i("data", R.string.CouldntConnect.toString())
                } else {
                    m_isConnected = true
                }
                //m_progress.dismiss()     //DIALOG PROGRESS
            }

        }

    }

