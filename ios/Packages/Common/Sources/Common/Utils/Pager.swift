//
//  Pager.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/6/24.
//

import Foundation

public struct Page {
  public var ids: ArraySlice<Int64>
  
  public init(ids: ArraySlice<Int64>) {
    self.ids = ids
  }
}

public struct Pager {
  private var ids: [Int64] = []
  private let pageSize: Int = 20
  
  public init() {
  }
  
  public mutating func setIds(_ ids: [Int64]) {
    self.ids = ids
  }
  
  public mutating func nextPage() -> Page {
    // first see if we can slice of the next page size
    let pageCount = min(ids.count, pageSize)
    
    // remove items from array
    let taken = ids.prefix(pageCount)
    ids.removeFirst(pageCount)
    
    return Page(ids: taken)
  }
  
  public func hasNextPage() -> Bool {
    return !ids.isEmpty
  }
}
