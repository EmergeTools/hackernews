//
//  HackerNewsAPI.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation

class HNApi {
  
  let baseUrl = "https://hacker-news.firebaseio.com/v0/"
  let decoder = JSONDecoder()
  
  init() {}
  
  func fetchNewStories() async -> [Story] {
    // make the request for ids
    let url = URL(string: baseUrl + "newstories.json")!
    
    do {
      let (data, response) = try await URLSession.shared.data(from: url)
      if Flags.isEnabled(.networkDebugger) {
        NetworkDebugger.printStats(for: response)
      }
      let storyIds = try decoder.decode([Int64].self, from: data)
      let items = await fetchItems(ids: Array(storyIds.prefix(20)))
      return items.compactMap { $0 as? Story }
    } catch {
      print("Error fetch post IDs: \(error)")
      return []
    }
  }
  
  func fetchTopStories() async -> [Story] {
    NotificationCenter.default.post(name: Notification.Name(rawValue: "EmergeMetricStarted"), object: nil, userInfo: [
      "metric": "FETCH_STORIES"
    ])
    let url = URL(string: baseUrl + "topstories.json")!
    
    do {
      let (data, response) = try await URLSession.shared.data(from: url)
      if Flags.isEnabled(.networkDebugger) {
        NetworkDebugger.printStats(for: response)
      }
      let storyIds = try decoder.decode([Int64].self, from: data)
      let items = await fetchItems(ids: Array(storyIds.prefix(20)))

      NotificationCenter.default.post(name: Notification.Name(rawValue: "EmergeMetricEnded"), object: nil, userInfo: [
        "metric": "FETCH_STORIES"
      ])
      return items.compactMap { $0 as? Story }
    } catch {
      print("Error fetching post IDs: \(error)")
      return []
    }
  }
  
  func fetchItems(ids: [Int64]) async -> [HNItem] {
    do {
      return try await withThrowingTaskGroup(of: HNItem.self) { taskGroup in
        for id in ids {
          taskGroup.addTask {
            let url = URL(string: "https://hacker-news.firebaseio.com/v0/item/\(id).json")!
            let (data, response) = try await URLSession.shared.data(from: url)
            if Flags.isEnabled(.networkDebugger) {
              NetworkDebugger.printStats(for: response)
            }
            let decoder = JSONDecoder()
//            print("Received response: \(response)")
//            if let str = String(data: data, encoding: .utf8) {
//              print("Data: \(str)")
//            } else {
//              print("No readable data received in response")
//            }
            let baseItem = try decoder.decode(BaseItem.self, from: data)
            
            switch baseItem.type {
            case .story:
              return try decoder.decode(Story.self, from: data)
            case .comment:
              return try decoder.decode(Comment.self, from: data)
            case .job:
              return try decoder.decode(Job.self, from: data)
            case .poll:
              return try decoder.decode(Poll.self, from: data)
            case .pollopt:
              return try decoder.decode(Pollopt.self, from: data)
            }
          }
        }
        
        var items = [Int64 : HNItem]()
        for try await result in taskGroup {
          items[result.id] = result
        }
        return ids.compactMap { items[$0] }
      }
    } catch let error {
      print("Error fetching item details \(error)")
      return []
    }
  }
}

protocol HNItem: Codable {
  var id: Int64 { get }
  var by: String? { get }
  var time: Int64 { get }
  var type: ItemType { get }
}

enum ItemType: String, Codable {
  case story, comment, job, poll, pollopt
}

struct BaseItem: HNItem {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let deleted: Bool?
  let dead: Bool?
}

struct Story: HNItem, Codable, Hashable {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let title: String
  let text: String?
  let url: String?
  let score: Int
  let descendants: Int
  let kids: [Int64]?
  
  var comments: [Int64] {
    return kids ?? []
  }
  
  var commentCount: Int {
    return descendants
  }
  
  var displayableUrl: String? {
    guard let url = makeUrl() else {
      return nil
    }
    return url.host ?? ""
  }
  
  var displayableDate: String {
    if ProcessInfo.processInfo.environment["EMERGE_IS_RUNNING_FOR_SNAPSHOTS"] == "1" {
      return "10 minutes ago"
    }
    let date = Date(timeIntervalSince1970: TimeInterval(time))
    return date.timeAgoDisplay()
  }
  
  func makeUrl() -> URL? {
    guard let url = url else {
      return nil
    }
    return URL(string: url)
  }
}

struct Comment: HNItem, Identifiable {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let text: String?
  let parent: Int?
  let kids: [Int64]?
  
  var replies: [Int64]? {
    kids
  }
}

struct Job: HNItem {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let title: String
  let text: String?
}

struct Poll: HNItem {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let title: String
  let score: Int
  let descendants: Int
  let comments: [Int64]
  let pollopts: [Int64]
}

struct Pollopt: HNItem {
  let id: Int64
  let by: String?
  let time: Int64
  let type: ItemType
  let poll: Int64
  let score: Int
  let text: String?
}
