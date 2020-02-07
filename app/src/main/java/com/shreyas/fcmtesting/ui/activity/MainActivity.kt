package com.shreyas.fcmtesting.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.shreyas.fcmtesting.model.Model
import com.shreyas.fcmtesting.R
import com.shreyas.fcmtesting.model.Content
import com.shreyas.fcmtesting.ui.fragment.ContentFragment
import com.shreyas.fcmtesting.utiels.getCommand
import com.shreyas.fcmtesting.utiels.Utiles
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    var token: String? = null
    var name: String? = null
    var deviceId: String? = null


    companion object {
        val BLACK = "Black"
        val WHITE = "White"
        val COLOR = "Color"
        val FROMSERIVCE = "fromIntent"
        val FROMSERIVCECONTENT = "fromIntentContent"
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(brHandler, IntentFilter("com.shreyas.fcmtesting_FCM"))
        db = FirebaseFirestore.getInstance()
        setUpFirebase()

        Log.d("test_12345", "da: ${(deviceId)}")

        try {
            db.collection("users").document(deviceId!!).get()
                .addOnSuccessListener { docRef ->
                    if (docRef.exists()) {

                    } else {
                        showAlertDialog()
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("test_1232", "sda:  ${e.message}")

                }
        } catch (e: Exception) {
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

            Log.d(
                "test12345",
                "Sda:  " + editText.text.toString().matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+\$".toRegex())
            )

            name = editText.text.toString().trim()

            if (name!!.isEmpty()) {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (alphaRegex.matches(name!!)) {
                Toast.makeText(this, "valid", Toast.LENGTH_SHORT).show()
//                checkFirebase(name!!, dialog)
                setUpDb(name!!, dialog)
            } else {

                Toast.makeText(this, "Name should be alpha numeric", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun checkFirebase(name: String, dialog: Dialog) {
//        db.collection("users").document(name).get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.exists()) {
//                Toast.makeText(this, "The user name already exists",Toast.LENGTH_SHORT).show()
//            }else {
//                setUpDb(name, dialog)
//            }
//        }
//    }

    //create a document of type string key=name and data to that document is token whick obtained from model class
    private fun setUpDb(name: String, dialog: Dialog) {
        val model = Model(name, token!!)

        db.collection("users").document(deviceId!!).set(model).addOnSuccessListener { docRefs ->
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
            Utiles.setPrefs(this, name, token)
            dialog.dismiss()
        }
            .addOnFailureListener { e ->
                Log.d("test_1234", "e:   ${e.message}")
                Toast.makeText(this, "Somthing went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getData() {
        try {
            var command = intent.extras!!.getString("Command")

            val text = intent.extras!!.getString("Text")
//            Log.d("firebase_1234", "data1111: ${contentDetatils.type}")
            if (command != null) {
                setCommand(command)
            } else {
                getCommand(this)
                    .observe(this, Observer { it ->
                        command = it.toString()
                    })
                Log.d("firebase_1234", "service:1111 ${command}")
            }

            val content = intent.extras!!.getString("Content")
            val contentDetatils: Content = Gson().fromJson(content, Content::class.java)
            if (contentDetatils != null) {
                setFragment(contentDetatils)
            }

        } catch (e: Exception) {
            Log.d("firebase_1234", "Exception: ${e.message}")
            try {
                var command: String = ""
                getCommand(this)
                    .observe(this, Observer { it ->
                        command = it.toString()
                    })
                Log.d("firebase_1234", "service:1111 ${command}")
//                setCommand(Signlton.ops)
            } catch (e1: Exception) {
                Log.d("firebase_1234", "Exception:1111 ${e.message}")

            }
        }
    }

    private fun setCommand(command: String?) {
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

                db.collection("users").document(deviceId!!).get()
                    .addOnSuccessListener { snap->
                        if (snap.exists()) {
                            val model = Model(snap.get("name").toString(), snap.get("token").toString())
                            Log.d("qwed", "asd: "+model.name)
                            Log.d("qwed", "asd: "+model.token)
                            Log.d("qwed", "asd: "+token)
                            if (!model.token.equals(token)) {
                                model.token = token!!
                                db.collection("users").document(deviceId!!).set(model)
                            }
                        }

                    }
                Log.d("test_123", "tokentest: ${msg}")
            })
    }

    private var brHandler = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val command = intent?.getStringExtra(FROMSERIVCE)
            setCommand(command)
            try {
                val content = intent?.getStringExtra(FROMSERIVCECONTENT)
                val contentDetails = Gson().fromJson<Content>(content, Content::class.java)
                if (contentDetails != null) {
                    setFragment(contentDetails)
                }
            } catch (e: Exception) {

            }

        }

    }

    private fun setFragment(contentDetails: Content?) {
        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.add(R.id.frag_contanier, ContentFragment(contentDetails))
        fragTransaction.commit()
    }

//    private fun setContent(contentDetails: Content?) {
//        val type = contentDetails?.type
//        Log.d("content_test_123", "ASd: "+type)
//        if (type.equals("image")) {
//            findViewById<ImageView>(R.id.iv_image).let { it ->
//                Glide.with(this).load(contentDetails?.url).into(it)
//            }
//        } else if (type.equals("gif")) {
//            findViewById<ImageView>(R.id.iv_image).let { it ->
//                Glide.with(this).load(contentDetails?.url).into(it)
//            }
//        } else {
//            Log.d("content_test_123", "ASd:video:    "+type)
//            setUpvideoWindow(contentDetails)
//        }
//
//
//    }

//    private fun setUpvideoWindow(contentDetails: Content?) {
//        try {
//            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            val inflater =
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val view = inflater.inflate(R.layout.exoplayer, null)
//            val params: WindowManager.LayoutParams
//            setView(view, wm, contentDetails)
//            if (Build.VERSION.SDK_INT >= 26) {
//                params = WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT
//                )
//            } else {
//                params = WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_PHONE,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT
//                )
//            }
//            params.gravity = Gravity.CENTER or Gravity.CENTER
//            params.x = 0
//            params.y = 0
//            Log.d("ttt", "Sad: " + params.gravity)
//            wm.addView(view, params)
//        }catch (e:Exception) {
//            Log.d("content_test_123", "Exception: " + e.message)
//        }
//
//    }

//    private fun setView(view: View?, wm: WindowManager, contentDetails: Content?) {
//        val mIvClose:ImageView? = view?.findViewById(R.id.iv_close)
//
//        var playerView: PlayerView? = view?.findViewById(R.id.exoplayer)
//        var exoplayer: SimpleExoPlayer? = null
//        var playbackStateBuilder : PlaybackStateCompat.Builder? = null
//        var mediaSession: MediaSessionCompat? = null
//
//        try {
//            mIvClose?.setOnClickListener {
//                wm.removeView(view)
//                exoplayer?.release()
//            }
//
//            exoplayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
//            playerView?.player = exoplayer
//
//            val userAgent = Util.getUserAgent(this, "exo")
//            Log.d("content_test_123", "sad: ${contentDetails?.url}")
//            val mediaUri = Uri.parse("https://userengagement.s3.amazonaws.com/assets/big_buck_bunny.mp4")
////        val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, userAgent)).createMediaSource(mediaUri)
//            val mediaSource = ExtractorMediaSource(
//                mediaUri,
//                DefaultDataSourceFactory(baseContext, userAgent),
//                DefaultExtractorsFactory(),
//                null,
//                null
//            )
//
//            exoplayer.prepare(mediaSource)
//
//            val componentName = ComponentName(baseContext, "Exo")
//            mediaSession = MediaSessionCompat(baseContext, "ExoPlayer", componentName, null)
//
//            playbackStateBuilder = PlaybackStateCompat.Builder()
//            playbackStateBuilder?.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or
//                    PlaybackStateCompat.ACTION_FAST_FORWARD)
//            mediaSession?.setPlaybackState(playbackStateBuilder?.build())
//            mediaSession?.isActive = true
//
//            playerView?.player?.playWhenReady = true
//        }catch (e:Exception) {
//            Log.d("content_test_123", "ew: ${e.message}")
//        }
//
//    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brHandler)
    }
}
