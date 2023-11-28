package com.example.notificationdemo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun checkAllPermission(permissions: List<String>): Boolean{
        for (permission in permissions){
            if(checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
                return false
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(
                        this,
                        "" + "权限" + permissions[i] + "申请失败，不能读取信息",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}