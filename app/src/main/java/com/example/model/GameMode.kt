package com.example.model

enum class GameMode(
    val code: String,
    val titleEn: String,
    val titleBn: String,
    val tagEn: String,
    val tagBn: String,
    val defaultEntryFee: Int,
    val maxPlayers: Int,
    val totalPrizeMoney: Int,
    val adminCommission: Int,
    val descriptionEn: String,
    val descriptionBn: String
) {
    LONE_WOLF(
        code = "LW",
        titleEn = "Lone Wolf — 1v1",
        titleBn = "লোন উলফ — ১v১",
        tagEn = "1v1 Duel",
        tagBn = "১v১ লড়াই",
        defaultEntryFee = 25,
        maxPlayers = 2,
        totalPrizeMoney = 40,
        adminCommission = 10,
        descriptionEn = "2 Players • Entry ₹25 • Winner ₹40 • Commission ₹10",
        descriptionBn = "২ জন খেলোয়াড় • এন্ট্রি ₹২৫ • বিজয়ী ₹৪০ • কমিশন ₹১০"
    ),
    CLASH_SQUAD(
        code = "CS",
        titleEn = "Clash Squad — 4v4",
        titleBn = "ক্ল্যাশ স্কোয়াড — ৪v৪",
        tagEn = "4v4 Squad",
        tagBn = "৪v৪ স্কোয়াড",
        defaultEntryFee = 20,
        maxPlayers = 8,
        totalPrizeMoney = 120,
        adminCommission = 40,
        descriptionEn = "8 Players (2 Teams) • Entry ₹20 • Winning Team ₹120 (₹35/₹30/₹30/₹25) • Commission ₹40",
        descriptionBn = "৮ জন খেলোয়াড় (২ টিম) • এন্ট্রি ₹২০ • বিজয়ী টিম ₹১২০ (₹৩৫/₹৩০/₹৩০/₹২৫) • কমিশন ₹৪০"
    ),
    BATTLE_ROYALE(
        code = "BR",
        titleEn = "Battle Royale — 40 Players",
        titleBn = "ব্যাটেল রয়্যাল — ৪০ জন খেলোয়াড়",
        tagEn = "40P Survival",
        tagBn = "৪০ জন সারভাইভাল",
        defaultEntryFee = 20,
        maxPlayers = 40,
        totalPrizeMoney = 570,
        adminCommission = 230,
        descriptionEn = "40 Players • Entry ₹20 • Total Prize ₹570 (Placement ₹460 + Kills ₹110) • Commission ₹230",
        descriptionBn = "৪০ জন খেলোয়াড় • এন্ট্রি ₹২০ • মোট প্রাইজ ₹৫৭০ (প্লেসমেন্ট ₹৪৬০ + কিল ₹১১০) • কমিশন ₹২৩০"
    );

    companion object {
        val BR_PLACEMENT_PRIZES = listOf(
            1 to 100,
            2 to 70,
            3 to 50,
            4 to 50,
            5 to 40,
            6 to 30,
            7 to 30,
            8 to 30,
            9 to 30,
            10 to 30
        )

        val BR_KILL_PRIZES = listOf(
            1 to 30,
            2 to 25,
            3 to 20,
            4 to 20,
            5 to 15
        )

        val CS_WINNING_TEAM_PRIZES = listOf(
            1 to 35,
            2 to 30,
            3 to 30,
            4 to 25
        )
    }
}
