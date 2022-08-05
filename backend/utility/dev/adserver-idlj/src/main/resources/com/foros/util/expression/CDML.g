grammar CDML;

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
HashSet<Long> channelIds = new HashSet<Long>();

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

prog returns [Collection<Long> value]
   :  expr {$value = channelIds;}
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
    :   CHANNEL_ID
    {
       channelIds.add(Long.parseLong($CHANNEL_ID.text));
    }
    |   '('! expr ')'!
    ;

CHANNEL_ID 	: '0'..'9'+;
OR	:	('|');
AND	:	('&');
AND_NOT	:	('^');