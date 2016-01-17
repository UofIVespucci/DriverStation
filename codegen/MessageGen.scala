import scala.xml._
import java.io._

object App {
    sealed abstract class DType { val name: String; val bytes: Int }
    case class Flt32(val name: String) extends DType { val bytes = 4 }
    case class Int32(val name: String) extends DType { val bytes = 4 }
    case class Int16(val name: String) extends DType { val bytes = 2 }
    case class UInt8(val name: String) extends DType { val bytes = 1 }
    case class Msg(val sig: Int, val name: String, val data: Seq[DType])

    var sigNum = -1
    def genUniqueSig(): Int = {
        sigNum += 1
        sigNum
    }

    def JavaFormat(msg: Msg): String = {
        val typemap: Map[Class[_<:DType],String] =
            Map( (classOf[Flt32] -> "float"), (classOf[Int32] -> "int"),
                 (classOf[Int16] -> "short"), (classOf[UInt8] -> "byte") )
        def toIdent(t: DType): String = typemap(t.getClass) +" "+ t.name
        val length = msg.data.map(_.bytes).sum + 1 //for the signifier byte
        val argline = msg.data.map(toIdent).mkString(", ")
        val readbuf = msg.data.map{t =>
                toIdent(t)+" = buf.get"+typemap(t.getClass).capitalize+"();"
            }.mkString("\n        ")
        val buildbuf = msg.data.map{t =>
                "buf.put"+typemap(t.getClass).capitalize+"("+t.name+");"
            }.mkString("\n        ")
        s"""|package com.VespuChat.messages;
        |
        |import com.serial.PacketReader;
        |
        |class ${msg.name} implements PacketReader {
        |    public int claim(byte data){
        |        return (data == ${msg.sig}) ${length} : -1;
        |    }
        |    public void handle(byte[] data){
        |        ByteBuffer buf = ByteBuffer.wrap(data);
        |        byte  sig = buf.get();
        |        ${readbuf}
        |        /*handle the message*/
        |    }
        |    public static byte[] build(${argline}){
        |        ByteBuffer buf = ByteBuffer.allocate(${length});
        |        buf.put(${msg.sig});
        |        ${buildbuf}
        |        buf.array();
        |    }
        |}
        |""".stripMargin
    }

    def CppFormat(msg: Msg):String = {
        val typemap: Map[Class[_<:DType],String] =
            Map( (classOf[Flt32] -> "float"), (classOf[Int32] -> "int32_t"),
                 (classOf[Int16] -> "int16_t"), (classOf[UInt8] -> "uint8_t") )
        def toIdent(t: DType): String = typemap(t.getClass) +" "+ t.name
        val length = msg.data.map(_.bytes).sum + 1 //for the signifier byte
        val idents = msg.data.map(toIdent)
        val assign = msg.data.map(t => s"data->${t.name} = ${t.name};")
        s"""|#ifndef ${msg.name.toUpperCase}_H
        |#define ${msg.name.toUpperCase}_H
        |
        |#include "serial/Receiver.h"
        |
        |typedef struct ${msg.name}conv {
        |    uint8_t sig;
        |    ${idents.mkString(";\n    ")};
        |} ${msg.name}conv;
        |class ${msg.name} : public Receiver {
        |public:
        |    int claim(char data) {
        |        return (data == ${msg.sig}) ${length} : -1;
        |    }
        |    void handle(const char* buf, int len){
        |        ${msg.name}conv *data = (*${msg.name}conv) buf;
        |        //handle data
        |    }
        |    static byte* build(${idents.mkString(", ")}){
        |        byte *buf = malloc(${length});
        |        ${msg.name}conv *data = (*${msg.name}conv) buf;
        |        data->sig = ${msg.sig};
        |        ${assign.mkString("\n        ")}
        |        return buf;
        |    }
        |};
        |#endif
        |""".stripMargin
    }

    def main(args: Array[String]) {
        if(args.size == 0){
            println("provide XML file of message types")
            return
        }
        val xml = XML.loadFile(args(0))

        //parse the XML
        val messages = for(msg <- xml \ "message") yield {
            def toValue(vnode: scala.xml.Node): DType = {
                val name = (vnode \ "@name").toString
                (vnode \ "@type").toString.toLowerCase match {
                    case "float" => Flt32(name)
                    case "int32" => Int32(name)
                    case "int16" => Int16(name)
                    case "uint8" => UInt8(name)
                    case _ => throw new Exception("Unknown datatype")
                }
            }
            val values = (msg \ "value").map(toValue)
            val name: String = (msg \ "@name").toString
            Msg(genUniqueSig, name, values)
        }

        val languages = Seq((JavaFormat(_),".java"), (CppFormat(_),".h"))
        for((lang, filetype) <- languages; msg <- messages) {
            val code = lang(msg)
            val pw = new PrintWriter(new File("output/"+msg.name+filetype))
            pw.write(code)
            pw.close
        }
    }
}
