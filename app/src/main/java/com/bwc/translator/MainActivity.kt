package com.bwc.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bwc.translator.ui.screens.TranslatorScreen
import com.bwc.translator.ui.theme.BWCTranslatorTheme
import com.bwc.translator.viewmodel.TranslatorViewModel

class MainActivity : ComponentActivity() {

    private val translatorViewModel: TranslatorViewModel by viewModels {
        TranslatorViewModel.TranslatorViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BWCTranslatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TranslatorScreen(viewModel = translatorViewModel)
                }
            }
        }
    }
}