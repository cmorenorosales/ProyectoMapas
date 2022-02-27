package com.example.proyectomapas

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registrar : AppCompatActivity() {
    private lateinit var eTxtCorreo:EditText
    private lateinit var eTextPassword:EditText
    private lateinit var eTextPassword2:EditText
    private lateinit var btnRegistrar:Button
    private lateinit var eTextInicarSesion:TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)
        eTxtCorreo=findViewById<EditText>(R.id.eTextCorreo)
        eTextPassword=findViewById<EditText>(R.id.eTextPassword1)
        eTextPassword2=findViewById<EditText>(R.id.eTextPassword2)
        btnRegistrar=findViewById<Button>(R.id.btnRegistrarse)
        eTextInicarSesion=findViewById<EditText>(R.id.eTextIniciarSesion)
        auth= Firebase.auth
        eTextInicarSesion.setOnClickListener{
            val intent = Intent(this,InicioSesion::class.java)
            startActivity(intent)
        }
        btnRegistrar.setOnClickListener{
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(eTxtCorreo.text.toString()).matches()){
                if (eTextPassword.text.toString().equals(eTextPassword2.text.toString())){
                    if(eTextPassword.length()>=6){

                        crearuser(eTextPassword.text.toString(),eTxtCorreo.text.toString())
                    }else{
                        Toast.makeText(baseContext, "Minimo de 6 caracteres",
                            Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(baseContext, "Passwords no iguales",
                        Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(baseContext, "Correo invalido",
                    Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun crearuser(pass: String,correo: String){

        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null)
                }
            }

    }
    private fun updateUI(user: FirebaseUser?) {


        if (user!=null)
            Toast.makeText(baseContext, "Usuario creado",
                Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(baseContext, "Error al crear usuario",
                Toast.LENGTH_SHORT).show()
    }
}