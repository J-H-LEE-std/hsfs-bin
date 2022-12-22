import weka.classifiers.Evaluation
import weka.classifiers.trees.J48
import weka.core.Instances
import weka.filters.Filter
import weka.filters.unsupervised.attribute.Remove
import java.util.Random
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class WekaRater(private val selected: Array<Boolean>, private val data: Instances) {
    private val selectedIndex by lazy {
        getFeatureIndex(selected)
    }
    private val classIndex = data.numAttributes() - 1
    companion object{
        @JvmStatic var randseed: Long = 10000
    }
    init{
        data.setClassIndex(classIndex)
    }

    private fun roundResult(num: Double): Double{
        return (num * 100000.0).roundToInt() / 100000.0
    }

    fun evalFeatureSet(folds:Int = 5): Double{
        val eval = Evaluation(preprocess(data))
        eval.crossValidateModel(J48(), preprocess(data), folds, Random(randseed++))
        return roundResult( eval.pctCorrect() )
    }

    private fun getFeatureIndex(bool: Array<Boolean>): String {
        val num: ArrayList<Int> = arrayListOf()
        for (i in bool.indices) {
            if (bool[i]) num.add(i+1)
        }

        return num.joinToString(",")
    }

    private fun preprocess(inst: Instances): Instances{
        val command = "$selectedIndex,last"
        val options: Array<String> = arrayOf("-V", "-R", command)
        val remove = Remove()
        remove.options = options
        remove.setInputFormat(inst)
        return Filter.useFilter(inst, remove)
    }
}