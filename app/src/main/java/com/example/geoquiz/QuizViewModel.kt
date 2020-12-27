package com.example.geoquiz

import androidx.lifecycle.ViewModel

/**
 * This View
 */
class QuizViewModel : ViewModel() {

    private val TAG = javaClass.simpleName

    var currentIndex = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionCheated: Boolean
        get() = questionBank[currentIndex].cheated

    fun moveToPrevious() {
        currentIndex = (currentIndex + questionBank.size - 1) % questionBank.size
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun markCurrentAnswerAsCheated() {
        questionBank[currentIndex].cheated = true
    }

    fun countCheats(): Int = questionBank.count { it.cheated }

}