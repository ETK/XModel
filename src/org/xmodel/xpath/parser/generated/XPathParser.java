/*
 * JAHM - Java Advanced Hierarchical Model 
 * 
 * XPathParser.java
 * 
 * Copyright 2009 Robert Arvin Dunnagan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmodel.xpath.parser.generated;

import java.util.List;
import java.util.ArrayList;
import org.xmodel.*;
import org.xmodel.xpath.*;
import org.xmodel.xpath.expression.*;
import org.xmodel.xpath.function.*;
import org.xmodel.xpath.function.custom.*;
import org.xmodel.xpath.variable.*;

@SuppressWarnings("all")
public class XPathParser implements XPathParserConstants {
  public void setSpec( String spec)
  {
    this.spec = spec;
  }

  /** Extracts the literal value from a token of type <LITERAL> */
  protected static String getLiteralValue( Token literal )
  {
    return literal.image.substring( 1, literal.image.length()-1 );
  }

  private static final IPathElement createStep( int axis, String nodeTest, PredicateExpression predicate)
  {
        if ( nodeTest != null && nodeTest.equals( "text()")) axis = IAxis.ATTRIBUTE;
    if ( predicate != null)
      return new PathElement( axis, nodeTest, predicate);
    return new PathElement( axis, nodeTest);
  }

  private static final void addDescendantStep( AbstractPath path, IPathElement step)
  {
    // step predicate will still belong to the same path so no need to update
    if ( step.axis() == IAxis.CHILD)
    {
      path.addElement( new PathElement( IAxis.DESCENDANT, step.type(), step.predicate()));
    }
    else
    {
      path.addElement( new PathElement( IAxis.DESCENDANT | IAxis.SELF, null, null));
      path.addElement( step);
    }
  }

  private static final IExpression createOR( IExpression lhs, IExpression rhs)
  {
    LogicalExpression.Operator operator = LogicalExpression.Operator.OR;
    return new LogicalExpression( operator, lhs, rhs);
  }

  private static final IExpression createAND( IExpression lhs, IExpression rhs)
  {
    LogicalExpression.Operator operator = LogicalExpression.Operator.AND;
    return new LogicalExpression( operator, lhs, rhs);
  }

  private static final IExpression createGT( IExpression lhs, IExpression rhs)
  {
    RelationalExpression.Operator operator = RelationalExpression.Operator.GT;
    return new RelationalExpression( operator, lhs, rhs);
  }

  private static final IExpression createGE( IExpression lhs, IExpression rhs)
  {
    RelationalExpression.Operator operator = RelationalExpression.Operator.GE;
    return new RelationalExpression( operator, lhs, rhs);
  }

  private static final IExpression createLT( IExpression lhs, IExpression rhs)
  {
    RelationalExpression.Operator operator = RelationalExpression.Operator.LT;
    return new RelationalExpression( operator, lhs, rhs);
  }

  private static final IExpression createLE( IExpression lhs, IExpression rhs)
  {
    RelationalExpression.Operator operator = RelationalExpression.Operator.LE;
    return new RelationalExpression( operator, lhs, rhs);
  }

  private static final IExpression createEQ( IExpression lhs, IExpression rhs)
  {
    EqualityExpression.Operator operator = EqualityExpression.Operator.EQ;
    return new EqualityExpression( operator, lhs, rhs);
  }

  private static final IExpression createNEQ( IExpression lhs, IExpression rhs)
  {
    EqualityExpression.Operator operator = EqualityExpression.Operator.NEQ;
    return new EqualityExpression( operator, lhs, rhs);
  }

  private static final IExpression createADD( IExpression lhs, IExpression rhs)
  {
    ArithmeticExpression.Operator operator = ArithmeticExpression.Operator.ADD;
    return new ArithmeticExpression( operator, lhs, rhs);
  }

  private static final IExpression createSUB( IExpression lhs, IExpression rhs)
  {
    ArithmeticExpression.Operator operator = ArithmeticExpression.Operator.SUB;
    return new ArithmeticExpression( operator, lhs, rhs);
  }

  private static final IExpression createMUL( IExpression lhs, IExpression rhs)
  {
    ArithmeticExpression.Operator operator = ArithmeticExpression.Operator.MUL;
    return new ArithmeticExpression( operator, lhs, rhs);
  }

  private static final IExpression createDIV( IExpression lhs, IExpression rhs)
  {
    ArithmeticExpression.Operator operator = ArithmeticExpression.Operator.DIV;
    return new ArithmeticExpression( operator, lhs, rhs);
  }

  private static final IExpression createMOD( IExpression lhs, IExpression rhs)
  {
    ArithmeticExpression.Operator operator = ArithmeticExpression.Operator.MOD;
    return new ArithmeticExpression( operator, lhs, rhs);
  }

  private static final IExpression createNEG( IExpression rhs)
  {
    return new NegateExpression( rhs);
  }

  private static final IExpression createUnion( IExpression lhs, IExpression rhs)
  {
    return new UnionExpression( lhs, rhs);
  }

  private static final IExpression createChildExtension( IExpression lhs, AbstractPath path)
  {
    return new FilteredExpression( lhs, new PathExpression( path));
  }

  private static final IExpression createDescendantExtension( IExpression lhs, AbstractPath path)
  {
        IPathElement element = path.getPathElement( 0);
        element.setAxis( IAxis.DESCENDANT);
    return new FilteredExpression( lhs, new PathExpression( path));
  }

  private final void createAbsolute( IPath relativePath, AbstractPath resultPath)
  {
    // create path and add root node
    IPathElement element = relativePath.getPathElement( 0);
    resultPath.addElement( new PathElement( IAxis.ROOT, element.type(), element.predicate()));

    // change the path to which the element predicate belongs
    PredicateExpression predicate = (PredicateExpression)element.predicate();
    if ( predicate != null) predicate.setParentPath( resultPath);

    // add other path elements (relative path must be discarded)
    for ( int i=1; i<relativePath.length(); i++)
    {
      element = relativePath.getPathElement( i);
      resultPath.addElement( element);

      // change the path to which the element predicate belongs
      predicate = (PredicateExpression)element.predicate();
      if ( predicate != null) predicate.setParentPath( resultPath);
    }
  }

  private final void createAbsoluteDescendant( IPath relativePath, AbstractPath resultPath)
  {
    // create path and add root node
    IPathElement element = relativePath.getPathElement( 0);
    resultPath.addElement( new PathElement( IAxis.ROOT, null, null));
    resultPath.addElement( new PathElement( (IAxis.DESCENDANT | IAxis.SELF), element.type(), element.predicate()));

    // change the path to which the element predicate belongs
    PredicateExpression predicate = (PredicateExpression)element.predicate();
    if ( predicate != null) predicate.setParentPath( resultPath);

    // add other path elements (relative path must be discarded)
    for ( int i=1; i<relativePath.length(); i++)
    {
      element = relativePath.getPathElement( i);
      resultPath.addElement( element);

      // change the path to which the element predicate belongs
      predicate = (PredicateExpression)element.predicate();
      if ( predicate != null) predicate.setParentPath( resultPath);
    }
  }

  private final void createAbsolute( AbstractPath resultPath)
  {
    resultPath.addElement( new PathElement( IAxis.ROOT));
  }

  String spec;
  RootExpression root;
  final static FunctionFactory functionFactory = FunctionFactory.getInstance();

// More XPath 1.0 comformant NCName that has characters that cause regex warnings
//
//TOKEN://{//  <NCName: (<Letter> | "_") (<NCNameChar>)* >//  | <#NCNameChar: <Letter> | <Digit> | "." | "-" | "_" | <CombiningChar> | <Extender> >//  | <#Letter: <BaseChar> | <Ideographic> >//  //  | <#BaseChar://      ["A"-"Z"] | ["a"-"z"] | ["�"-"�"] | ["�"-"�"]//    | ["�"-"�"] | ["?"-"�"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"]//    | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?"//    | "?" | "?" | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"]//    | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | "?"//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | "?" | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | "?" | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | "?" | "?" | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?"//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"]//    | "?" | "?" | "?" | "?" | "?" | "?" | ["?"-"?"] | "?"//    | ["?"-"?"] | "?" | "?" | "?" | "?" | ["?"-"?"]//    | ["?"-"?"] | "?" | "?" | "?" | "?" | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | "?" | "?" | "?"//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | "?" | "?"//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | "?"//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] >//  | <#Ideographic:    ["?"-"?"] | "?" | ["?"-"?"] > //  | <#CombiningChar://      ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | "?"//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | "?" | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | "?" | "?" | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | "?" | "?" | "?"//    | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | "?"//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?" | "?" | "?"//    | "?" | "?" | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | "?"//    | ["?"-"?"] | ["?"-"?"] | "?" | ["?"-"?"] | "?"//    | ["?"-"?"] | "?" | "?" >//  | <#Digit://      ["0"-"9"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] | ["?"-"?"]//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] >//  | <#Extender ://      "�" | "?" | "?" | "?" | "?" | "?" | "?" | "?"//    | ["?"-"?"] | ["?"-"?"] | ["?"-"?"] >//}
  final public void Void() throws ParseException {

  }

  final public void ParsePath(AbstractPath path) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SLASH:
    case SLASHSLASH:
      AbsoluteLocationPath(path);
      break;
    case ABBREVIATED_ATTRIBUTE_AXIS:
    case STAR:
    case OR:
    case IN:
    case IF:
    case FOR:
    case AND:
    case DIV:
    case MOD:
    case THEN:
    case ELSE:
    case AXIS_SELF:
    case NT_TEXT:
    case NT_NODE:
    case AXIS_CHILD:
    case RETURN:
    case AXIS_NESTED:
    case AXIS_PARENT:
    case AXIS_ANCESTOR:
    case NT_COMMENT:
    case AXIS_FOLLOWING:
    case AXIS_PRECEDING:
    case AXIS_ATTRIBUTE:
    case AXIS_NAMESPACE:
    case AXIS_DESCENDANT:
    case AXIS_NESTED_OR_SELF:
    case AXIS_ANCESTOR_OR_SELF:
    case AXIS_FOLLOWING_SIBLING:
    case AXIS_PRECEDING_SIBLING:
    case AXIS_DESCENDANT_OR_SELF:
    case NT_PI:
    case NCName:
    case 54:
    case 55:
      RelativeLocationPath(path);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(0);
  }

  final public IExpression ParseExpression() throws ParseException {
  boolean hasLet = false;
  LetExpression let = new LetExpression();
  IExpression expression = null;
    root = new RootExpression();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LET:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      LetClause(root, let);
                           hasLet = true;
    }
    expression = Expr();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 52:
      jj_consume_token(52);
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(0);
    if ( hasLet)
    {
      let.addArgument( expression);
      root.addArgument( let);
      {if (true) return root;}
    }
    else
    {
      root.addArgument( expression);
      {if (true) return root;}
    }
    throw new Error("Missing return statement in function");
  }

  final public void LetClause(RootExpression root, LetExpression let) throws ParseException {
  Token var;
  IExpression assignment = null;
    jj_consume_token(LET);
    jj_consume_token(53);
    var = jj_consume_token(NCName);
    jj_consume_token(ASSIGN);
    assignment = Expr();
    jj_consume_token(52);
    // create new root expression with the same variable source
    IVariableSource source = root.getVariableSource();
    RootExpression letRoot = new RootExpression( assignment);
    let.addExpression( letRoot, var.image);
    letRoot.setVariableSource( source);
  }

  final public void AbsoluteLocationPath(AbstractPath absolutePath) throws ParseException {
  AbstractPath relativePath = new CanonicalPath();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SLASHSLASH:
      jj_consume_token(SLASHSLASH);
      RelativeLocationPath(relativePath);
                                                       createAbsoluteDescendant( relativePath, absolutePath);
      break;
    default:
      jj_la1[3] = jj_gen;
      if (jj_2_1(2)) {
        jj_consume_token(SLASH);
        RelativeLocationPath(relativePath);
                                                                createAbsolute( relativePath, absolutePath);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case SLASH:
          jj_consume_token(SLASH);
              createAbsolute( absolutePath);
          break;
        default:
          jj_la1[4] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
  }

  final public AbstractPath RelativeLocationPath(AbstractPath path) throws ParseException {
  IPathElement element;
    element = Step(path);
                                         path.addElement( element);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SLASH:
      case SLASHSLASH:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SLASH:
        jj_consume_token(SLASH);
        element = Step(path);
                                         path.addElement( element);
        break;
      case SLASHSLASH:
        jj_consume_token(SLASHSLASH);
        element = Step(path);
                                         addDescendantStep( path, element);
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return path;}
    throw new Error("Missing return statement in function");
  }

/**
 * Note that this method does not add the location step to the path argument.
 **/
  final public IPathElement Step(IPath path) throws ParseException {
  int axis;
  String nodeTest = null;
  PredicateExpression predicate = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ABBREVIATED_ATTRIBUTE_AXIS:
    case STAR:
    case OR:
    case IN:
    case IF:
    case FOR:
    case AND:
    case DIV:
    case MOD:
    case THEN:
    case ELSE:
    case AXIS_SELF:
    case NT_TEXT:
    case NT_NODE:
    case AXIS_CHILD:
    case RETURN:
    case AXIS_NESTED:
    case AXIS_PARENT:
    case AXIS_ANCESTOR:
    case NT_COMMENT:
    case AXIS_FOLLOWING:
    case AXIS_PRECEDING:
    case AXIS_ATTRIBUTE:
    case AXIS_NAMESPACE:
    case AXIS_DESCENDANT:
    case AXIS_NESTED_OR_SELF:
    case AXIS_ANCESTOR_OR_SELF:
    case AXIS_FOLLOWING_SIBLING:
    case AXIS_PRECEDING_SIBLING:
    case AXIS_DESCENDANT_OR_SELF:
    case NT_PI:
    case NCName:
      axis = AxisSpecifier();
      nodeTest = NodeTest();
      predicate = PredicateList(path);
                                        {if (true) return createStep( axis, nodeTest, predicate);}
      break;
    case 54:
      jj_consume_token(54);
      predicate = PredicateList(path);
                                              {if (true) return createStep( IAxis.SELF, null, predicate);}
      break;
    case 55:
      jj_consume_token(55);
      predicate = PredicateList(path);
                                               {if (true) return createStep( IAxis.PARENT, null, predicate);}
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public int AxisSpecifier() throws ParseException {
  int axis;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ABBREVIATED_ATTRIBUTE_AXIS:
      jj_consume_token(ABBREVIATED_ATTRIBUTE_AXIS);
                                 {if (true) return IAxis.ATTRIBUTE;}
      break;
    default:
      jj_la1[8] = jj_gen;
      if (jj_2_2(2147483647)) {
        // if not, problems for child[1], token <child> would be consummed
            // NCName() "::" is more conformant to the specification
            axis = AxisName();
                        {if (true) return axis;}
      } else {
        Void();
                        {if (true) return IAxis.CHILD;}
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public int AxisName() throws ParseException {
  int axis = -1;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AXIS_ANCESTOR:
      jj_consume_token(AXIS_ANCESTOR);
                                       axis = IAxis.ANCESTOR;
      break;
    case AXIS_ANCESTOR_OR_SELF:
      jj_consume_token(AXIS_ANCESTOR_OR_SELF);
                                       axis = IAxis.ANCESTOR | IAxis.SELF;
      break;
    case AXIS_ATTRIBUTE:
      jj_consume_token(AXIS_ATTRIBUTE);
                                       axis = IAxis.ATTRIBUTE;
      break;
    case AXIS_CHILD:
      jj_consume_token(AXIS_CHILD);
                                       axis = IAxis.CHILD;
      break;
    case AXIS_DESCENDANT:
      jj_consume_token(AXIS_DESCENDANT);
                                       axis = IAxis.DESCENDANT;
      break;
    case AXIS_DESCENDANT_OR_SELF:
      jj_consume_token(AXIS_DESCENDANT_OR_SELF);
                                       axis = IAxis.DESCENDANT | IAxis.SELF;
      break;
    case AXIS_NESTED:
      jj_consume_token(AXIS_NESTED);
                                       axis = IAxis.NESTED;
      break;
    case AXIS_NESTED_OR_SELF:
      jj_consume_token(AXIS_NESTED_OR_SELF);
                                       axis = IAxis.NESTED | IAxis.SELF;
      break;
    case AXIS_FOLLOWING:
      jj_consume_token(AXIS_FOLLOWING);
                                       axis = IAxis.FOLLOWING;
      break;
    case AXIS_FOLLOWING_SIBLING:
      jj_consume_token(AXIS_FOLLOWING_SIBLING);
                                       axis = IAxis.FOLLOWING_SIBLING;
      break;
    case AXIS_NAMESPACE:
      jj_consume_token(AXIS_NAMESPACE);
                                       axis = -1;
      break;
    case AXIS_PARENT:
      jj_consume_token(AXIS_PARENT);
                                       axis = IAxis.PARENT;
      break;
    case AXIS_PRECEDING:
      jj_consume_token(AXIS_PRECEDING);
                                       axis = IAxis.PRECEDING;
      break;
    case AXIS_PRECEDING_SIBLING:
      jj_consume_token(AXIS_PRECEDING_SIBLING);
                                       axis = IAxis.PRECEDING_SIBLING;
      break;
    case AXIS_SELF:
      jj_consume_token(AXIS_SELF);
                                       axis = IAxis.SELF;
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(DOTDOT);

    if ( axis < 0) {if (true) throw new ParseException( "Axis not supported: "+getToken( 0));}
    {if (true) return axis;}
    throw new Error("Missing return statement in function");
  }

  final public String NodeTest() throws ParseException {
  String result = null;
    if (jj_2_3(2147483647)) {
      result = NodeType();
      jj_consume_token(56);
      jj_consume_token(57);
                                {if (true) return result+"()";}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
      case OR:
      case IN:
      case IF:
      case FOR:
      case AND:
      case DIV:
      case MOD:
      case THEN:
      case ELSE:
      case AXIS_SELF:
      case NT_TEXT:
      case NT_NODE:
      case AXIS_CHILD:
      case RETURN:
      case AXIS_NESTED:
      case AXIS_PARENT:
      case AXIS_ANCESTOR:
      case NT_COMMENT:
      case AXIS_FOLLOWING:
      case AXIS_PRECEDING:
      case AXIS_ATTRIBUTE:
      case AXIS_NAMESPACE:
      case AXIS_DESCENDANT:
      case AXIS_NESTED_OR_SELF:
      case AXIS_ANCESTOR_OR_SELF:
      case AXIS_FOLLOWING_SIBLING:
      case AXIS_PRECEDING_SIBLING:
      case AXIS_DESCENDANT_OR_SELF:
      case NT_PI:
      case NCName:
        result = NameTest();
                          {if (true) return result;}
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public String NameTest() throws ParseException {
  String name = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STAR:
      jj_consume_token(STAR);
                                       {if (true) return( null);}
      break;
    default:
      jj_la1[11] = jj_gen;
      if (jj_2_4(2147483647)) {
        name = NCName();
        jj_consume_token(58);
        jj_consume_token(STAR);
                                       {if (true) return( name+':'+'*');}
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OR:
        case IN:
        case IF:
        case FOR:
        case AND:
        case DIV:
        case MOD:
        case THEN:
        case ELSE:
        case AXIS_SELF:
        case NT_TEXT:
        case NT_NODE:
        case AXIS_CHILD:
        case RETURN:
        case AXIS_NESTED:
        case AXIS_PARENT:
        case AXIS_ANCESTOR:
        case NT_COMMENT:
        case AXIS_FOLLOWING:
        case AXIS_PRECEDING:
        case AXIS_ATTRIBUTE:
        case AXIS_NAMESPACE:
        case AXIS_DESCENDANT:
        case AXIS_NESTED_OR_SELF:
        case AXIS_ANCESTOR_OR_SELF:
        case AXIS_FOLLOWING_SIBLING:
        case AXIS_PRECEDING_SIBLING:
        case AXIS_DESCENDANT_OR_SELF:
        case NT_PI:
        case NCName:
          name = QName();
                                       {if (true) return( name);}
          break;
        default:
          jj_la1[12] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public String NodeType() throws ParseException {
  Token token = null;
  String pi = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NT_TEXT:
      token = jj_consume_token(NT_TEXT);
      break;
    case NT_NODE:
      token = jj_consume_token(NT_NODE);
      break;
    case NT_PI:
      pi = ProcessingInstruction();
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return token.image;}
    throw new Error("Missing return statement in function");
  }

  final public String ProcessingInstruction() throws ParseException {
  Token target = null;
  String data = null;
    target = jj_consume_token(NT_PI);
    {if (true) return target.image;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression PrimaryExpr() throws ParseException {
  Token token;
  IExpression expression = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 53:
      jj_consume_token(53);
      token = jj_consume_token(NCName);
                            {if (true) return new VariableExpression( token.image);}
      break;
    case 56:
      jj_consume_token(56);
      expression = Expr();
      jj_consume_token(57);
                                     {if (true) return expression;}
      break;
    case LITERAL:
      token = jj_consume_token(LITERAL);
                        {if (true) return new LiteralExpression( getLiteralValue( token));}
      break;
    case NUMBER:
      token = jj_consume_token(NUMBER);
                        {if (true) return new LiteralExpression( Double.parseDouble( token.image));}
      break;
    case OR:
    case IN:
    case IF:
    case FOR:
    case AND:
    case DIV:
    case MOD:
    case THEN:
    case ELSE:
    case AXIS_SELF:
    case NT_TEXT:
    case NT_NODE:
    case AXIS_CHILD:
    case RETURN:
    case AXIS_NESTED:
    case AXIS_PARENT:
    case AXIS_ANCESTOR:
    case NT_COMMENT:
    case AXIS_FOLLOWING:
    case AXIS_PRECEDING:
    case AXIS_ATTRIBUTE:
    case AXIS_NAMESPACE:
    case AXIS_DESCENDANT:
    case AXIS_NESTED_OR_SELF:
    case AXIS_ANCESTOR_OR_SELF:
    case AXIS_FOLLOWING_SIBLING:
    case AXIS_PRECEDING_SIBLING:
    case AXIS_DESCENDANT_OR_SELF:
    case NT_PI:
    case NCName:
      expression = FunctionCall();
                                  {if (true) return expression;}
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public IExpression Expr() throws ParseException {
  IExpression expression = null;
    if (jj_2_5(2147483647)) {
      expression = ForExpr();
                                                 {if (true) return expression;}
    } else if (jj_2_6(2147483647)) {
      expression = IfExpr();
                                                 {if (true) return expression;}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LITERAL:
      case NUMBER:
      case ABBREVIATED_ATTRIBUTE_AXIS:
      case SLASH:
      case STAR:
      case MINUS:
      case SLASHSLASH:
      case OR:
      case IN:
      case IF:
      case FOR:
      case AND:
      case DIV:
      case MOD:
      case THEN:
      case ELSE:
      case AXIS_SELF:
      case NT_TEXT:
      case NT_NODE:
      case AXIS_CHILD:
      case RETURN:
      case AXIS_NESTED:
      case AXIS_PARENT:
      case AXIS_ANCESTOR:
      case NT_COMMENT:
      case AXIS_FOLLOWING:
      case AXIS_PRECEDING:
      case AXIS_ATTRIBUTE:
      case AXIS_NAMESPACE:
      case AXIS_DESCENDANT:
      case AXIS_NESTED_OR_SELF:
      case AXIS_ANCESTOR_OR_SELF:
      case AXIS_FOLLOWING_SIBLING:
      case AXIS_PRECEDING_SIBLING:
      case AXIS_DESCENDANT_OR_SELF:
      case NT_PI:
      case NCName:
      case 53:
      case 54:
      case 55:
      case 56:
        expression = OrExpr();
                            {if (true) return expression;}
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public IExpression ForExpr() throws ParseException {
  Token var;
  IExpression expression;
  IExpression forExpression;
  IExpression nextExpression;
    jj_consume_token(FOR);
    jj_consume_token(53);
    var = jj_consume_token(NCName);
    jj_consume_token(IN);
    expression = Expr();
    forExpression = new ForExpression( var.image);
    forExpression.addArgument( expression);
    nextExpression = forExpression;
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 59:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_3;
      }
      jj_consume_token(59);
      jj_consume_token(53);
      var = jj_consume_token(NCName);
      jj_consume_token(IN);
      expression = Expr();
    ForExpression newExpression = new ForExpression( var.image);
    newExpression.addArgument( expression);
    nextExpression.addArgument( newExpression);
    nextExpression = newExpression;
    }
    jj_consume_token(RETURN);
    expression = Expr();
    nextExpression.addArgument( expression);
    {if (true) return forExpression;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression IfExpr() throws ParseException {
  IExpression condition;
  IExpression thenExpression;
  IExpression elseExpression;
    jj_consume_token(IF);
    condition = Expr();
    jj_consume_token(THEN);
    thenExpression = Expr();
    jj_consume_token(ELSE);
    elseExpression = Expr();
    IfExpression expression = new IfExpression();
    expression.addArgument( condition);
    expression.addArgument( thenExpression);
    expression.addArgument( elseExpression);
    {if (true) return expression;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression OrExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    // this pattern results in left-associativity
      lhs = AndExpr();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[17] = jj_gen;
        break label_4;
      }
      jj_consume_token(OR);
      rhs = AndExpr();
                          lhs = createOR( lhs, rhs);
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression AndExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = EqualityExpr();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[18] = jj_gen;
        break label_5;
      }
      jj_consume_token(AND);
      rhs = EqualityExpr();
                                lhs = createAND( lhs, rhs);
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression EqualityExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = RelationalExpr();
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQ:
      case NEQ:
        ;
        break;
      default:
        jj_la1[19] = jj_gen;
        break label_6;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQ:
        jj_consume_token(EQ);
        rhs = RelationalExpr();
                                   lhs = createEQ( lhs, rhs);
        break;
      case NEQ:
        jj_consume_token(NEQ);
        rhs = RelationalExpr();
                                    lhs = createNEQ( lhs, rhs);
        break;
      default:
        jj_la1[20] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression RelationalExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = AdditiveExpr();
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LT:
      case GT:
      case LE:
      case GE:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_7;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LT:
        jj_consume_token(LT);
        rhs = AdditiveExpr();
                                 lhs = createLT( lhs, rhs);
        break;
      case GT:
        jj_consume_token(GT);
        rhs = AdditiveExpr();
                                 lhs = createGT( lhs, rhs);
        break;
      case LE:
        jj_consume_token(LE);
        rhs = AdditiveExpr();
                                 lhs = createLE( lhs, rhs);
        break;
      case GE:
        jj_consume_token(GE);
        rhs = AdditiveExpr();
                                 lhs = createGE( lhs, rhs);
        break;
      default:
        jj_la1[22] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression AdditiveExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = MultiplicativeExpr();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case MINUS:
        ;
        break;
      default:
        jj_la1[23] = jj_gen;
        break label_8;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        jj_consume_token(PLUS);
        rhs = MultiplicativeExpr();
                                         lhs = createADD( lhs, rhs);
        break;
      case MINUS:
        jj_consume_token(MINUS);
        rhs = MultiplicativeExpr();
                                          lhs = createSUB( lhs, rhs);
        break;
      default:
        jj_la1[24] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression MultiplicativeExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = UnaryExpr();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
      case DIV:
      case MOD:
        ;
        break;
      default:
        jj_la1[25] = jj_gen;
        break label_9;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DIV:
        jj_consume_token(DIV);
        rhs = UnaryExpr();
                              lhs = createDIV( lhs, rhs);
        break;
      case MOD:
        jj_consume_token(MOD);
        rhs = UnaryExpr();
                              lhs = createMOD( lhs, rhs);
        break;
      case STAR:
        jj_consume_token(STAR);
        rhs = UnaryExpr();
                               lhs = createMUL( lhs, rhs);
        break;
      default:
        jj_la1[26] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression UnaryExpr() throws ParseException {
  IExpression expression = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MINUS:
      jj_consume_token(MINUS);
      expression = UnaryExpr();
                                     {if (true) return createNEG( expression);}
      break;
    case LITERAL:
    case NUMBER:
    case ABBREVIATED_ATTRIBUTE_AXIS:
    case SLASH:
    case STAR:
    case SLASHSLASH:
    case OR:
    case IN:
    case IF:
    case FOR:
    case AND:
    case DIV:
    case MOD:
    case THEN:
    case ELSE:
    case AXIS_SELF:
    case NT_TEXT:
    case NT_NODE:
    case AXIS_CHILD:
    case RETURN:
    case AXIS_NESTED:
    case AXIS_PARENT:
    case AXIS_ANCESTOR:
    case NT_COMMENT:
    case AXIS_FOLLOWING:
    case AXIS_PRECEDING:
    case AXIS_ATTRIBUTE:
    case AXIS_NAMESPACE:
    case AXIS_DESCENDANT:
    case AXIS_NESTED_OR_SELF:
    case AXIS_ANCESTOR_OR_SELF:
    case AXIS_FOLLOWING_SIBLING:
    case AXIS_PRECEDING_SIBLING:
    case AXIS_DESCENDANT_OR_SELF:
    case NT_PI:
    case NCName:
    case 53:
    case 54:
    case 55:
    case 56:
      expression = UnionExpr();
                               {if (true) return expression;}
      break;
    default:
      jj_la1[27] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public IExpression UnionExpr() throws ParseException {
  IExpression lhs = null;
  IExpression rhs = null;
    lhs = PathExpr();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case UNION:
        ;
        break;
      default:
        jj_la1[28] = jj_gen;
        break label_10;
      }
      jj_consume_token(UNION);
      rhs = PathExpr();
                              lhs = createUnion( lhs, rhs);
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression FilterExpr() throws ParseException {
  IExpression expression = null;
  IExpression predicate = null;
    expression = PrimaryExpr();
    predicate = PredicateList(null);
    if ( predicate == null) {if (true) return expression;}
    {if (true) return new FilteredExpression( expression, predicate);}
    throw new Error("Missing return statement in function");
  }

  final public PredicateExpression PredicateList(IPath path) throws ParseException {
  PredicateExpression predicate = new PredicateExpression( path);
  IExpression rhs = null;
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 60:
        ;
        break;
      default:
        jj_la1[29] = jj_gen;
        break label_11;
      }
      rhs = Predicate();
                       predicate.addArgument( rhs);
    }
    List arguments = predicate.getArguments();
    if ( arguments.size() > 0) {if (true) return predicate;}
    {if (true) return null;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression Predicate() throws ParseException {
  IExpression expression = null;
    jj_consume_token(60);
    expression = Expr();
    jj_consume_token(61);
                                {if (true) return expression;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression FunctionCall() throws ParseException {
  IExpression function = null;
  IExpression expression = null;
    function = FunctionName();
    jj_consume_token(56);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LITERAL:
    case NUMBER:
    case ABBREVIATED_ATTRIBUTE_AXIS:
    case SLASH:
    case STAR:
    case MINUS:
    case SLASHSLASH:
    case OR:
    case IN:
    case IF:
    case FOR:
    case AND:
    case DIV:
    case MOD:
    case THEN:
    case ELSE:
    case AXIS_SELF:
    case NT_TEXT:
    case NT_NODE:
    case AXIS_CHILD:
    case RETURN:
    case AXIS_NESTED:
    case AXIS_PARENT:
    case AXIS_ANCESTOR:
    case NT_COMMENT:
    case AXIS_FOLLOWING:
    case AXIS_PRECEDING:
    case AXIS_ATTRIBUTE:
    case AXIS_NAMESPACE:
    case AXIS_DESCENDANT:
    case AXIS_NESTED_OR_SELF:
    case AXIS_ANCESTOR_OR_SELF:
    case AXIS_FOLLOWING_SIBLING:
    case AXIS_PRECEDING_SIBLING:
    case AXIS_DESCENDANT_OR_SELF:
    case NT_PI:
    case NCName:
    case 53:
    case 54:
    case 55:
    case 56:
      expression = Expr();
                              function.addArgument( expression);
      label_12:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 59:
          ;
          break;
        default:
          jj_la1[30] = jj_gen;
          break label_12;
        }
        jj_consume_token(59);
        expression = Expr();
                                    function.addArgument( expression);
      }
      break;
    default:
      jj_la1[31] = jj_gen;
      ;
    }
    jj_consume_token(57);
        {if (true) return function;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression FunctionName() throws ParseException {
  String name = null;
    name = QNameWithoutNodeType();
    IExpression function = functionFactory.createFunction( name);
    if ( function == null) {if (true) throw new ParseException( "Undefined function: "+name);}
    {if (true) return function;}
    throw new Error("Missing return statement in function");
  }

  final public IExpression PathExpr() throws ParseException {
  AbstractPath path = new CanonicalPath( root);
  IExpression lhs = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SLASH:
    case SLASHSLASH:
      AbsoluteLocationPath(path);
                                 lhs = new PathExpression( path);
      break;
    default:
      jj_la1[34] = jj_gen;
      if (jj_2_7(2147483647)) {
        lhs = FilterExpr();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case SLASH:
        case SLASHSLASH:
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case SLASH:
            jj_consume_token(SLASH);
            RelativeLocationPath(path);
                                            lhs = createChildExtension( lhs, path);
            break;
          case SLASHSLASH:
            jj_consume_token(SLASHSLASH);
            RelativeLocationPath(path);
                                                 lhs = createDescendantExtension( lhs, path);
            break;
          default:
            jj_la1[32] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          break;
        default:
          jj_la1[33] = jj_gen;
          ;
        }
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ABBREVIATED_ATTRIBUTE_AXIS:
        case STAR:
        case OR:
        case IN:
        case IF:
        case FOR:
        case AND:
        case DIV:
        case MOD:
        case THEN:
        case ELSE:
        case AXIS_SELF:
        case NT_TEXT:
        case NT_NODE:
        case AXIS_CHILD:
        case RETURN:
        case AXIS_NESTED:
        case AXIS_PARENT:
        case AXIS_ANCESTOR:
        case NT_COMMENT:
        case AXIS_FOLLOWING:
        case AXIS_PRECEDING:
        case AXIS_ATTRIBUTE:
        case AXIS_NAMESPACE:
        case AXIS_DESCENDANT:
        case AXIS_NESTED_OR_SELF:
        case AXIS_ANCESTOR_OR_SELF:
        case AXIS_FOLLOWING_SIBLING:
        case AXIS_PRECEDING_SIBLING:
        case AXIS_DESCENDANT_OR_SELF:
        case NT_PI:
        case NCName:
        case 54:
        case 55:
          RelativeLocationPath(path);
                                  lhs = new PathExpression( path);
          break;
        default:
          jj_la1[35] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
    {if (true) return lhs;}
    throw new Error("Missing return statement in function");
  }

  final public String QName() throws ParseException {
  String lhs = null;
  String rhs = null;
    lhs = NCName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 58:
      jj_consume_token(58);
      rhs = NCName();
      break;
    default:
      jj_la1[36] = jj_gen;
      ;
    }
    if( rhs != null)
    {
      {if (true) return lhs+":"+rhs;}
    }
    else
    {
      {if (true) return lhs;}
    }
    throw new Error("Missing return statement in function");
  }

  final public String QNameWithoutNodeType() throws ParseException {
  String prefix = null;
  String local;
    if (jj_2_8(2147483647)) {
      prefix = NCName();
      jj_consume_token(58);
      local = NCName();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
      case IN:
      case IF:
      case FOR:
      case AND:
      case DIV:
      case MOD:
      case THEN:
      case ELSE:
      case AXIS_SELF:
      case AXIS_CHILD:
      case RETURN:
      case AXIS_NESTED:
      case AXIS_PARENT:
      case AXIS_ANCESTOR:
      case AXIS_FOLLOWING:
      case AXIS_PRECEDING:
      case AXIS_ATTRIBUTE:
      case AXIS_NAMESPACE:
      case AXIS_DESCENDANT:
      case AXIS_NESTED_OR_SELF:
      case AXIS_ANCESTOR_OR_SELF:
      case AXIS_FOLLOWING_SIBLING:
      case AXIS_PRECEDING_SIBLING:
      case AXIS_DESCENDANT_OR_SELF:
      case NCName:
        local = NCNameWithoutNodeType();
        break;
      default:
        jj_la1[37] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    if ( prefix == null) {if (true) return local;}
    {if (true) return prefix+":"+local;}
    throw new Error("Missing return statement in function");
  }

  final public String NCName() throws ParseException {
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NCName:
      t = jj_consume_token(NCName);
      break;
    case IF:
      t = jj_consume_token(IF);
      break;
    case ELSE:
      t = jj_consume_token(ELSE);
      break;
    case THEN:
      t = jj_consume_token(THEN);
      break;
    case FOR:
      t = jj_consume_token(FOR);
      break;
    case IN:
      t = jj_consume_token(IN);
      break;
    case RETURN:
      t = jj_consume_token(RETURN);
      break;
    case OR:
      t = jj_consume_token(OR);
      break;
    case AND:
      t = jj_consume_token(AND);
      break;
    case DIV:
      t = jj_consume_token(DIV);
      break;
    case MOD:
      t = jj_consume_token(MOD);
      break;
    case AXIS_ANCESTOR:
      t = jj_consume_token(AXIS_ANCESTOR);
      break;
    case AXIS_ANCESTOR_OR_SELF:
      t = jj_consume_token(AXIS_ANCESTOR_OR_SELF);
      break;
    case AXIS_ATTRIBUTE:
      t = jj_consume_token(AXIS_ATTRIBUTE);
      break;
    case AXIS_CHILD:
      t = jj_consume_token(AXIS_CHILD);
      break;
    case AXIS_DESCENDANT:
      t = jj_consume_token(AXIS_DESCENDANT);
      break;
    case AXIS_DESCENDANT_OR_SELF:
      t = jj_consume_token(AXIS_DESCENDANT_OR_SELF);
      break;
    case AXIS_NESTED:
      t = jj_consume_token(AXIS_NESTED);
      break;
    case AXIS_NESTED_OR_SELF:
      t = jj_consume_token(AXIS_NESTED_OR_SELF);
      break;
    case AXIS_FOLLOWING:
      t = jj_consume_token(AXIS_FOLLOWING);
      break;
    case AXIS_FOLLOWING_SIBLING:
      t = jj_consume_token(AXIS_FOLLOWING_SIBLING);
      break;
    case AXIS_NAMESPACE:
      t = jj_consume_token(AXIS_NAMESPACE);
      break;
    case AXIS_PARENT:
      t = jj_consume_token(AXIS_PARENT);
      break;
    case AXIS_PRECEDING:
      t = jj_consume_token(AXIS_PRECEDING);
      break;
    case AXIS_PRECEDING_SIBLING:
      t = jj_consume_token(AXIS_PRECEDING_SIBLING);
      break;
    case AXIS_SELF:
      t = jj_consume_token(AXIS_SELF);
      break;
    case NT_COMMENT:
      t = jj_consume_token(NT_COMMENT);
      break;
    case NT_TEXT:
      t = jj_consume_token(NT_TEXT);
      break;
    case NT_PI:
      t = jj_consume_token(NT_PI);
      break;
    case NT_NODE:
      t = jj_consume_token(NT_NODE);
      break;
    default:
      jj_la1[38] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

/* used for QName - NCName */
  final public String NCNameWithoutNodeType() throws ParseException {
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NCName:
      t = jj_consume_token(NCName);
      break;
    case IF:
      t = jj_consume_token(IF);
      break;
    case ELSE:
      t = jj_consume_token(ELSE);
      break;
    case THEN:
      t = jj_consume_token(THEN);
      break;
    case FOR:
      t = jj_consume_token(FOR);
      break;
    case IN:
      t = jj_consume_token(IN);
      break;
    case RETURN:
      t = jj_consume_token(RETURN);
      break;
    case OR:
      t = jj_consume_token(OR);
      break;
    case AND:
      t = jj_consume_token(AND);
      break;
    case DIV:
      t = jj_consume_token(DIV);
      break;
    case MOD:
      t = jj_consume_token(MOD);
      break;
    case AXIS_ANCESTOR:
      t = jj_consume_token(AXIS_ANCESTOR);
      break;
    case AXIS_ANCESTOR_OR_SELF:
      t = jj_consume_token(AXIS_ANCESTOR_OR_SELF);
      break;
    case AXIS_ATTRIBUTE:
      t = jj_consume_token(AXIS_ATTRIBUTE);
      break;
    case AXIS_CHILD:
      t = jj_consume_token(AXIS_CHILD);
      break;
    case AXIS_DESCENDANT:
      t = jj_consume_token(AXIS_DESCENDANT);
      break;
    case AXIS_DESCENDANT_OR_SELF:
      t = jj_consume_token(AXIS_DESCENDANT_OR_SELF);
      break;
    case AXIS_NESTED:
      t = jj_consume_token(AXIS_NESTED);
      break;
    case AXIS_NESTED_OR_SELF:
      t = jj_consume_token(AXIS_NESTED_OR_SELF);
      break;
    case AXIS_FOLLOWING:
      t = jj_consume_token(AXIS_FOLLOWING);
      break;
    case AXIS_FOLLOWING_SIBLING:
      t = jj_consume_token(AXIS_FOLLOWING_SIBLING);
      break;
    case AXIS_NAMESPACE:
      t = jj_consume_token(AXIS_NAMESPACE);
      break;
    case AXIS_PARENT:
      t = jj_consume_token(AXIS_PARENT);
      break;
    case AXIS_PRECEDING:
      t = jj_consume_token(AXIS_PRECEDING);
      break;
    case AXIS_PRECEDING_SIBLING:
      t = jj_consume_token(AXIS_PRECEDING_SIBLING);
      break;
    case AXIS_SELF:
      t = jj_consume_token(AXIS_SELF);
      break;
    default:
      jj_la1[39] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  final private boolean jj_3R_23() {
    if (jj_scan_token(53)) return true;
    if (jj_scan_token(NCName)) return true;
    return false;
  }

  final private boolean jj_3R_18() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) {
    jj_scanpos = xsp;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) return true;
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_97() {
    if (jj_3R_18()) return true;
    if (jj_3R_101()) return true;
    return false;
  }

  final private boolean jj_3R_31() {
    if (jj_scan_token(NT_PI)) return true;
    return false;
  }

  final private boolean jj_3R_20() {
    if (jj_3R_31()) return true;
    return false;
  }

  final private boolean jj_3R_92() {
    if (jj_scan_token(UNION)) return true;
    if (jj_3R_91()) return true;
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_3R_14()) return true;
    if (jj_scan_token(58)) return true;
    if (jj_scan_token(STAR)) return true;
    return false;
  }

  final private boolean jj_3R_90() {
    if (jj_3R_91()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_92()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(31)) {
    jj_scanpos = xsp;
    if (jj_scan_token(32)) {
    jj_scanpos = xsp;
    if (jj_3R_20()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_86() {
    if (jj_3R_90()) return true;
    return false;
  }

  final private boolean jj_3R_115() {
    if (jj_3R_116()) return true;
    return false;
  }

  final private boolean jj_3R_114() {
    if (jj_3R_14()) return true;
    if (jj_scan_token(58)) return true;
    if (jj_scan_token(STAR)) return true;
    return false;
  }

  final private boolean jj_3R_85() {
    if (jj_scan_token(MINUS)) return true;
    if (jj_3R_81()) return true;
    return false;
  }

  final private boolean jj_3R_81() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_85()) {
    jj_scanpos = xsp;
    if (jj_3R_86()) return true;
    }
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_15()) return true;
    if (jj_scan_token(56)) return true;
    if (jj_scan_token(57)) return true;
    return false;
  }

  final private boolean jj_3R_113() {
    if (jj_scan_token(STAR)) return true;
    return false;
  }

  final private boolean jj_3R_112() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_113()) {
    jj_scanpos = xsp;
    if (jj_3R_114()) {
    jj_scanpos = xsp;
    if (jj_3R_115()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_111() {
    if (jj_3R_112()) return true;
    return false;
  }

  final private boolean jj_3R_89() {
    if (jj_scan_token(STAR)) return true;
    if (jj_3R_81()) return true;
    return false;
  }

  final private boolean jj_3R_88() {
    if (jj_scan_token(MOD)) return true;
    if (jj_3R_81()) return true;
    return false;
  }

  final private boolean jj_3R_110() {
    if (jj_3R_15()) return true;
    if (jj_scan_token(56)) return true;
    if (jj_scan_token(57)) return true;
    return false;
  }

  final private boolean jj_3R_109() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_110()) {
    jj_scanpos = xsp;
    if (jj_3R_111()) return true;
    }
    return false;
  }

  final private boolean jj_3R_87() {
    if (jj_scan_token(DIV)) return true;
    if (jj_3R_81()) return true;
    return false;
  }

  final private boolean jj_3R_82() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_87()) {
    jj_scanpos = xsp;
    if (jj_3R_88()) {
    jj_scanpos = xsp;
    if (jj_3R_89()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_75() {
    if (jj_3R_81()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_82()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_70() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(50)) {
    jj_scanpos = xsp;
    if (jj_scan_token(22)) {
    jj_scanpos = xsp;
    if (jj_scan_token(29)) {
    jj_scanpos = xsp;
    if (jj_scan_token(28)) {
    jj_scanpos = xsp;
    if (jj_scan_token(23)) {
    jj_scanpos = xsp;
    if (jj_scan_token(21)) {
    jj_scanpos = xsp;
    if (jj_scan_token(34)) {
    jj_scanpos = xsp;
    if (jj_scan_token(20)) {
    jj_scanpos = xsp;
    if (jj_scan_token(25)) {
    jj_scanpos = xsp;
    if (jj_scan_token(26)) {
    jj_scanpos = xsp;
    if (jj_scan_token(27)) {
    jj_scanpos = xsp;
    if (jj_scan_token(37)) {
    jj_scanpos = xsp;
    if (jj_scan_token(45)) {
    jj_scanpos = xsp;
    if (jj_scan_token(41)) {
    jj_scanpos = xsp;
    if (jj_scan_token(33)) {
    jj_scanpos = xsp;
    if (jj_scan_token(43)) {
    jj_scanpos = xsp;
    if (jj_scan_token(48)) {
    jj_scanpos = xsp;
    if (jj_scan_token(35)) {
    jj_scanpos = xsp;
    if (jj_scan_token(44)) {
    jj_scanpos = xsp;
    if (jj_scan_token(39)) {
    jj_scanpos = xsp;
    if (jj_scan_token(46)) {
    jj_scanpos = xsp;
    if (jj_scan_token(42)) {
    jj_scanpos = xsp;
    if (jj_scan_token(36)) {
    jj_scanpos = xsp;
    if (jj_scan_token(40)) {
    jj_scanpos = xsp;
    if (jj_scan_token(47)) {
    jj_scanpos = xsp;
    if (jj_scan_token(30)) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_67() {
    if (jj_scan_token(AXIS_SELF)) return true;
    return false;
  }

  final private boolean jj_3R_66() {
    if (jj_scan_token(AXIS_PRECEDING_SIBLING)) return true;
    return false;
  }

  final private boolean jj_3R_65() {
    if (jj_scan_token(AXIS_PRECEDING)) return true;
    return false;
  }

  final private boolean jj_3R_64() {
    if (jj_scan_token(AXIS_PARENT)) return true;
    return false;
  }

  final private boolean jj_3R_63() {
    if (jj_scan_token(AXIS_NAMESPACE)) return true;
    return false;
  }

  final private boolean jj_3R_62() {
    if (jj_scan_token(AXIS_FOLLOWING_SIBLING)) return true;
    return false;
  }

  final private boolean jj_3R_84() {
    if (jj_scan_token(MINUS)) return true;
    if (jj_3R_75()) return true;
    return false;
  }

  final private boolean jj_3R_61() {
    if (jj_scan_token(AXIS_FOLLOWING)) return true;
    return false;
  }

  final private boolean jj_3R_83() {
    if (jj_scan_token(PLUS)) return true;
    if (jj_3R_75()) return true;
    return false;
  }

  final private boolean jj_3R_76() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_83()) {
    jj_scanpos = xsp;
    if (jj_3R_84()) return true;
    }
    return false;
  }

  final private boolean jj_3R_60() {
    if (jj_scan_token(AXIS_NESTED_OR_SELF)) return true;
    return false;
  }

  final private boolean jj_3R_59() {
    if (jj_scan_token(AXIS_NESTED)) return true;
    return false;
  }

  final private boolean jj_3R_58() {
    if (jj_scan_token(AXIS_DESCENDANT_OR_SELF)) return true;
    return false;
  }

  final private boolean jj_3R_57() {
    if (jj_scan_token(AXIS_DESCENDANT)) return true;
    return false;
  }

  final private boolean jj_3R_71() {
    if (jj_3R_75()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_76()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_56() {
    if (jj_scan_token(AXIS_CHILD)) return true;
    return false;
  }

  final private boolean jj_3R_55() {
    if (jj_scan_token(AXIS_ATTRIBUTE)) return true;
    return false;
  }

  final private boolean jj_3R_54() {
    if (jj_scan_token(AXIS_ANCESTOR_OR_SELF)) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_14()) return true;
    if (jj_scan_token(DOTDOT)) return true;
    return false;
  }

  final private boolean jj_3R_53() {
    if (jj_scan_token(AXIS_ANCESTOR)) return true;
    return false;
  }

  final private boolean jj_3R_47() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_53()) {
    jj_scanpos = xsp;
    if (jj_3R_54()) {
    jj_scanpos = xsp;
    if (jj_3R_55()) {
    jj_scanpos = xsp;
    if (jj_3R_56()) {
    jj_scanpos = xsp;
    if (jj_3R_57()) {
    jj_scanpos = xsp;
    if (jj_3R_58()) {
    jj_scanpos = xsp;
    if (jj_3R_59()) {
    jj_scanpos = xsp;
    if (jj_3R_60()) {
    jj_scanpos = xsp;
    if (jj_3R_61()) {
    jj_scanpos = xsp;
    if (jj_3R_62()) {
    jj_scanpos = xsp;
    if (jj_3R_63()) {
    jj_scanpos = xsp;
    if (jj_3R_64()) {
    jj_scanpos = xsp;
    if (jj_3R_65()) {
    jj_scanpos = xsp;
    if (jj_3R_66()) {
    jj_scanpos = xsp;
    if (jj_3R_67()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    if (jj_scan_token(DOTDOT)) return true;
    return false;
  }

  final private boolean jj_3R_80() {
    if (jj_scan_token(GE)) return true;
    if (jj_3R_71()) return true;
    return false;
  }

  final private boolean jj_3R_79() {
    if (jj_scan_token(LE)) return true;
    if (jj_3R_71()) return true;
    return false;
  }

  final private boolean jj_3R_42() {
    if (jj_3R_48()) return true;
    return false;
  }

  final private boolean jj_3R_78() {
    if (jj_scan_token(GT)) return true;
    if (jj_3R_71()) return true;
    return false;
  }

  final private boolean jj_3R_77() {
    if (jj_scan_token(LT)) return true;
    if (jj_3R_71()) return true;
    return false;
  }

  final private boolean jj_3R_72() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_77()) {
    jj_scanpos = xsp;
    if (jj_3R_78()) {
    jj_scanpos = xsp;
    if (jj_3R_79()) {
    jj_scanpos = xsp;
    if (jj_3R_80()) return true;
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_41() {
    if (jj_3R_47()) return true;
    return false;
  }

  final private boolean jj_3R_68() {
    if (jj_3R_71()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_72()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_36() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_40()) {
    jj_scanpos = xsp;
    if (jj_3R_41()) {
    jj_scanpos = xsp;
    if (jj_3R_42()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_40() {
    if (jj_scan_token(ABBREVIATED_ATTRIBUTE_AXIS)) return true;
    return false;
  }

  final private boolean jj_3R_30() {
    if (jj_scan_token(55)) return true;
    if (jj_3R_101()) return true;
    return false;
  }

  final private boolean jj_3R_29() {
    if (jj_scan_token(54)) return true;
    if (jj_3R_101()) return true;
    return false;
  }

  final private boolean jj_3R_74() {
    if (jj_scan_token(NEQ)) return true;
    if (jj_3R_68()) return true;
    return false;
  }

  final private boolean jj_3R_69() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_73()) {
    jj_scanpos = xsp;
    if (jj_3R_74()) return true;
    }
    return false;
  }

  final private boolean jj_3R_73() {
    if (jj_scan_token(EQ)) return true;
    if (jj_3R_68()) return true;
    return false;
  }

  final private boolean jj_3R_28() {
    if (jj_3R_36()) return true;
    if (jj_3R_109()) return true;
    if (jj_3R_101()) return true;
    return false;
  }

  final private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_28()) {
    jj_scanpos = xsp;
    if (jj_3R_29()) {
    jj_scanpos = xsp;
    if (jj_3R_30()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_49() {
    if (jj_3R_68()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_69()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_3R_14()) return true;
    if (jj_scan_token(58)) return true;
    return false;
  }

  final private boolean jj_3R_14() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(50)) {
    jj_scanpos = xsp;
    if (jj_scan_token(22)) {
    jj_scanpos = xsp;
    if (jj_scan_token(29)) {
    jj_scanpos = xsp;
    if (jj_scan_token(28)) {
    jj_scanpos = xsp;
    if (jj_scan_token(23)) {
    jj_scanpos = xsp;
    if (jj_scan_token(21)) {
    jj_scanpos = xsp;
    if (jj_scan_token(34)) {
    jj_scanpos = xsp;
    if (jj_scan_token(20)) {
    jj_scanpos = xsp;
    if (jj_scan_token(25)) {
    jj_scanpos = xsp;
    if (jj_scan_token(26)) {
    jj_scanpos = xsp;
    if (jj_scan_token(27)) {
    jj_scanpos = xsp;
    if (jj_scan_token(37)) {
    jj_scanpos = xsp;
    if (jj_scan_token(45)) {
    jj_scanpos = xsp;
    if (jj_scan_token(41)) {
    jj_scanpos = xsp;
    if (jj_scan_token(33)) {
    jj_scanpos = xsp;
    if (jj_scan_token(43)) {
    jj_scanpos = xsp;
    if (jj_scan_token(48)) {
    jj_scanpos = xsp;
    if (jj_scan_token(35)) {
    jj_scanpos = xsp;
    if (jj_scan_token(44)) {
    jj_scanpos = xsp;
    if (jj_scan_token(39)) {
    jj_scanpos = xsp;
    if (jj_scan_token(46)) {
    jj_scanpos = xsp;
    if (jj_scan_token(42)) {
    jj_scanpos = xsp;
    if (jj_scan_token(36)) {
    jj_scanpos = xsp;
    if (jj_scan_token(40)) {
    jj_scanpos = xsp;
    if (jj_scan_token(47)) {
    jj_scanpos = xsp;
    if (jj_scan_token(30)) {
    jj_scanpos = xsp;
    if (jj_scan_token(38)) {
    jj_scanpos = xsp;
    if (jj_scan_token(31)) {
    jj_scanpos = xsp;
    if (jj_scan_token(49)) {
    jj_scanpos = xsp;
    if (jj_scan_token(32)) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_52() {
    if (jj_3R_70()) return true;
    return false;
  }

  final private boolean jj_3R_50() {
    if (jj_scan_token(AND)) return true;
    if (jj_3R_49()) return true;
    return false;
  }

  final private boolean jj_3R_51() {
    if (jj_3R_14()) return true;
    if (jj_scan_token(58)) return true;
    if (jj_3R_14()) return true;
    return false;
  }

  final private boolean jj_3R_107() {
    if (jj_scan_token(SLASHSLASH)) return true;
    if (jj_3R_19()) return true;
    return false;
  }

  final private boolean jj_3R_43() {
    if (jj_3R_49()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_50()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_106() {
    if (jj_scan_token(SLASH)) return true;
    if (jj_3R_19()) return true;
    return false;
  }

  final private boolean jj_3R_104() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_106()) {
    jj_scanpos = xsp;
    if (jj_3R_107()) return true;
    }
    return false;
  }

  final private boolean jj_3R_45() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_51()) {
    jj_scanpos = xsp;
    if (jj_3R_52()) return true;
    }
    return false;
  }

  final private boolean jj_3R_117() {
    if (jj_scan_token(58)) return true;
    if (jj_3R_14()) return true;
    return false;
  }

  final private boolean jj_3R_13() {
    if (jj_3R_19()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_104()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_100() {
    if (jj_scan_token(SLASH)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(SLASH)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_44() {
    if (jj_scan_token(OR)) return true;
    if (jj_3R_43()) return true;
    return false;
  }

  final private boolean jj_3R_99() {
    if (jj_scan_token(SLASHSLASH)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_37() {
    if (jj_3R_43()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_44()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_96() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_99()) {
    jj_scanpos = xsp;
    if (jj_3_1()) {
    jj_scanpos = xsp;
    if (jj_3R_100()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_116() {
    if (jj_3R_14()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_117()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_3R_18()) return true;
    return false;
  }

  final private boolean jj_3R_95() {
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_103() {
    if (jj_scan_token(SLASHSLASH)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_17() {
    if (jj_scan_token(IF)) return true;
    if (jj_3R_21()) return true;
    if (jj_scan_token(THEN)) return true;
    if (jj_3R_21()) return true;
    if (jj_scan_token(ELSE)) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  final private boolean jj_3R_102() {
    if (jj_scan_token(SLASH)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_98() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_102()) {
    jj_scanpos = xsp;
    if (jj_3R_103()) return true;
    }
    return false;
  }

  final private boolean jj_3R_94() {
    if (jj_3R_97()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_98()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3R_93() {
    if (jj_3R_96()) return true;
    return false;
  }

  final private boolean jj_3R_91() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_93()) {
    jj_scanpos = xsp;
    if (jj_3R_94()) {
    jj_scanpos = xsp;
    if (jj_3R_95()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_38() {
    if (jj_3R_45()) return true;
    return false;
  }

  final private boolean jj_3R_46() {
    if (jj_scan_token(59)) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  final private boolean jj_3R_22() {
    if (jj_scan_token(59)) return true;
    if (jj_scan_token(53)) return true;
    if (jj_scan_token(NCName)) return true;
    if (jj_scan_token(IN)) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  final private boolean jj_3R_39() {
    if (jj_3R_21()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_46()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3_6() {
    if (jj_3R_17()) return true;
    return false;
  }

  final private boolean jj_3R_35() {
    if (jj_3R_38()) return true;
    if (jj_scan_token(56)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_39()) jj_scanpos = xsp;
    if (jj_scan_token(57)) return true;
    return false;
  }

  final private boolean jj_3R_16() {
    if (jj_scan_token(FOR)) return true;
    if (jj_scan_token(53)) return true;
    if (jj_scan_token(NCName)) return true;
    if (jj_scan_token(IN)) return true;
    if (jj_3R_21()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_22()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(RETURN)) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_3R_16()) return true;
    return false;
  }

  final private boolean jj_3R_108() {
    if (jj_scan_token(60)) return true;
    if (jj_3R_21()) return true;
    if (jj_scan_token(61)) return true;
    return false;
  }

  final private boolean jj_3R_34() {
    if (jj_3R_37()) return true;
    return false;
  }

  final private boolean jj_3R_33() {
    if (jj_3R_17()) return true;
    return false;
  }

  final private boolean jj_3R_48() {
    return false;
  }

  final private boolean jj_3R_32() {
    if (jj_3R_16()) return true;
    return false;
  }

  final private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_32()) {
    jj_scanpos = xsp;
    if (jj_3R_33()) {
    jj_scanpos = xsp;
    if (jj_3R_34()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_27() {
    if (jj_3R_35()) return true;
    return false;
  }

  final private boolean jj_3R_26() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  final private boolean jj_3R_25() {
    if (jj_scan_token(LITERAL)) return true;
    return false;
  }

  final private boolean jj_3R_105() {
    if (jj_3R_108()) return true;
    return false;
  }

  final private boolean jj_3R_24() {
    if (jj_scan_token(56)) return true;
    if (jj_3R_21()) return true;
    if (jj_scan_token(57)) return true;
    return false;
  }

  final private boolean jj_3R_101() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_105()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  public XPathParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[40];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xfef04860,0x1000000,0x0,0x4000,0x40,0x4040,0x4040,0xfef00820,0x20,0x40000000,0xfef00800,0x800,0xfef00000,0x80000000,0xfef00014,0xfef05874,0x0,0x100000,0x2000000,0x8080,0x8080,0x30300,0x30300,0x1400,0x1400,0xc000800,0xc000800,0xfef05874,0x2000,0x0,0x0,0xfef05874,0x4040,0x4040,0x4040,0xfef00820,0x0,0x7ef00000,0xfef00000,0x7ef00000,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0xc7ffff,0x0,0x100000,0x0,0x0,0x0,0x0,0xc7ffff,0x0,0x1ffba,0x7ffff,0x0,0x7ffff,0x20001,0x127ffff,0x1e7ffff,0x8000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1e7ffff,0x0,0x10000000,0x8000000,0x1e7ffff,0x0,0x0,0x0,0xc7ffff,0x4000000,0x5ffbe,0x7ffff,0x5ffbe,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[8];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public XPathParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public XPathParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new XPathParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public XPathParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new XPathParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public XPathParser(XPathParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(XPathParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 40; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[62];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 40; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 62; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 8; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
