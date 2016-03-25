package ru.ifmo.ctddev.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class ExprTree {
    private TreeNode node;
    private List<ExprTree> children;
    Object data;

    public ExprTree(TreeNode node, ExprTree... children) {
        this.node = node;
        this.children = new ArrayList<>();
        Collections.addAll(this.children, children);
    }

    public ExprTree(TreeNode node, Object data, int dummy) {
        this.node = node;
        this.data = data;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        switch (node) {
            case OR:        return children.get(0) + " | " + children.get(1);
            case AND:       return children.get(0) + " & " + children.get(1);
            case LESS:      return children.get(0) + " < " + children.get(1);
            case GREATER:   return children.get(0) + " > " + children.get(1);
            case EQUALS:    return children.get(0) + " == " + children.get(1);
            case PLUS:      return children.get(0) + " + " + children.get(1);
            case MINUS:     return children.get(0) + " - " + children.get(1);
            case TIMES:     return children.get(0) + " * " + children.get(1);
            case DIV:       return children.get(0) + " / " + children.get(1);
            case BRACKETS:  return "(" + children.get(0) + ")";
            case VAR:       return data.toString();
            case INT:       return data.toString();
            case TUPLE: {
                StringBuilder sb = new StringBuilder();
                sb.append(children.get(0));
                for (int i = 1; i < children.size(); i++) {
                    sb.append(", ").append(children.get(i));
                }
                return sb.toString();
            }
            default:        throw new IllegalStateException("Unknown nodeType: " + node);
        }
    }

    public TreeNode getNode() {
        return node;
    }

    public List<ExprTree> getChildren() {
        return children;
    }
}