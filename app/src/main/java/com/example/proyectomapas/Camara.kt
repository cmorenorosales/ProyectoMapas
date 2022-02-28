package com.example.proyectomapas


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

class Camara : AppCompatActivity() {


    lateinit var btnCamara : Button
    lateinit var btnImagen : Button
    lateinit var imgView : ImageView
    lateinit var tituloImg : EditText

    val storage = Firebase.storage("gs://proyectomapas-f8be8.appspot.com")
    var storageRef = storage.reference
    var imagesRef = storageRef.child("images")
    var exito :Boolean=false
    //val path = spaceRef.path
    //val name = spaceRef.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)
        btnCamara = findViewById(R.id.btnCamara)
        btnImagen = findViewById(R.id.Imagen)
        imgView = findViewById(R.id.imageView)
        btnCamara.setOnClickListener(View.OnClickListener { abrirCamara() })
        btnImagen.setOnClickListener(View.OnClickListener { buscarImagenes() })
        tituloImg = findViewById(R.id.NombreImagen)
        requestPermission()

    }

    private fun requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                InicioSesion.permisos)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            InicioSesion.permisos -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permisos autorizados",Toast.LENGTH_SHORT).show()
                exito=true
            }else{
                Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirCamara() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }else{
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun buscarImagenes() {
        // You'll need to import com.google.firebase.storage.ktx.component1 and
        // com.google.firebase.storage.ktx.component2
        imagesRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                }

                items.forEach { item ->
                        var patata=storage.getReferenceFromUrl(item.toString())
                        val ONE_MEGABYTE: Long = 10024 * 10024

                        patata.getBytes(ONE_MEGABYTE).addOnSuccessListener{
                            d->
                            imgView!!.setImageBitmap(BitmapFactory.decodeByteArray(d,0,d.size))
                        }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagesRef = storageRef.child("images")

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val extras = data!!.extras
            val imgBitmap = extras!!["data"] as Bitmap?
            imgView!!.setImageBitmap(imgBitmap)

            val baos = ByteArrayOutputStream()
            if (imgBitmap != null) {
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            }

            val metadata = storageMetadata {
                setCustomMetadata("latituz", "98127498127")
                setCustomMetadata("longitud","-1312321312")
            }

            val fileName = tituloImg.text.toString()+".jpg"
            var spaceRef = imagesRef.child(fileName)

            val data = baos.toByteArray()
            spaceRef.putBytes(data,metadata)

        }
    }



}