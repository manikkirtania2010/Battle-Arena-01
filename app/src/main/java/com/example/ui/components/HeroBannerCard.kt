package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingGold
import com.example.ui.theme.GamingPrimary
import com.example.ui.theme.GamingSecondary
import com.example.ui.theme.GamingSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HeroBannerCard(
    language: AppLanguage,
    onReferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GamingSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, GamingBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.img_battle_arena_banner_1793539230302),
                contentDescription = "Tournament Arena Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic Dark Esports Gradient Mask
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                GamingDarkBackground.copy(alpha = 0.94f),
                                GamingDarkBackground.copy(alpha = 0.70f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingPrimary)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.BENGALI) "অফিসিয়াল টুর্নামেন্ট" else "OFFICIAL ESPORTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingSecondary.copy(alpha = 0.2f))
                            .border(0.8.dp, GamingSecondary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "FREE FIRE INDIA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GamingSecondary
                        )
                    }
                }

                // Center Title
                Column {
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ব্যাটেল এরিনা টুর্নামেন্ট" else "BATTLE ARENA TOURNAMENT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (language == AppLanguage.BENGALI) 
                            "১v১ লোন উলফ • ৪v৪ ক্ল্যাশ স্কোয়াড • ৪০ জন বিআর" 
                            else "1v1 Lone Wolf • 4v4 Clash Squad • 40P Battle Royale",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GamingGold
                    )
                }

                // Bottom strip with quick refer & earn banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GamingDarkBackground.copy(alpha = 0.85f))
                        .border(0.8.dp, GamingBorder, RoundedCornerShape(10.dp))
                        .clickable { onReferClick() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("hero_refer_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Refer",
                            tint = GamingGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.BENGALI) 
                                "রেফার করে প্রতি সফল আমন্ত্রণে ₹৫ আয় করুন!" 
                                else "Refer friends & earn ₹5 Bonus each!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.BENGALI) "ইনভাইট ➜" else "Invite ➜",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GamingPrimary
                    )
                }
            }
        }
    }
}
