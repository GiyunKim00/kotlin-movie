package view

class InputView {
    fun readYesOrNo(message: String): String {
        println(message)
        return readln().trim()
    }

    fun readMovieTitle(): String {
        println("예매할 영화 제목을 입력하세요:")
        return readln().trim()
    }

    fun readDate(): String {
        println("날짜를 입력하세요 (YYYY-MM-DD):")
        return readln().trim()
    }

    fun readScreeningNumber(): Int {
        println("상영 번호를 선택하세요:")
        return readln().trim().toInt()
    }

    fun readSeatNumbers(): String {
        println("예약할 좌석을 입력하세요 (A1, B2):")
        return readln().trim()
    }

    fun readPointAmount(): Int {
        println("사용할 포인트를 입력하세요 (없으면 0):")
        return readln().trim().toInt()
    }

    fun readPaymentMethod(): Int {
        println("결제 수단을 선택하세요:")
        println("1) 신용카드(5% 할인)")
        println("2) 현금(2% 할인)")
        return readln().trim().toInt()
    }
}