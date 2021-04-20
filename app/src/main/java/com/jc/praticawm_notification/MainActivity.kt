package com.jc.praticawm_notification

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        findViewById<EditText>(R.id.editFecha).setOnClickListener(){
            showDatePickerDialog()
        }

        findViewById<EditText>(R.id.editHora).setOnClickListener(){
            showTimePickerDialog()
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener(){
            Log.i("[PASO]", "1 OK")
            var Msg :String = ""
            //setOnetimeRequestNotify()
            if (findViewById<EditText>(R.id.editFecha).text.isEmpty()){
                Msg = "- Indicar la fecha \n"
            }
            if (findViewById<EditText>(R.id.editHora).text.isEmpty()){
                Msg += "- Indicar el horario \n"
            }
            if (findViewById<EditText>(R.id.txtMsg).text.isEmpty()){
                Msg += "- Indicar el mensaje \n"
            }
            if (Msg.isEmpty()) {
                /*Log.i("[PASOX]","Notificacion 1")
                val temporizador = object : CountDownTimer(15000, 1000){
                    override fun onTick(millisUntilFinished: Long) { Log.i("[PASOX]","TICK")}
                    override fun onFinish() { setOnetimeRequestNotify()
                        Log.i("[PASOX]","Notificacion enviada2")
                    }
                }

                temporizador.start()*/

                if (setOnetimeRequestNotify())
                    Toast.makeText(this, "Notificación Preparada ", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Error: \n" + Msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setOnetimeRequestNotify () : Boolean{
        // Este va a seervir para inicializar el Worker Manager, para ejecutar la operación

        val hoy = Date()
        val msg = findViewById<EditText>(R.id.txtMsg).text?: ""
        val aux2a = findViewById<EditText>(R.id.editFecha).text.split("/")
        val aux3a = findViewById<EditText>(R.id.editHora).text.split(":")

        val futuro = Date(aux2a[2].toInt() -1900, aux2a[1].toInt()-1, aux2a[0].toInt(), )
        futuro.hours = aux3a[0].toInt()
        futuro.minutes = aux3a[1].toInt()

        val difTiempo = (futuro.time - hoy.time) / 1000
        if (difTiempo < 900) {
            Toast.makeText(this, "El tiempo del recordatorio debe ser almenos de 15 minutos ", Toast.LENGTH_LONG).show()
            return false
        }

        Log.i("[PASO]", "x001 Hoy[" + hoy.time.toString() + "]")
        Log.i("[PASO]", "x002 Fut[" + futuro.time.toString() + "]")
        Log.i("[PASO]", "x003 Dif[" + difTiempo.toString() + "]")

        val wm = WorkManager.getInstance(applicationContext)
        val miMsg: Data = Data.Builder()
            .putString("msg", "[$msg]")
            .build() // Para construir el objeto

        Log.i("[PASO]", "[2A enviado notify]")

        val uploadRequest2 = PeriodicWorkRequest.Builder(uploadNotificacion::class.java, difTiempo, TimeUnit.SECONDS)
            .setInputData(miMsg)
            .build()

        Log.i("[PASO]", "[2b enviado notify]")

        // cancel the unique work
        wm.cancelAllWork()

        //wm.enqueue(uploadRequest2)

        // clear all finished or cancelled tasks from the WorkManager
        wm.enqueueUniquePeriodicWork("com.jc.praticawm_notification", ExistingPeriodicWorkPolicy.KEEP, uploadRequest2)

        //Para regresar una instancia del WorkM..
        wm.getWorkInfoByIdLiveData(uploadRequest2.id).observe(this, Observer {
            findViewById<TextView>(R.id.texto1).text = it.state.name
            Log.i("[PASO]", it.state.name.toString())
        })
        return true
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate =
                day.dosDigitos() + "/" + (month + 1).dosDigitos() + "/" + year.dosDigitos()
            findViewById<EditText>(R.id.editFecha).setText(selectedDate.toString())
        })
        newFragment.show(supportFragmentManager, "datePicker")
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePickerFragment.newInstance(TimePickerDialog.OnTimeSetListener { _, hora, minuto ->
            val horaMin = hora.dosDigitos() + ":" + minuto.dosDigitos()
            findViewById<EditText>(R.id.editHora).setText(horaMin.toString())
        })
        newFragment.show(supportFragmentManager, "timePicker")
    }

    //extension function para adjuntar el método dosDigitos sobre la clase Int:
    fun Int.dosDigitos() =
        if (this <= 9) "0$this" else this.toString()

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name ="NotificacionAppDemo"
            val descriptionText = "Notificaciones desde la App de Practica"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("com.jc.praticawm_notification", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}