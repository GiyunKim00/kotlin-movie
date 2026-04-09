package domain.reservation

import sun.awt.shell.ShellFolder.COLUMN_SIZE

enum class SeatGrade(val money: Int) {
    S(money = 18_000),
    A(money = 15_000),
    B(money = 12_000);

    companion object {
        private const val ROW_SIZE = 5
        private const val COLUMN_SIZE = 4

        fun grantGrade(rowIndex: Int, columnIndex: Int): SeatGrade {
            require(rowIndex in 0 until ROW_SIZE) { "유효하지 않은 행 인덱스입니다." }
            require(columnIndex in 0 until COLUMN_SIZE) { "유효하지 않은 열 인덱스입니다." }

            return when (rowIndex) {
                0, 1 -> B
                2, 3 -> S
                4 -> A
                else -> throw IllegalArgumentException("유효하지 않은 좌석 위치입니다.")
            }
        }
    }
}