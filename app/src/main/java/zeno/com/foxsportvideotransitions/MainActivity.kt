package zeno.com.foxsportvideotransitions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var customView : FoxSportView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customView = findViewById(R.id.fox_sport_view)
        customView.setOnClickListener({
            customView.start()
        })
    }
}
