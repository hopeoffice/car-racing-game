// server.js
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Score Schema
const scoreSchema = new mongoose.Schema({
    userId: String,
    username: String,
    photoUrl: String,
    score: Number,
    timestamp: { type: Date, default: Date.now }
});

const Score = mongoose.model('Score', scoreSchema);

// Routes
app.post('/scores', async (req, res) => {
    try {
        const score = new Score(req.body);
        await score.save();
        res.json({ success: true, rank: await getRank(score.userId) });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.get('/scores/top/:limit', async (req, res) => {
    const limit = parseInt(req.params.limit);
    const scores = await Score.find().sort({ score: -1 }).limit(limit);
    res.json(scores);
});

app.get('/scores/user/:userId', async (req, res) => {
    const userScore = await Score.findOne({ userId: req.params.userId }).sort({ score: -1 });
    const rank = await getRank(req.params.userId);
    res.json({ score: userScore, rank });
});

async function getRank(userId) {
    const userScore = await Score.findOne({ userId }).sort({ score: -1 });
    if (!userScore) return null;
    
    const higherScores = await Score.countDocuments({ 
        score: { $gt: userScore.score } 
    });
    
    return higherScores + 1;
}

// Archive job (run daily)
const archiveScores = async () => {
    const oneWeekAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
    const oldScores = await Score.find({ timestamp: { $lt: oneWeekAgo } });
    // Move to archive collection
    await ArchiveScore.insertMany(oldScores);
    await Score.deleteMany({ timestamp: { $lt: oneWeekAgo } });
};

mongoose.connect(process.env.MONGODB_URI)
    .then(() => {
        console.log('Connected to MongoDB');
        app.listen(3000, () => console.log('Server running on port 3000'));
    });
