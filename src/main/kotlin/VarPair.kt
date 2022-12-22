import java.io.Serializable

data class VarPair<A, B> (var first: A, var second: B) : Serializable {
    override fun toString(): String = "($first, $second)"
}
