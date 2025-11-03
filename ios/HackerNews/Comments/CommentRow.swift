//
//  CommentRow.swift
//  Hacker News
//
//  Created by Trevor Elkins on 9/5/23.
//

import Foundation
import SwiftUI
import Common

private let maxIndentationLevel: Int = 5

struct CommentRow: View {
  let state: CommentState
  let likeComment: (CommentState) -> Void
  let flagComment: (CommentState) -> Void
  let toggleComment: () -> Void

  @Environment(Theme.self) private var theme
  @State private var isPressed = false

  var body: some View {
    VStack(alignment: .leading, spacing: 0) {
      // first row
      HStack {
        Group {
          // author
          Text("@\(state.user)")
            .font(theme.commentAuthorFont)
          // time
          HStack(alignment: .center, spacing: 4.0) {
            Image(systemName: "clock")
              .font(.system(size: 12))
            Text(state.age)
              .font(theme.commentMetadataFont)
          }
          .font(.caption)
          // collapse/expand
          Image(systemName: "chevron.up.chevron.down")
            .font(.system(size: 12))
            .rotationEffect(.degrees(state.hidden ? 180 : 0))
          // space between
          Spacer()
          // upvote
          Button(action: {
            likeComment(state)
          }) {
            Image(systemName: "arrow.up")
              .font(.system(size: 12))
              .padding(.horizontal, 8)
              .padding(.vertical, 4)
          }
          .frame(height: 20)
          .background(state.upvoted ? .green.opacity(0.2) : .white.opacity(0.2))
          .foregroundStyle(state.upvoted ? .green : .onBackground)
          .clipShape(Capsule())
          
          Menu {
            Button(String(localized: "comments.action.report")) {
              flagComment(state)
            }
            Button(state.hidden ? String(localized: "comments.action.show") : String(localized: "comments.action.collapse")) {
              toggleComment()
            }
          } label: {
            Image(systemName: "ellipsis")
              .font(.system(size: 12))
              .padding(.horizontal, 8)
              .padding(.vertical, 4)
              .frame(height: 20)
              .background(.white.opacity(0.2))
              .foregroundStyle(.onBackground)
              .clipShape(Capsule())
          }
        }
      }
      .padding(8)
      .background(isPressed ? .surface.opacity(0.85) : .surface)
      .zIndex(1)  // Ensure header stays on top
      .onTapGesture(count: 1) {
        toggleComment()
      }

      // Comment Body
      if !state.hidden {
        VStack(alignment: .leading) {
          Text(state.text.formattedHTML())
            .font(theme.commentTextFont)
            .tint(.accentColor)
            .textSelection(.enabled)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(EdgeInsets(top: -3, leading: 8, bottom: 8, trailing: 8))
        .background(.surface)
        .transition(
          .asymmetric(
            insertion: .move(edge: .top).combined(with: .opacity),
            removal: .move(edge: .top).combined(with: .opacity)
          )
        )
        .clipped()
      }
    }
    .clipShape(RoundedRectangle(cornerRadius: 16.0))
    .padding(
      EdgeInsets(
        top: 0,
        leading: min(
          CGFloat(state.level * 20),
          CGFloat(maxIndentationLevel * 20)
        ),
        bottom: 0,
        trailing: 0
      )
    )
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    PreviewVariants {
      CommentRow(
        state: PreviewHelpers.makeFakeComment(),
        likeComment: { _ in },
        flagComment: { _ in },
        toggleComment: {}
      )
      .environment(Theme())
    }
  }
}

struct CommentViewIndentation_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      ForEach(0..<6) { index in
        CommentRow(
          state: PreviewHelpers.makeFakeComment(level: index),
          likeComment: { _ in },
          flagComment: { _ in },
          toggleComment: {}
        )
        .environment(Theme())
        .previewLayout(.sizeThatFits)
        .previewDisplayName("Indentation \(index)")
      }
    }
  }
}

#Preview("HTML case 1") {
  let text = """
<p></p><pre><code>    &gt; I do think C# is by far the best mainstream language
</code></pre>
C# is a hugely underrated language that I feel like often gets overlooked when teams look to move beyond JS/TS.  The language has a pretty tight syntactic congruency to JS/TS[0], Entity Framework is pretty amazing in terms of DX/perf/maturity, and it seems like we should see more C#/.NET it in the wild than we actually do.<p>My sense is that there are some legitimate reasons to pick something like Kotlin (JVM ecosystem), but a lot of folks that might have worked with C# in passing in the .NET Framework days simply haven't given the ecosystem another look.  It's productive, stable, performant, and secure.</p><p>VS Code support is really good and Rider has a community license available.</p><p>[0] <a href="https://github.com/CharlieDigital/js-ts-csharp">https://github.com/CharlieDigital/js-ts-csharp</a></p>
"""
  CommentRow(
    state: PreviewHelpers.makeFakeComment(level: 0, text: text),
    likeComment: { _ in },
    flagComment: { _ in },
    toggleComment: {}
  )
  .environment(Theme())
}

#Preview("HTML case 2") {
  let text = """
What do you mean that it's not truly cross-platform?<p><a href="https://developers.redhat.com/blog/2016/09/14/pinvoke-in-net-core-rhel" rel="nofollow">https://developers.redhat.com/blog/2016/09/14/pinvoke-in-net...</a></p><p><a href="https://developers.redhat.com/blog/2019/03/25/using-net-pinvoke-for-linux-system-functions" rel="nofollow">https://developers.redhat.com/blog/2019/03/25/using-net-pinv...</a></p><p>&gt; without much technical clarity in what it brings to the table that's lacking in other ecosystems</p><p>What other GC language offers such levels of both high level expressiveness and low level control and also has a big ecosystem?</p>
"""
  CommentRow(
    state: PreviewHelpers.makeFakeComment(level: 0, text: text),
    likeComment: { _ in },
    flagComment: { _ in },
    toggleComment: {}
  )
  .environment(Theme())
}

#Preview("HTML case 3") {
  let text = """
It's much much more complicated than that. Sun refused to add many language features that Microsoft (then a cautious but also genuine user of Java) wanted. Such as refusal to add delegates/closures:<p><a href="https://benhutchison.wordpress.com/2009/02/14/suns-rejection-of-delegates-for-java/" rel="nofollow">https://benhutchison.wordpress.com/2009/02/14/suns-rejection...</a>
<a href="https://stackoverflow.com/questions/1973579/why-doesnt-java-have-method-delegates" rel="nofollow">https://stackoverflow.com/questions/1973579/why-doesnt-java-...</a></p><p>J++, which was Microsoft's Java implementation in the 90's added a few language extensions that were clearly not Sun-approved, but driven by internal engineering feedback at MS. C# having struct and class keywords, allowing you to define your own value types, is clearly a result of that missing in Java, which still in 2025 has no such equivalent yet.</p><p>Also Java's then native code interop solution, JNI, was and still remains complete garbage, and it's flaws were a huge guide for Microsoft when they deveoped .NET and it's native interop equivalent, PI (platform invoke).</p><p>Thankfully, Java now have FFM [foreign function and memory APIs](<a href="https://docs.oracle.com/en/java/javase/21/core/foreign-function-and-memory-api.html" rel="nofollow">https://docs.oracle.com/en/java/javase/21/core/foreign-funct...</a>) APIs (and also JNA which is community driven), which are much better than JNI.</p>
"""
  CommentRow(
    state: PreviewHelpers.makeFakeComment(level: 0, text: text),
    likeComment: { _ in },
    flagComment: { _ in },
    toggleComment: {}
  )
  .environment(Theme())
}

#Preview("HTML case 4") {
  let text = """
Hudson River Trading | Hybrid | Full-time\n<p>We’re a quantitative trading firm based in NYC that trades hundreds of millions of shares each day on over 200 markets worldwide. We use math and technology in everything we do; our talented developers, engineers, and programmers build complex models and systems that allow us to make automated trading decisions on global markets.</p>\n<p>We’re hiring for:</p>\n<p>Senior Systems Engineer | US + London | <a href=\"https://www.hudsonrivertrading.com/careers/?q=senior+systems&amp;gh_src=ca07bf8d1us\" rel=\"nofollow\">https://www.hudsonrivertrading.com/careers/?q=senior+systems...</a></p>\n<p>Data Production Engineer | London | <a href=\"https://www.hudsonrivertrading.com/careers/job/?gh_jid=5196809&amp;req_id=431&amp;gh_src=ca07bf8d1us\" rel=\"nofollow\">https://www.hudsonrivertrading.com/careers/job/?gh_jid=51968...</a></p>\n<p>Design Verification Engineer | US + London | <a href=\"https://www.hudsonrivertrading.com/careers/?q=design+verification&amp;gh_src=ca07bf8d1us\" rel=\"nofollow\">https://www.hudsonrivertrading.com/careers/?q=design+verific...</a></p>\n<p>Middle Office Analyst - Crypto | SNG | <a href=\"https://www.hudsonrivertrading.com/careers/job/?gh_jid=6457676&amp;req_id=R-000384&amp;gh_src=ca07bf8d1us\" rel=\"nofollow\">https://www.hudsonrivertrading.com/careers/job/?gh_jid=64576...</a></p>\n<p>Windows Engineer | SNG | <a href=\"https://www.hudsonrivertrading.com/careers/job/?q=design+verification&amp;gh_src=ca07bf8d1us&amp;gh_jid=6533898&amp;req_id=R-000410&amp;gh_src=ca07bf8d1us\" rel=\"nofollow\">https://www.hudsonrivertrading.com/careers/job/?q=design+ver...</a></p>
"""
  CommentRow(
    state: PreviewHelpers.makeFakeComment(level: 0, text: text),
    likeComment: { _ in },
    flagComment: { _ in },
    toggleComment: {}
  )
  .environment(Theme())
}
