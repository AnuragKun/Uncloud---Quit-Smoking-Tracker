package com.arlabs.uncloud.presentation.widgets.manager

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.arlabs.uncloud.domain.manager.WidgetRefresher
import com.arlabs.uncloud.presentation.widgets.CommitmentGridWidget
import com.arlabs.uncloud.presentation.widgets.DailyClarityWidget
import com.arlabs.uncloud.presentation.widgets.DailyPledgeWidget
import com.arlabs.uncloud.presentation.widgets.EmergencyShieldWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GlanceWidgetRefresher @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetRefresher {

    override suspend fun refreshWidgets() {
        // Update all widgets
        DailyPledgeWidget().updateAll(context)
        CommitmentGridWidget().updateAll(context)
        DailyClarityWidget().updateAll(context)
        EmergencyShieldWidget().updateAll(context)
    }
}
