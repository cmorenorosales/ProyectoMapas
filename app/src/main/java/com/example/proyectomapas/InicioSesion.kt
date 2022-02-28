package com.example.proyectomapas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InicioSesion : AppCompatActivity() {
    private lateinit var btnRegistrarse:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var eTextCorreo:EditText
    private lateinit var eTxtPassword:EditText
    private lateinit var btnInicar:Button
    companion object{
        var permisos : Int =1
    }

    private var exito:Boolean=false
    private var PERMISOS= arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
       /* if(currentUser!=null){
            val intent=Intent(this,mapa::class.java)
            startActivity(intent)
            
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        btnInicar=findViewById<Button>(R.id.btnInicar)
        eTextCorreo=findViewById<EditText>(R.id.eTextCorreo)
        eTxtPassword=findViewById<EditText>(R.id.eTextPassword)
        btnRegistrarse=findViewById<Button>(R.id.btnRegistrarse)
        auth=Firebase.auth
        requestPermission()

        btnInicar.setOnClickListener{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(eTextCorreo.text.toString()).matches()){
                inicarSesion(eTxtPassword.text.toString(),eTextCorreo.text.toString())
            }
            else Toast.makeText(baseContext, "patatin",
                Toast.LENGTH_SHORT).show()
        }

        btnRegistrarse.setOnClickListener{
            val intent = Intent(this,Registrar::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {}

    //funciones de permisos
    private fun requestPermission() {

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
        +ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        +ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
        !=PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.CAMERA)||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,Manifest.permission.ACCESS_COARSE_LOCATION)||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

            }else{
                ActivityCompat.requestPermissions(
                    this,PERMISOS,permisos
                )
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            permisos -> if(grantResults.isNotEmpty() && grantResults[0]
                +grantResults[1]
                +grantResults[2]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permisos autorizados",Toast.LENGTH_SHORT).show()
                exito=true
            }else{
                Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun inicarSesion(pass:String,correo:String){
        auth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(this){
            task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    val intent=Intent(this,mapa::class.java)
                    //pido permisos de localizacion ANTES de lanzar el mapa
                    if (exito)
                        startActivity(intent)
                    else
                        Toast.makeText(this, "Necesitas activar la localización y sus permisos, ve a ajustes y aceptalos", Toast.LENGTH_SHORT).show()
                    updateUI(user)
            }else{
                updateUI(null)
            }
        }
    }
    private fun updateUI(user: FirebaseUser?) {


        if (user!=null)
            Toast.makeText(baseContext, "Sesion iniciada",
                Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(baseContext, "Fallo al iniciar sesion",
                Toast.LENGTH_SHORT).show()
    }
}