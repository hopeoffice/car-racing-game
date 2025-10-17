object ApiClient {
    private const val BASE_URL = "https://your-api-server.com/"
    
    val leaderboardService: LeaderboardService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LeaderboardService::class.java)
    }
}
