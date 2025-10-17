interface LeaderboardService {
    @POST("scores")
    fun submitScore(@Body score: Score): Call<ScoreResponse>
    
    @GET("scores/top/{limit}")
    fun getTopScores(@Path("limit") limit: Int): Call<List<Score>>
    
    @GET("scores/user/{userId}")
    fun getUserRank(@Path("userId") userId: String): Call<UserRank>
}
