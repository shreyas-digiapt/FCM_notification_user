//package com.shreyas.fcmtesting
//
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.graphics.Color
//import android.os.Build
//import android.os.IBinder
//import android.os.PowerManager
//import android.os.SystemClock
//import android.util.Log
//import android.widget.Toast
//import com.vdrop.calllimitkotline.Actions
//import com.vdrop.calllimitkotline.ServiceState
//import com.vdrop.calllimitkotline.setServiceState
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//
//
//class EndlessService : Service() {
//
////    private var wakeLock: PowerManager.WakeLock? = null
//    private var isServiceStarted = false
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        log("onStartCommand executed with startId: $startId")
//        if (intent != null) {
//            val action = intent.action
//            log("using an intent with action $action")
//            when (action) {
//                Actions.START.name -> startService(intent, startId)
//                Actions.STOP.name -> stopService()
//                else -> {
//                    log("This should never happen. No action in the received intent")
//                    startService(intent, startId)
//                    setServiceState(this, ServiceState.STARTED)
//                }
//            }
//        } else {
//            log(
//                "with a null intent. It has been probably restarted by the system."
//            )
//        }
//        // by returning this we make sure the service is restarted if the system kills the service
//        return START_STICKY
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        log("The service has been created".toUpperCase())
//        var notification = createNotification()
//        startForeground(1, notification)
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        log("The service has been destroyed".toUpperCase())
//        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun startService(intent: Intent, startId: Int) {
//        if (isServiceStarted) return
//        log("Starting the foreground service task")
//        Log.d("result_1234", "startService:")
//        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
//        isServiceStarted = true
//        setServiceState(this, ServiceState.STARTED)
//
//        // when phone sleeps
////        wakeLock =
////            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
////                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
////                    acquire()
////                }
////            }
//
//        // we are starting a loop in a coroutine
//        GlobalScope.launch(Dispatchers.IO) {
//            while (isServiceStarted) {
//                launch(Dispatchers.IO) {
//                    pingFakeServer(intent)
//                }
//                //initating the service depending upon delay. It plays a major role
////                delay(getStoreTime(this@EndlessService)+60000)
//                delay(100000)
//            }
//            log("End of the loop for the service")
//        }
//    }
//
//    private fun stopService() {
//        log("Stopping the foreground service")
//        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
//        try {
////            wakeLock?.let {
////                if (it.isHeld) {
////                    it.release()
////                }
////            }
//            stopForeground(true)
//            stopSelf()
//        } catch (e: Exception) {
//            log("Service stopped without being started: ${e.message}")
//        }
//        isServiceStarted = false
//        setServiceState(this, ServiceState.STOPPED)
//    }
//
//    private fun pingFakeServer(intent: Intent) {
//        detials()
//    }
//
//    private fun detials() {
//        Log.d("ccc","helper")
//
//        val br = BReciver()
//        Log.d("result_1234", "detials:")
//        var filter = IntentFilter()
//        filter.addAction(Intent.ACTION_USER_PRESENT)
//        filter.addAction("android.intent.action.BOOT_COMPLETED")
//        registerReceiver(br, filter)
//
////        val reciver = CallStateReciver()
////        val filter = IntentFilter()
//////        filter.addAction("android.intent.action.PHONE_STATE")
////        filter.addAction("android.intent.action.NEW_OUTGOING_CALL")
////        registerReceiver(reciver, filter)
////        val intent = Intent(this, CallStateReciver::class.java)
////        sendBroadcast(intent)
//
//
//    }
//
//    private fun createNotification(): Notification {
//        val notificationChannelId = "ENDLESS SERVICE CHANNEL"
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
//            val channel = NotificationChannel(
//                notificationChannelId,
//                "Endless Service notifications channel",
//                NotificationManager.IMPORTANCE_HIGH
//            ).let {
//                it.description = "Endless Service channel"
//                it.enableLights(true)
//                it.lightColor = Color.RED
//                it
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
//            PendingIntent.getActivity(this, 0, notificationIntent, 0)
//        }
//
//        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
//            this,
//            notificationChannelId
//        ) else Notification.Builder(this)
//
//        return builder
//            .setContentTitle("MYAPP is running")
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setTicker("Ticker text")
//            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
//            .build()
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent?) {
////        val restartServiceIntent = Intent(applicationContext, this.javaClass)
////        restartServiceIntent.setPackage(packageName)
////
////        val restartServicePendingIntent = PendingIntent.getService(
////            applicationContext,
////            1,
////            restartServiceIntent,
////            PendingIntent.FLAG_ONE_SHOT
////        )
////        val alarmService =
////            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
////        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] = restartServicePendingIntent
////        setServiceState(this, ServiceState.STARTED)
//        super.onTaskRemoved(rootIntent)
//       log("the serice is restored")
//    }
//
//}