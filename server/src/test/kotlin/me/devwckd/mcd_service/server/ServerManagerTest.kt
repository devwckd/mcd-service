package me.devwckd.mcd_service.server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ServerManagerTest {

    lateinit var serverManager: ServerManager

    @BeforeEach
    fun setup() {
        serverManager = ServerManager()
    }

    @Test
    fun `getAll should return empty collection`() {
        val result = serverManager.getAll()
        assertTrue(result.isEmpty())
    }

}