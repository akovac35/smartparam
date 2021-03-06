/*
 * Copyright 2014 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.engine.report;

import org.smartparam.engine.matchers.type.RangeBoundary;
import org.smartparam.engine.util.Objects;

/**
 *
 * @author Adam Dubiel
 */
public class ContinuousSpaceSegment<C extends Comparable<? super C>, V> implements Comparable<ContinuousSpaceSegment<C, V>> {

    private final RangeBoundary<C> segmentStart;

    private final RangeBoundary<C> segmentEnd;

    private final V value;

    public ContinuousSpaceSegment(C from, C to, V value) {
        this.segmentStart = new RangeBoundary<C>(from);
        this.segmentEnd = new RangeBoundary<C>(to);
        this.value = value;
    }

    public ContinuousSpaceSegment(RangeBoundary<C> from, RangeBoundary<C> to, V value) {
        this.segmentStart = from;
        this.segmentEnd = to;
        this.value = value;
    }

    public ContinuousSpaceSegment(ContinuousSpaceSegment<C, V> other, V value) {
        this.segmentStart = other.segmentStart();
        this.segmentEnd = other.segmentEnd();
        this.value = value;
    }

    public boolean contains(RangeBoundary<C> point) {
        return segmentStart.compareTo(point) < 0 && segmentEnd.compareTo(point) > 0;
    }

    public boolean contains(C point) {
        RangeBoundary<C> boundedPoint = new RangeBoundary<C>(point);
        return segmentStart.compareTo(boundedPoint) < 0 && segmentEnd.compareTo(boundedPoint) > 0;
    }

    IntersectionType intersects(C from, C to) {
        return intersects(new RangeBoundary<C>(from), new RangeBoundary<C>(to));
    }

    IntersectionType intersects(RangeBoundary<C> from, RangeBoundary<C> to) {
        boolean containsFrom = contains(from);
        boolean containsTo = contains(to);

        if (from.compareTo(segmentStart) == 0 && to.compareTo(segmentEnd()) == 0) {
            return IntersectionType.IDENTICAL;
        } else if (containsFrom && containsTo) {
            return IntersectionType.CONTAINS;
        } else if (containsTo) {
            return IntersectionType.BEFORE;
        } else if (containsFrom) {
            return IntersectionType.AFTER;
        }
        else if(from.compareTo(segmentStart) <= 0 && to.compareTo(segmentEnd()) >= 0) {
            return IntersectionType.CONTAINED;
        }
        return IntersectionType.NONE;
    }

    public RangeBoundary<C> segmentStart() {
        return segmentStart;
    }

    public RangeBoundary<C> segmentEnd() {
        return segmentEnd;
    }

    public V value() {
        return value;
    }

    @Override
    public int compareTo(ContinuousSpaceSegment<C, V> other) {
        int fromComparison = segmentStart.compareTo(other.segmentStart);
        if (fromComparison == 0) {
            return segmentEnd.compareTo(other.segmentEnd);
        }
        return fromComparison;
    }

    @Override
    public int hashCode() {
        return Objects.hash(segmentStart, segmentEnd, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!Objects.classEquals(this, obj)) {
            return false;
        }
        final ContinuousSpaceSegment<?, ?> other = (ContinuousSpaceSegment<?, ?>) obj;
        return Objects.equals(segmentStart, other.segmentStart)
                && Objects.equals(segmentEnd, other.segmentEnd)
                && Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return "[SpaceSegment from: " + segmentStart + " to: " + segmentEnd + "]";
    }



    static enum IntersectionType {

        NONE, IDENTICAL, BEFORE, CONTAINS, CONTAINED, AFTER

    }
}
