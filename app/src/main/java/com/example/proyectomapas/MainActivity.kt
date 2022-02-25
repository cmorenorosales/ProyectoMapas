package com.example.proyectomapas

import android.content.ContentValues.TAG
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

class MainActivity : AppCompatActivity() {

    private lateinit var correo :EditText
    private lateinit var pass :EditText
    private lateinit var boton :Button

    private lateinit var creado :TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        correo=findViewById(R.id.correo)
        pass=findViewById(R.id.pass)
        boton=findViewById(R.id.buttonCrear)

        creado=findViewById(R.id.CreadoOno)

        // Initialize Firebase Auth
        auth = Firebase.auth

        boton.setOnClickListener(){crearuser(pass.text.toString(),correo.text.toString())}

    }

    fun crearuser(pass: String,correo: String){

        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }

    }

    private fun updateUI(user: FirebaseUser?) {


        if (user!=null)
            creado.setText("usuario creado")
        else
            creado.setText("usuario no creado")
    }



}