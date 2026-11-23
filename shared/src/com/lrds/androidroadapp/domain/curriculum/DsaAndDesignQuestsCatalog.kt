package com.lrds.androidroadapp.domain.curriculum

import com.lrds.androidroadapp.domain.model.InterviewQuestion
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.StudyResource
import com.lrds.androidroadapp.domain.model.SubTopic
import com.lrds.androidroadapp.domain.model.ZoneId

object DsaAndDesignQuestsCatalog {
    val quests: List<QuestModule> = listOf(
        // =========================================================================
        // ZONE 4: GOLDEN LABYRINTH OF DSA & ALGORITHMS (PARALLEL DAILY PRACTICE!)
        // =========================================================================
        QuestModule(
            id = "q21-dsa-big-o",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 21,
            title = "Big O Notation & Complexity Analysis",
            subtitle = "Time & space trade-offs, amortized complexity & Kotlin collection costs",
            dayRange = "Parallel • Days 1–7",
            estimatedHours = 4,
            highYield = true,
            sourceBadge = "Your DSA Course: Big O",
            icon = "TrendingUp",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-bigo-1",
                    title = "Big O Fundamentals: O(1), O(log n), O(n), O(n log n), O(n²), O(2ⁿ)",
                    description = "Complete the \"Big O\" track in your DSA course. Master dropping constants, worst vs average vs amortized complexity (e.g., ArrayList doubling), and identifying constraints (`n ≤ 10⁵` → O(n log n)).",
                    xp = 60,
                    highYield = true,
                    courseTag = "Your DSA Course: Trilha Big O",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-1",
                            title = "Big-O Cheat Sheet (Visual Reference)",
                            url = "https://www.bigocheatsheet.com/",
                            type = ResourceType.ARTICLE,
                        ),
                        StudyResource(
                            id = "r-dsa-2",
                            title = "NeetCode Roadmap (Interactive DSA Map)",
                            url = "https://neetcode.io/roadmap",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-bigo-2",
                    title = "Space Complexity, Call Stack Depth & Kotlin Collection Complexities",
                    description = "Know exact time/space of Kotlin `ArrayList`, `ArrayDeque`, `HashMap`, `LinkedHashMap`, `TreeMap`, `PriorityQueue`, and recursive stack frames during interviews.",
                    xp = 55,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-3",
                            title = "Kotlin Collections Overview (Official Docs)",
                            url = "https://kotlinlang.org/docs/collections-overview.html",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "If an interviewer gives you `n <= 10^5`, what time complexities will pass vs TLE?",
                    keyPoints = "O(n) or O(n log n) will easily pass (~10⁵–10⁶ ops). O(n²) (~10¹⁰ ops) will Time Limit Exceed (TLE). Always ask for input constraints before choosing an algorithm!",
                ),
            ),
        ),
        QuestModule(
            id = "q22-dsa-structures",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 22,
            title = "Core Data Structures",
            subtitle = "Arrays, HashMaps, Linked Lists, Stacks, Queues & Monotonic Stacks",
            dayRange = "Parallel • Days 8–20",
            estimatedHours = 10,
            highYield = true,
            sourceBadge = "Your DSA Course: Estrutura de dados",
            icon = "Boxes",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-ds-1-arrays-maps",
                    title = "Arrays, Dynamic Arrays, HashMaps & HashSets",
                    description = "Complete Arrays & Hashing from \"Estrutura de dados\". Master frequency counting, prefix sums, HashMap collisions/load factor, and `LinkedHashMap` (LRU Cache!).",
                    xp = 75,
                    highYield = true,
                    courseTag = "Your DSA Course: Estrutura de dados",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-4",
                            title = "LeetCode: Two Sum (#1) & Group Anagrams (#49)",
                            url = "https://leetcode.com/problems/group-anagrams/",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-dsa-5",
                            title = "LeetCode: LRU Cache (#146 — Senior Classic!)",
                            url = "https://leetcode.com/problems/lru-cache/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-ds-2-linked-lists",
                    title = "Linked Lists: Reversal, Fast/Slow Pointers, Merge & Cycle Detection",
                    description = "Dummy head trick, in-place reversal, Floyd’s Tortoise & Hare cycle detection, finding middle node, and Doubly Linked Lists.",
                    xp = 70,
                    highYield = true,
                    courseTag = "Your DSA Course: Estrutura de dados",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-6",
                            title = "LeetCode: Reverse Linked List (#206) & Linked List Cycle (#141)",
                            url = "https://leetcode.com/problems/reverse-linked-list/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-ds-3-stacks-queues",
                    title = "Stacks, Queues, ArrayDeque & Monotonic Stack Pattern",
                    description = "Use Kotlin `ArrayDeque` (never legacy `java.util.Stack`!). Master Valid Parentheses, Min Stack, and Monotonic Stack (Daily Temperatures, Next Greater Element).",
                    xp = 70,
                    highYield = true,
                    courseTag = "Your DSA Course: Estrutura de dados",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-7",
                            title = "LeetCode: Valid Parentheses (#20) & Daily Temperatures (#739)",
                            url = "https://leetcode.com/problems/daily-temperatures/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How would you implement an O(1) LRU Cache from scratch in Kotlin (without using Android `LruCache`)?",
                    keyPoints = "Combine a `HashMap<Key, Node>` for O(1) lookup with a Doubly Linked List (with dummy head/tail sentinels) for O(1) move-to-front and O(1) tail eviction.",
                ),
            ),
        ),
        QuestModule(
            id = "q23-dsa-strings-window",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 23,
            title = "Strings, Two Pointers & Sliding Window",
            subtitle = "StringBuilder efficiency, opposite/fast pointers & variable sliding windows",
            dayRange = "Parallel • Days 21–32",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Your DSA Course: Strings + Pattern",
            icon = "TextCursorInput",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-str-1",
                    title = "String Internals, StringBuilder, Frequency Arrays & Palindromes",
                    description = "Complete the \"Strings\" track in your DSA course. Understand JVM String immutability (why `+` in a loop is O(n²)), `IntArray(26)` vs `HashMap`, and expand-around-center.",
                    xp = 65,
                    highYield = true,
                    courseTag = "Your DSA Course: Trilha String",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-8",
                            title = "LeetCode: Valid Palindrome (#125) & Longest Palindromic Substring (#5)",
                            url = "https://leetcode.com/problems/longest-palindromic-substring/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-str-2-twopointers",
                    title = "Two Pointers Pattern (3Sum, Container With Most Water, Trapping Rain Water)",
                    description = "Recognize sorted-array pair problems, skipping duplicates cleanly, and shrinking search space in O(n) time with O(1) extra space.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-9",
                            title = "LeetCode: Container With Most Water (#11) & 3Sum (#15)",
                            url = "https://leetcode.com/problems/3sum/",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-dsa-10",
                            title = "YouTube: NeetCode Two Pointers Playlist",
                            url = "https://www.youtube.com/results?search_query=NeetCode+Two+Pointers+patterns",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-str-3-sliding-window",
                    title = "Sliding Window (Fixed & Variable Window Templates)",
                    description = "One of the #1 asked patterns in US remote interviews! Master Longest Substring Without Repeating Characters (#3), Longest Repeating Character Replacement (#424), and Minimum Window Substring (#76).",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-11",
                            title = "LeetCode: Longest Substring Without Repeating Characters (#3)",
                            url = "https://leetcode.com/problems/longest-substring-without-repeating-characters/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How do you immediately recognize a Sliding Window problem in a coding interview?",
                    keyPoints = "Keywords like \"contiguous subarray\", \"longest/shortest substring\", \"at most K distinct\", or \"window of size K\" over a linear sequence where expanding/shrinking maintains a monotonic condition.",
                ),
            ),
        ),
        QuestModule(
            id = "q24-dsa-sorting-binary-search",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 24,
            title = "Sorting & Binary Search",
            subtitle = "Merge/Quick/TimSort, Intervals, Rotated Arrays & Binary Search on Answer",
            dayRange = "Parallel • Days 33–45",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Your DSA Course: Sorting + Pattern",
            icon = "ArrowDownWideNarrow",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-sort-1",
                    title = "Sorting Algorithms (MergeSort, QuickSort, TimSort, Bucket Sort) & Intervals",
                    description = "Complete the \"Sorting\" track in your DSA course. Know stability, worst-case vs average, Kotlin `sortedWith(compareBy(...))`, and Interval problems (Merge Intervals #56, Meeting Rooms).",
                    xp = 70,
                    highYield = true,
                    courseTag = "Your DSA Course: Trilha Sorting",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-12",
                            title = "LeetCode: Merge Intervals (#56) & Top K Frequent Elements (#347)",
                            url = "https://leetcode.com/problems/merge-intervals/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-bs-2",
                    title = "Binary Search Mastery & Binary Search on Answer Space",
                    description = "Prevent integer overflow (`left + (right - left) / 2`), Search in Rotated Sorted Array (#33), First/Last position, and monotonic answer space (Koko Eating Bananas #875).",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-13",
                            title = "LeetCode: Search in Rotated Sorted Array (#33)",
                            url = "https://leetcode.com/problems/search-in-rotated-sorted-array/",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-dsa-14",
                            title = "LeetCode: Koko Eating Bananas (#875)",
                            url = "https://leetcode.com/problems/koko-eating-bananas/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Why does Kotlin/JVM use TimSort for sorting objects instead of pure QuickSort?",
                    keyPoints = "TimSort (hybrid MergeSort + InsertionSort) is stable (preserves relative order of equal keys) and guarantees O(n log n) worst-case while running in O(n) on nearly-sorted real-world data.",
                ),
            ),
        ),
        QuestModule(
            id = "q25-dsa-trees-heaps-tries",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 25,
            title = "Trees, Tries & Heaps (Priority Queues)",
            subtitle = "DFS/BFS traversals, BSTs, LCA, PriorityQueue Top-K & Autocomplete Tries",
            dayRange = "Parallel • Days 46–62",
            estimatedHours = 10,
            highYield = true,
            sourceBadge = "Your DSA Course: Trees + Big Tech",
            icon = "GitFork",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-tree-1",
                    title = "Binary Trees & BSTs: DFS (Pre/In/Post), BFS Level Order & LCA",
                    description = "Complete the \"Trees\" track in your DSA course. Master Invert Binary Tree, Max Depth, Validate BST (#98), Lowest Common Ancestor (#236), and Level Order Traversal (#102).",
                    xp = 80,
                    highYield = true,
                    courseTag = "Your DSA Course: Trilha Trees",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-15",
                            title = "LeetCode: Binary Tree Level Order (#102) & LCA (#236)",
                            url = "https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-heap-2",
                    title = "Heaps / Priority Queues (Top-K, Merge K Lists & Two-Heap Median)",
                    description = "Master Kotlin `PriorityQueue<T>` (min-heap vs max-heap comparator), Kth Largest Element (#215), K Closest Points (#973), and Merge K Sorted Lists (#23).",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-16",
                            title = "LeetCode: Kth Largest Element (#215) & Merge K Lists (#23)",
                            url = "https://leetcode.com/problems/kth-largest-element-in-an-array/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-trie-3",
                    title = "Tries (Prefix Trees) for Search & Mobile Autocomplete",
                    description = "Implement Trie (#208) and Design Add & Search Words (#211) — also a super practical structure for local search/autocomplete in Mobile System Design!",
                    xp = 65,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-17",
                            title = "LeetCode: Implement Trie (#208)",
                            url = "https://leetcode.com/problems/implement-trie-prefix-tree/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "When finding the Top K largest elements out of N items, why use a Min-Heap of size K instead of a Max-Heap of size N?",
                    keyPoints = "Maintaining a Min-Heap of size K takes O(N log K) time and only O(K) space (evicting the smallest whenever size > K), which is huge when N is massive (or streaming) and K is small.",
                ),
            ),
        ),
        QuestModule(
            id = "q26-dsa-graphs-backtracking-dp",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 26,
            title = "Graphs, Backtracking & Dynamic Programming",
            subtitle = "BFS/DFS grids, Topological Sort, Union-Find, Subsets & 1D/2D DP essentials",
            dayRange = "Parallel • Days 63–80",
            estimatedHours = 12,
            highYield = true,
            sourceBadge = "Big Tech Senior Essentials",
            icon = "Network",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-graph-1",
                    title = "Graphs: Grid BFS/DFS, Shortest Path, Topological Sort & Union-Find",
                    description = "Number of Islands (#200), Clone Graph (#133), Rotting Oranges (#994), Course Schedule (#207 — Kahn’s BFS Topo Sort), and Number of Connected Components (Union-Find).",
                    xp = 90,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-18",
                            title = "LeetCode: Number of Islands (#200) & Course Schedule (#207)",
                            url = "https://leetcode.com/problems/course-schedule/",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-dsa-19",
                            title = "YouTube: NeetCode Graph Algorithms Crash Course",
                            url = "https://www.youtube.com/results?search_query=NeetCode+Graph+Algorithms+BFS+DFS+Topological+Sort",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-bt-2",
                    title = "Backtracking Template (Subsets, Permutations, Combination Sum, Word Search)",
                    description = "Master the universal `choose → explore → unchoose` recursion template and pruning duplicate branches.",
                    xp = 75,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-20",
                            title = "LeetCode: Subsets (#78) & Combination Sum (#39)",
                            url = "https://leetcode.com/problems/combination-sum/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-dp-3",
                    title = "Dynamic Programming Essentials (Memoization → Tabulation)",
                    description = "Focus on high-frequency Senior mobile DP: Climbing Stairs, House Robber (#198), Coin Change (#322), Word Break (#139), Longest Increasing Subsequence (#300), and Unique Paths (#62).",
                    xp = 90,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-21",
                            title = "LeetCode: Coin Change (#322) & Word Break (#139)",
                            url = "https://leetcode.com/problems/coin-change/",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "When should you use BFS vs DFS on a graph?",
                    keyPoints = "Use BFS whenever you need the shortest path (minimum steps/hops) in an unweighted graph or level-by-level expansion. Use DFS for path existence, cycle detection, topological ordering, backtracking, or exploring all connected components.",
                ),
            ),
        ),
        QuestModule(
            id = "q27-dsa-leetcode-sprints",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 27,
            title = "LeetCode Mastery & Kotlin Speed Drills",
            subtitle = "Your LeetCode track + NeetCode 75/150 timed practice & English narration",
            dayRange = "Parallel • Days 15–90",
            estimatedHours = 15,
            highYield = true,
            sourceBadge = "Your DSA Course: LeetCode",
            icon = "Terminal",
            subtopics = listOf(
                SubTopic(
                    id = "dsa-lc-1",
                    title = "Complete Your Course \"LeetCode\" Track + Blind 75 Core in Kotlin",
                    description = "Finish all curated problems in your course \"LeetCode\" track and log at least 50–75 Blind/NeetCode problems written cleanly in idiomatic Kotlin.",
                    xp = 100,
                    highYield = true,
                    courseTag = "Your DSA Course: Trilha LeetCode",
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-22",
                            title = "NeetCode Blind 75 Problem List",
                            url = "https://neetcode.io/practice?tab=blind75",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-dsa-23",
                            title = "Grind 75 (Customizable Weekly Interview Plan)",
                            url = "https://www.techinterviewhandbook.org/grind75",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "dsa-lc-2",
                    title = "Live Coding Protocol in English (Clarify → Brute → Optimal → Dry Run)",
                    description = "Practice the 5-step US/Big Tech coding rubric: 1) Restate & ask edge cases (null, empty, duplicates, negatives), 2) Propose brute force + Big O, 3) Optimize & get buy-in, 4) Code cleanly while narrating, 5) Trace a dry-run test case by hand.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-dsa-24",
                            title = "Tech Interview Handbook: Coding Interview Best Practices",
                            url = "https://www.techinterviewhandbook.org/coding-interview-techniques/",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "What do you do if you get stuck for 3 minutes during a live US/Big Tech DSA interview?",
                    keyPoints = "Never go silent! Verbalize what you have tried, walk through a small concrete example by hand, state the bottleneck of the brute-force solution (e.g. \"this inner lookup is O(n), maybe a HashMap or sorting helps\"), and welcome interviewer hints collaboratively.",
                ),
            ),
        ),
        QuestModule(
            id = "q28-boss-4",
            zoneId = ZoneId.GOLDEN,
            pillar = PillarId.DSA,
            order = 28,
            title = "BOSS BATTLE 4: 45-Minute Big Tech Coding Simulation",
            subtitle = "Defeat the Labyrinth Dragon: 2 unseen Mediums in 45 min in Kotlin + English",
            dayRange = "Day 82",
            estimatedHours = 3,
            isBoss = true,
            highYield = true,
            sourceBadge = "Zone 4 Boss Battle",
            icon = "Swords",
            subtopics = listOf(
                SubTopic(
                    id = "b4-1-mock-dsa",
                    title = "Complete 2 Timed Mock Coding Interviews (Pramp / Peer / Self-Timed)",
                    description = "Run two 45-minute sessions with zero IDE autocomplete (CoderPad/Google Doc style), narrating trade-offs, time/space complexity, and dry-run edge cases completely in English.",
                    xp = 180,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b4-1",
                            title = "Pramp / Exponent Free Peer Mock Interviews",
                            url = "https://www.tryexponent.com/practice",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Boss Self-Check: Did you clarify constraints, explain Big O before coding, handle edge cases, and manually trace your code without hitting Run first?",
                    keyPoints = "Top Big Tech interviewers evaluate signal across 4 axes: Problem Solving, Coding Fluency, Verification/Testing, and Communication.",
                ),
            ),
        ),

        // =========================================================================
        // ZONE 5: COSMIC MOBILE SYSTEM DESIGN & GLOBAL OFFER SUMMIT
        // =========================================================================
        QuestModule(
            id = "q29-sd-foundations",
            zoneId = ZoneId.COSMIC,
            pillar = PillarId.SYSTEM_DESIGN,
            order = 29,
            title = "System Design Foundations (Your Course Module 4)",
            subtitle = "Your completed intro lessons + mobile-specific constraints & interview framework",
            dayRange = "Days 73–75 (+ Active Review)",
            estimatedHours = 4,
            highYield = true,
            sourceBadge = "Your SD Course (Module 4)",
            icon = "Compass",
            subtopics = listOf(
                SubTopic(
                    id = "sd-course-1-intro",
                    title = "System Design Introdução (Course Module 4 • 08:10)",
                    description = "Completed in your Mobile System Design course! Keep this fresh in your Spaced Repetition review queue so your opening 3-minute structure feels effortless.",
                    xp = 50,
                    highYield = true,
                    courseTag = "Your SD Course • Completo (08:10)",
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-1",
                            title = "Mobile System Design Book (Alex Lementuev — GitHub)",
                            url = "https://github.com/weeeBox/mobile-system-design",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-course-2-functional",
                    title = "Framework Requisitos Funcionais (Course Module 4 • 09:58)",
                    description = "Completed in your course! Scoping MVP features vs out-of-scope features in the first 5 minutes, identifying user flows, and agreeing on boundaries with your interviewer.",
                    xp = 55,
                    highYield = true,
                    courseTag = "Your SD Course • Completo (09:58)",
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-2",
                            title = "Mobile System Design Interview Framework Guide",
                            url = "https://github.com/weeeBox/mobile-system-design#interview-structure",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-course-3-nonfunctional",
                    title = "Framework Requisitos Não Funcionais (Course Module 4 • 09:39)",
                    description = "Completed in your course! Offline support, latency/scroll 60–120fps, battery efficiency, low-end devices, data usage, security/privacy, observability, and backward API compatibility.",
                    xp = 55,
                    highYield = true,
                    courseTag = "Your SD Course • Completo (09:39)",
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-3",
                            title = "Non-Functional Requirements Checklist for Mobile",
                            url = "https://github.com/weeeBox/mobile-system-design",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-found-4-mobile-constraints",
                    title = "Mobile vs Backend System Design: Client Constraints & API Contracts",
                    description = "Why mobile SD focuses on client architecture, flaky cellular networks, OS background/process kills, disk/RAM limits, backwards compatibility (old app versions!), and BFF/GraphQL/Pagination payloads.",
                    xp = 65,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-4",
                            title = "YouTube: Mobile System Design Interview Guide (Senior Android)",
                            url = "https://www.youtube.com/results?search_query=Mobile+System+Design+Interview+Android+Senior+offline+first",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "How should you allocate your 45 minutes in a Senior Mobile System Design interview?",
                    keyPoints = "Minutes 0–5: Functional & Non-Functional Requirements + Scale/Constraints. Minutes 5–15: High-Level Client Architecture Diagram + API/Data Models. Minutes 15–35: Deep Dives (Offline/Cache, Pagination, Sync, Image/Media, Edge Cases). Minutes 35–45: Performance, Observability, Security, Testing & Trade-offs.",
                ),
            ),
        ),
        QuestModule(
            id = "q30-sd-building-blocks",
            zoneId = ZoneId.COSMIC,
            pillar = PillarId.SYSTEM_DESIGN,
            order = 30,
            title = "Mobile System Design Core Building Blocks",
            subtitle = "Sync engines, multi-tier caching, transport protocols & Image Loader design",
            dayRange = "Days 76–81",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Module 17 • Senior Mobile SD",
            icon = "Blocks",
            subtopics = listOf(
                SubTopic(
                    id = "sd-bb-1-client-arch",
                    title = "Client Architecture Blueprint: UI, Domain, Repository, Sync Engine & Local DB",
                    description = "Drawing crystal-clear Excalidraw/Whimsical diagrams showing reactive SSOT data flow, DI boundaries, pagination state machines, and modular feature boundaries.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-5",
                            title = "Excalidraw (Practice Drawing Clean Architecture Diagrams)",
                            url = "https://excalidraw.com/",
                            type = ResourceType.PRACTICE,
                        ),
                        StudyResource(
                            id = "r-sd-6",
                            title = "Android App Architecture Blueprint (Official)",
                            url = "https://developer.android.com/topic/architecture",
                            type = ResourceType.DOCS,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-bb-2-networking-sync",
                    title = "Real-Time Transport (Polling vs SSE vs WebSockets vs Push) & Offline Sync",
                    description = "Comparing Short/Long Polling, SSE, WebSockets, FCM/Push; designing an Outbox/Mutation Queue with idempotency keys, exponential backoff + jitter, and conflict resolution.",
                    xp = 80,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-7",
                            title = "Mobile System Design: Real-Time & Offline Sync Patterns",
                            url = "https://github.com/weeeBox/mobile-system-design",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-bb-3-image-loader",
                    title = "Design an Image Loading Library (Coil / Glide Internals)",
                    description = "Classic Meta/Google/Uber mobile prompt! Memory L1 (LruCache sized by % of heap) + Disk L2 (DiskLruCache), request deduplication, bitmap downsampling (`inSampleSize`), lifecycle cancellation, and prefetching.",
                    xp = 85,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-8",
                            title = "Coil Architecture & Caching Docs (GitHub)",
                            url = "https://coil-kt.github.io/coil/",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-sd-9",
                            title = "YouTube: Design an Image Caching Library (Mobile System Design)",
                            url = "https://www.youtube.com/results?search_query=Design+Image+Loading+Library+Android+Mobile+System+Design",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "If 20 cells in a LazyColumn request the exact same avatar image URL simultaneously, how should your Image Loader handle it?",
                    keyPoints = "Check L1 Memory Cache first. On miss, coalesce/deduplicate in-flight requests using a `ConcurrentHashMap<CacheKey, Deferred<Bitmap>>` so only ONE network/disk decode executes and all 20 targets receive the decoded result.",
                ),
            ),
        ),
        QuestModule(
            id = "q31-sd-case-studies",
            zoneId = ZoneId.COSMIC,
            pillar = PillarId.SYSTEM_DESIGN,
            order = 31,
            title = "Senior Mobile System Design Case Studies",
            subtitle = "Master the Top 6 Big Tech & US/EU prompts end-to-end on a whiteboard",
            dayRange = "Days 82–91",
            estimatedHours = 14,
            highYield = true,
            sourceBadge = "Big Tech Top Prompts",
            icon = "Orbit",
            subtopics = listOf(
                SubTopic(
                    id = "sd-cs-1-social-feed",
                    title = "Case Study 1: Design an Infinite Social Feed (Instagram / X / LinkedIn)",
                    description = "Cursor pagination, normalized local DB cache (Post + Author tables), prefetching images ahead of viewport, optimistic likes/comments, and handling feed refresh without losing scroll position.",
                    xp = 90,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-10",
                            title = "YouTube: Design Instagram / News Feed Mobile System Design",
                            url = "https://www.youtube.com/results?search_query=Mobile+System+Design+Instagram+News+Feed+Android",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-cs-2-chat-app",
                    title = "Case Study 2: Design a Real-Time Chat App (WhatsApp / Slack)",
                    description = "WebSocket lifecycle & heartbeat, local-first message states (`SENDING → SENT → DELIVERED → READ`), client-generated UUIDs for dedup, message ordering, media chunked upload, and E2EE awareness.",
                    xp = 90,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-11",
                            title = "YouTube: Design WhatsApp / Chat App Mobile System Design",
                            url = "https://www.youtube.com/results?search_query=Mobile+System+Design+WhatsApp+Chat+App+Android",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-cs-3-uber-video",
                    title = "Case Study 3: Live Location (Uber) & Short Video Feed (TikTok / YouTube)",
                    description = "Uber: FusedLocationProvider battery-adaptive polling, Foreground Service, map marker interpolation, geofencing. TikTok: ExoPlayer/Media3 instance pooling, HLS/DASH adaptive bitrate, pre-buffering next 2 videos.",
                    xp = 90,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-12",
                            title = "Jetpack Media3 / ExoPlayer Docs (Official)",
                            url = "https://developer.android.com/media/media3/exoplayer",
                            type = ResourceType.DOCS,
                        ),
                        StudyResource(
                            id = "r-sd-13",
                            title = "YouTube: Design Uber / TikTok Mobile System Design",
                            url = "https://www.youtube.com/results?search_query=Mobile+System+Design+Uber+TikTok+Android",
                            type = ResourceType.YOUTUBE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "sd-cs-4-sdk-upload",
                    title = "Case Study 4: Analytics SDK & Resumable Large File Uploader",
                    description = "Analytics SDK: thread-safe event batching, disk persistence, battery/network-aware flushing, tiny APK footprint, zero host-app crashes. File Uploader: chunked multipart uploads, WorkManager resumability, checksums.",
                    xp = 85,
                    resources = listOf(
                        StudyResource(
                            id = "r-sd-14",
                            title = "Mobile System Design Case Studies Repo",
                            url = "https://github.com/weeeBox/mobile-system-design",
                            type = ResourceType.REPO,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "In a WhatsApp-style chat app, how do you prevent duplicate messages if a user sends a message right as the network drops before the server ACK arrives?",
                    keyPoints = "Generate a client-side UUID (`clientMessageId`) as an idempotency key and persist it in Room as `SENDING`. When retrying on reconnect, the server deduplicates by `clientMessageId` and returns the canonical `serverMessageId` + timestamp.",
                ),
                InterviewQuestion(
                    question = "How do you make a TikTok vertical video feed start playing the next video in <150ms without running out of RAM?",
                    keyPoints = "Reuse a small pool of 2–3 Media3 `ExoPlayer` instances (current + preloaded next/prev), pre-buffer only the first 2–5 seconds into a bounded `SimpleCache`, adapt bitrate with HLS/DASH, and immediately release decoders for offscreen items.",
                ),
            ),
        ),
        QuestModule(
            id = "q32-career-us-eu-prep",
            zoneId = ZoneId.COSMIC,
            pillar = PillarId.CAREER,
            order = 32,
            title = "US / EU & Big Tech Career & Interview Prep",
            subtitle = "ATS Resume, STAR Behavioral Stories in English, Remote Strategy & Negotiation",
            dayRange = "Active Now • Days 1–95",
            estimatedHours = 8,
            highYield = true,
            sourceBadge = "Module 18 • Brazil → US/EU",
            icon = "Globe2",
            subtopics = listOf(
                SubTopic(
                    id = "car-1-resume-linkedin",
                    title = "US/EU Senior Android Resume (XYZ Formula) & LinkedIn Recruiter SEO",
                    description = "1–2 page ATS-clean English resume quantifying impact (\"Reduced cold start by 38% via Baseline Profiles for 2M MAU\"), US/EU timezone-friendly header (\"Brazil — GMT-3, US-EST overlap\"), and keyword-rich LinkedIn.",
                    xp = 70,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-car-1",
                            title = "Tech Interview Handbook: Software Engineer Resume Guide",
                            url = "https://www.techinterviewhandbook.org/resume/",
                            type = ResourceType.ARTICLE,
                        ),
                        StudyResource(
                            id = "r-car-2",
                            title = "Levels.fyi (Check Real US/EU/Remote Senior Android Salaries)",
                            url = "https://www.levels.fyi/t/software-engineer/focus/android",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "car-2-star-behavioral",
                    title = "Build Your 8 Senior STAR Stories in English (Big Tech Rubrics)",
                    description = "Write & rehearse 8 concise 2–3 min stories: 1) High-impact architecture decision, 2) Conflict/disagreement with PM or peer, 3) Production outage/failure & ownership, 4) Ambiguous project leadership, 5) Mentoring/raising the bar, 6) Performance win, 7) Cross-team alignment, 8) Saying no / pushing back with data.",
                    xp = 85,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-car-3",
                            title = "Tech Interview Handbook: Behavioral Interview Prep",
                            url = "https://www.techinterviewhandbook.org/behavioral-interview/",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "car-3-brazil-remote-playbook",
                    title = "Brazil → US/EU Remote Strategy: Sourcing, Contractor (USD/EUR) & Negotiation",
                    description = "Targeting US/EU remote-friendly companies (Wellfound, Otta/Welcome to the Jungle, LinkedIn Boolean search, direct referrals, staffing partners), understanding B2B PJ contractor vs EOR (Deel/Remote), and negotiating total comp.",
                    xp = 75,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-car-4",
                            title = "Wellfound (US Startups Hiring Remote Engineers)",
                            url = "https://wellfound.com/",
                            type = ResourceType.ARTICLE,
                        ),
                        StudyResource(
                            id = "r-car-5",
                            title = "Levels.fyi Salary Negotiation Guide",
                            url = "https://www.levels.fyi/blog/salary-negotiation-guide.html",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Tell me about a time you disagreed with a technical or product decision. How did you handle it?",
                    keyPoints = "Use STAR: focus on data/user impact rather than ego, show you actively listened to their constraints, proposed a concrete compromise or experiment, and highlight the measurable outcome.",
                ),
                InterviewQuestion(
                    question = "What distinguishes a Senior Android Engineer from a Mid-level engineer in US/EU interviews?",
                    keyPoints = "Ownership beyond tickets: proactively identifying tech debt/risks, communicating trade-offs clearly to non-engineers, raising team quality (testing, CI, PR reviews, mentoring), and designing scalable systems under ambiguity.",
                ),
            ),
        ),
        QuestModule(
            id = "q33-final-boss",
            zoneId = ZoneId.COSMIC,
            pillar = PillarId.CAREER,
            order = 33,
            title = "FINAL BOSS: Full US / Big Tech On-Site Loop",
            subtitle = "Claim the Global Offer Trophy — 4-Round End-to-End Mock Interview Loop",
            dayRange = "Days 95–100",
            estimatedHours = 5,
            isBoss = true,
            highYield = true,
            sourceBadge = "Ultimate Summit Boss",
            icon = "Crown",
            subtopics = listOf(
                SubTopic(
                    id = "b5-1-full-loop",
                    title = "Run Full 4-Round Mock Loop (DSA + Android Deep Dive + Mobile SD + Behavioral)",
                    description = "Simulate a real US/Big Tech final loop across 1–2 days completely in English. Record notes on weak spots, patch any final gaps via Spaced Repetition, and celebrate your readiness!",
                    xp = 250,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b5-1",
                            title = "Pramp / Exponent Mock Interview Platform",
                            url = "https://www.tryexponent.com/practice",
                            type = ResourceType.PRACTICE,
                        ),
                    ),
                ),
                SubTopic(
                    id = "b5-2-offer-retro",
                    title = "Post-Interview Debrief Log & Continuous Offer Pipeline",
                    description = "After every real interview you are doing right now, log the exact questions asked in your quest notes, flag any shaky topics for Spaced Repetition, and iterate fast!",
                    xp = 150,
                    highYield = true,
                    resources = listOf(
                        StudyResource(
                            id = "r-b5-2",
                            title = "Android Weekly (Stay Sharp on Modern Android News)",
                            url = "https://androidweekly.net/",
                            type = ResourceType.ARTICLE,
                        ),
                    ),
                ),
            ),
            interviewQuestions = listOf(
                InterviewQuestion(
                    question = "Final Boss Check: What 3 thoughtful senior-level questions will YOU ask the Hiring Manager and Staff Engineers at the end of every interview?",
                    keyPoints = "Examples: 1) \"What are the biggest technical or architectural bottlenecks the Android team wants to solve in the next 6–12 months?\" 2) \"How do you measure mobile release quality and engineering velocity?\" 3) \"What separates a good Senior Android dev from a great one on this team?\"",
                ),
            ),
        ),
    )
}
