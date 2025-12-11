package day11

import java.io.File
import kotlin.time.measureTime

private fun parseInput(file: String) = File(file)
    .readLines()
    .map {
        val parts = it.split(": ")
        val host = parts[0]
        val children = parts[1].split(" ")
        host to children
    }


fun main() {
    println("Day 11 - Reactor")

    val input1 = parseInput("./src/day11/sample_input.txt")

    measureTime {

        val root = input1.find { it.first == "you" }?.let {
            TreeNode(it.first)
        } ?: run { error("Didn't found start entry!") }

        input1.addDevices(root)
        root.printAscii()

        println("Part I: ${root.countBranches()}")
    }.also {
        println("Execution time: $it")
    }

//    val input2 = parseInput("./src/day11/sample_input2.txt")
    val input2 = parseInput("./src/day11/full_input.txt")

    // part two requires memoization due to combinatorial explosion
    measureTime {
        val count =
            input2.dfsCount("svr", "fft") * input2.dfsCount("fft", "dac") * input2.dfsCount("dac", "out") +
            input2.dfsCount("svr", "dac") * input2.dfsCount("dac", "fft") * input2.dfsCount("fft", "out")
        println("Part II: $count")
    }.also {
        println("Execution time: $it")
    }
}

private fun List<Pair<String, List<String>>>.addDevices(root: TreeNode<String>) {
    val queue = ArrayDeque<TreeNode<String>>()
    queue.add(root)

    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        val childrenEntries = find { it.first == currentNode.value }?.second ?: continue

        for (child in childrenEntries) {
            val childNode = currentNode.addChild(child)
            queue.add(childNode)
        }

        // If the current node has no children, add "out" node
        if (childrenEntries.isEmpty()) {
            currentNode.addChild("out")
        }
    }
}

private fun List<Pair<String, List<String>>>.dfsCount(
    source: String,
    target: String,
    memo: MutableMap<Pair<String, String>, Long> = mutableMapOf()
): Long =
    if (source == target) 1L
    else memo.getOrPut(source to target) {
        findDevice(source)?.second?.sumOf { next ->
            dfsCount(next, target, memo)
        } ?: 0L
    }

private fun List<Pair<String, List<String>>>.findDevice(name: String): Pair<String, List<String>>? {
    return this.find { it.first == name }
}

private class TreeNode<T>(
    val value: T,
    val children: MutableList<TreeNode<T>> = mutableListOf()
)

private fun <T> TreeNode<T>.addChild(value: T): TreeNode<T> {
    val newNode = TreeNode(value)
    this.children.add(newNode)
    return newNode
}

private fun <T> TreeNode<T>.printAscii(prefix: String = "", isLast: Boolean = true) {
    // Print current node
    val connector = if (prefix.isEmpty()) "" else if (isLast) "└── " else "├── "
    println(prefix + connector + value)

    // Prepare prefix for children
    val newPrefix = prefix + if (isLast) "    " else "│   "

    // Print children with correct connectors
    children.forEachIndexed { index, child ->
        val last = index == children.lastIndex
        child.printAscii(newPrefix, last)
    }
}

private fun <T> TreeNode<T>.countBranches(): Int {
    if (children.isEmpty()) return 1
    return children.sumOf { it.countBranches() }
}

private fun <T> TreeNode<T>.allPaths(): List<List<T>> {
    val result = mutableListOf<List<T>>()

    fun dfs(node: TreeNode<T>, currentPath: MutableList<T>) {
        currentPath.add(node.value)

        if (node.children.isEmpty()) {
            // Leaf -> store a copy of the current path
            result.add(currentPath.toList())
        } else {
            // Recurse into children
            for (child in node.children) {
                dfs(child, currentPath)
            }
        }

        // Backtrack
        currentPath.removeAt(currentPath.lastIndex)
    }

    dfs(this, mutableListOf())
    return result
}
