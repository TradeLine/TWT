package org.tlsys.node

import org.tlsys.BaseBlock

open class Push():Node()

open class PushLdc(val value:Any):Push() {
    override fun toString(): String{
        return "PushLdc(value=$value)"
    }
}

open class PushInt(val value:Int):Push() {
    override fun toString(): String{
        return "PushInt(value=$value)"
    }
}