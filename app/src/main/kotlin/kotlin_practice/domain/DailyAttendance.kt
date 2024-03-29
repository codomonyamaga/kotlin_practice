package kotlin_practice.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class DailyAttendance(
    val baseDate: LocalDate,
    val memberId: EmployeeId,
    val stamps: List<Stamp>
) {

    companion object {
        fun create(date: LocalDate, memberId: EmployeeId): DailyAttendance {
            return DailyAttendance(date, memberId, emptyList())
        }

        fun from(date: LocalDate, memberId: EmployeeId, stamps: List<Stamp>): DailyAttendance {
            // 始業が存在しない場合
            if (stamps.none { it.kind == StampKind.Start }) {
                throw IllegalArgumentException("始業が打刻されていません")
            }
            // 始業打刻が複数ある場合
            if (stamps.count { it.kind == StampKind.Start } > 1) {
                throw IllegalArgumentException("始業打刻が複数あります")
            }
            // 終業打刻が複数ある場合
            if (stamps.count { it.kind == StampKind.End } > 1) {
                throw IllegalArgumentException("終業打刻が複数あります")
            }
            // 休憩開始よりも休憩終了の数が多い場合
            if (stamps.count { it.kind == StampKind.Break } < stamps.count { it.kind == StampKind.BreakEnd }) {
                throw IllegalArgumentException("休憩開始よりも休憩終了の数が多いです")
            }
            // 休憩開始に対応する休憩終了がない場合
            if (stamps.count { it.kind == StampKind.Break } - stamps.count { it.kind == StampKind.BreakEnd } > 1) {
                throw IllegalArgumentException("休憩開始に対応する休憩終了がありません")
            }
            return DailyAttendance(date, memberId, stamps)
        }
    }

    // 打刻する
    // オブジェクトの再構築
    fun recordStamp(datetime: LocalDateTime, kind: StampKind): DailyAttendance {
        if (!canRecord(kind, datetime)) {
            throw IllegalArgumentException("不正な打刻です。種別：${kind}, 日時：${datetime}")
        }
        val newStamps = stamps + Stamp.create(kind, datetime)
        return from(baseDate, memberId, newStamps)
    }

    // 打刻可能かどうかを返す
    private fun canRecord(kind: StampKind, datetime: LocalDateTime): Boolean {
        return availableStampKinds().contains(kind) && availableStampTimeToRecord(datetime)
    }

    // 打刻可能な時間かどうかを返す
    private fun availableStampTimeToRecord(datetime: LocalDateTime): Boolean {
        return if (stamps.isEmpty()) {
            // 基準日の0時0分以降であること
            baseDate.atTime(0, 0) <= datetime
        } else {
            // 直前の打刻よりも後の時間であること
            stamps.maxBy { it.datetime }.datetime <= datetime
        }
    }

    // 打刻可能な種類を返す
    private fun availableStampKinds(): Set<StampKind> {
        val sorted = stamps.sortedBy { it.datetime }
        // 打刻履歴がない場合
        return if (stamps.isEmpty()) {
            setOf(StampKind.Start)
        } else if (sorted.last().kind == StampKind.Start) {
            // 最後の打刻が出勤の場合　→　休憩開始、退勤
            setOf(StampKind.Break, StampKind.End)
        } else if (sorted.last().kind == StampKind.Break) {
            // 最後の打刻が休憩開始の場合　→　休憩終了
            setOf(StampKind.BreakEnd)
        } else if (sorted.last().kind == StampKind.BreakEnd) {
            // 最後の打刻が休憩終了の場合　→　休憩開始、退勤
            setOf(StampKind.Break, StampKind.End)
        } else {
            // 最後の打刻が退勤の場合　→　打刻できない
            emptySet()
        }
    }
}

class Stamp(
    val stampId: UUID,
    val kind: StampKind,
    val datetime: LocalDateTime
){
    companion object {
        fun create(kind: StampKind, datetime: LocalDateTime): Stamp =
            Stamp(UUID.randomUUID(), kind, datetime)
    }
}

enum class StampKind {
    Start,
    Break,
    BreakEnd,
    End
}

data class EmployeeId(val value: UUID)
