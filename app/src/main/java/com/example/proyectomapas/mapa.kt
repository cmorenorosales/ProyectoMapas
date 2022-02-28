package com.example.proyectomapas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class mapa : AppCompatActivity() , OnMapReadyCallback ,GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener{
    private lateinit var  map: GoogleMap
    private lateinit var markerimagen: MutableMap<LatLng, Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        createfragment()

    }

    private fun createfragment(){

        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //funcion implementada por la interfaz de onmapreadycallback
    override fun onMapReady(p0: GoogleMap) {

        map=p0
        map.setOnMyLocationButtonClickListener (this)
        map.setOnMyLocationClickListener (this)
        enableMyLocation()
        createMarker()
    }

    //funcion crear marcador
    private fun createMarker(){
        //coordenadas del marcador en latitud longitud
        val coordenadas= LatLng(40.319185528083196, -3.477578675340428)
        val coordenadas2= LatLng(40.31772510790302, -3.4776298075467387)
        //creas un marcador con esas coordenadas y con el titulo de marcador
        val marker : MarkerOptions = MarkerOptions().position(coordenadas).title("patata")
        val marker2 : MarkerOptions = MarkerOptions().position(coordenadas2).title("patata2")
        //imagen=bitmap
        //añado al marcador la ui de la ventana de informacion
        map.setInfoWindowAdapter(CustomInfoWindowAdapter())

        val badge= BitmapFactory.decodeResource(resources,R.drawable.p1110594)
        val badge2= BitmapFactory.decodeResource(resources,R.drawable.fondo_konata)

        markerimagen = mutableMapOf(
            marker.position to badge

        )


        markerimagen.put(marker2.position ,badge2)

        //aniades el marcador al mapa
        map.addMarker(marker)
        map.addMarker(marker2)
        //el mapa hace zoom con una animacion de 5000 milisegundos al punto de coordenadas
        //todo sustituir coordenadas por la posicion del mapa en la que estamos

        // no se puede conseguir las coordenadas
        //var misCoordenadas=LatLng(map.getMyLocation().latitude,map.myLocation.longitude)


        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,18f),5000,null)

    }


    //funcion que comprueba si tenemos permisos y razon por la que la linea " map.isMyLocationEnabled = true" esta en rojo pero aun asi funciona
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    //mira si ya nos dieron los permisos y si no los pide
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }


    //pide los permisos
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                InicioSesion.permisos)
        }
    }


    //funcion que se realiza una vez nos dan los permisos, esta esta override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            InicioSesion.permisos -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


    //a partir de aqui customizo marcadores

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        // These are both view groups containing an ImageView with id "badge" and two
        // TextViews with id "title" and "snippet".
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)
        private val contents: View = layoutInflater.inflate(R.layout.custom_info_contents, null)



        override fun getInfoWindow(marker: Marker): View? {
            /*    if (options.checkedRadioButtonId != R.id.custom_info_window) {
                    // This means that getInfoContents will be called.
                    return null
                }
              */
            render(marker, window)
            return window
        }

        //esta funcion necesita ser implementada, aun no se para que
        override fun getInfoContents(p0: Marker?): View {
            TODO("Not yet implemented")
        }

        private fun render(marker: Marker, view: View) {



            // badge sera la variable donde guardemos el bitmap

            var badge=markerimagen.get(marker.position)

            view.findViewById<ImageView>(R.id.imagen).setImageBitmap(badge)

            // variables para guardar el titulo de la ventana de informacion y el titulo del marcador
            val title: String? = marker.title
            val titleUi = view.findViewById<TextView>(R.id.title)

            //hago que en el titulo de la ventana de info salga el titulo del marcador
            titleUi.text=title

            /*variables para guardar el contenido de la ventana de informacion
            val snippet: String? = marker.snippet
            val snippetUi = view.findViewById<TextView>(R.id.snippet)
            snippetUi.text = ""
            */
        }

    }

    override fun onMyLocationButtonClick(): Boolean {

        return false
    }

    override fun onMyLocationClick(p0: Location) {
        val intent = Intent(this,Camara::class.java)
        startActivity(intent)
    }
}