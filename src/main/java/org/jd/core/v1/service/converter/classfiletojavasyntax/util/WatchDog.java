/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.util;

import org.jd.core.v1.service.converter.classfiletojavasyntax.model.cfg.BasicBlock;

import java.util.HashSet;
import java.util.Set;

/**
 * Maintain a set of parent-child blocks
 */
public class WatchDog {
    protected Set<Link> links = new HashSet<>();

    public void clear() {
        links.clear();
    }

    /**
     * Add a new link with given parent-child blocks, and throws an Exception if
     * given link is already present
     * 
     * @param parent
     * @param child
     * @throws RuntimeException if links already contains given link
     */
    public void check(BasicBlock parent, BasicBlock child) {
        if (!child.matchType(BasicBlock.GROUP_END)) {
            Link link = new Link(parent, child);

            if (links.contains(link)) {
                throw new RuntimeException("CFG watchdog: parent=" + parent + ", child=" + child);
            }

            links.add(link);
        }
    }
    
    @Override
    public String toString() {
        return "WatchDog " + links;
    }

    protected static class Link {
        protected int parentIndex;
        protected int childIndex;

        public Link(BasicBlock parent, BasicBlock child) {
            this.parentIndex = parent.getIndex();
            this.childIndex = child.getIndex();
        }

        @Override
        public int hashCode() {
            return 4807589 + parentIndex + 31 * childIndex;
        }

        @Override
        public boolean equals(Object o) {
            Link other = (Link) o;

            return (parentIndex == other.parentIndex) && (childIndex == other.childIndex);
        }
        
        @Override
        public String toString() {
            return "Link (" + parentIndex + "," + childIndex + ")";
        }
    }
}
