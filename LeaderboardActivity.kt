class LeaderboardActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private var currentUserId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        
        setupViews()
        loadLeaderboard()
    }
    
    private fun setupViews() {
        recyclerView = findViewById(R.id.leaderboardRecyclerView)
        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter
        
        findViewById<Button>(R.id.globalLeaderboardBtn).setOnClickListener {
            openGlobalLeaderboard()
        }
        
        findViewById<Button>(R.id.playAgainBtn).setOnClickListener {
            finish()
        }
    }
    
    private fun loadLeaderboard() {
        val currentScore = intent.getIntExtra("current_score", 0)
        
        // Submit score first
        submitScore(currentScore) { success ->
            if (success) {
                // Then load leaderboard
                fetchLeaderboard()
            }
        }
    }
    
    private fun submitScore(score: Int, callback: (Boolean) -> Unit) {
        val player = getCurrentPlayer() // Get from Telegram or local storage
        val scoreData = Score(
            userId = player.id,
            username = player.username,
            photoUrl = player.photoUrl,
            score = score,
            timestamp = System.currentTimeMillis()
        )
        
        ApiClient.leaderboardService.submitScore(scoreData).enqueue(
            object : Callback<ScoreResponse> {
                override fun onResponse(call: Call<ScoreResponse>, response: Response<ScoreResponse>) {
                    callback(response.isSuccessful)
                }
                override fun onFailure(call: Call<ScoreResponse>, t: Throwable) {
                    callback(false)
                }
            }
        )
    }
    
    private fun fetchLeaderboard() {
        ApiClient.leaderboardService.getTopScores(10).enqueue(
            object : Callback<List<Score>> {
                override fun onResponse(call: Call<List<Score>>, response: Response<List<Score>>) {
                    response.body()?.let { scores ->
                        adapter.submitList(scores)
                        currentUserId = getCurrentPlayer().id
                        adapter.setCurrentUserId(currentUserId)
                    }
                }
                override fun onFailure(call: Call<List<Score>>, t: Throwable) {
                    Toast.makeText(this@LeaderboardActivity, "Failed to load leaderboard", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    private fun openGlobalLeaderboard() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://your-server.com/leaderboard.html"))
        startActivity(intent)
    }
}
