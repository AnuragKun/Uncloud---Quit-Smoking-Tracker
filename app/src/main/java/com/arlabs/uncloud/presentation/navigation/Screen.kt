package com.arlabs.uncloud.presentation.navigation

sealed interface Screen {
    val route: String

    data object Onboarding : Screen {
        override val route = "onboarding"
    }

    data object Home : Screen {
        override val route = "home"
    }

    data object Settings : Screen {
        override val route = "settings"
    }

    data object Health : Screen {
        override val route = "health"
    }

    data object PrivacyPolicy : Screen {
        override val route = "privacy_policy"
    }

    data object TermsOfService : Screen {
        override val route = "terms_of_service"
    }

    data object Panic : Screen {
        override val route = "panic"
    }

    data object Ranking : Screen {
        override val route = "ranking"
    }

    data object Protocol : Screen {
        override val route = "protocol"
    }

    data object ReportBreach : Screen {
        override val route = "report_breach"
    }
}
