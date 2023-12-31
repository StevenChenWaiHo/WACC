package wacc

import wacc.AbstractSyntaxTree.BaseT._
import wacc.AbstractSyntaxTree._

object ParseExamples {

  // Generating these could be automated from the BNF,
  // but implementing that would be almost as much work as the parser itself.
  // It would be quite interesting to build a reverse parsley for test generation, though
  var rValExamples = Set(
    "3 + exp" -> BinaryOp(BinaryOpType.Add, IntLiteral(3), IdentLiteral("exp")),
    "[3 + exp, 12]" -> ArrayLiteral(List(BinaryOp(BinaryOpType.Add, IntLiteral(3), IdentLiteral("exp")), IntLiteral(12))),
    "newpair (3 + exp, 12)" -> PairValue(BinaryOp(BinaryOpType.Add, IntLiteral(3), IdentLiteral("exp")), IntLiteral(12)),
    "fst fst_var" -> PairElement(PairElemT.Fst, IdentLiteral("fst_var")),
    "call call_ident()" -> Call(IdentLiteral("call_ident"), List())
  )

  var lValExamples = Set(
    "fst_var" -> IdentLiteral("fst_var"),
    "fst_var[12][13]" -> new ArrayElem("fst_var", List(IntLiteral(12), IntLiteral(13))),
    "fst fst_var[12][13]" -> PairElement(PairElemT.Fst, new ArrayElem("fst_var", List(IntLiteral(12), IntLiteral(13))))
  )

  var exprExamples = Set(
    "3 * 3 " -> BinaryOp(BinaryOpType.Mul, IntLiteral(3), IntLiteral(3))
  )

  var statExamples = Set(
    "println \"b\";\nreturn 3" -> StatList(List(Command(CmdT.PrintLn, StringLiteral("b")),Command(CmdT.Ret, IntLiteral(3)))),
    "return 12;\nreturn 13" -> StatList(List(Command(CmdT.Ret, IntLiteral(12)), Command(CmdT.Ret, IntLiteral(13))))
  )

  var pairExamples = Set(
    "pair(int, int) p = 2" -> Declaration(PairType(BaseType(Int_T), BaseType(Int_T)), IdentLiteral("p"), IntLiteral(2)), //wrong type
    "pair(int, int) p = newpair(10, 3)" -> Declaration(PairType(BaseType(Int_T), BaseType(Int_T)), IdentLiteral("p"), PairValue(IntLiteral(10), IntLiteral(3))),
    "pair(int, char) p = newpair(10, 'a')" -> Declaration(PairType(BaseType(Int_T), BaseType(Char_T)), IdentLiteral("p"), PairValue(IntLiteral(10), CharLiteral('a'))),
    "pair(bool, string) p = newpair(true, \"hi\")" -> Declaration(PairType(BaseType(Bool_T), BaseType(String_T)), IdentLiteral("p"), PairValue(BoolLiteral(true), StringLiteral("hi")))
  )

  var nestedPairExamples = Set(
    "pair(pair, int) q = 2" -> Declaration(PairType(NestedPair(), BaseType(Int_T)), IdentLiteral("q"), IntLiteral(2)), //wrong type
    "pair(pair, int) q = newpair(p, 3)" -> Declaration(PairType(NestedPair(), BaseType(Int_T)), IdentLiteral("q"), PairValue(IdentLiteral("p"), IntLiteral(3))),
    "pair(char, pair) q = newpair('a', p)" -> Declaration(PairType(BaseType(Char_T), NestedPair()), IdentLiteral("q"), PairValue(CharLiteral('a'), IdentLiteral("p"))),
    "pair(pair, pair) q = newpair(p, p)" -> Declaration(PairType(NestedPair(), NestedPair()), IdentLiteral("q"), PairValue(IdentLiteral("p"), IdentLiteral("p")))
  )

  private var declarationTypeExamples = Set(
    "int x " -> (BaseType(Int_T), IdentLiteral("x")),
    "char begin_char " -> (BaseType(Char_T), IdentLiteral("begin_char"))
  )

  var funcExamples: Set[(String, Func)] = {
    var set = Set[(String, Func)]()
    for (decT1 <- declarationTypeExamples;
         decT2 <- declarationTypeExamples;
         decT3 <- declarationTypeExamples;
         stat <- statExamples) {
      set = set.union(
        Set(
          """%s(%s, %s) is
            | %s
            |end
            |""".stripMargin.format(decT1._1, decT2._1, decT3._1, stat._1)
            -> Func(decT1._2._1, decT1._2._2, List(decT2._2, decT3._2), stat._2)
        )
      )
    }
    set
  }

  // Functions without arguments:
  var procExamples: Set[(String, Func)] = {
    var set = Set[(String, Func)]()
    for (decT1 <- declarationTypeExamples; stat <- statExamples) {
      set = set.union(
        Set(
          """%s() is
            | %s
            |end
            |""".stripMargin.format(decT1._1, stat._1)
            -> Func(decT1._2._1, decT1._2._2, List(), stat._2)
        )
      )
    }
    set
  }

}
