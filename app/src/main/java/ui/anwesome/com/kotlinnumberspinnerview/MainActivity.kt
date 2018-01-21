package ui.anwesome.com.kotlinnumberspinnerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ui.anwesome.com.numberspinnerview.NumberSpinnerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var view = NumberSpinnerView.create(this)
        view.addOnNumberSelectedListener {
            Toast.makeText(this,"number selected $it",Toast.LENGTH_SHORT).show()
        }
    }
}
