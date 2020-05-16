package com.example.zeitenrechner

import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // Berechnung der Zeit im Format hh:mm:ss
    fun calcTime(pace: String, distance: String): String {
        var d = distance.replace("m", "")
        var mPace = pace.substring(0..1).toDouble()
        var sPace = pace.substring(3..4).toDouble()
        var hTime = (((mPace * 60 + sPace) / (1000 / d.toDouble())) / 3600).toInt()
                            .toString()
        var mTime = ((((mPace * 60 + sPace) / (1000 / d.toDouble())) / 60) -
                            (hTime.toInt() * 60)).toInt().toString()
        var sTime = (((mPace * 60 + sPace) / (1000 / d.toDouble())) -
                            mTime.toInt() * 60).toInt().toString()
        if (sTime.length > 2) {
            //bei Strecken ab 10.000m alle Zahlen bis auf die letzten beiden Abschneiden
            sTime = sTime.substring(sTime.length - 2..sTime.length - 1)
        }
        if (hTime.length == 1) {
            hTime = "0$hTime"
        }
        if (mTime.length == 1) {
            mTime = "0$mTime"
        }
        if (sTime.length == 1) {
            sTime = "0$sTime"
        }
        var time = "$hTime:$mTime:$sTime"
        return time
    }

    // Berechnung der Distanz in Meter
    fun calcDistance(pace: String, time: String): Int {
        var hTime = time.substring(0..1).toDouble()
        var mTime = time.substring(3..4).toDouble()
        var sTime = time.substring(6..7).toDouble()
        var t = (hTime * 3600 + mTime * 60 + sTime)
        var m = pace.substring(0..1).toDouble()
        var s = pace.substring(3..4).toDouble()
        var distance = (t.toDouble() * 1000 / (m * 60 + s)).toInt()
        return distance
    }

    // Berechnung der Pace in km/h
    fun calcPaceKmH(time: String, distance: String): String {
        var hTime = time.substring(0..1).toDouble()
        var mTime = time.substring(3..4).toDouble()
        var sTime = time.substring(6..7).toDouble()
        var t = (hTime * 3600 + mTime * 60 + sTime)
        var d = distance.replace("m", "")
        var pace = (d.toDouble() / t.toDouble() * 3.6).roundToInt().toString()
        return pace
    }

    // Berechnung der Pace in min/km
    fun calcPaceMinKm(time: String, distance: String): String {
        var hTime = time.substring(0..1).toDouble()
        var mTime = time.substring(3..4).toDouble()
        var sTime = time.substring(6..7).toDouble()
        var t = (hTime * 3600 + mTime * 60 + sTime)
        var d = distance.replace("m", "")
        var m = ((t.toDouble() / d.toDouble()) * 16.66666666667).toInt().toString()
        var s = (Math.round(((((t.toDouble() / d.toDouble()) * 16.66666666667) % 1)
                        / 0.16666667) * 10.0) / 10.0).toString()
        if (m.length == 1) {
            var pace = "0$m:$s"
            return pace
        } else {
            var pace = "$m:$s"
            return pace
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        var db = DataBaseHandler(context)

        btnSaveData.setOnClickListener {
            if(enterName.text.toString().length > 0 && enterTime.text.toString().length > 0 && enterDistance.text.toString().length > 0 && enterPaceKmH.text.toString().length > 0 && enterPaceMinKm.text.toString().length > 0) {
                var data = Data(enterName.text.toString(), enterTime.text.toString(), enterDistance.text.toString(), enterPaceMinKm.text.toString(), enterPaceKmH.text.toString())
                db.insertData(data)
            } else {
                Toast.makeText(context, "Bitte die leeren Felder füllen!", Toast.LENGTH_SHORT).show()
            }
        }

        btnRead.setOnClickListener {
            var data = db.readData()
            tvResult.text = ""
            for (i in 0..(data.size-1)) {
                tvResult.append(data.get(i).id.toString() + " " + data.get(i).name + " " + data.get(i).distance + " " + data.get(i).time + " " + data.get(i).paceM + " " + data.get(i).paceH + "\n")
            }
        }

        btnUpdate.setOnClickListener {
            if(enterName.text.toString().length > 0 && enterTime.text.toString().length > 0 && enterDistance.text.toString().length > 0 && enterPaceKmH.text.toString().length > 0 && enterPaceMinKm.text.toString().length > 0) {
                var data = Data(enterName.text.toString(), enterTime.text.toString(), enterDistance.text.toString(), enterPaceMinKm.text.toString(), enterPaceKmH.text.toString())
                db.updateData(data)
            } else {
                Toast.makeText(context, "Bitte die leeren Felder füllen!", Toast.LENGTH_SHORT)
                    .show()
            }
            btnRead.performClick()
        }

        btnDelete.setOnClickListener {
            if(enterName.text.toString().length > 0) {
                var data = Data(enterName.text.toString())
                db.deleteData(data)
                Toast.makeText(context, "delete sucessfull", Toast.LENGTH_SHORT)
                    .show()
                btnRead.performClick()
            } else {
                Toast.makeText(context, "please fill a name", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnCompute.setOnClickListener {
            try {
                var emptyDistance = enterDistance.text.toString()
                var emptyTime = enterTime.text.toString()
                var emptyPaceKmH = enterPaceKmH.text.toString()
                var emptyPaceMinKm = enterPaceMinKm.text.toString()

                if (emptyTime.equals("")) {
                    try {
                        var paceminkm = enterPaceMinKm.text.toString()
                        var distance = enterDistance.text.toString()
                        var time = calcTime(paceminkm, distance)
                        enterTime.setText(
                            time.toString().replace(".", "")
                                    + " hh:min:sec"
                        )
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Fehler: Bitte das richtige Format eingeben!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                if (emptyDistance.equals("")) {
                    try {
                        var paceminkm = enterPaceMinKm.text.toString()
                        var time = enterTime.text.toString()
                        var distance = calcDistance(paceminkm, time)
                        enterDistance.setText(distance.toString() + " m")
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Fehler: Bitte das richtige Format eingeben!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                if (emptyPaceMinKm.equals("")) {
                    try {
                        var distance = enterDistance.text.toString()
                        var time = enterTime.text.toString()
                        var paceminkm = calcPaceMinKm(time, distance)
                        var pacekmh = calcPaceKmH(time, distance)
                        enterPaceMinKm.setText(paceminkm.toString().replace(".", "")
                                + " min/km")
                        enterPaceKmH.setText(pacekmh.toString() + " km/h")
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Fehler: Bitte das richtige Format eingeben!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                if (emptyPaceKmH.equals("")) {
                    try {
                        var distance = enterDistance.text.toString()
                        var time = enterTime.text.toString()
                        var pacekmh = calcPaceKmH(time, distance)
                        var paceminkm = calcPaceMinKm(time, distance)
                        enterPaceKmH.setText(pacekmh.toString() + " km/h")
                        enterPaceMinKm.setText(paceminkm.toString().replace(".", "")
                                + " min/km")
                    } catch (e: NumberFormatException) {
                        Toast.makeText(
                            this, "Fehler: Bitte das richtige Format eingeben!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                Toast.makeText(this, "Bitte zwei Felder befüllen", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
