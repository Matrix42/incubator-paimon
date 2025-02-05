/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.flink.sink.cdc;

import org.apache.paimon.flink.sink.ChannelComputer;
import org.apache.paimon.flink.sink.MultiTableCommittable;

import java.util.Objects;

/** {@link ChannelComputer} for {@link MultiTableCommittable}. */
public class MultiTableCommittableChannelComputer
        implements ChannelComputer<MultiTableCommittable> {

    private static final long serialVersionUID = 1L;

    private transient int numChannels;

    @Override
    public void setup(int numChannels) {
        this.numChannels = numChannels;
    }

    @Override
    public int channel(MultiTableCommittable multiTableCommittable) {
        return Math.floorMod(
                Objects.hash(multiTableCommittable.getDatabase(), multiTableCommittable.getTable()),
                numChannels);
    }

    @Override
    public String toString() {
        return "shuffle by table";
    }
}
