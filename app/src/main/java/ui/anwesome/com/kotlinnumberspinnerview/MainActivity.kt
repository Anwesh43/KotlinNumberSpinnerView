package ui.anwesome.com.kotlinnumberspinnerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.numberspinnerview.NumberSpinnerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NumberSpinnerView.create(this)
    }
}
