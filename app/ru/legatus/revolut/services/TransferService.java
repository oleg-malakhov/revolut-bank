package ru.legatus.revolut.services;

import java.math.BigDecimal;
import java.util.List;

import ru.legatus.revolut.model.Transfer;

public interface TransferService {

   /**
    * Deposit money on Account with the specified id
    * @param accountId Account ID
    * @param money money to deposit
    * @throws AccountNotFoundException if Account with the provided id doesn't exist
    */
   void deposit(int accountId, BigDecimal money);

   /**
    * Withdraw money from Account with the specified id
    * @param accountId Account ID
    * @param money money to withdraw
    * @throws AccountNotFoundException if Account with the provided id doesn't exist
    * @throws NotEnoughMoneyException if Account doesn't have the required sum
    */
   void withdraw(int accountId, BigDecimal money);

   /**
    * Transfer money between two Accounts with the specified IDs
    * @param accountIdFrom source Account to get money from
    * @param accountIdTo target Account to put money to
    * @param money moeney to transfer
    * @param message message attached to the transfer.
    * @throws AccountNotFoundException if any Account doesn't exist
    * @throws NotEnoughMoneyException if the source Account doesn't have the required sum
    */
   void transfer(int accountIdFrom, int accountIdTo, BigDecimal money, String message);

   /**
    * Lists all Account transfers in descending order by date.<br />
    * Money sum is negative if it was token from the Account.<br />
    * Money sum is positive if it was put into the Account. <br />
    * @param accountId Account ID to show transfers for
    * @return list of {@link Transfer} instances
    * @throws AccountNotFoundException if any Account doesn't exist
    */
   List<Transfer> listTransfers(int accountId);
}
