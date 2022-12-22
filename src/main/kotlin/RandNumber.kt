import java.util.Random

class RandNumber(seed: Long? = null) {
    private val rand = seed?.let { Random(it) } ?: Random()

    fun getRandom(): Double{
        return rand.nextDouble()
    }

    fun getRandomInt(int: Int? = null): Int{
        return int?.let { rand.nextInt(int) } ?: rand.nextInt()
    }
}