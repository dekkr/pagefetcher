package nl.dekkr.pagefetcher.persistence

import net.fwbrasil.activate.entity.Entity

trait StoredPage extends Entity {
  val uri: String

  def invariantNameMustNotBeEmpty = invariant {
    uri != null && uri.nonEmpty
  }
}
