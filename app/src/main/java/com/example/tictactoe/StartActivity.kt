package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.tictactoe.databinding.ActivityStartBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import kotlin.random.Random
import kotlin.random.nextInt

class StartActivity : AppCompatActivity() {
    lateinit var binding : ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playOffline.setOnClickListener{
            GameData.saveGameModel(
                GameModel(gameStatus = GameStatus.JOINED)
            )
            startGame()
        }

        binding.playOnline.setOnClickListener {
            GameData.myId = "X"
            GameData.saveGameModel(
                GameModel(
                    gameStatus = GameStatus.CREATED,
                    gameId = Random.nextInt(1000..9999).toString()
                )
            )
            startGame()
        }

        binding.joinGame.setOnClickListener {
            joinGame()
        }
    }

    private fun startGame(){
        startActivity(Intent(this,MainActivity::class.java))

    }

    private fun joinGame(){
        var gameId = binding.gameCode.text.toString()
        if(gameId.isEmpty()){
            binding.gameCode.setError("Please Enter Game Id")
            Toast.makeText(this, "please enter game id", Toast.LENGTH_SHORT).show()
            return
        }
        GameData.myId = "O"
        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model = it?.toObject(GameModel::class.java)
                if(model == null){
                    Toast.makeText(this, "please enter valid game id", Toast.LENGTH_SHORT).show()
                }else{
                    model.gameStatus = GameStatus.JOINED
                    GameData.saveGameModel(model)
                    startGame()
                }
            }
    }
}