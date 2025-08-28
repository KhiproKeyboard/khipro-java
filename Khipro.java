import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Khipro
{
    
    // --------------------------
    // Mapping groups (exactly as provided)
    // --------------------------
    
    private static final Map<String, String> SHOR = new ConcurrentHashMap<>();
    private static final Map<String, String> BYANJON = new ConcurrentHashMap<>();
    private static final Map<String, String> JUKTOBORNO = new ConcurrentHashMap<>();
    private static final Map<String, String> REPH = new ConcurrentHashMap<>();
    private static final Map<String, String> PHOLA = new ConcurrentHashMap<>();
    private static final Map<String, String> KAR = new ConcurrentHashMap<>();
    private static final Map<String, String> ONGKO = new ConcurrentHashMap<>();
    private static final Map<String, String> DIACRITIC = new ConcurrentHashMap<>();
    private static final Map<String, String> BIRAM = new ConcurrentHashMap<>();
    private static final Map<String, String> PRITHAYOK = new ConcurrentHashMap<>();
    private static final Map<String, String> AE = new ConcurrentHashMap<>();
    
    // Group maps collection
    private static final Map<String, Map<String, String>> GROUP_MAPS = new ConcurrentHashMap<>();
    
    // State machine configuration
    private static final String INIT = new String("init");
    private static final String SHOR_STATE = new String("shor-state");
    private static final String REPH_STATE = new String("reph-state");
    private static final String BYANJON_STATE = new String("byanjon-state");
    
    // Group order per state
    private static final Map<String, List<String>> STATE_GROUP_ORDER = new ConcurrentHashMap<>();
    
    // Precompute max key length per group for greedy matching
    private static final Map<String, Integer> MAXLEN_PER_GROUP = new ConcurrentHashMap<>();
    
    // যখন JVM এ ক্লাসটি লোড হবে, এই স্কোপে থাকা সবকিছু অটো ইনিশিয়ালাইজ হয়ে যাবে।
    static
    {
        //  SHOR ম্যাপ
        SHOR.put("o", "অ");
        SHOR.put("oo", "ঽ");
        SHOR.put("fuf", "‌ু");
        SHOR.put("fuuf", "‌ূ");
        SHOR.put("fqf", "‌ৃ");
        SHOR.put("fa", "া");
        SHOR.put("a", "আ");
        SHOR.put("fi", "ি");
        SHOR.put("i", "ই");
        SHOR.put("fii", "ী");
        SHOR.put("ii", "ঈ");
        SHOR.put("fu", "ু");
        SHOR.put("u", "উ");
        SHOR.put("fuu", "ূ");
        SHOR.put("uu", "ঊ");
        SHOR.put("fq", "ৃ");
        SHOR.put("q", "ঋ");
        SHOR.put("fe", "ে");
        SHOR.put("e", "এ");
        SHOR.put("foi", "ৈ");
        SHOR.put("oi", "ঐ");
        SHOR.put("fw", "ো");
        SHOR.put("w", "ও");
        SHOR.put("fou", "ৌ");
        SHOR.put("ou", "ঔ");
        SHOR.put("fae", "্যা");
        SHOR.put("ae", "অ্যা");
        SHOR.put("wa", "ওয়া");
        SHOR.put("fwa", "োয়া");
        SHOR.put("wae", "ওয়্যা");
        SHOR.put("we", "ওয়ে");
        SHOR.put("fwe", "োয়ে");
        SHOR.put("ngo", "ঙ");
        SHOR.put("nga", "ঙা");
        SHOR.put("ngi", "ঙি");
        SHOR.put("ngii", "ঙী");
        SHOR.put("ngu", "ঙু");
        SHOR.put("nguff", "ঙ");
        SHOR.put("nguu", "ঙূ");
        SHOR.put("nguuff", "ঙ");
        SHOR.put("ngq", "ঙৃ");
        SHOR.put("nge", "ঙে");
        SHOR.put("ngoi", "ঙৈ");
        SHOR.put("ngw", "ঙো");
        SHOR.put("ngou", "ঙৌ");
        SHOR.put("ngae", "ঙ্যা");
        
        //  BYANJON ম্যাপ
        BYANJON.put("k", "ক");
        BYANJON.put("kh", "খ");
        BYANJON.put("g", "গ");
        BYANJON.put("gh", "ঘ");
        BYANJON.put("c", "চ");
        BYANJON.put("ch", "ছ");
        BYANJON.put("j", "জ");
        BYANJON.put("jh", "ঝ");
        BYANJON.put("nff", "ঞ");
        BYANJON.put("tf", "ট");
        BYANJON.put("tff", "ঠ");
        BYANJON.put("tfh", "ঠ");
        BYANJON.put("df", "ড");
        BYANJON.put("dff", "ঢ");
        BYANJON.put("dfh", "ঢ");
        BYANJON.put("nf", "ণ");
        BYANJON.put("t", "ত");
        BYANJON.put("th", "থ");
        BYANJON.put("d", "দ");
        BYANJON.put("dh", "ধ");
        BYANJON.put("n", "ন");
        BYANJON.put("p", "প");
        BYANJON.put("ph", "ফ");
        BYANJON.put("b", "ব");
        BYANJON.put("v", "ভ");
        BYANJON.put("m", "ম");
        BYANJON.put("z", "য");
        BYANJON.put("l", "ল");
        BYANJON.put("sh", "শ");
        BYANJON.put("sf", "ষ");
        BYANJON.put("s", "স");
        BYANJON.put("h", "হ");
        BYANJON.put("y", "য়");
        BYANJON.put("rf", "ড়");
        BYANJON.put("rff", "ঢ়");
        BYANJON.put(",,", "়");
        
        // যুক্তবর্নের ম্যাপ
        JUKTOBORNO.put("rz", "র‍্য");
        JUKTOBORNO.put("kk", "ক্ক");
        JUKTOBORNO.put("ktf", "ক্ট");
        JUKTOBORNO.put("ktfr", "ক্ট্র");
        JUKTOBORNO.put("kt", "ক্ত");
        JUKTOBORNO.put("ktr", "ক্ত্র");
        JUKTOBORNO.put("kb", "ক্ব");
        JUKTOBORNO.put("km", "ক্ম");
        JUKTOBORNO.put("kz", "ক্য");
        JUKTOBORNO.put("kr", "ক্র");
        JUKTOBORNO.put("kl", "ক্ল");
        JUKTOBORNO.put("kf", "ক্ষ");
        JUKTOBORNO.put("ksf", "ক্ষ");
        JUKTOBORNO.put("kkh", "ক্ষ");
        JUKTOBORNO.put("kfnf", "ক্ষ্ণ");
        JUKTOBORNO.put("kfn", "ক্ষ্ণ");
        JUKTOBORNO.put("ksfnf", "ক্ষ্ণ");
        JUKTOBORNO.put("ksfn", "ক্ষ্ণ");
        JUKTOBORNO.put("kkhn", "ক্ষ্ণ");
        JUKTOBORNO.put("kkhnf", "ক্ষ্ণ");
        JUKTOBORNO.put("kfb", "ক্ষ্ব");
        JUKTOBORNO.put("ksfb", "ক্ষ্ব");
        JUKTOBORNO.put("kkhb", "ক্ষ্ব");
        JUKTOBORNO.put("kfm", "ক্ষ্ম");
        JUKTOBORNO.put("kkhm", "ক্ষ্ম");
        JUKTOBORNO.put("ksfm", "ক্ষ্ম");
        JUKTOBORNO.put("kfz", "ক্ষ্য");
        JUKTOBORNO.put("ksfz", "ক্ষ্য");
        JUKTOBORNO.put("kkhz", "ক্ষ্য");
        JUKTOBORNO.put("ks", "ক্স");
        JUKTOBORNO.put("khz", "খ্য");
        JUKTOBORNO.put("khr", "খ্র");
        JUKTOBORNO.put("ggg", "গ্গ");
        JUKTOBORNO.put("gnf", "গ্‌ণ");
        JUKTOBORNO.put("gdh", "গ্ধ");
        JUKTOBORNO.put("gdhz", "গ্ধ্য");
        JUKTOBORNO.put("gdhr", "গ্ধ্র");
        JUKTOBORNO.put("gn", "গ্ন");
        JUKTOBORNO.put("gnz", "গ্ন্য");
        JUKTOBORNO.put("gb", "গ্ব");
        JUKTOBORNO.put("gm", "গ্ম");
        JUKTOBORNO.put("gz", "গ্য");
        JUKTOBORNO.put("gr", "গ্র");
        JUKTOBORNO.put("grz", "গ্র্য");
        JUKTOBORNO.put("gl", "গ্ল");
        JUKTOBORNO.put("ghn", "ঘ্ন");
        JUKTOBORNO.put("ghr", "ঘ্র");
        JUKTOBORNO.put("ngk", "ঙ্ক");
        JUKTOBORNO.put("ngkt", "ঙ্‌ক্ত");
        JUKTOBORNO.put("ngkz", "ঙ্ক্য");
        JUKTOBORNO.put("ngkr", "ঙ্ক্র");
        JUKTOBORNO.put("ngkkh", "ঙ্ক্ষ");
        JUKTOBORNO.put("ngksf", "ঙ্ক্ষ");
        JUKTOBORNO.put("ngkh", "ঙ্খ");
        JUKTOBORNO.put("ngg", "ঙ্গ");
        JUKTOBORNO.put("nggz", "ঙ্গ্য");
        JUKTOBORNO.put("nggh", "ঙ্ঘ");
        JUKTOBORNO.put("ngghz", "ঙ্ঘ্য");
        JUKTOBORNO.put("ngghr", "ঙ্ঘ্র");
        JUKTOBORNO.put("ngm", "ঙ্ম");
        JUKTOBORNO.put("cc", "চ্চ");
        JUKTOBORNO.put("cch", "চ্ছ");
        JUKTOBORNO.put("cchb", "চ্ছ্ব");
        JUKTOBORNO.put("cchr", "চ্ছ্র");
        JUKTOBORNO.put("cnff", "চ্ঞ");
        JUKTOBORNO.put("cb", "চ্ব");
        JUKTOBORNO.put("cz", "চ্য");
        JUKTOBORNO.put("jj", "জ্জ");
        JUKTOBORNO.put("jjb", "জ্জ্ব");
        JUKTOBORNO.put("jjh", "জ্ঝ");
        JUKTOBORNO.put("jnff", "জ্ঞ");
        JUKTOBORNO.put("gg", "জ্ঞ");
        JUKTOBORNO.put("jb", "জ্ব");
        JUKTOBORNO.put("jz", "জ্য");
        JUKTOBORNO.put("jr", "জ্র");
        JUKTOBORNO.put("nc", "ঞ্চ");
        JUKTOBORNO.put("nffc", "ঞ্চ");
        JUKTOBORNO.put("nj", "ঞ্জ");
        JUKTOBORNO.put("nffj", "ঞ্জ");
        JUKTOBORNO.put("njh", "ঞ্ঝ");
        JUKTOBORNO.put("nffjh", "ঞ্ঝ");
        JUKTOBORNO.put("nch", "ঞ্ছ");
        JUKTOBORNO.put("nffch", "ঞ্ছ");
        JUKTOBORNO.put("ttf", "ট্ট");
        JUKTOBORNO.put("tftf", "ট্ট");
        JUKTOBORNO.put("tfb", "ট্ব");
        JUKTOBORNO.put("tfm", "ট্ম");
        JUKTOBORNO.put("tfz", "ট্য");
        JUKTOBORNO.put("tfr", "ট্র");
        JUKTOBORNO.put("ddf", "ড্ড");
        JUKTOBORNO.put("dfdf", "ড্ড");
        JUKTOBORNO.put("dfb", "ড্ব");
        JUKTOBORNO.put("dfz", "ড্য");
        JUKTOBORNO.put("dfr", "ড্র");
        JUKTOBORNO.put("rfg", "ড়্‌গ");
        JUKTOBORNO.put("dffz", "ঢ্য");
        JUKTOBORNO.put("dfhz", "ঢ্য");
        JUKTOBORNO.put("dffr", "ঢ্র");
        JUKTOBORNO.put("dfhr", "ঢ্র");
        JUKTOBORNO.put("nftf", "ণ্ট");
        JUKTOBORNO.put("nftff", "ণ্ঠ");
        JUKTOBORNO.put("nftfh", "ণ্ঠ");
        JUKTOBORNO.put("nftffz", "ণ্ঠ্য");
        JUKTOBORNO.put("nftfhz", "ণ্ঠ্য");
        JUKTOBORNO.put("nfdf", "ণ্ড");
        JUKTOBORNO.put("nfdfz", "ণ্ড্য");
        JUKTOBORNO.put("nfdfr", "ণ্ড্র");
        JUKTOBORNO.put("nfdff", "ণ্ঢ");
        JUKTOBORNO.put("nfdfh", "ণ্ঢ");
        JUKTOBORNO.put("nfnf", "ণ্ণ");
        JUKTOBORNO.put("nfn", "ণ্ণ");
        JUKTOBORNO.put("nfb", "ণ্ব");
        JUKTOBORNO.put("nfm", "ণ্ম");
        JUKTOBORNO.put("nfz", "ণ্য");
        JUKTOBORNO.put("tt", "ত্ত");
        JUKTOBORNO.put("ttb", "ত্ত্ব");
        JUKTOBORNO.put("ttz", "ত্ত্য");
        JUKTOBORNO.put("tth", "ত্থ");
        JUKTOBORNO.put("tn", "ত্ন");
        JUKTOBORNO.put("tb", "ত্ব");
        JUKTOBORNO.put("tm", "ত্ম");
        JUKTOBORNO.put("tmz", "ত্ম্য");
        JUKTOBORNO.put("tz", "ত্য");
        JUKTOBORNO.put("tr", "ত্র");
        JUKTOBORNO.put("trz", "ত্র্য");
        JUKTOBORNO.put("thb", "থ্ব");
        JUKTOBORNO.put("thz", "থ্য");
        JUKTOBORNO.put("thr", "থ্র");
        JUKTOBORNO.put("dg", "দ্‌গ");
        JUKTOBORNO.put("dgh", "দ্‌ঘ");
        JUKTOBORNO.put("dd", "দ্দ");
        JUKTOBORNO.put("ddb", "দ্দ্ব");
        JUKTOBORNO.put("ddh", "দ্ধ");
        JUKTOBORNO.put("db", "দ্ব");
        JUKTOBORNO.put("dv", "দ্ভ");
        JUKTOBORNO.put("dvr", "দ্ভ্র");
        JUKTOBORNO.put("dm", "দ্ম");
        JUKTOBORNO.put("dz", "দ্য");
        JUKTOBORNO.put("dr", "দ্র");
        JUKTOBORNO.put("drz", "দ্র্য");
        JUKTOBORNO.put("dhn", "ধ্ন");
        JUKTOBORNO.put("dhb", "ধ্ব");
        JUKTOBORNO.put("dhm", "ধ্ম");
        JUKTOBORNO.put("dhz", "ধ্য");
        JUKTOBORNO.put("dhr", "ধ্র");
        JUKTOBORNO.put("ntf", "ন্ট");
        JUKTOBORNO.put("ntfr", "ন্ট্র");
        JUKTOBORNO.put("ntff", "ন্ঠ");
        JUKTOBORNO.put("ntfh", "ন্ঠ");
        JUKTOBORNO.put("ndf", "ন্ড");
        JUKTOBORNO.put("ndfr", "ন্ড্র");
        JUKTOBORNO.put("nt", "ন্ত");
        JUKTOBORNO.put("ntb", "ন্ত্ব");
        JUKTOBORNO.put("ntr", "ন্ত্র");
        JUKTOBORNO.put("ntrz", "ন্ত্র্য");
        JUKTOBORNO.put("nth", "ন্থ");
        JUKTOBORNO.put("nthr", "ন্থ্র");
        JUKTOBORNO.put("nd", "ন্দ");
        JUKTOBORNO.put("ndb", "ন্দ্ব");
        JUKTOBORNO.put("ndz", "ন্দ্য");
        JUKTOBORNO.put("ndr", "ন্দ্র");
        JUKTOBORNO.put("ndh", "ন্ধ");
        JUKTOBORNO.put("ndhz", "ন্ধ্য");
        JUKTOBORNO.put("ndhr", "ন্ধ্র");
        JUKTOBORNO.put("nn", "ন্ন");
        JUKTOBORNO.put("nb", "ন্ব");
        JUKTOBORNO.put("nm", "ন্ম");
        JUKTOBORNO.put("nz", "ন্য");
        JUKTOBORNO.put("ns", "ন্স");
        JUKTOBORNO.put("ptf", "প্ট");
        JUKTOBORNO.put("pt", "প্ত");
        JUKTOBORNO.put("pn", "প্ন");
        JUKTOBORNO.put("pp", "প্প");
        JUKTOBORNO.put("pz", "প্য");
        JUKTOBORNO.put("pr", "প্র");
        JUKTOBORNO.put("pl", "প্ল");
        JUKTOBORNO.put("ps", "প্স");
        JUKTOBORNO.put("phr", "ফ্র");
        JUKTOBORNO.put("phl", "ফ্ল");
        JUKTOBORNO.put("bj", "ব্জ");
        JUKTOBORNO.put("bd", "ব্দ");
        JUKTOBORNO.put("bdh", "ব্ধ");
        JUKTOBORNO.put("bb", "ব্ব");
        JUKTOBORNO.put("bz", "ব্য");
        JUKTOBORNO.put("br", "ব্র");
        JUKTOBORNO.put("bl", "ব্ল");
        JUKTOBORNO.put("vb", "ভ্ব");
        JUKTOBORNO.put("vz", "ভ্য");
        JUKTOBORNO.put("vr", "ভ্র");
        JUKTOBORNO.put("vl", "ভ্ল");
        JUKTOBORNO.put("mn", "ম্ন");
        JUKTOBORNO.put("mp", "ম্প");
        JUKTOBORNO.put("mpr", "ম্প্র");
        JUKTOBORNO.put("mph", "ম্ফ");
        JUKTOBORNO.put("mb", "ম্ব");
        JUKTOBORNO.put("mbr", "ম্ব্র");
        JUKTOBORNO.put("mv", "ম্ভ");
        JUKTOBORNO.put("mvr", "ম্ভ্র");
        JUKTOBORNO.put("mm", "ম্ম");
        JUKTOBORNO.put("mz", "ম্য");
        JUKTOBORNO.put("mr", "ম্র");
        JUKTOBORNO.put("ml", "ম্ল");
        JUKTOBORNO.put("zz", "য্য");
        JUKTOBORNO.put("lk", "ল্ক");
        JUKTOBORNO.put("lkz", "ল্ক্য");
        JUKTOBORNO.put("lg", "ল্গ");
        JUKTOBORNO.put("ltf", "ল্ট");
        JUKTOBORNO.put("ldf", "ল্ড");
        JUKTOBORNO.put("lp", "ল্প");
        JUKTOBORNO.put("lph", "ল্ফ");
        JUKTOBORNO.put("lb", "ল্ব");
        JUKTOBORNO.put("lv", "ল্‌ভ");
        JUKTOBORNO.put("lm", "ল্ম");
        JUKTOBORNO.put("lz", "ল্য");
        JUKTOBORNO.put("ll", "ল্ল");
        JUKTOBORNO.put("shc", "শ্চ");
        JUKTOBORNO.put("shch", "শ্ছ");
        JUKTOBORNO.put("shn", "শ্ন");
        JUKTOBORNO.put("shb", "শ্ব");
        JUKTOBORNO.put("shm", "শ্ম");
        JUKTOBORNO.put("shz", "শ্য");
        JUKTOBORNO.put("shr", "শ্র");
        JUKTOBORNO.put("shl", "শ্ল");
        JUKTOBORNO.put("sfk", "ষ্ক");
        JUKTOBORNO.put("sfkr", "ষ্ক্র");
        JUKTOBORNO.put("sftf", "ষ্ট");
        JUKTOBORNO.put("sftfz", "ষ্ট্য");
        JUKTOBORNO.put("sftfr", "ষ্ট্র");
        JUKTOBORNO.put("sftff", "ষ্ঠ");
        JUKTOBORNO.put("sftfh", "ষ্ঠ");
        JUKTOBORNO.put("sftffz", "ষ্ঠ্য");
        JUKTOBORNO.put("sftfhz", "ষ্ঠ্য");
        JUKTOBORNO.put("sfnf", "ষ্ণ");
        JUKTOBORNO.put("sfn", "ষ্ণ");
        JUKTOBORNO.put("sfp", "ষ্প");
        JUKTOBORNO.put("sfpr", "ষ্প্র");
        JUKTOBORNO.put("sfph", "ষ্ফ");
        JUKTOBORNO.put("sfb", "ষ্ব");
        JUKTOBORNO.put("sfm", "ষ্ম");
        JUKTOBORNO.put("sfz", "ষ্য");
        JUKTOBORNO.put("sk", "স্ক");
        JUKTOBORNO.put("skr", "স্ক্র");
        JUKTOBORNO.put("skh", "স্খ");
        JUKTOBORNO.put("stf", "স্ট");
        JUKTOBORNO.put("stfr", "স্ট্র");
        JUKTOBORNO.put("st", "স্ত");
        JUKTOBORNO.put("stb", "স্ত্ব");
        JUKTOBORNO.put("stz", "স্ত্য");
        JUKTOBORNO.put("str", "স্ত্র");
        JUKTOBORNO.put("sth", "স্থ");
        JUKTOBORNO.put("sthz", "স্থ্য");
        JUKTOBORNO.put("sn", "স্ন");
        JUKTOBORNO.put("sp", "স্প");
        JUKTOBORNO.put("spr", "স্প্র");
        JUKTOBORNO.put("spl", "স্প্ল");
        JUKTOBORNO.put("sph", "স্ফ");
        JUKTOBORNO.put("sb", "স্ব");
        JUKTOBORNO.put("sm", "স্ম");
        JUKTOBORNO.put("sz", "স্য");
        JUKTOBORNO.put("sr", "স্র");
        JUKTOBORNO.put("sl", "স্ল");
        JUKTOBORNO.put("hn", "হ্ন");
        JUKTOBORNO.put("hnf", "হ্ণ");
        JUKTOBORNO.put("hb", "হ্ব");
        JUKTOBORNO.put("hm", "হ্ম");
        JUKTOBORNO.put("hz", "হ্য");
        JUKTOBORNO.put("hr", "হ্র");
        JUKTOBORNO.put("hl", "হ্ল");

        // অসম্ভব যুক্তবর্নের ম্যাপ।
        JUKTOBORNO.put("ksh", "কশ");
        JUKTOBORNO.put("nsh", "নশ");
        JUKTOBORNO.put("psh", "পশ");
        JUKTOBORNO.put("ld", "লদ");
        JUKTOBORNO.put("gd", "গদ");
        JUKTOBORNO.put("ngkk", "ঙ্কক");
        JUKTOBORNO.put("ngks", "ঙ্কস");
        JUKTOBORNO.put("cn", "চন");
        JUKTOBORNO.put("cnf", "চণ");
        JUKTOBORNO.put("jn", "জন");
        JUKTOBORNO.put("jnf", "জণ");
        JUKTOBORNO.put("tft", "টত");
        JUKTOBORNO.put("dfd", "ডদ");
        JUKTOBORNO.put("nft", "ণত");
        JUKTOBORNO.put("nfd", "ণদ");
        JUKTOBORNO.put("lt", "লত");
        JUKTOBORNO.put("sft", "ষত");
        JUKTOBORNO.put("nfth", "ণথ");
        JUKTOBORNO.put("nfdh", "ণধ");
        JUKTOBORNO.put("sfth", "ষথ");
        JUKTOBORNO.put("ktff", "কঠ");
        JUKTOBORNO.put("ktfh", "কঠ");
        JUKTOBORNO.put("ptff", "পঠ");
        JUKTOBORNO.put("ptfh", "পঠ");
        JUKTOBORNO.put("ltff", "লঠ");
        JUKTOBORNO.put("ltfh", "লঠ");
        JUKTOBORNO.put("stff", "সঠ");
        JUKTOBORNO.put("stfh", "সঠ");
        JUKTOBORNO.put("dfdff", "ডঢ");
        JUKTOBORNO.put("dfdfh", "ডঢ");
        JUKTOBORNO.put("ndff", "নঢ");
        JUKTOBORNO.put("ndfh", "নঢ");
        JUKTOBORNO.put("ktfrf", "ক্টড়");
        JUKTOBORNO.put("ktfrff", "ক্টঢ়");
        JUKTOBORNO.put("kth", "কথ");
        JUKTOBORNO.put("ktrf", "ক্তড়");
        JUKTOBORNO.put("ktrff", "ক্তঢ়");
        JUKTOBORNO.put("krf", "কড়");
        JUKTOBORNO.put("krff", "কঢ়");
        JUKTOBORNO.put("khrf", "খড়");
        JUKTOBORNO.put("khrff", "খঢ়");
        JUKTOBORNO.put("gggh", "জ্ঞঘ");
        JUKTOBORNO.put("gdff", "গঢ");
        JUKTOBORNO.put("gdfh", "গঢ");
        JUKTOBORNO.put("gdhrf", "গ্ধড়");
        JUKTOBORNO.put("gdhrff", "গ্ধঢ়");
        JUKTOBORNO.put("grf", "গড়");
        JUKTOBORNO.put("grff", "গঢ়");
        JUKTOBORNO.put("ghrf", "ঘড়");
        JUKTOBORNO.put("ghrff", "ঘঢ়");
        JUKTOBORNO.put("ngkth", "ঙ্কথ");
        JUKTOBORNO.put("ngkrf", "ঙ্কড়");
        JUKTOBORNO.put("ngkrff", "ঙ্কঢ়");
        JUKTOBORNO.put("ngghrf", "ঙ্ঘড়");
        JUKTOBORNO.put("ngghrff", "ঙ্ঘঢ়");
        JUKTOBORNO.put("cchrf", "চ্ছড়");
        JUKTOBORNO.put("cchrff", "চ্ছঢ়");
        JUKTOBORNO.put("tfrf", "টড়");
        JUKTOBORNO.put("tfrff", "টঢ়");
        JUKTOBORNO.put("dfrf", "ডড়");
        JUKTOBORNO.put("dfrff", "ডঢ়");
        JUKTOBORNO.put("rfgh", "ড়্‌গ");
        JUKTOBORNO.put("dffrf", "ঢড়");
        JUKTOBORNO.put("dfhrf", "ঢড়");
        JUKTOBORNO.put("dffrff", "ঢঢ়");
        JUKTOBORNO.put("dfhrff", "ঢঢ়");
        JUKTOBORNO.put("nfdfrf", "ণ্ডড়");
        JUKTOBORNO.put("nfdfrff", "ণ্ডঢ়");
        JUKTOBORNO.put("trf", "তড়");
        JUKTOBORNO.put("trff", "তঢ়");
        JUKTOBORNO.put("thrf", "থড়");
        JUKTOBORNO.put("thrff", "থঢ়");
        JUKTOBORNO.put("dvrf", "দ্ভড়");
        JUKTOBORNO.put("dvrff", "দ্ভঢ়");
        JUKTOBORNO.put("drf", "দড়");
        JUKTOBORNO.put("drff", "দঢ়");
        JUKTOBORNO.put("dhrf", "ধড়");
        JUKTOBORNO.put("dhrff", "ধঢ়");
        JUKTOBORNO.put("ntfrf", "ন্টড়");
        JUKTOBORNO.put("ntfrff", "ন্টঢ়");
        JUKTOBORNO.put("ndfrf", "ন্ডড়");
        JUKTOBORNO.put("ndfrff", "ন্ডঢ়");
        JUKTOBORNO.put("ntrf", "ন্তড়");
        JUKTOBORNO.put("ntrff", "ন্তঢ়");
        JUKTOBORNO.put("nthrf", "ন্থড়");
        JUKTOBORNO.put("nthrff", "ন্থঢ়");
        JUKTOBORNO.put("ndrf", "ন্দড়");
        JUKTOBORNO.put("ndrff", "ন্দঢ়");
        JUKTOBORNO.put("ndhrf", "ন্ধড়");
        JUKTOBORNO.put("ndhrff", "ন্ধঢ়");
        JUKTOBORNO.put("pth", "পথ");
        JUKTOBORNO.put("pph", "পফ");
        JUKTOBORNO.put("prf", "পড়");
        JUKTOBORNO.put("prff", "পঢ়");
        JUKTOBORNO.put("phrf", "ফড়");
        JUKTOBORNO.put("phrff", "ফঢ়");
        JUKTOBORNO.put("bjh", "বঝ");
        JUKTOBORNO.put("brf", "বড়");
        JUKTOBORNO.put("brff", "বঢ়");
        JUKTOBORNO.put("vrf", "ভড়");
        JUKTOBORNO.put("vrff", "ভঢ়");
        JUKTOBORNO.put("mprf", "ম্পড়");
        JUKTOBORNO.put("mprff", "ম্পঢ়");
        JUKTOBORNO.put("mbrf", "ম্বড়");
        JUKTOBORNO.put("mbrff", "ম্বঢ়");
        JUKTOBORNO.put("mvrf", "ম্ভড়");
        JUKTOBORNO.put("mvrff", "ম্ভঢ়");
        JUKTOBORNO.put("mrf", "মড়");
        JUKTOBORNO.put("mrff", "মঢ়");
        JUKTOBORNO.put("lkh", "লখ");
        JUKTOBORNO.put("lgh", "লঘ");
        JUKTOBORNO.put("shrf", "শড়");
        JUKTOBORNO.put("shrff", "শঢ়");
        JUKTOBORNO.put("sfkh", "ষখ");
        JUKTOBORNO.put("sfkrf", "ষ্কড়");
        JUKTOBORNO.put("sfkrff", "ষ্কঢ়");
        JUKTOBORNO.put("sftfrf", "ষ্টড়");
        JUKTOBORNO.put("sftfrff", "ষ্টঢ়");
        JUKTOBORNO.put("sfprf", "ষ্পড়");
        JUKTOBORNO.put("sfprff", "ষ্প");
        JUKTOBORNO.put("sfprff", "ষ্পঢ়");
        JUKTOBORNO.put("skrf", "স্কড়");
        JUKTOBORNO.put("skrff", "স্কঢ়");
        JUKTOBORNO.put("stfrf", "স্টড়");
        JUKTOBORNO.put("stfrff", "স্টঢ়");
        JUKTOBORNO.put("strf", "স্তড়");
        JUKTOBORNO.put("strff", "স্তঢ়");
        JUKTOBORNO.put("sprf", "স্পড়");
        JUKTOBORNO.put("sprff", "স্পঢ়");
        JUKTOBORNO.put("srf", "সড়");
        JUKTOBORNO.put("srff", "সঢ়");
        JUKTOBORNO.put("hrf", "হড়");
        JUKTOBORNO.put("hrff", "হঢ়");
        JUKTOBORNO.put("ldh", "লধ");
        JUKTOBORNO.put("ngksh", "ঙ্কশ");
        JUKTOBORNO.put("tfth", "টথ");
        JUKTOBORNO.put("dfdh", "ডধ");
        JUKTOBORNO.put("lth", "লথ");
        
        //  REPH ম্যাপ
        REPH.put("rr", "র্");
        REPH.put("r", "র");
        
        //  PHOLA ম্যাপ
        PHOLA.put("r", "র");
        PHOLA.put("z", "য");
        
        //  KAR ম্যাপ
        KAR.put("o", "");
        KAR.put("of", "অ");
        KAR.put("a", "া");
        KAR.put("af", "আ");
        KAR.put("i", "ি");
        KAR.put("if", "ই");
        KAR.put("ii", "ী");
        KAR.put("iif", "ঈ");
        KAR.put("u", "ু");
        KAR.put("uf", "উ");
        KAR.put("uu", "ূ");
        KAR.put("uuf", "ঊ");
        KAR.put("q", "ৃ");
        KAR.put("qf", "ঋ");
        KAR.put("e", "ে");
        KAR.put("ef", "এ");
        KAR.put("oi", "ৈ");
        KAR.put("oif", "ই");
        KAR.put("w", "ো");
        KAR.put("wf", "ও");
        KAR.put("ou", "ৌ");
        KAR.put("ouf", "উ");
        KAR.put("ae", "্যা");
        KAR.put("aef", "অ্যা");
        KAR.put("uff", "‌ু");
        KAR.put("uuff", "‌ূ");
        KAR.put("qff", "‌ৃ");
        KAR.put("we", "োয়ে");
        KAR.put("wef", "ওয়ে");
        KAR.put("waf", "ওয়া");
        KAR.put("wa", "োয়া");
        KAR.put("wae", "ওয়্যা");
        
        //  ONGKO ম্যাপ
        ONGKO.put(".1", ".১");
        ONGKO.put(".2", ".২");
        ONGKO.put(".3", ".৩");
        ONGKO.put(".4", ".৪");
        ONGKO.put(".5", ".৫");
        ONGKO.put(".6", ".৬");
        ONGKO.put(".7", ".৭");
        ONGKO.put(".8", ".৮");
        ONGKO.put(".9", ".৯");
        ONGKO.put(".0", ".০");
        ONGKO.put("1", "১");
        ONGKO.put("2", "২");
        ONGKO.put("3", "৩");
        ONGKO.put("4", "৪");
        ONGKO.put("5", "৫");
        ONGKO.put("6", "৬");
        ONGKO.put("7", "৭");
        ONGKO.put("8", "৮");
        ONGKO.put("9", "৯");
        ONGKO.put("0", "০");
        
        //  DIACRITIC ম্যাপ
        DIACRITIC.put("qq", "্");
        DIACRITIC.put("xx", "্‌");
        DIACRITIC.put("t/", "ৎ");
        DIACRITIC.put("x", "ঃ");
        DIACRITIC.put("ng", "ং");
        DIACRITIC.put("ngf", "ং");
        DIACRITIC.put("/", "ঁ");
        DIACRITIC.put("//", "/");
        DIACRITIC.put("`", "‌");
        DIACRITIC.put("``", "‍");
        
        //  BIRAM ম্যাপ
        BIRAM.put(".", "।");
        BIRAM.put("...", "...");
        BIRAM.put("..", ".");
        BIRAM.put("$", "৳");
        BIRAM.put("$f", "₹");
        BIRAM.put(",,,", ",,");
        BIRAM.put(".f", "॥");
        BIRAM.put(".ff", "৺");
        BIRAM.put("+f", "×");
        BIRAM.put("-f", "÷");
        
        //  PRITHAYOK ম্যাপ
        PRITHAYOK.put(";", "");
        PRITHAYOK.put(";;", ";");
        
        //  AE ম্যাপ
        AE.put("ae", "‍্যা");
        
        // Initialize GROUP_MAPS
        GROUP_MAPS.put("shor", SHOR);
        GROUP_MAPS.put("byanjon", BYANJON);
        GROUP_MAPS.put("juktoborno", JUKTOBORNO);
        GROUP_MAPS.put("reph", REPH);
        GROUP_MAPS.put("phola", PHOLA);
        GROUP_MAPS.put("kar", KAR);
        GROUP_MAPS.put("ongko", ONGKO);
        GROUP_MAPS.put("diacritic", DIACRITIC);
        GROUP_MAPS.put("biram", BIRAM);
        GROUP_MAPS.put("prithayok", PRITHAYOK);
        GROUP_MAPS.put("ae", AE);
        
        // Initialize STATE_GROUP_ORDER
        STATE_GROUP_ORDER.put(INIT, Arrays.asList("diacritic", "shor", "prithayok", "ongko", "biram", "reph", "juktoborno", "byanjon"));
        STATE_GROUP_ORDER.put(SHOR_STATE, Arrays.asList("diacritic", "shor", "biram", "prithayok", "ongko", "reph", "juktoborno", "byanjon"));
        STATE_GROUP_ORDER.put(REPH_STATE, Arrays.asList("prithayok", "ae", "juktoborno", "byanjon", "kar"));
        STATE_GROUP_ORDER.put(BYANJON_STATE, Arrays.asList("diacritic", "prithayok", "ongko", "biram", "kar", "juktoborno", "phola", "byanjon"));
        
        // Initialize MAXLEN_PER_GROUP
        for (Map.Entry<String, Map<String, String>> entry : GROUP_MAPS.entrySet())
        {
            String group = entry.getKey();
            Map<String, String> map = entry.getValue();
            int maxLen = map.keySet().stream()
                    .mapToInt(String::length)
                    .max()
                    .orElse(0);
            MAXLEN_PER_GROUP.put(group, maxLen);
        }
    }
    
    private static class MatchResult
    {
        final String group;
        final String key;
        final String value;
        
        MatchResult(String group, String key, String value)
        {
            this.group = group;
            this.key = key;
            this.value = value;
        }
    }
    
    private static MatchResult findLongest(String state, String text, int i)
    {
        List<String> allowed = STATE_GROUP_ORDER.get(state);
        if (allowed == null)
        {
            return new MatchResult("", "", "");
        }
        
        // Determine the max lookahead we need
        int maxlen = 00;
        for (String g : allowed)
        {
            maxlen = Math.max(maxlen, MAXLEN_PER_GROUP.getOrDefault(g, 0));
        }
        
        int end = Math.min(text.length(), i + maxlen);
        
        // Try lengths from longest to shortest to implement greedy matching
        for (int L = end - i; L > 0; L--)
        {
            String chunk = text.substring(i, i + L);
            
            // Check groups by priority
            for (String g : allowed)
            {
                Map<String, String> m = GROUP_MAPS.get(g);
                if (m != null && m.containsKey(chunk))
                {
                    return new MatchResult(g, chunk, m.get(chunk));
                }
            }
        }
        
        return new MatchResult("", "", "");
    }
    
    private static String applyTransition(String state, String group)
     {
        if (INIT.equals(state))
        {
            if ("diacritic".equals(group))
            {
                return SHOR_STATE;
            }
            if ("shor".equals(group))
            {
                return SHOR_STATE;
            }
            if ("prithayok".equals(group))
            {
                return INIT;
            }
            if ("ongko".equals(group) || "biram".equals(group))
            {
                return INIT;
            }
            if ("reph".equals(group))
            {
                return REPH_STATE;
            }
            if ("juktoborno".equals(group) || "byanjon".equals(group))
            {
                return BYANJON_STATE;
            }
            return state;
        }
        
        if (SHOR_STATE.equals(state))
        {
            if ("diacritic".equals(group) || "shor".equals(group))
            {
                return SHOR_STATE;
            }
            if ("biram".equals(group) || "prithayok".equals(group) || "ongko".equals(group))
            {
                return INIT;
            }
            if ("reph".equals(group))
            {
                return REPH_STATE;
            }
            if ("juktoborno".equals(group) || "byanjon".equals(group))
            {
                return BYANJON_STATE;
            }
            return state;
        }
        
        if (REPH_STATE.equals(state))
        {
            if ("prithayok".equals(group))
            {
                return INIT;
            }
            if ("ae".equals(group))
            {
                return SHOR_STATE;
            }
            if ("juktoborno".equals(group) || "byanjon".equals(group))
            {
                return BYANJON_STATE;
            }
            if ("kar".equals(group))
            {
                return SHOR_STATE;
            }
            return state;
        }
        
        if (BYANJON_STATE.equals(state))
        {
            if ("diacritic".equals(group) || "kar".equals(group))
            {
                return SHOR_STATE;
            }
            if ("prithayok".equals(group) || "ongko".equals(group) || "biram".equals(group))
            {
                return INIT;
            }
            // juktoborno, phola, byanjon keep BYANJON_STATE
            return BYANJON_STATE;
        }
        
        return state;
    }
    
    public static String convert(String text)
    {
        int i = 0;
        int n = text.length();
        String state = INIT;
        StringBuffer out = new StringBuffer();
        
        while (i < n)
        {
            MatchResult result = findLongest(state, text, i);
            if (result.group.isEmpty())
            {
                // No mapping: pass through this char and reset to INIT
                out.append(text.charAt(i));
                i += 1;
                state = INIT;
                continue;
            }
            
            // Special handling: PHOLA in BYANJON_STATE inserts virama before mapped char
            if (BYANJON_STATE.equals(state) && "phola".equals(result.group))
            {
                out.append("্");
                out.append(result.value);
            }
            else
            {
                out.append(result.value);
            }
            
            i += result.key.length();
            state = applyTransition(state, result.group);
        }
        
        return out.toString();
    }
    
    public static List<String> typeStream(String text)
    {
        List<String> results = new Vector<>();
        for (int k = 1; k <= text.length(); k++)
        {
            results.add(convert(text.substring(0, k)));
        }
        return results;
    }
    


    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🔡 Khipro Typing Preview (Press Enter to quit)\n");
        
        while (true)
        {
            System.out.print("Type in Banglish: ");
            String userInput = scanner.nextLine().trim();
            if (userInput.isEmpty())
            {
                break;
            }
            
            System.out.println("\nLive Typing:");
            List<String> steps = typeStream(userInput);
            for (int step = 0; step < steps.size(); step++)
            {
                String inputSubstring = new String(userInput.substring(0, step + 1));
                String output = new String(steps.get(step));
                System.out.println("'" + inputSubstring + "' → " + output);
            }
            System.out.println("-".repeat(40));
        }
        
        scanner.close();
    }
}