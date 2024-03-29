package kotlin_practice.domain

import java.time.LocalDate

interface DailyAttendanceRepository {
    fun fetch(memberId: EmployeeId, baseDate: LocalDate): DailyAttendance

    fun save(dailyAttendance: DailyAttendance)
}