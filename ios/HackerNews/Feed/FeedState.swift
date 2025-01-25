struct FeedState {
  let feeds = FeedType.allCases
  
  var selectedFeed: FeedType
  private var storiesByFeed: [FeedType: [StoryState]] = [:]

  init(
    selectedFeed: FeedType = .top,
    stories: [StoryState] = []
  ) {
    self.selectedFeed = selectedFeed
    self.storiesByFeed[selectedFeed] = stories
  }

  func storiesForFeed(_ feedType: FeedType) -> [StoryState] {
    return storiesByFeed[feedType] ?? []
  }

  mutating func setStories(_ stories: [StoryState], for feedType: FeedType) {
    storiesByFeed[feedType] = stories
  }
}
