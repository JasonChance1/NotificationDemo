package com.example.notificationdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.blankj.utilcode.util.ToastUtils
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
    private val customId = "1004"
    private val mCustomNotificationId = "1005"
    private val mCustomChannelId = "自定义渠道id"
    private val mCustomChannelName = "自定义通知"
    companion object{
        private const val mStopAction = "com.example.stop" // 暂停继续action
        private const val mDoneAction = "com.example.done" // 完成action
        private var mFlag = 0
        private var mIsStop = false // 是否在播放 默认未开始
        private const val mCustomNotificationId = 9006 // 通知id
    }
    private var mReceiver: NotificationReceiver? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createReceiver()
        initView()
        bindEvent()
    }


    private fun initView() {
        val permissionUtil = PermissionUtil()
        permissionUtil.requestCameraPermission()
        if(!permissionUtil.isExternalStoragePermission()){
            permissionUtil.requestExternalStoragePermission()
        }

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

        binding.custom.setOnClickListener {
            createNotificationForCustom()
        }

        val mediaPlayer = MediaPlayer.create(this, R.raw.msc)

        if (mediaPlayer != null) {
            binding.play.setOnClickListener {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    binding.play.text = "播放音乐"
                } else {
                    mediaPlayer.start()
                    binding.play.text = "暂停"
                }
                ToastUtils.showLong("点击")
            }
        } else {
            // 处理创建MediaPlayer失败的情况
            ToastUtils.showLong("无法创建MediaPlayer")
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


    private fun bigText() {
        val bigText =
            "A notification is a message that Android displays outside your app's UI to provide the user with reminders, communication from other people, or other timely information from your app. Users can tap the notification to open your app or take an action directly from the notification."

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel =
                NotificationChannel(bigTextId, "大文本", NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }
        val intent = Intent(this, ContentActivity::class.java)
        intent.putExtra("data", bigText)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        mBuilder = NotificationCompat.Builder(this, bigTextId)
            .setContentTitle("大文本")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon1))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        mManager.notify(bigTextId.toInt(), mBuilder.build())
    }


    private fun imgNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel =
                NotificationChannel(imgId, "大图通知", NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }


        mBuilder = NotificationCompat.Builder(this, imgId)
            .setContentTitle("大图通知")
            .setContentText("展开看啊看")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(BitmapFactory.decodeResource(resources, R.drawable.ic_big_pic))
            )
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon1))

        mManager.notify(imgId.toInt(), mBuilder.build())

    }

    private fun difStyle() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanel =
                NotificationChannel(imgId, "大图通知", NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(chanel)
        }


        mBuilder = NotificationCompat.Builder(this, imgId)
            .setContentTitle("大图通知")
            .setContentText("展开看啊看")
//            .setStyle(NotificationCompat.InboxStyle().setBigContentTitle("大盒子").addLine("cs").setSummaryText("summaryText"))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.icon1)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.large_icon1))

        mManager.notify(imgId.toInt(), mBuilder.build())

    }
    /**
     * 自定义通知
     */
    private fun createNotificationForCustom() {
        // 适配8.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(mCustomChannelId, mCustomChannelName, NotificationManager.IMPORTANCE_DEFAULT)
            mManager.createNotificationChannel(channel)
        }

        // 适配12.0及以上
        mFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // 添加自定义通知view
        val views = RemoteViews(packageName, R.layout.layout_notification)

        // 添加暂停继续事件
        val intentStop = Intent(mStopAction)
        val pendingIntentStop = PendingIntent.getBroadcast(this, 0, intentStop, mFlag)
        views.setOnClickPendingIntent(R.id.btn_stop, pendingIntentStop)

        // 添加完成事件
        val intentDone = Intent(mDoneAction)
        val pendingIntentDone = PendingIntent.getBroadcast(this, 0, intentDone, mFlag)
        views.setOnClickPendingIntent(R.id.btn_done, pendingIntentDone)

        // 创建Builder
        mBuilder = NotificationCompat.Builder(this, mCustomChannelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon1))
            .setAutoCancel(true)
            .setCustomContentView(views)
            .setCustomBigContentView(views)// 设置自定义通知view

        // 发起通知
        mManager.notify(mCustomNotificationId.toInt(), mBuilder.build())
    }
    /**
     * 创建广播接收器
     */
    private fun createReceiver() {
        val intentFilter = IntentFilter()
        // 添加接收事件监听
        intentFilter.addAction(mStopAction)
        intentFilter.addAction(mDoneAction)
        mReceiver = NotificationReceiver()
        // 注册广播
        registerReceiver(mReceiver, intentFilter)
    }
    /**
     * 更新自定义通知View
     */
    private fun updateCustomView() {
        val views = RemoteViews(packageName, R.layout.layout_notification)
        val intentUpdate = Intent(mStopAction)
        val pendingIntentUpdate = PendingIntent.getBroadcast(this, 0, intentUpdate, mFlag)
        views.setOnClickPendingIntent(R.id.btn_stop, pendingIntentUpdate)
        // 根据状态更新UI
        if (mIsStop) {
            views.setTextViewText(R.id.tv_status, "那些你很冒险的梦-停止播放")
            views.setTextViewText(R.id.btn_stop, "继续")
            binding.mbUpdateCustom.text = "继续"

        } else {
            views.setTextViewText(R.id.tv_status, "那些你很冒险的梦-正在播放")
            views.setTextViewText(R.id.btn_stop, "暂停")
            binding.mbUpdateCustom.text = "暂停"
        }

        mBuilder.setCustomContentView(views).setCustomBigContentView(views)
        // 重新发起通知更新UI，注意：必须得是同一个通知id，即mCustomNotificationId
        mManager.notify(mCustomNotificationId.toInt(), mBuilder.build())
    }

    private class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 拦截接收事件
            if (intent.action == mStopAction) {
                // 改变状态
                mIsStop = !mIsStop
                val activity = context as? MainActivity
                activity?.updateCustomView()
            } else if (intent.action == mDoneAction) {
                Toast.makeText(context, "完成", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
