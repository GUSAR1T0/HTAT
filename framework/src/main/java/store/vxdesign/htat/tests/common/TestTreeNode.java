/*
 * Copyright (c) 2018 Roman Mashenkin <xromash@vxdesign.store>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package store.vxdesign.htat.tests.common;

import io.bretty.console.tree.PrintableTreeNode;
import org.junit.platform.launcher.TestIdentifier;

import java.util.ArrayList;
import java.util.List;

public class TestTreeNode implements PrintableTreeNode {
    private final TestIdentifier test;
    private final List<TestTreeNode> children = new ArrayList<>();

    private TestStatus status;

    public TestTreeNode(TestIdentifier test) {
        this.test = test;
    }

    public void addChild(TestTreeNode node) {
        children.add(node);
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    @Override
    public String name() {
        if (test != null) {
            return (status != null ? "[" + status + "] " : "") + test.getDisplayName();
        } else {
            return "HTAT test tree";
        }
    }

    @Override
    public List<TestTreeNode> children() {
        return children;
    }
}
