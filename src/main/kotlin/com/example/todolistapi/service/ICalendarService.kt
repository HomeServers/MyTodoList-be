package com.example.todolistapi.service

import com.example.todolistapi.entity.Item
import com.example.todolistapi.entity.ItemStatus
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VToDo
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class ICalendarService {

    /**
     * Item 목록을 .ics 파일 내용으로 변환
     */
    fun exportToIcs(items: List<Item>): String {
        val calendar = Calendar()

        // VCALENDAR 속성 설정
        calendar.properties.add(ProdId("-//MyTodoList//iCal4j 3.2.14//EN"))
        calendar.properties.add(Version.VERSION_2_0)
        calendar.properties.add(CalScale.GREGORIAN)

        // 각 Item을 VTODO로 변환하여 추가
        items.forEach { item ->
            val vtodo = itemToVTodo(item)
            calendar.components.add(vtodo)
        }

        // Calendar를 문자열로 변환
        val outputStream = ByteArrayOutputStream()
        val outputter = CalendarOutputter()
        outputter.output(calendar, outputStream)

        return outputStream.toString(Charsets.UTF_8)
    }

    /**
     * .ics 파일 내용을 파싱하여 Item 생성에 필요한 데이터 추출
     * 반환값: (content, hash, status, dueDate) 목록
     */
    fun importFromIcs(icsContent: String): List<ImportedItemData> {
        val inputStream = ByteArrayInputStream(icsContent.toByteArray(Charsets.UTF_8))
        val builder = CalendarBuilder()
        val calendar = builder.build(inputStream)

        val importedItems = mutableListOf<ImportedItemData>()

        // VTODO 컴포넌트 추출
        calendar.getComponents<VToDo>(Component.VTODO).forEach { vtodo ->
            try {
                val itemData = vtodoToItemData(vtodo)
                importedItems.add(itemData)
            } catch (e: Exception) {
                // 파싱 실패한 항목은 건너뜀
                println("Failed to parse VTODO: ${e.message}")
            }
        }

        // VEVENT 컴포넌트 추출
        calendar.getComponents<VEvent>(Component.VEVENT).forEach { vevent ->
            try {
                val itemData = veventToItemData(vevent)
                importedItems.add(itemData)
            } catch (e: Exception) {
                // 파싱 실패한 항목은 건너뜀
                println("Failed to parse VEVENT: ${e.message}")
            }
        }

        return importedItems
    }

    /**
     * Item을 VTODO 컴포넌트로 변환
     */
    private fun itemToVTodo(item: Item): VToDo {
        val vtodo = VToDo()

        // UID: Item의 hash 사용
        vtodo.properties.add(Uid(item.hash))

        // SUMMARY: Item의 content 사용
        vtodo.properties.add(Summary(item.content))

        // STATUS: ItemStatus 매핑
        val status = when (item.status) {
            ItemStatus.PENDING -> Status.VTODO_NEEDS_ACTION
            ItemStatus.IN_PROGRESS -> Status.VTODO_IN_PROCESS
            ItemStatus.COMPLETED -> Status.VTODO_COMPLETED
            ItemStatus.EXPIRED -> Status.VTODO_NEEDS_ACTION // EXPIRED는 NEEDS-ACTION + 만료된 DUE로 표현
        }
        vtodo.properties.add(status)

        // DUE: dueDate 변환
        item.dueDate?.let { dueDate ->
            val instant = dueDate.atZone(ZoneId.systemDefault()).toInstant()
            val date = Date.from(instant)
            vtodo.properties.add(Due(net.fortuna.ical4j.model.DateTime(date)))
        }

        // DTSTAMP: 현재 시각
        vtodo.properties.add(DtStamp(net.fortuna.ical4j.model.DateTime(Date())))

        return vtodo
    }

    /**
     * VTODO 컴포넌트를 ImportedItemData로 변환
     */
    private fun vtodoToItemData(vtodo: VToDo): ImportedItemData {
        // UID (hash)
        val uid = vtodo.getProperty<Uid>(Property.UID)?.value
            ?: UUID.randomUUID().toString()

        // SUMMARY (content)
        val summary = vtodo.getProperty<Summary>(Property.SUMMARY)?.value
            ?: "Untitled"

        // STATUS
        val statusProp = vtodo.getProperty<Status>(Property.STATUS)
        val status = when (statusProp?.value) {
            Status.VTODO_IN_PROCESS.value -> ItemStatus.IN_PROGRESS
            Status.VTODO_COMPLETED.value -> ItemStatus.COMPLETED
            Status.VTODO_CANCELLED.value -> ItemStatus.EXPIRED
            else -> ItemStatus.PENDING // NEEDS-ACTION 또는 없음
        }

        // DUE (dueDate)
        val dueProp = vtodo.getProperty<Due>(Property.DUE)
        val dueDate = dueProp?.date?.let { date ->
            LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        }

        return ImportedItemData(
            content = summary,
            hash = uid,
            status = status,
            dueDate = dueDate
        )
    }

    /**
     * VEVENT 컴포넌트를 ImportedItemData로 변환
     */
    private fun veventToItemData(vevent: VEvent): ImportedItemData {
        // UID (hash)
        val uid = vevent.getProperty<Uid>(Property.UID)?.value
            ?: UUID.randomUUID().toString()

        // SUMMARY (content)
        val summary = vevent.getProperty<Summary>(Property.SUMMARY)?.value
            ?: "Untitled Event"

        // DTEND (종료 시간을 dueDate로 사용)
        val dtEnd = vevent.getProperty<DtEnd>(Property.DTEND)
        val dueDate = dtEnd?.date?.let { date ->
            LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        }

        // STATUS - VEVENT의 상태를 매핑
        val statusProp = vevent.getProperty<Status>(Property.STATUS)
        var status = when (statusProp?.value) {
            Status.VEVENT_CONFIRMED.value -> ItemStatus.PENDING
            Status.VEVENT_TENTATIVE.value -> ItemStatus.PENDING
            Status.VEVENT_CANCELLED.value -> ItemStatus.EXPIRED
            else -> ItemStatus.PENDING
        }

        // 종료 시간이 현재 시간보다 이전이면 자동으로 EXPIRED 처리
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now()) && status != ItemStatus.EXPIRED) {
            status = ItemStatus.EXPIRED
        }

        return ImportedItemData(
            content = summary,
            hash = uid,
            status = status,
            dueDate = dueDate
        )
    }
}

/**
 * Import된 Item 데이터
 */
data class ImportedItemData(
    val content: String,
    val hash: String,
    val status: ItemStatus,
    val dueDate: LocalDateTime?
)
