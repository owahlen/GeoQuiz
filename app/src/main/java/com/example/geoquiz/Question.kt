package com.example.geoquiz

import androidx.annotation.StringRes

/**
 * Model of the Quiz Question as used in the ViewModel
 */
data class Question(
    // ID of the question's string resource in strings.xml
    @StringRes val textResId: Int,
    // true if the correct answer to the question is "yes"; false otherwise
    val answer: Boolean,
    // remember if the user has cheated on this question
    var cheated: Boolean = false
)
