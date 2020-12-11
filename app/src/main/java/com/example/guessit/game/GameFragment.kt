package com.example.guessit.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.guessit.R
import com.example.guessit.databinding.FragmentGameBinding
import timber.log.Timber

class GameFragment : Fragment() {
    lateinit var binding: FragmentGameBinding
    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Timber.i("ViewModelProviders called")
        gameViewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.setLifecycleOwner(this)
        binding.gameViewModel = gameViewModel

        gameViewModel.finished.observe(this) { isFinished ->
            if (isFinished) {
                finishGame()
                gameViewModel.resetGameFinished()
            }
        }

        gameViewModel.buzzer.observe(this) {
            if (!it.pattern.contentEquals(longArrayOf(0))) {
                buzz(it.pattern)
                gameViewModel.resetBuzzer()
            }
        }

        return binding.root
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    private fun finishGame() {
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToResultFragment(
                gameViewModel.score.value
                    ?: 0
            )
        )
    }
}