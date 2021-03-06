/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.kave.histories.test;

import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.*;

import java.util.*;
import java.util.stream.Stream;

import cc.kave.histories.HistoryDetector;
import cc.kave.histories.model.OUHistory;
import org.junit.Test;

import cc.kave.histories.model.OUSnapshot;

public class HistoryDetectorTest {

    @Test
    public void createsNoHistoryIfNoSnapshots() throws Exception {
        HistoryDetector uut = new HistoryDetector();

        Set<OUHistory> actuals = uut.getDetectedHistories();

        assertThat(actuals, is(empty()));
    }

    @Test
    public void createsNoHistoryFromOneSnapshot() throws Exception {
        HistoryDetector uut = new HistoryDetector();

        uut.process(new OUSnapshot());
        Set<OUHistory> actuals = uut.getDetectedHistories();

        assertThat(actuals, is(empty()));
    }

    @Test
    public void createsHistoryFromTwoSnapshots() {
        OUSnapshot s1 = new OUSnapshot();
        OUSnapshot s2 = new OUSnapshot();

        HistoryDetector uut = new HistoryDetector();
        uut.process(s1);
        uut.process(s2);
        Set<OUHistory> actuals = uut.getDetectedHistories();

        OUHistory expected = history(s1, s2);
        assertThat(actuals, contains(expected));
    }

    @Test
    public void createsHistoryFromAllRelatedSnapshots() throws Exception {
        OUSnapshot s1 = new OUSnapshot();
        OUSnapshot s2 = new OUSnapshot();
        OUSnapshot s3 = new OUSnapshot();

        HistoryDetector uut = new HistoryDetector();
        uut.process(s1);
        uut.process(s2);
        uut.process(s3);
        Set<OUHistory> actuals = uut.getDetectedHistories();

        OUHistory expected = history(s1, s2, s3);
        assertThat(actuals, contains(expected));
    }

    @Test
    public void separatesSnapshotsByWorkPeriod() throws Exception {
        OUSnapshot s1 = new OUSnapshot("1", null, null, null, null, false);
        OUSnapshot s2 = new OUSnapshot("2", null, null, null, null, false);

        assertAssignmentToSeparateHistories(s1, s2);
    }

    @Test
    public void separatesSnapshotsByDay() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 7, 14, 10, 23, 42);
        Date t1 = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date t2 = calendar.getTime();

        OUSnapshot s1 = new OUSnapshot("1", t1, "M", null, null, false);
        OUSnapshot s2 = new OUSnapshot("1", t2, "M", null, null, false);

        assertAssignmentToSeparateHistories(s1, s2);
    }

    @Test
    public void separatesSnapshotsByEnclosingMethod() throws Exception {
        OUSnapshot s1 = new OUSnapshot("a", null, "M1", null, null, false);
        OUSnapshot s2 = new OUSnapshot("a", null, "M2", null, null, false);

        assertAssignmentToSeparateHistories(s1, s2);
    }

    @Test
    public void separatesSnapshotsByTargetType() throws Exception {
        OUSnapshot s1 = new OUSnapshot("a", null, "M", "T1", null, false);
        OUSnapshot s2 = new OUSnapshot("a", null, "M", "T2", null, false);

        assertAssignmentToSeparateHistories(s1, s2);
    }

    private OUHistory history(OUSnapshot... snapshots) {
        OUHistory history = new OUHistory();
        Stream.of(snapshots).forEach(snapshot -> history.addSnapshot(snapshot));
        return history;
    }

    private void assertAssignmentToSeparateHistories(OUSnapshot s1, OUSnapshot s2) {
        HistoryDetector uut = new HistoryDetector();
        uut.process(s1);
        uut.process(s2);
        Set<OUHistory> actuals = uut.getDetectedHistories();

        assertThat(actuals, is(empty()));
    }

}
