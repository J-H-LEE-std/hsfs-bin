import weka.core.Instances
import weka.core.converters.ArffSaver
import weka.core.converters.ConverterUtils.DataSource
import weka.filters.Filter
import weka.filters.unsupervised.attribute.Remove
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class HarmonySearch (
    hms: Int = 30,
    hmcr_min: Double = 0.7,
    hmcr_max: Double = 1.0,
    par: Double = 0.7,
    iter: Long = 0,
    init_seed: Long = 100000)
{
    private lateinit var HM: MutableList<Harmony>
    private val HMS = hms
    private val HMCR_MIN = hmcr_min
    private val HMCR_MAX = hmcr_max
    private var HMCR: Double = HMCR_MIN
    private val PAR = par
    private val iteration = iter
    private var seed = init_seed
    private val data: Instances = DataSource("./src/main/resources/truck_train.arff").dataSet
    private val domain = arrayOf(true, false)
    val func: (Array<Boolean>, Instances) -> WekaRater = {x, y -> WekaRater(x, y)}

    fun initHM(){
        HM = MutableList(this.HMS) { genHarmony() }
    }

    private fun genHarmony(): Harmony{
        val rand = Random(seed)
        val harm = VarPair(Array(data.numAttributes() - 1) { Random(seed).nextBoolean() }, Double.NaN)
        val harm_arr = arrayListOf<Boolean>()
        for (i in 0 until data.numAttributes() - 1){
            rand.setSeed(this.seed)
            harm_arr.add(rand.nextInt() % 2 == 0)
            this.seed++
        }
        harm.first = harm_arr.toTypedArray()

        return harm
    }

    fun genNewHarmony(): Harmony{
        val newHarmony = VarPair(Array(data.numAttributes() - 1) { true }, Double.NaN)

        for (i in newHarmony.first.indices){
            var newelem: Boolean
            val pr_c = RandNumber(seed++).getRandom()
            if (pr_c < HMCR){
                val domainPool = Array(this.HM.size) { x -> HM[x].first[i] }
                newelem = domainPool[RandNumber(seed++).getRandomInt(domainPool.size)]
                val pr_p = RandNumber(seed++).getRandom()
                if (pr_p > PAR) newelem = !newelem
            } else {
                newelem = domain[RandNumber(seed++).getRandomInt(domain.size)]
            }
            newHarmony.first[i] = newelem
        }

        return newHarmony
    }

    fun evaluate(harm: Harmony): Boolean{
        for (i in HM.indices){
            if (HM[i].second.isNaN()){
                HM[i].second = func(HM[i].first, data).evalFeatureSet()
            }
        }
        harm.second = func(harm.first, data).evalFeatureSet()
        HM.sortByDescending { it.second }
        if (harm.second > HM[HM.lastIndex].second){
            HM[HM.lastIndex] = harm
            return true
        } else return false
    }

    fun adjustHMCR(){
        HMCR += (HMCR_MAX - HMCR_MIN) / iteration
    }

    fun getMaxElem(): Harmony{
        HM.sortByDescending { it.second }
        return HM[0]
    }

    fun calcAvgScore(): Double{
        var score = 0.0
        for (i in HM.indices){
            score += HM[i].second
        }

        return score / HM.size
    }

    fun calcAvgFeature(): Double{
        val argarr = Array(HM.size) { 0 }

        for (i in HM.indices){
            val num: ArrayList<Int> = arrayListOf()
            for (j in HM[i].first.indices) {
                if (HM[i].first[j]) num.add(i+1)
            }
            argarr[i] = num.size
        }

        return 1.0 * argarr.sum() / argarr.size
    }

    fun exportARFF(index: ArrayList<Int>){
        val command = "${getFeatureIndex(index.toTypedArray())},last"
        val options: Array<String> = arrayOf("-V", "-R", command)
        val remove = Remove()
        remove.options = options
        remove.setInputFormat(data)
        val inst = Filter.useFilter(data, remove)

        val f = ArffSaver()
        f.instances = inst
        f.setFile(File("./src/main/resources/truck_train_hs.arff"))
        f.writeBatch()
    }

    private fun getFeatureIndex(num: Array<Int>): String {
        return num.joinToString(",")
    }
}