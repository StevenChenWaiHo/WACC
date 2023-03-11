package wacc

import wacc.AssemblerTypes._

object FinalIR {
  sealed trait FinalIR

  case class Str(condition: String, src: Register, operand: LHSop, dst: Register) extends FinalIR
  case class StrPre(condition: String, src: Register, operand: LHSop, dst: Register) extends FinalIR
  case class Ldr(condition: String, src: Register, operand: LHSop, dst: Register) extends FinalIR

  case class Push(condition: String, regs: List[Register]) extends FinalIR
  case class Pop(condition: String, regs: List[Register]) extends FinalIR
  
  case class Add(condition: String, setFlag: Suffi, op1: LHSop, op2: LHSop, dst: Register) extends FinalIR
  case class Sub(condition: String, setFlag: Suffi, op1: LHSop, op2: LHSop, dst: Register) extends FinalIR
  case class Rsb(condition: String, setFlag: Suffi, op1: LHSop, op2: LHSop, dst: Register) extends FinalIR
  case class Mul(condition: String, setFlag: Suffi, op1: LHSop, op2: LHSop, dst: Register) extends FinalIR

  case class Mov(condition: String, src: LHSop, dst: Register) extends FinalIR

  case class Branch(condition: String, name: String) extends FinalIR
  case class BranchLink(condition: String, name: LHSop) extends FinalIR

  case class Cmp(condition: String, op1: LHSop, op2: LHSop) extends FinalIR

  case class Global(name: String) extends FinalIR
  case class Lbl(name: String) extends FinalIR
  case class Comment(comment: String) extends FinalIR
  
  case class DataSeg() extends FinalIR
  case class TextSeg() extends FinalIR
  case class Word(len: Int) extends FinalIR
  case class AsciiZ(str: String) extends FinalIR

  case class Special(str: String) extends FinalIR
}