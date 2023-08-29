package com.emergetools.hackernews

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.emergetools.snapshots.EmergeSnapshots
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivitySnapshotTest {

  @get:Rule
  val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

  @get:Rule
  val snapshots = EmergeSnapshots()

  @Test
  fun mainActivity() {
    val scenario = activityScenarioRule.scenario
    scenario.onActivity {
      snapshots.take("Main Activity", it)
    }
  }
}
