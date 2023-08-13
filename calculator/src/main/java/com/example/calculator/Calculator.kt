package com.example.calculator

import java.text.NumberFormat
import java.text.ParsePosition
import java.util.LinkedList
import java.util.Queue
import java.util.Stack


object Calculator {
    /**
     * Parsing mathematical expressions specified in infix notation and produce an abstract syntax tree
     * @param expression infix notation
     * @return a queue with the abstract syntax tree
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    @Throws(NumberFormatException::class)
    private fun calculate(expression: String): Queue<String> {
        val list: Queue<String> = LinkedList()
        val operator = Stack<String>()
        try {
            val token = getString(expression)
            for (i in token.indices) {
                if (token[i].isNotEmpty() && isNumeric(token[i])) list.add(token[i]) else {
                    if (isOperator(token[i])) {
                        while (!operator.empty() && !isLeftBracket(operator.peek()) && (precedence(
                                token[i]
                            ) < precedence(operator.peek()) ||
                                    precedence(token[i]) == precedence(operator.peek()) && isLeftAssociativity(
                                token[i]
                            ))
                        ) {
                            list.add(operator.pop())
                        }
                        operator.push(token[i])
                    } else if (isLeftBracket(token[i])) operator.push(token[i]) else if (isRightBracket(
                            token[i]
                        )
                    ) {
                        while (!operator.empty() && !isLeftBracket(operator.peek())) {
                            list.add(operator.pop())
                        }
                        if (isLeftBracket(operator.peek())) operator.pop() else throw NumberFormatException(
                            "Missing left parentheses"
                        )
                    }
                }
            }
        } catch (e: NumberFormatException) {
            throw NumberFormatException(e.message)
        }
        while (!operator.empty()) {
            if (!isRightBracket(operator.peek()) && !isLeftBracket(operator.peek())) list.add(
                operator.pop()
            ) else throw NumberFormatException("Mismatched parentheses")
        }
        return list
    }

    /**
     * Separate the expression into tokens
     * @param expression an expression in infix notation
     * @return a string in infix notation
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    private fun getString(expression: String): List<String> {
        val str: MutableList<String> = ArrayList()
        var numOp = 0
        var num = 0
        var character: Char
        var i = 0
        while (i < expression.length) {
            character = expression[i]
            if (isLeftBracket(character.toString()) || isRightBracket(character.toString())) {
                str.add(character.toString())
            } else if (character == '-' && (i == 0 || isPreviewOperator(
                    i - 1,
                    expression
                )) || character in '0'..'9'
            ) {
                var hold = character.toString()
                i++
                if (i < expression.length) character = expression[i]
                while (character in '0'..'9' && i < expression.length) {
                    hold += character.toString()
                    i++
                    if (i < expression.length) character = expression[i]
                }
                i--
                str.add(hold)
                num += 1
            } else if (isOperator(character.toString())) {
                str.add(character.toString())
                numOp += 1
            }
            i++
        }
        if (num - numOp == 1) return str
        throw NumberFormatException("There is more operator than operand")
    }

    /**
     * Check if the previous token is an operator or left parenthesis
     * @param pos the position of the current token
     * @param expression an expression in infix notation
     * @return true if the previous token is an left parenthesis or an operator otherwise false
     */
    private fun isPreviewOperator(position: Int, expression: String): Boolean {
        var pos = position
        while (pos >= 0) {
            if (isOperator(expression[pos].toString()) || isLeftBracket(expression[pos].toString())) {
                return true
            } else if (expression[pos] != ' ') {
                return false
            }
            pos--
        }
        return false
    }

    /**
     * Compute the expression
     * @param expression an expression in infix notation
     * @return the total as a double
     * @throws NumberFormatException if there is more operators than numbers or the parentheses are mismatched
     */
    fun compute(expression: String): Double {
        var lhs: Double
        var rhs: Double
        return try {
            val list = calculate(expression)
            val total = Stack<Double>()
            while (!list.isEmpty()) {
                if (list.peek()?.let { isNumeric(it) } == true) total.push(
                    list.remove().toDouble()
                ) else {
                    rhs = total.pop()
                    lhs = total.pop()
                    total.push(compute(lhs, rhs, list.remove()))
                }
            }
            total.pop()
        } catch (e: NumberFormatException) {
            throw NumberFormatException(e.message)
        }
    }

    /**
     * Compute the specific operator
     * @param lhs LHS of the operand
     * @param rhs RHS of the operand
     * @param op operator
     * @return the result of the computation
     */
    private fun compute(lhs: Double, rhs: Double, op: String): Double {
        when (op) {
            "+" -> return lhs + rhs
            "-" -> return lhs - rhs
            "/" -> return lhs / rhs
            "*", "x", "X" -> return lhs * rhs
            "^" -> return Math.pow(lhs, rhs)
        }
        return 0.0
    }

    /**
     * Check if the token is a number
     * @param str the token to be evaluated
     * @return true if the token is a number otherwise false
     */
    private fun isNumeric(str: String): Boolean {
        val formatter: NumberFormat = NumberFormat.getInstance()
        val pos = ParsePosition(0)
        formatter.parse(str, pos)
        return str.length == pos.index
    }

    /**
     * check if the token is an operator
     * @param op operator
     * @return true if the token is an operator otherwise false
     */
    private fun isOperator(op: String): Boolean {
        if (op.length == 1) {
            val symbol = op[0]
            if (symbol == '^' || symbol == '+' ||
                symbol == '-' || symbol == '*' ||
                symbol == '/' || symbol == 'x' ||
                symbol == 'X'
            ) return true
        }
        return false
    }

    /**
     * check if the token is a left bracket
     * @param op operator
     * @return true if the token is a left bracket otherwise false
     */
    private fun isLeftBracket(op: String): Boolean {
        if (op.length == 1) {
            val symbol = op[0]
            if (symbol == '(') return true
        }
        return false
    }

    /**
     * check if the token is a right bracket
     * @param op operator
     * @return true if the token is a right bracket otherwise false
     */
    private fun isRightBracket(op: String): Boolean {
        if (op.length == 1) {
            val symbol = op[0]
            if (symbol == ')') return true
        }
        return false
    }

    /**
     * check if the operator is a left associative
     * @param op operator
     * @return true if the operator is a left associative otherwise false
     */
    private fun isLeftAssociativity(op: String): Boolean {
        if (op.length == 1) {
            val symbol = op[0]
            if (symbol == '+' || symbol == '-' || symbol == '*' ||
                symbol == '/' || symbol == 'x' || symbol == 'X'
            ) return true
        }
        return false
    }

    /**
     * check if the precedence of an operator
     * @param op operator
     * @return the value of the precedence of the operator
     */
    private fun precedence(op: String): Int {
        val value: Int = when (op) {
            "^" -> 3
            "/", "*", "x", "X" -> 2
            "+", "-" -> 1
            else -> throw IllegalArgumentException("$op IS NOT A OPERATOR")
        }
        return value
    }
}