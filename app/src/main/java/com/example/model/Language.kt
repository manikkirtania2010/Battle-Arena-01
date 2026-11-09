package com.example.model

/**
 * 13 Indian Languages supported in Battle Arena.
 */
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flag: String,
    val isRtl: Boolean = false
) {
    BENGALI("bn", "বাংলা", "Bengali", "🇮🇳"),
    ENGLISH("en", "English", "English", "🌐"),
    HINDI("hi", "हिन्दी", "Hindi", "🇮🇳"),
    ASSAMESE("as", "অসমীয়া", "Assamese", "🇮🇳"),
    ODIA("or", "ଓଡ଼ିଆ", "Odia", "🇮🇳"),
    MARATHI("mr", "मराठी", "Marathi", "🇮🇳"),
    GUJARATI("gu", "ગુજરાતી", "Gujarati", "🇮🇳"),
    PUNJABI("pa", "ਪੰਜਾਬੀ", "Punjabi", "🇮🇳"),
    TAMIL("ta", "தமிழ்", "Tamil", "🇮🇳"),
    TELUGU("te", "తెలుగు", "Telugu", "🇮🇳"),
    KANNADA("kn", "ಕನ್ನಡ", "Kannada", "🇮🇳"),
    MALAYALAM("ml", "മലയാളം", "Malayalam", "🇮🇳"),
    URDU("ur", "اردو", "Urdu", "🇮🇳", isRtl = true);

    val displayName: String get() = "$nativeName ($englishName)"

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return values().find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}

/**
 * Centralized multilingual dictionary for 13 Indian languages with English fallback.
 */
object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ব্যাটেল এরিনা"
        AppLanguage.HINDI -> "बैटल एरीना"
        AppLanguage.ASSAMESE -> "বেটেল এৰিনা"
        AppLanguage.ODIA -> "ବ୍ୟାଟେଲ୍ ଏରିନା"
        AppLanguage.MARATHI -> "बॅटल एरिना"
        AppLanguage.GUJARATI -> "બેટલ એરેના"
        AppLanguage.PUNJABI -> "ਬੈਟਲ ਅਰੀਨਾ"
        AppLanguage.TAMIL -> "பேட்டில் அரீனா"
        AppLanguage.TELUGU -> "బాటిల్ ఎరీనా"
        AppLanguage.KANNADA -> "ಬ್ಯಾಟಲ್ ಅರೀನಾ"
        AppLanguage.MALAYALAM -> "ബാറ്റിൽ അരീന"
        AppLanguage.URDU -> "بیٹل ایرینا"
        AppLanguage.ENGLISH -> "Battle Arena"
    }

    fun freeFireIndia(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ফ্রি ফায়ার ইন্ডিয়া টুর্নামেন্ট"
        AppLanguage.HINDI -> "फ्री फायर इंडिया टूर्नामेंट"
        AppLanguage.ASSAMESE -> "ফ্ৰী ফায়াৰ ইণ্ডিয়া টুৰ্নামেন্ট"
        AppLanguage.ODIA -> "ଫ୍ରି ଫାୟାର୍ ଇଣ୍ଡିଆ ଟୁର୍ଣ୍ଣାମେଣ୍ଟ"
        AppLanguage.MARATHI -> "फ्री फायर इंडिया टूर्नामेंट"
        AppLanguage.GUJARATI -> "ફ્રી ફાયર ઇન્ડિયા ટુર્નામેન્ટ"
        AppLanguage.PUNJABI -> "ਫ੍ਰੀ ਫਾਇਰ ਇੰਡੀਆ ਟੂਰਨਾਮੈਂਟ"
        AppLanguage.TAMIL -> "ஃப்ரீ ஃபயர் இந்தியா போட்டிகள்"
        AppLanguage.TELUGU -> "ఫ్రీ ఫైర్ ఇండియా టోర్నమెంట్లు"
        AppLanguage.KANNADA -> "ಫ್ರೀ ಫೈರ್ ಇಂಡಿಯಾ ಪಂದ್ಯಾವಳಿಗಳು"
        AppLanguage.MALAYALAM -> "ഫ്രീ ഫയർ ഇന്ത്യ ടൂർണമെന്റുകൾ"
        AppLanguage.URDU -> "فری فائر انڈیا ٹورنامنٹس"
        AppLanguage.ENGLISH -> "Free Fire India Esports"
    }

    // Bottom Navigation
    fun navTournaments(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "টুর্নামেন্ট"
        AppLanguage.HINDI -> "मैचेस"
        AppLanguage.ASSAMESE -> "টুৰ্নামেন্ট"
        AppLanguage.ODIA -> "ମ୍ୟାଚ୍"
        AppLanguage.MARATHI -> "सामने"
        AppLanguage.GUJARATI -> "મેચો"
        AppLanguage.PUNJABI -> "ਮੈਚ"
        AppLanguage.TAMIL -> "போட்டிகள்"
        AppLanguage.TELUGU -> "మ్యాచ్‌లు"
        AppLanguage.KANNADA -> "ಪಂದ್ಯಗಳು"
        AppLanguage.MALAYALAM -> "മത്സരങ്ങൾ"
        AppLanguage.URDU -> "میچز"
        AppLanguage.ENGLISH -> "Matches"
    }

    fun navWallet(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ওয়ালেট"
        AppLanguage.HINDI -> "वॉलेट"
        AppLanguage.ASSAMESE -> "ৱালেট"
        AppLanguage.ODIA -> "ୱାଲେଟ୍"
        AppLanguage.MARATHI -> "वॉलेट"
        AppLanguage.GUJARATI -> "વોલેટ"
        AppLanguage.PUNJABI -> "ਵਾਲਿਟ"
        AppLanguage.TAMIL -> "வாலட்"
        AppLanguage.TELUGU -> "వాలెట్"
        AppLanguage.KANNADA -> "ವಾಲೆಟ್"
        AppLanguage.MALAYALAM -> "വാലറ്റ്"
        AppLanguage.URDU -> "والیٹ"
        AppLanguage.ENGLISH -> "Wallet"
    }

    fun navLeaderboard(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "লিডারবোর্ড"
        AppLanguage.HINDI -> "लीडरबोर्ड"
        AppLanguage.ASSAMESE -> "লিডাৰবৰ্ড"
        AppLanguage.ODIA -> "ଲିଡରବୋର୍ଡ"
        AppLanguage.MARATHI -> "लीडरबोर्ड"
        AppLanguage.GUJARATI -> "લીડરબોર્ડ"
        AppLanguage.PUNJABI -> "ਲੀਡਰਬੋਰਡ"
        AppLanguage.TAMIL -> "தரவரிசை"
        AppLanguage.TELUGU -> "లీడర్‌బోర్డ్"
        AppLanguage.KANNADA -> "ಶ್ರೇಯಾಂಕಗಳು"
        AppLanguage.MALAYALAM -> "ലീഡർബോർഡ്"
        AppLanguage.URDU -> "لیڈر بورڈ"
        AppLanguage.ENGLISH -> "Ranks"
    }

    fun navProfile(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "প্রোফাইল"
        AppLanguage.HINDI -> "प्रोफाइल"
        AppLanguage.ASSAMESE -> "প্ৰফাইল"
        AppLanguage.ODIA -> "ପ୍ରୋଫାଇଲ୍"
        AppLanguage.MARATHI -> "प्रोफाइल"
        AppLanguage.GUJARATI -> "પ્રોફાઇલ"
        AppLanguage.PUNJABI -> "ਪ੍ਰੋਫਾਈਲ"
        AppLanguage.TAMIL -> "சுயவிவரம்"
        AppLanguage.TELUGU -> "ప్రొఫైల్"
        AppLanguage.KANNADA -> "ಪ್ರೊಫೈಲ್"
        AppLanguage.MALAYALAM -> "പ്രൊഫൈൽ"
        AppLanguage.URDU -> "پروفائل"
        AppLanguage.ENGLISH -> "Profile"
    }

    fun navAdmin(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "অ্যাডমিন"
        AppLanguage.HINDI -> "एडमिन"
        AppLanguage.ASSAMESE -> "এডমিন"
        AppLanguage.ODIA -> "ଅଡମିନ୍"
        AppLanguage.MARATHI -> "प्रशासक"
        AppLanguage.GUJARATI -> "એડમિન"
        AppLanguage.PUNJABI -> "ਐਡਮਿਨ"
        AppLanguage.TAMIL -> "நிர்வாகி"
        AppLanguage.TELUGU -> "అడ్మిన్"
        AppLanguage.KANNADA -> "ಅಡ್ಮಿನ್"
        AppLanguage.MALAYALAM -> "അഡ്മിൻ"
        AppLanguage.URDU -> "ایڈمن"
        AppLanguage.ENGLISH -> "Admin"
    }

    // Tabs & Match Filters
    fun tabAll(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "সব ম্যাচ"
        AppLanguage.HINDI -> "सभी मैच"
        AppLanguage.ASSAMESE -> "সকলো মেচ"
        AppLanguage.ODIA -> "ସମସ୍ତ ମ୍ୟାଚ୍"
        AppLanguage.MARATHI -> "सर्व सामने"
        AppLanguage.GUJARATI -> "બધી મેચો"
        AppLanguage.PUNJABI -> "ਸਾਰੇ ਮੈਚ"
        AppLanguage.TAMIL -> "அனைத்து போட்டிகள்"
        AppLanguage.TELUGU -> "అన్ని మ్యాచ్‌లు"
        AppLanguage.KANNADA -> "ಎಲ್ಲಾ ಪಂದ್ಯಗಳು"
        AppLanguage.MALAYALAM -> "എല്ലാ മത്സരങ്ങളും"
        AppLanguage.URDU -> "تمام میچز"
        AppLanguage.ENGLISH -> "All Matches"
    }

    fun tabUpcoming(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "আসন্ন"
        AppLanguage.HINDI -> "आगामी"
        AppLanguage.ASSAMESE -> "অনাগত"
        AppLanguage.ODIA -> "ଆଗାମୀ"
        AppLanguage.MARATHI -> "आगामी"
        AppLanguage.GUJARATI -> "આગામી"
        AppLanguage.PUNJABI -> "ਆਉਣ ਵਾਲੇ"
        AppLanguage.TAMIL -> "வரவிருக்கும்"
        AppLanguage.TELUGU -> "రాబోయేవి"
        AppLanguage.KANNADA -> "ಮುಂಬರುವ"
        AppLanguage.MALAYALAM -> "വരാനിരിക്കുന്നവ"
        AppLanguage.URDU -> "آنے والے"
        AppLanguage.ENGLISH -> "Upcoming"
    }

    fun tabLive(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "লাইভ"
        AppLanguage.HINDI -> "लाइव"
        AppLanguage.ASSAMESE -> "লাইভ"
        AppLanguage.ODIA -> "ଲାଇଭ୍"
        AppLanguage.MARATHI -> "थेट"
        AppLanguage.GUJARATI -> "લાઇવ"
        AppLanguage.PUNJABI -> "ਲਾਈਵ"
        AppLanguage.TAMIL -> "நேரலை"
        AppLanguage.TELUGU -> "లైవ్"
        AppLanguage.KANNADA -> "ಲೈವ್"
        AppLanguage.MALAYALAM -> "തത്സമയം"
        AppLanguage.URDU -> "لائیو"
        AppLanguage.ENGLISH -> "Live"
    }

    fun tabCompleted(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "সম্পন্ন"
        AppLanguage.HINDI -> "समाप्त"
        AppLanguage.ASSAMESE -> "সম্পূৰ্ণ"
        AppLanguage.ODIA -> "ସମାପ୍ତ"
        AppLanguage.MARATHI -> "पूर्ण झाले"
        AppLanguage.GUJARATI -> "પૂર્ણ"
        AppLanguage.PUNJABI -> "ਸਮਾਪਤ"
        AppLanguage.TAMIL -> "முடிந்தவை"
        AppLanguage.TELUGU -> "పూర్తయినవి"
        AppLanguage.KANNADA -> "ಪೂರ್ಣಗೊಂಡಿದೆ"
        AppLanguage.MALAYALAM -> "പൂർത്തിയായവ"
        AppLanguage.URDU -> "مکمل شدہ"
        AppLanguage.ENGLISH -> "Completed"
    }

    // Actions
    fun joinTournament(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "অংশগ্রহণ করুন"
        AppLanguage.HINDI -> "भाग लें"
        AppLanguage.ASSAMESE -> "অংশগ্ৰহণ কৰক"
        AppLanguage.ODIA -> "ଯୋଗ ଦିଅନ୍ତୁ"
        AppLanguage.MARATHI -> "सहभागी व्हा"
        AppLanguage.GUJARATI -> "જોડાઓ"
        AppLanguage.PUNJABI -> "ਸ਼ਾਮਲ ਹੋਵੋ"
        AppLanguage.TAMIL -> "சேரவும்"
        AppLanguage.TELUGU -> "చేరండి"
        AppLanguage.KANNADA -> "ಭಾಗವಹಿಸಿ"
        AppLanguage.MALAYALAM -> "പങ്കെടുക്കൂ"
        AppLanguage.URDU -> "شامل ہوں"
        AppLanguage.ENGLISH -> "Join Match"
    }

    fun joined(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "যোগদান সম্পন্ন"
        AppLanguage.HINDI -> "शामिल हो चुके"
        AppLanguage.ASSAMESE -> "যোগদান হ'ল"
        AppLanguage.ODIA -> "ଯୋଗଦାନ ସରିଛି"
        AppLanguage.MARATHI -> "सहभागी झाले"
        AppLanguage.GUJARATI -> "જોડાઈ ગયા"
        AppLanguage.PUNJABI -> "ਸ਼ਾਮਲ ਹੋ ਗਏ"
        AppLanguage.TAMIL -> "சேர்ந்துவிட்டீர்கள்"
        AppLanguage.TELUGU -> "చేరారు"
        AppLanguage.KANNADA -> "ಸೇರ್ಪಡೆಗೊಂಡಿದೆ"
        AppLanguage.MALAYALAM -> "ചേർന്നു"
        AppLanguage.URDU -> "شامل ہو گئے"
        AppLanguage.ENGLISH -> "Joined"
    }

    fun matchFull(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "স্লট পূর্ণ"
        AppLanguage.HINDI -> "स्लॉट फुल"
        AppLanguage.ASSAMESE -> "স্লট ভৰিছে"
        AppLanguage.ODIA -> "ସ୍ଲଟ୍ ଭର୍ତ୍ତି"
        AppLanguage.MARATHI -> "जागा भरल्या"
        AppLanguage.GUJARATI -> "સ્લોટ ભરાઈ ગયા"
        AppLanguage.PUNJABI -> "ਸਲਾਟ ਫੁੱਲ"
        AppLanguage.TAMIL -> "இடங்கள் நிரம்பின"
        AppLanguage.TELUGU -> "స్లాట్లు నిండాయి"
        AppLanguage.KANNADA -> "ಸ್ಲಾಟ್‌ಗಳು ಭರ್ತಿಯಾಗಿವೆ"
        AppLanguage.MALAYALAM -> "സ്ലോട്ടുകൾ നിറഞ്ഞു"
        AppLanguage.URDU -> "تمام سیٹیں بھر گئیں"
        AppLanguage.ENGLISH -> "Slots Full"
    }

    fun addMoney(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "টাকা যোগ করুন"
        AppLanguage.HINDI -> "पैसे जोड़ें"
        AppLanguage.ASSAMESE -> "টকা যোগ কৰক"
        AppLanguage.ODIA -> "ଟଙ୍କା ଯୋଡ଼ନ୍ତୁ"
        AppLanguage.MARATHI -> "पैसे जोडा"
        AppLanguage.GUJARATI -> "પૈસા ઉમેરો"
        AppLanguage.PUNJABI -> "ਪੈਸੇ ਜੋੜੋ"
        AppLanguage.TAMIL -> "பணம் சேர்க்க"
        AppLanguage.TELUGU -> "డబ్బు జోడించండి"
        AppLanguage.KANNADA -> "ಹಣ ಸೇರಿಸಿ"
        AppLanguage.MALAYALAM -> "പണം ചേർക്കുക"
        AppLanguage.URDU -> "رقم جمع کریں"
        AppLanguage.ENGLISH -> "Add Money"
    }

    fun withdraw(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "উইথড্র করুন"
        AppLanguage.HINDI -> "निकासी करें"
        AppLanguage.ASSAMESE -> "উইথড্ৰ কৰক"
        AppLanguage.ODIA -> "ଟଙ୍କା ଉଠାନ୍ତୁ"
        AppLanguage.MARATHI -> "पैसे काढा"
        AppLanguage.GUJARATI -> "ઉપાડો"
        AppLanguage.PUNJABI -> "ਕਢਵਾਓ"
        AppLanguage.TAMIL -> "பணம் எடுக்க"
        AppLanguage.TELUGU -> "విత్ డ్రా చేయండి"
        AppLanguage.KANNADA -> "ಹಿಂಪಡೆಯಿರಿ"
        AppLanguage.MALAYALAM -> "പിൻവലിക്കുക"
        AppLanguage.URDU -> "رقم نکالیں"
        AppLanguage.ENGLISH -> "Withdraw"
    }

    fun referEarn(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "রেফার ও আয়"
        AppLanguage.HINDI -> "रेफर करें और कमाएं"
        AppLanguage.ASSAMESE -> "ৰেফাৰ আৰু উপাৰ্জন"
        AppLanguage.ODIA -> "ରେଫର୍ ଏବଂ ରୋଜଗାର"
        AppLanguage.MARATHI -> "रेफर करा आणि कमवा"
        AppLanguage.GUJARATI -> "રેફર કરો અને કમાઓ"
        AppLanguage.PUNJABI -> "ਰੈਫ਼ਰ ਕਰੋ ਅਤੇ ਕਮਾਓ"
        AppLanguage.TAMIL -> "பகிர்ந்து சம்பாதிக்கவும்"
        AppLanguage.TELUGU -> "రిఫర్ చేసి సంపాదించండి"
        AppLanguage.KANNADA -> "ರೆಫರ್ ಮಾಡಿ ಗಳಿಸಿ"
        AppLanguage.MALAYALAM -> "റെഫർ ചെയ്തു നേടൂ"
        AppLanguage.URDU -> "ریفر کریں اور کمائیں"
        AppLanguage.ENGLISH -> "Refer & Earn"
    }

    fun copyRoom(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "রুম আইডি কপি"
        AppLanguage.HINDI -> "रूम आईडी कॉपी करें"
        AppLanguage.ASSAMESE -> "ৰুম আইডি কপি কৰক"
        AppLanguage.ODIA -> "ରୁମ୍ ଆଇଡି କପି କରନ୍ତୁ"
        AppLanguage.MARATHI -> "रूम आयडी कॉपी करा"
        AppLanguage.GUJARATI -> "રૂમ આઈડી કોપી કરો"
        AppLanguage.PUNJABI -> "ਰੂਮ ਆਈਡੀ ਕਾਪੀ ਕਰੋ"
        AppLanguage.TAMIL -> "அறை ஐடியை நகலெடுக்க"
        AppLanguage.TELUGU -> "రూమ్ ఐడీ కాపీ చేయండి"
        AppLanguage.KANNADA -> "ರೂಮ್ ಐಡಿ ನಕಲಿಸಿ"
        AppLanguage.MALAYALAM -> "റൂം ഐഡി കോപ്പി ചെയ്യുക"
        AppLanguage.URDU -> "روم آئی ڈی کاپی کریں"
        AppLanguage.ENGLISH -> "Copy Room ID"
    }

    fun copyPass(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "পাসওয়ার্ড কপি"
        AppLanguage.HINDI -> "पासवर्ड कॉपी करें"
        AppLanguage.ASSAMESE -> "পাছৱৰ্ড কপি কৰক"
        AppLanguage.ODIA -> "ପାସୱାର୍ଡ କପି କରନ୍ତୁ"
        AppLanguage.MARATHI -> "पासवर्ड कॉपी करा"
        AppLanguage.GUJARATI -> "પાસવર્ડ કોપી કરો"
        AppLanguage.PUNJABI -> "ਪਾਸਵਰਡ ਕਾਪੀ ਕਰੋ"
        AppLanguage.TAMIL -> "கடவுச்சொல் நகலெடுக்க"
        AppLanguage.TELUGU -> "పాస్‌వర్డ్ కాపీ చేయండి"
        AppLanguage.KANNADA -> "ಪಾಸ್‌ವರ್ಡ್ ನಕಲಿಸಿ"
        AppLanguage.MALAYALAM -> "പാസ്‌വേഡ് കോപ്പി ചെയ്യുക"
        AppLanguage.URDU -> "پاس ورڈ کاپی کریں"
        AppLanguage.ENGLISH -> "Copy Pass"
    }

    // Wallet
    fun totalBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "সর্বমোট ব্যালেন্স"
        AppLanguage.HINDI -> "कुल बैलेंस"
        AppLanguage.ASSAMESE -> "মুঠ বেলেন্স"
        AppLanguage.ODIA -> "ମୋଟ ବାଲାନ୍ସ"
        AppLanguage.MARATHI -> "एकूण शिल्लक"
        AppLanguage.GUJARATI -> "કુલ બેલેન્સ"
        AppLanguage.PUNJABI -> "ਕੁੱਲ ਬਕਾਇਆ"
        AppLanguage.TAMIL -> "மொத்த இருப்பு"
        AppLanguage.TELUGU -> "మొత్తం బ్యాలెన్స్"
        AppLanguage.KANNADA -> "ಒಟ್ಟು ಬ್ಯಾಲೆನ್ಸ್"
        AppLanguage.MALAYALAM -> "ആകെ ബാലൻസ്"
        AppLanguage.URDU -> "کل بیلنس"
        AppLanguage.ENGLISH -> "Total Balance"
    }

    fun depositBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ডিপোজিট ক্যাশ"
        AppLanguage.HINDI -> "जमा राशि"
        AppLanguage.ASSAMESE -> "জমা ধন"
        AppLanguage.ODIA -> "ଜମା ଟଙ୍କା"
        AppLanguage.MARATHI -> "ठेव रक्कम"
        AppLanguage.GUJARATI -> "થાપણ રકમ"
        AppLanguage.PUNJABI -> "ਜਮ੍ਹਾਂ ਰਕਮ"
        AppLanguage.TAMIL -> "வைப்பு தொகை"
        AppLanguage.TELUGU -> "డిపాజిట్ నగదు"
        AppLanguage.KANNADA -> "ಠೇವಣಿ ನಗದು"
        AppLanguage.MALAYALAM -> "നിക്ഷേപ തുക"
        AppLanguage.URDU -> "جمع شدہ رقم"
        AppLanguage.ENGLISH -> "Deposit Cash"
    }

    fun winningBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "উইনিং ক্যাশ"
        AppLanguage.HINDI -> "जीती हुई राशि"
        AppLanguage.ASSAMESE -> "বিজয়ী ধন"
        AppLanguage.ODIA -> "ଜିତିଥିବା ଟଙ୍କା"
        AppLanguage.MARATHI -> "जिंकलेली रक्कम"
        AppLanguage.GUJARATI -> "જીતેલી રકમ"
        AppLanguage.PUNJABI -> "ਜਿੱਤੀ ਹੋਈ ਰਕਮ"
        AppLanguage.TAMIL -> "வெற்றி தொகை"
        AppLanguage.TELUGU -> "గెలుచుకున్న నగదు"
        AppLanguage.KANNADA -> "ಗೆದ್ದ ಹಣ"
        AppLanguage.MALAYALAM -> "വിജയിച്ച തുക"
        AppLanguage.URDU -> "جیتی ہوئی رقم"
        AppLanguage.ENGLISH -> "Winnings Cash"
    }

    fun bonusBalance(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "বোনাস ক্যাশ"
        AppLanguage.HINDI -> "बोनस राशि"
        AppLanguage.ASSAMESE -> "বোনাস ধন"
        AppLanguage.ODIA -> "ବୋନସ୍ ଟଙ୍କା"
        AppLanguage.MARATHI -> "बोनस रक्कम"
        AppLanguage.GUJARATI -> "બોનસ રકમ"
        AppLanguage.PUNJABI -> "ਬੋਨਸ ਰਕਮ"
        AppLanguage.TAMIL -> "போனஸ் தொகை"
        AppLanguage.TELUGU -> "బోనస్ నగదు"
        AppLanguage.KANNADA -> "ಬೋನಸ್ ಹಣ"
        AppLanguage.MALAYALAM -> "ബോണസ് തുക"
        AppLanguage.URDU -> "بونس رقم"
        AppLanguage.ENGLISH -> "Bonus Cash"
    }

    fun minWithdrawNotice(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "নূন্যতম উইথড্র অ্যামাউন্ট ₹৫০"
        AppLanguage.HINDI -> "न्यूनतम निकासी राशि ₹50 है"
        AppLanguage.ASSAMESE -> "নূন্যতম উইথড্ৰ পৰিমাণ ₹৫০"
        AppLanguage.ODIA -> "ସର୍ବନିମ୍ନ ଉଠାଣ ରାଶି ₹୫୦"
        AppLanguage.MARATHI -> "किमान पैसे काढण्याची रक्कम ₹५०"
        AppLanguage.GUJARATI -> "ન્યૂનતમ ઉપાડ રકમ ₹૫૦ છે"
        AppLanguage.PUNJABI -> "ਘੱਟੋ-ਘੱਟ ਕਢਵਾਉਣ ਦੀ ਰਕਮ ₹50 ਹੈ"
        AppLanguage.TAMIL -> "குறைந்தபட்ச பணம் எடுக்கும் தொகை ₹50"
        AppLanguage.TELUGU -> "కనీస విత్‌డ్రా మొత్తం ₹50"
        AppLanguage.KANNADA -> "ಕನಿಷ್ಠ ಹಿಂಪಡೆಯುವಿಕೆ ಮೊತ್ತ ₹50"
        AppLanguage.MALAYALAM -> "കുറഞ്ഞ പിൻവലിക്കൽ തുക ₹50"
        AppLanguage.URDU -> "کم از کم رقم نکالنے کی حد 50 روپے ہے"
        AppLanguage.ENGLISH -> "Minimum withdrawal amount is ₹50"
    }

    fun transactionHistory(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "লেনদেনের হিস্ট্রি"
        AppLanguage.HINDI -> "लेन-देन का इतिहास"
        AppLanguage.ASSAMESE -> "লেনদেনৰ ইতিহাস"
        AppLanguage.ODIA -> "କାରବାର ଇତିହାସ"
        AppLanguage.MARATHI -> "व्यवहार इतिहास"
        AppLanguage.GUJARATI -> "વ્યવહારોનો ઇતિહાસ"
        AppLanguage.PUNJABI -> "ਲੈਣ-ਦੇਣ ਦਾ ਇਤਿਹਾਸ"
        AppLanguage.TAMIL -> "பரிவர்த்தனை வரலாறு"
        AppLanguage.TELUGU -> "లావాదేవీల చరిత్ర"
        AppLanguage.KANNADA -> "ವಹಿವಾಟು ಇತಿಹಾಸ"
        AppLanguage.MALAYALAM -> "ഇടപാട് ചരിത്രം"
        AppLanguage.URDU -> "لین دین کی تاریخ"
        AppLanguage.ENGLISH -> "Transaction History"
    }

    // Tournament Details
    fun entryFee(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "এন্ট্রি ফি"
        AppLanguage.HINDI -> "प्रवेश शुल्क"
        AppLanguage.ASSAMESE -> "প্ৰৱেশ মাচুল"
        AppLanguage.ODIA -> "ପ୍ରବେଶ ଶୁଳ୍କ"
        AppLanguage.MARATHI -> "प्रवेश शुल्क"
        AppLanguage.GUJARATI -> "એન્ટ્રી ફી"
        AppLanguage.PUNJABI -> "ਦਾਖਲਾ ਫੀਸ"
        AppLanguage.TAMIL -> "நுழைவு கட்டணம்"
        AppLanguage.TELUGU -> "ప్రవేశ రుసుము"
        AppLanguage.KANNADA -> "ಪ್ರವೇಶ ಶುಲ್ಕ"
        AppLanguage.MALAYALAM -> "എൻട്രി ഫീസ്"
        AppLanguage.URDU -> "داخلہ فیس"
        AppLanguage.ENGLISH -> "Entry Fee"
    }

    fun prizePool(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "প্রাইজ পুল"
        AppLanguage.HINDI -> "इनाम राशि"
        AppLanguage.ASSAMESE -> "পুৰস্কাৰ পুল"
        AppLanguage.ODIA -> "ପୁରସ୍କାର ରାଶି"
        AppLanguage.MARATHI -> "बक्षीस रक्कम"
        AppLanguage.GUJARATI -> "ઇનામ પુલ"
        AppLanguage.PUNJABI -> "ਇਨਾਮੀ ਰਕਮ"
        AppLanguage.TAMIL -> "பரிசு தொகை"
        AppLanguage.TELUGU -> "బహుమతి పూల్"
        AppLanguage.KANNADA -> "ಬಹುಮಾನ ಮೊತ್ತ"
        AppLanguage.MALAYALAM -> "സമ്മാന തുക"
        AppLanguage.URDU -> "انعامی رقم"
        AppLanguage.ENGLISH -> "Prize Pool"
    }

    fun slots(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "স্লট"
        AppLanguage.HINDI -> "स्लॉट"
        AppLanguage.ASSAMESE -> "স্লট"
        AppLanguage.ODIA -> "ସ୍ଲଟ୍"
        AppLanguage.MARATHI -> "जागा"
        AppLanguage.GUJARATI -> "સ્લોટ્સ"
        AppLanguage.PUNJABI -> "ਸਲਾਟ"
        AppLanguage.TAMIL -> "இடங்கள்"
        AppLanguage.TELUGU -> "స్లాట్లు"
        AppLanguage.KANNADA -> "ಸ್ಲಾಟ್‌ಗಳು"
        AppLanguage.MALAYALAM -> "സ്ലോട്ടുകൾ"
        AppLanguage.URDU -> "سیٹیں"
        AppLanguage.ENGLISH -> "Slots"
    }

    fun map(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ম্যাপ"
        AppLanguage.HINDI -> "मैप"
        AppLanguage.ASSAMESE -> "মেপ"
        AppLanguage.ODIA -> "ମ୍ୟାପ୍"
        AppLanguage.MARATHI -> "नकाशा"
        AppLanguage.GUJARATI -> "નકશો"
        AppLanguage.PUNJABI -> "ਨਕਸ਼ਾ"
        AppLanguage.TAMIL -> "வரைபடம்"
        AppLanguage.TELUGU -> "మ్యాప్"
        AppLanguage.KANNADA -> "ನಕ್ಷೆ"
        AppLanguage.MALAYALAM -> "ഭൂപടം"
        AppLanguage.URDU -> "نقشہ"
        AppLanguage.ENGLISH -> "Map"
    }

    fun startTime(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "শুরুর সময়"
        AppLanguage.HINDI -> "शुरू होने का समय"
        AppLanguage.ASSAMESE -> "আৰম্ভৰ সময়"
        AppLanguage.ODIA -> "ଆରମ୍ଭ ସମୟ"
        AppLanguage.MARATHI -> "सुरू होण्याची वेळ"
        AppLanguage.GUJARATI -> "શરૂ થવાનો સમય"
        AppLanguage.PUNJABI -> "ਸ਼ੁਰੂ ਹੋਣ ਦਾ ਸਮਾਂ"
        AppLanguage.TAMIL -> "தொடக்க நேரம்"
        AppLanguage.TELUGU -> "ప్రారంభ సమయం"
        AppLanguage.KANNADA -> "ಪ್ರಾರಂಭದ ಸಮಯ"
        AppLanguage.MALAYALAM -> "ആരംഭ സമയം"
        AppLanguage.URDU -> "شروع ہونے کا وقت"
        AppLanguage.ENGLISH -> "Start Time"
    }

    fun rules(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ম্যাচ নিয়মাবলী"
        AppLanguage.HINDI -> "मैच के नियम"
        AppLanguage.ASSAMESE -> "মেচৰ নিয়ম"
        AppLanguage.ODIA -> "ମ୍ୟାଚ୍ ନିୟମାବଳୀ"
        AppLanguage.MARATHI -> "सामन्याचे नियम"
        AppLanguage.GUJARATI -> "મેચના નિયમો"
        AppLanguage.PUNJABI -> "ਮੈਚ ਦੇ ਨਿਯਮ"
        AppLanguage.TAMIL -> "போட்டி விதிகள்"
        AppLanguage.TELUGU -> "మ్యాచ్ నియమాలు"
        AppLanguage.KANNADA -> "ಪಂದ್ಯದ ನಿಯಮಗಳು"
        AppLanguage.MALAYALAM -> "മത്സര നിയമങ്ങൾ"
        AppLanguage.URDU -> "میچ کے قواعد"
        AppLanguage.ENGLISH -> "Match Rules"
    }

    fun prizeBreakdown(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "প্রাইজ বণ্টন তালিকা"
        AppLanguage.HINDI -> "इनाम वितरण"
        AppLanguage.ASSAMESE -> "পুৰস্কাৰ বিতৰণ"
        AppLanguage.ODIA -> "ପୁରସ୍କାର ବଣ୍ଟନ"
        AppLanguage.MARATHI -> "बक्षीस वितरण"
        AppLanguage.GUJARATI -> "ઇનામ વિતરણ"
        AppLanguage.PUNJABI -> "ਇਨਾਮ ਵੰਡ"
        AppLanguage.TAMIL -> "பரிசு பகிர்வு"
        AppLanguage.TELUGU -> "బహుమతుల పంపిణీ"
        AppLanguage.KANNADA -> "ಬಹುಮಾನ ವಿತರಣೆ"
        AppLanguage.MALAYALAM -> "സമ്മാന വിതരണം"
        AppLanguage.URDU -> "انعامات کی تقسیم"
        AppLanguage.ENGLISH -> "Prize Distribution"
    }

    fun roomDetails(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "রুম আইডি ও পাসওয়ার্ড"
        AppLanguage.HINDI -> "रूम आईडी और पासवर्ड"
        AppLanguage.ASSAMESE -> "ৰুম আইডি আৰু পাছৱৰ্ড"
        AppLanguage.ODIA -> "ରୁମ୍ ଆଇଡି ଏବଂ ପାସୱାର୍ଡ"
        AppLanguage.MARATHI -> "रूम आयडी आणि पासवर्ड"
        AppLanguage.GUJARATI -> "રૂમ આઈડી અને પાસવર્ડ"
        AppLanguage.PUNJABI -> "ਰੂਮ ਆਈਡੀ ਅਤੇ ਪਾਸਵਰਡ"
        AppLanguage.TAMIL -> "அறை ஐடி & கடவுச்சொல்"
        AppLanguage.TELUGU -> "రూమ్ ఐడీ & పాస్‌వర్డ్"
        AppLanguage.KANNADA -> "ರೂಮ್ ಐಡಿ ಮತ್ತು ಪಾಸ್‌ವರ್ಡ್"
        AppLanguage.MALAYALAM -> "റൂം ഐഡിയും പാസ്‌വേഡും"
        AppLanguage.URDU -> "روم آئی ڈی اور پاس ورڈ"
        AppLanguage.ENGLISH -> "Room ID & Password"
    }

    fun roomLockedNotice(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "🔒 রুম আইডি ও পাসওয়ার্ড ম্যাচ শুরুর ১৫ মিনিট আগে শুধুমাত্র অনুমোদিত অংশগ্রহণকারীদের জন্য উন্মুক্ত হবে।"
        AppLanguage.HINDI -> "🔒 रूम आईडी और पासवर्ड मैच शुरू होने से 15 मिनट पहले केवल पंजीकृत खिलाड़ियों के लिए खुलेगा।"
        AppLanguage.ASSAMESE -> "🔒 ৰুম আইডি আৰু পাছৱৰ্ড মেচ আৰম্ভৰ ১৫ মিনিট আগতে কেৱল অনুমোদিত খেলুৱৈসকলৰ বাবে উপলব্ধ হ'ব।"
        AppLanguage.ODIA -> "🔒 ରୁମ୍ ଆଇଡି ଏବଂ ପାସୱାର୍ଡ ମ୍ୟାଚ୍ ଆରମ୍ଭର ୧୫ ମିନିଟ୍ ପୂର୍ବରୁ କେବଳ ଯୋଗ ଦେଇଥିବା ଖେଳାଳିଙ୍କ ପାଇଁ ଉନ୍ମୁକ୍ତ ହେବ।"
        AppLanguage.MARATHI -> "🔒 रूम आयडी आणि पासवर्ड सामना सुरू होण्यापूर्वी १५ मिनिटे फक्त सहभागी खेळाडूंसाठी खुला होईल."
        AppLanguage.GUJARATI -> "🔒 રૂમ આઈડી અને પાસવર્ડ મેચ શરૂ થવાના ૧૫ મિનિટ પહેલાં માત્ર જોડાયેલા ખેલાડીઓ માટે જ અનલૉક થશે."
        AppLanguage.PUNJABI -> "🔒 ਰੂਮ ਆਈਡੀ ਅਤੇ ਪਾਸਵਰਡ ਮੈਚ ਸ਼ੁਰੂ ਹੋਣ ਤੋਂ 15 ਮਿੰਟ ਪਹਿਲਾਂ ਸਿਰਫ਼ ਰਜਿਸਟਰਡ ਖਿਡਾਰੀਆਂ ਲਈ ਖੁੱਲ੍ਹੇਗਾ।"
        AppLanguage.TAMIL -> "🔒 அறை ஐடி மற்றும் கடவுச்சொல் போட்டி தொடங்குவதற்கு 15 நிமிடங்களுக்கு முன்பு உறுதிப்படுத்தப்பட்ட வீரர்களுக்கு மட்டுமே திறக்கப்படும்."
        AppLanguage.TELUGU -> "🔒 మ్యాచ్ ప్రారంభానికి 15 నిమిషాల ముందు మాత్రమే నమోదైన ఆటగాళ్లకు రూమ్ ఐడీ మరియు పాస్‌వర్డ్ అన్‌లాక్ అవుతుంది."
        AppLanguage.KANNADA -> "🔒 ಪಂದ್ಯ ಆರಂಭವಾಗುವ 15 ನಿಮಿಷಗಳ ಮೊದಲು ನೋಂದಾಯಿತ ಆಟಗಾರರಿಗೆ ಮಾತ್ರ ರೂಮ್ ಐಡಿ ಮತ್ತು ಪಾಸ್‌ವರ್ಡ್ ಅನ್‌ಲಾಕ್ ಆಗುತ್ತದೆ."
        AppLanguage.MALAYALAM -> "🔒 മത്സരം ആരംഭിക്കുന്നതിന് 15 മിനിറ്റ് മുമ്പ് രജിസ്റ്റർ ചെയ്ത കളിക്കാർക്ക് മാത്രമേ റൂം ഐഡിയും പാസ്‌വേഡും ലഭ്യമാകൂ."
        AppLanguage.URDU -> "🔒 روم آئی ڈی اور پاس ورڈ میچ شروع ہونے سے 15 منٹ پہلے صرف رجسٹرڈ کھلاڑیوں کے لیے دستیاب ہوگا۔"
        AppLanguage.ENGLISH -> "🔒 Room ID & Password will be unlocked 15 minutes before match starts only for joined players."
    }

    fun legalDisclaimer(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "⚠️ অস্বীকৃতি: ব্যাটেল এরিনা একটি স্কিল-ভিত্তিক এস্পোর্টস প্ল্যাটফর্ম। এটি গ্যারেনা বা ফ্রি ফায়ারের সাথে অফিসিয়ালি অনুমোদিত বা সম্পর্কিত নয়। সমস্ত ট্রেডমার্ক তাদের নিজ নিজ মালিকদের।"
        AppLanguage.HINDI -> "⚠️ अस्वीकरण: बैटल एरीना एक कौशल-आधारित ईस्पोर्ट्स प्लेटफॉर्म है। यह गरेना या फ्री फायर से आधिकारिक रूप से संबद्ध नहीं है। सभी ट्रेडमार्क उनके संबंधित स्वामियों के हैं।"
        AppLanguage.ASSAMESE -> "⚠️ অস্বীকাৰ: বেটেল এৰিনা এটা দক্ষতা-ভিত্তিক ই-স্প’ৰ্টছ প্লেটফৰ্ম। ই গেৰেনা বা ফ্ৰী ফায়াৰৰ সৈতে অফিচিয়েলভাৱে জড়িত নহয়।"
        AppLanguage.ODIA -> "⚠️ ଦାବି ତ୍ୟାଗ: ବ୍ୟାଟେଲ୍ ଏରିନା ଏକ ଦକ୍ଷତା-ଆଧାରିତ ଇ-ସ୍ପୋର୍ଟସ୍ ପ୍ଲାଟଫର୍ମ। ଏହା ଗାରେନା କିମ୍ବା ଫ୍ରି ଫାୟାର ସହିତ ଜଡିତ ନୁହେଁ।"
        AppLanguage.MARATHI -> "⚠️ अस्वीकरण: बॅटल एरिना हे कौशल्य-आधारित ई-स्पोर्ट्स प्लॅटफॉर्म आहे. हे गरेना किंवा फ्री फायरशी अधिकृतपणे संलग्न नाही."
        AppLanguage.GUJARATI -> "⚠️ અસ્વીકરણ: બેટલ એરેના એક કૌશલ્ય-આધારિત ઇ-સ્પોર્ટ્સ પ્લેટફોર્મ છે. તે ગરેના અથવા ફ્રી ફાયર સાથે સત્તાવાર રીતે જોડાયેલ નથી."
        AppLanguage.PUNJABI -> "⚠️ ਬੇਦਾਅਵਾ: ਬੈਟਲ ਅਰੀਨਾ ਇੱਕ ਹੁਨਰ-ਅਧਾਰਤ ਈ-ਸਪੋਰਟਸ ਪਲੇਟਫਾਰਮ ਹੈ। ਇਹ ਗਰੇਨਾ ਜਾਂ ਫ੍ਰੀ ਫਾਇਰ ਨਾਲ ਅਧਿਕਾਰਤ ਤੌਰ 'ਤੇ ਸੰਬੰਧਿਤ ਨਹੀਂ ਹੈ।"
        AppLanguage.TAMIL -> "⚠️ மறுப்பு: பேட்டில் அரீனா ஒரு திறன் சார்ந்த ஈ-ஸ்போர்ட்ஸ் தளம். இது கரீனா அல்லது ஃப்ரீ ஃபயருடன் அதிகாரப்பூர்வமாக இணைக்கப்படவில்லை."
        AppLanguage.TELUGU -> "⚠️ నిరాకరణ: బాటిల్ ఎరీనా నైపుణ్యం ఆధారిత ఇ-స్పోర్ట్స్ ప్లాట్‌ఫామ్. ఇది గారెనా లేదా ఫ్రీ ఫైర్‌తో అధికారికంగా అనుబంధించబడలేదు."
        AppLanguage.KANNADA -> "⚠️ ಹಕ್ಕು ನಿರಾಕರಣೆ: ಬ್ಯಾಟಲ್ ಅರೀನಾ ಕೌಶಲ್ಯ ಆಧಾರಿತ ಇ-ಸ್ಪೋರ್ಟ್ಸ್ ಪ್ಲಾಟ್‌ಫಾರ್ಮ್ ಆಗಿದೆ. ಇದು ಗರೆನಾ ಅಥವಾ ಫ್ರೀ ಫೈರ್‌ನೊಂದಿಗೆ ಅಧಿಕೃತವಾಗಿ ಸಂಯೋಜಿತವಾಗಿಲ್ಲ."
        AppLanguage.MALAYALAM -> "⚠️ നിരാകരണം: ബാറ്റിൽ അരീന ഒരു നൈപുണ്യ അധിഷ്ഠിത ഇ-സ്‌പോർട്‌സ് പ്ലാറ്റ്‌ഫോമാണ്. ഇത് ഗരേന അല്ലെങ്കിൽ ഫ്രീ ഫയറുമായി ഔദ്യോഗികമായി ബന്ധപ്പെട്ടിട്ടില്ല."
        AppLanguage.URDU -> "⚠️ دستبرداری: بیٹل ایرینا مہارت پر مبنی ای اسپورٹس پلیٹ فارم ہے۔ یہ باضابطہ طور پر گیرینا یا فری فائر سے منسلک نہیں ہے۔"
        AppLanguage.ENGLISH -> "⚠️ Disclaimer: Battle Arena is an independent skill-based esports community platform. Not affiliated with or endorsed by Garena or Free Fire."
    }

    // Language Selection Modal
    fun selectLanguageTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ভাষা নির্বাচন করুন"
        AppLanguage.HINDI -> "अपनी भाषा चुनें"
        AppLanguage.ASSAMESE -> "ভাষা নিৰ্বাচন কৰক"
        AppLanguage.ODIA -> "ଭାଷା ଚୟନ କରନ୍ତୁ"
        AppLanguage.MARATHI -> "भाषा निवडा"
        AppLanguage.GUJARATI -> "ભાષા પસંદ કરો"
        AppLanguage.PUNJABI -> "ਆਪਣੀ ਭਾਸ਼ਾ ਚੁਣੋ"
        AppLanguage.TAMIL -> "மொழியைத் தேர்ந்தெடுக்கவும்"
        AppLanguage.TELUGU -> "భాషను ఎంచుకోండి"
        AppLanguage.KANNADA -> "ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ"
        AppLanguage.MALAYALAM -> "ഭാഷ തിരഞ്ഞെടുക്കുക"
        AppLanguage.URDU -> "اپنی زبان منتخب کریں"
        AppLanguage.ENGLISH -> "Select Language"
    }

    fun languageSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "১৩টি ভারতীয় ভাষার যেকোনো একটি নির্বাচন করুন"
        AppLanguage.HINDI -> "13 भारतीय भाषाओं में से कोई भी चुनें"
        AppLanguage.ASSAMESE -> "১৩টা ভাৰতীয় ভাষাৰ যিকোনো এটা বাছক"
        AppLanguage.ODIA -> "୧୩ଟି ଭାରତୀୟ ଭାଷା ମଧ୍ୟରୁ ବାଛନ୍ତୁ"
        AppLanguage.MARATHI -> "१३ भारतीय भाषांपैकी एक निवडा"
        AppLanguage.GUJARATI -> "૧૩ ભારતીય ભાષાઓમાંથી પસંદ કરો"
        AppLanguage.PUNJABI -> "13 ਭਾਰਤੀ ਭਾਸ਼ਾਵਾਂ ਵਿੱਚੋਂ ਚੁਣੋ"
        AppLanguage.TAMIL -> "13 இந்திய மொழிகளில் ஒன்றைத் தேர்ந்தெடுக்கவும்"
        AppLanguage.TELUGU -> "13 భారతీయ భాషలలో ఒకదాన్ని ఎంచుకోండి"
        AppLanguage.KANNADA -> "13 ಭಾರತೀಯ ಭಾಷೆಗಳಲ್ಲಿ ಒಂದನ್ನು ಆಯ್ಕೆಮಾಡಿ"
        AppLanguage.MALAYALAM -> "13 ഇന്ത്യൻ ഭാഷകളിൽ ഒന്ന് തിരഞ്ഞെടുക്കുക"
        AppLanguage.URDU -> "13 ہندوستانی زبانوں میں سے ایک منتخب کریں"
        AppLanguage.ENGLISH -> "Choose from 13 Indian languages"
    }

    // Customer Support & Telegram
    fun customerSupport(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "গ্রাহক সহায়তা"
        AppLanguage.HINDI -> "ग्राहक सहायता"
        AppLanguage.ASSAMESE -> "গ্ৰাহক সেৱা"
        AppLanguage.ODIA -> "ଗ୍ରାହକ ସେବା"
        AppLanguage.MARATHI -> "ग्राहक सहाय्यता"
        AppLanguage.GUJARATI -> "ગ્રાહક સહાય"
        AppLanguage.PUNJABI -> "ਗਾਹਕ ਸਹਾਇਤਾ"
        AppLanguage.TAMIL -> "வாடிக்கையாளர் ஆதரவு"
        AppLanguage.TELUGU -> "కస్టమర్ సపోర్ట్"
        AppLanguage.KANNADA -> "ಗ್ರಾಹಕ ಬೆಂಬಲ"
        AppLanguage.MALAYALAM -> "ഉപഭോക്തൃ പിന്തുണ"
        AppLanguage.URDU -> "کسٹمر سپورٹ"
        AppLanguage.ENGLISH -> "Customer Support"
    }

    fun customerSupportDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ব্যাটেল এরিনা সাপোর্ট গ্রুপ (টেলিগ্রাম)"
        AppLanguage.HINDI -> "बैटल एरीना सपोर्ट ग्रुप (टेलीग्राम)"
        AppLanguage.ASSAMESE -> "বেটেল এৰিনা চাপোৰ্ট গ্ৰুপ (টেলিগ্ৰাম)"
        AppLanguage.ODIA -> "ବ୍ୟାଟେଲ୍ ଏରିନା ସପୋର୍ଟ ଗ୍ରୁପ୍ (ଟେଲିଗ୍ରାମ୍)"
        AppLanguage.MARATHI -> "बॅटल एरिना सपोर्ट ग्रुप (टेलिग्राम)"
        AppLanguage.GUJARATI -> "બેટલ એરેના સપોર્ટ ગ્રૂપ (ટેલિગ્રામ)"
        AppLanguage.PUNJABI -> "ਬੈਟਲ ਅਰੀਨਾ ਸਹਾਇਤਾ ਗਰੁੱਪ (ਟੈਲੀਗ੍ਰਾਮ)"
        AppLanguage.TAMIL -> "பேட்டில் அரீனா உதவி குழு (டெலிகிராம்)"
        AppLanguage.TELUGU -> "బాటిల్ ఎరీనా సపోర్ట్ గ్రూప్ (టెలిగ్రామ్)"
        AppLanguage.KANNADA -> "ಬ್ಯಾಟಲ್ ಅರೀನಾ ಬೆಂಬಲ ಗುಂಪು (ಟೆಲಿಗ್ರಾಮ್)"
        AppLanguage.MALAYALAM -> "ബാറ്റിൽ അരീന സപ്പോർട്ട് ഗ്രൂപ്പ് (ടെലിഗ്രാം)"
        AppLanguage.URDU -> "بیٹل ایرینا سپورٹ گروپ (ٹیلیگرام)"
        AppLanguage.ENGLISH -> "Battle Arena Support Group (Telegram)"
    }

    fun officialTelegram(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "অফিসিয়াল টেলিগ্রাম চ্যানেল"
        AppLanguage.HINDI -> "ऑफिशियल टेलीग्राम चैनल"
        AppLanguage.ASSAMESE -> "অফিচিয়েল টেলিগ্ৰাম চেনেল"
        AppLanguage.ODIA -> "ଅଫିସିଆଲ୍ ଟେଲିଗ୍ରାମ୍ ଚ୍ୟାନେଲ୍"
        AppLanguage.MARATHI -> "अधिकृत टेलिग्राम चॅनेल"
        AppLanguage.GUJARATI -> "ઓફિશિયલ ટેલિગ્રામ ચેનલ"
        AppLanguage.PUNJABI -> "ਸਰਕਾਰੀ ਟੈਲੀਗ੍ਰਾਮ ਚੈਨਲ"
        AppLanguage.TAMIL -> "அதிகாரப்பூர்வ டெலிகிராம் சேனல்"
        AppLanguage.TELUGU -> "అధికారిక టెలిగ్రామ్ ఛానల్"
        AppLanguage.KANNADA -> "ಅಧಿಕೃತ ಟೆಲಿಗ್ರಾಮ್ ಚಾನಲ್"
        AppLanguage.MALAYALAM -> "ഔദ്യോഗിക ടെലിഗ്രാം ചാനൽ"
        AppLanguage.URDU -> "آفیشل ٹیلیگرام چینل"
        AppLanguage.ENGLISH -> "Official Telegram Channel"
    }

    fun officialTelegramDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "@BattleArenaOfficial1 • নোটিশ ও রুম আইডি"
        AppLanguage.HINDI -> "@BattleArenaOfficial1 • मैच नोटिस और रूम आईडी"
        AppLanguage.ASSAMESE -> "@BattleArenaOfficial1 • জাননী আৰু ৰুম আইডি"
        AppLanguage.ODIA -> "@BattleArenaOfficial1 • ସୂଚନା ଏବଂ ରୁମ୍ ଆଇଡି"
        AppLanguage.MARATHI -> "@BattleArenaOfficial1 • सूचना आणि रूम आयडी"
        AppLanguage.GUJARATI -> "@BattleArenaOfficial1 • નોટિસ અને રૂમ આઈડી"
        AppLanguage.PUNJABI -> "@BattleArenaOfficial1 • ਨੋਟਿਸ ਅਤੇ ਰੂਮ ਆਈਡੀ"
        AppLanguage.TAMIL -> "@BattleArenaOfficial1 • அறிவிப்புகள் & அறை ஐடி"
        AppLanguage.TELUGU -> "@BattleArenaOfficial1 • నోటీసులు & రూమ్ ఐడీ"
        AppLanguage.KANNADA -> "@BattleArenaOfficial1 • ಸೂಚನೆಗಳು ಮತ್ತು ರೂಮ್ ಐಡಿ"
        AppLanguage.MALAYALAM -> "@BattleArenaOfficial1 • അറിയിപ്പുകളും റൂം ഐഡിയും"
        AppLanguage.URDU -> "@BattleArenaOfficial1 • نوٹس اور روم آئی ڈی"
        AppLanguage.ENGLISH -> "@BattleArenaOfficial1 • Match Notices & Updates"
    }

    fun helpAndSupport(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "হেল্প ও কাস্টমার সাপোর্ট"
        AppLanguage.HINDI -> "हेल्प और कस्टमर सपोर्ट"
        AppLanguage.ASSAMESE -> "সহায় আৰু গ্ৰাহক সেৱা"
        AppLanguage.ODIA -> "ସହାୟତା ଏବଂ ଗ୍ରାହକ ସେବା"
        AppLanguage.MARATHI -> "मदत आणि ग्राहक सहाय्यता"
        AppLanguage.GUJARATI -> "મદદ અને ગ્રાહક સહાય"
        AppLanguage.PUNJABI -> "ਮਦਦ ਅਤੇ ਗਾਹਕ ਸਹਾਇਤਾ"
        AppLanguage.TAMIL -> "உதவி & வாடிக்கையாளர் ஆதரவு"
        AppLanguage.TELUGU -> "సహాయం & కస్టమర్ సపోర్ట్"
        AppLanguage.KANNADA -> "ಸಹಾಯ ಮತ್ತು ಗ್ರಾಹಕ ಬೆಂಬಲ"
        AppLanguage.MALAYALAM -> "സഹായവും ഉപഭോക്തൃ പിന്തുണയും"
        AppLanguage.URDU -> "مدد اور کسٹمر سپورٹ"
        AppLanguage.ENGLISH -> "Help & Customer Support"
    }

    fun pushNotificationsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "পুশ নোটিফিকেশন (FCM)"
        AppLanguage.HINDI -> "पुश नोटिफिकेशन (FCM)"
        AppLanguage.ASSAMESE -> "পুশ জাননী (FCM)"
        AppLanguage.ODIA -> "ପୁଶ୍ ବିଜ୍ଞପ୍ତି (FCM)"
        AppLanguage.MARATHI -> "पुश सूचना (FCM)"
        AppLanguage.GUJARATI -> "પુશ સૂચનાઓ (FCM)"
        AppLanguage.PUNJABI -> "ਪੁਸ਼ ਸੂਚਨਾਵਾਂ (FCM)"
        AppLanguage.TAMIL -> "புஷ் அறிவிப்புகள் (FCM)"
        AppLanguage.TELUGU -> "పుష్ నోటిఫికేషన్‌లు (FCM)"
        AppLanguage.KANNADA -> "ಪುಶ್ ಅಧಿಸೂಚನೆಗಳು (FCM)"
        AppLanguage.MALAYALAM -> "പുഷ് അറിയിപ്പുകൾ (FCM)"
        AppLanguage.URDU -> "پش اطلاعات (FCM)"
        AppLanguage.ENGLISH -> "Push Notifications (FCM)"
    }

    fun tournamentReminders(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "টুর্নামেন্ট রেজিস্ট্রেশন অনুস্মারক"
        AppLanguage.HINDI -> "टूर्नामेंट पंजीकरण रिमाइंडर"
        AppLanguage.ASSAMESE -> "টুৰ্ণামেণ্ট পঞ্জীয়ন সোঁৱৰণী"
        AppLanguage.ODIA -> "ଟୁର୍ଣ୍ଣାମେଣ୍ଟ ପଞ୍ଜୀକରଣ ସ୍ମାରକ"
        AppLanguage.MARATHI -> "स्पर्धा नोंदणी स्मरणपत्र"
        AppLanguage.GUJARATI -> "ટૂર્નામેન્ટ નોંધણી રીમાઇન્ડર"
        AppLanguage.PUNJABI -> "ਟੂਰਨਾਮੈਂਟ ਰਜਿਸਟ੍ਰੇਸ਼ਨ ਰੀਮਾਈਂਡਰ"
        AppLanguage.TAMIL -> "போட்டி பதிவு நினைவூட்டல்"
        AppLanguage.TELUGU -> "టోర్నమెంట్ రిజిస్ట్రేషన్ రిమైండర్"
        AppLanguage.KANNADA -> "ಪಂದ್ಯಾವಳಿ ನೋಂದಣಿ ಜ್ಞಾಪನೆ"
        AppLanguage.MALAYALAM -> "ടൂർണമെന്റ് രജിസ്ട്രേഷൻ ഓർമ്മപ്പെടുത്തൽ"
        AppLanguage.URDU -> "ٹورنامنٹ رجسٹریشن یاد دہانی"
        AppLanguage.ENGLISH -> "Tournament Registration Reminders"
    }

    fun matchStartsAlert(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "ম্যাচ শুরুর সময় ও রুম আইডি"
        AppLanguage.HINDI -> "मैच शुरू होने का समय और रूम आईडी"
        AppLanguage.ASSAMESE -> "মেচ আৰম্ভৰ সময় আৰু ৰুম আইডি"
        AppLanguage.ODIA -> "ମ୍ୟାଚ୍ ଆରମ୍ଭ ସମୟ ଏବଂ ରୁମ୍ ଆଇଡି"
        AppLanguage.MARATHI -> "सामना सुरू होण्याची वेळ आणि रूम आयडी"
        AppLanguage.GUJARATI -> "મેચ શરૂ થવાનો સમય અને રૂમ આઈડી"
        AppLanguage.PUNJABI -> "ਮੈਚ ਸ਼ੁਰੂ ਹੋਣ ਦਾ ਸਮਾਂ ਅਤੇ ਰੂਮ ਆਈਡੀ"
        AppLanguage.TAMIL -> "போட்டி தொடக்க நேரம் & அறை ஐடி"
        AppLanguage.TELUGU -> "మ్యాచ్ ప్రారంభ సమయం & రూమ్ ఐడీ"
        AppLanguage.KANNADA -> "ಪಂದ್ಯ ಆರಂಭವಾಗುವ ಸಮಯ ಮತ್ತು ರೂಮ್ ಐಡಿ"
        AppLanguage.MALAYALAM -> "മത്സര ആരംഭ സമയവും റൂം ഐഡിയും"
        AppLanguage.URDU -> "میچ شروع ہونے کا وقت اور روم آئی ڈی"
        AppLanguage.ENGLISH -> "Match Start Times & Room Access"
    }

    fun prizeDistributionAlerts(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "পুরস্কার বিতরণ ও জয়ের অ্যালার্ট"
        AppLanguage.HINDI -> "इनाम वितरण और जीत के अलर्ट"
        AppLanguage.ASSAMESE -> "পুৰস্কাৰ বিতৰণ আৰু জয়ৰ সতৰ্কবাণী"
        AppLanguage.ODIA -> "ପୁରସ୍କାର ବଣ୍ଟନ ଏବଂ ବିଜୟ ବିଜ୍ଞପ୍ତି"
        AppLanguage.MARATHI -> "बक्षीस वितरण आणि विजय सूचना"
        AppLanguage.GUJARATI -> "ઇનામ વિતરણ અને જીતની સૂચનાઓ"
        AppLanguage.PUNJABI -> "ਇਨਾਮ ਵੰਡ ਅਤੇ ਜਿੱਤ ਅਲਰਟ"
        AppLanguage.TAMIL -> "பரிசு வழங்கல் & வெற்றி அறிவிப்புகள்"
        AppLanguage.TELUGU -> "బహుమతుల పంపిణీ & విజయాల హెచ్చరికలు"
        AppLanguage.KANNADA -> "ಬಹುಮಾನ ವಿತರಣೆ ಮತ್ತು ವಿಜೇತ ಎಚ್ಚರಿಕೆಗಳು"
        AppLanguage.MALAYALAM -> "സമ്മാന വിതരണവും വിജയ അലേർട്ടുകളും"
        AppLanguage.URDU -> "انعامات کی تقسیم اور جیت کے الرٹس"
        AppLanguage.ENGLISH -> "Prize Distribution & Winnings Alerts"
    }

    // Admin Panel Translations
    fun adminControlCenter(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "মালিক ও অ্যাডমিন কন্ট্রোল"
        AppLanguage.HINDI -> "सुपर एडमिन कंट्रोल सेंटर"
        AppLanguage.ASSAMESE -> "ছুপাৰ এডমিন কন্ট্ৰোল চেণ্টাৰ"
        AppLanguage.ODIA -> "ସୁପର ଅଡମିନ୍ କଣ୍ଟ୍ରୋଲ୍ ସେଣ୍ଟର"
        AppLanguage.MARATHI -> "सुपर ॲडमिन नियंत्रण केंद्र"
        AppLanguage.GUJARATI -> "સુપર એડમિન કંટ્રોલ સેન્ટર"
        AppLanguage.PUNJABI -> "ਸੁਪਰ ਐਡਮਿਨ ਕੰਟਰੋਲ ਸੈਂਟਰ"
        AppLanguage.TAMIL -> "சூப்பர் நிர்வாகி கட்டுப்பாட்டு மையம்"
        AppLanguage.TELUGU -> "సూపర్ అడ్మిన్ కంట్రోల్ సెంటర్"
        AppLanguage.KANNADA -> "ಸೂಪರ್ ಅಡ್ಮಿನ್ ನಿಯಂತ್ರಣ ಕೇಂದ್ರ"
        AppLanguage.MALAYALAM -> "സൂപ്പർ അഡ്മിൻ കൺട്രോൾ സെന്റർ"
        AppLanguage.URDU -> "سپر ایڈمن کنٹرول سینٹر"
        AppLanguage.ENGLISH -> "Super Admin Control Center"
    }

    fun createMatch(lang: AppLanguage) = when (lang) {
        AppLanguage.BENGALI -> "নতুন ম্যাচ"
        AppLanguage.HINDI -> "नया मैच बनाएं"
        AppLanguage.ASSAMESE -> "নতুন মেচ সৃষ্টি কৰক"
        AppLanguage.ODIA -> "ନୂତନ ମ୍ୟାଚ୍ ସୃଷ୍ଟି କରନ୍ତୁ"
        AppLanguage.MARATHI -> "नवीन सामना तयार करा"
        AppLanguage.GUJARATI -> "નવી મેચ બનાવો"
        AppLanguage.PUNJABI -> "ਨਵਾਂ ਮੈਚ ਬਣਾਓ"
        AppLanguage.TAMIL -> "புதிய போட்டி உருவாக்க"
        AppLanguage.TELUGU -> "కొత్త మ్యాచ్ సృష్టించండి"
        AppLanguage.KANNADA -> "ಹೊಸ ಪಂದ್ಯ ರಚಿಸಿ"
        AppLanguage.MALAYALAM -> "പുതിയ മത്സരം സൃഷ്ടിക്കുക"
        AppLanguage.URDU -> "نیا میچ بنائیں"
        AppLanguage.ENGLISH -> "Create Match"
    }

    fun gameModeTitle(mode: GameMode, lang: AppLanguage): String = when (mode) {
        GameMode.LONE_WOLF -> when (lang) {
            AppLanguage.BENGALI -> "লোন উলফ (১v১)"
            AppLanguage.HINDI -> "लोन वुल्फ (1v1)"
            AppLanguage.ASSAMESE -> "লোন উলফ (১v১)"
            AppLanguage.ODIA -> "ଲୋନ୍ ୱୁଲ୍ଫ (୧v୧)"
            AppLanguage.MARATHI -> "लोन वुल्फ (१v१)"
            AppLanguage.GUJARATI -> "લોન વુલ્ફ (૧v૧)"
            AppLanguage.PUNJABI -> "ਲੋਨ ਵੁਲਫ (1v1)"
            AppLanguage.TAMIL -> "லோன் உல்ஃப் (1v1)"
            AppLanguage.TELUGU -> "లోన్ వోల్ఫ్ (1v1)"
            AppLanguage.KANNADA -> "ಲೋನ್ ವುಲ್ಫ್ (1v1)"
            AppLanguage.MALAYALAM -> "ലോൺ വുൾഫ് (1v1)"
            AppLanguage.URDU -> "لون وولف (1v1)"
            AppLanguage.ENGLISH -> "Lone Wolf (1v1)"
        }
        GameMode.CLASH_SQUAD -> when (lang) {
            AppLanguage.BENGALI -> "ক্ল্যাশ স্কোয়াড (৪v৪)"
            AppLanguage.HINDI -> "क्लैश स्क्वाड (4v4)"
            AppLanguage.ASSAMESE -> "ক্লেশ স্কোৱাড (৪v৪)"
            AppLanguage.ODIA -> "କ୍ଲାସ୍ ସ୍କ୍ୱାଡ୍ (୪v୪)"
            AppLanguage.MARATHI -> "क्लॅश स्क्वॉड (४v४)"
            AppLanguage.GUJARATI -> "ક્લેશ સ્ક્વોડ (૪v૪)"
            AppLanguage.PUNJABI -> "ਕਲੈਸ਼ ਸਕੁਐਡ (4v4)"
            AppLanguage.TAMIL -> "கிளாஷ் ஸ்குவாட் (4v4)"
            AppLanguage.TELUGU -> "క్లాష్ స్క్వాడ్ (4v4)"
            AppLanguage.KANNADA -> "ಕ್ಲಾಶ್ ಸ್ಕ್ವಾಡ್ (4v4)"
            AppLanguage.MALAYALAM -> "ക്ലാഷ് സ്ക്വാഡ് (4v4)"
            AppLanguage.URDU -> "کلیش اسکواڈ (4v4)"
            AppLanguage.ENGLISH -> "Clash Squad (4v4)"
        }
        GameMode.BATTLE_ROYALE -> when (lang) {
            AppLanguage.BENGALI -> "ব্যাটেল রয়্যাল (৪০ প্লেয়ার)"
            AppLanguage.HINDI -> "बैटल रॉयल (40 खिलाड़ी)"
            AppLanguage.ASSAMESE -> "বেটেল ৰয়েল (৪০ খেলুৱৈ)"
            AppLanguage.ODIA -> "ବ୍ୟାଟେଲ୍ ରୟାଲ୍ (୪୦ ଖେଳାଳି)"
            AppLanguage.MARATHI -> "बॅटल रॉयल (४० खेळाडू)"
            AppLanguage.GUJARATI -> "બેટલ રોયલ (૪૦ ખેલાડીઓ)"
            AppLanguage.PUNJABI -> "ਬੈਟਲ ਰੋਇਲ (40 ਖਿਡਾਰੀ)"
            AppLanguage.TAMIL -> "பேட்டில் ராயல் (40 வீரர்கள்)"
            AppLanguage.TELUGU -> "బాటిల్ రాయల్ (40 ఆటగాళ్ళు)"
            AppLanguage.KANNADA -> "ಬ್ಯಾಟಲ್ ರಾಯಲ್ (40 ಆಟಗಾರರು)"
            AppLanguage.MALAYALAM -> "ബാറ്റിൽ റോയൽ (40 കളിക്കാർ)"
            AppLanguage.URDU -> "بیٹل رائل (40 کھلاڑی)"
            AppLanguage.ENGLISH -> "Battle Royale (40 Players)"
        }
    }

    // Dynamic Title Formatter
    fun formatMatchTitle(id: String, mode: GameMode, lang: AppLanguage, titleEn: String, titleBn: String): String {
        return when (lang) {
            AppLanguage.BENGALI -> titleBn
            AppLanguage.ENGLISH -> titleEn
            else -> {
                val modeLabel = gameModeTitle(mode, lang)
                "$modeLabel #$id"
            }
        }
    }
}
