package com.example.guessit.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import timber.log.Timber

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {
    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val TIMER = 60000L
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private val _buzzer = MutableLiveData<BuzzType>()
    val buzzer: LiveData<BuzzType>
        get() = _buzzer

    private var timer: CountDownTimer

    private val _currentTimer = MutableLiveData<Long>()
    val currentTimer: LiveData<Long>
        get() = _currentTimer

    val currentTimeString = Transformations.map(currentTimer) {
        DateUtils.formatElapsedTime(it)
    }

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    lateinit var wordList: MutableList<String>

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean>
        get() = _finished

    init {
        Timber.i("GameViewModel created!!")

        timer = object : CountDownTimer(TIMER, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTimer.value = _currentTimer.value?.minus(1)

                if (_currentTimer in 1L..10L)
                    _buzzer.value = BuzzType.COUNTDOWN_PANIC
            }

            override fun onFinish() {
                _finished.value = true
                _buzzer.value = BuzzType.GAME_OVER
            }
        }
        timer.start()

        _score.value = 0
        _word.value = ""
        _finished.value = false
        _currentTimer.value = 60L
        createList()
        nextWord()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Timber.i("GameViewModel destroyed!!")
    }

    private fun createList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    fun nextWord() {
        if (wordList.isEmpty())
            createList()


        _word.value = wordList.removeAt(0)
    }

    fun onSuccess() {
        _score.value = score.value?.plus(1)
        _buzzer.value = BuzzType.CORRECT
        nextWord()
    }

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun resetGameFinished() {
        _finished.value = false
    }

    fun resetBuzzer() {
        _buzzer.value = BuzzType.NO_BUZZ
    }
}