package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.enums.Status;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;
import org.example.account_processing.service.AccountBlockingService;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.CardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AccountBlockingServiceImpl implements AccountBlockingService {

    private final AccountService accountService;
    private final CardService cardService;

    @Override
    @Transactional
    public void blockAccount(Account account, String reason) {
        log.warn("Blocking account {} due to: {}", account.getId(), reason);

        account.setStatus(Status.FROZEN);
        accountService.saveAccount(account);

        List<Card> cards = cardService.findByAccountId(account.getId());
        for (Card card : cards) {
            card.setStatus(Status.FROZEN);
            cardService.saveCard(card);
            log.warn("Card {} frozen ", card.getId());
        }
        
        log.warn("Account {} and {} associated cards frozen ",
                account.getId(), cards.size());
    }
}
