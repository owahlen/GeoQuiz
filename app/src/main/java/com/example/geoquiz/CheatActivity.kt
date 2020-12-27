package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"

class CheatActivity : AppCompatActivity() {

    // declare views to be used in this Activity
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var showApiLevelTextView: TextView

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        // from the incoming intent extract the information if the answer is true or false
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        // get view references
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        showApiLevelTextView = findViewById(R.id.show_api_level_text_view)

        // define view handlers:
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }

        // get resource string with format args from string.xml and substitute format arguments
        val apiLevelText = getString(R.string.api_level, Build.VERSION.SDK_INT)
        showApiLevelTextView.setText(apiLevelText)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        // add the information if the answer was shown as an Intent to the result of this Activity.
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        // create an intent that invokes the CheatActivity passing "answerIsTrue" as extra parameter
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}