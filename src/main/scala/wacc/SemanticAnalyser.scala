package wacc

import wacc.AbstractSyntaxTree._
import wacc.AbstractSyntaxTree.BaseT._
import wacc.TypeValidator.returnType
import wacc.TypeValidator.declarationTypeToEither
import wacc.TypeProcessor.fromFunction

object SemanticAnalyser {

  def verifyProgram(program: Program): Either[List[String], ScopeContext] = {
    var topLevelContext = new ScopeContext()
    for (func <- program.funcs) {
      /* Ensure that funcs are added to top levek symbol table */
      topLevelContext = 
        topLevelContext.addFunc(func.ident.name, fromFunction(func)) match {
        case Left(err) => return Left(err)
        case Right(newContext) => newContext
      }
      verifyFunc(topLevelContext, func) match {
        case Left(err) => return Left(err)
        case Right(value) => 
      }
    }
    verifyStat(topLevelContext, program.stats)
  }

  private def verifyFunc(context: ScopeContext, func: Func): Either[List[String], ScopeContext] = {
    var funcContext = context
    /* Add all arguments to symbol table for func to use */
    func.types.foreach(elem => {
      funcContext = funcContext.addVar(elem._2.name, elem._1) match {
        case Left(err) => return Left(err)
        case Right(newContext) => newContext
      }
    })
    verifyStat(funcContext, func.code)
  }

  private def verifyStat(context: ScopeContext, stat: Stat): Either[List[String], ScopeContext] = {
    //println(stat)
    stat match {
      case SkipStat() => Right(context)
      case Declaration(dataType, ident, rvalue) => {
        if (context.findVar(ident.name).nonEmpty) {
          Left(List("Variable " + ident.name + " exists in this scope already"))
        }
        // TODO: add variable to context if no errors
        //context.addVar(ident.name, BaseType(ident)) ??
        dataType match {
          case NestedPair() => Left(List("Not Yet Implemented"))
           /*
            baseType.equals(/*evaluated expectation of rval*/)
            */
          case BaseType(baseType) => {
            // int i = 0
            rvalue match {
              case IntLiteral(x) => {
                dataType match {
                  case BaseType(Int_T) => context.addVar(ident.name, BaseType(Int_T))
                  case _ => Left(List("Incorrect types during assignment {%s, %s}".format(dataType, BaseType(Int_T))))
                }
              }
              // int i = i + 1
              case binOp@BinaryOp(op, expr1, expr2) => {
                returnType(binOp)(context) match {
                  case Left(err) => Left(err)
                  case Right(opType) => {
                    if (!dataType.equals(opType)) {
                      return Left(List("Incorrect type assignment"))
                    }
                    context.addVar(ident.name, opType)
                  }
                }
              }
              // int i = ord 'a'
              case unOp@UnaryOp(op, expr) => {
                return context.addVar(ident.name, BaseType(Char_T))
                returnType(unOp)(context) match {
                  case Left(err) => Left(err)
                  case Right(opType) => {
                    if (dataType.equals(opType)) {
                      return Left(List("Incorrect type assignment")) 
                    }
                    context.addVar(ident.name, opType)
                  }
                }
              }
              // int i = f()
              case call@Call(funcIdent, args) => {
                context.findFunc(funcIdent.name) match {
                  case None => Left(List("Function %s not in scope".format(funcIdent.name)))
                  case Some(exp) => {
                    exp matchedWith List(declarationTypeToEither(dataType)) match {
                      case Left(err) => Left(err)
                      case Right(opType) => {
                        context.addVar(ident.name, opType)
                      }
                    }
                  }
                }
              }
              case any => Left(List("rvalue %s not implemented".format(any)))
            }
          }
          case PairType(fstType, sndType) => {
            rvalue match {
              case PairValue(exp1, exp2) => {
                if (exp1 != fstType || exp2 != sndType) {
                  Left(List("Pair values do not match"))
                } else {
                  Left(List("Good Pair Value Not Yet Implemented"))
                }
              }
              case _ => {
                Left(List("Rhs not a pair"))
              }
            }
          }
          case ArrayType(dataType) => {
            rvalue match {
              case ArrayLiteral(elements) => {
                for (element <- elements) {
                  if (element != dataType) {
                    return Left(List("Invalid array typing"))
                  }
                }
                return Left(List("ArryType Not Yet Implemented"))
              }
            }
          }
        }
        /*and make sure dataType and rvalue have same type*/
      }
      case Assignment(lvalue, rvalue) => {
        /* Check if LHS is in scope */
        val name = lvalue match {
          case IdentLiteral(name) => name
          case ArrayElem(name, indicies) => name
        }
        val lTypeMaybe = context.findVar(name)
        if (lTypeMaybe.isEmpty) {
          return Left(List("Identifier %s not in scope".format(name)))
        }
        val lType = lTypeMaybe.get
        /* Check LHS and RHS are same type */
        val rType = rvalue match {
          case exp:Expr => returnType(exp)(context)
          case ArrayLiteral(elements) => Left(List("Not implemented"))
          case call@Call(funcIdent, args) => {
            context.findFunc(funcIdent.name) match {
              case None => Left(List("Function %s not in scope".format(funcIdent.name)))
              case Some(exp) => {
                exp matchedWith List(declarationTypeToEither(lType)) match {
                  case Left(err) => Left(err)
                  case Right(opType) => Right(lType)
                }
              }
            }
          }
          case PairElement(elem, lvalue) => Left(List("Not implemented"))
          case PairValue(exp1, exp2) => Left(List("Not implemented"))
        }
        rType match {
          case Left(err) => Left(err)
          case Right(t) => {
            if (!lType.equals(t)) {
              Left(List("Assignment types are not the same {%s, %s}".format(lType, rType)))
            }
            Right(context)
          }
        }
      }
      case Read(lvalue) => {
        /*Not sure what this is*/
        // TODO: ensure lvalue is int or char
        Right(context)
      }
      case Command(command, input) => {
        /*Not sure what this is*/
        Right(context)
      }
      case IfStat(cond, stat1, stat2) => {
        /*Make sure cond is boolean, verify stat1 and stat2 and make sure there is fi*/
        returnType(cond)(context) match {
          case Left(err) => Left(err)
          case Right(sType) => {
            sType match {
              case BaseType(Bool_T) => {
                verifyStat(context, stat1) match {
                  case Left(err) => return Left(err)
                  case Right(_) => return verifyStat(context, stat2)
                }
              }
              case _ => Left(List("Semantic Error: if condition is not of type Bool"))
            }
          }
        }
        // TODO: verify there is a fi
      }
      case WhileLoop(cond, stat) => {
        /*Make sure cond is boolean, verify stat*/
        returnType(cond)(context) match {
          case Left(err) => Left(err)
          case Right(sType) => {
            sType match {
              case BaseType(Bool_T) => verifyStat(context, stat)
              case _ => Left(List("Semantic Error: while condition is not of type Bool"))
            }
          }
        }
      }
      case BeginEndStat(stat) => {
        /*verify stat (What is this?)*/
        verifyStat(context, stat)
      }
      case StatList(statList) => {
        /*verify stat in list*/
        var newContext = context
        for (stat <- statList) {
          verifyStat(newContext, stat) match {
            case Left(err) => return Left(err)
            case Right(c) => newContext = c
          }
        }
        Right(newContext)
      }
    }
  }
}

/*
  sealed trait Errors
  case class DeclarationError(errorMessage: String) extends Errors
  case class AssignmentError(errorMessage: String) extends Errors
  case class ReadError(errorMessage: String) extends Errors
  case class CommandError(errorMessage: String) extends Errors
  case class ConditionError(errorMessage: String) extends Errors
  case class ArrayError(errorMessage: String) extends Errors
*/
