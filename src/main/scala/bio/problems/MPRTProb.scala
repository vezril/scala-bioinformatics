package bio.problems

import bio.algorithms.protein.MotifSearch
import bio.domain.protein.{MotifLocations, ProteinMotif}
import cats.effect.IO

import java.io.InputStream
import java.net.{HttpURLConnection, URI}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.util.Using

/** Rosalind MPRT ("Finding a Protein Motif") runner.
  *
  * Reads UniProt access IDs (one per line) from `mprt_data.txt`, fetches each
  * protein's sequence from the UniProt REST endpoint (FASTA) over HTTP, finds the
  * N-glycosylation motif `N{P}[ST]{P}`, and prints — for each protein that has at
  * least one match — its ID and the 1-based match locations. Network/parse
  * failures print a message rather than throwing.
  */
object MPRTProb {

  private val DataPath =
    "/Users/cference/Code/scala-bioinformatics/src/main/scala/resources/mprt_data.txt"

  /** The N-glycosylation motif; parse is total here (constant input). */
  private val NGlycosylation: ProteinMotif =
    ProteinMotif.parse("N{P}[ST]{P}").getOrElse(throw new IllegalStateException("bad motif"))

  def solve(): IO[Unit] =
    for {
      raw <- IO.blocking(
        new String(Files.readAllBytes(Paths.get(DataPath)), StandardCharsets.UTF_8)
      )
      ids = raw.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
      _ <- ids.traversePrint
    } yield ()

  private implicit class IdsOps(ids: Vector[String]) {
    def traversePrint: IO[Unit] =
      ids.foldLeft(IO.unit) { (acc, id) =>
        acc.flatMap(_ => printForId(id))
      }
  }

  private def printForId(id: String): IO[Unit] =
    fetchSequence(id).attempt.flatMap {
      case Left(err) => IO.println(s"fetch failed for $id: $err")
      case Right(seq) =>
        MotifSearch.findLocations(NGlycosylation, seq) match {
          case positions if positions.nonEmpty =>
            IO.println(MotifLocations(id, positions).format)
          case _ => IO.unit
        }
    }

  /** Fetch the protein sequence for a UniProt ID as a residue string.
    *
    * The ID may be a primary accession, a secondary accession, or an entry name
    * (`ACCESSION_ENTRY_ORG`). Entry names are reduced to the accession; secondary
    * accessions are resolved by the REST endpoint via an HTTP redirect to the
    * primary entry (followed manually in [[httpGet]]).
    */
  private def fetchSequence(id: String): IO[String] = IO.blocking {
    val accession = id.takeWhile(_ != '_')
    val url       = s"https://rest.uniprot.org/uniprotkb/$accession.fasta"
    val fasta     = httpGet(url, redirectsLeft = 5)
    fasta.linesIterator.filterNot(_.startsWith(">")).map(_.trim).mkString
  }

  /** GET `url`, manually following up to `redirectsLeft` redirects.
    *
    * `HttpURLConnection`'s built-in follower ignores `303 See Other` and refuses
    * cross-protocol (http↔https) hops, both of which occur when a *secondary*
    * UniProt accession redirects to its primary entry — so redirects are followed
    * by hand here, resolving relative `Location` headers against the request URL.
    */
  private def httpGet(url: String, redirectsLeft: Int): String = {
    val conn = URI.create(url).toURL.openConnection().asInstanceOf[HttpURLConnection]
    conn.setInstanceFollowRedirects(false)
    conn.setRequestMethod("GET")
    conn.setConnectTimeout(15000)
    conn.setReadTimeout(15000)

    val code = conn.getResponseCode
    if (code >= 300 && code < 400 && redirectsLeft > 0) {
      val location = Option(conn.getHeaderField("Location"))
      conn.disconnect()
      location match {
        case Some(loc) => httpGet(URI.create(url).resolve(loc).toString, redirectsLeft - 1)
        case None      => throw new RuntimeException(s"redirect without Location from $url")
      }
    } else if (code >= 200 && code < 300) {
      try {
        val stream: InputStream = conn.getInputStream
        Using.resource(Source.fromInputStream(stream, StandardCharsets.UTF_8.name))(_.mkString)
      } finally conn.disconnect()
    } else {
      conn.disconnect()
      throw new RuntimeException(s"HTTP $code for $url")
    }
  }
}
