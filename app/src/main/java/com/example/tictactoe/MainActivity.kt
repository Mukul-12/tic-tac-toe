package com.example.tictactoe

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    private var gameModel: GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GameData.fetchGameModel()

        binding.btn00.setOnClickListener(this)
        binding.btn01.setOnClickListener(this)
        binding.btn02.setOnClickListener(this)
        binding.btn10.setOnClickListener(this)
        binding.btn11.setOnClickListener(this)
        binding.btn12.setOnClickListener(this)
        binding.btn20.setOnClickListener(this)
        binding.btn21.setOnClickListener(this)
        binding.btn22.setOnClickListener(this)



        binding.startBtn.setOnClickListener{
            startGame()
        }

        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(gameStatus != GameStatus.INPROGRESS){
                Toast.makeText(applicationContext, "Game not started", Toast.LENGTH_SHORT).show()
                return
            }

            if(gameId != "-1" && currentPlayer!=GameData.myId){
                Toast.makeText(applicationContext, "Not your turn", Toast.LENGTH_SHORT).show()
                return
            }

            val clickedPos = (v?.tag as String).toInt()
            if(filledPos[clickedPos].isEmpty()){
                filledPos[clickedPos] = currentPlayer
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                checkForWinner()
                updateGameData(this)
            }

        }
    }

    private fun setUI(){
        gameModel?.apply {
            binding.btn00.text = filledPos[0]
            binding.btn01.text = filledPos[1]
            binding.btn02.text = filledPos[2]
            binding.btn10.text = filledPos[3]
            binding.btn11.text = filledPos[4]
            binding.btn12.text = filledPos[5]
            binding.btn20.text = filledPos[6]
            binding.btn21.text = filledPos[7]
            binding.btn22.text = filledPos[8]

            binding.startBtn.visibility = View.VISIBLE

            binding.gameStatusText.text = when(gameStatus){
                GameStatus.CREATED -> {
                    binding.startBtn.visibility = View.INVISIBLE
                    "Game Id :$gameId"
                }
                GameStatus.JOINED -> "Click on start game"
                GameStatus.INPROGRESS -> {
                    binding.startBtn.visibility = View.INVISIBLE
                    if(GameData.myId.isNotEmpty()){
                        when(GameData.myId){
                            currentPlayer -> "your turn"
                            else -> "$currentPlayer turn"
                        }
                    }else{
                        "$currentPlayer turn"
                    }

                }
                GameStatus.FINISHED -> {
                    if(winner.isNotEmpty()){
                        when(GameData.myId){
                            winner -> "You Won"
                            else -> "$winner won"
                        }
                    }
                    else "DRAW"
                }
            }
        }
    }

    private fun startGame(){
        gameModel?.apply {
            updateGameData(
                GameModel(
                    gameId = gameId,
                    gameStatus = GameStatus.INPROGRESS
                )
            )

        }
    }

    private fun checkForWinner(){
        val winningPos = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6)

        )

        gameModel?.apply {
            for(i in winningPos){
                if(
                    filledPos[i[0]] ==filledPos[i[1]] && filledPos[i[1]] == filledPos[i[2]] && filledPos[i[0]].isNotEmpty()
                ){
                    gameStatus = GameStatus.FINISHED
                    winner = filledPos[i[0]]
                }
            }
            if(filledPos.none(){it.isEmpty()}){
                gameStatus = GameStatus.FINISHED
            }
            updateGameData(this)
        }
    }

    private fun updateGameData(model : GameModel){
        GameData.saveGameModel(model)
    }


}