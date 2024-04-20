package com.helloyanis.streetmeet

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun notificationSending() {
        val mockNotificationService = mockk<NotificationService>()

        every { mockNotificationService.createChannelNotification() } just runs
        every { mockNotificationService.showBasicNotification() } just runs

        mockNotificationService.createChannelNotification()
        mockNotificationService.showBasicNotification()

        verify { mockNotificationService.createChannelNotification() }
        verify { mockNotificationService.showBasicNotification() }
    }
}