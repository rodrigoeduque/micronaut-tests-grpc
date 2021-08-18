package carrosgrpc.br.com

import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CarrosEndpoint(@Inject val repository: CarroRepository) : CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun adicionar(request: CarroRequest, responseObserver: StreamObserver<CarroResponse>) {

        if (repository.existsByPlaca(request.placa)) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription("Placa já cadastrada")
                    .asRuntimeException()
            )

            return
        }
        val carro = Carro(modelo = request.modelo, placa = request.placa)

        try {
            repository.save(carro)
        } catch (e: javax.validation.ConstraintViolationException) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Dados incorretos para entrada")
                    .asRuntimeException())
            return
        }

        responseObserver.onNext(CarroResponse.newBuilder().setId(carro.id!!).build())
        responseObserver.onCompleted()
    }
}