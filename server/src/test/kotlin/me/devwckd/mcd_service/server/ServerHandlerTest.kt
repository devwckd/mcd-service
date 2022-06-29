package me.devwckd.mcd_service.server

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import me.devwckd.mcd_service.room.RoomManager
import me.devwckd.mcd_service.util.PaginationInfo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerHandlerTest {

//    private lateinit var serverHandler: ServerHandler
//
//    private lateinit var roomManager: RoomManager
//    private lateinit var serverManager: ServerManager
//
//    @BeforeEach
//    fun setUp() {
//        roomManager = mockk()
//        serverManager = mockk()
//        serverHandler = ServerHandler(serverManager, roomManager)
//    }
//
//    @Test
//    fun `listPaginated should return empty paginated response on newly created handler`() {
//        every { serverManager.getAll() } returns listOf()
//        val (currentPage, maxPages, itemsPerPage, items) = serverHandler.listPaginated(PaginationInfo(0, 15))
//
//        assertEquals(0, currentPage)
//        assertEquals(0, maxPages)
//        assertEquals(15, itemsPerPage)
//        assertTrue(items.isEmpty())
//    }

//    @Test
//    fun `listPaginated should return 2 entries`() {
//        every { serverManager.getAll() } returns listOf(Server("id1", "ip1", "type1"), Server("id2", "ip2", "type2"))
//        every { roomManager.getByServerId(any()) } returns listOf()
//
//        val (currentPage, maxPages, itemsPerPage, items) = serverHandler.listPaginated(PaginationInfo(0, 15))
//
//        assertEquals(0, currentPage)
//        assertEquals(0, maxPages)
//        assertEquals(15, itemsPerPage)
//
//        assertEquals("id1", items.toList()[0].id)
//        assertEquals("ip1", items.toList()[0].ip)
//
//        assertEquals("id2", items.toList()[1].id)
//        assertEquals("ip2", items.toList()[1].ip)
//
//        verify(exactly = 1) { serverManager.getAll() }
//    }
}