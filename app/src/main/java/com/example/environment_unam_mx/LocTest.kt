package com.example.environment_unam_mx

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_loc_test.*
import com.example.environment_unam_mx.SplashScreenActivity.Companion.globalVar
import java.util.*

private const val PERMISSION_REQUEST = 10

class LocTest : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    public var hasGps = false
    public var hasNetwork = false
    public var locationGps: Location? = null
    public var locationNetwork: Location? = null

    public var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    public var locmediciones = "NotAvailable"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loc_test)
        disableView()
        //Next lines are to ensure I got permissions from the user.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
                Toast.makeText(this, R.string.GrantPerm, Toast.LENGTH_LONG).show()
            }
        } else {
            enableView()
        }

    }
    public fun disableView() {
        btngetloc.isEnabled = false //Disable button
        btngetloc.alpha = 0.5F //It looks like disabled
    }

    public fun enableView() {
        btngetloc.isEnabled = true
        btngetloc.alpha = 1F
        btngetloc.setOnClickListener { globalVar=getLocation()}
        Toast.makeText(this, R.string.LocOk, Toast.LENGTH_SHORT).show()
    }
    //The lint tool helps find poorly structured code that can impact the reliability and
    // efficiency of your Android apps and make your code harder to maintain.
    @SuppressLint("MissingPermission")

    public fun getLocation(): String {
        var latitud_measured_gps = "NoData"
        var longitud_measured_gps = "NoData"
        var loc_accuracy_gps = "NoData"
        var latitud_measured_network = "NoData"
        var longitud_measured_network = "NoData"
        var loc_accuracy_network = "NoData"
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (location != null) {
                            locationGps = location
                            txtviewlocation.append("\nGPS ")
                            txtviewlocation.append("\nLatitude : " + locationGps!!.latitude)
                            txtviewlocation.append("\nLongitude : " + locationGps!!.longitude)
                            Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                            latitud_measured_gps = locationGps!!.latitude.toString()
                            longitud_measured_gps = locationGps!!.longitude.toString()
                            loc_accuracy_gps = locationNetwork!!.accuracy.toString()
                            globalVar = Arrays.toString(arrayOf(latitud_measured_gps,longitud_measured_gps,loc_accuracy_gps))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (location != null) {
                            locationNetwork = location
                            txtviewlocation.append("\nNetwork ")
                            txtviewlocation.append("\nLatitude : " + locationNetwork!!.latitude)
                            txtviewlocation.append("\nLongitude : " + locationNetwork!!.longitude)
                            Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                            Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                            latitud_measured_network = locationNetwork!!.latitude.toString()
                            longitud_measured_network = locationNetwork!!.longitude.toString()
                            loc_accuracy_network = locationNetwork!!.accuracy.toString()
                            globalVar = Arrays.toString(arrayOf(latitud_measured_network,longitud_measured_network,loc_accuracy_network))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    txtviewlocation.append("\n Network location is more accurate")
                    txtviewlocation.append("\nNetwork ")
                    txtviewlocation.append("\nLatitude : " + locationNetwork!!.latitude)
                    txtviewlocation.append("\nLongitude : " + locationNetwork!!.longitude)
                    Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                }else{
                    txtviewlocation.append("\n GPS location is more accurate")
                    txtviewlocation.append("\nGPS ")
                    txtviewlocation.append("\nLatitude : " + locationGps!!.latitude)
                    txtviewlocation.append("\nLongitude : " + locationGps!!.longitude)
                    Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                    Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        //return arrayOf(latitud_measured_gps,longitud_measured_gps,loc_accuracy_gps,latitud_measured_network,longitud_measured_network,loc_accuracy_network).toString()
        return Arrays.toString(arrayOf(latitud_measured_gps,longitud_measured_gps,loc_accuracy_gps,latitud_measured_network,longitud_measured_network,loc_accuracy_network))
    }

    public fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, R.string.PermDenied, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.Go2Settings, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }
    }
}