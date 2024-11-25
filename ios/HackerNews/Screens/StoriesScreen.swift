//
//  StoriesScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 11/22/24.
//

import Foundation
import SwiftUI

struct StoriesScreen: View {
  @ObservedObject var model: AppViewModel
  
  var body: some View {
    switch model.storiesState {
    case .notStarted, .loading:
      ProgressView()
    case .loaded(let stories):
      List(stories, id: \.id) { story in
        StoryRowV2(model: model, story: story)
          .background(
            NavigationLink(
              value: navigationForStory(story: story),
              label: {}
            )
            .opacity(0.0)
          )
          .listRowBackground(Color.clear)
      }
      .listStyle(.plain)
    }
  }
  
  private func navigationForStory(story: Story) -> AppViewModel.AppNavigation {
    if let url = story.makeUrl() {
      return AppViewModel.AppNavigation.webLink(url: url, title: story.title)
    } else {
      return AppViewModel.AppNavigation.storyComments(story: story)
    }
  }
}


#Preview {
  StoriesScreen(model: AppViewModel())
}
