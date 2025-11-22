package org.bin.demo.uneodinary.view.compose.navi

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object TagSelect : Screen("tag_select")
}