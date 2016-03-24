package ru.ifmo.ctddev.parsing;

import ru.ifmo.ctddev.parsing.antlr.SLangBaseVisitor;
import ru.ifmo.ctddev.parsing.antlr.SLangParser;

/**
 * @author Моклев Вячеслав
 */
public class SemanticVisitor extends SLangBaseVisitor<Object> {
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
                case "<": return new ExprTree(
                        TreeNode.LESS,
                        visitExpr(ctx.expr(0)),
                        visitExpr(ctx.expr(1))
                );
                case ">": return new ExprTree(
                        TreeNode.GREATER,
                        visitExpr(ctx.expr(0)),
                        visitExpr(ctx.expr(1))
                );
                case "==": return new ExprTree(
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
                case '+': return new ExprTree(
                        TreeNode.PLUS,
                        visitExpr(ctx.expr()),
                        visitTerm(ctx.term())
                );
                case '-': return new ExprTree(
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
                case '*': return new ExprTree(
                        TreeNode.TIMES,
                        visitTerm(ctx.term()),
                        visitFactor(ctx.factor())
                );
                case '/': return new ExprTree(
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
            return new ExprTree(TreeNode.VAR, ctx.getText(), 0);
        }
        return new ExprTree(TreeNode.BRACKETS, visitExpr(ctx.expr()));
    }
}
