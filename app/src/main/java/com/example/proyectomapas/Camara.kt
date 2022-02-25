package com.example.proyectomapas

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream

class Camara : AppCompatActivity() {

    lateinit var btnCamara : Button
    lateinit var imgView : ImageView
    lateinit var tituloImg : EditText

    val storage = Firebase.storage("gs://proyectomapas-f8be8.appspot.com")
    var storageRef = storage.reference
    var imagesRef = storageRef.child("images")
    //al path = spaceRef.path
    //val name = spaceRef.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)
        btnCamara = findViewById(R.id.btnCamara)
        imgView = findViewById(R.id.imageView)
        btnCamara.setOnClickListener(View.OnClickListener { abrirCamara() })
        tituloImg = findViewById(R.id.NombreImagen)
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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