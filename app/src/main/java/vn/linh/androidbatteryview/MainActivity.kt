package vn.linh.androidbatteryview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_charge.setOnClickListener {
            battery_view.charge()
        }

        button_uncharge.setOnClickListener {
            battery_view.unCharge()
        }

        button_increase_battery.setOnClickListener {
            battery_view.setPercent(battery_view.getPercent() + 10)
        }
    }
}
