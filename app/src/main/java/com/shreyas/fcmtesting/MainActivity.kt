package com.shreyas.fcmtesting

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    var token:String? = null
    var name:String? = null

    companion object {
        val BLACK = "Black"
        val WHITE = "White"
        val COLOR = "Color"
        val FROMSERIVCE = "fromIntent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this).registerReceiver(brHandler, IntentFilter("com.shreyas.fcmtesting_FCM"))
        db = FirebaseFirestore.getInstance()
        setUpFirebase()

        Log.d("test_12345", "da: ${(Utiles.getPrefsName(this) == null) }")

        try {
            db.collection("users").document(Utiles.getPrefsName(this)!!).get()
                .addOnSuccessListener { docRef ->
                    if (docRef.exists()) {

                    } else {
                        showAlertDialog()
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("test_1232", "sda:  ${e.message}")

                }
        }catch (e:Exception) {
            showAlertDialog()
            Log.d("test000", "ASd: ${e.message}")
        }

        getData()


    }

    private fun showAlertDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)

        val editText = dialog.findViewById<EditText>(R.id.et_uniqueName)
        val mBtnSubmit = dialog.findViewById<Button>(R.id.btn_submit)

        dialog.show()

        mBtnSubmit.setOnClickListener(View.OnClickListener {

            val alphaRegex = "[a-zA-Z0-9]+".toRegex()

            Log.d("test12345", "Sda:  "+editText.text.toString().matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+\$".toRegex()))

            name = editText.text.toString().trim()

            if (name!!.isEmpty()) {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (alphaRegex.matches(name!!)) {
                Toast.makeText(this, "valid", Toast.LENGTH_SHORT).show()
                checkFirebase(name!!, dialog)
            } else {

                Toast.makeText(this, "Name should be alpha numeric", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkFirebase(name: String, dialog: Dialog) {
        db.collection("users").document(name).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                Toast.makeText(this, "The user name already exists",Toast.LENGTH_SHORT).show()
            }else {
                setUpDb(name, dialog)
            }
        }
    }

    //create a document of type string key=name and data to that document is token whick obtained from model class
    private fun setUpDb(name: String, dialog: Dialog) {
        val model = Model(token!!)

        db.collection("users").document(name).set(model).addOnSuccessListener { docRefs->
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
            Utiles.setPrefs(this, name, token)
            dialog.dismiss()
        }
            .addOnFailureListener { e->
                Log.d("test_1234", "e:   ${e.message}")
                Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getData() {
        try {
            var command = intent.extras!!.getString("Command")
            val text = intent.extras!!.getString("Text")
            Log.d("firebase_1234", "data1111: ${(command == null)}   ${text} gyhu ")
            if (command != null) {
                setContetnt(command)
            }else {
                getCommand(this).observe(this, Observer { it->
                    command = it.toString()
                })
                Log.d("firebase_1234", "service:1111 ${command}")
            }


        } catch (e: Exception) {
            Log.d("firebase_1234", "Exception: ${e.message}")
            try {
                var command:String = ""
                    getCommand(this).observe(this, Observer { it->
                    command = it.toString()
                })
                Log.d("firebase_1234", "service:1111 ${command}")
//                setContetnt(Signlton.ops)
            } catch (e1: Exception) {
                Log.d("firebase_1234", "Exception:1111 ${e.message}")

            }
        }
    }

    private fun setContetnt(command: String?) {
        Log.d("firebase_12345", "data: ${command} ")
        if (command.equals(BLACK)) {
            container.setBackgroundColor(resources.getColor(R.color.black, null))
        } else if (command.equals(COLOR)) {
            val colors = arrayOf(
                R.color.Aqua,
                R.color.Aquamarine,
                R.color.BlueViolet,
                R.color.Brown,
                R.color.Chocolate,
                R.color.Cyan
            ).random()
            container.setBackgroundColor(resources.getColor(colors, null))
        } else {
            container.setBackgroundColor(resources.getColor(R.color.white, null))
        }

    }

    private fun setUpFirebase() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.d("firebase_123", "notsuccess: ${task.exception!!.message}")
                    return@OnCompleteListener
                }

                token = task.result?.token
                val msg = getString(R.string.msg_token_fmt, token)

                Log.d("firebase_123", "oldtoken: ${token}\n${Utiles.getPrefsName(this)}")
                if (Utiles.getPrefsName(this) != null) {
                    if (Utiles.getPrefsToken(this).equals(token)) {

                    }else {
                        Log.d("firebase_123", "newtoken: ${token}")
                        Utiles.setPrefs(this, Utiles.getPrefsName(this)!!, token)
                        val model = Model(token!!)
                        db.collection("users").document(Utiles.getPrefsName(this)!!).set(model).addOnSuccessListener{

                        }
                    }
                }
                Log.d("test_123", "tokentest: ${msg}")
            })
    }

   private var brHandler = object : BroadcastReceiver() {
       override fun onReceive(context: Context?, intent: Intent?) {

           val command = intent?.getStringExtra(FROMSERIVCE)
           setContetnt(command)
       }

   }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brHandler)
    }
}
