package carrosgrpc.br.com

import carrosgrpc.br.com.customanotation.Placa
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Carro(
    @field:NotBlank
    @Column(nullable = false)
    val modelo: String,
    @field:NotBlank
//    @field:Placa
    @Column(nullable = false)
    val placa: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
