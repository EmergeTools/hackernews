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
  var id: Int64
  var by: String?
  var time: Int64
  var title: String
  var url: String?
  var score: Int
  var descendants: Int

  init(
    id: Int64,
    by: String?,
    time: Int64,
    title: String,
    url: String?,
    score: Int,
    descendants: Int
  ) {
    self.id = id
    self.by = by
    self.time = time
    self.title = title
    self.url = url
    self.score = score
    self.descendants = descendants
  }
}

extension StoryContent {
  func toBookmark() -> Bookmark {
    return Bookmark(
      id: id,
      by: author,
      time: timestamp,
      title: title,
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
        where: #Predicate { bookmark in bookmark.id == id}
      )
    } catch {
      fatalError(error.localizedDescription)
    }
  }
}

struct FakeBookmarkDataStore: BookmarksDataStore {
  func fetchBookmarks() -> [Bookmark] {
    return []
  }
  
  func addBookmark(_ bookmark: Bookmark) {
  }

  func removeBookmark(with id: Int64) {
  }
}
