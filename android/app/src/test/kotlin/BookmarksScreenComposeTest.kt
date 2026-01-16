import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.emergetools.hackernews.features.bookmarks.BookmarksScreen
import com.emergetools.hackernews.features.bookmarks.BookmarksState
import com.emergetools.hackernews.features.stories.StoryItem
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.time.Instant

//@GraphicsMode(GraphicsMode.Mode.NATIVE)
//@RunWith(RobolectricTestRunner::class)
class BookmarksScreenComposeTest {

  @get:Rule
  val composeRule = createComposeRule()

//  @Test
  fun roborazziTest() {
    composeRule.setContent {
      HackerNewsTheme {
        BookmarksScreen(
          state = BookmarksState(
            bookmarks = listOf(
              StoryItem.Content(
                id = 1L,
                title = "Show HN: A new Android client",
                author = "heyrikin",
                score = 10,
                commentCount = 45,
                epochTimestamp = Instant.now().minusSeconds(60 * 60 * 3).epochSecond,
                bookmarked = true,
                url = ""
              ),
              StoryItem.Content(
                id = 2L,
                title = "Can we stop the decline of monarch butterflies and other pollinators?",
                author = "rbro112",
                score = 40,
                commentCount = 23,
                epochTimestamp = Instant.now().minusSeconds(60 * 60 * 2).epochSecond,
                bookmarked = true,
                url = ""
              ),
              StoryItem.Content(
                id = 3L,
                title = "Andy Warhol's lost Amiga art found",
                author = "telkins",
                score = 332,
                commentCount = 103,
                epochTimestamp = Instant.now().minusSeconds(60 * 60 * 7).epochSecond,
                bookmarked = true,
                url = ""
              ),
            )
          ),
          actions = {},
          navigator = {}
        )
      }
    }

    composeRule
      .onRoot()
      .captureRoboImage()
  }
}
