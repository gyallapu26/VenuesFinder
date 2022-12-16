package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert
import org.junit.Test

class CashRegisterTest {

    @Test
    fun `assert change should be equals to amount paid`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)
        val price = 2500L
        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }
        val actual = cashRegister.performTransaction(price, amountPaid)
        Assert.assertEquals(0L, actual.total)
    }

    @Test
    fun `assert change should be with expected refund amount`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 5) }
            Coin.values().forEach { add(it, 5) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val price = 2680L

        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 2)
        }
        val actual = cashRegister.performTransaction(price, amountPaid)
        Assert.assertEquals(20L, actual.total)
    }

    @Test
    fun `assert change should not be with expected refund amount`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 3) }
            Coin.values().forEach { add(it, 3) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val price = 33500L

        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1) // 10000
            add(Bill.FIFTY_EURO, 4) // 20000
            add(Bill.TEN_EURO, 3) // 3000
            add(Bill.FIVE_EURO, 4) // 2000
        }

        val actual = cashRegister.performTransaction(price, amountPaid)

        Assert.assertNotEquals(400L, actual.total)
    }

    @Test
    fun `assert change refund should contains bill and coin equals expected`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 2) }
            Coin.values().forEach { add(it, 2) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val price = 3354L

        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
        }

        val actual = cashRegister.performTransaction(price, amountPaid)

        Assert.assertEquals(146L, actual.total)
    }

    @Test
    fun `assert change shopper money to minimal amount`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 2) }
            Coin.values().forEach { add(it, 2) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val price = 0L

        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 5) // 10000
            add(Bill.TEN_EURO, 20) // 20000
            add(Bill.FIVE_EURO, 2) // 1000
        }

        val expectedMinimal = Change().apply {
            add(Bill.TWO_HUNDRED_EURO, 1)
            add(Bill.ONE_HUNDRED_EURO, 1)
            add(Bill.TEN_EURO, 1)
        }

        val actual = cashRegister.performTransaction(price, amountPaid)

        Assert.assertEquals(expectedMinimal, actual)
    }

    @Test
    fun `assert cash register should tracks change expected`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 2) }
            Coin.values().forEach { add(it, 2) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val firstPrice = 640L
        val firstAmountPaid = Change().apply {
            add(Bill.FIVE_EURO, 1) // 500
            add(Coin.ONE_EURO, 1) // 100
            add(Coin.FIFTY_CENT, 1) // 50
        }

        val secondPrice = 1550L // amountPaid 1600 = -50

        val secondAmountPaid = Change().apply {
            add(Bill.TEN_EURO, 1) // 1000
            add(Bill.FIVE_EURO, 1) // 500
            add(Coin.ONE_EURO, 1) // 100
        }

        val expectedChange = Change().apply {
            Bill.values().forEach { add(it, 2) }
            Coin.values().forEach { add(it, 2) }
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
            add(Coin.FIFTY_CENT, 1)
            add(Bill.TEN_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 1)
            remove(Coin.TEN_CENT, 1)
            remove(Coin.FIFTY_CENT, 1)
        }

        cashRegister.performTransaction(firstPrice, firstAmountPaid)
        cashRegister.performTransaction(secondPrice, secondAmountPaid)

        Assert.assertEquals(expectedChange, cashRegisterChange)
    }

    @Test
    fun `assert cash register pay back price to shopper money to expected`() {
        val cashRegisterChange = Change().apply {
            Bill.values().forEach { add(it, 2) }
            Coin.values().forEach { add(it, 2) }
        }
        val cashRegister = CashRegister(cashRegisterChange)

        val price = -15650L // 156,50

        val amountPaid = Change.none()

        val expectedPayBackMinimal = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1) // 10000
            add(Bill.FIFTY_EURO, 1) // 5000
            add(Bill.FIVE_EURO, 1) // 500
            add(Coin.ONE_EURO, 1) // 100
            add(Coin.FIFTY_CENT, 1) // 50
        }

        val actual = cashRegister.performTransaction(price, amountPaid)

        Assert.assertEquals(expectedPayBackMinimal, actual)
    }

    @Test(expected = CashRegister.TransactionException::class)
    fun `assert cash register does not have enough change should give exception`() {
        val cashRegisterTotal = Change().apply {
            Bill.values().forEach { add(it, 1) }
        }
        val cashRegister = CashRegister(cashRegisterTotal)

        val price = 640L // amountPaid 650 = -10

        val amountPaid = Change().apply {
            add(Bill.FIVE_EURO, 1) // 500
            add(Coin.ONE_EURO, 1) // 100
            add(Coin.FIFTY_CENT, 1) // 50
        }

        cashRegister.performTransaction(price, amountPaid)

        Assert.fail("An Exception CashRegister: Not enough change in cash register")
    }


}
