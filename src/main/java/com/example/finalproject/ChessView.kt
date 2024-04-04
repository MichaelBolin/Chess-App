package com.example.finalproject

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.button.MaterialButton


class ChessView : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    private lateinit var currContext : Context
    private var chessLogic : ChessLogic = ChessLogic()
    private lateinit var buttons : Array<Array<ImageButton>>
    private lateinit var activity : MainActivity
    private var activePiece : Int = -1
    var currPlayer : String = "White"
    private var whitePiecesSet : ArrayList<Int> = arrayListOf<Int>()
    private var blackPiecesSet : ArrayList<Int> = arrayListOf<Int>()
    private var activePiecei : Int = 0
    private var activePiecej : Int = 0
    lateinit var status : TextView
    lateinit var textBelowGrid : TextView
    private var slideDirection = "right"
    private lateinit var soundPool : SoundPool
    private var pickUpSound = 0
    private var victorySound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whitePiecesSet.add(R.id.Wpawn)
        // ... Add other initialization here or in buildBoard() method
        whitePiecesSet.add(R.id.Wpawn)
        whitePiecesSet.add(R.id.Wknight)
        whitePiecesSet.add(R.id.Wbishop)
        whitePiecesSet.add(R.id.Wqueen)
        whitePiecesSet.add(R.id.Wking)
        whitePiecesSet.add(R.id.Wrook)
        blackPiecesSet.add(R.id.Bpawn)
        blackPiecesSet.add(R.id.Bknight)
        blackPiecesSet.add(R.id.Bbishop)
        blackPiecesSet.add(R.id.Bqueen)
        blackPiecesSet.add(R.id.Bking)
        blackPiecesSet.add(R.id.Brook)

        var poolBuilder : SoundPool.Builder = SoundPool.Builder()
        soundPool = poolBuilder.setMaxStreams(4).build()
        pickUpSound = soundPool.load(this, R.raw.pick_up_piece, 1)
        victorySound = soundPool.load(this, R.raw.victory_sound, 1)


        currContext = this
        buildBoard(CONTEXT)
    }

    fun buildBoard(context: Context) {
        var width : Int  = Resources.getSystem().displayMetrics.widthPixels
        var w : Int = width / 8



        var gridLayout = GridLayout(context)
        gridLayout.setId(ViewCompat.generateViewId())
        gridLayout.columnCount = 8
        gridLayout.rowCount = 8
        var rowSpec : GridLayout.Spec = GridLayout.spec( 0, 8 )
        var columnSpec : GridLayout.Spec = GridLayout.spec( 0, 8 )
        var lp : GridLayout.LayoutParams = GridLayout.LayoutParams( rowSpec, columnSpec )
        status = TextView( context )
        status.setLayoutParams(lp)
        status.width = width
        status.height = 250
        status.gravity = Gravity.CENTER
        status.setBackgroundColor(Color.parseColor("#D2B48C"))
        status.textSize = 35.0f
        status.text = "$currPlayer's Turn"

        if(currPlayer == "White") status.setTextColor(Color.WHITE) else status.setTextColor(Color.BLACK)

        gridLayout.addView(status)


        buttons = Array<Array<ImageButton>>( 8, { i -> Array<ImageButton> ( 8, { j -> ImageButton( context ) } ) } )

        var handler : ButtonHandler = ButtonHandler()

        for( i in 0 .. buttons.size - 1 ) {
            for( j in 0 .. buttons[i].size - 1 ) {
                gridLayout.addView( buttons[i][j], w, w)
                if ((i + j)%2 == 1) buttons[i][j].setBackgroundColor(Color.BLACK) else buttons[i][j].setBackgroundColor(Color.WHITE)
                if ((i == 0 && j == 0) || (i == 0 && j == 7)) {
                    buttons[i][j].setImageResource(R.drawable.b_rook_1x)
                    buttons[i][j].id = R.id.Brook
                } else if ((i == 7 && j == 0) || (i == 7 && j == 7)) {
                    buttons[i][j].setImageResource(R.drawable.w_rook_1x)
                    buttons[i][j].id = R.id.Wrook
                } else if ((i == 0 && j == 1) || (i == 0 && j == 6)) {
                    buttons[i][j].setImageResource(R.drawable.b_knight_1x)
                    buttons[i][j].id = R.id.Bknight
                } else if ((i == 7 && j == 1) || (i == 7 && j == 6)) {
                    buttons[i][j].setImageResource(R.drawable.w_knight_1x)
                    buttons[i][j].id = R.id.Wknight
                } else if ((i == 0 && j == 2) || (i == 0 && j == 5)) {
                    buttons[i][j].setImageResource(R.drawable.b_bishop_1x)
                    buttons[i][j].id = R.id.Bbishop
                } else if ((i == 7 && j == 2) || (i == 7 && j == 5)) {
                    buttons[i][j].setImageResource(R.drawable.w_bishop_1x)
                    buttons[i][j].id = R.id.Wbishop
                } else if (i == 0 && j == 3) {
                    buttons[i][j].setImageResource(R.drawable.b_king_1x)
                    buttons[i][j].id = R.id.Bking
                } else if (i == 7 && j == 4) {
                    buttons[i][j].setImageResource(R.drawable.w_king_1x)
                    buttons[i][j].id = R.id.Wking
                } else if (i == 0 && j == 4) {
                    buttons[i][j].setImageResource(R.drawable.b_queen_1x)
                    buttons[i][j].id = R.id.Bqueen
                } else if (i == 7 && j == 3) {
                    buttons[i][j].setImageResource(R.drawable.w_queen_1x)
                    buttons[i][j].id = R.id.Wqueen
                } else if (i == 1) {
                    buttons[i][j].setImageResource(R.drawable.b_pawn_1x)
                    buttons[i][j].id = R.id.Bpawn
                } else if (i == 6) {
                    buttons[i][j].setImageResource(R.drawable.w_pawn_1x)
                    buttons[i][j].id = R.id.Wpawn
                } else {
                    buttons[i][j].id = R.id.empty
                }
                buttons[i][j].scaleType = ImageView.ScaleType.FIT_XY
                buttons[i][j].setOnClickListener( handler )
            }
        }


        val button = MaterialButton(context)
        val button2 = MaterialButton(context)
        val button3 = MaterialButton(context)
        val dummyButton = MaterialButton(context)
        button.setId(ViewCompat.generateViewId())
        button2.setId(ViewCompat.generateViewId())
        button3.setId(ViewCompat.generateViewId())
        dummyButton.setId(ViewCompat.generateViewId())
        button.text = "GAME RULES"
        button2.text = "STATISTICS"
        button3.text = "RESET GAME"
        dummyButton.text = ""
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
        button2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);
        button3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f);

        button.setBackgroundColor(Color.BLACK)
        button2.setBackgroundColor(Color.BLACK)
        button3.setBackgroundColor(Color.BLACK)
        dummyButton.setBackgroundColor(Color.parseColor("#D2B48C"))
        button.setTextColor(Color.WHITE)
        button2.setTextColor(Color.WHITE)
        button3.setTextColor(Color.WHITE)




        relativeLayout = RelativeLayout(context)
        relativeLayout.setBackgroundColor(Color.parseColor("#D2B48C"))

        relativeLayout.addView(gridLayout)

        textBelowGrid = TextView( context )
        textBelowGrid.setId(ViewCompat.generateViewId())
        val textParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.removeRule(RelativeLayout.BELOW)
        textParams.addRule(RelativeLayout.BELOW, gridLayout.id) // Position below GridLayout
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        textBelowGrid.width = width
        textBelowGrid.height = 200
        textBelowGrid.gravity = Gravity.CENTER
        textBelowGrid.setBackgroundColor(Color.parseColor("#D2B48C"))
        textBelowGrid.textSize = 40.0f
        textBelowGrid.text = ""
        textBelowGrid.layoutParams = textParams
        relativeLayout.addView(textBelowGrid)




        val buttonParams3 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams3.removeRule((RelativeLayout.BELOW))
        buttonParams3.addRule(RelativeLayout.BELOW, textBelowGrid.id) // Position below game over text
        buttonParams3.addRule(RelativeLayout.CENTER_HORIZONTAL) // Center horizontally

        val dummyButtonParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        dummyButtonParams.addRule(RelativeLayout.BELOW, button3.id) // Position below game over text
        dummyButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL) // Center horizontally
        dummyButtonParams.width = 10
        dummyButtonParams.height = 10

        val buttonParams2 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams2.addRule(RelativeLayout.BELOW, button3.id) // Position stats below Reset
        buttonParams2.addRule((RelativeLayout.LEFT_OF), dummyButton.id)

        val buttonParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        buttonParams.addRule(RelativeLayout.BELOW, button3.id) // Position right of stats button
        buttonParams.addRule((RelativeLayout.RIGHT_OF), dummyButton.id)
        buttonParams.setMargins(20,40,20,0)
        buttonParams2.setMargins(20,40,20,0)
        buttonParams3.setMargins(20,30,20,0)

        button.layoutParams = buttonParams
        button2.layoutParams = buttonParams2
        button3.layoutParams = buttonParams3
        dummyButton.layoutParams = dummyButtonParams
        var handler2 : ButtonHandler2 = ButtonHandler2()
        var handler3 : ButtonHandler3 = ButtonHandler3()
        var handler4 : ButtonHandler4 = ButtonHandler4()
        button.setOnClickListener(handler2)
        button2.setOnClickListener(handler3)
        button3.setOnClickListener(handler4)

        relativeLayout.addView(button3)
        relativeLayout.addView(dummyButton)
        relativeLayout.addView(button)
        relativeLayout.addView(button2)

        setContentView( relativeLayout )
    }

    fun getWhiteTaken() : Int {
        return chessLogic.getNumberWhitePiecesTaken()
    }

    fun getBlackTaken() : Int {
        return chessLogic.getNumberBlackPiecesTaken()
    }

    inner class ButtonHandler2 : View.OnClickListener {
        override fun onClick(v: View?) {
            setContentView(R.layout.chess_directions)
        }
    }

    inner class ButtonHandler3 : View.OnClickListener {
        override fun onClick(v: View?) {
            findStats(v)
        }
    }

    inner class ButtonHandler4 : View.OnClickListener {
        override fun onClick(v: View?) {
            startGame(v)
        }
    }

    fun startGame(v: View?) {
        var intent: Intent = Intent(this, ChessView::class.java)
        startActivity( intent )
        if (slideDirection == "left") {
            slideDirection = "right"
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_from_right)
        } else {
            slideDirection = "left"
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
        val recipient = "mbolin22@terpmail.umd.edu"
        val subject = "App Feedback"
        val body = "Your email body goes here"

        val rBar = findViewById<RatingBar>(R.id.ratingBar)
        val ratingVal = rBar.rating.toString()

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Check out the Chess app! I give this app a " + ratingVal + " star rating")
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"))
            startActivity(intent)
        }
    }

    fun gameOver() {
        if(currPlayer == "White") textBelowGrid.setTextColor(Color.WHITE) else textBelowGrid.setTextColor(Color.BLACK)
        currPlayer = "Game_Over"
        textBelowGrid.text = "GAME OVER"
        soundPool.play(victorySound,1.0f, 1.0f, 2,0,1.0f)
    }

    inner class ButtonHandler : View.OnClickListener {
        override fun onClick(v: View?) {
            if (v != null) {
                first@ for( i in 0 .. buttons.size - 1 ) {
                    for( j in 0 .. buttons[i].size - 1 ) {
                        if( v == buttons[i][j] ) {
                            Log.w("Button Found", "processing")
                            if (activePiece == NO_PIECE_SELECTED){
                                Log.w("First Click", "processing")
                                activePiece = chessLogic.processFirstClick(buttons[i][j], currPlayer, whitePiecesSet, blackPiecesSet)
                                if (activePiece != NO_PIECE_SELECTED) {
                                    activePiecei = i
                                    activePiecej = j
                                    buttons[i][j].setBackgroundColor(Color.YELLOW)
                                    soundPool.play(pickUpSound, 1.0f, 1.0f, 1, 0,1.0f)
                                }
                            } else {
                                Log.w("Second Click", "processing")
                                activePiece = chessLogic.processSecondClick(currContext, activePiece, currPlayer, buttons, i, j, whitePiecesSet, blackPiecesSet, activePiecei, activePiecej, soundPool)

                                if (activePiece == TURN_OVER) {
                                    currPlayer = if(currPlayer == "White") "Black" else "White"
                                    status.text = "$currPlayer's Turn"
                                    if(currPlayer == "White") status.setTextColor(Color.WHITE) else status.setTextColor(Color.BLACK)
                                    activePiece = -1
                                    activePiecei = 0
                                    activePiecej = 0
                                } else if (activePiece == WHITE_VICTORY) {
                                    status.text = "White Victory!"
                                    textBelowGrid.setTextColor(Color.WHITE)
                                    gameOver()
                                } else if (activePiece == BLACK_VICTORY) {
                                    status.text = "Black Victory!"
                                    textBelowGrid.setTextColor(Color.BLACK)
                                    gameOver()
                                }
                            }
                            break@first
                        }
                    }
                }
            }
        }
    }

    companion object {
        lateinit var CONTEXT: Context
        lateinit var ACTIVITY : MainActivity
        const val STORED_WHITE_WINS : String = "white_victories"
        const val STORED_BLACK_WINS : String = "black_victories"
        const val WHITE_PIECES_TAKEN : String = "white_pieces_taken"
        const val BLACK_PIECES_TAKEN : String = "black_pieces_taken"
        const val NO_PIECE_SELECTED : Int = -1
        const val TURN_OVER : Int = 0
        const val WHITE_VICTORY : Int = -2
        const val BLACK_VICTORY : Int = -3
    }


}