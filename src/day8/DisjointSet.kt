package day8

class DisjointSetUnion<T> {
    // Maps to store the parent of each element and the rank (height approximation) of the set
    private val parent = mutableMapOf<T, T>()
    
    val sets
        get() = parent.entries.groupBy { find(it.key) }
    
    /**
     * Creates a new set containing only the element [element].
     */
    fun makeSet(element: T) {
        if (element !in parent) {
            parent[element] = element // Element is its own parent initially
        }
    }

    /**
     * Finds the representative (root) of the set containing [element], using path compression.
     */
    fun find(element: T): T {
        // Ensure the element has been added to the DSU
        if (element !in parent) makeSet(element) // Auto-create set if element is new

        val currentParent = parent[element]
        if (currentParent == element) return element

        // Path compression: recursively find the root and set it as the direct parent
        val root = find(currentParent!!)
        parent[element] = root
        return root
    }

    /**
     * Merges the sets containing [element1] and [element2] into a single set.
     */
    fun union(element1: T, element2: T) {
        parent[find(element2)] = find(element1)
    }

    override fun toString(): String {
        return parent.entries.groupBy { find(it.key) }
            .mapValues { it.value.map { entry -> entry.key } }
            .toString()
    }
}