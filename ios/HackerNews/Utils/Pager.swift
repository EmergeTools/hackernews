//
//  Pager.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/6/24.
//

import Foundation

struct Page {
  var ids: ArraySlice<Int64>
}

struct Pager {
  private var ids: [Int64] = []
  private let pageSize: Int = 20
  
  mutating func setIds(_ ids: [Int64]) {
    self.ids = ids
  }
  
  mutating func nextPage() -> Page {
    // first see if we can slice of the next page size
    let pageCount = min(ids.count, pageSize)
    
    // remove items from array
    let taken = ids.prefix(pageCount)
    ids.removeFirst(pageCount)
    
    return Page(ids: taken)
  }
  
  func hasNextPage() -> Bool {
    return !ids.isEmpty
  }
}
