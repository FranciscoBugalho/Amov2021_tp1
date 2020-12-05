package pt.isec.amovtp1.grocerylistmanagement

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.DATE_FORMAT_TO_CARD
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.DATE_FORMAT_TO_DB
import pt.isec.amovtp1.grocerylistmanagement.data.Constants.TIME_FORMAT_TO_CARD
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    /**
     * setPic
     */
    fun setPic(view: View, path: String) {
        val bmpOptions = BitmapFactory.Options()
        bmpOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmpOptions)
        bmpOptions.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(path, bmpOptions)
        when (view) {
            is ImageView -> view.setImageBitmap(bitmap)
            else -> view.background = BitmapDrawable(view.resources, bitmap)
        }
    }

    /**
     * setImgFromAsset
     */
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

    /**
     * convertDateToDatetime
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateToDatetime(date: Date) : String {
        return SimpleDateFormat(DATE_FORMAT_TO_DB).format(Date(date.time))
    }

    /**
     * convertToDate
     */
    @SuppressLint("SimpleDateFormat")
    fun convertToDate(date: String) : Date {
        return SimpleDateFormat(DATE_FORMAT_TO_DB).parse(date)!!
    }

    /**
     * convertDateToStrCard
     */
    @SuppressLint("SimpleDateFormat")
    fun convertDateToStrCard(date: Date) : String {
        return SimpleDateFormat(DATE_FORMAT_TO_CARD).format(Date(date.time))
    }

    /**
     * convertTimeToStrCard
     */
    @SuppressLint("SimpleDateFormat")
    fun convertTimeToStrCard(date: Date) : String {
        return SimpleDateFormat(TIME_FORMAT_TO_CARD).format(Date(date.time))
    }

}