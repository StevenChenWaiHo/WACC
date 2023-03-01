package wacc

import wacc.AbstractSyntaxTree._
import wacc.TAC._

import scala.collection.mutable.ListBuffer

object Assembler {
  val stack = Array[Register]()
  val memory = Array[Int]()

  sealed trait Register {
    def toEither(): ImmediateValueOrRegister = new ImmediateValueOrRegister(Left(this))
  }

  object r0 extends Register {
    override def toString(): String = "r0"
  }

  object r1 extends Register {
    override def toString(): String = "r1"
  }

  object r2 extends Register {
    override def toString(): String = "r2"
  }

  object r3 extends Register {
    override def toString(): String = "r3"
  }

  object r4 extends Register {
    override def toString(): String = "r4"
  }

  object r5 extends Register {
    override def toString(): String = "r5"
  }

  object r6 extends Register {
    override def toString(): String = "r6"
  }

  object r7 extends Register {
    override def toString(): String = "r7"
  }

  object r8 extends Register {
    override def toString(): String = "r8"
  }

  object r9 extends Register {
    override def toString(): String = "r9"
  }

  object r10 extends Register {
    override def toString(): String = "r10"
  }

  object r11 extends Register {
    override def toString(): String = "r11"
  }

  object r12 extends Register {
    override def toString(): String = "r12"
  }

  object r13 extends Register {
    override def toString(): String = "r13"
  }

  object r14 extends Register {
    override def toString(): String = "r14"
  }

  object fp extends Register {
    override def toString(): String = "fp"
  }

  object lr extends Register {
    override def toString(): String = "lr"
  }

  object pc extends Register {
    override def toString(): String = "pc"
  }

  object sp extends Register {
    override def toString(): String = "sp"
  }

  val listOfRegisters = Map[Register, Int](r0 -> 0, r1 -> 0, r2 -> 0, r3 -> 0, r4 -> 0, r5 -> 0, r6 -> 0,
    r7 -> 0, r8 -> 0, r9 -> 0, r10 -> 0, r11 -> 0, r12 -> 0, r13 -> 0, r14 -> 0)

  def value(): Unit = {
    //TODO: implement register value func
  }

  def push(register: Register): String = {
    "push {" + register.toString() + "}"
  }

  def pop(register: Register): String = {
    "pop {" + register.toString() + "}"
  }

  def mov(registerDest: Register, registerSrc: Register): String = {
    "mov " + registerDest.toString() + ", " + registerSrc.toString()
  }

  def movImm(registerDest: Register, operand: Int): Unit = {
    listOfRegisters.updated(registerDest, operand)
  }

  def store(registerDest: Register, registerSrc: Register, operand: Int = 0): Unit = {
    val memoryLocation: Int = listOfRegisters(registerSrc) + operand
    listOfRegisters.updated(registerDest, memory(listOfRegisters(registerSrc) + operand))
  }

  def compare(registerDest: Register, registerSrc: Register): Boolean = {
    listOfRegisters(registerDest) == listOfRegisters(registerSrc)
  }

  def translateBeginEnd(stat: Stat, context: ScopeContext): List[String] = {
    //Seems like it takes as many variables as it can find in every scope and pushes the corresponding
    //number of registers, instead of just this scope.
    var str: List[String] = List("")
    var defaultRegistersList: List[Register] = List()
    if (context.scopeLevel() == 0) {
      defaultRegistersList = List(r8, r10, r12)
    } else {
      defaultRegistersList = List(r0)
    }
    val registersList: List[Register] = List(r6, r4, r7, r5, r1, r2)
    if (context.scopeVarSize() >= 4) {
      defaultRegistersList = defaultRegistersList ++ registersList
    } else {
      defaultRegistersList = defaultRegistersList ++ registersList.slice(0, context.scopeVarSize())
    }
    /*
    str = str ++ translatePush("", List(fp, lr)) //Maybe not meant to be in BeginEnd
    str = str ++ translatePush("", defaultRegistersList) //dependent on context
    str = str ++ delegateASTNode(stat, context)
    str = str ++ translatePop("", defaultRegistersList) // dependent on context
    str = str ++ translatePop("", List(fp, pc)) //Maybe meant to be in prog
    */
    return str
  }

  def translateSkip(): List[String] = {
    var str = List("")
    return str
  }

  def translateCommand(cmd: AbstractSyntaxTree.CmdT.Cmd, expr: AbstractSyntaxTree.Expr): List[String] = {
    List("")
  }

  def translateFunction(returnType: AbstractSyntaxTree.DeclarationType,
                        ident: AbstractSyntaxTree.IdentLiteral,
                        types: List[(AbstractSyntaxTree.DeclarationType,
                          AbstractSyntaxTree.IdentLiteral)],
                        code: Stat): List[String] = {
    List("")
  }

  def translateARM(command: String, operand: String, operand2: String = ""): String = {
    //Maybe add check to make sure command is valid
    if (operand2 == "") {
      command + " " + operand
    }
    command + " " + operand + ", " + operand2
  }

  sealed trait Operand2

  case class ImmediateValueOrRegister(operand: Either[Register, Int]) extends Operand2 {
    @Override
    override def toString: String = {
      operand match {
        case Left(value) => {
          value.toString
        }
        case Right(value) => {
          "#" + value
        }
      }
    }
  }

  case class LogicalShiftLeft(sourceRegister: Register, operand: Either[Register, Int]) extends Operand2 {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "LSL " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "LSL " + "#" + value
        }
      }
    }
  }

  case class LogicalShiftRight(sourceRegister: Register, operand: Either[Register, Int]) extends Operand2 {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "LSR " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "LSR " + " " + "#" + value
        }
      }
    }
  }

  case class ArithmeticShiftRight(sourceRegister: Register, operand: Either[Register, Int]) extends Operand2 {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "ASR " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "ASR " + "#" + value
        }
      }
    }
  }

  case class RotateRight(sourceRegister: Register, operand: Either[Register, Int]) extends Operand2 {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "LSL " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "LSL " + "#" + value
        }
      }
    }
  }

  case class RotateRightExtended(sourceRegister: Register) extends Operand2 {
    override def toString: String = {
      sourceRegister + ", " + "RRX"
    }
  }

  sealed trait Suffi

  case class Control() extends Suffi {
    override def toString: String = {
      "c"
    }
  }

  case class Extension() extends Suffi {
    override def toString: String = {
      "x"
    }
  }

  case class Status() extends Suffi {
    override def toString: String = {
      "s"
    }
  }

  case class Flags() extends Suffi {
    override def toString: String = {
      "f"
    }
  }

  case class None() extends Suffi {
    override def toString: String = {
      ""
    }
  }

  def pushPopAssist(condition: String, registers: List[Register]): String = {
    var str = condition + " {"
    for (register <- registers) {
      if (register != registers.last) {
        str = str + register.toString + ", "
      } else {
        str = str + register.toString
      }
    }
    str = str + "}"
    return str
  }

  def translatePush(condition: String, registers: List[Register]): String = {
    return "push" + pushPopAssist(condition, registers)
  }

  def translatePop(condition: String, registers: List[Register]): String = {
    return "pop" + pushPopAssist(condition, registers)
  }

  def ldrStrAssist(condition: String, destinationRegister: Register, sourceRegister: Register, operand: Either[Operand2, String]): String = {
    var str = condition + " " + destinationRegister.toString + ", "
    operand match {
      case Left(x) => {
        str = str + "[" + sourceRegister.toString + ", " + x + "]"
      }
      case Right(x) => {
        str = str + "=" + x
      }
    }
    return str
  }

  def translateLdr(condition: String, destinationRegister: Register, sourceRegister: Register, operand: Either[Operand2, String]): String = {
    //Incomplete
    return "ldr" + ldrStrAssist(condition, destinationRegister, sourceRegister, operand)
  }

  def translateStr(condition: String, destinationRegister: Register, sourceRegister: Register, operand: Either[Operand2, String]): String = {
    //Incomplete
    return "str" + ldrStrAssist(condition, destinationRegister, sourceRegister, operand)
  }

  def addSubMulAssist(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand: Operand2): String = {
    return condition + setflag + " " + destinationRegister + ", " + sourceRegister + ", " + operand
  }

  //Incomplete, no condition
  def translateAdd(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand: Operand2): String = {
    return "add" + addSubMulAssist(condition, setflag, destinationRegister, sourceRegister, operand)
  }

  def translateSub(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand: Operand2): String = {
    return "sub" + addSubMulAssist(condition, setflag, destinationRegister, sourceRegister, operand)
  }

  def translateRsb(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand: Operand2): String = {
    return "rsb" + addSubMulAssist(condition, setflag, destinationRegister, sourceRegister, operand)
  }

  def translateMul(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, sourceRegisterTwo: Register): String = {
    return "mul" + addSubMulAssist(condition, setflag, destinationRegister, sourceRegister, ImmediateValueOrRegister(Left(sourceRegisterTwo)))
  }

  def fourMulAssist(condition: String, setflag: Suffi, destinationLow: Register, destinationHigh: Register,
                    sourceRegister: Register, operand: Register): String = {
    var str = condition + setflag + " " + destinationLow + "," + " " + destinationHigh + "," + " " + sourceRegister +
      "," + " " + operand
    return str
  }

  def translateMla(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand1: Register, operand2: Register): String = {
    return "mla" + fourMulAssist(condition, setflag, destinationRegister, sourceRegister, operand1, operand2)
  }

  def translateUmull(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand1: Register, operand2: Register): String = {
    return "umull" + fourMulAssist(condition, setflag, destinationRegister, sourceRegister, operand1, operand2)
  }

  def translateUmlal(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand1: Register, operand2: Register): String = {
    return "umlal" + fourMulAssist(condition, setflag, destinationRegister, sourceRegister, operand1, operand2)
  }

  def translateSmull(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand1: Register, operand2: Register): String = {
    return "smull" + fourMulAssist(condition, setflag, destinationRegister, sourceRegister, operand1, operand2)
  }

  def translateSmlal(condition: String, setflag: Suffi, destinationRegister: Register, sourceRegister: Register, operand1: Register, operand2: Register): String = {
    return "smlal" + fourMulAssist(condition, setflag, destinationRegister, sourceRegister, operand1, operand2)
  }

  def CompareAssist(condition: String, register1: Register, operand: Operand2): String = {
    return condition + " " + register1.toString + ", " + operand.toString
  }

  def translateCompare(condition: String, register1: Register, operand: Operand2): String = {
    return "cmp" + CompareAssist(condition, register1, operand)
  }

  def translateCompareNeg(condition: String, register1: Register, operand: Operand2): String = {
    return "cmn" + CompareAssist(condition, register1, operand)
  }

  def translateMove(condition: String, dst: Register, operand: Operand2): String = {
    "mov " + dst.toString + ", " + operand.toString()
  }

  def translateBranch(condition: String, operand: String): String = {
    return "b" + condition + " " + operand
  }

  def translateBranchLink(condition: String, operand: String): String = {
    return "bl" + condition + " " + operand
  }

  /*
  //TODO: implement other commands
  val OperandToLiteral: Map[TAC.Operand, Either[String, Either[Register, Int]]]
  def translateOperand(operand: TAC.Operand): Either[String, Either[Register, Int]] = {
    if (!(!OperandToLiteral.contains(operand))) {
      return OperandToLiteral(operand)
    } else {
      operand match {
        case TRegister(num) => {
          OperandToLiteral.updated(operand, Right(Left(r0)))
          return Right(Left(r0))
        }
        case LiteralTAC() => {
          case CharLiteralTAC(c) => {
            OperandToLiteral.updated(operand, Left(c.toString))
            return Left(c.toString)
          }
          case StringLiteralTAC(s) => {
            OperandToLiteral.updated(operand, Left(s))
            return Left(s)
          }
          case IntLiteralTAC(int) => {
            OperandToLiteral.updated(operand,Right(Right(Int)))
          }
          case IdentLiteralTAC(ident) => {
            OperandToLiteral.updated(operand, Left(ident))
            return Left(ident)
          }
          case BoolLiteralTAC(bool) => {
            OperandToLiteral.updated(operand, Left("Not complete"))
            return Left("Not complete")
          }
        }
        case ArrayOp(elems) => {
          OperandToLiteral.updated(operand, Left("Not complete"))
          Left("Not complete")
        }
        case ArrayElemTAC(ar, in) => {
          OperandToLiteral.updated(operand, Left("Not complete"))
          Left("Not complete")
        }
      }
    }
  }
  */
  def translate_prints(): List[String] = {
    var str = List("")
    str = str ++ List(translatePush("", List(lr)))
    str = str ++ List(translateMove("", r2, ImmediateValueOrRegister(Left(r0))))
    str = str ++ List(translateLdr("", r1, r0, Left(ImmediateValueOrRegister(Right(-4)))))
    str = str ++ List(translateLdr("", r0, r0, Right("=.L.prints_str_0")))
    str = str ++ List(translateBranchLink("", "printf"))
    str = str ++ List(translateMove("", r0, ImmediateValueOrRegister(Right(0))))
    str = str ++ List(translateBranchLink("", "fflush"))
    str = str ++ List(translatePop("", List(pc)))
    str
  }

  def determineLdr(x: Int): Boolean = {
    if (x <= 255) {
      return false
    } else {
      for (i <- 1 to 16) {
        if ((x >> i) << i == x) {
          return false //false
        }
      }
      return true //true
    }
  }

  // TODO: move this in to register alloc
  def translateRegister(treg: TRegister): Register = {
    r0
  }

  def translateOperand(op: Operand): ImmediateValueOrRegister = {
    val regOrIm = op match {
      case reg: TRegister => Left(translateRegister(reg))
      case IntLiteralTAC(value) => Right(value)
      case _ => null // TODO: this should not match
    }
    new ImmediateValueOrRegister(regOrIm)
  }

  def translateTAC(tripleAddressCode: TAC): List[String] = {
    //Need to figure out how registers work
    //Push and pop might not be in right place
    //Algorithm for determining if ldr is needed
    var strList = List("")
    tripleAddressCode match {
      // case BinaryOpTAC(op, t1, t2, res) => {
      //   op match {
      // case BinaryOpType.Add => {
      //   val destinationRegister: Register= translateOperand(res).left.getOrElse(r0)
      //   var t1t: Either[Register, Int] = translateOperand(t1)
      //   var t2t: Either[Register, Int] = translateOperand(t2)
      //   strList = strList ++ List(translatePush("", List(r8)))
      //   strList = strList ++ List(translatePop("", List(r8)))
      //   strList = strList ++ List(translateMove("", r8, ImmediateValueOrRegister(Left(r8))))
      //   strList = strList ++ List(translateMove("", r8, ImmediateValueOrRegister(Left(r8))))
      //   t1t match {
      //     case Left(x) => {
      //       t2t match {
      //         case Right(x) => {
      //           if (determineLdr(x)) {
      //             strList = strList ++ List(translateLdr("", r9, r0, Right("=" + x)))
      //             t2t = Left(r9)
      //           }
      //         }
      //       }
      //       strList = strList ++ List(translateAdd("", Status(), destinationRegister, x, ImmediateValueOrRegister(t2t)))
      //     }
      //     case Right(x) => {
      //       if (determineLdr(x)) {
      //         strList = strList ++ List(translateLdr("", r8, r0, Right("=" + x)))
      //       } else {
      //         strList = strList ++ List(translateMove("", r8, ImmediateValueOrRegister(Right(x))))
      //       }
      //       strList = strList ++ List(translateAdd("", Status(), destinationRegister, r8, ImmediateValueOrRegister(t2t)))
      //     }
      //   }
      //   strList = strList ++ List(translateBranchLink("vs", "_errOverflow"))
      //   return strList
      // }
      /*
      case BinaryOpType.Sub => {
        strList = strList ++ List(translateSub(translateOperand(res), translateOperand(t1), translateOperand(t2)))
      }
      case BinaryOpType.Mul => {
        strList ++ List(translateSmull(r8, translateOperand(res), translateOperand(t1), translateOperand(t2)))
        strList ++ List(translateCompare("", r9, ArithmeticShiftRight(r8, Right(31))))
        strList ++ List(translateBranchLink("ne", "_errOverflow"))
      }
      case BinaryOpType.Div => {
        strList ++ List()
      }
      */
      //   }
      // }
      // case AssignmentTAC(t1, res) => {
      //   t1 match {
      //     case TRegister(num) => {
      //       strList ++ List(translateMove("", translateOperand(res), ImmediateValueOrRegister(Right(num))))
      //     }
      //     case IdentLiteralTAC(name) => {
      //       strList ++ List(translateMove("", r8, nameToAddress(name)))
      //       strList ++ List(translateMove("", translateOperand(res), ImmediateValueOrRegister(Left(r8))))
      //     }
      //     case IntLiteralTAC(value) => {
      //       strList ++ List(translateMove("", translateOperand(res), ImmediateValueOrRegister(value)))
      //     }
      //     case StringLiteralTAC(str) => {
      //       strList ++ List(translateLdr("", translateOperand(res), nameToLabel(str)))
      //     }

      //   }
      // }
      case Label(name) => {
        List(name + ":")
      }
      case BeginFuncTAC() => {
        translatePush("", List(fp, lr)) ::
          translatePush("", List(r8, r10, r12)) ::
          translateMove("", fp, sp.toEither()) :: List()
      }
      case EndFuncTAC() => {
        translateMove("", r0, r0.toEither()) ::
          translatePop("", List(r8, r10, r12)) ::
          translatePop("", List(fp, pc)) :: List()
      }
      case AssignmentTAC(operand, reg) => {
        translateMove("", translateRegister(reg), translateOperand(operand)) :: List()
      }
      case CommandTAC(cmd, operand) => {
        if (cmd == CmdT.Exit) {
          mov(r0, r0) :: // TODO: change from default r0
            translateBranchLink("", "exit") :: List() // TODO: should not default to t0
        } else {
          List("Command not implemented")
        }
      }
    }
  }

  def translateProgram(tacList: List[TAC]): List[String] = {
    var output = List[String]()
    // temp dummy header to start
    output = List(".data", ".text", ".global main")
    tacList.foreach(tac => {
      output = output ++ translateTAC(tac)
    })
    output
  }
}
