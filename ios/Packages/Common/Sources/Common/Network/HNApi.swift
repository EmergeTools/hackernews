//
//  HackerNewsAPI.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation


public class HNApi {
  private let baseUrl = "https://hacker-news.firebaseio.com/v0/"
  private let decoder = JSONDecoder()
  private let session = URLSession.shared

  public init() {}
  
  public func fetchStories(feedType: FeedType) async -> [Int64] {
    NotificationCenter.default.post(name: Notification.Name(rawValue: "EmergeMetricStarted"), object: nil, userInfo: [
      "metric": "FETCH_STORIES"
    ])
    
    let feedUrl = switch feedType {
    case .top:
      "topstories.json"
    case .new:
      "newstories.json"
    case .best:
      "beststories.json"
    case .ask:
      "askstories.json"
    case .show:
      "showstories.json"
    }
    
    let url = URL(string: baseUrl + feedUrl)!
    
    do {
      let (data, response) = try await URLSession.shared.data(from: url)
      if Flags.isEnabled(.networkDebugger) {
        NetworkDebugger.printStats(for: response)
      }
      
      let storyIds = try decoder.decode([Int64].self, from: data)
      
      NotificationCenter.default.post(name: Notification.Name(rawValue: "EmergeMetricEnded"), object: nil, userInfo: [
        "metric": "FETCH_STORIES"
      ])
      return storyIds
    } catch {
      print("Error fetching post IDs: \(error)")
      return []
    }
  }
  
  public func fetchPage(page: Page) async -> [Story] {
    do {
      return try await withThrowingTaskGroup(of: HNItem.self) { taskGroup in
        for id in page.ids {
          taskGroup.addTask {
            let url = URL(string: "https://hacker-news.firebaseio.com/v0/item/\(id).json")!
            let (data, response) = try await URLSession.shared.data(from: url)
            if Flags.isEnabled(.networkDebugger) {
              NetworkDebugger.printStats(for: response)
            }
            let decoder = JSONDecoder()
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
        
        var idToItem = [Int64 : HNItem]()
        for try await result in taskGroup {
          idToItem[result.id] = result
        }
        return page.ids.compactMap { idToItem[$0] as? Story }
      }
    } catch let error {
      print("Error loading page: \(error)")
      return []
    }
  }
  
  public func fetchItems(ids: [Int64]) async -> [HNItem] {
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
              return try decoder.decode(Story.self, from: data)
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

public protocol HNItem: Codable {
  var id: Int64 { get }
  var by: String? { get }
  var time: Int64 { get }
  var type: ItemType { get }
}

public enum ItemType: String, Codable {
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

public struct Story: HNItem, Codable, Hashable {
  public let id: Int64
  public let by: String?
  public let time: Int64
  public let type: ItemType
  public let title: String
  public let text: String?
  public let url: String?
  public let score: Int
  public let descendants: Int
  public let kids: [Int64]?

  public var comments: [Int64] {
    return kids ?? []
  }
  
  public var commentCount: Int {
    return descendants
  }
  
  public var displayableUrl: String? {
    guard let url = makeUrl() else {
      return nil
    }
    return url.host ?? ""
  }
  
  public var displayableDate: String {
    if ProcessInfo.processInfo.environment["EMERGE_IS_RUNNING_FOR_SNAPSHOTS"] == "1" {
      return "10 minutes ago"
    }
    let date = Date(timeIntervalSince1970: TimeInterval(time))
    return date.timeAgoDisplay()
  }
  
  public func makeUrl() -> URL? {
    guard let url = url else {
      return nil
    }
    return URL(string: url)
  }
  
  public init(id: Int64, by: String? = nil, time: Int64, type: ItemType, title: String, text: String? = nil, url: String? = nil, score: Int, descendants: Int, kids: [Int64]?  = nil) {
    self.id = id
    self.by = by
    self.time = time
    self.type = type
    self.title = title
    self.text = text
    self.url = url
    self.score = score
    self.descendants = descendants
    self.kids = kids
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
  
  var displayableDate: String {
    if ProcessInfo.processInfo.environment["EMERGE_IS_RUNNING_FOR_SNAPSHOTS"] == "1" {
      return "10 minutes ago"
    }
    let date = Date(timeIntervalSince1970: TimeInterval(time))
    return date.timeAgoDisplay()
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
