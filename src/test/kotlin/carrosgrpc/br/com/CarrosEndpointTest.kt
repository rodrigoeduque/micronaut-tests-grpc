package carrosgrpc.br.com

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(
    val repository: CarroRepository,
    val grpcClient: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    /**
     * Passo a passo para projetar teste
     * 1. Happy Path (Caminho feliz - Onde tudo tem que dar certo) -> OK
     * 2. Quando já existe carro com a placa -> OK
     * 3. Quando os dados de entrada são inválidos
     */

    @Test
    internal fun `deve adicionar um novo carro`() {

        //cenário -> tinhamos o deleteall movido para o Setup

        //ação
        val response = grpcClient.adicionar(CarroRequest.newBuilder().setModelo("Gol").setPlaca("HPX-1234").build())

        //validação
        with(response) {
            assertNotNull(id)
            assertTrue(repository.existsById(id)) //Verifico efeito colateral -> Verifica realmente se houve integração
        }

    }

    @Test
    internal fun `nao deve adicionar novo carro, caso a placa exista`() {
        //cenário
        repository.save(Carro(modelo = "Palio", placa = "AAA-1234"))

        //ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(CarroRequest.newBuilder().setModelo("Fox").setPlaca("AAA-1234").build())
        }


        //validação

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Placa já cadastrada", status.description)
        }
    }

    @Test
    internal fun `nao deve adicionar um novo carro caso os dados de entrada estejam inválidos`() {
        //cenário
        repository.deleteAll()

        //ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(CarroRequest.newBuilder().setModelo("Ferrari").setPlaca("").build())
        }

        //validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados incorretos para entrada", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {

            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}