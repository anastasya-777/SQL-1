package ru.topacademy.tests;

import org.junit.jupiter.api.Test;
import ru.topacademy.data.APIHelper;
import ru.topacademy.data.DataHelper;
import ru.topacademy.data.DataMySql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SqlTest {
    @Test
    public void validTransferFromFirstToSecond() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = DataMySql.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCoda());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = APIHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        int amount = DataHelper.generateValidAmount(firstCardBalance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(),
                DataHelper.getSecondCardInfo().getNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = APIHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        assertAll(() -> assertEquals(firstCardBalance - amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance + amount, actualSecondCardBalance));
    }

    @Test
    public void validTransferFromSecondToFirst() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = DataMySql.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCoda());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = APIHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        int amount = DataHelper.generateValidAmount(secondCardBalance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getSecondCardInfo().getNumber(),
                DataHelper.getFirstCardInfo().getNumber(), amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = APIHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        assertAll(() -> assertEquals(firstCardBalance + amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance - amount, actualSecondCardBalance));
    }
}