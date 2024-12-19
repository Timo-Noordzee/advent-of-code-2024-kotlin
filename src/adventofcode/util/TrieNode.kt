package adventofcode.util

class TrieNode(
    private val children: Array<TrieNode?> = Array(26) { null },
    var isEnd: Boolean = false
) {
    operator fun contains(key: Char) = children[key - 'a'] != null

    operator fun get(key: Char) = children[key - 'a']

    operator fun set(key: Char, value: TrieNode) {
        children[key - 'a'] = value
    }
}