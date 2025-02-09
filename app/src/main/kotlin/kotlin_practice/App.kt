/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package kotlin_practice

import kotlin_practice.domain.EmployeeId
import kotlin_practice.domain.StampKind
import kotlin_practice.gateway.DailyAttendanceRepositoryImpl
import kotlin_practice.usecase.RecordStamp
import java.time.LocalDateTime
import java.util.*

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    controllerSuccess()
    controllerFailed()
}

val employeeId = UUID.fromString("056e9caf-3d06-4935-a4e4-1216ef2efa66")

fun controllerSuccess() {
    // 始業打刻　成功
    val employeeId = EmployeeId(employeeId)
    val datetime = LocalDateTime.of(2024, 2, 1, 9, 0)
    val kind = StampKind.Start
    val useCase = RecordStamp(DailyAttendanceRepositoryImpl())
    useCase.record(employeeId, datetime, kind)
    val updated = DailyAttendanceRepositoryImpl().fetch(employeeId, datetime.toLocalDate())
    println("勤務日：${ updated.baseDate }")
    updated.stamps.forEach{
        println("kind: ${it.kind}  datetime: ${it.datetime}")
    }
}

fun controllerFailed() {
    // 始業打刻　既に打刻済みのため失敗
    val employeeId = EmployeeId(employeeId)
    val datetime = LocalDateTime.of(2024, 1, 1, 9, 0)
    val kind = StampKind.Start
    val useCase = RecordStamp(DailyAttendanceRepositoryImpl())
    useCase.record(employeeId, datetime, kind)
}
