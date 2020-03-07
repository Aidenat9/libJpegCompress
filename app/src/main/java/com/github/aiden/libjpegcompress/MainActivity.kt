package com.github.aiden.libjpegcompress

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.bither.util.NativeUtil
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    val REQUEST_PICK_IMAGE = 10011
    val REQUEST_KITKAT_PICK_IMAGE = 10012
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,permissions,110)
            else Toast.makeText(this,"app已有权限",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"系统小于6.0",Toast.LENGTH_LONG).show()
        }
    }

    fun pickFromGallery(v: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(
                Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                REQUEST_PICK_IMAGE
            )
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_KITKAT_PICK_IMAGE)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_PICK_IMAGE -> if (data != null) {
                    val uri = data.data
                    compressImage(uri!!)
                } else {
                    Log.e("======", "========图片为空======")
                }
                REQUEST_KITKAT_PICK_IMAGE -> if (data != null) {
                    val uri = ensureUriPermission(this, data)
                    compressImage(uri!!)
                } else {
                    Log.e("======", "====-----==图片为空======")
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun ensureUriPermission(context: Context, intent: Intent): Uri? {
        val uri = intent.data
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val takeFlags = intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                uri.let {
                    context.getContentResolver().takePersistableUriPermission(it!!, takeFlags)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        Log.e("===intent  ","=="+uri)
        return uri
    }


    fun compressImage(uri: Uri) {
        Log.e("===compressImage===", "====开始====uri==" + uri.path.toString())
        try {

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)


            val saveFile1 = File(externalCacheDir, "质量压缩.jpg")
            NativeUtil.compressImageToFile(bitmap, saveFile1)


            val saveFile2 = File(externalCacheDir, "尺寸压缩.jpg")
            NativeUtil.compressBitmapToFile(bitmap, saveFile2)

            Log.e("===compressImage===", "====uri==" + uri.toString())

            val saveFile3 = File(externalCacheDir, "采样率压缩.jpg")

            val f = File("/storage/sdcard0/DCIM/Camera/IMG_20161130_200251.jpg")
            if (f.exists()) {
                NativeUtil.compressBitmapSampleSize(f.getAbsolutePath(), saveFile3)
            } else {
                Log.e("===compressImage===", "采样率压缩找不到这个代码里面写死的图片哦~~~~")
            }
            val saveFile = File(externalCacheDir, "终极压缩.jpg")

            Log.e("===compressImage===", "====开始==压缩==saveFile==" + saveFile.getAbsolutePath())
            NativeUtil.compressBitmap(bitmap, saveFile.getAbsolutePath())
            Log.e("===compressImage===", "====完成==压缩==saveFile==" + saveFile.getAbsolutePath())

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
