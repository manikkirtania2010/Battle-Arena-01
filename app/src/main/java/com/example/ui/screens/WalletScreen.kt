package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.Transaction
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.Wallet
import com.example.model.WithdrawalRequest
import com.example.model.WithdrawalStatus
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingGreen
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingRed
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurface
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    wallet: Wallet,
    transactions: List<Transaction>,
    withdrawals: List<WithdrawalRequest>,
    language: AppLanguage,
    onAddMoneyClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTxFilter by remember { mutableStateOf<TransactionType?>(null) }

    val filteredTransactions = remember(transactions, selectedTxFilter) {
        if (selectedTxFilter == null) transactions
        else if (selectedTxFilter == TransactionType.PRIZE_MONEY || selectedTxFilter == TransactionType.MATCH_WINNING) {
            transactions.filter { it.type == TransactionType.PRIZE_MONEY || it.type == TransactionType.MATCH_WINNING }
        } else {
            transactions.filter { it.type == selectedTxFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(14.dp, 14.dp, 14.dp, 100.dp)
    ) {
        // Main Balance Hero Card
        item {
            WalletHeroCard(
                wallet = wallet,
                language = language,
                onAddMoneyClick = onAddMoneyClick,
                onWithdrawClick = onWithdrawClick
            )
        }

        // Active Withdrawal Requests section
        if (withdrawals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (language == AppLanguage.BENGALI) "উইথড্রয়াল স্ট্যাটাস ট্র্যাকার" else "Withdrawal Requests Tracker",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(withdrawals.take(3), key = { it.id }) { req ->
                WithdrawalStatusCard(req = req, language = language)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Transaction History Header & Filters
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = GamingGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.transactionHistory(language),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transaction Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TxFilterChip(
                        label = AppStrings.tabAll(language),
                        isSelected = selectedTxFilter == null,
                        onClick = { selectedTxFilter = null }
                    )
                }
                item {
                    TxFilterChip(
                        label = if (language == AppLanguage.BENGALI) "ডিপোজিট" else "Deposits",
                        isSelected = selectedTxFilter == TransactionType.ADD_MONEY,
                        onClick = { selectedTxFilter = TransactionType.ADD_MONEY }
                    )
                }
                item {
                    TxFilterChip(
                        label = if (language == AppLanguage.BENGALI) "প্রাইজ জয়" else "Winnings",
                        isSelected = selectedTxFilter == TransactionType.PRIZE_MONEY,
                        onClick = { selectedTxFilter = TransactionType.PRIZE_MONEY }
                    )
                }
                item {
                    TxFilterChip(
                        label = if (language == AppLanguage.BENGALI) "এন্ট্রি ফি" else "Entry Fees",
                        isSelected = selectedTxFilter == TransactionType.ENTRY_FEE,
                        onClick = { selectedTxFilter = TransactionType.ENTRY_FEE }
                    )
                }
                item {
                    TxFilterChip(
                        label = if (language == AppLanguage.BENGALI) "উইথড্রয়াল" else "Withdrawals",
                        isSelected = selectedTxFilter == TransactionType.WITHDRAWAL,
                        onClick = { selectedTxFilter = TransactionType.WITHDRAWAL }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Transaction items
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "কোনো লেনদেন পাওয়া যায়নি" else "No transactions found",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                TransactionItemRow(tx = tx, language = language)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun WalletHeroCard(
    wallet: Wallet,
    language: AppLanguage,
    onAddMoneyClick: () -> Unit,
    onWithdrawClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_hero_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top: Available Total Balance
            Text(
                text = AppStrings.totalBalance(language),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "₹${wallet.totalBalance}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = GamingGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GamingGreen.copy(alpha = 0.15f))
                        .border(1.dp, GamingGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SECURE WALLET",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance Breakdowns: Deposit | Winning | Bonus
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GamingSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = AppStrings.depositBalance(language), fontSize = 11.sp, color = TextSecondary)
                    Text(text = "₹${wallet.depositBalance}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = AppStrings.winningBalance(language), fontSize = 11.sp, color = TextSecondary)
                    Text(text = "₹${wallet.winningBalance}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GamingGreen)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = AppStrings.bonusBalance(language), fontSize = 11.sp, color = TextSecondary)
                    Text(text = "₹${wallet.bonusBalance}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GamingSecondary)
                }
            }

            // Reserved in Withdrawal Row (if any)
            if (wallet.reservedBalance > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingGold.copy(alpha = 0.12f))
                        .border(1.dp, GamingGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Reserved",
                            tint = GamingGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "উইথড্রয়ালে সংরক্ষিত (পেন্ডিং):" else "Reserved in Withdrawal (Pending):",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "₹${wallet.reservedBalance}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Add Money & Withdraw
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddMoneyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("add_money_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.addMoney(language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onWithdrawClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("withdraw_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GamingSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
                ) {
                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Withdraw", tint = GamingGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.withdraw(language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (language == AppLanguage.BENGALI)
                    "🏆 ভার্চুয়াল প্রাইজ ওয়ালেট: উইনিং ব্যালেন্স মালিকের পর্যালোচনার পর ইউপিআই-তে রিয়েল মানি হিসেবে পাঠানো হয়。"
                else
                    "🏆 Virtual Prize Wallet: Winnings are virtual. Real money is manually sent to your UPI by Owner upon verification.",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun WithdrawalStatusCard(req: WithdrawalRequest, language: AppLanguage) {
    val statusColor = when (req.status) {
        WithdrawalStatus.PAID -> GamingGreen
        WithdrawalStatus.APPROVED -> GamingSecondary
        WithdrawalStatus.PROCESSING -> Color(0xFF38BDF8)
        WithdrawalStatus.PENDING -> GamingGold
        WithdrawalStatus.REJECTED -> GamingRed
        WithdrawalStatus.FAILED -> Color(0xFFF97316)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Req #${req.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "• ₹${req.amount}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GamingGold)
                    }
                    Text(text = "${req.method} (${req.payoutDestination})", fontSize = 11.sp, color = TextSecondary)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(0.8.dp, statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = req.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            if (!req.paymentReference.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bank UTR: ${req.paymentReference}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GamingGreen
                )
            }

            if (!req.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reason: ${req.rejectionReason}",
                    fontSize = 10.sp,
                    color = GamingRed
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(tx: Transaction, language: AppLanguage) {
    val sdf = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateStr = remember(tx.timestamp) { sdf.format(Date(tx.timestamp)) }

    val (icon, iconColor, isCredit) = when (tx.type) {
        TransactionType.ADD_MONEY -> Triple(Icons.Default.Add, GamingGreen, true)
        TransactionType.PRIZE_MONEY, TransactionType.MATCH_WINNING -> Triple(Icons.Default.EmojiEvents, GamingGold, true)
        TransactionType.REFERRAL_BONUS -> Triple(Icons.Default.CardGiftcard, GamingSecondary, true)
        TransactionType.ENTRY_FEE -> Triple(Icons.Default.SportsEsports, GamingPrimary, false)
        TransactionType.WITHDRAWAL -> Triple(Icons.Default.ArrowDownward, GamingGold, false)
        TransactionType.REFUND -> Triple(Icons.Default.ArrowUpward, GamingGreen, true)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, GamingBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tx.type.name,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) tx.titleBn else tx.titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dateStr • ID: ${tx.referenceId.take(12)}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${tx.amount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCredit) GamingGreen else TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tx.status.name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (tx.status) {
                        TransactionStatus.SUCCESS -> GamingGreen
                        TransactionStatus.PENDING -> GamingGold
                        TransactionStatus.FAILED -> GamingRed
                    }
                )
            }
        }
    }
}

@Composable
fun TxFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}
