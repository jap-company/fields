package jap.fields

object FoldUtil {

  /** Combines `results` using `combine` function, returns empty if empty. This has minor optimisations that checks size
    * to handle simple cases.
    */
  @inline def fold[A](results: List[A], empty: A, combine: (A, A) => A) =
    if (results.size == 0) empty
    else if (results.size == 1) results.head
    else if (results.size == 2) combine(results(0), results(1))
    else results.reduce(combine)
}
