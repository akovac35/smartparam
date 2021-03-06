/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
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
package org.smartparam.engine.core.index;

import org.smartparam.engine.core.matcher.Matcher;
import org.smartparam.engine.core.type.Type;
import org.smartparam.engine.util.Formatter;

/**
 * Parameter index - tree structure for efficient parameter lookup.
 *
 * @param <T> leaf type
 *
 * @author Przemek Hertel
 * @since 1.0.0
 */
public class LevelIndex<T> {

    /**
     * Number of levels in index tree (depth).
     */
    private int levelCount;

    /**
     * Root of index tree.
     */
    private LevelNode<T> root;

    /**
     * Matchers for each level. If matchers[i] == null, use default matcher.
     * matchers[i] contains matcher for levels[i+1] (root has zero index).
     */
    private Matcher[] matchers;

    /**
     * Types of values held at each level.
     */
    private Type<?>[] types;

    /**
     * Create new empty index with types and matchers for each level.
     *
     * @param levelCount number of levels (depth)
     * @param types
     * @param matchers
     */
    public LevelIndex(int levelCount, Type<?>[] types, Matcher... matchers) {
        this.levelCount = levelCount;
        this.types = new Type<?>[levelCount];
        this.matchers = new Matcher[levelCount];

        if (types != null) {
            System.arraycopy(types, 0, this.types, 0, types.length);
        }

        if (matchers != null) {
            System.arraycopy(matchers, 0, this.matchers, 0, matchers.length);
        }

        this.root = new LevelNode<T>(this);
    }

    /**
     * Create new empty index with default matchers and types.
     *
     * @param levelCount depth
     */
    public LevelIndex(int levelCount) {
        this(levelCount, null);
    }

    /**
     * Add pattern along with returned value to index tree.
     *
     * @param levelValues
     * @param leaf
     */
    public void add(String[] levelValues, T leaf) {
        root.add(levelValues, leaf, 0);
    }

    /**
     * Pretty printing whole index tree.
     */
    public String printTree() {
        StringBuilder builder = new StringBuilder(Formatter.INITIAL_STR_LEN_256);
        root.printNode(builder, 0);
        return builder.toString();
    }

    Matcher[] getMatchers() {
        return matchers;
    }

    Type<?>[] getTypes() {
        return types;
    }

    /**
     * Return matcher for given level index.
     */
    public Matcher getMatcher(int depth) {
        return matchers[depth];
    }

    /**
     * Return type for given level index.
     */
    public Type<?> getType(int depth) {
        return types[depth];
    }

    /**
     * Return depth of tree.
     */
    public int getLevelCount() {
        return levelCount;
    }

    public LevelNode<T> getRoot() {
        return root;
    }
}
