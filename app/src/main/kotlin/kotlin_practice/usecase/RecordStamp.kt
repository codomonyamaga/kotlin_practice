package kotlin_practice.usecase

import kotlin_practice.domain.DailyAttendanceRepository
import kotlin_practice.domain.EmployeeId
import kotlin_practice.domain.StampKind
import java.time.LocalDateTime

class RecordStamp(private val dailyAttendanceRepository: DailyAttendanceRepository){

    fun record(memberId: EmployeeId, datetime: LocalDateTime, kind: StampKind) {
        val dailyAttendance = dailyAttendanceRepository.fetch(memberId, datetime.toLocalDate())
        val stamp = dailyAttendance.recordStamp(datetime, kind)
        dailyAttendanceRepository.save(stamp)
    }
}