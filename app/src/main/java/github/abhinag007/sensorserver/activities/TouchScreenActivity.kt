package github.abhinag007.sensorserver.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import github.abhinag007.sensorserver.R
import github.abhinag007.sensorserver.service.SensorService
import github.abhinag007.sensorserver.service.ServiceBindHelper

class TouchScreenActivity : AppCompatActivity()
{
    private val TAG = "TouchScreenActivity"
    private var sensorService: SensorService? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_screen)

        val serviceBindHelper = ServiceBindHelper(
            context = applicationContext,
            service = SensorService::class.java,
            componentLifecycle = lifecycle
        )

        serviceBindHelper.onServiceConnected { binder ->

            val localBinder = binder as SensorService.LocalBinder
            sensorService = localBinder.service
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        event?.let {
            sensorService?.sendMotionEvent(it)
        }

        return super.onTouchEvent(event)
    }


}