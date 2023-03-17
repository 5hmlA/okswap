package osp.sparkj.okswap.connect

/**
 * @author yun.
 * @date 2023/3/17
 * @des [一句话描述]
 * @since [https://github.com/mychoices]
 * <p><a href="https://github.com/mychoices">github</a>
 */

interface Nest {
    fun handle(pigeon: Pigeon)
}


interface Pigeon {
    fun deliver(byteArray: ByteArray)
}

interface Connect {
    suspend fun connect(): Pigeon
}