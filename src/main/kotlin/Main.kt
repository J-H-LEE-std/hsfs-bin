typealias Harmony = VarPair<Array<Boolean>, Double>

fun main(args: Array<String>) {
    val iter: Long = 50000
    val hs = HarmonySearch(iter = iter)
    val log = Logger()
    val numlog = Logger()
    val avglog = Logger()
    lateinit var harm: Harmony

    try{
        hs.initHM()

        for (it in 1..iter) {
            harm = hs.genNewHarmony()
            if(hs.evaluate(harm)) println("Answer updated in iteration {$it}.")
            log.log.add(Pair(it, hs.getMaxElem().second))
            numlog.log.add(Pair(it, hs.calcAvgFeature()))
            avglog.log.add(Pair(it, hs.calcAvgScore()))
            hs.adjustHMCR()
        }

        val selected = getFeatureIndex(hs.getMaxElem().first)
        println("Selected Feature: (${selected.joinToString(",")})")
        log.writeToFile("./src/main/resources/log_truck.csv")
        numlog.writeToFile("./src/main/resources/numlog_truck.csv")
        avglog.writeToFile("./src/main/resources/avglog_truck.csv")
        hs.exportARFF(selected)
        println("Saved Arff File Successfully.")
    } catch (e: Exception){
        println(e.message)
    }
}

private fun getFeatureIndex(bool: Array<Boolean>): ArrayList<Int> {
    val num: ArrayList<Int> = arrayListOf()
    for (i in bool.indices) {
        if (bool[i]) num.add(i+1)
    }

    return num
}