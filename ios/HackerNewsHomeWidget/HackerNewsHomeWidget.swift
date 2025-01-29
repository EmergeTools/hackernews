//
//  HackerNewsHomeWidget.swift
//  HackerNewsHomeWidget
//
//  Created by Trevor Elkins on 1/28/25.
//

import Foundation
import SwiftUI
import WidgetKit

struct HackerNewsProvider: TimelineProvider {
  let api = HNApi()

  func placeholder(in context: Context) -> StoryTimelineEntry {
    StoryTimelineEntry(date: Date(), stories: [])
  }

  func getSnapshot(
    in context: Context, completion: @escaping (StoryTimelineEntry) -> Void
  ) {
    let entry = StoryTimelineEntry(date: Date(), stories: [])
    completion(entry)
  }

  func getTimeline(
    in context: Context, completion: @escaping (Timeline<Entry>) -> Void
  ) {
    Task {
      let storyIds = await api.fetchStories(feedType: .top)
      let page = Page(ids: storyIds.prefix(10))
      let stories = await api.fetchPage(page: page)

      let entry = StoryTimelineEntry(
        date: .now,
        stories: stories
      )

      // Update every 15 minutes
      let nextUpdate = Calendar.current.date(
        byAdding: .minute, value: 15, to: .now)!
      let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))

      completion(timeline)
    }
  }
}

struct StoryTimelineEntry: TimelineEntry {
  let date: Date
  let stories: [Story]
}

struct HackerNewsHomeWidgetEntryView: View {
  @Environment(\.widgetFamily) private var family
  @Environment(Theme.self) private var theme

  let entry: HackerNewsProvider.Entry

  var body: some View {
    VStack(alignment: .leading, spacing: 8) {
      Text("Top Stories")
        .font(theme.titleFont)
        .foregroundColor(.hnOrange)

      switch family {
      case .systemSmall:
        ForEach(entry.stories.prefix(2), id: \.id) { story in
          smallStoryRow(story)
          if story.id != entry.stories.prefix(2).last?.id {
            Divider()
          }
        }

      case .systemMedium:
        ForEach(entry.stories.prefix(2), id: \.id) { story in
          mediumStoryRow(story)
          if story.id != entry.stories.prefix(2).last?.id {
            Divider()
          }
        }

      case .systemLarge, .systemExtraLarge:
        ForEach(entry.stories.prefix(5), id: \.id) { story in
          storyRow(story)
          if story.id != entry.stories.prefix(5).last?.id {
            Divider()
          }
        }

      case .accessoryCircular, .accessoryRectangular, .accessoryInline:
        fatalError("Unsupported family \(family)")

      @unknown default:
        fatalError("Unsupported family \(family)")
      }
    }
  }

  private func smallStoryRow(_ story: Story) -> some View {
    VStack(alignment: .leading, spacing: 4) {
      Link(destination: URL(string: "hackernews://story/\(story.id)")!) {
        VStack(alignment: .leading, spacing: 4) {
          Text(story.title)
            .font(theme.titleFont)
            .lineLimit(2)
            .foregroundColor(.primary)

          HStack(spacing: 12) {
            HStack(spacing: 4) {
              Image(systemName: "arrow.up")
                .font(theme.userSansFont(size: 10))
                .foregroundColor(.green)
              Text("\(story.score)")
                .font(theme.userSansFont(size: 10, weight: .medium))
            }

            HStack(spacing: 4) {
              Image(systemName: "message.fill")
                .font(.system(size: 10))
              Text("\(story.descendants)")
                .font(.system(size: 10, weight: .medium))
            }
            .foregroundStyle(.blue)
          }
        }
      }
    }
    .padding(.horizontal, 8)
    .padding(.vertical, 4)
  }

  private func storyRow(_ story: Story) -> some View {
    VStack(alignment: .leading, spacing: 6) {
      Link(destination: URL(string: "hackernews://story/\(story.id)")!) {
        VStack(alignment: .leading, spacing: 6) {
          Text(story.title)
            .font(theme.titleFont)
            .lineLimit(2)
            .multilineTextAlignment(.leading)
            .foregroundColor(.primary)

          HStack(spacing: 16) {
            HStack(spacing: 4) {
              Image(systemName: "arrow.up")
                .font(theme.userSansFont(size: 12))
                .foregroundColor(.green)
              Text("\(story.score)")
                .font(theme.userSansFont(size: 12, weight: .medium))
            }

            HStack(spacing: 4) {
              Image(systemName: "clock")
                .font(.system(size: 12))
                .foregroundColor(.purple)
              Text(
                Date(timeIntervalSince1970: TimeInterval(story.time))
                  .timeAgoDisplay()
              )
              .font(.system(size: 12, weight: .medium))
            }

            Spacer()

            HStack(spacing: 4) {
              Image(systemName: "message.fill")
                .font(.system(size: 12))
              Text("\(story.descendants)")
                .font(.system(size: 12, weight: .medium))
            }
            .foregroundStyle(.blue)
          }
        }
      }
    }
    .padding(.vertical, 4)
  }

  private func mediumStoryRow(_ story: Story) -> some View {
    VStack(alignment: .leading, spacing: 6) {
      Link(destination: URL(string: "hackernews://story/\(story.id)")!) {
        VStack(alignment: .leading, spacing: 6) {
          Text(story.title)
            .font(theme.titleFont)
            .lineLimit(2)
            .multilineTextAlignment(.leading)
            .foregroundColor(.primary)

          HStack(spacing: 16) {
            HStack(spacing: 4) {
              Image(systemName: "arrow.up")
                .font(theme.userSansFont(size: 12))
                .foregroundColor(.green)
              Text("\(story.score)")
                .font(theme.userSansFont(size: 12, weight: .medium))
            }

            HStack(spacing: 4) {
              Image(systemName: "clock")
                .font(.system(size: 12))
                .foregroundColor(.purple)
              Text(
                Date(timeIntervalSince1970: TimeInterval(story.time))
                  .timeAgoDisplay()
              )
              .font(.system(size: 12, weight: .medium))
            }

            Spacer()

            HStack(spacing: 4) {
              Image(systemName: "message.fill")
                .font(.system(size: 12))
              Text("\(story.descendants)")
                .font(.system(size: 12, weight: .medium))
            }
            .foregroundStyle(.blue)
          }
        }
      }
    }
    .padding(.vertical, 4)
  }
}

struct HackerNewsHomeWidget: Widget {
  @State var theme = Theme(context: .widget)
  let kind: String = "HackerNewsHomeWidget"

  var body: some WidgetConfiguration {
    StaticConfiguration(kind: kind, provider: HackerNewsProvider()) { entry in
      if #available(iOS 17.0, *) {
        HackerNewsHomeWidgetEntryView(entry: entry)
          .containerBackground(.fill.tertiary, for: .widget)
          .environment(theme)
      } else {
        HackerNewsHomeWidgetEntryView(entry: entry)
          .padding()
          .background()
          .environment(theme)
      }
    }
    .configurationDisplayName("Hacker News")
    .description("Top stories from Hacker News")
    .supportedFamilies([
      .systemSmall,
      .systemMedium,
      .systemLarge,
      .systemExtraLarge,
    ])
  }
}

#Preview(as: .systemSmall) {
  HackerNewsHomeWidget()
} timeline: {
  StoryTimelineEntry(date: .now, stories: [])
  StoryTimelineEntry(date: .now, stories: [])
}
