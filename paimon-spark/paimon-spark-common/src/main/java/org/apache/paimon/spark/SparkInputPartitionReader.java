/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.spark;

import org.apache.paimon.disk.IOManager;
import org.apache.paimon.reader.RecordReaderIterator;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;

import java.io.IOException;

/** A spark 3 {@link PartitionReader} for paimon, created by {@link PartitionReaderFactory}. */
public class SparkInputPartitionReader implements PartitionReader<InternalRow> {

    private final IOManager ioManager;
    private final RecordReaderIterator<org.apache.paimon.data.InternalRow> iterator;
    private final SparkInternalRow row;

    public SparkInputPartitionReader(
            IOManager ioManager,
            RecordReaderIterator<org.apache.paimon.data.InternalRow> iterator,
            SparkInternalRow row) {
        this.ioManager = ioManager;
        this.iterator = iterator;
        this.row = row;
    }

    @Override
    public boolean next() {
        if (iterator.hasNext()) {
            row.replace(iterator.next());
            return true;
        }
        return false;
    }

    @Override
    public InternalRow get() {
        return row;
    }

    @Override
    public void close() throws IOException {
        try {
            iterator.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
        try {
            ioManager.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
