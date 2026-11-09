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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.model.LeaderboardEntry
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
fun LeaderboardScreen(
    leaderboard: List<LeaderboardEntry>,
    currentUserId: String?,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var filterType by remember { mutableStateOf(0) } // 0: Earnings, 1: Kills, 2: Wins

    val sortedList = remember(leaderboard, filterType) {
        when (filterType) {
            0 -> leaderboard.sortedByDescending { it.totalEarnings }
            1 -> leaderboard.sortedByDescending { it.totalKills }
            else -> leaderboard.sortedByDescending { it.matchesWon }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingDarkBackground)
            .testTag("leaderboard_screen"),
        contentPadding = PaddingValues(14.dp, 14.dp, 14.dp, 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Leaderboard",
                    tint = GamingGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "টুর্নামেন্ট লিডারবোর্ড" else "Tournament Leaderboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ভারতের শীর্ষ ফ্রি ফায়ার চ্যাম্পিয়নরা" else "Top Free Fire Champions Across India",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs: Earnings | Kills | Wins
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GamingSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LeaderboardFilterBtn(
                    label = if (language == AppLanguage.BENGALI) "আয় (টাকা)" else "Earnings (₹)",
                    isSelected = filterType == 0,
                    onClick = { filterType = 0 },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardFilterBtn(
                    label = if (language == AppLanguage.BENGALI) "মোট কিল" else "Kills 🔥",
                    isSelected = filterType == 1,
                    onClick = { filterType = 1 },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardFilterBtn(
                    label = if (language == AppLanguage.BENGALI) "বিজয়" else "Wins 🏆",
                    isSelected = filterType == 2,
                    onClick = { filterType = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Top 3 Podium
        if (sortedList.size >= 3) {
            item {
                PodiumView(top3 = sortedList.take(3), filterType = filterType, language = language)
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Remaining Rankings
        itemsIndexed(sortedList) { index, entry ->
            val rank = index + 1
            val isCurrentUser = entry.userId == currentUserId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GamingSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCurrentUser) GamingPrimary else GamingBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Rank Circle
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when (rank) {
                                        1 -> GamingGold
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> GamingSurfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$rank",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (rank <= 3) Color.Black else TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.freeFireName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (isCurrentUser) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GamingGreen)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = "YOU", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                            Text(
                                text = "UID: ${entry.freeFireUid}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Stat value according to filter
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = when (filterType) {
                                0 -> "₹${entry.totalEarnings}"
                                1 -> "${entry.totalKills} Kills"
                                else -> "${entry.matchesWon} Wins"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = when (filterType) {
                                0 -> GamingGold
                                1 -> GamingRed
                                else -> GamingSecondary
                            }
                        )
                        Text(
                            text = "${entry.matchesWon}W • ${entry.totalKills}K",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumView(top3: List<LeaderboardEntry>, filterType: Int, language: AppLanguage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver)
        if (top3.size > 1) {
            PodiumPillar(entry = top3[1], rank = 2, height = 130.dp, color = Color(0xFFC0C0C0), filterType = filterType)
        }

        // 1st Place (Gold)
        if (top3.isNotEmpty()) {
            PodiumPillar(entry = top3[0], rank = 1, height = 160.dp, color = GamingGold, filterType = filterType)
        }

        // 3rd Place (Bronze)
        if (top3.size > 2) {
            PodiumPillar(entry = top3[2], rank = 3, height = 110.dp, color = Color(0xFFCD7F32), filterType = filterType)
        }
    }
}

@Composable
fun PodiumPillar(entry: LeaderboardEntry, rank: Int, height: androidx.compose.ui.unit.Dp, color: Color, filterType: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Text(
            text = entry.freeFireName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1
        )
        Text(
            text = when (filterType) {
                0 -> "₹${entry.totalEarnings}"
                1 -> "${entry.totalKills} Kills"
                else -> "${entry.matchesWon} Wins"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(85.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.35f), GamingSurface)
                    )
                )
                .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = "Rank $rank",
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "#$rank",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
            }
        }
    }
}

@Composable
fun LeaderboardFilterBtn(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) GamingPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}
