package ru.ifmo.ctddev.parsing;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import ru.ifmo.ctddev.parsing.antlr.SLangLexer;
import ru.ifmo.ctddev.parsing.antlr.SLangParser;

/**
 * @author Моклев Вячеслав
 */
public class Main {
    public static void main(String[] args) {
        ANTLRInputStream is = new ANTLRInputStream("(a + b) * (c - d * e)");
        SLangLexer lexer = new SLangLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SLangParser parser = new SLangParser(tokens);
        ParseTree tree = parser.expr();
        System.out.println(new SemanticVisitor().visit(tree));
    }
}
