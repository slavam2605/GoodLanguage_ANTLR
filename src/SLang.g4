grammar SLang;

// code structure
statements: statements statement DELIM
    |       statement DELIM;
statement:  tuple ASSIGN tuple
    |       expr
    |       tuple ARROW tuple ':' statement ('!' statement)?;
tuple:      tuple ',' tupleObject
    |       tupleObject;
tupleObject:    econd
    |           expr
    |           '(' tuple ')';

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
    |   INT
    |   '(' expr ')';

// terminals
INT:    [0-9]+;
ID:     [a-zA-Z_]*[a-zA-Z0-9_]+;
DELIM:  [;\n]+;
ASSIGN: '=';
ARROW:  '->';
BR:     [\n] -> channel(HIDDEN);
WS:     [ \t\r]+ -> skip;