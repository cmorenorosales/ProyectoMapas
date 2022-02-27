package com.example.proyectomapas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class InicioSesion : AppCompatActivity() {
    private lateinit var btnRegistrarse:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var eTextCorreo:EditText
    private lateinit var eTxtPassword:EditText
    private lateinit var btnInicar:Button

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if(currentUser!=null){
            val intent=Intent(this,mapa::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        btnInicar.findViewById<Button>(R.id.btnRegistrarse)
        eTextCorreo.findViewById<EditText>(R.id.eTextCorreo)
        eTxtPassword.findViewById<EditText>(R.id.eTextPassword)
        btnRegistrarse.findViewById<Button>(R.id.btnRegistrarse)

        btnInicar.setOnClickListener{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(eTextCorreo.text.toString()).matches()){
                inicarSesion(eTextCorreo.text.toString(),eTxtPassword.text.toString())
            }
        }

        btnRegistrarse.setOnClickListener{
            val intent = Intent(this,Registrar::class.java)
            startActivity(intent)
        }
    }

    fun inicarSesion(pass:String,correo:String){
        auth.signInWithEmailAndPassword(correo,pass).addOnCompleteListener(this){
            task ->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    val intent=Intent(this,mapa::class.java)
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