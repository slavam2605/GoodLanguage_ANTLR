grammar SLang;

// code structure
statements: statements statement
    |       statement;
statement:  ID ASSIGN expr
    |       ID ASSIGN econd;

ASSIGN: '=';

// expressions
econd
    :   econd '|' tcond
    |   tcond;
tcond
    :   tcond '&' fcond
    |   fcond;
fcond
    :   expr op=('<'|'>'|'==') expr
    |   '(' econd ')';
expr
    :   expr op=('+'|'-') term
    |   term;
term
    :   term op=('*'|'/') factor
    |   factor;
factor
    :   ID
    |   '(' expr ')';

// terminals
INT:    [0-9]+;
ID:     [a-zA-Z_]*[a-zA-Z0-9_]+;
WS:     [ \t\n\r]+ -> skip;