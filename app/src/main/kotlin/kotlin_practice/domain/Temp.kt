//package kotlin_practice.domain
//
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.util.*
//
//class Temp {
//}
//
//
//class DailyAttendance2(
//    val baseDate: LocalDate,
//    val memberId: MemberId,
//    val stamps: List<Stamp2>
//){
//    companion object {
//        fun create(date: LocalDate, memberId: MemberId): DailyAttendance2 {
//            return DailyAttendance2(date, memberId, emptyList())
//        }
//
//        fun from(
//            date: LocalDate,
//            memberId: MemberId,
//            stamps: List<Stamp2>,
//        ): DailyAttendance2 {
//            // 始業が存在しない場合
//            if (stamps.none { it is Stamp2.Start }) {
//                throw IllegalArgumentException("始業が打刻されていません")
//            }
//            // 始業打刻が複数ある場合
//            if (stamps.count { it is Stamp2.Start } > 1) {
//                throw IllegalArgumentException("始業打刻が複数あります")
//            }
//            // 終業打刻が複数ある場合
//            if (stamps.count { it is Stamp2.End } > 1) {
//                throw IllegalArgumentException("終業打刻が複数あります")
//            }
//            // 休憩開始よりも休憩終了の数が多い場合
//            if (stamps.count { it is Stamp2.Break } < stamps.count { it is Stamp2.BreakEnd }) {
//                throw IllegalArgumentException("休憩開始よりも休憩終了の数が多いです")
//            }
//            // 休憩開始に対応する休憩終了がない場合
//            if (stamps.count { it is Stamp2.Break } - stamps.count { it is Stamp2.BreakEnd } > 1) {
//                throw IllegalArgumentException("休憩開始に対応する休憩終了がありません")
//            }
//            return DailyAttendance2(date, memberId, stamps)
//        }
//    }
//
//    // 打刻する
//    // オブジェクトの再構築
//    fun <T> recordStamp(datetime: LocalDateTime, stamp: T): DailyAttendance {
//        val validKinds = availableStampKinds()
//        if (!validKinds.contains(kind)) {
//            throw IllegalArgumentException("不正な打刻種別です: $kind")
//        }
//        val stampToRecord = Stamp(UUID.randomUUID(), StampKind.Start, datetime)
//        val newStamps = stamps + stampToRecord
//        return DailyAttendance.from(baseDate, memberId, newStamps)
//    }
//
//    // 打刻可能かどうかを返す
//    private fun isStampAvailable(stamp: Stamp2): Boolean {
//        return when (stamp) {
//            is Stamp2.Start -> stamps.none { it is Stamp2.Start }
//            is Stamp2.Break -> stamps.any { it is Stamp2.Start } && stamps.count { it is Stamp2.Break } == stamps.count { it is Stamp2.BreakEnd }
//            is Stamp2.BreakEnd -> stamps.any { it is Stamp2.Start } && stamps.count { it is Stamp2.Break } > stamps.count { it is Stamp2.BreakEnd }
//            is Stamp2.End -> stamps.any { it is Stamp2.Start }
//        }
//    }
//
//    fun availableStamps(datetime: LocalDateTime): Set<Stamp2> {
//        if (stamps.isEmpty()) {
//            return setOf(Stamp2.Start(UUID.randomUUID(), datetime))
//        }
//    }
//}
//
//sealed interface Stamp2 {
//    companion object {
//        fun create(stampId: UUID, datetime: LocalDateTime, kind: StampKind): Stamp2 {
//            return when (kind) {
//                StampKind.Start -> Start(stampId, datetime)
//                StampKind.Break -> Break(stampId, datetime)
//                StampKind.BreakEnd -> BreakEnd(stampId, datetime)
//                StampKind.End -> End(stampId, datetime)
//            }
//        }
//
//    }
//
//    class Start(val stampId: UUID, val datetime: LocalDateTime) : Stamp2
//    class Break(val stampId: UUID, val datetime: LocalDateTime) : Stamp2
//    class BreakEnd(val stampId: UUID, val datetime: LocalDateTime) : Stamp2
//    class End(val stampId: UUID, val datetime: LocalDateTime) : Stamp2
//}