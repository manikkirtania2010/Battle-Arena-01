package com.example.ui.screens

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.GameMode
import com.example.model.Tournament
import com.example.model.TournamentStatus
import com.example.ui.components.HeroBannerCard
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

@Composable
fun TournamentsScreen(
    tournaments: List<Tournament>,
    currentUserId: String?,
    language: AppLanguage,
    selectedMode: GameMode?,
    selectedStatus: TournamentStatus?,
    onSelectMode: (GameMode?) -> Unit,
    onSelectStatus: (TournamentStatus?) -> Unit,
    onTournamentClick: (Tournament) -> Unit,
    onJoinTournament: (String) -> Unit,
    onReferClick: () -> Unit,
    onTick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000L)
            onTick()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .testTag("tournaments_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Banner
        item {
            Box(modifier = Modifier.padding(14.dp)) {
                HeroBannerCard(
                    language = language,
                    onReferClick = onReferClick
                )
            }
        }

        // Mode Filter Bar (All, Lone Wolf, Clash Squad, Battle Royale)
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                Text(
                    text = if (language == AppLanguage.BENGALI) "গেম মোড সিলেক্ট করুন" else "Select Tournament Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        ModeFilterChip(
                            label = if (language == AppLanguage.BENGALI) "সকল মোড" else "All Modes",
                            isSelected = selectedMode == null,
                            icon = Icons.Default.SportsEsports,
                            onClick = { onSelectMode(null) }
                        )
                    }
                    items(GameMode.values()) { mode ->
                        ModeFilterChip(
                            label = AppStrings.gameModeTitle(mode, language),
                            isSelected = selectedMode == mode,
                            icon = when (mode) {
                                GameMode.LONE_WOLF -> Icons.Default.Person
                                GameMode.CLASH_SQUAD -> Icons.Default.Groups
                                GameMode.BATTLE_ROYALE -> Icons.Default.Shield
                            },
                            onClick = { onSelectMode(mode) }
                        )
                    }
                }
            }
        }

        // Status Filter Chips (All, Upcoming, Live, Completed)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterChip(
                    label = AppStrings.tabAll(language),
                    isSelected = selectedStatus == null,
                    onClick = { onSelectStatus(null) }
                )
                StatusFilterChip(
                    label = AppStrings.tabUpcoming(language),
                    isSelected = selectedStatus == TournamentStatus.UPCOMING,
                    onClick = { onSelectStatus(TournamentStatus.UPCOMING) }
                )
                StatusFilterChip(
                    label = AppStrings.tabLive(language),
                    isSelected = selectedStatus == TournamentStatus.LIVE,
                    onClick = { onSelectStatus(TournamentStatus.LIVE) }
                )
                StatusFilterChip(
                    label = AppStrings.tabCompleted(language),
                    isSelected = selectedStatus == TournamentStatus.COMPLETED,
                    onClick = { onSelectStatus(TournamentStatus.COMPLETED) }
                )
            }
        }

        // Legal Notice Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingSurfaceVariant.copy(alpha = 0.6f))
                    .border(0.6.dp, GamingBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = AppStrings.legalDisclaimer(language),
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }

        // Tournaments List
        if (tournaments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "No Tournaments",
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "কোনো টুর্নামেন্ট পাওয়া যায়নি" else "No tournaments found",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(tournaments, key = { it.id }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    isJoined = currentUserId != null && tournament.isUserJoined(currentUserId),
                    language = language,
                    onClick = { onTournamentClick(tournament) },
                    onJoin = { onJoinTournament(tournament.id) },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun TournamentCard(
    tournament: Tournament,
    isJoined: Boolean,
    language: AppLanguage,
    onClick: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modeColor = when (tournament.mode) {
        GameMode.LONE_WOLF -> GamingPrimary
        GameMode.CLASH_SQUAD -> GamingSecondary
        GameMode.BATTLE_ROYALE -> GamingGold
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isJoined) GamingGreen.copy(alpha = 0.8f) else GamingBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Mode Tag + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(modeColor.copy(alpha = 0.15f))
                            .border(1.dp, modeColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tournament.getModeTitle(language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = modeColor
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${tournament.mapName}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Status Chip
                when (tournament.status) {
                    TournamentStatus.LIVE -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingRed.copy(alpha = 0.2f))
                                .border(1.dp, GamingRed, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "● LIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = GamingRed
                            )
                        }
                    }
                    TournamentStatus.CONFIRMED -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingGold.copy(alpha = 0.25f))
                                .border(1.dp, GamingGold, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ STARTS IN ${tournament.getRemainingCountdownSeconds()}s",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = GamingGold
                            )
                        }
                    }
                    TournamentStatus.MATCHMAKING -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSecondary.copy(alpha = 0.2f))
                                .border(1.dp, GamingSecondary, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.BENGALI) "⚔️ প্রতিপক্ষের অপেক্ষা (১/২)" else "⚔️ WAITING OPPONENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingSecondary
                            )
                        }
                    }
                    TournamentStatus.UPCOMING -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = AppStrings.tabUpcoming(language),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingSecondary
                            )
                        }
                    }
                    TournamentStatus.UNSUCCESSFUL -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingRed.copy(alpha = 0.2f))
                                .border(1.dp, GamingRed, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (tournament.isRefundProcessed) "REFUNDED" else "UNSUCCESSFUL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingRed
                            )
                        }
                    }
                    TournamentStatus.COMPLETED -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(GamingSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = AppStrings.tabCompleted(language),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = tournament.getTitle(language),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row: Entry Fee & Prize Pool
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GamingSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = AppStrings.entryFee(language),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${tournament.entryFee}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = GamingPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = AppStrings.prizePool(language),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${tournament.totalPrizePool}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = GamingGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slots Progress Bar
            val progress = (tournament.filledSlots.toFloat() / tournament.totalSlots.toFloat()).coerceIn(0f, 1f)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${AppStrings.slots(language)}: ${tournament.filledSlots}/${tournament.totalSlots}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = "${AppStrings.startTime(language)}: ${tournament.startTimeFormatted}",
                        fontSize = 11.sp,
                        color = GamingSecondary
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = modeColor,
                    trackColor = GamingSurfaceVariant
                )
            }

            // Secure Registration Status indicator (Room credentials protected inside Match Details)
            if (isJoined) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingGreen.copy(alpha = 0.12f))
                        .border(1.dp, GamingGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Verified Pass",
                            tint = GamingGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) "✓ অফিসিয়াল এন্ট্রি নিশ্চিত (পেইড)" else "✓ Official Registration Confirmed (Paid)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GamingGreen
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BENGALI) "রুম পাস ভেতরে →" else "Room Pass Inside →",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GamingGreen
                    )
                }
            } else if (tournament.status == TournamentStatus.COMPLETED && tournament.winnerSummaryEn != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingGold.copy(alpha = 0.1f))
                        .border(0.8.dp, GamingGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🏆 " + if (language == AppLanguage.BENGALI) tournament.winnerSummaryBn else tournament.winnerSummaryEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GamingGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("details_btn_${tournament.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GamingSurfaceVariant,
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "বিস্তারিত দেখুন" else "Details & Rules",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (tournament.status == TournamentStatus.UPCOMING || tournament.status == TournamentStatus.LIVE) {
                    Button(
                        onClick = onJoin,
                        enabled = !isJoined && !tournament.isFull,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("join_btn_${tournament.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isJoined) GamingGreen else GamingPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = GamingSurfaceVariant,
                            disabledContentColor = TextMuted
                        )
                    ) {
                        Text(
                            text = when {
                                isJoined -> if (language == AppLanguage.BENGALI) "যোগদান সম্পন্ন ✓" else "Joined ✓"
                                tournament.isFull -> AppStrings.matchFull(language)
                                else -> "${AppStrings.joinTournament(language)} (₹${tournament.entryFee})"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeFilterChip(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) GamingPrimary else GamingSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) GamingPrimary else GamingBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextSecondary
            )
        }
    }
}

@Composable
fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) GamingSecondary.copy(alpha = 0.2f) else GamingSurface)
            .border(
                1.dp,
                if (isSelected) GamingSecondary else GamingBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GamingSecondary else TextSecondary
        )
    }
}
