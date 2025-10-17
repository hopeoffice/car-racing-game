class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private var currentScore = 0
    private var gameRunning = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        
        gameView = findViewById(R.id.gameView)
        setupGame()
    }
    
    private fun setupGame() {
        gameView.setOnGameEndListener { score ->
            currentScore = score
            gameRunning = false
            showGameOverDialog(score)
        }
        
        gameView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && !gameRunning) {
                restartGame()
                return@setOnTouchListener true
            }
            false
        }
        
        startGame()
    }
    
    private fun startGame() {
        gameRunning = true
        gameView.startGame()
    }
    
    private fun restartGame() {
        gameRunning = true
        gameView.restartGame()
    }
    
    private fun showGameOverDialog(score: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Game Over!")
            .setMessage("Your score: $score")
            .setPositiveButton("View Leaderboard") { _, _ ->
                submitScoreAndShowLeaderboard(score)
            }
            .setNegativeButton("Play Again") { _, _ ->
                restartGame()
            }
            .create()
        dialog.show()
    }
    
    private fun submitScoreAndShowLeaderboard(score: Int) {
        // Submit score to server
        val intent = Intent(this, LeaderboardActivity::class.java)
        intent.putExtra("current_score", score)
        startActivity(intent)
    }
}
