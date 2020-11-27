package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.DATE_FORMATE_TO_CARD
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.DATE_FORMATE_TO_DB
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun setPic(view: View, path: String) {
        /*val targetW = view.width
        val targetH = view.height
        if (targetH < 1 || targetW < 1)
            return*/
        val bmpOptions = BitmapFactory.Options()
        bmpOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmpOptions)
        /*val photoW = bmpOptions.outWidth
        val photoH = bmpOptions.outHeight
        val scale = min(photoW / targetW, photoH / targetH)
        bmpOptions.inSampleSize = scale*/
        bmpOptions.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(path, bmpOptions)
        when {
            view is ImageView -> (view as ImageView).setImageBitmap(bitmap)
            else -> view.background = BitmapDrawable(view.resources, bitmap)
        }
    }

    fun setImgFromAsset(mImageView: ImageView, strName: String) {
        val assetManager = mImageView.context.assets
        try {
            val istr = assetManager.open(strName)
            val d = Drawable.createFromStream(istr, null)
            mImageView.setImageDrawable(d)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToDatetime(date: Date) : String {
        return SimpleDateFormat(DATE_FORMATE_TO_DB).format(Date(date.time))
    }

    @SuppressLint("SimpleDateFormat")
    fun convertToDate(date: String) : Date {
        return SimpleDateFormat(DATE_FORMATE_TO_DB).parse(date)!!
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToStrCard(date: Date) : String {
        return SimpleDateFormat(DATE_FORMATE_TO_CARD).format(Date(date.time))
    }

}