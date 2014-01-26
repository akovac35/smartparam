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

import java.util.List;

/**
 *
 * @author Adam Dubiel
 */
public interface ReportingTreeNode<V> {

    ReportingTree<V> tree();

    ReportingTreeNode<V> addChild(String levelValue);

    void insertPath(ReportingTreePath<V> path);

    ReportingTreeNode<V> cloneBranch();

    ReportingTreeNode<V> parent();

    int depth();

    boolean leaf();

    void harvestLeavesValues(List<V> leafBucket);

    void printNode(StringBuilder builder);

    String levelPath();
}
