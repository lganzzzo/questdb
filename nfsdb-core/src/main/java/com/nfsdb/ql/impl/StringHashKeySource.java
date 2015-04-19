/*
 * Copyright (c) 2014-2015. Vlad Ilyushchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nfsdb.ql.impl;

import com.nfsdb.ql.KeyCursor;
import com.nfsdb.ql.KeySource;
import com.nfsdb.ql.PartitionSlice;
import com.nfsdb.utils.Hash;

import java.util.List;

public class StringHashKeySource implements KeySource, KeyCursor {
    private final String column;
    private final List<String> values;
    private int bucketCount = -1;
    private int valueIndex;

    public StringHashKeySource(String column, List<String> values) {
        this.column = column;
        this.values = values;
    }

    @Override
    public KeyCursor cursor(PartitionSlice slice) {
        if (bucketCount == -1) {
            bucketCount = slice.partition.getJournal().getMetadata().getColumn(column).distinctCountHint;
        }
        this.valueIndex = 0;
        return this;
    }

    @Override
    public boolean hasNext() {
        return valueIndex < values.size();
    }

    @Override
    public int next() {
        return Hash.boundedHash(values.get(valueIndex++), bucketCount);
    }

    @Override
    public void reset() {
        bucketCount = -1;
    }

    @Override
    public int size() {
        return values.size();
    }
}