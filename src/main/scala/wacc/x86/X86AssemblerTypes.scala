package wacc

import wacc.AssemblerTypes._

object X86AssemblerTypes {
  //TODO change these to x86 format
  //TODO change all calls to LHSop instances in ALL files
  case class X86StackOffset(offset: Int) extends LHSop {
    override def toString(): String = "STACK" + offset.toString()
  }

  case class X86ImmediateInt(i: Int) extends LHSop {
    override def toString(): String = i.toString()
  }

  case class X86LabelString(name: String) extends LHSop {
    override def toString(): String = "=" + name
  }

  case class X86BranchString(name: String) extends LHSop {
    override def toString(): String = name
  }

  sealed trait X86Register extends Register {
    import scala.math.Ordered.orderingToOrdered
    def compare(that: X86Register): Int = listOfRegisters.get(this) compare listOfRegisters.get(that)
    // rbx/rsp/rbp should not be here
    val listOfRegisters = Map[Register, Int](rax -> 0, rcx -> 1, rdx -> 2, rsi -> 6, rdi -> 7,
    r8 -> 8, r9 -> 9, r10 -> 10, r11 -> 11, r12 -> 12, r13 -> 13, r14 -> 14, r15 -> 15)
  }

  object rax extends X86Register {
    override def toString(): String = "rax" // r0 equiv.
  }

  object rcx extends X86Register {
    override def toString(): String = "rcx" // 4th arg (assume r4 equiv.)
  }

  object rdx extends X86Register {
    override def toString(): String = "rdx" // r3 equiv.
  }

  object rbx extends Register {
    override def toString(): String = "rbx" // base pointer? (lr/pc) PRESERVED
  }

  object rsp extends Register {
    override def toString(): String = "rsp" // sp PRESERVED
  }

  object rbp extends Register {
    override def toString(): String = "rbp" // fp PRESERVED
  }

  object rsi extends X86Register {
    override def toString(): String = "rsi" // r2 equiv.
  }

  object rdi extends X86Register {
    override def toString(): String = "rdi" // r1 equiv.
  }

  object r8 extends X86Register {
    override def toString(): String = "r8" // 5th arg
  }

  object r9 extends X86Register {
    override def toString(): String = "r9" // 6th arg
  }

  object r10 extends X86Register {
    override def toString(): String = "r10" // temp
  }

  object r11 extends X86Register {
    override def toString(): String = "r11" // temp
  }

  object r12 extends X86Register {
    override def toString(): String = "r12" // PRESERVED
  }

  object r13 extends X86Register {
    override def toString(): String = "r13" // PRESERVED
  }

  object r14 extends X86Register {
    override def toString(): String = "r14" // PRESERVED
  }

  object r15 extends X86Register {
    override def toString(): String = "r15" // PRESERVED
  }

  // object lr extends X86Register {
  //   override def toString(): String = "lr"
  // }

  // object pc extends X86Register {
  //   override def toString(): String = "pc"
  // }

  case class ImmediateValueOrRegister(operand: Either[X86Register, Int]) extends LHSop {
    @Override
    override def toString: String = {
      operand match {
        case Left(value) => {
          value.toString
        }
        case Right(value) => {
          value.toString
        }
      }
    }
  }

  case class LogicalShiftLeft(sourceRegister: X86Register, operand: Either[X86Register, Int]) extends LHSop {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "shl " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "shl " + value
        }
      }
    }
  }

  case class LogicalShiftRight(sourceRegister: X86Register, operand: Either[X86Register, Int]) extends LHSop {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "shr " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "shr " + " " + value
        }
      }
    }
  }

  case class ArithmeticShiftRight(sourceRegister: X86Register, operand: Either[X86Register, Int]) extends LHSop {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "sar " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "sar " + value
        }
      }
    }
  }

  case class RotateRight(sourceRegister: X86Register, operand: Either[X86Register, Int]) extends LHSop {
    override def toString: String = {
      operand match {
        case Left(x) => {
          sourceRegister + ", " + "ror " + x
        }
        case Right(value) => {
          sourceRegister + ", " + "ror " + value
        }
      }
    }
  }

  // dont think its used
  case class X86Control() extends Suffi {
    override def toString: String = {
      ""
    }
  }

  case class X86Extension() extends Suffi {
    override def toString: String = {
      ""
    }
  }

  case class X86Status() extends Suffi {
    override def toString: String = {
      ""
    }
  }

  case class X86Flags() extends Suffi {
    override def toString: String = {
      ""
    }
  }

  case class X86None() extends Suffi {
    override def toString: String = {
      ""
    }
  }
}
