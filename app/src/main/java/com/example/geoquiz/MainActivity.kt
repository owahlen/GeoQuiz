package com.example.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlin.math.max

private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val MAX_CHEAT_COUNT = 3

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    // declare views to be used in this Activity
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var remainingCheatsTextView: TextView
    private lateinit var questionTextView: TextView

    // lazy initialization of ViewModel of this activity.
    private val quizViewModel: QuizViewModel by lazy {
        // The ViewModel is associated with the MainActivity and survives configuration changes.
        // I.e. while the Activity is destroyed on a change from portrait view to landscape view
        // the ViewModel survives this change. Note that the ViewModel is destroyed if the OS
        // kills the Activity due to low memory.
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    // called when Activity state changes from Nonexistent or Stashed (e.g. killed by OS)
    // to Stopped (in memory)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        // render activity_main.xml
        setContentView(R.layout.activity_main)

        // restore viewModel from savedState in case the OS had shutdown the app
        quizViewModel.currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0

        // get view references
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        previousButton = findViewById(R.id.previous_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        remainingCheatsTextView = findViewById(R.id.remaining_cheats_text_view)
        questionTextView = findViewById(R.id.question_text_view)

        // define view handlers:
        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        cheatButton.setOnClickListener { view ->
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            // build the intent to be processed by the Android ActivityManager
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            // check the phone's SDK version against the version where a method was introduced
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions
                    .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                // start the activity using the intent giving extra options
                // the REQUEST_CODE_CHEAT is sent to the child activity and received back, here.
                // In case of several child Activities it is used to distinguish who is reporting
                // back.
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                // start the activity using the intent
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
        updateCheatButton()
        updateRemainingCheatsTextView()
    }

    // called when returning from child Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            val cheated = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            if (cheated) {
                quizViewModel.markCurrentAnswerAsCheated()
                updateCheatButton()
                updateRemainingCheatsTextView()
            }
        }
    }

    // called when Activity state changes from Stopped to Paused (visible)
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    // called when Activity state changes from Paused to Resumed (active in foreground)
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    // called when Activity state changes from Resumed to Paused (visible)
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    // Called when the Activity enters a state that allows the OS to potentially kill it:
    // This may be necessary in case the OS is running low on memory
    // In fact this method is called when the app transitions from Paused into Stopped state.
    // Note that this operation is expensive since the savedInstanceState is serialized to disk
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(TAG, "onSaveInstanceState() called")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    // called when Activity state changes from Paused to Stopped (in memory)
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    // called when Activity state changes from Stopped to Nonexistent
    // (finished or destroyed by Android). Note that this method is not called
    // if the OS decided to kill the app due to low memory.
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    // private helper methods
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.currentQuestionCheated -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

    }

    private fun updateCheatButton() {
        val cheatCount = quizViewModel.countCheats()
        if (cheatCount >= MAX_CHEAT_COUNT) {
            cheatButton.setEnabled(false)
        }
    }

    private fun updateRemainingCheatsTextView() {
        val remainingCheats = max(MAX_CHEAT_COUNT - quizViewModel.countCheats(),0)
        val remainingCheatsText = getString(R.string.remaining_cheats, remainingCheats)
        remainingCheatsTextView.setText(remainingCheatsText)

    }
}