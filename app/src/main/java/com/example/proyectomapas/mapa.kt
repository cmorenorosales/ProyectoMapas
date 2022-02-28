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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage

class mapa : AppCompatActivity() , OnMapReadyCallback ,GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener{
    private lateinit var  map: GoogleMap
    private  var markerimagen: MutableMap<LatLng, Bitmap> = mutableMapOf(    )

    val storage = Firebase.storage("gs://proyectomapas-f8be8.appspot.com")
    var storageRef = storage.reference
    var imagesRef = storageRef.child("images")

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }


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

        buscarImagenes()

        map.setInfoWindowAdapter(CustomInfoWindowAdapter())


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

        intent.putExtra("latitud",p0.latitude.toString())
        intent.putExtra("longitud",p0.longitude.toString())
        startActivity(intent)
        finish();
    }

    //funcion buscar imagenes pintar marcadores
    private fun buscarImagenes() {
        // You'll need to import com.google.firebase.storage.ktx.component1 and
        // com.google.firebase.storage.ktx.component2

        var latitud : Double = 0.0
        var longitud : Double = 0.0
        var tit : String=""
        var coorde:LatLng = LatLng(0.0,0.0)
        var markador : MarkerOptions
        var badge= BitmapFactory.decodeResource(resources,R.drawable.p1110594)

        imagesRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }

                //recogemos una por una las rutas de todas las fotos
                items.forEach { item ->
                    //patata guarda la ruta del item
                    var patata=storage.getReferenceFromUrl(item.toString())
                    //constante para el tamaño de las imagenes
                    val ONE_MEGABYTE: Long = 10024 * 10024

                    //recogemos la imagen de el item tratado
                    patata.getBytes(ONE_MEGABYTE).addOnSuccessListener{
                            d->

                            //si hemos podido coger la imagen  intentamos coger los metadatos del item que tiene esa imagen
                            item.metadata.addOnSuccessListener { f->
                                //guardamos latitud longitud y titulo
                                if (f.getCustomMetadata("latitud")!=null) {
                                    latitud = f.getCustomMetadata("latitud").toString().toDouble();
                                    if (f.getCustomMetadata("longitud") != null) {
                                        longitud = f.getCustomMetadata("longitud").toString().toDouble();
                                        tit = f.getCustomMetadata("titulo").toString();
                                        //creamos  un marcador con las coordenadas
                                        coorde = LatLng(latitud, longitud)
                                        markador = MarkerOptions().position(coorde).title(tit)

                                        badge = BitmapFactory.decodeByteArray(d, 0, d.size)

                                        //guardo en el mapa la posicion del marcador con su foto asociada
                                        markerimagen.put(markador.position, badge)

                                        //pongo el marcador en el mapa
                                        map.addMarker(markador)
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }



    }
}

