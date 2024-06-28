package com.helloyanis.streetmeet

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.helloyanis.streetmeet.services.NotificationService
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class NotificationUnitTest {
    @Test
    fun sendNotification() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockNotificationManager = mockk<NotificationManager>(relaxed = true)

        val notificationService = NotificationService(mockContext, mockNotificationManager)

        val title = "Test notification"
        val description = "Test description"

        notificationService.send(title, description)

        val slot = slot<Notification>()
        verify { mockNotificationManager.notify(any(), capture(slot)) }

        val capturedNotification = slot.captured
        assertEquals(capturedNotification.extras.getString(NotificationCompat.EXTRA_TITLE), title)
        assertEquals(capturedNotification.extras.getString(NotificationCompat.EXTRA_TEXT), description)
    }
}