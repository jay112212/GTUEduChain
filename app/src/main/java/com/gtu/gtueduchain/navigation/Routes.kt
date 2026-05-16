package com.gtu.gtueduchain.navigation
import com.gtu.gtueduchain.navigation.Routes


sealed class Routes(val route: String) {

    object Home : Routes("home")

    object Admin : Routes("admin")

    object IssueDegree : Routes("issue_degree")

    object Student : Routes("student")

    object Verify : Routes("verify")

    object Ledger : Routes("ledger")
}
