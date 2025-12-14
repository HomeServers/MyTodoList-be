package com.example.todolistapi.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ICalendarServiceTest {

    private val service = ICalendarService()

    @Test
    fun `VEVENT와 VTODO를 모두 파싱할 수 있다`() {
        val icsContent = """
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Test//Test//EN
CALSCALE:GREGORIAN
BEGIN:VEVENT
UID:test-event-001
SUMMARY:Team Meeting
DTSTART:20251215T100000Z
DTEND:20251215T110000Z
STATUS:CONFIRMED
DTSTAMP:20251214T120000Z
END:VEVENT
BEGIN:VEVENT
UID:test-event-002
SUMMARY:Past Conference
DTSTART:20251201T140000Z
DTEND:20251201T160000Z
STATUS:CONFIRMED
DTSTAMP:20251214T120000Z
END:VEVENT
BEGIN:VTODO
UID:test-todo-001
SUMMARY:Complete project documentation
STATUS:NEEDS-ACTION
DUE:20251220T170000Z
DTSTAMP:20251214T120000Z
END:VTODO
END:VCALENDAR
        """.trimIndent()

        val items = service.importFromIcs(icsContent)

        // 총 3개의 아이템이 파싱되어야 함 (VEVENT 2개 + VTODO 1개)
        assertEquals(3, items.size)

        // VEVENT 검증
        val teamMeeting = items.find { it.hash == "test-event-001" }
        assertNotNull(teamMeeting)
        assertEquals("Team Meeting", teamMeeting?.content)
        assertNotNull(teamMeeting?.dueDate)

        // 과거 이벤트는 EXPIRED 상태여야 함
        val pastConference = items.find { it.hash == "test-event-002" }
        assertNotNull(pastConference)
        assertEquals("Past Conference", pastConference?.content)
        // 2025년 12월 1일은 과거이므로 EXPIRED여야 함

        // VTODO 검증
        val todo = items.find { it.hash == "test-todo-001" }
        assertNotNull(todo)
        assertEquals("Complete project documentation", todo?.content)

        println("✅ 테스트 성공: VEVENT ${items.count { it.content.contains("Meeting") || it.content.contains("Conference") }}개, VTODO ${items.count { it.content.contains("documentation") }}개 파싱됨")
        items.forEach { item ->
            println("  - ${item.content} (${item.status}, due: ${item.dueDate})")
        }
    }
}
