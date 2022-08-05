grammar HumanReadableCDML;

options {
    output = AST;
    ASTLabelType = CommonTree; // type of $stat.tree ref etc...
}


@parser::header {
package com.foros.util.expression;

import java.util.HashSet;
import java.util.Collection;
}

@lexer::header {
package com.foros.util.expression;

}

@parser::members {
HashSet<String> channelNames = new HashSet<String>();

private CDMLParsingError error = null;
public CDMLParsingError getError()
{
  return error;
}

public void reportError(RecognitionException e) 
{
  //super.reportError(e);
  
  String hdr = getErrorHeader(e);
  String msg = getErrorMessage(e, getTokenNames());
  
  error = new CDMLParsingError(hdr + " " + msg, e);
}

}

@lexer::members {
private CDMLParsingError error = null;
public CDMLParsingError getError()
{
  return error;
}

public void reportError(RecognitionException e) 
{
  //super.reportError(e);
  
  String hdr = getErrorHeader(e);
  String msg = getErrorMessage(e, getTokenNames());
  
  error = new CDMLParsingError(hdr + " " + msg, e);
}

}

prog returns [Collection<String> value]
   :  expr {$value = channelNames;}   EOF!
   ;

expr
   :  andNotExpr (OR^ andNotExpr)*
   ;

andNotExpr 
    :   andExpr (AND_NOT^ andExpr)*
    ;

andExpr 
    :   atom (AND^ atom)*
    ; 

atom 
    : CHANNEL_NAME
    {
       channelNames.add($CHANNEL_NAME.text.substring(1, $CHANNEL_NAME.text.length()-1));
    }
    |   '('! expr ')'!
    ;
    
//CHANNEL_NAME 	: '[' ( options {greedy=false;} : . )+ ']';
CHANNEL_NAME 	: '[' (.)+ ']';
OR	:	('or'|'OR');
AND	:	('and'|'AND');
AND_NOT	:	('and_not'|'AND_NOT');
WS  	:	(' '|'\t'|'\n'|'\r')+ { skip(); } ;
