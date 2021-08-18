package carrosgrpc.br.com

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("carrosgrpc.br.com")
		.start()
}

