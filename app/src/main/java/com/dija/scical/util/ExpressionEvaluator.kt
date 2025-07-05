package com.dija.scical.util

import com.dija.scical.domain.model.AngleMode
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.ArrayDeque
import kotlin.math.*

private fun Double.toRadiansIfDeg(mode: AngleMode) =
    if (mode == AngleMode.DEG) Math.toRadians(this) else this

object ExpressionEvaluator {

    val mc = MathContext(34, RoundingMode.HALF_UP)

    /**
     * @param input     Raw infix expression (e.g. "sin(45)+3^2")
     * @param angleMode DEG or RAD
     * @param memory    Current value stored in M (optional)
     * @param precision Digits shown to the user (rounded AFTER full‑precision calc)
     * @return          Final value OR throws IllegalArgumentException on malformed input
     */
    fun evaluate(
        input: String,
        angleMode: AngleMode = AngleMode.DEG,
        memory: BigDecimal = BigDecimal.ZERO,
        precision: Int = 10
    ): BigDecimal {
        // 1) Tokenise
        val tokens = Lexer(memory).scan(input)
        // 2) Infix → Postfix (RPN)
        val rpn = ShuntingYard.toRpn(tokens)
        // 3) Evaluate RPN
        val raw = Rpn.eval(rpn, angleMode, mc)
        // 4) Round to requested precision & strip trailing zeros
        return raw.setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros()
    }
}

/* ------------------------------------------------------------------------- */
/* TOKEN DEFINITION                                                           */
/* ------------------------------------------------------------------------- */

private sealed interface Token {
    data class Num(val value: BigDecimal) : Token
    data class Op(val symbol: Char) : Token          // + - * × / ÷ ^ % ! u (unary +) v (unary -) # (func marker)
    data class Fun(val name: String) : Token         // sin, cos, …
    object LParen : Token
    object RParen : Token
}

/* ------------------------------------------------------------------------- */
/* LEXER – converts raw string → List<Token>                                  */
/* ------------------------------------------------------------------------- */

private class Lexer(private val memory: BigDecimal) {

    private val constantMap = mapOf(
        "pi" to BigDecimal(Math.PI, ExpressionEvaluator.mc),
        "π"  to BigDecimal(Math.PI, ExpressionEvaluator.mc),
        "e"  to BigDecimal(E , ExpressionEvaluator.mc),
        "m"  to memory
    )

    fun scan(src: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        fun peek(): Char? = src.getOrNull(i)
        fun consume(): Char = src[i++]

        while (i < src.length) {
            when (val ch = peek()!!) {
                ' ', '\t', '\n' -> i++ // skip whitespace
                in '0'..'9', '.' -> {      // number literal
                    val start = i
                    while (peek()?.let { it.isDigit() || it == '.' } == true) i++
                    tokens += Token.Num(src.substring(start, i).toBigDecimal())
                }
                '+','-','*','×','/','÷','^','%','!' -> {
                    tokens += Token.Op(consume())
                }
                '(' -> { consume(); tokens += Token.LParen }
                ')' -> { consume(); tokens += Token.RParen }
                else -> {                         // function or constant
                    if (ch.isLetter()) {
                        val start = i
                        while (peek()?.isLetter() == true) i++
                        val word = src.substring(start, i).lowercase()
                        when {
                            word in Funcs.names -> tokens += Token.Fun(word)
                            word in constantMap -> tokens += Token.Num(constantMap.getValue(word))
                            else -> throw IllegalArgumentException("Unknown symbol: $word")
                        }
                    } else {
                        throw IllegalArgumentException("Illegal char: $ch")
                    }
                }
            }
        }
        return tokens
    }
}

/* ------------------------------------------------------------------------- */
/* SHUNTING‑YARD – infix → postfix (RPN)                                      */
/* ------------------------------------------------------------------------- */

private object ShuntingYard {

    private val precedence: Map<Char, Int> = mapOf(
        '+' to 2, '-' to 2,
        '×' to 3, '*' to 3, '÷' to 3, '/' to 3, '%' to 3,
        '^' to 4,
        'u' to 5, 'v' to 5,      // unary ±
        '!' to 6                 // factorial highest
    )

    fun toRpn(tokens: List<Token>): List<Token> {
        val output = ArrayDeque<Token>()
        val ops = ArrayDeque<Token.Op>()
        var prev: Token? = null

        fun pushOp(op: Token.Op) {
            val sym = op.symbol
            val isRightAssoc = sym == '^' || sym == 'u' || sym == 'v' || sym == '!'
            while (ops.isNotEmpty()) {
                val top = ops.peek()
                val higher = (precedence[top.symbol] ?: 0) > (precedence[sym] ?: 0)
                val equal  = (precedence[top.symbol] ?: 0) == (precedence[sym] ?: 0)
                if (higher || (!isRightAssoc && equal)) {
                    output += ops.pop()
                } else break
            }
            ops.push(op)
        }

        tokens.forEach { tk ->
            when (tk) {
                is Token.Num -> output += tk
                is Token.Fun -> ops.push(Token.Op('#')).also { ops.push(Token.Op('#')) /*marker*/; ops.push(Token.Op('#')) } // push marker (handled after ) )
                is Token.Op  -> {
                    // Detect unary +/‑ : occurs at start or after ( or another operator
                    if ((tk.symbol == '+' || tk.symbol == '-') &&
                        (prev == null || prev is Token.Op || prev is Token.LParen)) {
                        val unary = if (tk.symbol == '+') 'u' else 'v'
                        pushOp(Token.Op(unary))
                    } else if (tk.symbol == '!') {
                        // factorial is postfix, higher precedence than exponent
                        pushOp(tk)
                    } else {
                        pushOp(tk)
                    }
                }
                is Token.LParen -> ops.push(Token.Op('('))
                is Token.RParen -> {
                    while (ops.isNotEmpty() && ops.peek().symbol != '(') output += ops.pop()
                    if (ops.isEmpty()) throw IllegalArgumentException("Mismatched )")
                    ops.pop() // discard '('
                    // If a function marker is on top, pop it to output (we used '#')
                    if (ops.isNotEmpty() && ops.peek().symbol == '#') output += ops.pop()
                }
            }
            prev = tk
        }

        while (ops.isNotEmpty()) {
            val op = ops.pop()
            if (op.symbol == '(') throw IllegalArgumentException("Mismatched (")
            output += op
        }
        return output.toList()
    }
}

/* ------------------------------------------------------------------------- */
/* FUNCTION TABLE + RPN EVALUATOR                                            */
/* ------------------------------------------------------------------------- */

private object Funcs {
    val names = setOf("sin", "cos", "tan", "log", "ln", "sqrt", "abs")

    fun apply(name: String, x: Double, mode: AngleMode): Double = when (name) {
        "sin"  -> sin(x.toRadiansIfDeg(mode))
        "cos"  -> cos(x.toRadiansIfDeg(mode))
        "tan"  -> tan(x.toRadiansIfDeg(mode))
        "log"  -> log10(x)
        "ln"   -> ln(x)
        "sqrt" -> sqrt(x)
        "abs"  -> abs(x)
        else    -> error("Unknown function $name")
    }
}

private object Rpn {

    fun eval(rpn: List<Token>, angleMode: AngleMode, mc: MathContext): BigDecimal {
        val stack = ArrayDeque<BigDecimal>()

        rpn.forEach { tk ->
            when (tk) {
                is Token.Num -> stack.push(tk.value)
                is Token.Fun -> {
                    val arg = stack.pop().toDouble()
                    val res = Funcs.apply(tk.name, arg, angleMode)
                    stack.push(BigDecimal(res, mc))
                }
                is Token.Op  -> {
                    when (tk.symbol) {
                        '+', '-', '×', '*', '÷', '/', '^', '%', 'u', 'v', '!' -> {
                            when (tk.symbol) {
                                'u' -> { /* unary + noop */ }
                                'v' -> {
                                    val v = stack.pop()
                                    stack.push(v.negate())
                                }
                                '!' -> {
                                    val n = stack.pop().toInt()
                                    require(n >= 0) { "Factorial input must be ≥ 0" }
                                    val fact = (1..n).fold(BigDecimal.ONE) { acc, i -> acc.multiply(BigDecimal(i), mc) }
                                    stack.push(fact)
                                }
                                '+', '-', '×', '*', '÷', '/', '^', '%' -> {
                                    val b = stack.pop(); val a = stack.pop()
                                    val res = when (tk.symbol) {
                                        '+', -> a.add(b, mc)
                                        '-'  -> a.subtract(b, mc)
                                        '×', '*' -> a.multiply(b, mc)
                                        '÷', '/' -> a.divide(b, mc)
                                        '%'  -> a.remainder(b, mc)
                                        '^'  -> BigDecimal(a.toDouble().pow(b.toInt()), mc)
                                        else -> error("Op?")
                                    }
                                    stack.push(res)
                                }
                            }
                        }
                        '#' -> {/* function marker – already handled when ) encountered */}
                        else -> error("Unsupported op ${tk.symbol}")
                    }
                }
                else -> error("Unexpected token $tk")
            }
        }

        require(stack.size == 1) { "Malformed expression" }
        return stack.pop()
    }
}
