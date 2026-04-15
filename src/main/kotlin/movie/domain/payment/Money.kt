package movie.domain.payment

@JvmInline
value class Money(
    val amount: Int,
) {
    init {
        require(amount >= 0) { "금액은 0 이상이어야 합니다." }
    }

    operator fun plus(other: Money): Money = Money(amount + other.amount)

    operator fun minus(other: Money): Money {
        val result = amount - other.amount
        return Money(maxOf(0, result))
    }

    operator fun minus(other: Int): Money {
        val result = amount - other
        return Money(maxOf(0, result))
    }

    operator fun compareTo(other: Int): Int = amount.compareTo(other)

    fun percent(rate: Double): Money = Money((amount * rate).toInt())
}
