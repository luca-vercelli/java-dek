/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.core.v1.service.converter.classfiletojavasyntax.model.cfg;

import java.util.List;
import java.util.Set;

import org.jd.core.v1.model.classfile.Method;
import org.jd.core.v1.util.DefaultList;

/**
 * Flow graph of bytecode of a single method.
 * 
 * The object contains a structure of BasicBlock's.
 */
public class ControlFlowGraph {
    protected Method method;
    protected List<BasicBlock> list = new DefaultList<BasicBlock>() {

        private static final long serialVersionUID = -6085305152533533391L;

        public BasicBlock remove(int index) {
            throw new RuntimeException("Unexpected call");
        }
    };
    protected int[] offsetToLineNumbers = null;

    public ControlFlowGraph(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public List<BasicBlock> getBasicBlocks() {
        return list;
    }

    /**
     * Return first block in internal list, it should be a START block
     */
    public BasicBlock getStart() {
        return list.get(0);
    }

    /**
     * Add a new BasicBlock in the graph, with same features as
     * <code>original</code>
     * 
     * @param original
     * @return new block
     */
    public BasicBlock newBasicBlock(BasicBlock original) {
        BasicBlock basicBlock = new BasicBlock(this, list.size(), original);
        list.add(basicBlock);
        return basicBlock;
    }

    /**
     * Add a new BasicBlock in the graph, with default type DELETED
     */
    public BasicBlock newBasicBlock(int fromOffset, int toOffset) {
        return newBasicBlock(0, fromOffset, toOffset);
    }

    /**
     * Add a new BasicBlock in the graph
     */
    public BasicBlock newBasicBlock(int type, int fromOffset, int toOffset) {
        BasicBlock basicBlock = new BasicBlock(this, list.size(), type, fromOffset, toOffset, true);
        list.add(basicBlock);
        return basicBlock;
    }

    /**
     * Add a new BasicBlock in the graph
     */
    public BasicBlock newBasicBlock(int type, int fromOffset, int toOffset, boolean inverseCondition) {
        BasicBlock basicBlock = new BasicBlock(this, list.size(), type, fromOffset, toOffset, inverseCondition);
        list.add(basicBlock);
        return basicBlock;
    }

    /**
     * Add a new BasicBlock in the graph
     */
    public BasicBlock newBasicBlock(int type, int fromOffset, int toOffset, Set<BasicBlock> predecessors) {
        BasicBlock basicBlock = new BasicBlock(this, list.size(), type, fromOffset, toOffset, true, predecessors);
        list.add(basicBlock);
        return basicBlock;
    }

    public void setOffsetToLineNumbers(int[] offsetToLineNumbers) {
        this.offsetToLineNumbers = offsetToLineNumbers;
    }

    public int getLineNumber(int offset) {
        return (offsetToLineNumbers == null) ? 0 : offsetToLineNumbers[offset];
    }

    @Override
    public String toString() {
        String s = "ControlFlowGraph for " + method + ":\r\n";
        for (BasicBlock l : list) {
            int toOffset = l.getToOffset();
            if (toOffset > 0) {
                --toOffset;
            }
            s += String.format("%03d-%03d %s\r\n", l.getFromOffset(), toOffset, l.getTypeName());
        }
        return s;
    }	
}
