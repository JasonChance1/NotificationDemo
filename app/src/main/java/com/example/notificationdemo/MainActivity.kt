package com.example.notificationdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.notificationdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var count = 0
    private lateinit var mManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder
    private val normalId = "1000"
    private val importantId = "1001"
    private val bigTextId = "1002"
    private val imgId = "1003"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        bindEvent()
    }


    private fun initView() {
        val permissionUtil = PermissionUtil()
        permissionUtil.requestCameraPermission()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionUtil.requestNotificationPermission()
        }
        permissionUtil.requestAlertPermission()

        mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindEvent() {
        binding.normal.setOnClickListener {
//            normalNotification()
            difStyle()
        }

        binding.important.setOnClickListener {
            importantNotification()
        }

        binding.bigText.setOnClickListener {
            bigText()
        }

        binding.bigPic.setOnClickListener {
            imgNotification()
        }
    }

    private fun normalNotification() {
        val intent = Intent(this, ContentActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(normalId, "普通通知", NotificationManager.IMPORTANCE_LOW)
        } else {
            return
        }
        mManager.createNotificationChannel(channel)
        mBuilder = NotificationCompat.Builder(this, normalId)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentText("普通通知")
            .setContentTitle("普通通知")
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.big_icon))

        mManager.notify(normalId.toInt(), mBuilder.build())

    }


    private fun importantNotification() {
        count++

        val intent = Intent(this, ContentActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(importantId, "重要通知", NotificationManager.IMPORTANCE_HIGH)
        } else {
            Toast.makeText(this, "sdfsdf", Toast.LENGTH_LONG).show()
            return
        }
        mManager.createNotificationChannel(channel)

        mBuilder = NotificationCompat.Builder(this, importantId)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.important_large_icon))
            .setSmallIcon(R.drawable.important_icon)
            .setAutoCancel(true)
            .setContentTitle("重要通知")
            .setContentText("重要通知内容")
            .setNumber(count)// 桌面通知最大数量
            .addAction(R.drawable.icon1, "去看看", pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE) // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏

        mManager.notify(importantId.toInt(), mBuilder.build())
    }


    private fun bigText(){
        val bigText =
            "A notification is a message that Android displays outside your app's UI to provide the user with reminders, communication from other people, or other timely information from your app. Users can tap the notification to open your app or take an action directly from the notification."

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(bigTextId,"大文本",NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }
        val intent = Intent(this, ContentActivity::class.java)
        intent.putExtra("data",bigText)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT )

        mBuilder = NotificationCompat.Builder(this,bigTextId)
            .setContentTitle("大文本")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.large_icon1))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        mManager.notify(bigTextId.toInt(),mBuilder.build())
    }


    private fun imgNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(imgId,"大图通知",NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }


        mBuilder = NotificationCompat.Builder(this,imgId)
            .setContentTitle("大图通知")
            .setContentText("展开看啊看")
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources,R.drawable.ic_big_pic)))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.large_icon1))

        mManager.notify(imgId.toInt(),mBuilder.build())

    }

    private fun difStyle(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(imgId,"大图通知",NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }


        mBuilder = NotificationCompat.Builder(this,imgId)
            .setContentTitle("大图通知")
            .setContentText("展开看啊看")
//            .setStyle(NotificationCompat.InboxStyle().setBigContentTitle("大盒子").addLine("cs").setSummaryText("summaryText"))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.large_icon1))

        mManager.notify(imgId.toInt(),mBuilder.build())

    }
}
