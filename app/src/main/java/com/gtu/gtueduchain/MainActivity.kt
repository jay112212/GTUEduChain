package com.gtu.gtueduchain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gtu.gtueduchain.navigation.NavGraph
import com.gtu.gtueduchain.ui.theme.GTUEduChainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GTUEduChainTheme {
                NavGraph()
            }
        }
    }
}
