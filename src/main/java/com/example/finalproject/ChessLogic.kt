package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.media.SoundPool
import android.util.Log
import android.widget.ImageButton
import kotlin.math.abs

class ChessLogic {
    private lateinit var currPlayer: String
    private var activePiece: Int = -1
    private lateinit var currentPiecePlacement: Array<Array<ImageButton>>
    private var iReq : Int = 0
    private var jReq : Int = 0
    private lateinit var wPieces: ArrayList<Int>
    private lateinit var bPieces: ArrayList<Int>
    private var white_victories = 0
    private var black_victories = 0
    private var white_pieces_taken = 0
    private var black_pieces_taken = 0
    private var w_victory = false
    private var b_victory = false
    private lateinit var soundPool: SoundPool
    private var putDownSound = 0

    // Did the correct player click on the correct pieces
    // Return -1 means player clicked something that was not their piece, otherwise return the id of the piece they clicked
    fun processFirstClick(selectedPiece: ImageButton, currPlayer: String, whitePieces: ArrayList<Int>, blackPieces: ArrayList<Int>) : Int {
        wPieces = whitePieces
        bPieces = blackPieces
        this.currPlayer = currPlayer
        if(currPlayer == "White" && whitePieces.contains(selectedPiece.id)){
            return selectedPiece.id
        } else if (currPlayer == "Black" && blackPieces.contains(selectedPiece.id)) {
            return selectedPiece.id
        } else {
            return NO_PIECE_SELECTED
        }
    }

    /* Is the piece able to move to this position / capture / etc.?
     Return -1 allows same player to click new piece
     Return id allows same player to click new place to move current piece
     Return 0 indicates a player's turn was successfully completed
     pieceId is active piece
     currPlayer is the active team (white or black)
     gameboard is the current state of the entire chessboard
     iRequested and jRequested is the i and j location of the user's second click
     whitePieces and blackPieces is a list of all pieces for each respective color
     activePiecei and activePiecej is the i and j location of the selected piece (selected in the first click)
     */
    fun processSecondClick(context: Context, pieceId: Int, currPlayer: String, gameBoard: Array<Array<ImageButton>>, iRequested: Int, jRequested: Int, whitePieces: ArrayList<Int>, blackPieces: ArrayList<Int>, activePiecei: Int, activePiecej: Int, soundPoolParam: SoundPool) : Int {
        activePiece = pieceId
        this.currPlayer = currPlayer
        currentPiecePlacement = gameBoard
        iReq = iRequested
        jReq = jRequested
        soundPool = soundPoolParam
        putDownSound = soundPool.load(context, R.raw.put_down_piece, 1)
        if (currPlayer == "White" && whitePieces.contains(currentPiecePlacement[iReq][jReq].id)) {
            if (currentPiecePlacement[iReq][jReq].id == pieceId) {
                // deselect piece
                if ((activePiecei + activePiecej)%2 == 1) gameBoard[activePiecei][activePiecej].setBackgroundColor(Color.BLACK) else gameBoard[activePiecei][activePiecej].setBackgroundColor(
                    Color.WHITE)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
                return NO_PIECE_SELECTED
            } else {
                // invalid click on white's own piece
                return pieceId
            }
        } else if (currPlayer == "Black" && blackPieces.contains(currentPiecePlacement[iReq][jReq].id)) {
            if (currentPiecePlacement[iReq][jReq].id == pieceId) {
                // deselect piece
                if ((activePiecei + activePiecej)%2 == 1) gameBoard[activePiecei][activePiecej].setBackgroundColor(Color.BLACK) else gameBoard[activePiecei][activePiecej].setBackgroundColor(
                    Color.WHITE)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
                return NO_PIECE_SELECTED
            } else {
                // invalid click on black's own piece
                return pieceId
            }
        }


        // check if requested movement of piece is valid
        if (pieceId == R.id.Wrook || pieceId == R.id.Brook) {
            if (validateRookMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                currentPiecePlacement[iReq][jReq].id = pieceId
                if (pieceId == R.id.Wrook) currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_rook_1x)
                else currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_rook_1x)
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                return pieceId
            }
        } else if (pieceId == R.id.Wqueen || pieceId == R.id.Bqueen) {
            if (validateQueenMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                currentPiecePlacement[iReq][jReq].id = pieceId
                if (pieceId == R.id.Wqueen) currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_queen_1x)
                else currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_queen_1x)
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                return pieceId
            }
        } else if (pieceId == R.id.Wknight || pieceId == R.id.Bknight) {
            if (validateKnightMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                currentPiecePlacement[iReq][jReq].id = pieceId
                if (pieceId == R.id.Wknight) currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_knight_1x)
                else currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_knight_1x)
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                return pieceId
            }
        } else if (pieceId == R.id.Wbishop || pieceId == R.id.Bbishop) {
            if (validateBishopMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                currentPiecePlacement[iReq][jReq].id = pieceId
                if (pieceId == R.id.Wbishop) currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_bishop_1x)
                else currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_bishop_1x)
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                return pieceId
            }
        } else if (pieceId == R.id.Wpawn || pieceId == R.id.Bpawn) {
            Log.w("Pawn Move", "Check Move is Valid")
            Log.w("Pawn Move", "" + pieceId)
            if (validatePawnMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                Log.w("Pawn Move", "Move is Valid")
                if (pieceId == R.id.Wpawn) {
                    if (iReq == 0) {
                        currentPiecePlacement[iReq][jReq].id = R.id.Wqueen
                        currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_queen_1x)
                    } else {
                        currentPiecePlacement[iReq][jReq].id = pieceId
                        currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_pawn_1x)
                    }
                } else {
                    if (iReq == 7) {
                        currentPiecePlacement[iReq][jReq].id = R.id.Bqueen
                        currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_queen_1x)
                    } else {
                        currentPiecePlacement[iReq][jReq].id = pieceId
                        currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_pawn_1x)
                    }
                }
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                Log.w("Pawn Move", "Move is Invalid")
                return pieceId
            }
        } else if (pieceId == R.id.Wking || pieceId == R.id.Bking) {
            if (validateKingMove(activePiecei, activePiecej, iRequested, jRequested)) {
                checkVictory(context)
                checkCapture(context)
                currentPiecePlacement[iReq][jReq].id = pieceId
                if (pieceId == R.id.Wking) currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.w_king_1x)
                else currentPiecePlacement[iReq][jReq].setImageResource(R.drawable.b_king_1x)
                currentPiecePlacement[activePiecei][activePiecej].id = R.id.empty
                currentPiecePlacement[activePiecei][activePiecej].setImageResource(0)
                soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
            } else {
                return pieceId
            }
        }

        if ((activePiecei + activePiecej)%2 == 1) gameBoard[activePiecei][activePiecej].setBackgroundColor(Color.BLACK) else gameBoard[activePiecei][activePiecej].setBackgroundColor(
            Color.WHITE)
        if(w_victory) {
            return WHITE_VICTORY
        } else if (b_victory) {
            return BLACK_VICTORY
        } else {
            return TURN_OVER
        }
    }

    private fun checkVictory(context: Context) {
        if (currPlayer == "White" && currentPiecePlacement[iReq][jReq].id == R.id.Bking) {
            var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
                Context.MODE_PRIVATE )
            white_victories = pref.getInt(STORED_WHITE_WINS, 0)
            white_victories += 1
            var editor : SharedPreferences.Editor = pref.edit()
            editor.putInt(STORED_WHITE_WINS, white_victories)
            editor.apply()
            w_victory = true
        } else if (currPlayer == "Black" && currentPiecePlacement[iReq][jReq].id == R.id.Wking) {
            var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
                Context.MODE_PRIVATE )
            black_victories = pref.getInt(STORED_BLACK_WINS, 0)
            black_victories += 1
            var editor : SharedPreferences.Editor = pref.edit()
            editor.putInt(STORED_BLACK_WINS, black_victories)
            editor.apply()
            b_victory = true
        }
    }

    private fun checkCapture(context: Context) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences",
            Context.MODE_PRIVATE )
        var editor : SharedPreferences.Editor = pref.edit()
        if (currPlayer == "White" && bPieces.contains(currentPiecePlacement[iReq][jReq].id)) {
            var wPiecesTaken = pref.getInt(ChessView.WHITE_PIECES_TAKEN, 0)
            white_pieces_taken += 1
            wPiecesTaken += 1
            editor.putInt(ChessView.WHITE_PIECES_TAKEN, wPiecesTaken)
            putDownSound = soundPool.load(context, R.raw.captured_piece,1)
            soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
        } else if (currPlayer == "Black" && wPieces.contains(currentPiecePlacement[iReq][jReq].id)) {
            var bPiecesTaken = pref.getInt(ChessView.BLACK_PIECES_TAKEN, 0)
            black_pieces_taken += 1
            bPiecesTaken += 1
            editor.putInt(ChessView.BLACK_PIECES_TAKEN, bPiecesTaken)
            putDownSound = soundPool.load(context, R.raw.captured_piece,1)
            soundPool.play(putDownSound, 1.0f, 1.0f, 1, 0,1.0f)
        }
        editor.apply()
    }

    fun getNumberWhitePiecesTaken() : Int{
        return white_pieces_taken
    }


    fun getNumberBlackPiecesTaken() : Int{
        return black_pieces_taken
    }

    // check to make sure the point can be reached diagonally and without obstruction
    private fun validateBishopMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        if (abs(activePiecei - iRequested) != abs(activePiecej - jRequested)) {
            return false
        } else {
            // same distance for both i and j
            var distance = abs(activePiecej - jRequested) - 1
            // checking left
            if (activePiecej > jRequested) {
                //checking above left
                if (activePiecei > iRequested) {
                    for(i in 1..distance) {
                        if (currentPiecePlacement[activePiecei - i][activePiecej - i].id != R.id.empty) {
                            return false
                        }
                    }
                    //checking below left
                } else {
                    for(i in 1..distance) {
                        if (currentPiecePlacement[activePiecei + i][activePiecej - i].id != R.id.empty) {
                            return false
                        }
                    }
                }
                // checking right
            } else {
                //checking above right
                if (activePiecei > iRequested) {
                    for (i in 1..distance) {
                        if (currentPiecePlacement[activePiecei - i][activePiecej + i].id != R.id.empty) {
                            return false
                        }
                    }
                    //checking below right
                } else {
                    for (i in 1..distance) {
                        if (currentPiecePlacement[activePiecei + i][activePiecej + i].id != R.id.empty) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }



    // check to make sure the point can be reached within one tile and without obstruction
    private fun validateKingMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        var iDifference = abs(activePiecei - iRequested)
        var jDifference = abs(activePiecej - jRequested)
        if ( iDifference + jDifference == 1 || iDifference == 1 && jDifference == 1) {
            return validateQueenMove(activePiecei, activePiecej, iRequested, jRequested)
        } else {
            return false
        }
    }

    // check to make sure the point can be reached using knight method and without obstruction
    private fun validateKnightMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        var iDifference = abs(activePiecei - iRequested)
        var jDifference = abs(activePiecej - jRequested)
        return iDifference == 2 && jDifference == 1 || jDifference == 2 && iDifference == 1
    }

    // check to make sure the point can be reached within one tile and without obstruction
    private fun validatePawnMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        var result = false
        if (activePiecej == jRequested) {
            if (currPlayer == "White") {
                if ((iRequested == activePiecei - 1 || iRequested == activePiecei - 2)
                    && bPieces.contains(currentPiecePlacement[iRequested][jRequested].id)) {
                    return false
                } else if ((iRequested == activePiecei - 2)
                    && bPieces.contains(currentPiecePlacement[iRequested + 1][jRequested].id)) {
                    return false
                } else if (activePiecei == 6) {
                    return (activePiecei - iRequested) < 3
                } else {
                    return (activePiecei - iRequested) == 1
                }
            } else if (currPlayer == "Black") {
                if ((iRequested == activePiecei + 1 || iRequested == activePiecei + 2)
                    && wPieces.contains(currentPiecePlacement[iRequested][jRequested].id)) {
                    return false
                } else if ((iRequested == activePiecei + 2)
                    && wPieces.contains(currentPiecePlacement[iRequested - 1][jRequested].id)) {
                    return false
                } else if (activePiecei == 1) {
                    return (iRequested - activePiecei) < 3
                } else {
                    return (iRequested - activePiecei) == 1
                }
            }
            // White Pawn taking Pieces
        } else if ((jRequested == activePiecej - 1 && iRequested == activePiecei - 1) ||
            (jRequested == activePiecej + 1 && iRequested == activePiecei - 1)) {
            if (currPlayer == "White" && bPieces.contains(currentPiecePlacement[iRequested][jRequested].id)) {
                return true
            }
            // Black Pawn taking Pieces
        } else if ((jRequested == activePiecej - 1 && iRequested == activePiecei + 1) ||
            (jRequested == activePiecej + 1 && iRequested == activePiecei + 1)) {
            if (currPlayer == "Black" && wPieces.contains(currentPiecePlacement[iRequested][jRequested].id)) {
                return true
            }
        }
        return result
    }

    // check to make sure the point can be reached within in line or diagonally and without obstruction
    private fun validateQueenMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        return (validateBishopMove(activePiecei, activePiecej, iRequested, jRequested) || validateRookMove(activePiecei, activePiecej, iRequested, jRequested))
    }

    // check to make sure the point can be reached within line and without obstruction
    private fun validateRookMove(activePiecei: Int, activePiecej: Int, iRequested: Int, jRequested: Int) : Boolean {
        if (activePiecei == iRequested) {
            // check Horizontal (traverse j)
            var distance = abs(activePiecej - jRequested) - 1
            //check left
            if (activePiecej > jRequested) {
                for(j in 1..distance) {
                    if (currentPiecePlacement[activePiecei][activePiecej - j].id != R.id.empty) {
                        return false
                    }
                }
                return true
                // check right
            } else {
                for(j in 1..distance) {
                    if (currentPiecePlacement[activePiecei][activePiecej + j].id != R.id.empty) {
                        return false
                    }
                }
                return true
            }
        } else if (activePiecej == jRequested) {
            // check Vertical (traverse i)
            var distance = abs(activePiecei - iRequested) - 1
            // check above
            if (activePiecei > iRequested) {
                for(i in 1..distance) {
                    if (currentPiecePlacement[activePiecei - i][activePiecej].id != R.id.empty) {
                        return false
                    }
                }
                return true
                // check below
            } else {
                for(i in 1..distance) {
                    if (currentPiecePlacement[activePiecei + i][activePiecej].id != R.id.empty) {
                        return false
                    }
                }
                return true
            }
        } else {
            return false
        }
    }

    companion object {
        private const val STORED_WHITE_WINS : String = "white_victories"
        private const val STORED_BLACK_WINS : String = "black_victories"
        const val NO_PIECE_SELECTED : Int = -1
        const val TURN_OVER : Int = 0
        const val WHITE_VICTORY : Int = -2
        const val BLACK_VICTORY : Int = -3
    }

}