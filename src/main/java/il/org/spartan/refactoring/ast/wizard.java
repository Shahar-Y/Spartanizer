package il.org.spartan.refactoring.ast;

import static il.org.spartan.Utils.*;
import static org.eclipse.jdt.core.dom.ASTNode.*;
import static org.eclipse.jdt.core.dom.Assignment.Operator.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import org.eclipse.jdt.core.dom.rewrite.*;

import il.org.spartan.refactoring.assemble.*;
import il.org.spartan.refactoring.utils.*;

/** Collection of definitions and functions that capture some of the quircks of
 * the {@link ASTNode} hierarchy.
 * @author Yossi Gil
 * @since 2014 */
public interface wizard {
  @SuppressWarnings("serial") Map<Operator, Operator> conjugate = new HashMap<Operator, Operator>() {
    {
      put(GREATER, LESS);
      put(LESS, GREATER);
      put(GREATER_EQUALS, LESS_EQUALS);
      put(LESS_EQUALS, GREATER_EQUALS);
    }
  };
  PostfixExpression.Operator DECREMENT_POST = PostfixExpression.Operator.DECREMENT;
  PrefixExpression.Operator DECREMENT_PRE = PrefixExpression.Operator.DECREMENT;
  PostfixExpression.Operator INCREMENT_POST = PostfixExpression.Operator.INCREMENT;
  PrefixExpression.Operator INCREMENT_PRE = PrefixExpression.Operator.INCREMENT;
  /** This list was generated by manually editing the original list at
   * {@link Assignment.Operator}. */
  @SuppressWarnings("serial") static final Map<Operator, Assignment.Operator> infix = new HashMap<Operator, Assignment.Operator>() {
    {
      put(PLUS, PLUS_ASSIGN);
      put(MINUS, MINUS_ASSIGN);
      put(TIMES, TIMES_ASSIGN);
      put(DIVIDE, DIVIDE_ASSIGN);
      put(AND, BIT_AND_ASSIGN);
      put(OR, BIT_OR_ASSIGN);
      put(XOR, BIT_XOR_ASSIGN);
      put(REMAINDER, REMAINDER_ASSIGN);
      put(LEFT_SHIFT, LEFT_SHIFT_ASSIGN);
      put(RIGHT_SHIFT_SIGNED, RIGHT_SHIFT_SIGNED_ASSIGN);
      put(RIGHT_SHIFT_UNSIGNED, RIGHT_SHIFT_UNSIGNED_ASSIGN);
    }
  };
  PrefixExpression.Operator MINUS1 = PrefixExpression.Operator.MINUS;
  InfixExpression.Operator MINUS2 = InfixExpression.Operator.MINUS;
  PrefixExpression.Operator PLUS1 = PrefixExpression.Operator.PLUS;
  InfixExpression.Operator PLUS2 = InfixExpression.Operator.PLUS;

  static InfixExpression.Operator assignmentToInfix(final Assignment.Operator o) {
    return o == PLUS_ASSIGN ? InfixExpression.Operator.PLUS
        : o == MINUS_ASSIGN ? MINUS
            : o == TIMES_ASSIGN ? TIMES
                : o == DIVIDE_ASSIGN ? DIVIDE
                    : o == BIT_AND_ASSIGN ? AND
                        : o == BIT_OR_ASSIGN ? OR
                            : o == BIT_XOR_ASSIGN ? XOR
                                : o == REMAINDER_ASSIGN ? REMAINDER
                                    : o == LEFT_SHIFT_ASSIGN ? LEFT_SHIFT
                                        : o == RIGHT_SHIFT_SIGNED_ASSIGN ? RIGHT_SHIFT_SIGNED
                                            : o == RIGHT_SHIFT_UNSIGNED_ASSIGN ? RIGHT_SHIFT_UNSIGNED : null;
  }

  /** Obtain a condensed textual representation of an {@link ASTNode}
   * @param ¢ JD
   * @return textual representation of the parameter, */
  static String asString(final ASTNode ¢) {
    return removeWhites(wizard.body(¢));
  }

  static String body(final ASTNode ¢) {
    return tide.clean("" + ¢);
  }

  /** the function checks if all the given assignments have the same left hand
   * side(variable) and operator
   * @param base The assignment to compare all others to
   * @param as The assignments to compare
   * @return true if all assignments has the same left hand side and operator as
   *         the first one or false otherwise */
  static boolean compatible(final Assignment base, final Assignment... as) {
    if (hasNull(base, as))
      return false;
    for (final Assignment a : as)
      if (wizard.incompatible(base, a))
        return false;
    return true;
  }

  /** @param o the assignment operator to compare all to
   * @param os A unknown number of assignments operators
   * @return true if all the operator are the same or false otherwise */
  static boolean compatibleOps(final Assignment.Operator o, final Assignment.Operator... os) {
    if (hasNull(o, os))
      return false;
    for (final Assignment.Operator ¢ : os)
      if (¢ == null || ¢ != o)
        return false;
    return true;
  }

  /** Makes an opposite operator from a given one, which keeps its logical
   * operation after the node swapping. ¢.¢. "&" is commutative, therefore no
   * change needed. "<" isn'¢ commutative, but it has its opposite: ">=".
   * @param ¢ The operator to flip
   * @return correspond operator - ¢.¢. "<=" will become ">", "+" will stay
   *         "+". */
  static Operator conjugate(final Operator ¢) {
    return !wizard.conjugate.containsKey(¢) ? ¢ : wizard.conjugate.get(¢);
  }

  /** @param ns unknown number of nodes to check
   * @return true if one of the nodes is an Expression Statement of type Post or
   *         Pre Expression with ++ or -- operator. false if none of them are or
   *         if the given parameter is null. */
  static boolean containIncOrDecExp(final ASTNode... ns) {
    if (ns == null)
      return false;
    for (final ASTNode ¢ : ns)
      if (¢ != null && iz.incrementOrDecrement(¢))
        return true;
    return false;
  }

  /** Compute the "de Morgan" conjugate of the operator present on an
   * {@link InfixExpression}.
   * @param x an expression whose operator is either
   *        {@link Operator#CONDITIONAL_AND} or {@link Operator#CONDITIONAL_OR}
   * @return {@link Operator#CONDITIONAL_AND} if the operator present on the
   *         parameter is {@link Operator#CONDITIONAL_OR}, or
   *         {@link Operator#CONDITIONAL_OR} if this operator is
   *         {@link Operator#CONDITIONAL_AND}
   * @see duplicate#deMorgan(Operator) */
  static Operator deMorgan(final InfixExpression x) {
    return wizard.deMorgan(x.getOperator());
  }

  /** Compute the "de Morgan" conjugate of an operator.
   * @param o must be either {@link Operator#CONDITIONAL_AND} or
   *        {@link Operator#CONDITIONAL_OR}
   * @return {@link Operator#CONDITIONAL_AND} if the parameter is
   *         {@link Operator#CONDITIONAL_OR} , or
   *         {@link Operator#CONDITIONAL_OR} if the parameter is
   *         {@link Operator#CONDITIONAL_AND}
   * @see wizard#deMorgan(InfixExpression) */
  static Operator deMorgan(final Operator o) {
    assert iz.deMorgan(o);
    return o.equals(CONDITIONAL_AND) ? CONDITIONAL_OR : CONDITIONAL_AND;
  }

  /** Find the first matching expression to the given boolean (b).
   * @param b JD,
   * @param xs JD
   * @return first expression from the given list (es) whose boolean value
   *         matches to the given boolean (b). */
  static Expression find(final boolean b, final List<Expression> xs) {
    for (final Expression $ : xs)
      if (iz.booleanLiteral($) && b == az.booleanLiteral($).booleanValue())
        return $;
    return null;
  }

  static boolean incompatible(final Assignment a1, final Assignment a2) {
    return hasNull(a1, a2) || !compatibleOps(a1.getOperator(), a2.getOperator()) || !wizard.same(step.left(a1), step.left(a2));
  }

  // TODO: Alex: please convert this code into table driven. It is much easier
  // to maintain. The table is already in this file.
  static Assignment.Operator InfixToAssignment(final InfixExpression.Operator o) {
    return o == PLUS ? Assignment.Operator.PLUS_ASSIGN
        : o == MINUS ? MINUS_ASSIGN
            : o == TIMES ? TIMES_ASSIGN
                : o == DIVIDE ? DIVIDE_ASSIGN
                    : o == AND ? BIT_AND_ASSIGN
                        : o == OR ? BIT_OR_ASSIGN
                            : o == XOR ? BIT_XOR_ASSIGN
                                : o == REMAINDER ? REMAINDER_ASSIGN
                                    : o == LEFT_SHIFT ? LEFT_SHIFT_ASSIGN
                                        : o == RIGHT_SHIFT_SIGNED ? RIGHT_SHIFT_SIGNED_ASSIGN
                                            : o == RIGHT_SHIFT_UNSIGNED ? RIGHT_SHIFT_UNSIGNED_ASSIGN : null;
  }

  /** Determine whether an InfixExpression.Operator is a comparison operator or
   * not
   * @param o JD
   * @return true if one of {@link #InfixExpression.Operator.XOR},
   *         {@link #InfixExpression.Operator.OR},
   *         {@link #InfixExpression.Operator.AND}, and false otherwise */
  static boolean isBitwiseOperator(final InfixExpression.Operator o) {
    return in(o, XOR, OR, AND);
  }

  /** Determine whether an InfixExpression.Operator is a comparison operator or
   * not
   * @param o JD
   * @return true if one of {@link #InfixExpression.Operator.LESS},
   *         {@link #InfixExpression.Operator.GREATER},
   *         {@link #InfixExpression.Operator.LESS_EQUALS},
   *         {@link #InfixExpression.Operator.GREATER_EQUALS},
   *         {@link #InfixExpression.Operator.EQUALS},
   *         {@link #InfixExpression.Operator.NOT_EQUALS},
   *         {@link #InfixExpression.Operator.CONDITIONAL_OR},
   *         {@link #InfixExpression.Operator.CONDITIONAL_AND} and false
   *         otherwise */
  static boolean isComparison(final InfixExpression.Operator o) {
    return in(o, LESS, GREATER, LESS_EQUALS, GREATER_EQUALS, EQUALS, //
        NOT_EQUALS, CONDITIONAL_OR, CONDITIONAL_AND);
  }

  /** Determine whether an InfixExpression.Operator is a shift operator or not
   * @param o JD
   * @return true if one of {@link #InfixExpression.Operator.LEFT_SHIFT},
   *         {@link #InfixExpression.Operator.RIGHT_SHIFT_SIGNED},
   *         {@link #InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED} and false
   *         otherwise */
  static boolean isShift(final InfixExpression.Operator o) {
    return in(o, LEFT_SHIFT, RIGHT_SHIFT_SIGNED, RIGHT_SHIFT_UNSIGNED);
  }

  /** Determine whether a node is an infix expression whose operator is
   * non-associative.
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the parameter is a node which
   *         is an infix expression whose operator is */
  static boolean nonAssociative(final ASTNode n) {
    return nonAssociative(az.infixExpression(n));
  }

  static boolean nonAssociative(final InfixExpression x) {
    return x != null && in(x.getOperator(), MINUS, DIVIDE, REMAINDER, LEFT_SHIFT, RIGHT_SHIFT_SIGNED, RIGHT_SHIFT_UNSIGNED);
  }

  /** Parenthesize an expression (if necessary).
   * @param x JD
   * @return a
   *         {@link il.org.spartan.refactoring.assemble.duplicate#duplicate(Expression)}
   *         of the parameter wrapped in parenthesis. */
  static Expression parenthesize(final Expression x) {
    return iz.noParenthesisRequired(x) ? duplicate.of(x) : make.parethesized(x);
  }

  static ASTParser parser(final int kind) {
    final ASTParser $ = ASTParser.newParser(ASTParser.K_COMPILATION_UNIT);
    $.setKind(kind);
    $.setResolveBindings(false);
    final Map<String, String> options = JavaCore.getOptions();
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8); // or newer
    // version
    $.setCompilerOptions(options);
    return $;
  }

  /** Make a duplicate, suitable for tree rewrite, of the parameter
   * @param ¢ JD
   * @param ¢ JD
   * @return a duplicate of the parameter, downcasted to the returned type.
   * @see ASTNode#copySubtree
   * @see ASTRewrite */
  @SuppressWarnings("unchecked") static <N extends ASTNode> N rebase(final N n, final AST t) {
    return (N) copySubtree(t, n);
  }

  /** As {@link step#elze(ConditionalExpression)} but returns the last else
   * statement in "if - else if - ... - else" statement
   * @param ¢ JD
   * @return last nested else statement */
  static Statement recursiveElze(final IfStatement ¢) {
    Statement $ = ¢.getElseStatement();
    while ($ instanceof IfStatement)
      $ = ((IfStatement) $).getElseStatement();
    return $;
  }

  /** Remove all occurrences of a boolean literal from a list of
   * {@link Expression}¢
   * @param ¢ JD
   * @param xs JD */
  static void removeAll(final boolean b, final List<Expression> xs) {
    for (;;) {
      final Expression ¢ = find(b, xs);
      if (¢ == null)
        return;
      xs.remove(¢);
    }
  }

  /** Determine whether two nodes are the same, in the sense that their textual
   * representations is identical.
   * <p>
   * Each of the parameters may be <code><b>null</b></code>; a
   * <code><b>null</b></code> is only equal to< code><b>null</b></code>
   * @param n1 JD
   * @param n2 JD
   * @return <code><b>true</b></code> if the parameters are the same. */
  static boolean same(final ASTNode n1, final ASTNode n2) {
    return n1 == n2 || n1 != null && n2 != null && n1.getNodeType() == n2.getNodeType() && body(n1).equals(body(n2));
  }

  /** String wise comparison of all the given SimpleNames
   * @param ¢ string to compare all names to
   * @param es SimplesNames to compare by their string value to cmpTo
   * @return true if all names are the same (string wise) or false otherwise */
  static boolean same(final Expression x, final Expression... es) {
    for (final Expression ¢ : es)
      if (!same(¢, x))
        return false;
    return true;
  }

  /** Determine whether two lists of nodes are the same, in the sense that their
   * textual representations is identical.
   * @param ns1 first list to compare
   * @param ns2 second list to compare
   * @return are the lists equal string-wise */
  static <¢ extends ASTNode> boolean same(final List<¢> ns1, final List<¢> ns2) {
    if (ns1 == ns2)
      return true;
    if (ns1.size() != ns2.size())
      return false;
    for (int ¢ = 0; ¢ < ns1.size(); ++¢)
      if (!same(ns1.get(¢), ns2.get(¢)))
        return false;
    return true;
  }
}
