//
//  Bookmarks.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 1/16/25.
//

import Foundation
import SwiftData

@Model
class Bookmark {
  var uid: Int64
  var by: String?
  var time: Int64
  var title: String
  var text: String?
  var url: String?
  var score: Int
  var descendants: Int

  init(
    uid: Int64,
    by: String?,
    time: Int64,
    title: String,
    text: String?,
    url: String?,
    score: Int,
    descendants: Int
  ) {
    self.uid = uid
    self.by = by
    self.time = time
    self.title = title
    self.text = text
    self.url = url
    self.score = score
    self.descendants = descendants
  }
}

extension StoryContent {
  func toBookmark() -> Bookmark {
    return Bookmark(
      uid: id,
      by: author,
      time: timestamp,
      title: title,
      text: body,
      url: url,
      score: score,
      descendants: commentCount
    )
  }
}

protocol BookmarksDataStore {
  func fetchBookmarks() -> [Bookmark]
  func addBookmark(_ bookmark: Bookmark)
  func removeBookmark(with id: Int64)
  func containsBookmark(with id: Int64) -> Bool
}

class LiveBookmarksDataStore: BookmarksDataStore {
  private let modelContainer: ModelContainer
  private let modelContext: ModelContext

  @MainActor
  static let shared = LiveBookmarksDataStore()

  @MainActor
  private init() {
    self.modelContainer = try! ModelContainer(for: Bookmark.self)
    self.modelContext = modelContainer.mainContext
  }

  func fetchBookmarks() -> [Bookmark] {
    do {
      return try modelContext.fetch(FetchDescriptor<Bookmark>())
    } catch {
      fatalError(error.localizedDescription)
    }
  }

  func addBookmark(_ bookmark: Bookmark) {
    modelContext.insert(bookmark)
    do {
      try modelContext.save()
    } catch {
      fatalError(error.localizedDescription)
    }
  }

  func removeBookmark(with id: Int64) {
    do {
      try modelContext.delete(
        model: Bookmark.self,
        where: #Predicate { bookmark in bookmark.uid == id}
      )
    } catch {
      fatalError(error.localizedDescription)
    }
  }

  func containsBookmark(with id: Int64) -> Bool {
    do {
      let bookmarks = try modelContext.fetch(FetchDescriptor<Bookmark>(predicate: #Predicate { bookmark in bookmark.uid == id}))
      return !bookmarks.isEmpty
    } catch {
      fatalError(error.localizedDescription)
    }
  }
}
