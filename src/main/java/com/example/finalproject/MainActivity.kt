package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : AppCompatActivity() {
    private var slide_direction = "left"
    private var ad : InterstitialAd? = null
    private lateinit var board : ChessView
    private var gameStarted : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chess_directions)
        var board = ChessView()
        ChessView.CONTEXT = this
        ChessView.ACTIVITY = this
        var adUnitId : String = // "ca-app-pub-3940256099942544/6300978111"
            "ca-app-pub-3940256099942544/1033173712"
        var adRequest : AdRequest = (AdRequest.Builder( )).build( )
        var adLoad : AdLoad = AdLoad( )
        InterstitialAd.load( this, adUnitId, adRequest, adLoad )
    }

    fun startGame(v: View?) {
        gameStarted = true
        var intent: Intent = Intent(this, ChessView::class.java)
        startActivity( intent )
        if (slide_direction == "left") {
            slide_direction = "right"
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_from_right)
        } else {
            slide_direction = "left"
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_from_left)
        }

    }

    fun checkDirections(v: View?) {
        setContentView(R.layout.chess_directions)
    }


    fun findStats(v: View?) {
        setContentView(R.layout.statistics_page)

        var pref : SharedPreferences = this.getSharedPreferences( this.packageName + "_preferences",
            Context.MODE_PRIVATE )
        var whiteText : TextView = findViewById(R.id.secondMessageValue)
        whiteText.text = "" + pref.getInt(ChessView.WHITE_PIECES_TAKEN, 0)
        var blackText : TextView = findViewById(R.id.thirdMessageValue)
        blackText.text = "" + pref.getInt(ChessView.BLACK_PIECES_TAKEN, 0)
        var whiteVicText : TextView = findViewById(R.id.fourthMessageValue)
        whiteVicText.text = "" + pref.getInt(ChessView.STORED_WHITE_WINS, 0)
        var blackVicText : TextView = findViewById(R.id.fifthMessageValue)
        blackVicText.text = "" + pref.getInt(ChessView.STORED_BLACK_WINS, 0)
    }

    fun sendEmail(v: View?) {
        val recipient = "dmille18@terpmail.umd.edu"
        val subject = "App Feedback"
        val body = "Your email body goes here"

        val rBar = findViewById<RatingBar>(R.id.ratingBar)
        val msg = rBar.rating.toString()

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Check out the Chess app! I give this app a " + msg + " star rating")
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"))
            startActivity(intent)
        }
    }

    inner class AdLoad : InterstitialAdLoadCallback( ) {
        override fun onAdFailedToLoad(p0: LoadAdError) {
            super.onAdFailedToLoad(p0)
            Log.w( "MainActivity", "ad failed to load" )
        }

        override fun onAdLoaded(p0: InterstitialAd) {
            super.onAdLoaded(p0)
            Log.w( "MainActivity", "ad loaded" )
            // show the ad
            ad = p0
            ad!!.show( this@MainActivity )

            // handle user interaction with the ad
            var manageAd : ManageAdShowing = ManageAdShowing()
            ad!!.fullScreenContentCallback = manageAd
        }
    }

    inner class ManageAdShowing : FullScreenContentCallback( ) {
        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.w( "MainActivity", "user closed the ad" )
            // some code here to continue the app
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.w( "MainActivity", "User clicked on the ad" )
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.w( "MainActivity", "user has seen the ad" )
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.w( "MainActivity", "ad has been shown" )
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            Log.w( "MainActivity", "ad failed to show" )
        }
    }
}