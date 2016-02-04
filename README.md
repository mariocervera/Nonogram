# Nonogram

The Nonogram puzzle (also known as Hanjie, Picross, or Griddler) is categorized as a Constraint Satisfaction Problem (CSP).
This type of problems are defined as a set of objects whose state must satisfy a number of constraints or limitations.
CSPs are typically solved using a form of search, such as backtracking, constraint propagation, or local search.

This repository contains a Nonogram solver that is based on backtracking. The algorithm incrementally builds candidate
solutions, and abandons each partial candidate ("backtracks") as soon as the algorithm determines that the candidate cannot
possibly be completed to a valid solution. The (tree) search space that is generated is traversed in depth-first order.

The algorithm, before resorting to backtracking, applies a set of direct rules that enable the partial (and, sometimes,
total) resolution of the problem. Thus, the backtracking algorithm does not start the Nonogram from scratch; it starts
from a partially (or totally) solved Nonogram. This results in a reduction in execution time. Additionally, the algorithm
reduces the time of execution by means of pruning conditions, which discard nodes of the search tree that cannot possible
lead to a valid solution. This is in contrast to brute-force algorithms, which are generally too ineffective (mostly in
terms of execution time) since they systematically enumerate ALL possible candidates for the solution and check whether
each candidate satisfies the problem's statement.

The code that is contained in this repository can be directly imported into Eclipse and executed as a regular Java application. The "inputFiles" folder contains some examples of Nonograms, which can be used as test cases for the Nonogram solver.
