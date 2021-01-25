import kotlin.math.pow

open class Main() {
    open val sod : Int = 0
}

fun quadratic(a: Double, b: Double, c: Double): Pair<Double, Double> {
    val det: Double = b.pow(2) - 4 * a * c
    val x1: Double = ((-1 * b) + det.pow(0.5)) / (2 * a)
    val x2: Double = ((-1 * b) - det.pow(0.5)) / (2 * a)

    return Pair(x1, x2)
}

fun main(args: Array<String>) {
    println("Hello "+ args[0])
    val answers: MutableMap<Triple<Int, Int, Int>, Pair<Double, Double>> = mutableMapOf()
    var ijk: Triple<Int, Int, Int>
    var xs: Pair<Double, Double>
    for (i in -5..5) {
        for (j in -5..5) {
            for (k in -5..5) {
                ijk = Triple(i, j, k)
                xs = quadratic(i.toDouble(), j.toDouble(), k.toDouble())
                answers[ijk] = xs
            }
        }
    }
    println(answers)
}
