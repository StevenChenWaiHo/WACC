package wacc

import parsley.{Failure, Success}
import wacc.Parser.ProgramParser.program
import wacc.SemanticAnalyser.verifyProgram
import wacc.Translator.delegateASTNode
import wacc.Inlining.inline_delegateASTNode
import wacc.ARM11Assembler
import wacc.TAC._
import wacc.PeepholeOptimisation.PeepholeOptimise
import wacc.ArchitectureType.getArchitecture

import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter}
import scala.io.Source


object Main {
  val OutputAssemblyFile = true

  val SyntaxErrorCode = 100
  val SemanticErrorCode = 200
  val SuccessCode = 0

  var target = ArchitectureType.ARM11

  def main(args: Array[String]): Unit = {
    if (args.length != 2) throw new IllegalArgumentException(
      "Incorrect number of arguments provided. Received: " + args.length + ", Expected 2."
    )
    val filename = args.head
    val file = Option(Source.fromFile(filename))
      .getOrElse(throw new FileNotFoundException("File: " + filename + " does not exist."))
    val inputProgram = file.mkString
    file.close

    println(inputProgram + "\n\n")
    val optionalFlagString = args(1)
    val inlineFlag = optionalFlagString.contains("i")

    /* Compile */
    // Parse input file
    val ast = program.parse(inputProgram)
    ast match {
      case Failure(err) => {
        println("Syntax Error: %s".format(err))
        sys.exit(SyntaxErrorCode)
      }
      case Success(x) =>
    }

    // Apply semantic analysis
    val verified = verifyProgram(ast.get)
    if (verified.isLeft) {
      print("Semantic Error: ")
      verified.left.foreach(errList => {
        errList.reverse.foreach(err => {
          if (err != null && err.nonEmpty) println(err)
        })
      })
      sys.exit(SemanticErrorCode)
    }
    
    // Translate the ast to TAC
    var tac = List[TAC]()
    if (inlineFlag){
      println("--- INLINED TAC ---")
      tac = inline_delegateASTNode(ast.get)._1
    }
    else {
      println("--- TAC ---")
      tac = delegateASTNode(ast.get)._1
    }
    
  
    tac.foreach(l => println(l))

    // Convert the TAC to IR
    val assembler = new Assembler()

    val (ir, funcs) = assembler.assembleProgram(tac)

    println("--- FinalIR ---")
    ir.foreach{x => println(x)}

    // Apply optimisations here
    // TODO: only optimise based on cmdline flags
    val result = PeepholeOptimise(ir)

    var asm = new String()
    target match {
      case ArchitectureType.ARM11 => {
        // Convert the IR to ARM11
        asm = ARM11Assembler.assemble(result, funcs)
        println("--- ARM ---")
      }
      case ArchitectureType.X86 => {
        // Convert the IR to X86_64
        //val x86 = X86Assembler.assemble(result, funcs)
        println("--- X86_64 ---")
      }
    }
    print(asm)

    /* Output the assembly file */
    if(OutputAssemblyFile) {
      val inputFilename = args.head.split("/").last
      val outputFilename = inputFilename.replace(".wacc", ".s")
      val outputFile = new File(outputFilename)
      val fileWriter = new BufferedWriter(new FileWriter(outputFile))
      fileWriter.write(asm + "\n")
      fileWriter.close()
    }
    println("\n\nCompilation Successful!")
    sys.exit(SuccessCode)
  }
}



