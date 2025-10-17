class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback, Runnable {
    
    private var gameThread: Thread? = null
    private var isRunning = false
    private var score = 0
    private var gameEndListener: ((Int) -> Unit)? = null
    
    // Game objects
    private var playerCar: Car? = null
    private var obstacles = mutableListOf<Obstacle>()
    private var roadMarkings = mutableListOf<RoadMarking>()
    
    init {
        holder.addCallback(this)
    }
    
    fun setOnGameEndListener(listener: (Int) -> Unit) {
        gameEndListener = listener
    }
    
    fun startGame() {
        score = 0
        isRunning = true
        gameThread = Thread(this)
        gameThread?.start()
    }
    
    fun restartGame() {
        score = 0
        isRunning = true
        obstacles.clear()
        initializeGameObjects()
    }
    
    private fun initializeGameObjects() {
        playerCar = Car(width / 2f, height * 0.8f)
        // Initialize road markings, etc.
    }
    
    override fun run() {
        var lastTime = System.nanoTime()
        val nsPerUpdate = 1000000000.0 / 60.0
        
        var delta = 0.0
        while (isRunning) {
            val now = System.nanoTime()
            delta += (now - lastTime) / nsPerUpdate
            lastTime = now
            
            while (delta >= 1) {
                update()
                delta--
            }
            
            if (holder.surface.isValid) {
                draw()
            }
        }
    }
    
    private fun update() {
        playerCar?.update()
        updateObstacles()
        updateRoadMarkings()
        checkCollisions()
        score++
    }
    
    private fun updateObstacles() {
        // Move obstacles and generate new ones
        obstacles.forEach { it.update() }
        obstacles.removeAll { it.isOffScreen() }
        
        if (Random.nextInt(100) < 2) { // 2% chance each frame
            obstacles.add(Obstacle(Random.nextInt(width - 100), -100))
        }
    }
    
    private fun checkCollisions() {
        obstacles.forEach { obstacle ->
            if (playerCar?.collidesWith(obstacle) == true) {
                isRunning = false
                gameEndListener?.invoke(score)
            }
        }
    }
    
    private fun draw() {
        val canvas = holder.lockCanvas()
        try {
            // Draw background
            canvas.drawColor(Color.DKGRAY)
            
            // Draw road markings
            roadMarkings.forEach { it.draw(canvas) }
            
            // Draw obstacles
            obstacles.forEach { it.draw(canvas) }
            
            // Draw player car
            playerCar?.draw(canvas)
            
            // Draw score
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = 48f
            }
            canvas.drawText("Score: $score", 50f, 100f, paint)
            
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }
    
    override fun surfaceCreated(holder: SurfaceHolder) {
        initializeGameObjects()
    }
    
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        gameThread?.join()
    }
}
