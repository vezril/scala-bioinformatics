package bio.algorithms.analysis

import bio.domain.analysis.GcContent
import bio.parsing.FastaRecord

object HighestGc {

  /** Finds the [[FastaRecord]] with the highest GC content, paired with that content value.
    *
    * Returns `None` for an empty input list. Ties are resolved deterministically to the
    * first record encountered (strictly-greater comparison preserves the incumbent).
    *
    * GC content is computed for each record exactly once.
    */
  def find(records: List[FastaRecord]): Option[(FastaRecord, GcContent)] =
    records.foldLeft(Option.empty[(FastaRecord, GcContent)]) { (best, record) =>
      val current = (record, GcContent.of(record.dna))
      best match {
        case None                                            => Some(current)
        case Some((_, incumbent)) if current._2.value > incumbent.value => Some(current)
        case _                                               => best
      }
    }
}
