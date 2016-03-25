package ru.ifmo.ctddev.parsing;

import ru.ifmo.ctddev.parsing.antlr.SLangBaseVisitor;
import ru.ifmo.ctddev.parsing.antlr.SLangParser;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Моклев Вячеслав
 */
public class SemanticVisitor extends SLangBaseVisitor<Object> {
    private PrintStream out = System.out;

    @Override
    public Void visitStatements(SLangParser.StatementsContext ctx) {
        if (ctx.statements() != null) {
            visitStatements(ctx.statements());
        }
        visitStatement(ctx.statement());
        return null;
    }

    private void getVars(Set<String> set, ExprTree tree) {
        if (tree.getNode() == TreeNode.VAR) {
            set.add((String) tree.data);
        }
        tree.getChildren().forEach(t -> getVars(set, t));
    }

    @Override
    public Void visitStatement(SLangParser.StatementContext ctx) {
        if (ctx.ASSIGN() != null) {
            genTupleAssign(visitTuple(ctx.tuple(0)), visitTuple(ctx.tuple(1)));
            return null;
        }
        if (ctx.ARROW() != null) {
            out.println("{");

            out.println('}');
            return null;
        }
        out.println(ctx.expr());
        return null;
    }

    private static int varID = 0;

    private ExprTree getNewVar() {
        return new ExprTree(TreeNode.VAR, "temp" + varID++, 0);
    }

    private ExprTree getNewTuple(ExprTree tree) {
        if (tree.getNode() != TreeNode.TUPLE) {
            return getNewVar();
        }
        ExprTree[] subTuples = new ExprTree[tree.getChildren().size()];
        for (int i = 0; i < tree.getChildren().size(); i++) {
            subTuples[i] = getNewTuple(tree.getChildren().get(i));
        }
        return new ExprTree(TreeNode.TUPLE, subTuples);
    }

    private String getJavaTuple(ExprTree tree) {
        if (tree.getNode() != TreeNode.TUPLE) {
            return tree.toString();
        }
        StringBuilder sb = new StringBuilder("new Tuple(");
        sb.append(tree.getChildren().get(0));
        for (int i = 1; i < tree.getChildren().size(); i++) {
            sb.append(", ").append(getJavaTuple(tree.getChildren().get(i)));
        }
        sb.append(")");
        return sb.toString();
    }

    private void genTupleAssign(ExprTree left, ExprTree right) {
        int n = left.getChildren().size();
        if (n == 0 || n == 1) {
            out.println(left + " = " + getJavaTuple(right) + ";");
            return;
        }
        if (left.getChildren().size() != right.getChildren().size()) {
            throw new RuntimeException("Assignment of tuples of different size: " +
                    left.getChildren().size() + " != " + right.getChildren().size());
        }
        Set<String> varsLeft = new HashSet<>();
        Set<String> varsRight = new HashSet<>();
        getVars(varsLeft, left);
        getVars(varsRight, right);
        boolean flag = false;
        for (String var : varsLeft) {
            if (varsRight.contains(var)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            ExprTree[] newVars = new ExprTree[n];
            for (int i = 0; i < n; i++) {
                newVars[i] = getNewTuple(right.getChildren().get(i));
                genTupleAssign(newVars[i], right.getChildren().get(i));
            }
            for (int i = 0; i < n; i++) {
                genTupleAssign(left.getChildren().get(i), newVars[i]);
            }
        } else {
            for (int i = 0; i < n; i++) {
                genTupleAssign(left.getChildren().get(i), right.getChildren().get(i));
            }
        }
    }

    @Override
    public ExprTree visitTuple(SLangParser.TupleContext ctx) {
        if (ctx.getChildCount() == 3) {
            ExprTree tree = visitTuple(ctx.tuple());
            tree.getChildren().add(visitTupleObject(ctx.tupleObject()));
            return tree;
        }
        return new ExprTree(TreeNode.TUPLE, visitTupleObject(ctx.tupleObject()));
    }

    @Override
    public ExprTree visitTupleObject(SLangParser.TupleObjectContext ctx) {
        if (ctx.econd() != null) {
            return visitEcond(ctx.econd());
        }
        if (ctx.expr() != null) {
            return visitExpr(ctx.expr());
        }
        return visitTuple(ctx.tuple());
    }

    @Override
    public ExprTree visitEcond(SLangParser.EcondContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visitTcond(ctx.tcond());
        }
        return new ExprTree(
                TreeNode.OR,
                visitEcond(ctx.econd()),
                visitTcond(ctx.tcond())
        );
    }

    @Override
    public ExprTree visitTcond(SLangParser.TcondContext ctx) {
        if (ctx.getChildCount() == 1) {
            return visitFcond(ctx.fcond());
        }
        return new ExprTree(
                TreeNode.AND,
                visitTcond(ctx.tcond()),
                visitFcond(ctx.fcond())
        );
    }

    @Override
    public ExprTree visitFcond(SLangParser.FcondContext ctx) {
        if (ctx.op != null) {
            switch (ctx.op.getText()) {
                case "<":
                    return new ExprTree(
                            TreeNode.LESS,
                            visitExpr(ctx.expr(0)),
                            visitExpr(ctx.expr(1))
                    );
                case ">":
                    return new ExprTree(
                            TreeNode.GREATER,
                            visitExpr(ctx.expr(0)),
                            visitExpr(ctx.expr(1))
                    );
                case "==":
                    return new ExprTree(
                            TreeNode.EQUALS,
                            visitExpr(ctx.expr(0)),
                            visitExpr(ctx.expr(1))
                    );
            }
        }
        return new ExprTree(TreeNode.BRACKETS, visitEcond(ctx.econd()));
    }

    @Override
    public ExprTree visitExpr(SLangParser.ExprContext ctx) {
        if (ctx.op != null) {
            switch (ctx.op.getText().charAt(0)) {
                case '+':
                    return new ExprTree(
                            TreeNode.PLUS,
                            visitExpr(ctx.expr()),
                            visitTerm(ctx.term())
                    );
                case '-':
                    return new ExprTree(
                            TreeNode.MINUS,
                            visitExpr(ctx.expr()),
                            visitTerm(ctx.term())
                    );
            }
        }
        return visitTerm(ctx.term());
    }

    @Override
    public ExprTree visitTerm(SLangParser.TermContext ctx) {
        if (ctx.op != null) {
            switch (ctx.op.getText().charAt(0)) {
                case '*':
                    return new ExprTree(
                            TreeNode.TIMES,
                            visitTerm(ctx.term()),
                            visitFactor(ctx.factor())
                    );
                case '/':
                    return new ExprTree(
                            TreeNode.DIV,
                            visitTerm(ctx.term()),
                            visitFactor(ctx.factor())
                    );
            }
        }
        return visitFactor(ctx.factor());
    }

    @Override
    public ExprTree visitFactor(SLangParser.FactorContext ctx) {
        if (ctx.getChildCount() == 1) {
            if (ctx.ID() != null) return new ExprTree(TreeNode.VAR, ctx.getText(), 0);
            if (ctx.INT() != null) return new ExprTree(TreeNode.INT, Integer.valueOf(ctx.getText()), 0);
            throw new IllegalStateException("Unknown rule");
        }
        return new ExprTree(TreeNode.BRACKETS, visitExpr(ctx.expr()));
    }
}
