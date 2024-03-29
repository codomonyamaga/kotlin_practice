package kotlin_practice.gateway

import kotlin_practice.domain.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DailyAttendanceRepositoryImpl : DailyAttendanceRepository {
    override fun fetch(memberId: EmployeeId, baseDate: LocalDate): DailyAttendance {
        // DBに保存されていたら、オブジェクトを生成する
        val start = findStart(baseDate, memberId)
        val breakStart = findBreakStart(baseDate, memberId)
        val breakEnd = findBreakEnd(baseDate, memberId)
        val end = findEnd(baseDate, memberId)

        val stamps = listOfNotNull(start) + breakStart + breakEnd + listOfNotNull(end)

        // DBに保存されていなかったら、オブジェクトを新しく構築する
        return if (stamps.isEmpty()) {
            // DBに保存されていなかったら、オブジェクトを新しく構築する
            DailyAttendance.create(baseDate, memberId)
        } else {
            DailyAttendance.from(
                baseDate,
                memberId,
                stamps
            )
        }
    }

    override fun save(dailyAttendance: DailyAttendance) {
        dailyAttendance.stamps.forEach {
            when (it.kind) {
                StampKind.Start -> startStampsTable.add(
                    StampRecord(
                        it.stampId,
                        dailyAttendance.baseDate,
                        dailyAttendance.memberId.value,
                        it.datetime
                    )
                )

                StampKind.Break -> breakStartStampsTable.add(
                    StampRecord(
                        it.stampId,
                        dailyAttendance.baseDate,
                        dailyAttendance.memberId.value,
                        it.datetime
                    )
                )

                StampKind.BreakEnd -> breakEndStampsTable.add(
                    StampRecord(
                        it.stampId,
                        dailyAttendance.baseDate,
                        dailyAttendance.memberId.value,
                        it.datetime
                    )
                )

                StampKind.End -> endStampsTable.add(
                    StampRecord(
                        it.stampId,
                        dailyAttendance.baseDate,
                        dailyAttendance.memberId.value,
                        it.datetime
                    )
                )
            }
        }
    }

    private fun findStart(baseDate: LocalDate, memberId: EmployeeId): Stamp? {
        return startStampsTable.filter { it.baseDate == baseDate && it.memberId == memberId.value }
            .maxByOrNull { it.datetime }
            ?.let {
                Stamp(
                    stampId = it.stampId,
                    kind = StampKind.Start,
                    datetime = it.datetime
                )
            }
    }

    private fun findEnd(baseDate: LocalDate, memberId: EmployeeId): Stamp? {
        return endStampsTable.filter { it.baseDate == baseDate && it.memberId == memberId.value }
            .maxByOrNull { it.datetime }
            ?.let {
                Stamp(
                    stampId = it.stampId,
                    kind = StampKind.End,
                    datetime = it.datetime
                )
            }
    }

    private fun findBreakStart(baseDate: LocalDate, memberId: EmployeeId): List<Stamp>{
        return breakStartStampsTable.filter { it.baseDate == baseDate && it.memberId == memberId.value }
            .map {
                Stamp(
                    stampId = it.stampId,
                    kind = StampKind.Break,
                    datetime = it.datetime
                )
            }
    }

    private fun findBreakEnd(baseDate: LocalDate, memberId: EmployeeId): List<Stamp>{
        return breakEndStampsTable.filter { it.baseDate == baseDate && it.memberId == memberId.value }
            .map {
                Stamp(
                    stampId = it.stampId,
                    kind = StampKind.BreakEnd,
                    datetime = it.datetime
                )
            }
    }
}

val employeeId = UUID.fromString("056e9caf-3d06-4935-a4e4-1216ef2efa66")

val startStampsTable = mutableListOf(
    StampRecord(UUID.fromString("1b31b378-ccf4-471a-82e9-9b2e442be458"), LocalDate.of(2024, 1, 1), employeeId, LocalDateTime.of(2024, 1, 1, 9, 0)),
    StampRecord(UUID.fromString("5dcdb48f-e0dd-41e4-b736-e24b0673e6e2"), LocalDate.of(2024, 1, 2), employeeId, LocalDateTime.of(2024, 1, 2, 9, 0)),
    StampRecord(UUID.fromString("49318b5d-c634-4805-a9a8-e66773145bce"), LocalDate.of(2024, 1, 3), employeeId, LocalDateTime.of(2024, 1, 3, 9, 0)),
)

val breakStartStampsTable = mutableListOf(
    StampRecord(UUID.fromString("5d54a1d1-2344-495d-8cf0-b41389143870"), LocalDate.of(2024, 1, 1), employeeId, LocalDateTime.of(2024, 1, 1, 12, 0)),
    StampRecord(UUID.fromString("cd1fa59c-fddf-432e-8776-2e7c26d8299e"), LocalDate.of(2024, 1, 2), employeeId, LocalDateTime.of(2024, 1, 2, 12, 0)),
    StampRecord(UUID.fromString("e6da0a09-adcb-4928-abb6-586fd423e670"), LocalDate.of(2024, 1, 3), employeeId, LocalDateTime.of(2024, 1, 3, 12, 0)),
)

val breakEndStampsTable = mutableListOf(
    StampRecord(UUID.fromString("d795656b-89eb-4914-8d59-8757983ba0ae"), LocalDate.of(2024, 1, 1), employeeId, LocalDateTime.of(2024, 1, 1, 13, 0)),
    StampRecord(UUID.fromString("cd9326a2-07d5-4737-821e-407a192a124d"), LocalDate.of(2024, 1, 2), employeeId, LocalDateTime.of(2024, 1, 2, 13, 0)),
    StampRecord(UUID.fromString("45209c9a-9b9b-49ec-acdd-d5e64676b4cf"), LocalDate.of(2024, 1, 3), employeeId, LocalDateTime.of(2024, 1, 3, 13, 0)),
)

val endStampsTable = mutableListOf(
    StampRecord(UUID.fromString("681f1abf-fad8-4820-9545-ee8e4b7aec81"), LocalDate.of(2024, 1, 1), employeeId, LocalDateTime.of(2024, 1, 1, 18, 0)),
    StampRecord(UUID.fromString("5683f219-50a7-4ee9-882d-d593e2c91aeb"), LocalDate.of(2024, 1, 2), employeeId, LocalDateTime.of(2024, 1, 2, 18, 0)),
    StampRecord(UUID.fromString("6878fe23-4174-4f36-b45b-cdd7d2d3649c"), LocalDate.of(2024, 1, 3), employeeId, LocalDateTime.of(2024, 1, 3, 18, 0)),
)

//// DBに保存されているデータ
//var stamps = mutableListOf(
//    // 2024/1/1
//    StampRecord(LocalDate.of(2024, 1, 1), memberId, LocalDateTime.of(2024, 1, 1, 9, 0), StampKindType.Start),
//    StampRecord(LocalDate.of(2024, 1, 1), memberId, LocalDateTime.of(2024, 1, 1, 12, 0), StampKindType.BreakStart),
//    StampRecord(LocalDate.of(2024, 1, 1), memberId, LocalDateTime.of(2024, 1, 1, 13, 0), StampKindType.BreakEnd),
//    StampRecord(LocalDate.of(2024, 1, 1), memberId, LocalDateTime.of(2024, 1, 1, 18, 0), StampKindType.End),
//    // 2024/1/2
//    StampRecord(LocalDate.of(2024, 1, 2), memberId, LocalDateTime.of(2024, 1, 2, 9, 0), StampKindType.Start),
//    StampRecord(LocalDate.of(2024, 1, 2), memberId, LocalDateTime.of(2024, 1, 2, 12, 0), StampKindType.BreakStart),
//    StampRecord(LocalDate.of(2024, 1, 2), memberId, LocalDateTime.of(2024, 1, 2, 13, 0), StampKindType.BreakEnd),
//    StampRecord(LocalDate.of(2024, 1, 2), memberId, LocalDateTime.of(2024, 1, 2, 18, 0), StampKindType.End),
//    // 2024/1/3
//    StampRecord(LocalDate.of(2024, 1, 3), memberId, LocalDateTime.of(2024, 1, 3, 9, 0), StampKindType.Start),
//    StampRecord(LocalDate.of(2024, 1, 3), memberId, LocalDateTime.of(2024, 1, 3, 12, 0), StampKindType.BreakStart),
//    StampRecord(LocalDate.of(2024, 1, 3), memberId, LocalDateTime.of(2024, 1, 3, 13, 0), StampKindType.BreakEnd),
//    StampRecord(LocalDate.of(2024, 1, 3), memberId, LocalDateTime.of(2024, 1, 3, 18, 0), StampKindType.End),
//)


data class StampRecord(
    val stampId: UUID,
    val baseDate: LocalDate,
    val memberId: UUID,
    val datetime: LocalDateTime,
)

enum class StampKindType {
    Start,
    BreakStart,
    BreakEnd,
    End,
}