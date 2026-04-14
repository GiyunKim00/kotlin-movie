package movie.domain.payment

@JvmInline
value class Money(
    val amount: Int,
) {
    init {
        require(amount >= 0) { "금액은 0 이상이어야 합니다." }
    }

    operator fun plus(other: Money): Money = Money(amount + other.amount)

    operator fun minus(other: Money): Money = Money(amount - other.amount)

    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    fun percent(rate: Double): Money = Money((amount * rate).toInt())
}
