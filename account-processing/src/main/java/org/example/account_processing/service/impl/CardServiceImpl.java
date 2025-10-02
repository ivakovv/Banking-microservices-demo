package org.example.account_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.account_processing.enums.Status;
import org.example.account_processing.model.Account;
import org.example.account_processing.model.Card;

import java.util.List;
import org.example.account_processing.repository.CardRepository;
import org.example.account_processing.service.AccountService;
import org.example.account_processing.service.CardService;
import org.example.account_processing.util.CardNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final CardNumberGenerator cardNumberGenerator;

    @Override
    @Transactional
    public Card createCard(Card card) {

        Account account = accountService.findById(card.getAccount().getId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found with id: " + card.getAccount().getId());
        }
        
        if (account.getStatus() == Status.ARRESTED || account.getStatus() == Status.CLOSED
                || account.getStatus() == Status.FROZEN) {
            log.warn("Cannot create card for blocked account: {}", account.getId());
            throw new IllegalStateException("Cannot create card for blocked account: " + account.getId());
        }

        card.setCardId(cardNumberGenerator.generateUniqueCardNumber());

        card.setStatus(Status.OPENED);
        
        Card savedCard = cardRepository.save(card);

        account.setCardExist(true);
        accountService.saveAccount(account);
        
        log.info("Successfully created card with id: {} for account: {}, cardId: {}, paymentSystem: {}", 
                savedCard.getId(), account.getId(), savedCard.getCardId(), savedCard.getPaymentSystem());
        
        return savedCard;
    }

    @Override
    public Card findById(String cardId) {
        return cardRepository.findByCardId(cardId).orElse(null);
    }

    @Override
    @Transactional
    public Card saveCard(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public List<Card> findByAccountId(Long accountId) {
        return cardRepository.findByAccountId(accountId);
    }
}
