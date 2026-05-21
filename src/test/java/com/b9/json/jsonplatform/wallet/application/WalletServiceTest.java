package com.b9.json.jsonplatform.wallet.application;

import com.b9.json.jsonplatform.wallet.domain.Wallet;
import com.b9.json.jsonplatform.wallet.domain.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        wallet = new Wallet(UUID.randomUUID());
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testGetWalletById_Exist() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletById(walletId);

        assertNotNull(result);
        assertEquals(walletId, result.getId());
    }

    @Test
    void testGetWalletById_NotExist() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getWalletById(walletId));
    }

    @Test
    void testIncreaseBalance() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);

        walletService.increaseBalance(walletId, new BigDecimal("100"));

        assertEquals(new BigDecimal("100"), wallet.getBalance());
    }

    @Test
    void testDecreaseBalance_Success() {
        wallet.setBalance(new BigDecimal("200"));

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);

        walletService.decreaseBalance(walletId, new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
    }

    @Test
    void testDecreaseBalance_Insufficient() {
        wallet.setBalance(new BigDecimal("50"));
        BigDecimal tooMuch = new BigDecimal("100");

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.decreaseBalance(walletId, tooMuch));
    }

    @Test
    void testCreateWallet_savesWalletWithZeroBalance() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.createWallet(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void testGetWalletByUserId_Exist() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByUserId(userId);

        assertNotNull(result);
        assertEquals(walletId, result.getId());
    }

    @Test
    void testGetWalletByUserId_NotExist() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getWalletByUserId(userId));
    }

    @Test
    void testIncreaseBalance_WalletNotFound() {
        BigDecimal amount = new BigDecimal("10");
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.increaseBalance(walletId, amount));

        verify(walletRepository, never()).save(any());
    }

    @Test
    void testDecreaseBalance_WalletNotFound() {
        BigDecimal amount = new BigDecimal("10");
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.decreaseBalance(walletId, amount));

        verify(walletRepository, never()).save(any());
    }

    @Test
    void testDecreaseBalance_ExactBalance_leavesZero() {
        wallet.setBalance(new BigDecimal("100"));
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any())).thenReturn(wallet);

        walletService.decreaseBalance(walletId, new BigDecimal("100"));

        assertEquals(0, wallet.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testDecreaseBalance_OneCentOverBalance_throwsInsufficient() {
        wallet.setBalance(new BigDecimal("100.00"));
        BigDecimal overAmount = new BigDecimal("100.01");
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class,
                () -> walletService.decreaseBalance(walletId, overAmount));

        verify(walletRepository, never()).save(any());
    }
}